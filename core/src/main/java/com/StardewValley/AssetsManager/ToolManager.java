package com.StardewValley.AssetsManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
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

    // Tool swing animation
    private boolean isSwinging = false;
    private float swingTime = 0f;
    private static final float SWING_DURATION = 0.6f; // Total swing time
    private Tools.HoeStage currentHoeStage = Tools.HoeStage.BEGINNER;
    private Tools.AxeStage currentAxeStage = Tools.AxeStage.BEGINNER;
    private Tools.PickaxeStage currentPickaxeStage = Tools.PickaxeStage.BEGINNER;
    private String currentToolType = "Hoe";

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
        // Load hoe textures for each stage
        hoeBeginnerTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Hoe.png"));
        hoeCopperTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Copper_Hoe.png"));
        hoeIronTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Steel_Hoe.png"));
        hoeGoldTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Gold_Hoe.png"));
        hoeIridiumTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Iridium_Hoe.png"));

        // Load axe textures for each stage
        axeBeginnerTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Axe.png"));
        axeCopperTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Copper_Axe.png"));
        axeIronTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Steel_Axe.png"));
        axeGoldTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Gold_Axe.png"));
        axeIridiumTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Iridium_Axe.png"));

        // Load pickaxe textures for each stage
        pickaxeBeginnerTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Pickaxe.png"));
        pickaxeCopperTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Copper_Pickaxe.png"));
        pickaxeIronTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Steel_Pickaxe.png"));
        pickaxeGoldTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Gold_Pickaxe.png"));
        pickaxeIridiumTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Iridium_Pickaxe.png"));

        // Load additional tools textures - using correct names from Tools.java
        wateringCanTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Watering_Can.png"));
        fishingPoleTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Training_Rod.png"));
        scytheTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Scythe.png"));
        milkPailTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Milk_Pail.png"));
        shearsTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Shears.png"));
    }

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

    public Texture getWateringCanTexture() {
        return wateringCanTexture;
    }

    public Texture getFishingPoleTexture() {
        return fishingPoleTexture;
    }

    public Texture getScytheTexture() {
        return scytheTexture;
    }

    public Texture getMilkPailTexture() {
        return milkPailTexture;
    }

    public Texture getShearsTexture() {
        return shearsTexture;
    }

    public Texture getToolTexture(Tools tool) {
        String toolName = tool.getName();

        if ("Hoe".equals(toolName)) {
            return getHoeTexture(tool.getHoeStage());
        } else if ("Axe".equals(toolName)) {
            return getAxeTexture(tool.getAxeStage());
        } else if ("Pickaxe".equals(toolName)) {
            return getPickaxeTexture(tool.getPickaxeStage());
        } else if ("Watering_Can".equals(toolName)) {
            return getWateringCanTexture();
        } else if ("fishingpole".equals(toolName)) {
            return getFishingPoleTexture();
        } else if ("Scythe".equals(toolName)) {
            return getScytheTexture();
        } else if ("Milk_Pail".equals(toolName)) {
            return getMilkPailTexture();
        } else if ("Shears".equals(toolName)) {
            return getShearsTexture();
        }

        // For other tools, fallback to their path
        return hoeBeginnerTexture; // Default fallback for now
    }

    public void startToolSwing(Tools tool) {
        if (!isSwinging) {
            String toolName = tool.getName();
            // Only animate specific tools
            if ("Hoe".equals(toolName) || "Axe".equals(toolName) ||
                "Pickaxe".equals(toolName) || "Scythe".equals(toolName)) {

                isSwinging = true;
                swingTime = 0f;
                currentToolType = toolName;

                if ("Hoe".equals(toolName)) {
                    currentHoeStage = tool.getHoeStage();
                } else if ("Axe".equals(toolName)) {
                    currentAxeStage = tool.getAxeStage();
                } else if ("Pickaxe".equals(toolName)) {
                    currentPickaxeStage = tool.getPickaxeStage();
                }
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

    public boolean isSwinging() {
        return isSwinging;
    }

    public float getSwingAngle() {
        if (!isSwinging) return -90f; // At rest, tool points upward (-90 means tip is up)

        // Calculate swing progress (0 to 1)
        float progress = swingTime / SWING_DURATION;

        // FIXED: Reverse the animation direction by reversing the progress
        // Instead of going from -90 to +45, we'll go from +45 back to -90
        // This makes the animation appear to move in the correct direction
        return +45f - (135f * progress);
    }

    // This method helps MapView determine where to position the origin of rotation
    public float[] getToolRotationOrigin() {
        float[] origin = new float[2]; // [originX, originY] as a percentage of tool size

        // Position the origin near the bottom of the tool (the handle)
        origin[0] = 0.5f;   // X center
        origin[1] = 0.25f;  // Y at 25% from bottom

        return origin;
    }

    public Texture getCurrentToolTexture() {
        if ("Hoe".equals(currentToolType)) {
            return getHoeTexture(currentHoeStage);
        } else if ("Axe".equals(currentToolType)) {
            return getAxeTexture(currentAxeStage);
        } else if ("Pickaxe".equals(currentToolType)) {
            return getPickaxeTexture(currentPickaxeStage);
        } else if ("Watering_Can".equals(currentToolType)) {
            return getWateringCanTexture();
        } else if ("fishingpole".equals(currentToolType)) {
            return getFishingPoleTexture();
        } else if ("Scythe".equals(currentToolType)) {
            return getScytheTexture();
        } else if ("Milk_Pail".equals(currentToolType)) {
            return getMilkPailTexture();
        } else if ("Shears".equals(currentToolType)) {
            return getShearsTexture();
        }

        return hoeBeginnerTexture; // Default fallback
    }

    public void dispose() {
        // Dispose hoe textures
        if (hoeBeginnerTexture != null) hoeBeginnerTexture.dispose();
        if (hoeCopperTexture != null) hoeCopperTexture.dispose();
        if (hoeIronTexture != null) hoeIronTexture.dispose();
        if (hoeGoldTexture != null) hoeGoldTexture.dispose();
        if (hoeIridiumTexture != null) hoeIridiumTexture.dispose();

        // Dispose axe textures
        if (axeBeginnerTexture != null) axeBeginnerTexture.dispose();
        if (axeCopperTexture != null) axeCopperTexture.dispose();
        if (axeIronTexture != null) axeIronTexture.dispose();
        if (axeGoldTexture != null) axeGoldTexture.dispose();
        if (axeIridiumTexture != null) axeIridiumTexture.dispose();

        // Dispose pickaxe textures
        if (pickaxeBeginnerTexture != null) pickaxeBeginnerTexture.dispose();
        if (pickaxeCopperTexture != null) pickaxeCopperTexture.dispose();
        if (pickaxeIronTexture != null) pickaxeIronTexture.dispose();
        if (pickaxeGoldTexture != null) pickaxeGoldTexture.dispose();
        if (pickaxeIridiumTexture != null) pickaxeIridiumTexture.dispose();

        // Dispose additional tools textures
        if (wateringCanTexture != null) wateringCanTexture.dispose();
        if (fishingPoleTexture != null) fishingPoleTexture.dispose();
        if (scytheTexture != null) scytheTexture.dispose();
        if (milkPailTexture != null) milkPailTexture.dispose();
        if (shearsTexture != null) shearsTexture.dispose();
    }
}
