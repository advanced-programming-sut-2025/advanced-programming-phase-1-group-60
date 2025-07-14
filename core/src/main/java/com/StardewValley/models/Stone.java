package com.StardewValley.models;

import java.util.Random;

public class Stone implements RandomElement {
    private final ForagingMineral mineral;
    private final int stoneVariant;

    public Stone(ForagingMineral mineral) {
        this.mineral = mineral;
        this.stoneVariant = new Random().nextInt(8) + 1;
    }
    public int getStoneVariant() {
        return stoneVariant;
    }
    public ForagingMineral getMineral() {
        return mineral;
    }

    public char symbol() { return 'S'; }
    public boolean isPassable() { return false; }
}
