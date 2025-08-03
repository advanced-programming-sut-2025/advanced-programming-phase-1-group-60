package com.StardewValley.Network.Client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class ClientMain {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    public static boolean isConnected = false;

    // Unique identifier for this game instance
    private static final String instanceId = UUID.randomUUID().toString();

    public static String getInstanceId() {
        return instanceId;
    }

    public static void connectToServer(String username) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            isConnected = true;
            System.out.println("Connected to server: #" + SERVER_IP + ":" + SERVER_PORT);
            System.out.println("This client is associated with user: " + username);
            System.out.println("Game instance ID: " + instanceId);

            // Send both username and instance ID to server
            String clientInfo = username + "|" + instanceId;
            OutputStream out = socket.getOutputStream();
            out.write(clientInfo.getBytes());
            out.flush();
        } catch (IOException e) {
            isConnected = false;
            System.out.println("Unable to connect to server.");
            e.printStackTrace();
        }
    }
}
