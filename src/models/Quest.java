package models;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    private int id;                          // شناسه یکتا
    private Npc giver;                       // NPC که این مأموریت را ارائه می‌دهد
    private Item requiredItem;        // آیتم‌های مورد نیاز برای تکمیل
    private Reward reward;                   // جایزه مأموریت
    private boolean completed;               // وضعیت اتمام مأموریت
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
        this.activationFriendLevel = activationFriendLevel;
        this.activationSeasonOffset = activationSeasonOffset;
    }

    // گترها
    public int getId() { return id; }
    public Npc getGiver() { return giver; }
    public Item getRequiredItems() { return requiredItem; }
    public Reward getReward() { return reward; }
    public boolean isCompleted() { return completed; }
    public int getActivationFriendLevel() { return activationFriendLevel; }
    public int getActivationSeasonOffset() { return activationSeasonOffset; }

    // علامت‌گذاری به‌عنوان انجام شده
    public void complete() {
        this.completed = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append("\n");
        sb.append("Giver: ").append(giver.getName()).append("\n");
        sb.append("RequiredItem: ").append(requiredItem.getName()).append(" ").append(requiredItem.getQuantity()).append("\n");
        return sb.toString();
    }
}
