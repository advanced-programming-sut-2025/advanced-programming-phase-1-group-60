package com.StardewValley.AssetsManager;

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
    private Texture chatIconTexture; // آیکون جدید برای چت

    // Stone textures
    private Texture[] stoneTiles;

    // NPC and Store textures
    private Map<String, Texture> npcTextures;
    private Texture storeTexture;


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

    private void loadTextures() {
        grassTile = new Texture(Gdx.files.internal("Map/Floor/Grass.png"));
        placeholderTile = new Texture(Gdx.files.internal("placeholder.png"));
        chatIconTexture = new Texture(Gdx.files.internal("assets/Village/chat_icon.png"));

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
    }

    public Texture getGrassTile() {
        return grassTile;
    }

    public Texture getChatIconTexture() { return chatIconTexture; }

    public Texture getStoneTile(int variant) {
        // Ensure variant is between 1-8
        int index = Math.min(Math.max(variant, 1), 8) - 1;
        return stoneTiles[index];
    }

    public Texture getPlaceholderTile() {
        return placeholderTile;
    }

    // Get a random stone texture
    public Texture getRandomStoneTile() {
        return stoneTiles[random.nextInt(stoneTiles.length)];
    }

    public Texture getNpcTexture(String npcName) {
        return npcTextures.get(npcName.toLowerCase());
    }

    public Texture getStoreTexture() {
        return storeTexture;
    }

    public void dispose() {
        grassTile.dispose();
        placeholderTile.dispose();
        chatIconTexture.dispose();

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
    }
}
