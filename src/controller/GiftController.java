// src/main/java/controller/GiftController.java
package controller;

import models.Item;
import models.Npc;
import models.TimeSystem;
import models.User;
import repository.NpcRepository;
import repository.UserRepository;

import java.util.*;

public class GiftController {
    private static final GiftController INSTANCE = new GiftController();

    // نگهداری رکورد هدیه‌های داده شده در هر روز برای جلوگیری از دریافت XP چندباره
    // کلید: "giverName#receiverName#date"
    private Set<String> dailyGifts = new HashSet<>();

    private GiftController() {}

    public static GiftController getInstance() {
        return INSTANCE;
    }

    public String giftToNpc(User player, String npcName, String item, Integer quantity) {
        Npc npc = NpcRepository.getInstance().getNpcByName(npcName);
        if (npc == null) {
            return "NPC not found.";
        }

        if (!player.getInventory().hasItem(item, quantity)) {
            return "You do not have enough of this item.";
        }
        // بررسی محدودیت روزانه
        String key = player.getUsername() + "#" + npcName + "#" + TimeSystem.getInstance().getCurrentDate();
        if (dailyGifts.contains(key)) {
            return applyGiftEffect(player, npc, item, false);
        }
        player.getInventory().removeItem(item, quantity);
        dailyGifts.add(key);
        return applyGiftEffect(player, npc, item, true);
    }


    private String applyGiftEffect(User player, Npc npc, String item, boolean firstToday) {
        int xp = 0;
        String msg;
        // همیشه اولین هدیه در روز 50 XP
        if (firstToday) {
            if (npc.isFavoriteItem(item)) {
                xp += 200;
                msg = " It's one of their favorites! +200 XP.";
            }
            else {
                msg = "You've given your first gift to " + npc.getName() + ". +50 friendship XP.";
            }
        } else {
            msg = "You've given another gift to " + npc.getName() + ".";
        }
        player.increaseFriendshipXpsWithNpc(npc, xp);
        return msg;
    }

    public String giftToPlayer(User from, String toUsername, String item, Integer quantity) {
        User to = UserRepository.getInstance().getUserByUsername(toUsername);
        if (to == null) {
            return "Target player not found.";
        }
        if (!from.getInventory().hasItem(item, quantity)) {
            return "You do not have enough of this item.";
        }
        from.getInventory().removeItem(item, quantity);
        to.getInventory().addItem(item, quantity);

        String key = from.getUsername() + "#" + toUsername + "#" + TimeSystem.getInstance().getCurrentDate();
        int xp;
        if (!dailyGifts.contains(key)) {
            xp = 50;
            dailyGifts.add(key);
        } else {
            xp = 20;
        }
        from.increaseFriendshipXpsWithUsers(to, xp);
        to.increaseFriendshipXpsWithUsers(from, xp);
        return "You gave " + toUsername + " a gift. +" + xp + " friendship XP.";
    }
}
