package models;

import java.util.List;
import java.util.Map;

public class Reward {
    private Item item;    // آیتم‌های جایزه و تعداد
    private int gold;                    // مقدار طلا
    private int friendshipXp;            // امتیاز دوستی اضافی


    public void setItem (Item item) {
        this.item = item;
    }

    public void setGold (int gold) {
        this.gold = gold;
    }

    public void setFriendshipXp (int friendshipXp) {
        this.friendshipXp = friendshipXp;
    }

    public Item getItems() { return item; }
    public int getGold() { return gold; }
    public int getFriendshipXp() { return friendshipXp; }
}

