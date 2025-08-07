package com.StardewValley.Network;

import java.util.Map;

public class Message {
    private ActionType action;
    private Map<String, Object> payload;
    private String senderId;

    public enum ActionType {
        // Lobby Actions
        CREATE_LOBBY,
        CREATE_LOBBY_SUCCESS,
        JOIN_LOBBY,
        LEAVE_LOBBY,
        KICK_PLAYER,
        START_GAME,
        PROCEED_TO_MAP_SELECTION,
        LOBBY_LIST_UPDATE,
        LOBBY_STATE_UPDATE,
        SELECT_MAP,
        MAP_SELECTION_UPDATE,
        GAME_STARTED,

        // Trade Actions
        TRADE_REQUEST,          // C -> S: Player A wants to trade with B
        TRADE_INVITE,           // S -> C: Player A invites you to trade
        TRADE_RESPONSE,         // C -> S: Player B accepts/rejects trade
        TRADE_START,            // S -> C: Both players should open trade view
        TRADE_UPDATE_OFFER,     // C -> S: Player updates their offer
        TRADE_OFFER_UPDATED,    // S -> C: The trade offer has been updated
        TRADE_SUBMIT,           // C -> S: Requester submits the final offer
        TRADE_FINALIZE_REQUEST, // S -> C: Receiver gets the final offer to accept/reject
        TRADE_FINALIZE_RESPONSE,// C -> S: Receiver sends final accept/reject
        TRADE_COMPLETE,         // S -> C: Trade was successful, update inventories
        TRADE_CANCELLED,        // S -> C: Trade was cancelled

        // Game Actions
        PLAYER_MOVE,
        PLAYER_POSITION_UPDATE,
        USE_TOOL,
        BUY_ITEM,
        SELL_ITEM,
        CHAT_MESSAGE,
        INTERACT_NPC,
        SHOW_REACTION,
        ITEM_SOLD_UPDATE,

        // General
        ERROR,
        SUCCESS,
        GAME_STATE_UPDATE,
        GET_PLAYER_LIST
    }

    public Message(ActionType action, Map<String, Object> payload) {
        this.action = action;
        this.payload = payload;
    }

    // Getters and Setters
    public ActionType getAction() { return action; }
    public void setAction(ActionType action) { this.action = action; }
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
}
