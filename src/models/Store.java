package models;

import java.util.*;

public class Store implements StaticElement {

    private String name;
    private String type;
    private String creator;
    public WorkTime workTime;
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
    public boolean isFishSmokerSold;
    public boolean isTroutSoupSold;

    // برای Stardrop saloon
    private List<String> availableRecipes = new ArrayList<>();
    public Map<String, Integer> soldRecipes = new HashMap<>();
    private Map<String, Integer> recipePrices = new HashMap<>();

    public void addFoodItem(Item food) {
        this.items.add(food);
    }

    public void addRecipe(String recipeName, int price) {
        this.availableRecipes.add(recipeName);
        this.recipePrices.put(recipeName.toLowerCase(), price);
    }

    public boolean isOpen() {
        int hour = TimeSystem.getInstance().getCurrentHour();
        return hour >= workTime.getOpenTime() && hour <= workTime.getCloseTime();
    }

    public Result purchaseProduct(User player, String input) {
        Result result = new Result();
        if (!isOpen()) {
            result.setMessage(name + " is closed");
            result.setSuccess(false);
            return result;
        }
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
                                int needed = materials.get(invItem.getName().toLowerCase());
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
                    case "Hay", "Milk Pail", "Shears" -> {
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
            case "The Stardrop Saloon" -> {
                // buy -p productName
                String[] parts = input.split(" ");

                int startIndex = -1;
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("-p")) {
                        startIndex = i + 1;
                        break;
                    }
                }

                int endIndex = parts.length;
                if (parts.length > 0 && parts[parts.length - 1].equals("Recipe")) {
                    endIndex = parts.length - 1;
                }

                String productName = String.join(" ", Arrays.copyOfRange(parts, startIndex, endIndex));

                if (parts[parts.length - 1].equals("Recipe")) {
                    return handleRecipePurchase(player, productName);
                } else {
                    return handleFoodPurchase(player, productName);
                }
            }
            case "Fish Shop" -> {
                // buy -p productName
                String[] parts = input.split(" ");

                int startIndex = -1;
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("-p")) {
                        startIndex = i + 1;
                        break;
                    }
                }

                int endIndex = parts.length;
                if (parts.length > 0 && parts[parts.length - 1].equals("Recipe")) {
                    endIndex = parts.length - 1;
                }

                String productName = String.join(" ", Arrays.copyOfRange(parts, startIndex, endIndex));

                if (parts[parts.length - 1].equals("Recipe")) {
                    if (player.getMoney() < 10_000) {
                        result.setMessage("not enough money");
                        result.setSuccess(false);
                        return result;
                    }
                    player.setMoney(player.getMoney() - 10000);
                    result.setSuccess(true);
                    player.learnRecipe(productName);
                    result.setMessage("bought " + productName + " recipe");
                    return result;
                }
                if (player.getMoney() < 250) {
                    result.setMessage("not enough money");
                    result.setSuccess(false);
                    return result;
                }
                Item foodItem = new Item();
                foodItem.setName(productName);
                foodItem.getProperties().put("energy", 20);
                foodItem.setType("Food");
                player.getInventory().addItem(foodItem);
                player.setMoney(player.getMoney() - 250);
                result.setSuccess(true);
                result.setMessage("Bought " + productName);
            }
        }
        result.setSuccess(false);
        return result;
    }

    private Result handleRecipePurchase(User player, String recipeName) {
        Result result = new Result();

        int soldToday = soldRecipes.getOrDefault(recipeName, 0);
        if (soldToday >= 1) {
            result.setMessage("Recipe sold out for today");
            result.setSuccess(false);
            return result;
        }

        if (!availableRecipes.contains(recipeName)) {
            result.setMessage("Recipe not available");
            result.setSuccess(false);
            return result;
        }

        int price = recipePrices.get(recipeName.toLowerCase());
        if (player.getMoney() < price) {
            result.setMessage("Not enough money");
            result.setSuccess(false);
            return result;
        }

        if (player.getCookRecipes().contains(recipeName)) {
            result.setMessage("Already know this recipe");
            result.setSuccess(false);
            return result;
        }

        player.setMoney(player.getMoney() - price);
        player.learnRecipe(recipeName);
        soldRecipes.put(recipeName, 1);

        result.setSuccess(true);
        result.setMessage("Learned " + recipeName + " recipe");
        return result;
    }

    private Result handleFoodPurchase(User player, String foodName) {
        Result result = new Result();

        Item foodItem = items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(foodName))
                .findFirst()
                .orElse(null);

        if (foodItem == null) {
            result.setMessage("Item not available");
            result.setSuccess(false);
            return result;
        }

        if (player.getMoney() >= foodItem.getStorePrice()) {
            player.getInventory().addItem(foodItem);
            player.setMoney(player.getMoney() - foodItem.getStorePrice());
            result.setSuccess(true);
            result.setMessage("bought " + foodName);
        } else {
            result.setMessage("Not enough money");
            result.setSuccess(false);
        }

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

    public void setUpgradeCosts(Map<Integer, Integer> upgradeCosts) {
        this.upgradeCosts = upgradeCosts;
    }

    public void setUpgradeBinsCosts(Map<Integer, Integer> upgradeBinsCosts) {
        this.upgradeBinsCosts = upgradeBinsCosts;
    }

    // use only for blacksmith and Fish shop store
    public Result upgradeTools(User player, int level, Tools tool) {
        Result result = new Result();
        String toolName = (tool != null) ? tool.getName() : "Training Rod";
        if (!isOpen()) {
            result.setMessage(name + " is closed");
            result.setSuccess(false);
            return result;
        }
        if (toolName.toLowerCase().contains("rod") || toolName.toLowerCase().contains("pole")) {
            if (tool != null) {
                int currentLevel = switch (tool.getFishingpoleStage()) {
                    case BAMBOO -> 1;
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
                    result.setMessage("buy training at first");
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

    private String getUpgradeName(int level, boolean isBin) {
        return switch (level) {
            case 1 -> "Copper " + (isBin ? "Trash Can" : "Tool");
            case 2 -> "Steel " + (isBin ? "Trash Can" : "Tool");
            case 3 -> "Gold " + (isBin ? "Trash Can" : "Tool");
            case 4 -> "Iridium " + (isBin ? "Trash Can" : "Tool");
            default -> "Unknown";
        };
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
            case "The Stardrop Saloon" -> {
                output.append("The Stardrop Saloon:\n");
                output.append("Food and Drink:\n");
                for (Item item : items) {
                    output.append(item.getName()).append(" ").append(item.getStorePrice()).append("g").append("\n");
                }
                output.append("Recipes :\n");
                for (String recipe : availableRecipes) {
                    output.append(recipe).append(" Recipe").append(" ").append(recipePrices.get(recipe.toLowerCase())).append("g").append("\n");
                }
            }
            case "Fish Shop" -> {
                output.append("Fish Shop:\n").append("Fishing Poles:\n").append("Training Rod: 25g\n")
                        .append("Bamboo Pole: 500g\n").append("Fiberglass Rod: 1800g\n").append("Iridium Rod: 7500g\n")
                        .append("Fish Smoker Recipe 10000g\n").append("Trout Soup 250g\n");
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
            case "The Stardrop Saloon" -> {
                output.append("The Stardrop Saloon:\n");
                output.append("Food and Drink:\n");
                for (Item item : items) {
                    output.append(item.getName()).append(" ").append(item.getStorePrice()).append("g").append("\n");
                }
                output.append("Recipes :\n");
                for (String recipe : availableRecipes) {
                    int sold = soldRecipes.getOrDefault(recipe, 0);
                    if (sold < 1) {
                        output.append(recipe).append(" Recipe").append(" ").append(recipePrices.get(recipe.toLowerCase())).append("g").append("\n");
                    }
                }
            }
            case "Fish Shop" -> {
                output.append("Fish Shop:\n");
                output.append("Fishing Poles:\n");
                if (soldPoleUpgrades.getOrDefault(0, 0) == 0) {
                    output.append("Training Rod: 25g\n");
                }
                if (soldPoleUpgrades.getOrDefault(1, 0) == 0) {
                    output.append("Bamboo Pole: 500g\n");
                }
                if (soldPoleUpgrades.getOrDefault(2, 0) == 0) {
                    output.append("Fiberglass Rod: 1800g\n");
                }
                if (soldPoleUpgrades.getOrDefault(3, 0) == 0) {
                    output.append("Iridium Rod: 7500g\n");
                }
                if (!isFishSmokerSold) output.append("Fish Smoker Recipe:\n");
                if (!isFishSmokerSold) output.append("Trout Soup:\n");
            }
        }
        return output.toString();
    }

    //GETTERS
    public String getName() {
        return name;
    }

    public int getLeftCornerX() {
        return leftCornerX;
    }

    public int getLeftCornerY() {
        return leftCornerY;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public char symbol() {
        return 'S';
    }

    @Override
    public boolean isPassable() {
        return false;
    }

    public static class WorkTime {
        private final int openTime;
        private final int closeTime;

        public WorkTime(int openTime, int closeTime) {
            this.openTime = openTime;
            this.closeTime = closeTime;
        }

        public int getOpenTime() {
            return openTime;
        }

        public int getCloseTime() {
            return closeTime;
        }
    }
}