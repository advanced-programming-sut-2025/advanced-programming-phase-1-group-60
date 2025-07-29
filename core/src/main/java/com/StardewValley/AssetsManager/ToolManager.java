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

    // Tool swing animation
    private boolean isSwinging = false;
    private float swingTime = 0f;
    private static final float SWING_DURATION = 0.6f; // Total swing time
    private Tools.HoeStage currentHoeStage = Tools.HoeStage.BEGINNER;
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

    public Texture getToolTexture(Tools tool) {
        String toolName = tool.getName();

        if ("Hoe".equals(toolName)) {
            return getHoeTexture(tool.getHoeStage());
        }

        // For other tools, fallback to their path
        return hoeBeginnerTexture; // Default fallback for now
    }

    public void startToolSwing(Tools tool) {
        if (!isSwinging) {
            isSwinging = true;
            swingTime = 0f;
            currentToolType = tool.getName();

            if ("Hoe".equals(tool.getName())) {
                currentHoeStage = tool.getHoeStage();
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
        if (!isSwinging) return 0f;

        // Calculate swing progress (0 to 1)
        float progress = swingTime / SWING_DURATION;

        // Create smooth swing motion from 90 degrees (top) to 180 degrees (down)
        // Using sine wave for smooth acceleration/deceleration
        float angle = 90f + (90f * MathUtils.sin(progress * MathUtils.PI));
        return angle;
    }

    public Texture getCurrentToolTexture() {
        if ("Hoe".equals(currentToolType)) {
            return getHoeTexture(currentHoeStage);
        }
        return hoeBeginnerTexture; // Default fallback
    }

    public void dispose() {
        if (hoeBeginnerTexture != null) hoeBeginnerTexture.dispose();
        if (hoeCopperTexture != null) hoeCopperTexture.dispose();
        if (hoeIronTexture != null) hoeIronTexture.dispose();
        if (hoeGoldTexture != null) hoeGoldTexture.dispose();
        if (hoeIridiumTexture != null) hoeIridiumTexture.dispose();
    }
}
