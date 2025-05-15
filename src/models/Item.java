package models;

import java.util.HashMap;

public class Item {
    private int id;
    private String name;
    private String type;
    private int basePrice;
    private int quantity;
    private int storePrice;
    private HashMap<String, Object> properties = new HashMap<>();

    public boolean isEdible() {
        return "Food".equals(type) || "Ingredient".equals(type);
    }

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

    public void setType(String type) {
        this.type = type;
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

    public void use() {
    }
}
