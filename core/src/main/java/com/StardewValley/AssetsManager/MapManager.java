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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MapManager {
    private static MapManager instance;
    private Random random;

    // Map textures
    private Texture grassTile;
    private Texture placeholderTile;
    private Texture chatIconTexture; // Chat icon from the new version

    // Stone textures
    private Texture[] stoneTiles;

    // Tree Textures
    private Map<String, Texture> treeTextures = new HashMap<>();

    // Foraging Stuff
    private Map<String, Texture> foragingMineralTextures = new HashMap<>();
    private Map<String, Texture> foragingCropTextures = new HashMap<>();
    private Map<String, Texture> foragingTreeTextures = new HashMap<>();

    // Structures
    private Texture cabinTexture;
    private Texture greenhouseTexture;
    private Texture waterTexture;
    private Texture quarryTexture;
    private Texture sellingBinTexture;

    // NPC and Store textures (from new version)
    private Map<String, Texture> npcTextures;
    private Texture storeTexture;

    // Character textures
    private Texture playerSpriteSheet;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> idleDownAnimation;

    private MapManager() {
        random = new Random();
        npcTextures = new HashMap<>();
        loadTextures();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }
    private void loadPlayerAnimations() {
        playerSpriteSheet = new Texture(Gdx.files.internal("Map/Character/Alex.png"));

        // Split the sprite sheet into 4x4 grid
        TextureRegion[][] frames = TextureRegion.split(playerSpriteSheet,
            playerSpriteSheet.getWidth() / 4, playerSpriteSheet.getHeight() / 4);

        // Create animations for each direction (4 frames each)
        TextureRegion[] walkDownFrames = new TextureRegion[4];
        TextureRegion[] walkRightFrames = new TextureRegion[4];
        TextureRegion[] walkUpFrames = new TextureRegion[4];
        TextureRegion[] walkLeftFrames = new TextureRegion[4];

        // Extract frames for each direction
        for (int i = 0; i < 4; i++) {
            walkDownFrames[i] = frames[0][i];  // First row - walking down
            walkRightFrames[i] = frames[1][i]; // Second row - walking right
            walkUpFrames[i] = frames[2][i];    // Third row - walking up
            walkLeftFrames[i] = frames[3][i];  // Fourth row - walking left
        }

        // Create animations with 0.15f frame duration
        float frameDuration = 0.15f;
        walkDownAnimation = new Animation<>(frameDuration, walkDownFrames);
        walkRightAnimation = new Animation<>(frameDuration, walkRightFrames);
        walkUpAnimation = new Animation<>(frameDuration, walkUpFrames);
        walkLeftAnimation = new Animation<>(frameDuration, walkLeftFrames);

        // Idle animation uses first frame of walking down
        idleDownAnimation = new Animation<>(frameDuration, walkDownFrames[0]);

        // Set all animations to loop
        walkDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }
    private void loadTextures() {
        // Basic textures
        grassTile = new Texture(Gdx.files.internal("Map/Floor/Grass.png"));
        placeholderTile = new Texture(Gdx.files.internal("placeholder.png"));
        chatIconTexture = new Texture(Gdx.files.internal("assets/Village/chat_icon.png"));

        // Structure textures
        cabinTexture = new Texture(Gdx.files.internal("Map/Floor/Cabin.png"));
        greenhouseTexture = new Texture(Gdx.files.internal("Map/Floor/Greenhouse.png"));
        waterTexture = new Texture(Gdx.files.internal("Map/Floor/Water.png"));
        quarryTexture = new Texture(Gdx.files.internal("Map/Floor/Quarry.png"));
        sellingBinTexture = new Texture(Gdx.files.internal("assets/Inventory/Bin.png"));

        // Load stone textures
        stoneTiles = new Texture[8];
        for (int i = 0; i < 8; i++) {
            stoneTiles[i] = new Texture(Gdx.files.internal("Map/Stone/Stone_" + (i + 1) + ".png"));
        }

        // Load NPC textures
        npcTextures.put("sebastian", new Texture(Gdx.files.internal("assets/Village/sebastian.png")));
        npcTextures.put("abigail", new Texture(Gdx.files.internal("assets/Village/abigail.png")));
        npcTextures.put("harvey", new Texture(Gdx.files.internal("assets/Village/harvey.png")));
        npcTextures.put("leah", new Texture(Gdx.files.internal("assets/Village/leah.png")));
        npcTextures.put("robin", new Texture(Gdx.files.internal("assets/Village/robin.png")));

        // Load Store texture
        storeTexture = new Texture(Gdx.files.internal("assets/Village/store.png"));

        // Load all Tree Textures
        loadTreeTextures();

        // Load all foraging textures
        loadForagingTextures();

        // Load player animations
        loadPlayerAnimations();
    }

    // Basic texture getters
    public Texture getGrassTile() {
        return grassTile;
    }

    public Texture getPlaceholderTile() {
        return placeholderTile;
    }

    public Texture getChatIconTexture() {
        return chatIconTexture;
    }

    // Stone texture methods
    public Texture getStoneTile(int variant) {
        // Ensure variant is between 1-8
        int index = Math.min(Math.max(variant, 1), 8) - 1;
        return stoneTiles[index];
    }

    public Texture getRandomStoneTile() {
        return stoneTiles[random.nextInt(stoneTiles.length)];
    }

    // Structure texture getters
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

    public Texture getSellingBinTexture() {
        return sellingBinTexture;
    }

    // Tree texture methods
    public Texture getTreeTexture(String imagePath) {
        Texture texture = treeTextures.get(imagePath);
        if (texture == null) {
            System.out.println("Missing tree texture: " + imagePath);
            return placeholderTile;
        }
        return texture;
    }

    // Foraging texture methods
    public Texture getForagingMineralTexture(String imagePath) {
        return foragingMineralTextures.getOrDefault(imagePath, placeholderTile);
    }

    public Texture getForagingCropTexture(String imagePath) {
        return foragingCropTextures.getOrDefault(imagePath, placeholderTile);
    }

    public Texture getForagingTreeTexture(String imagePath) {
        return foragingTreeTextures.getOrDefault(imagePath, placeholderTile);
    }
    public Animation<TextureRegion> getWalkDownAnimation() { return walkDownAnimation; }
    public Animation<TextureRegion> getWalkRightAnimation() { return walkRightAnimation; }
    public Animation<TextureRegion> getWalkUpAnimation() { return walkUpAnimation; }
    public Animation<TextureRegion> getWalkLeftAnimation() { return walkLeftAnimation; }
    public Animation<TextureRegion> getIdleAnimation() { return idleDownAnimation; }
    // NPC and Store texture methods
    public Texture getNpcTexture(String npcName) {
        return npcTextures.get(npcName.toLowerCase());
    }

    public Texture getStoreTexture() {
        return storeTexture;
    }

    // Private loading methods
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
        chatIconTexture.dispose();

        // Dispose structure textures
        if (cabinTexture != null) cabinTexture.dispose();
        if (greenhouseTexture != null) greenhouseTexture.dispose();
        if (waterTexture != null) waterTexture.dispose();
        if (quarryTexture != null) quarryTexture.dispose();
        if (sellingBinTexture != null) sellingBinTexture.dispose();

        // Dispose stone textures
        for (Texture stoneTile : stoneTiles) {
            stoneTile.dispose();
        }

        // Dispose NPC textures
        for (Texture npcTexture : npcTextures.values()) {
            npcTexture.dispose();
        }

        // Dispose Store texture
        storeTexture.dispose();

        // Dispose all texture maps
        disposeTextureMap(treeTextures);
        disposeTextureMap(foragingMineralTextures);
        disposeTextureMap(foragingCropTextures);
        disposeTextureMap(foragingTreeTextures);
        if (playerSpriteSheet != null) playerSpriteSheet.dispose();
    }

    private void disposeTextureMap(Map<String, Texture> textureMap) {
        for (Texture texture : textureMap.values()) {
            texture.dispose();
        }
    }
}
