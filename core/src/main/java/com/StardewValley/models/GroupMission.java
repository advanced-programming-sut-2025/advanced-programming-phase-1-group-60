package com.StardewValley.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class GroupMission implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String requiredItemName;
    private int requiredAmount;
    private int capacity;
    private int rewardPerPlayer;
    private List<String> joinedPlayers;
    private Map<String, Integer> contributions;
    private MissionStatus status;

    public enum MissionStatus {
        AVAILABLE,
        ACTIVE,
        COMPLETED
    }

    public GroupMission(int id, String name, String requiredItemName, int requiredAmount, int capacity, int rewardPerPlayer) {
        this.id = id;
        this.name = name;
        this.requiredItemName = requiredItemName;
        this.requiredAmount = requiredAmount;
        this.capacity = capacity;
        this.rewardPerPlayer = rewardPerPlayer;
        this.joinedPlayers = new ArrayList<>();
        this.contributions = new HashMap<>();
        this.status = MissionStatus.AVAILABLE;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRequiredItemName() {
        return requiredItemName;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRewardPerPlayer() {
        return rewardPerPlayer;
    }

    public List<String> getJoinedPlayers() {
        return joinedPlayers;
    }

    public Map<String, Integer> getContributions() {
        return contributions;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public boolean isFull() {
        return joinedPlayers.size() >= capacity;
    }

    public void addPlayer(String username) {
        if (!joinedPlayers.contains(username) && !isFull()) {
            joinedPlayers.add(username);
            contributions.put(username, 0);
        }
    }

    public int getTotalContributions() {
        return contributions.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void addContribution(String username, int amount) {
        contributions.put(username, contributions.getOrDefault(username, 0) + amount);
    }

    public boolean isComplete() {
        return getTotalContributions() >= requiredAmount;
    }
}
