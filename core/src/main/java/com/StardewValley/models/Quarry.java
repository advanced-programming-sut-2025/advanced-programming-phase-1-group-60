package com.StardewValley.models;

public class Quarry implements StaticElement {
    public char symbol() { return 'Q'; }
    public boolean isPassable() { return false; }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
