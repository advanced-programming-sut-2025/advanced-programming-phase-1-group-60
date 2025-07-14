package com.StardewValley.AssetsManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.Random;

public class MapManager {
    private static MapManager instance;
    private Random random;

    // Map textures
    private Texture grassTile;
    private Texture placeholderTile;

    // Stone textures
    private Texture[] stoneTiles;

    private MapManager() {
        random = new Random();
        loadTextures();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    private void loadTextures() {
        grassTile = new Texture(Gdx.files.internal("Map/Floor/Grass.png"));
        placeholderTile = new Texture(Gdx.files.internal("placeholder.png"));

        // Load stone textures
        stoneTiles = new Texture[8];
        for (int i = 0; i < 8; i++) {
            stoneTiles[i] = new Texture(Gdx.files.internal("Map/Stone/Stone_" + (i+1) + ".png"));
        }
    }

    public Texture getGrassTile() {
        return grassTile;
    }
    public Texture getStoneTile(int variant) {
        // Ensure variant is between 1-8
        int index = Math.min(Math.max(variant, 1), 8) - 1;
        return stoneTiles[index];
    }
    public Texture getPlaceholderTile() {
        return placeholderTile;
    }

    // Get a random stone texture
    public Texture getRandomStoneTile() {
        return stoneTiles[random.nextInt(stoneTiles.length)];
    }

    public void dispose() {
        grassTile.dispose();
        placeholderTile.dispose();

        // Dispose stone textures
        for (Texture stoneTile : stoneTiles) {
            stoneTile.dispose();
        }
    }
}
