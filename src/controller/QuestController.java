package controller;

import models.Quest;
import models.Reward;
import models.Item;
import models.Npc;
import models.User;
import repository.NpcRepository;

import java.util.*;

public class QuestController {
    private User player;
    private Map<Integer, Quest> activeQuests = new HashMap<>();
    private int questIdCounter = 1;

    public QuestController(User player) {
        this.player = player;
        // در ابتدای بازی مأموریت اول همه‌ی NPCها فعال می‌شود
        for (Npc npc : NpcRepository.getInstance().getAllNpcs()) {
            for (Quest q : npc.getQuests()) {
                if (q.getActivationFriendLevel() == 0 && q.getActivationSeasonOffset() == 0) {
                    activeQuests.put(q.getId(), q);
                }
            }
        }
    }

    /** لیست مأموریت‌های فعال */
    public void listQuests() {
        System.out.println("Active quests:");
        for (Quest q : activeQuests.values()) {
            if (!q.isCompleted()) {
                System.out.println(q.getId() + ": " + q.getDescription());
            }
        }
    }

    /** تلاش برای اتمام یک مأموریت */
    public void finishQuest(int questId, int playerX, int playerY) {
        Quest q = activeQuests.get(questId);
        if (q == null || q.isCompleted()) {
            System.out.println("Quest not found or already completed.");
            return;
        }
        Npc giver = q.getGiver();
        // TODO: بررسی فاصله بازیکن تا محل NPC با مختصات playerX, playerY و giver.getTile()

        // بررسی آیتم‌های مورد نیاز
        boolean hasAll = true;
        for (Item req : q.getRequiredItems()) {
            if (!player.getInventory().hasItem(req.getName(), req.getQuantity())) {
                hasAll = false;
                break;
            }
        }
        if (!hasAll) {
            System.out.println("You don't have the required items.");
            return;
        }
        // کم کردن آیتم‌ها از پلیر
        for (Item req : q.getRequiredItems()) {
            player.getInventory().removeItem(req.getName(), req.getQuantity());
        }
        // اعطای جایزه
        Reward rw = q.getReward();
        for (Map.Entry<Item, Integer> e : rw.getItems().entrySet()) {
            player.getInventory().addItem(e.getKey().getName(), e.getValue());
        }
        player.setMoney(player.getMoney() + rw.getGold());
        player.increaseFriendshipXpsWithNpc(giver, rw.getFriendshipXp());

        q.complete();
        System.out.println("Quest #" + questId + " completed! Reward granted.");

        // فعال‌سازی مأموریت‌های جدید بر اساس سطح دوستی یا فصل
        updateActiveQuests();
    }

    /** هر بار که لازم است مأموریت‌های جدید فعال شوند */
    private void updateActiveQuests() {
        int currentSeasonOffset = /* TODO: محاسبه تعداد فصل‌های گذشته از ابتدای بازی */ 0;
        for (Npc npc : NpcRepository.getInstance().getAllNpcs()) {
            int friendLevel = player.getFriendshipLevelWithNpc(npc);
            for (Quest q : npc.getQuests()) {
                if (!activeQuests.containsKey(q.getId()) && !q.isCompleted()) {
                    if (friendLevel >= q.getActivationFriendLevel()
                            && currentSeasonOffset >= q.getActivationSeasonOffset()) {
                        activeQuests.put(q.getId(), q);
                    }
                }
            }
        }
    }
}