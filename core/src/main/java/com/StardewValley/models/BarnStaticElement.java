package com.StardewValley.models;

public class BarnStaticElement implements StaticElement {
    @Override
    public char symbol() { return 'B'; }
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
