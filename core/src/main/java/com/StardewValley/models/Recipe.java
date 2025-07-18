package com.StardewValley.models;

import java.util.HashMap; // <-- IMPORTANT: Add this import
import java.util.Map;

public class Recipe {
    private String name;
    private Map<String, Integer> ingredients;
    private int energy;
    private String source;
    private int sellPrice;

    public Recipe(String name, Map<String, Integer> ingredients, int energy, String source, int sellPrice) {
        this.name = name;
        // >>> FIX HERE <<<
        // If the 'ingredients' map passed to the constructor is null,
        // initialize it to an empty HashMap instead.
        this.ingredients = (ingredients != null) ? ingredients : new HashMap<>();
        this.energy = energy;
        this.source = source;
        this.sellPrice = sellPrice;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, Integer> getIngredients() { return ingredients; }

    public void setIngredients(Map<String, Integer> ingredients) {
        // >>> FIX HERE <<<
        // If the 'ingredients' map passed to the setter is null,
        // ensure it's set to an empty HashMap instead of null.
        this.ingredients = (ingredients != null) ? ingredients : new HashMap<>();
    }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public int getSellPrice() { return sellPrice; }
    public void setSellPrice(int sellPrice) { this.sellPrice = sellPrice; }
}
