package com.StardewValley.models;

import java.util.List;
import java.util.stream.Collectors;

public class Fish {
    private String name;
    private int basePrice;
    private String quality;
    private String season;
    private boolean isLegendary;
    private float x; // NEW: X-coordinate for rendering
    private float y;

    public Fish(String name, int basePrice, String season, boolean isLegendary) {
        this.name = name;
        this.basePrice = basePrice;
        this.season = season;
        this.isLegendary = isLegendary;
    }
    // Getters
    public String getName() { return name; }
    public int getBasePrice() { return basePrice; }
    public String getSeason() { return season; }
    public boolean isLegendary() { return isLegendary; }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    // NEW: Setter for position
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Fish{" +
            "name='" + getName() + '\'' +
            ", basePrice=" + getBasePrice() +
            ", season='" + season + '\'' +
            ", isLegendary=" + isLegendary +
            ", x=" + x +
            ", y=" + y +
            '}';
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getQuality() {
        return quality;
    }
}
