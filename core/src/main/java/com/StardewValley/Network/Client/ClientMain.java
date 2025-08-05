package com.StardewValley.Network.Client;

import com.StardewValley.Network.GameStateManager;
import com.StardewValley.Network.Message.GameMessage;
import com.StardewValley.Network.Message.GameMessage.MessageType;
import com.StardewValley.models.Game;
import com.StardewValley.models.User;
import com.StardewValley.repository.UserRepository;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ClientMain {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    public static boolean isConnected = false;

    private static Socket clientSocket;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;
    private static String username;
    private static String instanceId = UUID.randomUUID().toString();
    private static Consumer<GameMessage> messageHandler;
    private static ExecutorService listenerThread = Executors.newSingleThreadExecutor();

    // Store remote player information
    private static final Map<String, RemotePlayerInfo> remotePlayers = new HashMap<>();

    public static class RemotePlayerInfo {
        private String username;
        private String instanceId;
        private float x;
        private float y;
        private String location;
        private int farmIndex;
        private User userObject;
        private int currentEmojiId = -1;
        private String currentReactionText = null;
        private long reactionTimestamp = 0;
        private static final long REACTION_DISPLAY_TIME = 5000;
        public RemotePlayerInfo(String username, String instanceId, float x, float y, String location, int farmIndex) {
            this.username = username;
            this.instanceId = instanceId;
            this.x = x;
            this.y = y;
            this.location = location;
            this.farmIndex = farmIndex;

            // Try to get or create User object
            this.userObject = UserRepository.getInstance().getUserByUsername(username);
        }
        public void setCurrentReaction(int emojiId, String text, long timestamp) {
            this.currentEmojiId = emojiId;
            this.currentReactionText = text;
            this.reactionTimestamp = timestamp;
        }

        public boolean hasActiveReaction() {
            return currentEmojiId != -1 &&
                (System.currentTimeMillis() - reactionTimestamp) < REACTION_DISPLAY_TIME;
        }

        public int getCurrentEmojiId() {
            return currentEmojiId;
        }

        public String getCurrentReactionText() {
            return currentReactionText;
        }

        public void clearReaction() {
            currentEmojiId = -1;
            currentReactionText = null;
        }
        // Getters
        public String getUsername() { return username; }
        public String getInstanceId() { return instanceId; }
        public float getX() { return x; }
        public float getY() { return y; }
        public String getLocation() { return location; }
        public int getFarmIndex() { return farmIndex; }
        public User getUserObject() { return userObject; }

        // Setters
        public void setPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public static String getInstanceId() {
        return instanceId;
    }

    // Get all remote players
    public static Map<String, RemotePlayerInfo> getRemotePlayers() {
        return remotePlayers;
    }

    public static void connectToServer(String userName) {
        if (!isConnected) {
            try {
                // Store username
                username = userName;

                // Setup socket connection
                clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
                objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

                // Generate a unique instance ID for this client
                instanceId = UUID.randomUUID().toString();
                System.out.println("Connected to server with instance ID: " + instanceId);

                // Start listening for messages
                isConnected = true;
                startListening();

                // Get farm index from GameStateManager (this is stored in the database)
                int farmIndex = 0;
                Integer savedFarmIndex = GameStateManager.getInstance().loadFromDB(username);
                if (savedFarmIndex != null) {
                    farmIndex = savedFarmIndex;
                    System.out.println("Loaded farm index from DB: " + farmIndex);
                }

                // Announce this player to others
                Map<String, Object> joinData = new HashMap<>();
                joinData.put("farmIndex", farmIndex);

                GameMessage joinMessage = new GameMessage(
                    MessageType.PLAYER_JOIN,
                    username,
                    instanceId
                );

                sendMessage(joinMessage);
                System.out.println("Sent join message for player: " + username + " (Farm " + (farmIndex + 1) + ")");

            } catch (IOException e) {
                System.err.println("Failed to connect to server: " + e.getMessage());
                isConnected = false;
            }
        }
    }

    public static void setMessageHandler(Consumer<GameMessage> handler) {
        messageHandler = handler;
    }
    public static void sendPlayerReaction(int emojiId, String customText) {
        if (!isConnected) return;

        GameMessage reactionMessage = new GameMessage(MessageType.PLAYER_REACTION, username, instanceId);
        reactionMessage.addData("emojiId", emojiId);

        if (customText != null && !customText.isEmpty()) {
            // Ensure text is no longer than 10 characters
            if (customText.length() > 10) {
                customText = customText.substring(0, 10);
            }
            reactionMessage.addData("text", customText);
        }

        reactionMessage.addData("timestamp", System.currentTimeMillis());

        sendMessage(reactionMessage);
    }
    private static void handlePlayerReaction(GameMessage message) {
        String playerInstanceId = message.getInstanceId();

        // Don't process our own messages
        if (playerInstanceId.equals(instanceId)) return;

        RemotePlayerInfo playerInfo = remotePlayers.get(playerInstanceId);
        if (playerInfo != null) {
            int emojiId = (int) message.getData().get("emojiId");
            String text = message.getData().containsKey("text") ?
                (String) message.getData().get("text") : null;
            long timestamp = (long) message.getData().get("timestamp");

            // Store reaction info in the player's data for rendering
            playerInfo.setCurrentReaction(emojiId, text, timestamp);

            if (emojiId >= 1000) {
                System.out.println("Player " + playerInfo.getUsername() + " reacted with text " + (emojiId - 1000));
            } else {
                System.out.println("Player " + playerInfo.getUsername() + " reacted with emoji " + emojiId);
            }
        }
    }
    public static void sendPlayerPosition(float x, float y, String location) {
        if (!isConnected) return;

        GameMessage posMessage = new GameMessage(MessageType.PLAYER_POSITION, username, instanceId);
        posMessage.addData("x", x);
        posMessage.addData("y", y);
        posMessage.addData("location", location);

        sendMessage(posMessage);
    }

    private static void sendMessage(GameMessage message) {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            disconnect();
        }
    }

    private static void startListening() {
        listenerThread.submit(() -> {
            try {
                while (isConnected) {
                    GameMessage message = (GameMessage) objectInputStream.readObject();
                    processIncomingMessage(message);
                    if (messageHandler != null) {
                        messageHandler.accept(message);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in client listener: " + e.getMessage());
                disconnect();
            }
        });
    }

    private static void processIncomingMessage(GameMessage message) {
        switch (message.getType()) {
            case PLAYER_JOIN:
                // A new player has joined
                handlePlayerJoin(message);
                break;

            case PLAYER_POSITION:
                // Update position of a remote player
                updateRemotePlayerPosition(message);
                break;

            case PLAYER_LEAVE:
                // A player has left
                removeRemotePlayer(message.getInstanceId());
                break;
            case PLAYER_REACTION:
                // Handle incoming reaction
                handlePlayerReaction(message);
                break;
            default:
                // Handle other message types
                break;
        }
    }

    private static void handlePlayerJoin(GameMessage message) {
        String playerUsername = message.getUsername();
        String playerInstanceId = message.getInstanceId();

        // Don't process our own messages
        if (playerInstanceId.equals(instanceId)) return;

        int farmIndex = (int) message.getData().get("farmIndex");

        // Default position and location until we receive a position update
        float x = 0, y = 0;
        String location = "village";

        // Create remote player info
        RemotePlayerInfo playerInfo = new RemotePlayerInfo(
            playerUsername, playerInstanceId, x, y, location, farmIndex);
        remotePlayers.put(playerInstanceId, playerInfo);

        System.out.println("Player joined: " + playerUsername + " (Farm " + (farmIndex + 1) + ")");

        // Get the game instance
        Game gameInstance = Game.getInstance();

        // Check if the game instance is properly initialized with players
        if (gameInstance == null || gameInstance.getPlayers() == null || gameInstance.getPlayers().isEmpty()) {
            System.out.println("Game instance not fully initialized yet, storing remote player info only");
            return;
        }

        User user = UserRepository.getInstance().getUserByUsername(playerUsername);
        if (user == null) {
            System.err.println("Error: User " + playerUsername + " not found in repository");
            return;
        }

        // Check if this player is already in the game's player list
        if (gameInstance.getPlayers().stream().noneMatch(p -> p.getUsername().equals(playerUsername))) {
            // Player is not in the game, add them to the players list
            gameInstance.getPlayers().add(user);
            System.out.println("Added " + playerUsername + " to players list");

            // Now select a map for this player (use 1-based index for game model)
            try {
                gameInstance.selectMap(user, farmIndex + 1);
                System.out.println("Selected map " + (farmIndex + 1) + " for " + playerUsername);
            } catch (Exception e) {
                System.err.println("Error selecting map for " + playerUsername + ": " + e.getMessage());
            }
        } else {
            System.out.println("Player " + playerUsername + " already in game");
        }

        // Make sure this player has the correct farm index in the game's selected maps
        if (gameInstance.getSelectedMaps().containsKey(user)) {
            int currentMapSelection = gameInstance.getSelectedMaps().get(user);
            if (currentMapSelection != farmIndex + 1) {
                try {
                    gameInstance.selectMap(user, farmIndex + 1);
                    System.out.println("Updated map selection for " + playerUsername + " to " + (farmIndex + 1));
                } catch (Exception e) {
                    System.err.println("Error updating map selection: " + e.getMessage());
                }
            }
        }
    }

    private static void updateRemotePlayerPosition(GameMessage message) {
        String playerInstanceId = message.getInstanceId();

        // Don't process our own messages
        if (playerInstanceId.equals(instanceId)) return;

        float x = (float) message.getData().get("x");
        float y = (float) message.getData().get("y");
        String location = (String) message.getData().get("location");

        RemotePlayerInfo playerInfo = remotePlayers.get(playerInstanceId);
        if (playerInfo == null) {
            // First time seeing this player, need to request more info
            // This shouldn't happen if proper PLAYER_JOIN was received first
            System.out.println("Received position for unknown player: " + message.getUsername());

            // We can create a temporary entry
            int farmIndex = 0; // Default value, will be updated when we get proper join info
            playerInfo = new RemotePlayerInfo(message.getUsername(), playerInstanceId, x, y, location, farmIndex);
            remotePlayers.put(playerInstanceId, playerInfo);
        } else {
            // Update existing player
            playerInfo.setPosition(x, y);
            playerInfo.setLocation(location);
        }
    }

    private static void removeRemotePlayer(String instanceId) {
        RemotePlayerInfo removed = remotePlayers.remove(instanceId);
        if (removed != null) {
            System.out.println("Remote player left: " + removed.getUsername());

            // Could also remove from Game instance, but we'll leave them for now
            // since removing might cause issues with farms, etc.
        }
    }

    public static void disconnect() {
        isConnected = false;
        try {
            if (clientSocket != null) clientSocket.close();
            if (objectOutputStream != null) objectOutputStream.close();
            if (objectInputStream != null) objectInputStream.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}
