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
}
