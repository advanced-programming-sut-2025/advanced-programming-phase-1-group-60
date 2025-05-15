package models;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalTime;
import java.util.Map;

public class Store implements StaticElement {

    private String name;
    private String type;
    private String creator;
    private WorkTime workTime;
    private List<Item> items = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private int upgradeLevel;
    private int leftCornerX;
    private int leftCornerY;

    public Store(String name, int leftCornerX, int leftCornerY) {
        this.name = name;
        this.leftCornerX = leftCornerX;
        this.leftCornerY = leftCornerY;
    }

    Map<Integer, Integer> upgradeCosts = new HashMap<>(); // for blacksmith
    Map<Integer, Integer> upgradeBinsCosts = new HashMap<>(); // for blacksmith
    public Map<Integer, Integer> soldUpgrades = new HashMap<>(); // برای پیگیری فروش ابزارها
    public Map<Integer, Integer> soldBinsUpgrades = new HashMap<>(); // برای پیگیری فروش سطلها

    // marin'sRanch
    boolean isHaySoldToday;
    public List<Animal> animals = new ArrayList<>();

    // برای Carpenter'sShop
    public Map<String, Integer> soldBuildings = new HashMap<>(); // فروش روزانه

    // برای Fish Shop
    public Map<Integer, Integer> upgradePoleCosts = new HashMap<>();
    public Map<Integer, Integer> soldPoleUpgrades = new HashMap<>();



    public boolean isOpen(LocalTime currentTime) {
        int hour = currentTime.getHour();
        return hour >= workTime.getOpenTime() && hour <= workTime.getCloseTime();
    }

