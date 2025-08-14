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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MapManager {
    private static MapManager instance;
    private Random random;
    private Texture faintTexture;
    private Animation<TextureRegion> faintAnimation;
    // Map textures
    private Texture grassTile;
    private final Map<String, Texture> seasonFloorTextures = new HashMap<>();
    private Texture placeholderTile;
    private Texture chatIconTexture; // Chat icon from the new version
    private Texture pixelWhiteTexture; // For drawing highlights (e.g., a 1x1 white pixel)
    private Map<String, Texture> buildingTextures = new HashMap<>();
    private Texture plowedGroundTexture;
    private Texture wateredGroundTexture;
    private final Map<String, Texture> fertilizerTextures = new HashMap<>();
    private Texture clockMainTexture;
    private Texture clockMomentTexture;
    private TextureRegion[] seasonRegions;   // 4 regions (row 0)
    private TextureRegion[] weatherRegions;
    private final Map<String, Texture> clockSeasonIconTextures = new HashMap<>();
    private final Map<String, Texture> clockWeatherIconTextures = new HashMap<>();
    // Stone textures
    private Texture[] stoneTiles;
    //Fish:
    public static TextureRegion salmon = new TextureRegion(new Texture("Fish/Salmon.png"));
    public static TextureRegion sardine = new TextureRegion(new Texture("Fish/Sardine.png"));
    public static TextureRegion shad = new TextureRegion(new Texture("Fish/Shad.png"));
    public static TextureRegion blueDiscus = new TextureRegion(new Texture("Fish/Blue_Discus.png"));
    public static TextureRegion midnightCarp = new TextureRegion(new Texture("Fish/Midnight_Carp.png"));
    public static TextureRegion squid = new TextureRegion(new Texture("Fish/Squid.png"));
    public static TextureRegion tuna = new TextureRegion(new Texture("Fish/Tuna.png"));
    public static TextureRegion perch = new TextureRegion(new Texture("Fish/Perch.png"));
    public static TextureRegion flounder = new TextureRegion(new Texture("Fish/Flounder.png"));
    public static TextureRegion lionfish = new TextureRegion(new Texture("Fish/Lionfish.png"));
    public static TextureRegion herring = new TextureRegion(new Texture("Fish/Herring.png"));
    public static TextureRegion ghostfish = new TextureRegion(new Texture("Fish/Ghostfish.png"));
    public static TextureRegion tilapia = new TextureRegion(new Texture("Fish/Tilapia.png"));
    public static TextureRegion dorado = new TextureRegion(new Texture("Fish/Dorado.png"));
    public static TextureRegion sunfish = new TextureRegion(new Texture("Fish/Sunfish.png"));
    public static TextureRegion rainbowTrout = new TextureRegion(new Texture("Fish/Rainbow_Trout.png"));
    public static TextureRegion legend = new TextureRegion(new Texture("Fish/Legend.png"));
    public static TextureRegion glacierfish = new TextureRegion(new Texture("Fish/Glacierfish.png"));
    public static TextureRegion angler = new TextureRegion(new Texture("Fish/Angler.png"));
    public static TextureRegion crimsonfish = new TextureRegion(new Texture("Fish/Crimsonfish.png"));

    //Mini_Game:
    public static TextureRegionDrawable fishingSystem = new TextureRegionDrawable(new TextureRegion(new Texture("MiniGame/FishingSystem.png")));
    public static TextureRegionDrawable greenBar = new TextureRegionDrawable(new TextureRegion(new Texture("MiniGame/Green_Bar.png")));
    public static TextureRegionDrawable normalFish = new TextureRegionDrawable(new TextureRegion(new Texture("MiniGame/Normal_Fish.png")));
    public static TextureRegionDrawable legendFish = new TextureRegionDrawable(new TextureRegion(new Texture("MiniGame/Legend_Fish.png")));

    // Tree Textures
    private Map<String, Texture> treeTextures = new HashMap<>();

    // Foraging Stuff
    private Map<String, Texture> foragingMineralTextures = new HashMap<>();
    private Map<String, Texture> foragingCropTextures = new HashMap<>();
    private Map<String, Texture> foragingTreeTextures = new HashMap<>();

    // Structures
    private Texture cabinTexture;
    private Texture greenhouseTexture;
    private Texture greenhouseBrokenTexture;
    private Texture waterTexture;
    private Texture quarryTexture;
    private Texture sellingBinTexture;

    // NPC and Store textures
    private Map<String, Texture> npcTextures;
    private Texture storeTexture;
    private Texture cabinsSheet;
    private TextureRegion[] storeVariantRegions; // 3 variants (rows)
    private Map<String, TextureRegion> assignedStoreVariants = new HashMap<>();
    // Character textures
    private Texture playerSpriteSheet;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> idleDownAnimation;
    private Texture[] playerTextures;
    // Animals
    private Map<String, Texture> animalTextures = new HashMap<>();
    private Map<String, Map<String, Animation<TextureRegion>>> animalAnimations = new HashMap<>();
    private Texture coopTexture;
    private Texture barnTexture;
    private Texture hayTexture;

    // Lightning
    private Animation<TextureRegion> lightningAnimation;
    private Texture[] lightningFrameTextures; // to dispose later
    private Texture burntTreeTexture;
    private static final float LIGHTNING_FRAME_DURATION = 0.07f;

    private MapManager() {
        random = new Random();
        npcTextures = new HashMap<>();
        loadTextures();
        loadAnimalAnimations();
        loadBuildingTextures();
        loadLightningAssets();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }
    private void loadPlayerAnimations() {
        // Load separate sprite sheets for each direction
        Texture walkDownSheet = new Texture(Gdx.files.internal("assets/Character/Walk/Walk_1.png"));
        Texture walkLeftSheet = new Texture(Gdx.files.internal("assets/Character/Walk/Walk_2.png"));
        Texture walkRightSheet = new Texture(Gdx.files.internal("assets/Character/Walk/Walk_3.png"));
        Texture walkUpSheet = new Texture(Gdx.files.internal("assets/Character/Walk/Walk_4.png"));

        // Store textures to dispose of them later
        playerTextures = new Texture[]{walkDownSheet, walkLeftSheet, walkRightSheet, walkUpSheet};

        // Calculate frame width and height for each direction
        int frameWidth = walkDownSheet.getWidth() / 5;
        int frameHeight = walkDownSheet.getHeight();

        // Extract frames from each direction's sprite sheet (each has 5 frames)
        TextureRegion[][] walkDownFrames = TextureRegion.split(walkDownSheet, frameWidth, frameHeight);
        TextureRegion[][] walkLeftFrames = TextureRegion.split(walkLeftSheet, frameWidth, frameHeight);
        TextureRegion[][] walkRightFrames = TextureRegion.split(walkRightSheet, frameWidth, frameHeight);
        TextureRegion[][] walkUpFrames = TextureRegion.split(walkUpSheet, frameWidth, frameHeight);

        // Create animation arrays for each direction
        TextureRegion[] walkDown = new TextureRegion[5];
        TextureRegion[] walkLeft = new TextureRegion[5];
        TextureRegion[] walkRight = new TextureRegion[5];
        TextureRegion[] walkUp = new TextureRegion[5];

        // Extract frames for each direction and trim problematic pixels if needed
        for (int i = 0; i < 5; i++) {
            // Normal frames for down and right (no artifacts)
            walkDown[i] = walkDownFrames[0][i];
            walkRight[i] = walkRightFrames[0][i];

            // For left and up animations, trim 1 pixel from edges to remove artifacts
            walkLeft[i] = new TextureRegion(walkLeftFrames[0][i]);
            walkLeft[i].setRegion(
                walkLeft[i].getRegionX() + 1,  // Trim left edge
                walkLeft[i].getRegionY(),
                walkLeft[i].getRegionWidth() - 2,  // Trim right edge
                walkLeft[i].getRegionHeight()
            );

            walkUp[i] = new TextureRegion(walkUpFrames[0][i]);
            walkUp[i].setRegion(
                walkUp[i].getRegionX() + 1,  // Trim left edge
                walkUp[i].getRegionY(),
                walkUp[i].getRegionWidth() - 2,  // Trim right edge
                walkUp[i].getRegionHeight()
            );
        }

        // Create animations with optimized frame duration
        float frameDuration = 0.12f; // Slightly faster for smoother animation

        walkDownAnimation = new Animation<>(frameDuration, walkDown);
        walkLeftAnimation = new Animation<>(frameDuration, walkLeft);
        walkRightAnimation = new Animation<>(frameDuration, walkRight);
        walkUpAnimation = new Animation<>(frameDuration, walkUp);

        // Create idle animations using first frame of each walk animation
        idleDownAnimation = new Animation<>(frameDuration, walkDown[0]);

        // Explicitly set all animations to loop
        walkDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkUpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        idleDownAnimation.setPlayMode(Animation.PlayMode.NORMAL); // Idle doesn't need to loop
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
    private void loadLightningAssets() {
        try {
            lightningFrameTextures = new Texture[7];
            TextureRegion[] regions = new TextureRegion[7];
            boolean any = false;
            for (int i = 1; i <= 7; i++) {
                String path = "assets/Map/Lightning/" + i + ".png";
                if (Gdx.files.internal(path).exists()) {
                    Texture t = new Texture(Gdx.files.internal(path));
                    lightningFrameTextures[i - 1] = t;
                    regions[i - 1] = new TextureRegion(t);
                    any = true;
                } else {
                    System.err.println("[Lightning] Missing frame: " + path);
                }
            }
            if (any) {
                lightningAnimation = new Animation<>(LIGHTNING_FRAME_DURATION, regions);
                lightningAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            } else {
                lightningAnimation = null;
            }
            String burntPath = "assets/Map/Lightning/Burnt.png";
            if (Gdx.files.internal(burntPath).exists()) {
                burntTreeTexture = new Texture(Gdx.files.internal(burntPath));
            } else {
                System.err.println("[Lightning] Missing burnt tree texture: " + burntPath);
            }
        } catch (Exception e) {
            System.err.println("[Lightning] Error loading assets: " + e.getMessage());
        }
    }
    public Animation<TextureRegion> getLightningAnimation() {
        return lightningAnimation;
    }
    public Texture getBurntTreeTexture() {
        return burntTreeTexture != null ? burntTreeTexture : placeholderTile;
    }
    public Texture getSeasonClockFrame(String season) {
        if (season == null) return clockMainTexture;
        Texture t = clockSeasonIconTextures.get(season.toLowerCase());
        return (t != null) ? t : clockMainTexture;
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
        wateredGroundTexture = new Texture(Gdx.files.internal("assets/Map/Floor/Watered.png"));
        loadClockTextures();
        // Structure textures
        cabinTexture = new Texture(Gdx.files.internal("Map/Floor/Cabin.png"));
        greenhouseTexture = new Texture(Gdx.files.internal("Map/Floor/Greenhouse.png"));
        String brokenPath = "assets/Map/Floor/Greenhouse_Broken.png";
        if (Gdx.files.internal(brokenPath).exists()) {
            greenhouseBrokenTexture = new Texture(Gdx.files.internal(brokenPath));
        } else {
            greenhouseBrokenTexture = null; // keep null; getter will return placeholder to make issue visible
        }
        waterTexture = new Texture(Gdx.files.internal("Map/Floor/Water.png"));
        quarryTexture = new Texture(Gdx.files.internal("Map/Floor/Quarry.png"));
        sellingBinTexture = new Texture(Gdx.files.internal("assets/Inventory/Bin.png"));
        hayTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Hay.png"));
        plowedGroundTexture = new Texture(Gdx.files.internal("assets/Map/Floor/Plowed.png"));
        stoneTiles = new Texture[8];
        for (int i = 0; i < 8; i++) {
            stoneTiles[i] = new Texture(Gdx.files.internal("Map/Stone/Stone_" + (i + 1) + ".png"));
        }
        loadSeasonFloorTextures();
        loadFertilizerTextures();
        // Load NPC textures
        npcTextures.put("sebastian", new Texture(Gdx.files.internal("assets/Village/sebastian.png")));
        npcTextures.put("abigail", new Texture(Gdx.files.internal("assets/Village/abigail.png")));
        npcTextures.put("harvey", new Texture(Gdx.files.internal("assets/Village/harvey.png")));
        npcTextures.put("leah", new Texture(Gdx.files.internal("assets/Village/leah.png")));
        npcTextures.put("robin", new Texture(Gdx.files.internal("assets/Village/robin.png")));
        storeTexture = new Texture(Gdx.files.internal("assets/Village/store.png"));
        loadStoreVariants();
        loadTreeTextures();
        loadForagingTextures();
        loadPlayerAnimations();
        loadFaintAnimation();
        coopTexture = new Texture(Gdx.files.internal("assets/Inventory/AnimalPlaces/Coop.png"));
        barnTexture = new Texture(Gdx.files.internal("assets/Inventory/AnimalPlaces/Barn.png"));
    }
    public Animation<TextureRegion> getFaintAnimation() {
        return faintAnimation;
    }
    private void loadFaintAnimation() {
        String path = "assets/Character/Faint.png";
        if (!Gdx.files.internal(path).exists()) {
            System.err.println("[Faint] Missing " + path);
            return;
        }
        faintTexture = new Texture(Gdx.files.internal(path));
        int fullW = faintTexture.getWidth();
        int fullH = faintTexture.getHeight();
        int frameCount = 2; // as specified
        int frameW = fullW / frameCount;
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(faintTexture, i * frameW, 0, frameW, fullH);
        }
        // 0.55s per frame (tweak)
        faintAnimation = new Animation<>(0.55f, frames);
        faintAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        System.out.println("[Faint] Loaded faint animation frames.");
    }
    private void loadStoreVariants() {
        String path = "assets/Map/Store/Cabins.png";
        if (!Gdx.files.internal(path).exists()) {
            System.err.println("[StoreVariants] Missing " + path + " (using fallback single-tile store texture).");
            return;
        }
        cabinsSheet = new Texture(Gdx.files.internal(path));
        int totalHeight = cabinsSheet.getHeight();
        int totalWidth = cabinsSheet.getWidth();
        int rows = 3; // as specified
        int regionHeight = totalHeight / rows;
        storeVariantRegions = new TextureRegion[rows];
        for (int i = 0; i < rows; i++) {
            storeVariantRegions[i] = new TextureRegion(cabinsSheet, 0, i * regionHeight, totalWidth, regionHeight);
        }
        System.out.println("[StoreVariants] Loaded " + rows + " store variants from Cabins.png");
    }
    public TextureRegion getStoreVariant(String storeName) {
        if (storeVariantRegions == null || storeVariantRegions.length == 0) {
            // fallback: wrap legacy texture if available
            if (storeTexture != null) {
                return new TextureRegion(storeTexture);
            }
            return null;
        }
        String key = storeName == null ? "default" : storeName.toLowerCase();
        if (!assignedStoreVariants.containsKey(key)) {
            TextureRegion chosen = storeVariantRegions[random.nextInt(storeVariantRegions.length)];
            assignedStoreVariants.put(key, chosen);
        }
        return assignedStoreVariants.get(key);
    }
    public int getStoreVariantWidthTiles(TextureRegion region) {
        if (region == null) return 1;
        // Assume 32px tile
        return Math.max(1, Math.round(region.getRegionWidth() / 32f));
    }

    public int getStoreVariantHeightTiles(TextureRegion region) {
        if (region == null) return 1;
        return Math.max(1, Math.round(region.getRegionHeight() / 32f));
    }
    private void loadSeasonFloorTextures() {
        loadSeasonFloorTexture("Spring");
        loadSeasonFloorTexture("Summer");
        loadSeasonFloorTexture("Fall");
        loadSeasonFloorTexture("Winter");
    }
    private void loadSeasonFloorTexture(String season) {
        String path = "assets/Map/Floor/" + season + ".png";
        if (Gdx.files.internal(path).exists()) {
            seasonFloorTextures.put(season, new Texture(Gdx.files.internal(path)));
        } else {
            System.out.println("Season floor texture missing for " + season + " at " + path + " (using fallback grass).");
        }
    }
    public Texture getSeasonFloorTexture(String season) {
        Texture t = seasonFloorTextures.get(season);
        return (t != null) ? t : grassTile;
    }
    private void loadClockTextures() {
        try {
            String mainPath = "assets/Map/Clock/Main.png";
            String momentPath = "assets/Map/Clock/Moment.png";
            if (Gdx.files.internal(mainPath).exists()) {
                clockMainTexture = new Texture(Gdx.files.internal(mainPath));
            } else {
                System.err.println("[Clock] Missing " + mainPath);
            }
            if (Gdx.files.internal(momentPath).exists()) {
                clockMomentTexture = new Texture(Gdx.files.internal(momentPath));
                int cols = 4;
                int rows = 3;
                int cellW = clockMomentTexture.getWidth() / cols;
                int cellH = clockMomentTexture.getHeight() / rows;
                seasonRegions = new TextureRegion[4];
                weatherRegions = new TextureRegion[4];
                for (int i = 0; i < 4; i++) {
                    seasonRegions[i] = new TextureRegion(clockMomentTexture, i * cellW, 0, cellW, cellH);
                    weatherRegions[i] = new TextureRegion(clockMomentTexture, i * cellW, cellH, cellW, cellH);
                }
            } else {
                System.err.println("[Clock] Missing " + momentPath);
            }

            // NEW: Load standalone season icon textures (per-file)
            String[] seasons = {"Spring","Summer","Fall","Winter"};
            for (String s : seasons) {
                String p = "assets/Map/Clock/" + s + ".png";
                if (Gdx.files.internal(p).exists()) {
                    clockSeasonIconTextures.put(s.toLowerCase(), new Texture(Gdx.files.internal(p)));
                }
            }

            // NEW: Attempt weather icons (currently only clear sky specified)
            // We'll try common names; whichever exists will be used.
            String[] weatherCandidates = {"Clear","ClearSky","Sunny","Sun"};
            for (String w : weatherCandidates) {
                String p = "assets/Map/Clock/" + w + ".png";
                if (Gdx.files.internal(p).exists()) {
                    clockWeatherIconTextures.put(w.toLowerCase(), new Texture(Gdx.files.internal(p)));
                }
            }
        } catch (Exception e) {
            System.err.println("[Clock] Error loading clock textures: " + e.getMessage());
        }
    }
    public Texture getSeasonIconTexture(String season) {
        if (season == null) return null;
        return clockSeasonIconTextures.get(season.toLowerCase());
    }
    public Texture getWeatherIconTexture(String weather) {
        if (weather == null) return null;
        String lower = weather.toLowerCase();
        if (clockWeatherIconTextures.containsKey(lower)) {
            return clockWeatherIconTextures.get(lower);
        }
        // Map synonyms
        if (lower.equals("sunny") && clockWeatherIconTextures.containsKey("clear")) {
            return clockWeatherIconTextures.get("clear");
        }
        if (lower.equals("clear") && clockWeatherIconTextures.containsKey("sunny")) {
            return clockWeatherIconTextures.get("sunny");
        }
        return null;
    }
    public Texture getClockMainTexture() {
        return clockMainTexture;
    }
    public TextureRegion getSeasonRegion(String season) {
        if (seasonRegions == null) return null;
        if (season == null) return seasonRegions[0];
        switch (season) {
            case "Spring": return seasonRegions[0];
            case "Summer": return seasonRegions[1];
            case "Fall":   return seasonRegions[2];
            case "Winter": return seasonRegions[3];
            default: return seasonRegions[0];
        }
    }
    public TextureRegion getWeatherRegion(String weather) {
        if (weatherRegions == null) return null;
        if (weather == null) return weatherRegions[0];
        // Map weather strings to index (adjust if needed)
        switch (weather.toLowerCase()) {
            case "sunny": return weatherRegions[0];
            case "rain":  return weatherRegions[1];
            case "snow":  return weatherRegions[2];
            case "storm": return weatherRegions[3];
            default: return weatherRegions[0];
        }
    }
    private void loadFertilizerTextures() {
        loadFertilizerTexture("Basic_Fertilizer");
        loadFertilizerTexture("Deluxe_Fertilizer");
        loadFertilizerTexture("Quality_Fertilizer");
    }
    private void loadFertilizerTexture(String name) {
        String path = "assets/Map/FruitsAndVegetables/Fertilizer/" + name + ".png";
        if (Gdx.files.internal(path).exists()) {
            fertilizerTextures.put(name, new Texture(Gdx.files.internal(path)));
        }
    }
    public Texture getWateredGroundTexture() {
        return wateredGroundTexture;
    }
    public Texture getFertilizerTexture(String fertName) {
        return fertilizerTextures.get(fertName);
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
    public Texture getGreenhouseBrokenTexture() {
        return greenhouseBrokenTexture != null ? greenhouseBrokenTexture : greenhouseTexture;
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
        if (wateredGroundTexture != null) wateredGroundTexture.dispose();
        for (Texture t : fertilizerTextures.values()) if (t != null) t.dispose();
        fertilizerTextures.clear();
        // Dispose structure textures
        if (cabinTexture != null) cabinTexture.dispose();
        if (greenhouseTexture != null) greenhouseTexture.dispose();
        if (waterTexture != null) waterTexture.dispose();
        if (quarryTexture != null) quarryTexture.dispose();
        if (sellingBinTexture != null) sellingBinTexture.dispose();
        if (clockMainTexture != null) clockMainTexture.dispose();
        if (clockMomentTexture != null) clockMomentTexture.dispose();
        for (Texture t : clockSeasonIconTextures.values()) t.dispose();
        for (Texture t : clockWeatherIconTextures.values()) t.dispose();
        if (greenhouseBrokenTexture != null) greenhouseBrokenTexture.dispose();
        if (burntTreeTexture != null) burntTreeTexture.dispose();
        if (lightningFrameTextures != null) {
            for (Texture t : lightningFrameTextures) {
                if (t != null) t.dispose();
            }
        }
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
        if (playerTextures != null) {
            for (Texture texture : playerTextures) {
                if (texture != null) texture.dispose();
            }
        }
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
