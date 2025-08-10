package com.StardewValley.models;

import com.StardewValley.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupMissionManager {
    private static GroupMissionManager instance;
    private List<GroupMission> allMissions;

    private GroupMissionManager() {
        initializeMissions();
    }

    public static synchronized GroupMissionManager getInstance() {
        if (instance == null) {
            instance = new GroupMissionManager();
        }
        return instance;
    }

    private void initializeMissions() {
        allMissions = new ArrayList<>();
        allMissions.add(new GroupMission(1, "تحویل چوب", "Wood", 500, 2, 100));
        allMissions.add(new GroupMission(2, "سوپ قزل‌آلا", "trout soup", 2, 2, 50));
        allMissions.add(new GroupMission(3, "تحویل سنگ", "Stone", 1000, 3, 150));
    }

    public List<GroupMission> getAllMissions() {
        return allMissions;
    }

    public GroupMission getMissionById(int id) {
        return allMissions.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    public List<GroupMission> getAvailableMissions() {
        return allMissions.stream()
            .filter(m -> m.getStatus() == GroupMission.MissionStatus.AVAILABLE && !m.isFull())
            .collect(Collectors.toList());
    }

    public synchronized String joinMission(String username, int missionId) {
        User user = UserRepository.getInstance().getUserByUsername(username);
        if (user == null) {
            return "User not found.";
        }

        if (user.getActiveGroupMissions().size() >= 3) {
            return "You already have 3 active missions.";
        }

        GroupMission mission = getMissionById(missionId);
        if (mission == null || mission.isFull() || mission.getStatus() != GroupMission.MissionStatus.AVAILABLE) {
            return "Mission not available or full.";
        }

        mission.addPlayer(username);
        user.addActiveGroupMission(mission);

        if (mission.isFull()) {
            mission.setStatus(GroupMission.MissionStatus.ACTIVE);
        }

        return "Successfully joined the mission.";
    }

    public synchronized String deliverItem(String username, int missionId, int amount) {
        User user = UserRepository.getInstance().getUserByUsername(username);
        GroupMission mission = getMissionById(missionId);
        System.out.println("[SERVER LOG] User " + username + " is trying to deliver " + amount + " of " + mission.getRequiredItemName() + " for mission " + missionId);
        System.out.println("[SERVER LOG] Server-side inventory for " + username + ": " + user.getInventory().getItems());
        boolean hasItems = user.getInventory().hasItem(mission.getRequiredItemName(), amount);
        System.out.println("[SERVER LOG] Server check 'user.getInventory().hasItem': " + hasItems);

        if (user == null || mission == null || !mission.getJoinedPlayers().contains(username) || mission.getStatus() != GroupMission.MissionStatus.ACTIVE) {
            return "Invalid request. You are not part of this active mission.";
        }

        String itemName = mission.getRequiredItemName();
        if (!user.getInventory().hasItem(itemName, amount)) {
            return "Not enough " + itemName + " in your inventory.";
        }

        // First, update the mission contribution
        mission.addContribution(username, amount);

        // Then, remove the item from the player's inventory
        user.getInventory().removeItemByName(itemName, amount);

        // Check if the mission is complete
        if (mission.isComplete()) {
            mission.setStatus(GroupMission.MissionStatus.COMPLETED);
            // Distribute rewards to all joined players
            for (String playerUsername : mission.getJoinedPlayers()) {
                User participant = UserRepository.getInstance().getUserByUsername(playerUsername);
                if (participant != null) {
                    participant.setMoney(participant.getMoney() + mission.getRewardPerPlayer());
                    // The mission will be removed from the active list on the next broadcast
                }
            }
            UserRepository.getInstance().saveUsers();
            return "Item delivered. Mission complete! Rewards have been distributed.";
        }

        return "Item delivered successfully.";
    }
}
