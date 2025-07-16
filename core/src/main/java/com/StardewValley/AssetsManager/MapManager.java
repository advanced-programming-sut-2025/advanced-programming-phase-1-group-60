package com.StardewValley.AssetsManager;

import com.StardewValley.models.ForagingCrop;
import com.StardewValley.models.ForagingMineral;
import com.StardewValley.models.ForagingTree;
import com.StardewValley.models.Tree;
import com.StardewValley.repository.ForagingRepository;
import com.StardewValley.repository.TreeRepository;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MapManager {
    private static MapManager instance;
    private Random random;
    // Map textures
    private Texture grassTile;
    private Texture placeholderTile;
    // Stone textures
    private Texture[] stoneTiles;
    //Tree Textures
    private Map<String, Texture> treeTextures = new HashMap<>();
    //Foraging Stuff
    private Map<String, Texture> foragingMineralTextures = new HashMap<>();
    private Map<String, Texture> foragingCropTextures = new HashMap<>();
    private Map<String, Texture> foragingTreeTextures = new HashMap<>();
    // Structues
    private Texture cabinTexture;
    private Texture greenhouseTexture;
    private Texture waterTexture;
    private Texture quarryTexture;

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
        cabinTexture = new Texture(Gdx.files.internal("Map/Floor/Cabin.png"));
        greenhouseTexture = new Texture(Gdx.files.internal("Map/Floor/Greenhouse.png"));
        waterTexture = new Texture(Gdx.files.internal("Map/Floor/Water.png"));
        quarryTexture = new Texture(Gdx.files.internal("Map/Floor/Quarry.png"));
        // Load stone textures
        stoneTiles = new Texture[8];
        for (int i = 0; i < 8; i++) {
            stoneTiles[i] = new Texture(Gdx.files.internal("Map/Stone/Stone_" + (i+1) + ".png"));
        }
        // Load all Tree Textures
        loadTreeTextures();

        // Load all foraging textures
        loadForagingTextures();
    }
    public Texture getTreeTexture(String imagePath) {
        Texture texture = treeTextures.get(imagePath);
        if (texture == null) {
            System.out.println("Missing tree texture: " + imagePath);
            return placeholderTile;
        }
        return texture;
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
    public Texture getForagingMineralTexture(String imagePath) {
        return foragingMineralTextures.getOrDefault(imagePath, placeholderTile);
    }
    public Texture getForagingCropTexture(String imagePath) {
        return foragingCropTextures.getOrDefault(imagePath, placeholderTile);
    }
    public Texture getForagingTreeTexture(String imagePath) {
        return foragingTreeTextures.getOrDefault(imagePath, placeholderTile);
    }
    public Texture getCabinTexture() {
        return cabinTexture;
    }
    public Texture getGreenhouseTexture() {
        return greenhouseTexture;
    }
    public Texture getWaterTexture() {
        return waterTexture;
    }
    public Texture getQuarryTexture() {
        return quarryTexture;
    }
    public Texture getRandomStoneTile() {
        return stoneTiles[random.nextInt(stoneTiles.length)];
    }

    private void loadTreeTextures() {
        for (Tree tree : TreeRepository.trees) {
            try {
                if (tree.getImagePath() != null) {
                    String path = "Map/Tree/" + tree.getImagePath();
                    if (Gdx.files.internal(path).exists()) {
                        treeTextures.put(tree.getImagePath(), new Texture(Gdx.files.internal(path)));
                        System.out.println("Loaded tree texture: " + path);
                    } else {
                        System.out.println("Tree texture file not found: " + path);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading tree texture: " + e.getMessage());
            }
        }
    }
    private void loadForagingTextures() {
        // Load foraging minerals
        for (ForagingMineral mineral : ForagingRepository.foragingMinerals) {
            loadTexture(mineral.getImagePath(), "Map/ForagingMineral/", foragingMineralTextures, "mineral");
        }

        // Load foraging crops
        for (ForagingCrop crop : ForagingRepository.foragingCrops) {
            loadTexture(crop.getImagePath(), "Map/ForagingCrop/", foragingCropTextures, "crop");
        }

        // Load foraging trees
        for (ForagingTree tree : ForagingRepository.foragingTrees) {
            loadTexture(tree.getImagePath(), "Map/ForagingTree/", foragingTreeTextures, "tree");
        }
    }
    private void loadTexture(String imagePath, String basePath, Map<String, Texture> textureMap, String type) {
        try {
            if (imagePath != null) {
                String path = basePath + imagePath;
                if (Gdx.files.internal(path).exists()) {
                    textureMap.put(imagePath, new Texture(Gdx.files.internal(path)));
                    System.out.println("Loaded foraging " + type + " texture: " + path);
                } else {
                    System.out.println("Foraging " + type + " texture not found: " + path);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading foraging " + type + " texture: " + e.getMessage());
        }
    }

    public void dispose() {
        // Dispose basic textures
        grassTile.dispose();
        placeholderTile.dispose();

        // Dispose stone textures
        for (Texture stoneTile : stoneTiles) {
            stoneTile.dispose();
        }

        // Dispose all texture maps
        if (cabinTexture != null) cabinTexture.dispose();
        if (greenhouseTexture != null) greenhouseTexture.dispose();
        if (waterTexture != null) waterTexture.dispose();
        if (quarryTexture != null) quarryTexture.dispose();
        disposeTextureMap(treeTextures);
        disposeTextureMap(foragingMineralTextures);
        disposeTextureMap(foragingCropTextures);
        disposeTextureMap(foragingTreeTextures);
    }
    private void disposeTextureMap(Map<String, Texture> textureMap) {
        for (Texture texture : textureMap.values()) {
            texture.dispose();
        }
    }
}
