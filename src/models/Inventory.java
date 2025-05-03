package models;

import java.util.HashMap;
import java.util.List;


public class Inventory {
    private List<Item> items;
    private int capacity;
    private int currentSize;
    private User owner;


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

    public void addItem(Item item, int count) {
        Item target = null;
        for (Item i : items) {
            if (i.getName().equals(item.getName())) {
                target = i;
            }
        }
        if (target == null) {
            items.add(item);
        }
        else {
            target.setQuantity(target.getQuantity() + count);
        }
    }


    public boolean removeItem(Item item, int count) {
        return false;
    }


    public boolean hasItem(Item item, int count) {
        return false;
    }


    public int getItemCount(Item item) {
        return 0;
    }


    public HashMap<Item, Integer> getAllItems() {
        return null;
    }
}
