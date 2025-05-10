package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Inventory {
    private List<Item> items;
    private int capacity;
    private int currentSize;
    private User owner;

    public Inventory() {
        items = new ArrayList<>(); // Ensure items is never null
    }
    public List<Item> getItems() {
        return items;
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

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public void addItem(String item, int count) {
        Item target = null;
        for (Item i : items) {
            if (i.getName().equals(item)) {
                target = i;
            }
        }
        if (target == null) {
            Item i = new Item();
            i.setName(item);
            i.setQuantity(count);
            items.add(i);
        }
        else {
            target.setQuantity(target.getQuantity() + count);
        }
    }


    public void removeItem(String item, int count) {
        for (Item i : items) {
            if (i.getName().equals(item)) {
                if (i.getQuantity() >= count) {
                    i.setQuantity(i.getQuantity() - count);
                }
                else {
                    items.remove(i);
                }
            }
        }
    }


    public boolean hasItem(String item, int count) {
        for (Item i : items) {
            if (i.getName().equals(item)) {
                if (i.getQuantity() >= count) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getItemCount(Item item) {
        return 0;
    }


    public HashMap<Item, Integer> getAllItems() {
        return null;
    }
}