    public Result purchaseProduct(User player, String input) {
        Result result = new Result();
        switch (name) {
            case "Blacksmith" -> {
                // buy -p productName -n amount
                String[] parts = input.split(" ");
                String product = parts[2];
                int quantity = Integer.parseInt(parts[4]);
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
                                player.setMoney(player.getMoney() - newItem.getStorePrice());
                                result.setSuccess(true);
                                break;
                            }
                        }
                    }
                }
            }
            case "Carpenter'sShop" -> {
                // buy -p productName
                String[] parts = input.split(" ");
                String product = parts[2];
                if (parts.length > 3) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(parts[2]).append(" ").append(parts[3]);
                    product = sb.toString();
                }
                for (Item item : items) {
                    if (item.getName().equals(product)) {
                        int sold = soldBuildings.getOrDefault(product, 0);
                        if (sold >= 1) {
                            result.setMessage("sold out");
                            return result;
                        }


                        if (player.getMoney() < item.getStorePrice()) {
                            result.setMessage("not enough money");
                            return result;
                        }

                        // بررسی مواد اولیه
                        Map<String, Integer> materials = (Map<String, Integer>) item.getProperties().get("materials");
                        Map<String, Integer> lowerCaseMap = new HashMap<>();
                        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
                            String lowerKey = entry.getKey().toLowerCase();
                            lowerCaseMap.put(lowerKey, entry.getValue());
                        }
                        materials = lowerCaseMap;
                        boolean hasAllMaterials = true;
                        List<Item> requiredItems = new ArrayList<>();

                        // جمع‌آوری مواد مورد نیاز
                        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
                            String materialName = entry.getKey();
                            int needed = entry.getValue();
                            boolean found = false;

                            for (Item invItem : player.getInventory().getItems()) {
                                if (invItem.getName().equalsIgnoreCase(materialName)) {
                                    if (invItem.getQuantity() >= needed) {
                                        requiredItems.add(invItem);
                                        found = true;
                                        break;
                                    }
                                }
                            }

                            if (!found) {
                                hasAllMaterials = false;
                                break;
                            }
                        }

                        if (hasAllMaterials) {
                            // کسر پول
                            player.setMoney(player.getMoney() - item.getStorePrice());

                            // کسر مواد اولیه
                            for (Item invItem : requiredItems) {
                                int needed = materials.get(invItem.getName());
                                invItem.setQuantity(invItem.getQuantity() - needed);
                            }

                            // ایجاد ساختمان
                            if (item.getName().contains("Barn")) {
                                Barn barn = new Barn(
                                        item.getName(),
                                        getCapacity(item.getName()),
                                        (int) item.getProperties().get("width"),
                                        (int) item.getProperties().get("height")
                                );
                                player.addAnimalPlace(barn);
                            } else if (item.getName().contains("Coop")) {
                                Coop coop = new Coop(
                                        item.getName(),
                                        getCapacity(item.getName()),
                                        (int) item.getProperties().get("width"),
                                        (int) item.getProperties().get("height")
                                );
                                player.addAnimalPlace(coop);
                            }

                            // ثبت فروش
                            soldBuildings.put(product, 1);
                            result.setSuccess(true);
                            result.setMessage("bought " + product);
                            return result;
                        } else {
                            result.setMessage("not enough materials");
                        }
                    }
                }
            }
            case "Marin'sRanch" -> {
                //buy -p productName [-n amount | -n name]
                String[] parts = input.split(" ");
                String product = parts[2];
                switch (product) {
                    case "Hay" , "Milk Pail" , "Shears" -> {
                        int quantity = Integer.parseInt(parts[4]);
                        Item itemTOsell = null;
                        for (Item item : items) {
                            if (item.getName().equals(product)) {
                                itemTOsell = item;
                            }
                        }
                        if (itemTOsell == null) {
                            result.setMessage(product + " not available");
                            result.setSuccess(false);
                            return result;
                        }

                        if (player.getMoney() < quantity * itemTOsell.getStorePrice()) {
                            result.setMessage("not enough money");
                            result.setSuccess(false);
                            return result;
                        }
                        itemTOsell.setQuantity(quantity);
                        player.getInventory().addItem(itemTOsell);
                        player.setMoney(player.getMoney() - quantity * itemTOsell.getStorePrice());
                        result.setSuccess(true);
                        result.setMessage("bought " + product);
                        return result;
                    }
                    default -> {
                        String name = parts[4];
                        Animal animalToSell = null;
                        for (Animal a : animals) {
                            if (a.getType().equals(product)) {
                                animalToSell = a;
                                break;
                            }
                        }
                        if (animalToSell == null) {
                            result.setMessage(product + " not available");
                            result.setSuccess(false);
                            return result;
                        }

                        if (player.getMoney() < animalToSell.getPrice()) {
                            result.setMessage("not enough money");
                            result.setSuccess(false);
                            return result;
                        }
                        animalToSell.setOwner(player);
                        animalToSell.setName(name);
                        player.getAnimals().add(animalToSell);
                        result.setSuccess(true);
                        result.setMessage("bought " + product);
                        return result;
                    }

                }
            }
        }
        result.setSuccess(false);
        return result;
    }


    private int getCapacity(String buildingName) {
        return switch (buildingName) {
            case "Barn" -> 4;
            case "Big Barn" -> 8;
            case "Deluxe Barn" -> 12;
            case "Coop" -> 4;
            case "Big Coop" -> 8;
            case "Deluxe Coop" -> 12;
            default -> 0;
        };
    }


    public boolean sellProduct(User player, Product product, int quantity) {
        return false;
    }

    ;

    public boolean upgradeStore(User player) {
        return false;
    }


    ;

    public String getName() {
        return name;
    }

    public int getLeftCornerX() {
        return leftCornerX;
    }

    public int getLeftCornerY() {
        return leftCornerY;
    }

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

    public void setUpgradeCosts(Map<Integer, Integer> upgradeCosts) {
        this.upgradeCosts = upgradeCosts;
    }

    public void setUpgradeBinsCosts(Map<Integer, Integer> upgradeBinsCosts) {
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

    // use only for blacksmith and Fish shop store
    public Result upgradeTools(User player, int level, Tools tool) {
        Result result = new Result();
        String toolName = tool.getName();

        if (toolName.toLowerCase().contains("rod") || toolName.toLowerCase().contains("pole")) {
            if (tool != null) {
                int currentLevel = switch (tool.getFishingpoleStage()) {
                    case BAMBO -> 1;
                    case FIBERGLASS -> 2;
                    case IRIDIUM -> 3;
                    default -> 0;
                };
                if (currentLevel != level - 1) {
                    result.setMessage("upgrade level by level");
                    result.setSuccess(false);
                    return result;
                }

                if (player.getMoney() <= upgradeCosts.get(level)) {
                    result.setMessage("not enough money");
                    result.setSuccess(false);
                    return result;
                }

                if ((toolName.toLowerCase().contains("fiberglass") && player.getFishingSkills() < 2) ||
                        toolName.toLowerCase().contains("iridium") && player.getFishingSkills() < 4) {
                    result.setMessage("not enough skills");
                    result.setSuccess(false);
                    return result;
                }

                int soldToday = soldPoleUpgrades.getOrDefault(level, 0);
                if (soldToday >= 1) {
                    result.setMessage("This fishing pole upgrade is sold out for today.");
                    result.setSuccess(false);
                    return result;
                }

                soldPoleUpgrades.put(level, soldToday + 1);
                player.setMoney(player.getMoney() - upgradeCosts.get(level));
                result.setSuccess(true);
                result.setMessage("bought " + toolName);
                return result;
            } else {
                if (level != 0) {
                    result.setMessage("first buy training");
                    result.setSuccess(false);
                    return result;
                }

                if (player.getMoney() <= upgradeCosts.get(level)) {
                    result.setMessage("not enough money");
                    result.setSuccess(false);
                    return result;
                }

                int soldToday = soldPoleUpgrades.getOrDefault(level, 0);
                if (soldToday >= 1) {
                    result.setMessage("This fishing pole is sold out for today.");
                    result.setSuccess(false);
                    return result;
                }
                soldPoleUpgrades.put(level, soldToday + 1);
                player.setMoney(player.getMoney() - upgradeCosts.get(level));
                result.setSuccess(true);
                result.setMessage("bought " + toolName);
                return result;
            }
        }

        boolean isBin = toolName.equalsIgnoreCase("Trash can");
        if (isBin) {
            int sold = soldBinsUpgrades.getOrDefault(level, 0);
            if (sold >= 1) {
                result.setMessage("This bin upgrade has already been sold today.");
                return result;
            }
        } else {
            int sold = soldUpgrades.getOrDefault(level, 0);
            if (sold >= 1) {
                result.setMessage("This tool upgrade has already been sold today.");
                return result;
            }
        }

        int cost = 0;
        for (Integer grade : upgradeCosts.keySet()) {
            if (grade == level) {
                if (toolName.equals("Trash can")) {
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
            case 1:
                neededIngredient = "copperBar";
                break;
            case 2:
                neededIngredient = "ironBar";
                break;
            case 3:
                neededIngredient = "goldBar";
                break;
            case 4:
                neededIngredient = "iridiumBar";
        }

        boolean hasIngredient = false;

        for (Item item : player.getInventory().getItems()) {
            if (item.getName().equals(neededIngredient)) {
                if (item.getQuantity() >= 5) {
                    hasIngredient = true;
                    ingredient = item;
                    break;
                }
            }
        }

        if (player.getMoney() >= cost && tool.getToolLevel() == (level - 1) && !hasIngredient) {
            tool.upgrade();
            result.setSuccess(true);
            player.setMoney(player.getMoney() - cost);
            result.setMessage("Upgrade successful");
            if (isBin) {
                soldBinsUpgrades.put(level, soldBinsUpgrades.getOrDefault(level, 0) + 1);
            } else {
                soldUpgrades.put(level, soldUpgrades.getOrDefault(level, 0) + 1);
            }
            return result;
        }

        result.setSuccess(false);
        result.setMessage("Upgrade failed");
        return result;
    }


    public String showAllProducts() {
        StringBuilder output = new StringBuilder();
        switch (name) {
            case "Blacksmith" -> {
                output.append("Blacksmith :\n");
                output.append("minerals :\n");
                for (Item item : items) {
                    output.append(item.getName()).append(" ").append(item.getStorePrice()).append("g").append("\n");
                }
                output.append("tools upgrades\n").append("Copper Tool\n").append("Steel Tool\n").append("Gold Tool\n")
                        .append("Iridium Tool\n").append("Copper Trash Can\n").append("Steel Trash Can\n")
                        .append("Gold Trash Can\n").append("Iridium Trash Can\n");
            }
            case "Marin'sRanch" -> {
                output.append("Marin'sRanch :\n");
                output.append("""
                        animals :
                        Chicken 800g
                        Cow 1500g
                        Goat 4000g
                        Duck 1200g
                        Sheep 8000g
                        Rabbit 8000g
                        Dinosaur 14000g
                        Pig 16000g""");
                output.append("\nitems :\n");
                for (Item item : items) {
                    output.append(item.getName()).append(" ").append(item.getStorePrice()).append("g").append("\n");
                }
            }
            case "Carpenter'sShop" -> {
                output.append("Carpenter's Shop:\n");
                for (Item item : items) {
                    Map<String, Integer> materials = (Map<String, Integer>) item.getProperties().get("materials");
                    output.append(item.getName())
                            .append(" price: ").append(item.getStorePrice()).append("g")
                            .append("materials: ").append(materials)
                            .append("\n");
                }
            }
        }
        return output.toString();
    }

    public String showAvailableProducts() {
        StringBuilder output = new StringBuilder();
        switch (name) {
            case "Blacksmith" -> {
                output.append("Blacksmith :\n");
                output.append("minerals :");
                for (Item item : items) {
                    output.append(item.getName()).append(" ").append(item.getStorePrice()).append("g").append("\n");
                }
                output.append("\nAvailable Tool Upgrades:\n");
                for (int level : upgradeCosts.keySet()) {
                    if (soldUpgrades.getOrDefault(level, 0) == 0) {
                        output.append(getUpgradeName(level, false))
                                .append(" ").append(upgradeCosts.get(level)).append("g\n");
                    }
                }

                output.append("\nAvailable Trash Can Upgrades:\n");
                for (int level : upgradeBinsCosts.keySet()) {
                    if (soldBinsUpgrades.getOrDefault(level, 0) == 0) {
                        output.append(getUpgradeName(level, true))
                                .append(" ").append(upgradeBinsCosts.get(level)).append("g\n");
                    }
                }
            }
            case "Marin'sRanch" -> {
                output.append("Marin'sRanch :\n");
                output.append("""
                        animals :
                        Chicken 800g
                        Cow 1500g
                        Goat 4000g
                        Duck 1200g
                        Sheep 8000g
                        Rabbit 8000g
                        Dinosaur 14000g
                        Pig 16000g""");
                output.append("items :\n");
                for (Item item : items) {
                    if (!item.getName().equalsIgnoreCase("Hay") || !isHaySoldToday) {
                        output.append(item.getName()).append(" ").append(item.getStorePrice()).append("g").append("\n");
                    }
                }
            }
            case "Carpenter'sShop" -> {
                output.append("Carpenter's Shop:\n");
                for (Item item : items) {
                    int sold = soldBuildings.getOrDefault(item.getName(), 0);
                    if (sold < 1) {
                        Map<String, Integer> materials = (Map<String, Integer>) item.getProperties().get("materials");
                        output.append(item.getName())
                                .append("price:").append(item.getStorePrice()).append("g")
                                .append("materials: ").append(materials)
                                .append("\n");
                    }
                }
            }
        }
        return output.toString();
    }

    private String getUpgradeName(int level, boolean isBin) {
        return switch (level) {
            case 1 -> "Copper " + (isBin ? "Trash Can" : "Tool");
            case 2 -> "Steel " + (isBin ? "Trash Can" : "Tool");
            case 3 -> "Gold " + (isBin ? "Trash Can" : "Tool");
            case 4 -> "Iridium " + (isBin ? "Trash Can" : "Tool");
            default -> "Unknown";
        };
    }

}