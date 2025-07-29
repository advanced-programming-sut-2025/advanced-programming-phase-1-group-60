package com.StardewValley.models;

public class Cabin implements StaticElement {
    public char symbol() { return 'C'; }
    public boolean isPassable() { return true; }

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
