package models;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalTime;

public class Store implements StaticElement{

    private String name;
    private String type;
    private String creator;
    private WorkTime workTime;
    private List<Item> items = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private int upgradeLevel;

    public Store(String name) {
        this.name = name;
    }

    HashMap<Integer, Integer> upgradeCosts = new HashMap<>(); // for blacksmith
    HashMap<Integer, Integer> upgradeBinsCosts = new HashMap<>(); // for blacksmith

    public boolean isOpen(LocalTime currentTime) {
        int hour = currentTime.getHour();
        return hour >= workTime.getOpenTime() && hour <= workTime.getCloseTime();
    }

    public Result purchaseProduct(User player, String product, int quantity) {
        Result result = new Result();
        if (items != null) {
            for (Item item : items) {
                if (item.getName().equals(product)) {
                    if (player.getMoney() >= (item.getBasePrice() * quantity)) {
                        Item newItem = new Item();
                        newItem.setName(product);
                        newItem.setQuantity(quantity);
                        newItem.setBasePrice(item.getBasePrice());
                        newItem.setBasePrice(item.getBasePrice());
                        newItem.setType(item.getType());
                        newItem.setProperties(item.getProperties());
                        player.addItem(item);
                        player.setMoney(player.getMoney() - newItem.getPrice());
                        result.setSuccess(true);
                        break;
                    }
                }
            }
        }
        result.setSuccess(false);
        return result;
    }

    ;


    public boolean sellProduct(User player, Product product, int quantity) {
        return false;
    }

    ;

    public boolean upgradeStore(User player) {
        return false;
    }


    ;

    public boolean hasProductInStock(Product product, int quantity) {
        return false;
    }

    ;

    public void addProduct(Product product) {
    }

    public void removeProduct(Product product) {
        return;
    }

    public String getType() {
        return null;
    }

    public Npc getCreator() {
        return null;
    }

    public WorkTime getWorkTime() {
        return null;
    }

    ;

    public List<Product> getAvailableProducts() {
        return null;
    }

    public int getUpgradeLevel() {
        return 0;
    }

    ;

    public boolean isOpen(LocalTime time, DayOfWeek day) {
        return false;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setUpgradeCosts(HashMap<Integer, Integer> upgradeCosts) {
        this.upgradeCosts = upgradeCosts;
    }

    public void setUpgradeBinsCosts(HashMap<Integer, Integer> upgradeBinsCosts) {
        this.upgradeBinsCosts = upgradeBinsCosts;
    }

    @Override
    public char symbol() {
        return 'S';
    }

    @Override
    public boolean isPassable() {
        return false;
    }


    ;


    private class WorkTime {
        private int openTime;
        private int closeTime;
        private boolean[] workingDays;

        public int getOpenTime() {
            return openTime;
        }

        public int getCloseTime() {
            return closeTime;
        }

        public boolean[] getWorkingDays() {
            return workingDays;
        }


    }

    // use only for blacksmith store
    public Result upgradeTools(User player, int level, String toolType) {
        Result result = new Result();

        for (Tools tool : player.getTools()) {
            if (tool.getType().equals(toolType)) {
                int cost = 0;
                for (Integer grade : upgradeCosts.keySet()) {
                    if (grade == level) {
                        if (toolType.equals("Trash can")) {
                            cost += upgradeBinsCosts.get(grade);
                        } else {
                            cost = upgradeCosts.get(grade);
                        }
                        break;
                    }
                }
                String neededIngredient = "";
                Item ingredient = null;
                switch (level) {
                    case 1: neededIngredient = "copperBar";
                    case 2: neededIngredient = "ironBar";
                    case 3: neededIngredient = "goldBar";
                    case 4: neededIngredient = "iridiumBar";
                }

                boolean hasIngredient = false;

                for (Item item : player.getInventory().getItems()) {
                    if (item.getType().equals(neededIngredient)) {
                        if (item.getQuantity() >= 5) {
                            hasIngredient = true;
                            ingredient = item;
                            break;
                        }
                    }
                }

                if (player.getMoney() >= cost && tool.getToolLevel() == (level - 1) && hasIngredient) {
                    tool.upgrade();
                    result.setSuccess(true);
                    ingredient.setQuantity(ingredient.getQuantity() - 5);
                    player.setMoney(player.getMoney() - cost);
                    result.setMessage("Upgrade successful");
                    break;
                }
            }
        }
        result.setSuccess(false);
        result.setMessage("Upgrade failed");
        return result;
    }


}