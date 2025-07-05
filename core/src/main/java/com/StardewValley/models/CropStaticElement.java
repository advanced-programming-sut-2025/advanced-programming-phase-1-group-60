package com.StardewValley.models;

public class CropStaticElement implements StaticElement {
    @Override
    public char symbol() { return '*'; }
    @Override
    public boolean isPassable() { return true; }
}
