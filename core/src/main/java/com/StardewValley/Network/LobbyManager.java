package com.StardewValley.Network;

import com.StardewValley.models.Lobby;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LobbyManager {
    private static LobbyManager instance;
    private final List<Lobby> lobbies = new ArrayList<>();
    private final String DB_PATH = "core/src/main/java/com/StardewValley/Network/Database/Lobbies.csv";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private LobbyManager() {
        loadLobbies();
    }

    public static synchronized LobbyManager getInstance() {
        if (instance == null) {
            instance = new LobbyManager();
        }
        return instance;
    }

    public synchronized void addLobby(Lobby lobby) {
        lobbies.add(lobby);
        saveLobbies();
    }

    public synchronized List<Lobby> getLobbies() {
        loadLobbies(); // Always get the freshest data
        return new ArrayList<>(lobbies);
    }

    public synchronized Optional<Lobby> getLobbyById(String lobbyId) {
        return lobbies.stream().filter(l -> l.getId().equals(lobbyId)).findFirst();
    }

    public synchronized void updateLobby(Lobby updatedLobby) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbies.get(i).getId().equals(updatedLobby.getId())) {
                lobbies.set(i, updatedLobby);
                saveLobbies();
                return;
            }
        }
    }

    public synchronized void handlePlayerLeave(String lobbyId, String username) {
        Optional<Lobby> lobbyOpt = getLobbyById(lobbyId);
        if (lobbyOpt.isPresent()) {
            Lobby lobby = lobbyOpt.get();
            // FIX: This now correctly checks if the lobby is empty after removal
            boolean shouldClose = lobby.removeMember(username);

            if (shouldClose) {
                lobbies.remove(lobby);
                System.out.println("Lobby " + lobby.getName() + " closed because it is now empty.");
            }
            saveLobbies();
        }
    }

    private void loadLobbies() {
        lobbies.clear();
        File file = new File(DB_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lobbies.add(Lobby.fromCSV(line));
                }
            }
        } catch (IOException ignored) {}
    }

    public synchronized void saveLobbies() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DB_PATH))) {
            for (Lobby l : lobbies) {
                pw.println(l.toCSV());
            }
        } catch (IOException ignored) {}
    }
}
