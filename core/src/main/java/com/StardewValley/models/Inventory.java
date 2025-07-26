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
    private Tools.TrashbinStage trashCanStage = Tools.TrashbinStage.BEGINNER;
    private Item[] quickAccessSlots = new Item[6];

    public Inventory(InventoryType type, User owner) {
        this.type = type;
        this.owner = owner;
        items = new ArrayList<>();
        switch (type) {
            case NORMAL -> capacity = 12;
            case BIG -> capacity = 24;
            case DELUXE -> capacity = 36;
        }
    }
    public List<Item> getItems() {
        return items;
    }

    public enum InventoryType {
        NORMAL, BIG, DELUXE
    }

    public Item[] getQuickAccessSlots() {
        return quickAccessSlots;
    }
    public void setQuickAccessSlot(int index, Item item) {
        if (index >= 0 && index < 6) {
            quickAccessSlots[index] = item;
        }
    }
    public Item getQuickAccessSlot(int index) {
        if (index >= 0 && index < 6) {
            return quickAccessSlots[index];
        }
        return null;
    }
    public void clearQuickAccessSlot(int index) {
        if (index >= 0 && index < 6) {
            quickAccessSlots[index] = null;
        }
    }

    public void setTrashCanStage(Tools.TrashbinStage stage) {
        this.trashCanStage = stage;
    }

    public Tools.TrashbinStage getTrashCanStage() {
        return this.trashCanStage;
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
            case DELUXE -> capacity = 36;
        }
    }

    public boolean addItem(Item item) {
        if (item instanceof Tools) {
            if (items.size() < capacity) {
                items.add(item);
                return true;
            } else {
                return false;
            }
        }
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(item.getName())) {
                i.setQuantity(i.getQuantity() + item.getQuantity());
                return true;
            }
        }
        if (items.size() < capacity) {
            items.add(item);
            return true;
        } else {
            return false;
        }
    }

    public boolean tryAddItem(Item item) {
        if (item instanceof Tools) {
            if (items.size() < capacity) {
                items.add(item);
                return true;
            } else {
                System.out.println("Inventory is full!");
                return false;
            }
        }
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(item.getName())) {
                i.setQuantity(i.getQuantity() + item.getQuantity());
                return true;
            }
        }
        if (items.size() < capacity) {
            items.add(item);
            return true;
        } else {
            System.out.println("Inventory is full!");
            return false;
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
        if (items.size() < capacity) {
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
                return false;
            }
        }
        return true;
    }

    public void removeMaterials(Map<String, Integer> requiredMaterials) {
        for (Map.Entry<String, Integer> entry : requiredMaterials.entrySet()) {
            String materialName = entry.getKey();
            int quantityToRemove = entry.getValue();

            removeItemByName(materialName, quantityToRemove);
        }
    }

    public String trashItem(Item item) {
        double refundRate = 0;
        switch (trashCanStage) {
            case COPPER: refundRate = 0.10; break;
            case STEEL: refundRate = 0.30; break;
            case GOLD: refundRate = 0.50; break;
            case IRIDIUM: refundRate = 0.70; break;
            default: break;
        }

        int price = 0;
        if (item.getStorePrice() > 0) {
            price = item.getStorePrice();
        } else if (item.getSellPrice() > 0) {
            price = item.getSellPrice();
        } else if (item.getBasePrice() > 0) {
            price = item.getBasePrice();
        } else {
            price = 10;
        }

        int refundAmount = (int) (price * refundRate);
        if (owner != null) {
            owner.setMoney(owner.getMoney() + refundAmount);
        }

        removeItem(item);

        if (refundAmount > 0) {
            return "Item trashed. You received " + refundAmount + "g back.";
        } else {
            return "Item trashed.";
        }
    }
}
