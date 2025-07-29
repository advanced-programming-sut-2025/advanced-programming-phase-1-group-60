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
    private Texture pixelWhiteTexture; // For drawing highlights (e.g., a 1x1 white pixel)
    private Map<String, Texture> buildingTextures = new HashMap<>();
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
        loadBuildingTextures();
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
        walkDownAnimation = new Animation<>(frameDuration, new TextureRegion[]{frames[0][0], frames[0][1], frames[0][2], frames[0][3]});
        walkRightAnimation = new Animation<>(frameDuration, new TextureRegion[]{frames[1][0], frames[1][1], frames[1][2], frames[1][3]});
        walkUpAnimation = new Animation<>(frameDuration, new TextureRegion[]{frames[2][0], frames[2][1], frames[2][2], frames[2][3]});
        walkLeftAnimation = new Animation<>(frameDuration, new TextureRegion[]{frames[3][0], frames[3][1], frames[3][2], frames[3][3]});
        idleDownAnimation = new Animation<>(frameDuration, frames[0][0]);

        // Idle animation uses first frame of walking down
        idleDownAnimation = new Animation<>(frameDuration, walkDownFrames[0]);

        // Set all animations to loop
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
        hayTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Hay.png"));
        plowedGroundTexture = new Texture(Gdx.files.internal("assets/Map/Floor/Plowed.png"));
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
        storeTexture = new Texture(Gdx.files.internal("assets/Village/store.png"));
        loadTreeTextures();
        loadForagingTextures();
        loadPlayerAnimations();
        coopTexture = new Texture(Gdx.files.internal("assets/Inventory/AnimalPlaces/Coop.png"));
        barnTexture = new Texture(Gdx.files.internal("assets/Inventory/AnimalPlaces/Barn.png"));
    }

    // Basic texture getters
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

    // Stone texture methods
    public Texture getStoneTile(int variant) {
        // Ensure variant is between 1-8
        int index = Math.min(Math.max(variant, 1), 8) - 1;
        return stoneTiles[index];
    }
    private void loadBuildingTextures() {
        try {
            // For a generic white pixel texture for drawing colored rectangles
//            pixelWhiteTexture = new Texture(Gdx.files.internal("assets/textures/pixel_white.png")); // <--- YOU NEED TO CREATE THIS 1x1 WHITE PNG
//            System.out.println("Loaded pixel_white.png for highlighting.");

            // Example building textures (adjust paths and names to your actual assets):
            if (Gdx.files.internal("assets/Inventory/Bee_House.png").exists()) {
                buildingTextures.put("Bee_House", new Texture(Gdx.files.internal("assets/Inventory/Bee_House.png")));
                System.out.println("Loaded building texture: Bee_House");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Bee_House.png");
            }
            if (Gdx.files.internal("assets/Inventory/Cheese_Press.png").exists()) {
                buildingTextures.put("Cheese_Press", new Texture(Gdx.files.internal("assets/Inventory/Cheese_Press.png")));
                System.out.println("Loaded building texture: Cheese_Press");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Cheese_Press.png");
            }
            if (Gdx.files.internal("assets/Inventory/Keg.png").exists()) {
                buildingTextures.put("Keg", new Texture(Gdx.files.internal("assets/Inventory/Keg.png")));
                System.out.println("Loaded building texture: Keg");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Keg.png");
            }
            if (Gdx.files.internal("assets/Inventory/Dehydrator.png").exists()) {
                buildingTextures.put("Dehydrator", new Texture(Gdx.files.internal("assets/Inventory/Dehydrator.png")));
                System.out.println("Loaded building texture: Dehydrator");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Dehydrator.png");
            }
            if (Gdx.files.internal("assets/Inventory/Dehydrator.png").exists()) {
                buildingTextures.put("Dehydrator", new Texture(Gdx.files.internal("assets/Inventory/Dehydrator.png")));
                System.out.println("Loaded building texture: Dehydrator");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Dehydrator.png");
            }
            if (Gdx.files.internal("assets/Inventory/Charcoal_Kiln.png").exists()) {
                buildingTextures.put("Charcoal_Kiln", new Texture(Gdx.files.internal("assets/Inventory/Charcoal_Kiln.png")));
                System.out.println("Loaded building texture: Charcoal_Kiln");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Charcoal_Kiln.png");
            }
            if (Gdx.files.internal("assets/Inventory/Loom.png").exists()) {
                buildingTextures.put("Loom", new Texture(Gdx.files.internal("assets/Inventory/Loom.png")));
                System.out.println("Loaded building texture: Loom");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Loom.png");
            }
            if (Gdx.files.internal("assets/Inventory/Mayonnaise_Machine.png").exists()) {
                buildingTextures.put("Mayonnaise_Machine", new Texture(Gdx.files.internal("assets/Inventory/Mayonnaise_Machine.png")));
                System.out.println("Loaded building texture: Mayonnaise_Machine");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Mayonnaise_Machine.png");
            }
            if (Gdx.files.internal("assets/Inventory/Oil_Maker.png").exists()) {
                buildingTextures.put("Oil_Maker", new Texture(Gdx.files.internal("assets/Inventory/Oil_Maker.png")));
                System.out.println("Loaded building texture: Oil_Maker");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Oil_Maker.png");
            }
            if (Gdx.files.internal("assets/Inventory/Preserves_Jar.png").exists()) {
                buildingTextures.put("Preserves_Jar", new Texture(Gdx.files.internal("assets/Inventory/Preserves_Jar.png")));
                System.out.println("Loaded building texture: Preserves_Jar");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Preserves_Jar.png");
            }
            if (Gdx.files.internal("assets/Inventory/Fish_Smoker.png").exists()) {
                buildingTextures.put("Fish_Smoker", new Texture(Gdx.files.internal("assets/Inventory/Fish_Smoker.png")));
                System.out.println("Loaded building texture: Fish_Smoker");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Fish_Smoker.png");
            }
            if (Gdx.files.internal("assets/Inventory/Furnace.png").exists()) {
                buildingTextures.put("Furnace", new Texture(Gdx.files.internal("assets/Inventory/Furnace.png")));
                System.out.println("Loaded building texture: Furnace");
            } else {
                System.out.println("Building texture file not found: assets/Inventory/Furnace.png");
            }

        } catch (Exception e) {
            System.err.println("Error loading building textures: " + e.getMessage());
        }
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
        if (coopTexture != null) coopTexture.dispose();
        if (barnTexture != null) barnTexture.dispose();
        disposeTextureMap(animalTextures);
        storeTexture.dispose();

        // Dispose all texture maps
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
    // Add this new public getter for the generic white pixel texture:
    public Texture getPixelWhiteTexture() {
//        // This is used by MapView for drawing colored highlights.
//        if (pixelWhiteTexture == null) {
//            // Fallback or warning if not loaded
//            System.err.println("Pixel white texture not loaded. Returning placeholder.");
//            return placeholderTile; // Or throw an error
//        }
//        return pixelWhiteTexture;
        return placeholderTile;
    }

    // Add this new public getter for specific building textures:
    public Texture getBuildingTexture(String buildingName) {
        // This will be used by MapView to get the actual texture for a placed building
        // or the ghost image during build mode.
        Texture texture = buildingTextures.get(buildingName);
        if (texture == null) {
            System.out.println("Missing building texture for: " + buildingName + ". Returning placeholder.");
            return placeholderTile; // Return a default/placeholder if not found
        }
        return texture;
    }

    // Add a more generic `getTexture` method if you want a unified way to retrieve any texture by name.
    // This can be useful for dynamic texture loading or fetching the `pixel_white` by its name.
    public Texture getTexture(String name) {
        // This is a simplified example; you might want to combine all your texture maps here
        // or create a more robust texture management system.
        switch (name) {
            case "pixel_white": return getPixelWhiteTexture();
            case "grass": return grassTile;
            case "water": return waterTexture;
            // Add cases for other commonly accessed textures if you want to use this method generally.
            // For buildings, it's better to use getBuildingTexture().
            default:
                Texture buildingTex = buildingTextures.get(name);
                if (buildingTex != null) return buildingTex;
                System.out.println("Generic texture '" + name + "' not found. Returning placeholder.");
                return placeholderTile;
        }
    }
    private void disposeTextureMap(Map<String, Texture> textureMap) {
        for (Texture texture : textureMap.values()) {
            texture.dispose();
        }
        if (pixelWhiteTexture != null) pixelWhiteTexture.dispose(); // <-- NEW LINE

        // Dispose building textures map
        disposeTextureMap(buildingTextures);
    }
}
