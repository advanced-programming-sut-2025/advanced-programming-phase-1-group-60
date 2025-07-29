package com.StardewValley.models;

public class CoopStaticElement implements StaticElement {
    @Override
    public char symbol() { return 'O'; }
    @Override
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
