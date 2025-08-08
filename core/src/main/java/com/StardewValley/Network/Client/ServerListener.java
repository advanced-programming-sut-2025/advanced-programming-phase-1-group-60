package com.StardewValley.Network.Client;

import com.StardewValley.Network.JsonUtil;
import com.StardewValley.Network.Message;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.exceptions.GameException;
import com.StardewValley.models.Lobby;
import com.StardewValley.models.User;
import com.StardewValley.view.InLobbyView;
import com.StardewValley.view.MapSelectionView;
import com.StardewValley.view.MapView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerListener implements Runnable {
    private final Socket socket;
    private final Game game;
    private final LobbyController lobbyController;
    private BufferedReader in;
    private final Gson gson = new Gson();

    public ServerListener(Socket socket, Game game, LobbyController lobbyController) {
        this.socket = socket;
        this.game = game;
        this.lobbyController = lobbyController;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                Message message = JsonUtil.fromJson(serverResponse);
                if (message != null) {
                    Gdx.app.postRunnable(() -> {
                        try {
                            handleMessage(message);
                        } catch (GameException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server: " + e.getMessage());
        } finally {
            ClientMain.isConnected = false;
        }
    }

    @SuppressWarnings("unchecked")
    private void handleMessage(Message message) throws GameException {
        switch (message.getAction()) {
            case CREATE_LOBBY_SUCCESS: {
                Object createdLobbyData = message.getPayload().get("lobby");
                String createdLobbyJson = gson.toJson(createdLobbyData);
                Lobby createdLobby = gson.fromJson(createdLobbyJson, Lobby.class);
                if (createdLobby != null) {
                    game.setScreen(new InLobbyView(game, lobbyController, createdLobby));
                }
                break;
            }
            case PROCEED_TO_MAP_SELECTION: {
                Object lobbyData = message.getPayload().get("lobby");
                String lobbyJson = gson.toJson(lobbyData);
                Lobby lobby = gson.fromJson(lobbyJson, Lobby.class);
                if (lobby != null) {
                    game.setScreen(new MapSelectionView(game, lobbyController, lobby));
                }
                break;
            }
            case MAP_SELECTION_UPDATE: {
                if (game.getScreen() instanceof MapSelectionView) {
                    Map<String, Double> rawSelections =
                        (Map<String, Double>) message.getPayload().get("selections");
                    Map<String, Integer> selections = new HashMap<>();
                    for (Map.Entry<String, Double> entry : rawSelections.entrySet()) {
                        selections.put(entry.getKey(), entry.getValue().intValue());
                    }
                    ((MapSelectionView) game.getScreen()).updateMapSelections(selections);
                }
                break;
            }
            case GAME_STARTED: {
                System.out.println("Server commanded to start the game!");

                // Get the map selections from the payload
                Map<String, Double> rawSelections = (Map<String, Double>) message.getPayload().get("mapSelections");
                Map<String, Integer> mapSelections = new HashMap<>();

                // Convert Double to Integer (GSON limitation)
                for (Map.Entry<String, Double> entry : rawSelections.entrySet()) {
                    mapSelections.put(entry.getKey(), entry.getValue().intValue());
                }

                // Create the game instance and start gameplay
                lobbyController.handleGameStart(mapSelections);

                break;
            }
            case LOBBY_STATE_UPDATE: {
                Object lobbyData = message.getPayload().get("lobby");
                String lobbyJson = gson.toJson(lobbyData);
                Lobby lobby = gson.fromJson(lobbyJson, Lobby.class);

                if (lobby != null) {
                    if (game.getScreen() instanceof InLobbyView) {
                        ((InLobbyView) game.getScreen()).updateLobbyState(lobby);
                    } else {
                        game.setScreen(new InLobbyView(game, lobbyController, lobby));
                    }
                }
                break;
            }
            case PLAYER_POSITION_UPDATE: {
                if (game.getScreen() instanceof MapView) {
                    Map<String, Object> playerData = (Map<String, Object>) message.getPayload().get("player");
                    String username = (String) playerData.get("username");
                    double x = (double) playerData.get("x");
                    double y = (double) playerData.get("y");
                    int direction = ((Double) playerData.get("direction")).intValue();
                    boolean moving = (boolean) playerData.get("moving");
                    boolean inVillage = (boolean) playerData.get("inVillage");
                    int farmIndex = ((Double) playerData.get("farmIndex")).intValue();

                    MapView mapView = (MapView) game.getScreen();
                    mapView.updateNetworkPlayerPosition(
                        username, (float)x, (float)y, direction, moving, inVillage, farmIndex);
                }
                break;
            }
            case SHOW_REACTION: {
                if (game.getScreen() instanceof MapView) {
                    Map<String, Object> reactionData = (Map<String, Object>) message.getPayload().get("reaction");
                    String username = (String) reactionData.get("username");
                    int emojiId = ((Double) reactionData.get("emojiId")).intValue();
                    String text = (String) reactionData.get("text");

                    MapView mapView = (MapView) game.getScreen();
                    mapView.showEmojiReaction(username, emojiId, text);
                }
                break;
            }
            case PLAYER_DATA_UPDATE: { // NEW: Handle PLAYER_DATA_UPDATE from server
                Map<String, Object> updatedUserData = (Map<String, Object>) message.getPayload().get("updatedUser");
                if (updatedUserData != null) {
                    String username = (String) updatedUserData.get("username");
                    double money = ((Double) updatedUserData.get("money"));
                    double completedQuestsCount = ((Double) updatedUserData.get("completedQuestsCount"));
                    double averageSkillLevel = ((Double) updatedUserData.get("averageSkillLevel"));

                    // Update the local Game model's User object
                    User userToUpdate = com.StardewValley.models.Game.getInstance().getUserByUsername(username);
                    if (userToUpdate != null) {
                        userToUpdate.setMoney((int) money);
                        // Ensure User class has setters for these if you want to update them directly
                        // userToUpdate.setCompletedQuestsCount((int) completedQuestsCount);
                        // userToUpdate.setAverageSkillLevel((float) averageSkillLevel);

                        // If you have a more complex User object, you might need to deserialize it fully
                        // User updatedUser = gson.fromJson(gson.toJson(updatedUserData), User.class);
                        // Game.getInstance().updateUser(updatedUser); // A method to replace/update user in Game

                        System.out.println("Client received PLAYER_DATA_UPDATE for " + username + ": Money=" + money);
                    }
                }
                break;
            }
            default: {
                System.out.println("Unhandled message from server: " + message.getAction());
                break;
            }
        }
    }
}
