package com.StardewValley.controller;

import com.StardewValley.Network.LobbyManager;
import com.StardewValley.Network.SessionManager;
import com.StardewValley.models.Lobby;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyController {
    private final com.badlogic.gdx.Game game;
    private final LoginMenuController loginController;

    public LobbyController(com.badlogic.gdx.Game game, LoginMenuController loginController) {
        this.game = game;
        this.loginController = loginController;
    }

    public List<String> getOnlinePlayers() {
        return new ArrayList<>(SessionManager.getInstance().getLoggedInUsers());
    }

    public List<Lobby> getLobbies() {
        return LobbyManager.getInstance().getLobbies();
    }

    public void createLobby(String name, boolean isPublic, String password, boolean isVisible) {
        String creator = loginController.getLoggedInUser().getUsername();
        Lobby lobby = new Lobby(name, isPublic, password, isVisible, creator);
        LobbyManager.getInstance().addLobby(lobby);
        game.setScreen(new com.StardewValley.view.GameView(game, loginController, lobby));
    }

    public void onCreateLobby() {
        // This will be handled by the view (show dialog)
    }

    public void onJoinLobby() {
        // To be implemented
    }

    public void onRefreshLobbies() {
        // To be implemented
    }
    public String getCurrentUsername() {
        return loginController.getLoggedInUser().getUsername();
    }
    public Lobby getLobbyById(String id) {
        for (Lobby lobby : LobbyManager.getInstance().getLobbies()) {
            if (lobby.getId().equals(id)) return lobby;
        }
        return null;
    }
    public void leaveLobby(Lobby lobby, String username) {
        if (lobby != null && lobby.getMembers().contains(username)) {
            lobby.removeMember(username);
            LobbyManager.getInstance().saveLobbies();
        }
    }
    public void joinLobby(Lobby lobby, String password, String username) {
        if (!lobby.isPublic() && (lobby.getPassword() == null || !lobby.getPassword().equals(password))) {
            return; // Password incorrect, handle in view
        }
        if (!lobby.getMembers().contains(username)) {
            lobby.addMember(username);
            LobbyManager.getInstance().saveLobbies();
        }
        // Optionally, update UI or move to lobby screen
    }
    public void onBack() {
        game.setScreen(new com.StardewValley.view.MainView(game, loginController));
    }
}
