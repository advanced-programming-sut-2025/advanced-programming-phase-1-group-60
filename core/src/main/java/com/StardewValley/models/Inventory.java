package com.StardewValley.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Inventory {
    private List<Item> items;
    private int capacity;
    private int currentSize;
    private InventoryType type;
    private User owner;

    public Inventory(InventoryType type) {
        this.type = type;
        items = new ArrayList<>();
        switch (type) {
            case NORMAL -> capacity = 12;
            case BIG -> capacity = 24;
            case DELUXE -> capacity = Integer.MAX_VALUE;
        }
    }
    public List<Item> getItems() {
        return items;
    }

    public enum InventoryType {
        NORMAL, BIG, DELUXE
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public InventoryType getType() {
        return type;
    }

    public void setType(InventoryType type) {
        this.type = type;
        switch (type) {
            case NORMAL -> capacity = 12;
            case BIG -> capacity = 24;
            case DELUXE -> capacity = Integer.MAX_VALUE;
        }
    }

    public boolean addItem(Item item) {
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(item.getName())) {
                i.setQuantity(i.getQuantity() + item.getQuantity());
                return true;
            }
        }
        if (type == InventoryType.DELUXE || items.size() < capacity) {
            items.add(item);
            return true;
        } else {
            return false;
        }
    }

    public boolean tryAddItem(Item item) {
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(item.getName())) {
                i.setQuantity(i.getQuantity() + item.getQuantity());
                return true; // آیتم با موفقیت اضافه شد
            }
        }
        if (type == InventoryType.DELUXE || items.size() < capacity) {
            items.add(item);
            return true; // آیتم با موفقیت اضافه شد
        } else {
            System.out.println("Inventory is full!");
            return false; // موجودی پر است
        }
    }

    public void removeItem(Item item) {
        for (int idx = 0; idx < items.size(); idx++) {
            Item i = items.get(idx);
            if (i.getName().equalsIgnoreCase(item.getName())) {
                if (i.getQuantity() > item.getQuantity()) {
                    i.setQuantity(i.getQuantity() - item.getQuantity());
                } else {
                    items.remove(idx);
                }
                return;
            }
        }
    }

    public void addItemByName(String itemName, int count) {
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(itemName)) {
                i.setQuantity(i.getQuantity() + count);
                return;
            }
        }
        if (type == InventoryType.DELUXE || items.size() < capacity) {
            Item i = new Item();
            i.setName(itemName);
            i.setQuantity(count);
            items.add(i);
        } else {
            System.out.println("Inventory is full!");
        }
    }


    public void removeItemByName(String itemName, int count) {
        String normalized = itemName.trim().toLowerCase();
        for (int idx = 0; idx < items.size(); idx++) {
            Item i = items.get(idx);
            if (i.getName().trim().toLowerCase().equals(normalized)) {
                if (i.getQuantity() > count) {
                    i.setQuantity(i.getQuantity() - count);
                } else {
                    items.remove(idx);
                }
                return;
            }
        }
    }


    public boolean hasItem(String itemName, int count) {
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(itemName) && i.getQuantity() >= count) {
                return true;
            }
        }
        return false;
    }


    public int getItemCount(Item item) {
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(item.getName())) {
                return i.getQuantity();
            }
        }
        return 0;
    }


    public HashMap<Item, Integer> getAllItems() {
        HashMap<Item, Integer> map = new HashMap<>();
        for (Item i : items) {
            map.put(i, i.getQuantity());
        }
        return map;
    }

    public Item getItem(String itemName) {
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(itemName)) {
                return i;
            }
        }
        return null;
    }

    public int getItemQuantityByName(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public boolean hasMaterials(Map<String, Integer> requiredMaterials) {
        for (Map.Entry<String, Integer> entry : requiredMaterials.entrySet()) {
            String materialName = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (!hasItem(materialName, requiredQuantity)) {
                return false; // اگر حتی یک ماده اولیه کافی نباشد
            }
        }
        return true;
    }

    public void removeMaterials(Map<String, Integer> requiredMaterials) {
        for (Map.Entry<String, Integer> entry : requiredMaterials.entrySet()) {
            String materialName = entry.getKey();
            int quantityToRemove = entry.getValue();

            removeItemByName(materialName, quantityToRemove); // حذف مواد اولیه
        }
    }
}
