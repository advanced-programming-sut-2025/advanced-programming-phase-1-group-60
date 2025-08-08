package com.StardewValley.Network.Server;

import com.StardewValley.Network.Message;
import com.StardewValley.Network.SessionManager;
import com.StardewValley.models.Game;
import com.StardewValley.models.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    private static final int PORT = 12345;
    private static final String SERVER_NAME = "StardewServer";
    private static final String SERVER_STATUS_FILE = "core/src/main/java/com/StardewValley/Network/Database/CurrentServer.JSON";

    private final Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SessionManager.getInstance().clearAllSessions();
        writeServerStatus(true);
        new ServerMain().startServer();
    }

    public void startServer() {
        System.out.println("Server started: #" + SERVER_NAME);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeServerStatus(false);
        }
    }

    public void addClient(String username, ClientHandler handler) {
        connectedClients.put(username, handler);
    }

    public void removeClient(String username) {
        if (username != null) {
            connectedClients.remove(username);
        }
    }

    public Set<String> getConnectedClients() {
        return connectedClients.keySet();
    }

    public void broadcastMessage(Message message) {
        for (ClientHandler handler : connectedClients.values()) {
            handler.sendMessage(message);
        }
    }

    public void sendMessageTo(String username, Message message) {
        ClientHandler handler = connectedClients.get(username);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    public static void writeServerStatus(boolean running) {
        String json = "{ \"running\": " + running + " }";
        try {
            Files.write(Paths.get(SERVER_STATUS_FILE), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * NEW: Broadcasts updated game data (e.g., player money, skills, quests) to all clients in the same game.
     * This method will serialize relevant User data and send it.
     * @param updatedUser The user whose data has changed.
     */
    public void broadcastGameDataUpdate(User updatedUser) {
        // Find all users in the same game instance
        List<User> usersInGame = Game.getInstance().getPlayers(); // Assuming Game.getInstance() gives the current game
        // and its users are relevant for the scoreboard.
        // If you have multiple active game instances on the server,
        // you'll need a way to get users for a specific gameId.
        // For now, we assume a single game instance.

        // Create a simplified payload for the updated user
        Map<String, Object> userDataPayload = new HashMap<>();
        userDataPayload.put("username", updatedUser.getUsername());
        userDataPayload.put("money", updatedUser.getMoney());
        userDataPayload.put("completedQuestsCount", updatedUser.getCompletedQuestsCount());
        userDataPayload.put("averageSkillLevel", updatedUser.getAverageSkillLevel());
        // Add any other data needed for the scoreboard or other UI updates

        Map<String, Object> payload = new HashMap<>();
        payload.put("updatedUser", userDataPayload);

        Message updateMessage = new Message(Message.ActionType.PLAYER_DATA_UPDATE, payload);

        // Send this update to all clients connected to this game instance
        for (User user : usersInGame) {
            ClientHandler handler = connectedClients.get(user.getUsername());
            if (handler != null) {
                handler.sendMessage(updateMessage);
            }
        }
        System.out.println("Broadcasted PLAYER_DATA_UPDATE for user: " + updatedUser.getUsername());
    }
}
