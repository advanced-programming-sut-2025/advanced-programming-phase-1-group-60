package com.StardewValley.models;

public class ForagingMineral extends ForageItem {
    private String name;
    private int baseSellPrice;

    public ForagingMineral(String name, int baseSellPrice) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
    }

    public ForagingMineral() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public void setBaseSellPrice(int baseSellPrice) {
        this.baseSellPrice = baseSellPrice;
    }
}
