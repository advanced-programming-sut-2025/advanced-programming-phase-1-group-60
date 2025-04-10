package models;

import java.util.List;

public class Inventory {
    private Map<Item, Integer> items;
    private int capacity;
    private int currentSize;


    public Map<Item, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Item, Integer> items) {
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

    public boolean addItem(Item item, int count) {
    }


    public boolean removeItem(Item item, int count) {
    }


    public boolean hasItem(Item item, int count) {

    }


    public int getItemCount(Item item) {

    }


    public Map<Item, Integer> getAllItems() {

    }
}
}
