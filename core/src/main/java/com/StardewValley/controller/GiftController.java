package com.StardewValley.controller;

import com.StardewValley.models.Npc;
import com.StardewValley.models.TimeSystem;
import com.StardewValley.models.User;
import com.StardewValley.repository.NpcRepository;
import com.StardewValley.repository.UserRepository;

import java.util.*;

public class GiftController {
    private static final GiftController INSTANCE = new GiftController();

    // رکورد کلیه رویدادهای هدیه
    private List<GiftEvent> giftEvents = new ArrayList<>();
    // نگهداری رویدادهایی که اعلان شده‌اند
    private Set<String> notifiedEvents = new HashSet<>();
    // رویدادهای در انتظار امتیازدهی (eventKey -> GiftEvent)
    private Map<String, GiftEvent> pendingRatings = new LinkedHashMap<>();

    private GiftController() {}

    public static GiftController getInstance() {
        return INSTANCE;
    }

    public String giftToNpc(User player, String npcName, String itemName, Integer quantity) {
        npcName = npcName.toUpperCase();
        Npc npc = NpcRepository.getInstance().getNpcByName(npcName);
        if (npc == null) return "NPC not found.";
        if (!player.getInventory().hasItem(itemName, quantity))
            return "You do not have enough of this item.";

        // حذف آیتم و ثبت رویداد
        player.getInventory().removeItemByName(itemName, quantity);
        String date = TimeSystem.getInstance().getCurrentDate();
        GiftEvent ev = new GiftEvent(player.getUsername(), npc.getName(), itemName, quantity, date);
        giftEvents.add(ev);
        pendingRatings.put(ev.getEventKey(), ev);
        return "You've sent " + itemName + " x" + quantity + " to " + npc.getName()
                + ". Awaiting rating: use 'gift rate -r [1-5]'";
    }

    public String giftToPlayer(User from, String toUsername, String itemName, Integer quantity) {
        User to = UserRepository.getInstance().getUserByUsername(toUsername);
        if (Math.abs(from.getPosition().getPositionX() - to.getPosition().getPositionX()) > 1 ||
        Math.abs(from.getPosition().getPositionY() - to.getPosition().getPositionY()) > 1) {
            return "too far";
        }
        if (to == null) return "Target player not found.";
        if (from.getFriendshipXpsWithUsers(to) < 100)
            return "Not enough friendship.";
        if (!from.getInventory().hasItem(itemName, quantity))
            return "You do not have enough of this item.";

        // انتقال آیتم و ثبت رویداد
        from.getInventory().removeItemByName(itemName, quantity);
        to.getInventory().addItemByName(itemName, quantity);

        String date = TimeSystem.getInstance().getCurrentDate();
        GiftEvent ev = new GiftEvent(from.getUsername(), toUsername, itemName, quantity, date);
        giftEvents.add(ev);
        pendingRatings.put(ev.getEventKey(), ev);
        return "You gave " + toUsername + " " + itemName + " x" + quantity
                + ". They can rate it with 'gift rate -r [1-5]'";
    }

    /**
     * اعمال امتیاز توسط گیرنده با دستور 'gift rate -r [1-5]'
     */
    public String rateGift(User receiver, int rating) {
        for (Iterator<Map.Entry<String, GiftEvent>> it = pendingRatings.entrySet().iterator(); it.hasNext();) {
            GiftEvent ev = it.next().getValue();
            if (ev.getReceiver().equals(receiver.getUsername())) {
                it.remove();
                int xpDelta = 15 + 30 * (3 - rating);
                User sender = UserRepository.getInstance().getUserByUsername(ev.getSender());
                if (sender != null) {
                    if (rating > 2) {
                        sender.increaseFriendshipXpsWithUsers(receiver, xpDelta);
                        receiver.increaseFriendshipXpsWithUsers(sender, xpDelta);
                    } else {
                        sender.increaseFriendshipXpsWithUsers(receiver, -xpDelta);
                        receiver.increaseFriendshipXpsWithUsers(sender, -xpDelta);
                    }
                }
                return "You've rated " + ev.getItemName() + " x" + ev.getQuantity()
                        + " with " + rating + ". Friendship XP change: " + xpDelta;
            }
        }
        return "No pending gifts to rate.";
    }

    public String checkNewGift(User user) {
        String username = user.getUsername();
        for (GiftEvent ev : giftEvents) {
            String key = ev.getEventKey();
            if (ev.getReceiver().equals(username)
                    && !notifiedEvents.contains(key)
                    && pendingRatings.containsKey(key)) {
                notifiedEvents.add(key);
                return ev.getSender() + " sent you " + ev.getItemName() + " x" + ev.getQuantity()
                        + ". Use 'gift rate -r [1-5]' to rate it.";
            }
        }
        return null;
    }

    public List<String> getGiftHistory(User user) {
        String username = user.getUsername();
        List<String> history = new ArrayList<>();
        for (GiftEvent ev : giftEvents) {
            if (ev.getReceiver().equals(username)) {
                history.add(ev.getDate() + ": " + ev.getSender() + " sent you "
                        + ev.getItemName() + " x" + ev.getQuantity());
            }
        }
        return history;
    }

    private static class GiftEvent {
        private final String sender, receiver, itemName, date;
        private final int quantity;

        public GiftEvent(String sender, String receiver, String itemName, int quantity, String date) {
            this.sender = sender;
            this.receiver = receiver;
            this.itemName = itemName;
            this.quantity = quantity;
            this.date = date;
        }
        public String getSender()   { return sender; }
        public String getReceiver() { return receiver; }
        public String getItemName() { return itemName; }
        public int getQuantity()    { return quantity; }
        public String getDate()     { return date; }

        // کلید یکتا: sender#receiver#date
        public String getEventKey() {
            return sender + "#" + receiver + "#" + date;
        }
    }
}
