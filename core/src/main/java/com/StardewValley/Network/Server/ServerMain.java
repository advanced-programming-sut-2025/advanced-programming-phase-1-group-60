package com.StardewValley.Network.Server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.*;

public class ServerMain {
    private static final int PORT = 12345;
    private static final String SERVER_NAME = "StardewServer";
    private static final Set<String> connectedClients = new HashSet<>();
    private static final String SERVER_STATUS_FILE = "core/src/main/java/com/StardewValley/Network/Database/CurrentServer.JSON";

    public static void main(String[] args) {
        writeServerStatus(true);
        System.out.println("Server started: #" + SERVER_NAME);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Read the username sent by the client
                InputStream in = clientSocket.getInputStream();
                byte[] buffer = new byte[64];
                int len = in.read(buffer);
                String username = new String(buffer, 0, len);
                connectedClients.add(username);
                System.out.println("User connected: " + username);
                System.out.println("Current users: " + connectedClients);
                // You can start a new thread for each client here for further communication
            }
        } catch (IOException e) {
            e.printStackTrace();
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
