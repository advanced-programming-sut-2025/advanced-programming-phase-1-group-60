package com.StardewValley.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().create();

    public static String toJson(Message message) {
        return gson.toJson(message);
    }

    public static Message fromJson(String json) {
        try {
            return gson.fromJson(json, Message.class);
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + json);
            e.printStackTrace();
            return null;
        }
    }
}
