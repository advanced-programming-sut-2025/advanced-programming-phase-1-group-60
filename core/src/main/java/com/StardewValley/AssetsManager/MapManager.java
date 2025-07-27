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
    private Texture chatIconTexture;
    private Texture plowedGroundTexture;
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

    // NPC and Store textures
    private Map<String, Texture> npcTextures;
    private Texture storeTexture;

    // Character textures
    private Texture playerSpriteSheet;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> idleDownAnimation;

    // Animals
    private Map<String, Texture> animalTextures = new HashMap<>();
    private Map<String, Map<String, Animation<TextureRegion>>> animalAnimations = new HashMap<>();
    private Texture coopTexture;
    private Texture barnTexture;
    private Texture hayTexture;

    private MapManager() {
        random = new Random();
        npcTextures = new HashMap<>();
        loadTextures();
        loadAnimalAnimations();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }
    private void loadPlayerAnimations() {
        playerSpriteSheet = new Texture(Gdx.files.internal("Map/Character/Alex.png"));
        TextureRegion[][] frames = TextureRegion.split(playerSpriteSheet,
            playerSpriteSheet.getWidth() / 4, playerSpriteSheet.getHeight() / 4);

        float frameDuration = 0.15f;
        walkDownAnimation = new Animation<>(frameDuration, new TextureRegion[]{frames[0][0], frames[0][1], frames[0][2], frames[0][3]});
        walkRightAnimation = new Animation<>(frameDuration, new TextureRegion[]{frames[1][0], frames[1][1], frames[1][2], frames[1][3]});
        walkUpAnimation = new Animation<>(frameDuration, new TextureRegion[]{frames[2][0], frames[2][1], frames[2][2], frames[2][3]});
        walkLeftAnimation = new Animation<>(frameDuration, new TextureRegion[]{frames[3][0], frames[3][1], frames[3][2], frames[3][3]});
        idleDownAnimation = new Animation<>(frameDuration, frames[0][0]);

        walkDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    private void loadAnimalAnimations() {
        // Coop Animals (Chicken)
        loadAnimalSpriteSheet("Chicken", "assets/Animals/Sprites/Chicken.png", 64, 112, 4, 7, 16, 16, true);
        // Barn Animals (Cow)
        loadAnimalSpriteSheet("Cow", "assets/Animals/Sprites/Cow.png", 128, 160, 4, 5, 32, 32, false);
        // Add other animals here following the same pattern
        loadAnimalSpriteSheet("Duck", "assets/Animals/Sprites/Duck.png", 64, 112, 4, 7, 16, 16, true);
        loadAnimalSpriteSheet("Rabbit", "assets/Animals/Sprites/Rabbit.png", 64, 112, 4, 7, 16, 16, true);
        loadAnimalSpriteSheet("Dinosaur", "assets/Animals/Sprites/Dinosaur.png", 64, 112, 4, 7, 16, 16, true);
        loadAnimalSpriteSheet("Goat", "assets/Animals/Sprites/Goat.png", 128, 160, 4, 5, 32, 32, false);
        loadAnimalSpriteSheet("Sheep", "assets/Animals/Sprites/Sheep.png", 128, 160, 4, 5, 32, 32, false);
        loadAnimalSpriteSheet("Pig", "assets/Animals/Sprites/Pig.png", 128, 160, 4, 5, 32, 32, false);
    }

    private void loadAnimalSpriteSheet(String animalType, String path, int sheetWidth, int sheetHeight, int cols, int rows, int frameWidth, int frameHeight, boolean hasLeft) {
        Map<String, Animation<TextureRegion>> animations = new HashMap<>();
        Texture sheet = new Texture(Gdx.files.internal(path));
        TextureRegion[][] frames = TextureRegion.split(sheet, frameWidth, frameHeight);

        float frameDuration = 0.2f;

        // Down (row 0)
        animations.put("down", new Animation<>(frameDuration, frames[0]));
        // Right (row 1)
        animations.put("right", new Animation<>(frameDuration, frames[1]));
        // Up (row 2)
        animations.put("up", new Animation<>(frameDuration, frames[2]));

        if (hasLeft) {
            // Left (row 3 for coop)
            animations.put("left", new Animation<>(frameDuration, frames[3]));
        } else {
            // Left (flipped from right for barn)
            TextureRegion[] leftFrames = new TextureRegion[cols];
            for (int i = 0; i < cols; i++) {
                leftFrames[i] = new TextureRegion(frames[1][i]);
                leftFrames[i].flip(true, false);
            }
            animations.put("left", new Animation<>(frameDuration, leftFrames));
        }

        // Idle animations (first frame of each direction)
        animations.put("idle_down", new Animation<>(frameDuration, frames[0][0]));
        animations.put("idle_right", new Animation<>(frameDuration, frames[1][0]));
        animations.put("idle_up", new Animation<>(frameDuration, frames[2][0]));
        animations.put("idle_left", new Animation<>(frameDuration, animations.get("left").getKeyFrame(0)));

        for(Animation<TextureRegion> anim : animations.values()) {
            anim.setPlayMode(Animation.PlayMode.LOOP);
        }

        animalAnimations.put(animalType, animations);
    }
    public TextureRegion getPettingFrame(String animalType) {
        String path;
        int frameWidth, frameHeight, row, col;

        switch (animalType) {
            case "Cow":
            case "Goat":
            case "Sheep":
            case "Pig":
                path = "assets/Animals/Sprites/" + animalType + ".png";
                frameWidth = 32;
                frameHeight = 32;
                row = 3; // Row 4 (index 3)
                col = 1; // Column 2 (index 1)
                break;
            case "Chicken":
            case "Duck":
            case "Rabbit":
            case "Dinosaur":
                path = "assets/Animals/Sprites/" + animalType + ".png";
                frameWidth = 16;
                frameHeight = 16;
                row = 6; // Row 7 (index 6)
                col = 2; // Column 3 (index 2)
                break;
            default:
                return null;
        }

        Texture sheet = new Texture(Gdx.files.internal(path));
        TextureRegion[][] frames = TextureRegion.split(sheet, frameWidth, frameHeight);

        if (frames.length > row && frames[row].length > col) {
            return frames[row][col];
        }

        return null;
    }



    public Animation<TextureRegion> getAnimalAnimation(String animalType, String direction, boolean isMoving) {
        Map<String, Animation<TextureRegion>> anims = animalAnimations.get(animalType);
        if (anims == null) return null;

        String animKey = (isMoving ? "" : "idle_") + direction;
        return anims.get(animKey);
    }

    private void loadTextures() {
        grassTile = new Texture(Gdx.files.internal("Map/Floor/Grass.png"));
        placeholderTile = new Texture(Gdx.files.internal("placeholder.png"));
        chatIconTexture = new Texture(Gdx.files.internal("assets/Village/chat_icon.png"));
        cabinTexture = new Texture(Gdx.files.internal("Map/Floor/Cabin.png"));
        greenhouseTexture = new Texture(Gdx.files.internal("Map/Floor/Greenhouse.png"));
        waterTexture = new Texture(Gdx.files.internal("Map/Floor/Water.png"));
        quarryTexture = new Texture(Gdx.files.internal("Map/Floor/Quarry.png"));
        sellingBinTexture = new Texture(Gdx.files.internal("assets/Inventory/Bin.png"));
        hayTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Hay.png"));
        plowedGroundTexture = new Texture(Gdx.files.internal("assets/Map/Floor/Plowed.png"));
        stoneTiles = new Texture[8];
        for (int i = 0; i < 8; i++) {
            stoneTiles[i] = new Texture(Gdx.files.internal("Map/Stone/Stone_" + (i + 1) + ".png"));
        }
        npcTextures.put("sebastian", new Texture(Gdx.files.internal("assets/Village/sebastian.png")));
        npcTextures.put("abigail", new Texture(Gdx.files.internal("assets/Village/abigail.png")));
        npcTextures.put("harvey", new Texture(Gdx.files.internal("assets/Village/harvey.png")));
        npcTextures.put("leah", new Texture(Gdx.files.internal("assets/Village/leah.png")));
        npcTextures.put("robin", new Texture(Gdx.files.internal("assets/Village/robin.png")));
        storeTexture = new Texture(Gdx.files.internal("assets/Village/store.png"));
        loadTreeTextures();
        loadForagingTextures();
        loadPlayerAnimations();
        coopTexture = new Texture(Gdx.files.internal("assets/Inventory/AnimalPlaces/Coop.png"));
        barnTexture = new Texture(Gdx.files.internal("assets/Inventory/AnimalPlaces/Barn.png"));
    }

    public Texture getGrassTile() {
        return grassTile;
    }

    public Texture getHayTexture() {
        return hayTexture;
    }

    public Texture getPlaceholderTile() {
        return placeholderTile;
    }

    public Texture getChatIconTexture() {
        return chatIconTexture;
    }

    public Texture getStoneTile(int variant) {
        int index = Math.min(Math.max(variant, 1), 8) - 1;
        return stoneTiles[index];
    }

    public Texture getRandomStoneTile() {
        return stoneTiles[random.nextInt(stoneTiles.length)];
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

    public Texture getSellingBinTexture() {
        return sellingBinTexture;
    }

    public Texture getTreeTexture(String imagePath) {
        return treeTextures.getOrDefault(imagePath, placeholderTile);
    }

    public Texture getCoopTexture() {
        return coopTexture;
    }

    public Texture getBarnTexture() {
        return barnTexture;
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
    public Animation<TextureRegion> getWalkDownAnimation() { return walkDownAnimation; }
    public Animation<TextureRegion> getWalkRightAnimation() { return walkRightAnimation; }
    public Animation<TextureRegion> getWalkUpAnimation() { return walkUpAnimation; }
    public Animation<TextureRegion> getWalkLeftAnimation() { return walkLeftAnimation; }
    public Animation<TextureRegion> getIdleAnimation() { return idleDownAnimation; }

    public Texture getNpcTexture(String npcName) {
        return npcTextures.get(npcName.toLowerCase());
    }

    public Texture getStoreTexture() {
        return storeTexture;
    }
    public Texture getPlowedGroundTexture() {
        return plowedGroundTexture;
    }
    private void loadTreeTextures() {
        for (Tree tree : TreeRepository.trees) {
            try {
                if (tree.getImagePath() != null) {
                    String path = "Map/Tree/" + tree.getImagePath();
                    if (Gdx.files.internal(path).exists()) {
                        treeTextures.put(tree.getImagePath(), new Texture(Gdx.files.internal(path)));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading tree texture: " + e.getMessage());
            }
        }
    }

    private void loadForagingTextures() {
        for (ForagingMineral mineral : ForagingRepository.foragingMinerals) {
            loadTexture(mineral.getImagePath(), "Map/ForagingMineral/", foragingMineralTextures, "mineral");
        }
        for (ForagingCrop crop : ForagingRepository.foragingCrops) {
            loadTexture(crop.getImagePath(), "Map/ForagingCrop/", foragingCropTextures, "crop");
        }
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
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading foraging " + type + " texture: " + e.getMessage());
        }
    }

    public void dispose() {
        grassTile.dispose();
        placeholderTile.dispose();
        chatIconTexture.dispose();
        if (cabinTexture != null) cabinTexture.dispose();
        if (greenhouseTexture != null) greenhouseTexture.dispose();
        if (waterTexture != null) waterTexture.dispose();
        if (quarryTexture != null) quarryTexture.dispose();
        if (sellingBinTexture != null) sellingBinTexture.dispose();
        for (Texture stoneTile : stoneTiles) {
            stoneTile.dispose();
        }
        for (Texture npcTexture : npcTextures.values()) {
            npcTexture.dispose();
        }
        if (coopTexture != null) coopTexture.dispose();
        if (barnTexture != null) barnTexture.dispose();
        disposeTextureMap(animalTextures);
        storeTexture.dispose();
        disposeTextureMap(treeTextures);
        disposeTextureMap(foragingMineralTextures);
        disposeTextureMap(foragingCropTextures);
        disposeTextureMap(foragingTreeTextures);
        if (playerSpriteSheet != null) playerSpriteSheet.dispose();
        for(Map<String, Animation<TextureRegion>> anims : animalAnimations.values()) {
            for(Animation<TextureRegion> anim : anims.values()) {
                for(TextureRegion region : anim.getKeyFrames()) {
                }
            }
        }
        if (plowedGroundTexture != null) plowedGroundTexture.dispose();
    }

    private void disposeTextureMap(Map<String, Texture> textureMap) {
        for (Texture texture : textureMap.values()) {
            texture.dispose();
        }
    }
}
