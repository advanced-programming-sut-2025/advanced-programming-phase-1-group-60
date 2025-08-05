package com.StardewValley.Network;

import com.StardewValley.models.User;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameStateManager {
    private static GameStateManager instance;
    private final ConcurrentHashMap<String, Integer> userFarmSelections = new ConcurrentHashMap<>();
    private final String DB_PATH = "core/src/main/java/com/StardewValley/Network/Database/FarmSelections.csv";

    private GameStateManager() {
        // Private constructor for singleton
    }

    public static synchronized GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    /**
     * Save a user's farm selection to the manager
     * @param username The username
     * @param farmIndex The selected farm index (0-3)
     */
    public void saveFarmSelection(String username, int farmIndex) {
        userFarmSelections.put(username, farmIndex);
        saveToDB(username, farmIndex);
        System.out.println("Farm selection saved: " + username + " -> Farm " + farmIndex);
    }

    /**
     * Get the farm index selected by a user
     * @param username The username
     * @return The farm index or 0 if not found
     */
    public int getFarmSelection(String username) {
        Integer farmIndex = userFarmSelections.getOrDefault(username, null);

        // If not in memory, try to load from DB
        if (farmIndex == null) {
            farmIndex = loadFromDB(username);
            if (farmIndex != null) {
                userFarmSelections.put(username, farmIndex);
            } else {
                // Default to farm 0 if nothing found
                farmIndex = 0;
            }
        }

        System.out.println("Retrieved farm selection: " + username + " -> Farm " + farmIndex);
        return farmIndex;
    }

    /**
     * Save farm selection to database
     */
    private void saveToDB(String username, int farmIndex) {
        try {
            File file = new File(DB_PATH);
            File directory = file.getParentFile();

            // Create directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Read existing entries
            ConcurrentHashMap<String, Integer> entries = new ConcurrentHashMap<>();
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split(",");
                        if (parts.length == 2) {
                            entries.put(parts[0], Integer.parseInt(parts[1]));
                        }
                    }
                }
                reader.close();
            }

            // Update or add new entry
            entries.put(username, farmIndex);

            // Write all entries back to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String user : entries.keySet()) {
                writer.write(user + "," + entries.get(user));
                writer.newLine();
            }
            writer.close();

            System.out.println("Saved to database: " + username + " -> Farm " + farmIndex);
        } catch (IOException e) {
            System.err.println("Error saving farm selection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load farm selection from database
     */
    public Integer loadFromDB(String username) {
        try {
            File file = new File(DB_PATH);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split(",");
                        if (parts.length == 2 && parts[0].equals(username)) {
                            reader.close();
                            return Integer.parseInt(parts[1]);
                        }
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Error loading farm selection: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Clear all stored farm selections
     */
    public void clearSelections() {
        userFarmSelections.clear();
        try {
            File file = new File(DB_PATH);
            if (file.exists()) {
                new FileWriter(file, false).close(); // Clear file contents
            }
        } catch (IOException e) {
            System.err.println("Error clearing farm selections: " + e.getMessage());
        }
    }
}
