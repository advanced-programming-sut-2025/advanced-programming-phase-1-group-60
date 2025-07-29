package com.StardewValley.models;

public class SellingBin implements StaticElement {
    @Override
    public char symbol() {
        return 'B'; // B for Bin
    }

    @Override
    public boolean isPassable() {
        return false;
    }

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
