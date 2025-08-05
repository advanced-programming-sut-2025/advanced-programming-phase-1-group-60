// core/src/main/java/com/StardewValley/Network/SessionManager.java
package com.StardewValley.Network;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionManager {
    private static SessionManager instance;
    private final Map<String, String> loggedInUsers = new HashMap<>();
    private static final String DB_PATH = "core/src/main/java/com/StardewValley/Network/Database/LoggedInUsers.csv";

    private SessionManager() {
        loadLoggedInUsers();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public synchronized boolean isLoggedIn(String username) {
        loadLoggedInUsers();
        return loggedInUsers.containsKey(username);
    }

    public synchronized String getClientForUser(String username) {
        loadLoggedInUsers();
        return loggedInUsers.get(username);
    }

    public synchronized void login(String username, String clientId) {
        loadLoggedInUsers(); // Ensure latest state
        loggedInUsers.put(username, clientId);
        saveLoggedInUsers();
    }

    public synchronized void logout(String username) {
        loadLoggedInUsers(); // Ensure latest state
        loggedInUsers.remove(username);
        saveLoggedInUsers();
    }

    public synchronized void clearAllSessions() {
        loggedInUsers.clear();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH, false))) {
            // Writing to a file with 'append' set to false will truncate it.
            bw.write("");
        } catch (IOException e) {
            System.err.println("Could not clear session file: " + e.getMessage());
        }
    }

    private void loadLoggedInUsers() {
        loggedInUsers.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    loggedInUsers.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException ignored) {}
    }
    public List<String> getLoggedInUsers() {
        loadLoggedInUsers();
        return new ArrayList<>(loggedInUsers.keySet());
    }
    private void saveLoggedInUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DB_PATH, false))) {
            for (Map.Entry<String, String> entry : loggedInUsers.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }
}
