package com.StardewValley.AssetsManager;

import com.StardewValley.models.Seeds;
import com.StardewValley.models.Tile;
import com.StardewValley.models.TimeSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Greenhouse interior manager (farming enabled).
 */
public class GreenhouseManager {

    public static final int WIDTH = 12;
    public static final int HEIGHT = 10;

    private static final String INTERIOR_IMAGE_PATH = "assets/Map/Greenhouse/greenhouse.png";

    private static GreenhouseManager instance;

    private final Tile[][] tiles;
    private Texture interiorTexture;

    private GreenhouseManager() {
        tiles = new Tile[HEIGHT][WIDTH];
        initTiles();
        loadInterior();
    }

    public static GreenhouseManager getInstance() {
        if (instance == null) {
            instance = new GreenhouseManager();
        }
        return instance;
    }

    private void initTiles() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                tiles[y][x] = new Tile(x, y);
                tiles[y][x].setType(".");
            }
        }
    }

    private void loadInterior() {
        if (Gdx.files.internal(INTERIOR_IMAGE_PATH).exists()) {
            interiorTexture = new Texture(Gdx.files.internal(INTERIOR_IMAGE_PATH));
        } else {
            System.err.println("[GreenhouseManager] Interior image missing: " + INTERIOR_IMAGE_PATH);
        }
    }

    public Texture getInteriorTexture() {
        return interiorTexture;
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return null;
        return tiles[y][x];
    }

    /**
     * Plant a seed ignoring seasonal restrictions.
     */
    public boolean plantSeed(int x, int y, Seeds seed) {
        Tile t = getTile(x, y);
        if (t == null) return false;
        if (!t.isPlowed() || t.getPlantedSeed() != null) return false;

        // Clone seed so inventory copy changes don't affect planted seed
        Seeds clone = new Seeds();
        clone.setName(seed.getName());
        clone.setGrowsInto(seed.getGrowsInto());
        clone.setSuitableSeasons(seed.getSuitableSeasons());
        clone.setTotalHarvestTime(seed.getTotalHarvestTime());
        clone.setImagePath(seed.getImagePath());
        clone.setQuantity(1);

        t.setPlantedSeed(clone);
        t.setDaysGrown(0);
        t.setWatered(true);
        t.setLastWateredDay(TimeSystem.getInstance().getCurrentDay());
        t.setType("*");
        return true;
    }

    /**
     * Advance all greenhouse crops daily if watered.
     */
    public void advanceDaily() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile t = tiles[y][x];
                if (t.getPlantedSeed() != null) {
                    if (t.isWatered()) {
                        t.setDaysGrown(t.getDaysGrown() + 1);
                    }
                    t.setWatered(false); // reset for next day
                }
            }
        }
    }

    public void dispose() {
        if (interiorTexture != null) interiorTexture.dispose();
    }
}
