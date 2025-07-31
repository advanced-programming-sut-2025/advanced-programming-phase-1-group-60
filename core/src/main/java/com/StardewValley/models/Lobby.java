package com.StardewValley.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby {
    private final String id;
    private String name;
    private boolean isPublic;
    private String password;
    private boolean isVisible;
    private String creator;
    private List<String> members;

    public Lobby(String name, boolean isPublic, String password, boolean isVisible, String creator) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.isPublic = isPublic;
        this.password = password;
        this.isVisible = isVisible;
        this.creator = creator;
        this.members = new ArrayList<>();
        this.members.add(creator);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isPublic() { return isPublic; }
    public String getPassword() { return password; }
    public boolean isVisible() { return isVisible; }
    public String getCreator() { return creator; }
    public List<String> getMembers() { return members; }

    public void addMember(String username) {
        if (!members.contains(username)) members.add(username);
    }
    public void removeMember(String username) {
        members.remove(username);
    }
    // CSV serialization for simple DB
    public String toCSV() {
        return String.join(",",
            id, name, String.valueOf(isPublic), password == null ? "" : password,
            String.valueOf(isVisible), creator, String.join(";", members)
        );
    }

    public static Lobby fromCSV(String csv) {
        String[] parts = csv.split(",", -1);
        Lobby lobby = new Lobby(
            parts[1],
            Boolean.parseBoolean(parts[2]),
            parts[3].isEmpty() ? null : parts[3],
            Boolean.parseBoolean(parts[4]),
            parts[5]
        );
        // Clear the default creator added in constructor
        lobby.members.clear();
        if (parts.length > 6 && !parts[6].isEmpty()) {
            for (String m : parts[6].split(";")) lobby.members.add(m);
        }
        // Set id via reflection (since it's final)
        try {
            java.lang.reflect.Field idField = Lobby.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(lobby, parts[0]);
        } catch (Exception ignored) {}
        return lobby;
    }
}
