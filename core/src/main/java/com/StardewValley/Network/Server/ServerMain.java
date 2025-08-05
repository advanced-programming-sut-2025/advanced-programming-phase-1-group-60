package com.StardewValley.Network.Server;

import com.StardewValley.Network.Message;
import com.StardewValley.Network.SessionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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
}
