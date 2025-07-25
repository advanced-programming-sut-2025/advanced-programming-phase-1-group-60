package com.StardewValley.models;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    private int id;                          // شناسه یکتا
    private Npc giver;                       // NPC که این مأموریت را ارائه می‌دهد
    private Item requiredItem;        // آیتم‌های مورد نیاز برای تکمیل
    private Reward reward;                   // جایزه مأموریت
    private boolean completed;               // وضعیت اتمام مأموریت (برای سازگاری با کد قبلی)
    private User completedBy;                // بازیکنی که ماموریت را تکمیل کرده است
    private int activationFriendLevel;       // سطح دوستی برای فعال‌سازی (برای مأموریت دوم)
    private int activationSeasonOffset;      // تعداد فصل بعد از دریافت برای فعال‌سازی مأموریت سوم (مثال: 1)

    public Quest(int id, Npc giver,
                 Item requiredItem, Reward reward,
                 int activationFriendLevel, int activationSeasonOffset) {
        this.id = id;
        this.giver = giver;
        this.requiredItem = requiredItem;
        this.reward = reward;
        this.completed = false;
        this.completedBy = null; // در ابتدا هیچکس تکمیل نکرده
        this.activationFriendLevel = activationFriendLevel;
        this.activationSeasonOffset = activationSeasonOffset;
    }

    // گترها
    public int getId() { return id; }
    public Npc getGiver() { return giver; }
    public Item getRequiredItems() { return requiredItem; }
    public Reward getReward() { return reward; }
    @Deprecated // از متد isCompletedByUser استفاده کنید
    public boolean isCompleted() { return completed; }
    public User getCompletedBy() { return completedBy; }
    public int getActivationFriendLevel() { return activationFriendLevel; }
    public int getActivationSeasonOffset() { return activationSeasonOffset; }

    // متد جدید برای بررسی اینکه آیا ماموریت توسط بازیکنی تکمیل شده است یا خیر
    public boolean isCompletedByAnyone() {
        return completedBy != null;
    }

    // علامت‌گذاری به‌عنوان انجام شده توسط یک بازیکن خاص
    public void complete(User user) {
        this.completed = true; // برای سازگاری
        this.completedBy = user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(giver.getName()).append("'s Request (ID: ").append(id).append(")\n");

        if (requiredItem != null) {
            sb.append("Required: ").append(requiredItem.getQuantity()).append("x ").append(requiredItem.getName()).append("\n");
        }
        if (reward != null) {
            sb.append("Reward: ");
            List<String> rewards = new ArrayList<>();
            if (reward.getMoney() > 0) rewards.add(reward.getMoney() + "g");
            if (reward.getItems() != null) rewards.add(reward.getItems().getQuantity() + "x " + reward.getItems().getName());
            if (reward.getFriendshipXp() > 0) rewards.add(reward.getFriendshipXp() + " XP");
            sb.append(String.join(", ", rewards));
        }
        return sb.toString();
    }
}
