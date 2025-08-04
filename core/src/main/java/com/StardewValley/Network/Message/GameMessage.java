// core/src/main/java/com/StardewValley/Network/Message/GameMessage.java
package com.StardewValley.Network.Message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        PLAYER_POSITION,
        PLAYER_JOIN,
        PLAYER_LEAVE,
        ACTION_REQUEST,
        ACTION_RESPONSE,
        STATE_UPDATE
    }

    private MessageType type;
    private String username;
    private String instanceId;
    private Map<String, Object> data;

    public GameMessage(MessageType type, String username, String instanceId) {
        this.type = type;
        this.username = username;
        this.instanceId = instanceId;
        this.data = new HashMap<>();
    }

    // Getters and setters
    public MessageType getType() { return type; }
    public String getUsername() { return username; }
    public String getInstanceId() { return instanceId; }
    public Map<String, Object> getData() { return data; }

    public void addData(String key, Object value) {
        data.put(key, value);
    }
}
