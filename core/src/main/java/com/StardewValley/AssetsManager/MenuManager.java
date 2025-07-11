package com.StardewValley.AssetsManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MenuManager {
    private static MenuManager instance;
    private AssetManager assetManager;
    private Skin pixthulhuSkin;
    private Texture backgroundLayer;
    private Texture middlegroundLayer;
    private float[] backgroundPositions;
    private final float BACKGROUND_SCROLL_SPEED = 10f;

    private MenuManager() {
        assetManager = new AssetManager();
        loadAssets();
        backgroundPositions = new float[2];
        backgroundPositions[0] = 0;
        backgroundPositions[1] = Gdx.graphics.getWidth();
    }

    public static MenuManager getInstance() {
        if (instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }

    private void loadAssets() {
        pixthulhuSkin = new Skin(Gdx.files.internal("assets/Pixthulhu/skin/pixthulhu-ui.json"));
        backgroundLayer = new Texture(Gdx.files.internal("assets/Background/layers/background.png"));
        middlegroundLayer = new Texture(Gdx.files.internal("assets/Background/layers/middleground.png"));
    }

    public Skin getPixthulhuSkin() {
        return pixthulhuSkin;
    }

    public void updateBackgroundAnimation(float delta) {
        for (int i = 0; i < backgroundPositions.length; i++) {
            // Move each background position left
            backgroundPositions[i] -= BACKGROUND_SCROLL_SPEED * delta;

            // If a background has moved completely off screen to the left
            if (backgroundPositions[i] <= -Gdx.graphics.getWidth()) {
                // Move it to the right of the other background
                float otherPos = backgroundPositions[(i + 1) % 2];
                backgroundPositions[i] = otherPos + Gdx.graphics.getWidth();
            }
        }
    }

    public float[] getBackgroundPositions() {
        return backgroundPositions;
    }

    public Texture getBackgroundLayer() {
        return backgroundLayer;
    }

    public Texture getMiddlegroundLayer() {
        return middlegroundLayer;
    }

    public void dispose() {
        if (pixthulhuSkin != null) pixthulhuSkin.dispose();
        if (backgroundLayer != null) backgroundLayer.dispose();
        if (middlegroundLayer != null) middlegroundLayer.dispose();
        assetManager.dispose();
    }
}
