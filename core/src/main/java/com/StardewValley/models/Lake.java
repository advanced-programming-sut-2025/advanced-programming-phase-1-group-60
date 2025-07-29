package com.StardewValley.models;

public class Lake implements StaticElement {
    public char symbol() { return 'L'; }
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
