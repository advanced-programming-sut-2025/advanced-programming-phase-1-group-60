package com.StardewValley.controller;

import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.Network.LobbyManager;
import com.StardewValley.Network.Message;
import com.StardewValley.Network.SessionManager;
import com.StardewValley.exceptions.GameException;
import com.StardewValley.models.Lobby;
import com.StardewValley.models.User;
import com.StardewValley.models.Tools;
import com.StardewValley.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyController {
    private final com.badlogic.gdx.Game game;
    private final LoginMenuController loginController;
    private final LobbyManager lobbyManager;

    public LobbyController(com.badlogic.gdx.Game game, LoginMenuController loginController) {
        this.game = game;
        this.loginController = loginController;
        this.lobbyManager = LobbyManager.getInstance();
    }

    public void startGame(String lobbyId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("lobbyId", lobbyId);
        Message message = new Message(Message.ActionType.START_GAME, payload);
        ClientMain.sendMessage(message);
    }

    public List<String> getOnlinePlayers() {
        return new ArrayList<>(SessionManager.getInstance().getLoggedInUsers());
    }

    public List<Lobby> getLobbies() {
        // FIX: Always reload lobbies from file to see new ones created by other clients
        // This is a temporary fix for the file-based approach.
        return lobbyManager.getLobbies();
    }

    public User getUserByUsername(String username) {
        return UserRepository.getInstance().getUserByUsername(username);
    }

    public LoginMenuController getLoginController() {
        return loginController;
    }

    public Lobby getLobbyForUser(String username) {
        // FIX: Reload lobbies to get the most up-to-date state
        for (Lobby lobby : getLobbies()) {
            if (lobby.getMembers().contains(username)) {
                return lobby;
            }
        }
        return null;
    }

    public String getCurrentUsername() {
        return loginController.getLoggedInUser().getUsername();
    }

    public void createLobby(String name, boolean isPublic, String password, boolean isVisible, int capacity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("isPublic", isPublic);
        payload.put("password", password);
        payload.put("isVisible", isVisible);
        payload.put("capacity", capacity);

        Message message = new Message(Message.ActionType.CREATE_LOBBY, payload);
        ClientMain.sendMessage(message);
    }

    // REFACTORED: This now sends a message to the server
    public void joinLobby(String lobbyId, String password, String username) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("lobbyId", lobbyId);
        payload.put("password", password);
        // username is already known by the server's ClientHandler

        Message message = new Message(Message.ActionType.JOIN_LOBBY, payload);
        ClientMain.sendMessage(message);
    }

    // REFACTORED: This now sends a message to the server
    public void leaveLobby(String lobbyId, String username) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("lobbyId", lobbyId);

        Message message = new Message(Message.ActionType.LEAVE_LOBBY, payload);
        ClientMain.sendMessage(message);
    }

    public void kickPlayer(String lobbyId, String adminUsername, String playerToKick) {
        // This should also be a message to the server
        Map<String, Object> payload = new HashMap<>();
        payload.put("lobbyId", lobbyId);
        payload.put("playerToKick", playerToKick);

        Message message = new Message(Message.ActionType.KICK_PLAYER, payload);
        ClientMain.sendMessage(message);
    }
    public void handleGameStart(Map<String, Integer> mapSelections) throws GameException {
        System.out.println("Starting game with map selections: " + mapSelections);

        // Get current username
        String currentUsername = loginController.getLoggedInUser().getUsername();

        // Find the current user's selected map
        Integer selectedMap = mapSelections.get(currentUsername);
        if (selectedMap == null) {
            System.err.println("Error: No map selection found for current user!");
            selectedMap = 0; // Default to first map as fallback
        }

        // Create a new Game instance with all players
        com.StardewValley.models.Game gameInstance = com.StardewValley.models.Game.resetInstance();

        // Get all usernames from the map selections
        List<String> otherPlayers = new ArrayList<>(mapSelections.keySet());
        otherPlayers.remove(currentUsername); // Remove current player

        // Create the game with all players
        gameInstance.newGame(loginController.getLoggedInUser(), otherPlayers);

        // Add starter tools to all players
        for (User user : gameInstance.getPlayers()) {
            Tools.addBeginnerHoeToInventory(user.getInventory());
            Tools.addBeginnerPickaxeToInventory(user.getInventory());
            Tools.addBeginnerAxeToInventory(user.getInventory());
            Tools.addBeginnerWateringcanToInventory(user.getInventory());
            Tools.addBeginnerScytheToInventory(user.getInventory());
        }

        // Initialize quests
        com.StardewValley.repository.QuestRepository.getInstance().initialize();

        // Assign map selections to each player
        for (User player : gameInstance.getPlayers()) {
            int mapId = mapSelections.getOrDefault(player.getUsername(), 1);
            gameInstance.selectMap(player, mapId);
            System.out.println("Assigned " + player.getUsername() + " to map " + mapId);
        }

        // Initialize the game map
        gameInstance.initializeGameMap();
        gameInstance.setState(com.StardewValley.models.Game.GameState.IN_GAME);

        // Create a new GameView with the initialized game
        com.StardewValley.view.GameView gameView = new com.StardewValley.view.GameView(game, loginController, null);

        // Save map selection for persistence
        com.StardewValley.Network.GameStateManager.getInstance().saveFarmSelection(currentUsername, selectedMap - 1);

        // Set the game screen
        gameView.setMapView(new com.StardewValley.view.MapView(gameInstance.getCurrentMap(), gameView::showMainMenu, gameView));
        gameView.showGameplayScreen();
    }
    public void onBack() {
        game.setScreen(new com.StardewValley.view.MainView(game, loginController));
    }
}
