package com.StardewValley.models;

public class Reward {
    private Item item;    // آیتم‌های جایزه و تعداد
    private int money;                    // مقدار طلا
    private int friendshipXp;            // امتیاز دوستی اضافی


    public void setItem (Item item) {
        this.item = item;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setFriendshipXp (int friendshipXp) {
        this.friendshipXp = friendshipXp;
    }

    public Item getItems() { return item; }
    public int getMoney() { return money; }
    public int getFriendshipXp() { return friendshipXp; }
}

