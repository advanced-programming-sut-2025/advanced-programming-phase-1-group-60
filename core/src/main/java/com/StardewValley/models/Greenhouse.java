package com.StardewValley.models;

public class Greenhouse implements StaticElement {
    // Starts broken by default
    private boolean repaired = false;

    @Override
    public char symbol() { return 'G'; }

    @Override
    public boolean isPassable() { return false; }

    @Override
    public String getName() {
        return "Greenhouse";
    }

    // Matches the template placements (w=5, h=6)
    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight() {
        return 6;
    }

    // State
    public boolean isRepaired() {
        return repaired;
    }

    public void repair() {
        this.repaired = true;
    }
}
