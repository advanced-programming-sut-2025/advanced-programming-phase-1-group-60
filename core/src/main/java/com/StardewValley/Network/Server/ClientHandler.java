package com.StardewValley.Network.Server;

import com.StardewValley.Network.GameStateManager;
import com.StardewValley.Network.Message.GameMessage;
import com.StardewValley.Network.Message.GameMessage.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private String username;
    private String instanceId;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerMain server;
    private boolean running = true;

    public ClientHandler(Socket socket, ServerMain server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // Set up streams
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // Process messages in a loop
            while (running) {
                GameMessage message = (GameMessage) in.readObject();
                processMessage(message);
            }

        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void processMessage(GameMessage message) {
        this.username = message.getUsername();
        this.instanceId = message.getInstanceId();

        switch (message.getType()) {
            case PLAYER_JOIN:
                handlePlayerJoin(message);
                break;

            case PLAYER_POSITION:
                handlePlayerPosition(message);
                break;

            case PLAYER_LEAVE:
                closeConnection();
                break;
            case PLAYER_REACTION:
                handlePlayerReaction(message);
                break;

            default:
                System.out.println("Unhandled message type: " + message.getType());
        }
    }
    private void handlePlayerReaction(GameMessage message) {
        // Just broadcast the reaction to all clients
        server.broadcastMessage(message, instanceId);
        System.out.println("Player " + username + " sent a reaction");
    }
    private void handlePlayerJoin(GameMessage message) {
        System.out.println("Player joined: " + username + " (Instance: " + instanceId + ")");

        // Get the farm index from the message or from GameStateManager
        int farmIndex;
        if (message.getData().containsKey("farmIndex")) {
            farmIndex = (int) message.getData().get("farmIndex");
        } else {
            farmIndex = GameStateManager.getInstance().getFarmSelection(username);
        }

        // Default starting position and location
        float startX = 0, startY = 0;
        String location = "village";

        // Register client with server
        server.registerClient(username, instanceId, this);

        // Initialize player in GameStateManager
        GameStateManager.getInstance().registerPlayer(username, instanceId, startX, startY, location);

        // Send existing player states to the new player
        GameStateManager.getInstance().sendWorldStateToClient(this);

        // Broadcast to all other clients that this player has joined
        message.addData("farmIndex", farmIndex);
        server.broadcastMessage(message, instanceId);
    }

    private void handlePlayerPosition(GameMessage message) {
        float x = (float) message.getData().get("x");
        float y = (float) message.getData().get("y");
        String location = (String) message.getData().get("location");

        // Update player position in game state
        GameStateManager.getInstance().updatePlayerPosition(username, instanceId, x, y, location);

        // Broadcast position to all other clients
        server.broadcastMessage(message, instanceId);
    }

    public void sendMessage(GameMessage message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending message to client: " + e.getMessage());
            closeConnection();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getInstanceId() {
        return instanceId;
    }

    private void closeConnection() {
        running = false;
        server.unregisterClient(instanceId);

        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
