package com.StardewValley.Network;

import java.util.Map;

public class Message {
    private ActionType action;
    private Map<String, Object> payload;
    private String senderId; // Unique ID for the client connection

    public enum ActionType {
        // Lobby Actions
        CREATE_LOBBY,
        CREATE_LOBBY_SUCCESS, // <-- این اکشن جدید اضافه شد
        JOIN_LOBBY,
        LEAVE_LOBBY,
        KICK_PLAYER,
        START_GAME, // User clicks start game
        PROCEED_TO_MAP_SELECTION, // Server tells clients to go to map selection
        LOBBY_LIST_UPDATE,
        LOBBY_STATE_UPDATE,
        SELECT_MAP, // Client tells server which map they selected
        MAP_SELECTION_UPDATE, // Server tells clients which maps are taken
        GAME_STARTED, // Server tells clients the game has started with all assignments
        PLAYER_DATA_UPDATE,
        // Game Actions
        PLAYER_MOVE, // Player position update
        PLAYER_POSITION_UPDATE, // Broadcast of all player positions
        USE_TOOL,
        BUY_ITEM,
        SELL_ITEM,
        CHAT_MESSAGE,
        INTERACT_NPC,
        SHOW_REACTION, // For emojis over player's head

        // General
        ERROR,
        SUCCESS,
        GAME_STATE_UPDATE
    }

    public Message(ActionType action, Map<String, Object> payload) {
        this.action = action;
        this.payload = payload;
    }

    // Getters and Setters
    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
