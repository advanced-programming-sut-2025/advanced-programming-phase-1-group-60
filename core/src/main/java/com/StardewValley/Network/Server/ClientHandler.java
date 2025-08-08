package com.StardewValley.Network.Server;

import com.StardewValley.Network.JsonUtil;
import com.StardewValley.Network.LobbyManager;
import com.StardewValley.Network.Message;
import com.StardewValley.models.Game;
import com.StardewValley.models.Lobby;
import com.StardewValley.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap; // Import HashMap

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ServerMain server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private final LobbyManager lobbyManager;

    // NEW: To store map selections for each lobby
    private static final Map<String, Map<String, Integer>> lobbyMapSelections = new HashMap<>();

    public ClientHandler(Socket socket, ServerMain server) {
        this.clientSocket = socket;
        this.server = server;
        this.lobbyManager = LobbyManager.getInstance();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            this.username = in.readLine();
            if (this.username == null || this.username.isEmpty()) {
                throw new IOException("Client did not send a username.");
            }
            server.addClient(this.username, this);
            System.out.println("User connected: " + this.username);
            System.out.println("Current users: " + server.getConnectedClients());

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Message receivedMessage = JsonUtil.fromJson(inputLine);
                if (receivedMessage != null) {
                    new Thread(() -> processMessage(receivedMessage)).start();
                }
            }
        } catch (IOException e) {
            System.out.println("Client " + (username != null ? username : "") + " disconnected: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(username);
            System.out.println("User disconnected: " + username);
            System.out.println("Current users: " + server.getConnectedClients());
        }
    }

    private void processMessage(Message message) {
        System.out.println("Processing message from " + username + ": " + message.getAction());
        switch (message.getAction()) {
            case CREATE_LOBBY:
                handleCreateLobby(message.getPayload());
                break;
            case JOIN_LOBBY:
                handleJoinLobby(message.getPayload());
                break;
            case LEAVE_LOBBY:
                handleLeaveLobby(message.getPayload());
            case START_GAME:
                handleStartGame(message.getPayload());
                break;
            case SELECT_MAP:
                handleSelectMap(message.getPayload());
                break;
            case PLAYER_MOVE:
                handlePlayerMove(message.getPayload());
                break;
            case SHOW_REACTION:
                handleEmojiReaction(message.getPayload());
                break;
            case PLAYER_DATA_UPDATE: // NEW: Handle player data update from client
                handlePlayerDataUpdate(message.getPayload());
                break;
            default:
                System.out.println("Unknown action received by handler: " + message.getAction());
        }
    }

    private void handleCreateLobby(Map<String, Object> payload) {
        String name = (String) payload.get("name");
        boolean isPublic = (Boolean) payload.get("isPublic");
        String password = (String) payload.get("password");
        boolean isVisible = (Boolean) payload.get("isVisible");
        // GSON ممکن است اعداد را به صورت Double بخواند
        int capacity = ((Double) payload.get("capacity")).intValue();

        // سرور لابی را ایجاد می کند
        Lobby newLobby = new Lobby(name, isPublic, password, isVisible, this.username, capacity);
        lobbyManager.addLobby(newLobby);
        System.out.println("Server created lobby '" + name + "' with ID: " + newLobby.getId() + " for user " + this.username);

        // سرور به کلاینت ایجاد کننده پاسخ می دهد تا وارد لابی شود
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("lobby", newLobby);
        Message successMessage = new Message(Message.ActionType.CREATE_LOBBY_SUCCESS, responsePayload);
        sendMessage(successMessage);
    }

    private void handleJoinLobby(Map<String, Object> payload) {
        String lobbyId = (String) payload.get("lobbyId");
        String password = (String) payload.get("password");

        Lobby lobby = lobbyManager.getLobbyById(lobbyId).orElse(null);

        if (lobby == null) {
            // Send error message back to client
            return;
        }
        if (lobby.isFull()) {
            // Send error message
            return;
        }
        if (!lobby.isPublic() && (lobby.getPassword() == null || !lobby.getPassword().equals(password))) {
            // Send error message
            return;
        }

        lobby.addMember(this.username);
        lobbyManager.updateLobby(lobby);

        // Broadcast the updated lobby state to all members
        broadcastLobbyState(lobby);
    }

    private void handleLeaveLobby(Map<String, Object> payload) {
        String lobbyId = (String) payload.get("lobbyId");
        Lobby lobby = lobbyManager.getLobbyById(lobbyId).orElse(null);

        if (lobby != null) {
            boolean lobbyIsEmpty = lobby.removeMember(this.username);
            if (lobbyIsEmpty) {
                lobbyManager.getLobbies().remove(lobby);
                lobbyManager.saveLobbies();
                System.out.println("Lobby " + lobbyId + " closed as it is empty.");
            } else {
                lobbyManager.updateLobby(lobby);
                // Broadcast the new state to remaining members
                broadcastLobbyState(lobby);
            }
        }
    }

    private void broadcastLobbyState(Lobby lobby) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("lobby", lobby);
        Message updateMessage = new Message(Message.ActionType.LOBBY_STATE_UPDATE, payload);

        for (String member : lobby.getMembers()) {
            server.sendMessageTo(member, updateMessage);
        }
    }

    private void handleStartGame(Map<String, Object> payload) {
        // دیگر نیازی به بارگذاری مجدد فایل نیست چون سرور همیشه لیست بروز را در حافظه دارد
        String lobbyId = (String) payload.get("lobbyId");
        Lobby lobby = lobbyManager.getLobbyById(lobbyId).orElse(null);

        if (lobby == null || !lobby.getAdmin().equals(this.username)) {
            System.err.println("START_GAME validation failed for user " + this.username + " and lobby " + lobbyId);
            return;
        }

        // Initialize map selection for this lobby
        lobbyMapSelections.put(lobbyId, new HashMap<>());

        Map<String, Object> newPayload = new HashMap<>();
        newPayload.put("lobby", lobby);

        // Send a message to all lobby members to proceed to map selection
        Message proceedMessage = new Message(Message.ActionType.PROCEED_TO_MAP_SELECTION, newPayload);
        for (String memberUsername : lobby.getMembers()) {
            server.sendMessageTo(memberUsername, proceedMessage);
        }
        System.out.println("Handler for " + username + " instructed lobby " + lobbyId + " to proceed to map selection.");
    }
    private void handleEmojiReaction(Map<String, Object> payload) {
        // Create a new message to broadcast to all players
        Map<String, Object> broadcastPayload = new HashMap<>();
        broadcastPayload.put("reaction", payload);

        Message broadcastMessage = new Message(Message.ActionType.SHOW_REACTION, broadcastPayload);

        // Get the lobby for this player
        String playerUsername = (String) payload.get("username");
        Lobby playerLobby = null;

        for (Lobby lobby : lobbyManager.getLobbies()) {
            if (lobby.getMembers().contains(playerUsername)) {
                playerLobby = lobby;
                break;
            }
        }

        // If player is in a lobby, broadcast to all lobby members
        if (playerLobby != null) {
            for (String member : playerLobby.getMembers()) {
                server.sendMessageTo(member, broadcastMessage);
            }
        }
    }
    private void handleSelectMap(Map<String, Object> payload) {
        String lobbyId = (String) payload.get("lobbyId");
        // GSON might deserialize numbers as Double, so we handle both Integer and Double.
        int mapIndex;
        Object mapIndexObj = payload.get("mapIndex");
        if (mapIndexObj instanceof Double) {
            mapIndex = ((Double) mapIndexObj).intValue();
        } else {
            mapIndex = (Integer) mapIndexObj;
        }

        Lobby lobby = lobbyManager.getLobbyById(lobbyId).orElse(null);
        if (lobby == null) return;

        Map<String, Integer> selections = lobbyMapSelections.get(lobbyId);
        if (selections == null) {
            selections = new HashMap<>();
            lobbyMapSelections.put(lobbyId, selections);
        }

        // Don't allow duplicate map selections
        if (selections.containsValue(mapIndex)) {
            // Send error message back to the client
            Map<String, Object> errorPayload = new HashMap<>();
            errorPayload.put("message", "This map has already been selected by another player");
            Message errorMessage = new Message(Message.ActionType.ERROR, errorPayload);
            sendMessage(errorMessage);
            return;
        }

        selections.put(this.username, mapIndex);

        // Update all clients with the current selections
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("selections", selections);
        Message updateMessage = new Message(Message.ActionType.MAP_SELECTION_UPDATE, updatePayload);
        for (String memberUsername : lobby.getMembers()) {
            server.sendMessageTo(memberUsername, updateMessage);
        }

        // If all players have selected maps, automatically start the game
        if (selections.size() == lobby.getMembers().size()) {
            Map<String, Object> gameStartPayload = new HashMap<>();
            gameStartPayload.put("mapSelections", selections);
            Message gameStartedMessage = new Message(Message.ActionType.GAME_STARTED, gameStartPayload);
            for (String memberUsername : lobby.getMembers()) {
                server.sendMessageTo(memberUsername, gameStartedMessage);
            }
            System.out.println("All players in lobby " + lobbyId + " have selected maps. Starting game.");
        }
    }
    private void handlePlayerMove(Map<String, Object> payload) {
        // Create a new message to broadcast to all players
        Map<String, Object> broadcastPayload = new HashMap<>();
        broadcastPayload.put("player", payload);

        Message broadcastMessage = new Message(Message.ActionType.PLAYER_POSITION_UPDATE, broadcastPayload);

        // Get the lobby for this player
        String playerUsername = (String) payload.get("username");
        Lobby playerLobby = null;

        for (Lobby lobby : lobbyManager.getLobbies()) {
            if (lobby.getMembers().contains(playerUsername)) {
                playerLobby = lobby;
                break;
            }
        }

        // If player is in a lobby, broadcast to all lobby members
        if (playerLobby != null) {
            for (String member : playerLobby.getMembers()) {
                server.sendMessageTo(member, broadcastMessage);
            }
        }
    }
    public void sendMessage(Message message) {
        if (out != null) {
            String jsonMessage = JsonUtil.toJson(message);
            out.println(jsonMessage);
        }
    }
    /**
     * NEW: Handles PLAYER_DATA_UPDATE messages from clients.
     * This method updates the server's authoritative Game/User model
     * and then broadcasts the updated data to all relevant clients.
     * @param payload The payload containing the updated user data.
     */
    private void handlePlayerDataUpdate(Map<String, Object> payload) {
        // Extract updated user data from the payload
        // Note: GSON might deserialize numbers as Double, so cast accordingly.
        String username = (String) payload.get("username");
        double money = ((Double) payload.get("money"));
        double completedQuestsCount = ((Double) payload.get("completedQuestsCount"));
        double averageSkillLevel = ((Double) payload.get("averageSkillLevel"));

        // Find the actual User object on the server and update its properties
        // This assumes Game.getInstance() gives access to the current active game state
        // and its users. If you have multiple games, you'd need to identify the correct game instance.
        User userToUpdate = Game.getInstance().getUserByUsername(username);
        if (userToUpdate != null) {
            userToUpdate.setMoney((int) money);
            // For completedQuestsCount, you might need a more granular update
            // For simplicity, we'll just set it assuming the client sends the correct count.
            // A more robust solution would involve the server validating and updating quests.
            // userToUpdate.setCompletedQuestsCount((int) completedQuestsCount); // You might need to add this setter
            // For averageSkillLevel, similarly.
            // userToUpdate.setAverageSkillLevel((float) averageSkillLevel); // You might need to add this setter

            // IMPORTANT: If skills or quests are complex objects, you'll need to
            // serialize/deserialize them properly. For now, we're just sending primitive data.
            // If you send a full User object, ensure it's serializable by Gson.

            // Now, broadcast this updated user data to all clients in the same game instance
            // (or all clients if it's a global scoreboard)
            server.broadcastGameDataUpdate(userToUpdate); // Assuming Game has a getGameId()
        } else {
            System.err.println("Received PLAYER_DATA_UPDATE for unknown user: " + username);
        }
    }

    public String getUsername() {
        return username;
    }
}
