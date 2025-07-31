package com.StardewValley.Network;

import com.StardewValley.models.Lobby;
import java.io.*;
import java.util.*;

public class LobbyManager {
    private static LobbyManager instance;
    private final List<Lobby> lobbies = new ArrayList<>();
    private final String DB_PATH = "core/src/main/java/com/StardewValley/Network/Database/Lobbies.csv";

    private LobbyManager() {
        loadLobbies();
    }

    public static LobbyManager getInstance() {
        if (instance == null) instance = new LobbyManager();
        return instance;
    }

    public synchronized void addLobby(Lobby lobby) {
        lobbies.add(lobby);
        saveLobbies();
    }

    public synchronized List<Lobby> getLobbies() {
        loadLobbies();
        return new ArrayList<>(lobbies);
    }
    public synchronized void removeLobby(Lobby lobby) {
        lobbies.removeIf(l -> l.getId().equals(lobby.getId()));
        saveLobbies();
    }
    public synchronized void removeMemberFromLobby(String lobbyId, String username) {
        Lobby lobby = lobbies.stream().filter(l -> l.getId().equals(lobbyId)).findFirst().orElse(null);
        if (lobby != null) {
            lobby.getMembers().remove(username);
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
                lobbies.add(Lobby.fromCSV(line));
            }
        } catch (IOException ignored) {}
    }

    public void saveLobbies() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DB_PATH))) {
            for (Lobby l : lobbies) pw.println(l.toCSV());
        } catch (IOException ignored) {}
    }
}
