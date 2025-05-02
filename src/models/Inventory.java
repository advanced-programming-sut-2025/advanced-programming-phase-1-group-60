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

    public boolean addItem(Item item, int count) {
        return false;
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
