package com.StardewValley.models;

import java.util.HashMap;

public class Item {
    private int id;
    private String name;
    private String type;
    private int basePrice;
    private int quantity;
    private int storePrice;
    private boolean edible;
    private int energy;
    private int sellPrice;
    private HashMap<String, Object> properties = new HashMap<>();
    private User owner; // Added owner field
    private String path;

    public boolean isEdible() {
        return "Food".equals(type) || "Ingredient".equals(type);
    }

    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Item (String name, int quantity, String path) {
        this.name = name;
        this.quantity = quantity;
        this.path = path;
    }

    public Item() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public Item setType(String type) {
        this.type = type;
        return this;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public void setStorePrice(int storePrice) {
        this.storePrice = storePrice;
    }

    public int getStorePrice() {
        return storePrice;
    }

    public int getPrice() {
        return basePrice * quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public void setEdible(boolean edible) {
        this.edible = edible;
    }

    public int getEnergy() {
        return energy;
    }

    public int getSellPrice() {
        return sellPrice;
    }
    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public User getOwner() { // Added getOwner method
        return owner;
    }

    public void setOwner(User owner) { // Added setOwner method
        this.owner = owner;
    }

    public void use() {
    }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    @Override
    public String toString() {
        return name + " " + quantity;
    }
}
