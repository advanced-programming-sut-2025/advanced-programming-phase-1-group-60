package com.StardewValley.AssetsManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiManager implements Disposable {
    private static EmojiManager instance;
    private Map<Integer, Texture> emojiTextures;
    private Map<String, Object> userQuickEmojis; // Changed from List<Integer> to Object to handle both types
    private static final int TOTAL_EMOJI_COUNT = 59; // Emojis000.png to Emojis045.png
    private static final int MAX_QUICK_EMOJIS = 5;
    private static final String PREFS_FILE = "emoji_preferences.json";

    private EmojiManager() {
        emojiTextures = new HashMap<>();
        userQuickEmojis = new HashMap<>();
        loadEmojiTextures();
        loadUserPreferences();
    }

    public static EmojiManager getInstance() {
        if (instance == null) {
            instance = new EmojiManager();
        }
        return instance;
    }

    private void loadEmojiTextures() {
        for (int i = 0; i < TOTAL_EMOJI_COUNT; i++) {
            String filename = String.format("assets/Emoji/Emojis%03d.png", i);
            try {
                if (Gdx.files.internal(filename).exists()) {
                    emojiTextures.put(i, new Texture(Gdx.files.internal(filename)));
                    System.out.println("Loaded emoji texture: " + filename);
                } else {
                    System.out.println("Emoji texture file not found: " + filename);
                }
            } catch (Exception e) {
                System.err.println("Error loading emoji texture " + filename + ": " + e.getMessage());
            }
        }
    }

    private void loadUserPreferences() {
        try {
            if (Gdx.files.local(PREFS_FILE).exists()) {
                Json json = new Json();
                userQuickEmojis = json.fromJson(HashMap.class, Gdx.files.local(PREFS_FILE));
            }
        } catch (Exception e) {
            System.err.println("Error loading emoji preferences: " + e.getMessage());
            userQuickEmojis = new HashMap<>();
        }
    }

    public void saveUserPreferences() {
        try {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            FileWriter writer = new FileWriter(Gdx.files.local(PREFS_FILE).file());
            writer.write(json.toJson(userQuickEmojis));
            writer.close();
        } catch (Exception e) {
            System.err.println("Error saving emoji preferences: " + e.getMessage());
        }
    }

    public Texture getEmojiTexture(int id) {
        return emojiTextures.getOrDefault(id, null);
    }

    public List<Integer> getUserQuickEmojis(String username) {
        if (!userQuickEmojis.containsKey(username)) {
            // Set default quick emojis (first 5)
            List<Integer> defaultEmojis = new ArrayList<>();
            for (int i = 0; i < Math.min(MAX_QUICK_EMOJIS, TOTAL_EMOJI_COUNT); i++) {
                defaultEmojis.add(i);
            }
            userQuickEmojis.put(username, defaultEmojis);
            return defaultEmojis;
        }

        // Handle both List<Integer> and Array<Integer> types
        Object userEmojis = userQuickEmojis.get(username);

        if (userEmojis instanceof List) {
            return (List<Integer>) userEmojis;
        }
        else if (userEmojis instanceof Array) {
            // Convert LibGDX Array to Java List
            Array<Integer> gdxArray = (Array<Integer>) userEmojis;
            List<Integer> javaList = new ArrayList<>(gdxArray.size);

            for (int i = 0; i < gdxArray.size; i++) {
                javaList.add(gdxArray.get(i));
            }

            // Replace the Array with List for future use
            userQuickEmojis.put(username, javaList);
            return javaList;
        }

        // Fallback case - something went wrong, return default list
        List<Integer> defaultEmojis = new ArrayList<>();
        for (int i = 0; i < Math.min(MAX_QUICK_EMOJIS, TOTAL_EMOJI_COUNT); i++) {
            defaultEmojis.add(i);
        }
        userQuickEmojis.put(username, defaultEmojis);
        return defaultEmojis;
    }

    public void setUserQuickEmoji(String username, int position, int emojiId) {
        if (position < 0 || position >= MAX_QUICK_EMOJIS) return;

        List<Integer> userEmojis = getUserQuickEmojis(username);
        userEmojis.set(position, emojiId);
        saveUserPreferences();
    }

    public List<Integer> getAllEmojiIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < TOTAL_EMOJI_COUNT; i++) {
            if (emojiTextures.containsKey(i)) {
                ids.add(i);
            }
        }
        return ids;
    }

    @Override
    public void dispose() {
        for (Texture texture : emojiTextures.values()) {
            texture.dispose();
        }
    }
}
