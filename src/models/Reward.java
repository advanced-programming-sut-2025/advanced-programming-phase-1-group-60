package models;

import java.util.List;
import java.util.Map;

public class Reward {
    private Map<Item, Integer> items;    // آیتم‌های جایزه و تعداد
    private int gold;                    // مقدار طلا
    private int friendshipXp;            // امتیاز دوستی اضافی

    public Reward(Map<Item, Integer> items, int gold, int friendshipXp) {
        this.items = items;
        this.gold = gold;
        this.friendshipXp = friendshipXp;
    }

    public Map<Item, Integer> getItems() { return items; }
    public int getGold() { return gold; }
    public int getFriendshipXp() { return friendshipXp; }
}

