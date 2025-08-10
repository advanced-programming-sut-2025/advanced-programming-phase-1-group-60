package com.StardewValley.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Lobby {
    private final String id;
    private String name;
    private boolean isPublic;
    private String password;
    private boolean isVisible;
    private String admin;
    private List<String> members;
    private int capacity;
    private List<String> chatHistory = new ArrayList<>(); // Initialize to prevent NullPointerException


    public Lobby(String name, boolean isPublic, String password, boolean isVisible, String creator, int capacity) {
        // FIX: Generate a 10-digit random ID
        this.id = generateRandomId(10);
        this.name = name;
        this.isPublic = isPublic;
        this.password = password;
        this.isVisible = isVisible;
        this.admin = creator;
        this.capacity = capacity;
        this.members = new ArrayList<>();
        this.members.add(creator);
    }

    // New method to generate a random numeric ID
    private String generateRandomId(int length) {
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            idBuilder.append(random.nextInt(10));
        }
        return idBuilder.toString();
    }


    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isPublic() { return isPublic; }
    public String getPassword() { return password; }
    public boolean isVisible() { return isVisible; }
    public String getAdmin() { return admin; }
    public List<String> getMembers() { return members; }
    public int getCapacity() { return capacity; }

    // --- Logic Methods ---
    public boolean isFull() {
        return members.size() >= capacity;
    }
    public void setChatHistory(List<String> chatHistory) {
        this.chatHistory = chatHistory != null ? new ArrayList<>(chatHistory) : new ArrayList<>();
    }

    public void addMessageToChatHistory(String message) {
        this.chatHistory.add(message);
    }
    public void addMember(String username) {
        if (!members.contains(username) && !isFull()) {
            members.add(username);
        }
    }

    /**
     * Removes a member from the lobby. If the admin leaves, it assigns a new admin.
     * @param username The username of the member to remove.
     * @return True if the lobby is now empty and should be closed, false otherwise.
     */
    public boolean removeMember(String username) {
        members.remove(username);

        if (username.equals(admin) && !members.isEmpty()) {
            this.admin = members.get(0);
            System.out.println("New admin for lobby " + this.name + " is " + this.admin);
        }

        // FIX: The lobby should only be closed if it's completely empty.
        return members.isEmpty();
    }

    // --- CSV Serialization for simple DB ---
    public String toCSV() {
        return String.join(",",
            id, name, String.valueOf(isPublic), password == null ? "" : password,
            String.valueOf(isVisible), admin, String.valueOf(capacity), String.join(";", members)
        );
    }

    public static Lobby fromCSV(String csv) {
        String[] parts = csv.split(",", -1);
        Lobby lobby = new Lobby(
            parts[1],
            Boolean.parseBoolean(parts[2]),
            parts[3].isEmpty() ? null : parts[3],
            Boolean.parseBoolean(parts[4]),
            parts[5], // admin
            Integer.parseInt(parts[6]) // capacity
        );
        lobby.members.clear();
        if (parts.length > 7 && !parts[7].isEmpty()) {
            lobby.members.addAll(Arrays.asList(parts[7].split(";")));
        }
        // Manually set the final ID field from the CSV
        try {
            java.lang.reflect.Field idField = Lobby.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(lobby, parts[0]);
        } catch (Exception ignored) {}
        return lobby;
    }
}
