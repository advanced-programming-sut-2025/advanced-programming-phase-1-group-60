package com.StardewValley.models;

public class Stone implements RandomElement {
    private final ForagingMineral mineral;

    public Stone(ForagingMineral mineral) {
        this.mineral = mineral;
    }

    public ForagingMineral getMineral() {
        return mineral;
    }

    public char symbol() { return 'S'; }
    public boolean isPassable() { return false; }
}
