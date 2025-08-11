package com.StardewValley.AssetsManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.StardewValley.models.Tools;

public class ToolManager {
    private static ToolManager instance;

    // Hoe textures for different stages
    private Texture hoeBeginnerTexture;
    private Texture hoeCopperTexture;
    private Texture hoeIronTexture;
    private Texture hoeGoldTexture;
    private Texture hoeIridiumTexture;

    // Axe textures for different stages
    private Texture axeBeginnerTexture;
    private Texture axeCopperTexture;
    private Texture axeIronTexture;
    private Texture axeGoldTexture;
    private Texture axeIridiumTexture;

    // Pickaxe textures for different stages
    private Texture pickaxeBeginnerTexture;
    private Texture pickaxeCopperTexture;
    private Texture pickaxeIronTexture;
    private Texture pickaxeGoldTexture;
    private Texture pickaxeIridiumTexture;

    // Additional tools textures
    private Texture wateringCanTexture;
    private Texture fishingPoleTexture;
    private Texture scytheTexture;
    private Texture milkPailTexture;
    private Texture shearsTexture;

    // Old swing data
    private boolean isSwinging = false;
    private float swingTime = 0f;
    private static final float SWING_DURATION = 0.6f;
    private Tools.HoeStage currentHoeStage = Tools.HoeStage.BEGINNER;
    private Tools.AxeStage currentAxeStage = Tools.AxeStage.BEGINNER;
    private Tools.PickaxeStage currentPickaxeStage = Tools.PickaxeStage.BEGINNER;
    private String currentToolType = "Hoe";

    // NEW tool-use character animations
    private static final int TOOL_FRAMES = 6;
    private static final float TOOL_FRAME_DURATION = 0.07f;
    private Texture toolDownSheet;
    private Texture toolRightSheet;
    private Texture toolLeftSheet;
    private Texture toolUpSheet;

    private Animation<TextureRegion> animDown;
    private Animation<TextureRegion> animRight;
    private Animation<TextureRegion> animLeft;
    private Animation<TextureRegion> animUp;

    private boolean usingTool = false;
    private float toolUseTime = 0f;
    private float toolUseTotal;

    private ToolManager() {
        loadToolTextures();
    }

    public static ToolManager getInstance() {
        if (instance == null) {
            instance = new ToolManager();
        }
        return instance;
    }

    private void loadToolTextures() {
        // Hoe
        hoeBeginnerTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Hoe.png"));
        hoeCopperTexture   = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Copper_Hoe.png"));
        hoeIronTexture     = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Steel_Hoe.png"));
        hoeGoldTexture     = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Gold_Hoe.png"));
        hoeIridiumTexture  = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Iridium_Hoe.png"));

        // Axe
        axeBeginnerTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Axe.png"));
        axeCopperTexture   = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Copper_Axe.png"));
        axeIronTexture     = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Steel_Axe.png"));
        axeGoldTexture     = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Gold_Axe.png"));
        axeIridiumTexture  = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Iridium_Axe.png"));

        // Pickaxe
        pickaxeBeginnerTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Pickaxe.png"));
        pickaxeCopperTexture   = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Copper_Pickaxe.png"));
        pickaxeIronTexture     = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Steel_Pickaxe.png"));
        pickaxeGoldTexture     = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Gold_Pickaxe.png"));
        pickaxeIridiumTexture  = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Iridium_Pickaxe.png"));

        // Other tools
        wateringCanTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Watering_Can.png"));
        fishingPoleTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Training_Rod.png"));
        scytheTexture      = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Scythe.png"));
        milkPailTexture    = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Milk_Pail.png"));
        shearsTexture      = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Shears.png"));

        // Tool-use sheets
        toolDownSheet  = new Texture(Gdx.files.internal("assets/Character/Tool/Tool_1.png")); // Down
        toolRightSheet = new Texture(Gdx.files.internal("assets/Character/Tool/Tool_2.png")); // Right
        toolLeftSheet  = new Texture(Gdx.files.internal("assets/Character/Tool/Tool_3.png")); // Left (reverse order)
        toolUpSheet    = new Texture(Gdx.files.internal("assets/Character/Tool/Tool_4.png")); // Up

        animDown  = buildAnimation(toolDownSheet, false);
        animRight = buildAnimation(toolRightSheet, false);
        // Left sheet frames are laid out in reverse (right->left) per your note; reverse the playback order:
        animLeft  = buildAnimation(toolLeftSheet, true);
        animUp    = buildAnimation(toolUpSheet, false);

        toolUseTotal = TOOL_FRAMES * TOOL_FRAME_DURATION;
    }

