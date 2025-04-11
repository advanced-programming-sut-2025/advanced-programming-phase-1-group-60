package controller;

import models.Inventory;
import models.Item;

public class InventoryController {
    private Inventory currentInventory;

    // Adding items
    // TODO : adding items to the inventory with their id and every attribute getting saved in a JSON file
    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }
        return true;
    }

    // Removing items
    // TODO : Remove the item with that id from the inventory and also update the JSON file
    public boolean removeItem(Item item) {
        if (item == null) {
            return false;
        }
        return true;
    }

    // Showing items
    // TODO : Show every item in the inventory (load the JSON file and show it)
    public Inventory showInventory() {
        return currentInventory;
    }

    // Checking inventory capacity
    // TODO : Check if the inventory has enough space to hold a new item
    private boolean hasCapacity() {
        return true;
    }
}