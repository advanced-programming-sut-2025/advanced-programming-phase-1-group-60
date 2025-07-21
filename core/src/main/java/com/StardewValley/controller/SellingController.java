package com.StardewValley.controller;

import com.StardewValley.models.Item;
import com.StardewValley.models.User;

public class SellingController {
    private static SellingController instance;

    private SellingController() {}

    public static SellingController getInstance() {
        if (instance == null) {
            instance = new SellingController();
        }
        return instance;
    }

    public static int getItemSellPrice(Item item) {
        if (item.getSellPrice() > 0) {
            return item.getSellPrice();
        } else if (item.getStorePrice() > 0) {
            return item.getStorePrice() / 2;
        } else if (item.getBasePrice() > 0) {
            return item.getBasePrice() / 2;
        } else {
            return 10;
        }
    }

    public String sellItem(User player, String itemName, int quantity) {
        Item itemToSell = player.getInventory().getItem(itemName);

        if (itemToSell == null || player.getInventory().getItemQuantityByName(itemName) < quantity) {
            return "You do not have enough of this item to sell.";
        }

        int pricePerItem = getItemSellPrice(itemToSell);

        int totalSale = pricePerItem * quantity;
        player.setMoney(player.getMoney() + totalSale);
        player.getInventory().removeItemByName(itemName, quantity);

        return "You sold " + quantity + " " + itemName + " for " + totalSale + "g.";
    }
}