    private Animation<TextureRegion> buildAnimation(Texture sheet, boolean reverseOrder) {
        int frameWidth = sheet.getWidth() / TOOL_FRAMES;
        int frameHeight = sheet.getHeight();
        TextureRegion[][] split = TextureRegion.split(sheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[TOOL_FRAMES];
        if (reverseOrder) {
            for (int i = 0; i < TOOL_FRAMES; i++) {
                frames[i] = split[0][TOOL_FRAMES - 1 - i];
            }
        } else {
            for (int i = 0; i < TOOL_FRAMES; i++) {
                frames[i] = split[0][i];
            }
        }
        return new Animation<>(TOOL_FRAME_DURATION, frames);
    }

    // ---- Existing getters ----
    public Texture getHoeTexture(Tools.HoeStage stage) {
        switch (stage) {
            case COPPER: return hoeCopperTexture;
            case IRON: return hoeIronTexture;
            case GOLD: return hoeGoldTexture;
            case IRIDIUM: return hoeIridiumTexture;
            default: return hoeBeginnerTexture;
        }
    }
    public Texture getAxeTexture(Tools.AxeStage stage) {
        switch (stage) {
            case COPPER: return axeCopperTexture;
            case IRON: return axeIronTexture;
            case GOLD: return axeGoldTexture;
            case IRIDIUM: return axeIridiumTexture;
            default: return axeBeginnerTexture;
        }
    }
    public Texture getPickaxeTexture(Tools.PickaxeStage stage) {
        switch (stage) {
            case COPPER: return pickaxeCopperTexture;
            case IRON: return pickaxeIronTexture;
            case GOLD: return pickaxeGoldTexture;
            case IRIDIUM: return pickaxeIridiumTexture;
            default: return pickaxeBeginnerTexture;
        }
    }
    public Texture getWateringCanTexture() { return wateringCanTexture; }
    public Texture getFishingPoleTexture() { return fishingPoleTexture; }
    public Texture getScytheTexture() { return scytheTexture; }
    public Texture getMilkPailTexture() { return milkPailTexture; }
    public Texture getShearsTexture() { return shearsTexture; }

    public Texture getToolTexture(Tools tool) {
        String toolName = tool.getName();
        if ("Hoe".equals(toolName)) return getHoeTexture(tool.getHoeStage());
        if ("Axe".equals(toolName)) return getAxeTexture(tool.getAxeStage());
        if ("Pickaxe".equals(toolName)) return getPickaxeTexture(tool.getPickaxeStage());
        if ("Watering_Can".equals(toolName) || "Watering Can".equals(toolName) || "WateringCan".equals(toolName)) return getWateringCanTexture();
        if ("fishingpole".equalsIgnoreCase(toolName) || "FishingPole".equalsIgnoreCase(toolName)) return getFishingPoleTexture();
        if ("Scythe".equals(toolName)) return getScytheTexture();
        if ("Milk_Pail".equals(toolName)) return getMilkPailTexture();
        if ("Shears".equals(toolName)) return getShearsTexture();
        return hoeBeginnerTexture;
    }

    // ---- Old swing support (still here if needed) ----
    public void startToolSwing(Tools tool) {
        if (!isSwinging) {
            String name = tool.getName();
            if ("Hoe".equals(name) || "Axe".equals(name) || "Pickaxe".equals(name) || "Scythe".equals(name)) {
                isSwinging = true;
                swingTime = 0f;
                currentToolType = name;
                if ("Hoe".equals(name)) currentHoeStage = tool.getHoeStage();
                else if ("Axe".equals(name)) currentAxeStage = tool.getAxeStage();
                else if ("Pickaxe".equals(name)) currentPickaxeStage = tool.getPickaxeStage();
            }
        }
    }

    public void updateSwing(float delta) {
        if (isSwinging) {
            swingTime += delta;
            if (swingTime >= SWING_DURATION) {
                isSwinging = false;
                swingTime = 0f;
            }
        }
    }

    public boolean isSwinging() { return isSwinging; }

    public float getSwingAngle() {
        if (!isSwinging) return -90f;
        float progress = swingTime / SWING_DURATION;
        return 45f - 135f * progress;
    }

    public float[] getToolRotationOrigin() {
        return new float[]{0.5f, 0.25f};
    }

    public Texture getCurrentToolTexture() {
        switch (currentToolType) {
            case "Hoe": return getHoeTexture(currentHoeStage);
            case "Axe": return getAxeTexture(currentAxeStage);
            case "Pickaxe": return getPickaxeTexture(currentPickaxeStage);
            case "Watering_Can":
            case "Watering Can":
            case "WateringCan": return getWateringCanTexture();
            case "fishingpole":
            case "FishingPole": return getFishingPoleTexture();
            case "Scythe": return getScytheTexture();
            case "Milk_Pail": return getMilkPailTexture();
            case "Shears": return getShearsTexture();
            default: return hoeBeginnerTexture;
        }
    }

    // ---- New tool-use animation API ----
    public void startToolUse(int directionCode) {
        // directionCode is consumed only indirectly via getToolUseFrame parameter
        usingTool = true;
        toolUseTime = 0f;
        // Stop old overlay to avoid duplicate rendering
        isSwinging = false;
    }

    public void updateToolUse(float delta) {
        if (usingTool) {
            toolUseTime += delta;
            if (toolUseTime >= toolUseTotal) {
                usingTool = false;
                toolUseTime = 0f;
            }
        }
    }

    public boolean isUsingTool() {
        return usingTool;
    }

    public TextureRegion getToolUseFrame(int directionCode) {
        // directionCode mapping: 0=down,1=right,2=up,3=left
        Animation<TextureRegion> anim;
        switch (directionCode) {
            case 1: anim = animRight; break;
            case 2: anim = animUp;    break;
            case 3: anim = animLeft;  break;
            default: anim = animDown;
        }
        return anim.getKeyFrame(toolUseTime, false);
    }

    public void updateAll(float delta) {
        updateSwing(delta);
        updateToolUse(delta);
    }

    public void dispose() {
        // Dispose previous textures
        if (hoeBeginnerTexture != null) hoeBeginnerTexture.dispose();
        if (hoeCopperTexture != null) hoeCopperTexture.dispose();
        if (hoeIronTexture != null) hoeIronTexture.dispose();
        if (hoeGoldTexture != null) hoeGoldTexture.dispose();
        if (hoeIridiumTexture != null) hoeIridiumTexture.dispose();

        if (axeBeginnerTexture != null) axeBeginnerTexture.dispose();
        if (axeCopperTexture != null) axeCopperTexture.dispose();
        if (axeIronTexture != null) axeIronTexture.dispose();
        if (axeGoldTexture != null) axeGoldTexture.dispose();
        if (axeIridiumTexture != null) axeIridiumTexture.dispose();

        if (pickaxeBeginnerTexture != null) pickaxeBeginnerTexture.dispose();
        if (pickaxeCopperTexture != null) pickaxeCopperTexture.dispose();
        if (pickaxeIronTexture != null) pickaxeIronTexture.dispose();
        if (pickaxeGoldTexture != null) pickaxeGoldTexture.dispose();
        if (pickaxeIridiumTexture != null) pickaxeIridiumTexture.dispose();

        if (wateringCanTexture != null) wateringCanTexture.dispose();
        if (fishingPoleTexture != null) fishingPoleTexture.dispose();
        if (scytheTexture != null) scytheTexture.dispose();
        if (milkPailTexture != null) milkPailTexture.dispose();
        if (shearsTexture != null) shearsTexture.dispose();

        if (toolDownSheet != null) toolDownSheet.dispose();
        if (toolRightSheet != null) toolRightSheet.dispose();
        if (toolLeftSheet != null) toolLeftSheet.dispose();
        if (toolUpSheet != null) toolUpSheet.dispose();
    }
}
