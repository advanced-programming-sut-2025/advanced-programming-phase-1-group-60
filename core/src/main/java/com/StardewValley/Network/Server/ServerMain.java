package com.StardewValley.Network.Server;

import com.StardewValley.Network.GameStateManager;
import com.StardewValley.Network.Message.GameMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    private static final int PORT = 12345;
    private static final String SERVER_NAME = "StardewServer";
    private static final String SERVER_STATUS_FILE = "core/src/main/java/com/StardewValley/Network/Database/CurrentServer.JSON";

    // Map of instance IDs to client handlers
    private final ConcurrentHashMap<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();
    // Map of usernames to instance IDs
    private final Map<String, String> usernameToInstanceId = new HashMap<>();

    public static void main(String[] args) {
        ServerMain server = new ServerMain();
        server.start();
    }

    public void start() {
        writeServerStatus(true);
        System.out.println("Server started: #" + SERVER_NAME);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Create a new client handler for this connection
                ClientHandler handler = new ClientHandler(clientSocket, this);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
            writeServerStatus(false);
        }
    }

    /**
     * Register a client with the server
     */
    public void registerClient(String username, String instanceId, ClientHandler handler) {
        clientHandlers.put(instanceId, handler);
        usernameToInstanceId.put(username, instanceId);
        System.out.println("Registered client: " + username + " (Instance: " + instanceId + ")");
        System.out.println("Total connected clients: " + clientHandlers.size());
    }

    /**
     * Unregister a client from the server
     */
    public void unregisterClient(String instanceId) {
        ClientHandler handler = clientHandlers.remove(instanceId);
        if (handler != null) {
            String username = handler.getUsername();
            usernameToInstanceId.remove(username);
            GameStateManager.getInstance().removePlayer(instanceId);

            // Notify all clients that this player has left
            GameMessage leaveMessage = new GameMessage(
                GameMessage.MessageType.PLAYER_LEAVE,
                username,
                instanceId
            );
            broadcastMessage(leaveMessage, null);

            System.out.println("Unregistered client: " + username + " (Instance: " + instanceId + ")");
            System.out.println("Total connected clients: " + clientHandlers.size());
        }
    }

    /**
     * Broadcast a message to all connected clients except the sender
     */
    public void broadcastMessage(GameMessage message, String excludeInstanceId) {
        for (Map.Entry<String, ClientHandler> entry : clientHandlers.entrySet()) {
            String instanceId = entry.getKey();
            if (!instanceId.equals(excludeInstanceId)) {
                ClientHandler client = entry.getValue();
                client.sendMessage(message);
            }
        }
    }

    /**
     * Update the server status file
     */
    public static void writeServerStatus(boolean running) {
        String json = "{ \"running\": " + running + " }";
        try {
            Files.write(Paths.get(SERVER_STATUS_FILE), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
