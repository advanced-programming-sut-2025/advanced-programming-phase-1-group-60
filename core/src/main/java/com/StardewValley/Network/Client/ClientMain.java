package com.StardewValley.Network.Client;

import com.StardewValley.Network.JsonUtil;
import com.StardewValley.Network.Message;
import com.StardewValley.controller.LobbyController; // <-- ایمپورت جدید
import com.badlogic.gdx.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ClientMain {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    private static Socket socket;
    private static PrintWriter out;
    public static boolean isConnected = false;
    private static final String instanceId = UUID.randomUUID().toString();
    private static Thread listenerThread;

    public static String getInstanceId() {
        return instanceId;
    }

    public static void connectToServer(String username, Game game, LobbyController lobbyController) { // <-- پارامتر تغییر کرد
        if (isConnected) return;
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            isConnected = true;

            out.println(username);

            System.out.println("Connected to server: #" + SERVER_IP + ":" + SERVER_PORT);
            System.out.println("This client is associated with user: " + username);
            System.out.println("Game instance ID: " + instanceId);

            listenerThread = new Thread(new ServerListener(socket, game, lobbyController)); // <-- پارامتر تغییر کرد
            listenerThread.start();

        } catch (IOException e) {
            isConnected = false;
            System.out.println("Unable to connect to server.");
            e.printStackTrace();
        }
    }

    public static void sendMessage(Message message) {
        if (!isConnected || out == null) {
            System.err.println("Not connected to server. Cannot send message.");
            return;
        }
        String jsonMessage = JsonUtil.toJson(message);
        out.println(jsonMessage);
    }

    public static void disconnect() {
        try {
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
            if (out != null) out.close();
            if (socket != null) socket.close();
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
