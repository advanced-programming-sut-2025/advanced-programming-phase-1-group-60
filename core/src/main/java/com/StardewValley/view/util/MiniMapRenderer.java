package com.StardewValley.view.util;

import com.StardewValley.models.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Generates a lightweight minimap texture.
 * Strategy:
 *   - Each tile => 4x4 pixels (configurable)
 *   - Encodes: water, plowed, crop, building, default ground
 *   - Marks current player's position as white.
 *   - Renders ONLY the current farm (and a border) to keep size small.
 * Extend easily for villagers, animals etc.
 */
public class MiniMapRenderer {

    private static final int TILE_PIXEL_SIZE = 4;

    // Colors (RGBA8888)
    private static final int COLOR_GROUND   = rgba(0x4C, 0xAF, 0x50); // green
    private static final int COLOR_WATER    = rgba(0x1E, 0x88, 0xE5); // blue
    private static final int COLOR_PLOWED   = rgba(0x8D, 0x6E, 0x63); // brown
    private static final int COLOR_CROP     = rgba(0xFF, 0xEB, 0x3B); // yellow
    private static final int COLOR_BUILDING = rgba(0xF4, 0x43, 0x36); // red
    private static final int COLOR_PLAYER   = rgba(0xFF, 0xFF, 0xFF); // white
    private static final int COLOR_BG       = rgba(0x12, 0x1A, 0x12); // dark background

    private static int rgba(int r, int g, int b) {
        return (r & 0xFF) << 24 |
            (g & 0xFF) << 16 |
            (b & 0xFF) << 8  |
            0xFF;
    }

    public static Texture generate(GameMap gameMap, int currentFarmIndex, boolean inVillage, Vector2 playerWorldPos) {
        if (gameMap == null) return emptyTexture();

        // We render only the current farm to keep clarity
        Farm farm = gameMap.getFarm(currentFarmIndex);
        if (farm == null) return emptyTexture();

        int widthTiles = farm.getWidth();
        int heightTiles = farm.getHeight();
        int texWidth = widthTiles * TILE_PIXEL_SIZE;
        int texHeight = heightTiles * TILE_PIXEL_SIZE;

        Pixmap pm = new Pixmap(texWidth, texHeight, Pixmap.Format.RGBA8888);

        // Fill background
        pm.setColor( (COLOR_BG >> 24 & 0xFF)/255f,
            (COLOR_BG >> 16 & 0xFF)/255f,
            (COLOR_BG >>  8 & 0xFF)/255f,
            1f);
        pm.fill();

        // Determine player's farm-relative position
        Vector2 farmTopLeftWorld = computeFarmTopLeftWorld(gameMap, currentFarmIndex);
        // Convert player world position to farm-local tile indices (approx)
        int playerTileX = (int)((playerWorldPos.x - farmTopLeftWorld.x) / 32f);
        int playerTileY = (int)((playerWorldPos.y - farmTopLeftWorld.y) / 32f);

        // Iterate farm tiles
        for (int y = 0; y < heightTiles; y++) {
            for (int x = 0; x < widthTiles; x++) {
                Tile t = farm.getTile(x, y);
                int color = COLOR_GROUND;
                if (t != null) {
                    if (t.getStaticElement().isPresent()) {
                        Object se = t.getStaticElement().get();
                        if (se instanceof Lake) color = COLOR_WATER;
                        else color = COLOR_BUILDING;
                    } else if (t.isPlowed()) {
                        if (t.getPlantedSeed() != null) color = COLOR_CROP;
                        else color = COLOR_PLOWED;
                    } else if (t.getRandomElement().isPresent()
                        && t.getRandomElement().get() instanceof com.StardewValley.models.ForagingMineral) {
                        // Optional: treat minerals as crops color
                        color = COLOR_CROP;
                    }
                }
                fillTile(pm, x, y, color);
            }
        }

        // Draw player marker if within farm bounds
        if (playerTileX >= 0 && playerTileX < widthTiles && playerTileY >= 0 && playerTileY < heightTiles) {
            fillTile(pm, playerTileX, playerTileY, COLOR_PLAYER);
        }

        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    private static void fillTile(Pixmap pm, int tileX, int tileY, int rgba) {
        int px = tileX * TILE_PIXEL_SIZE;
        int py = tileY * TILE_PIXEL_SIZE;
        int r = rgba >> 24 & 0xFF;
        int g = rgba >> 16 & 0xFF;
        int b = rgba >> 8  & 0xFF;
        int a = rgba       & 0xFF;
        pm.setColor(r/255f, g/255f, b/255f, a/255f);
        pm.fillRectangle(px, py, TILE_PIXEL_SIZE, TILE_PIXEL_SIZE);
    }

    private static Texture emptyTexture() {
        Pixmap pm = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        pm.setColor(0,0,0,1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    private static Vector2 computeFarmTopLeftWorld(GameMap gameMap, int farmIndex) {
        // Mirror logic from MapView.getFarmTopLeft (approximation)
        int farmW = FarmTemplate.WIDTH;
        int vilW = gameMap.getVilW();
        int vilH = gameMap.getVilH();
        float tileSize = 32f;
        switch (farmIndex) {
            case 0: return new Vector2(0, vilH * tileSize);
            case 1: return new Vector2((vilW + farmW) * tileSize, vilH * tileSize);
            case 2: return new Vector2(0, 0);
            case 3: return new Vector2((vilW + farmW) * tileSize, 0);
            default: return new Vector2(0,0);
        }
    }
}
