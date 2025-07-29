package com.StardewValley.models;

import java.util.*;
import java.util.stream.Collectors;

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
    public Map<Integer, Integer> soldUpgrades = new HashMap<>(); // for blacksmith
    public Map<Integer, Integer> soldBinsUpgrades = new HashMap<>(); // for blacksmith

    // marin'sRanch
    boolean isHaySoldToday;
    public List<Animal> animals = new ArrayList<>();

    // for Carpenter'sShop
    public Map<String, Integer> soldBuildings = new HashMap<>();

    // for Fish Shop
    public Map<Integer, Integer> upgradePoleCosts = new HashMap<>();
    public Map<Integer, Integer> soldPoleUpgrades = new HashMap<>();
    public boolean isFishSmokerSold;
    public boolean isTroutSoupSold;

    // for Stardrop saloon
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
    public int getUpgradePrice(int level, boolean isBin) {
        if (isBin) {
            return upgradeBinsCosts.getOrDefault(level, 0);
        } else {
            return upgradeCosts.getOrDefault(level, 0);
        }
    }

    public boolean isUpgradeAvailable(User player, int level, boolean isBin) {
        int cost = getUpgradePrice(level, isBin);
        String requiredMaterial = getRequiredMaterialForUpgrade(level);
        return player.getMoney() >= cost && player.getInventory().hasItem(requiredMaterial, 5);
    }

    private String getRequiredMaterialForUpgrade(int level) {
        return switch (level) {
            case 1 -> "Copper_Bar";
            case 2 -> "Iron_Bar";
            case 3 -> "Gold_Bar";
            case 4 -> "Iridium_Bar";
            default -> "";
        };
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
                String[] parts = input.split(" ");
                String product = parts[2];
                int quantity = Integer.parseInt(parts[4]);
                if (items != null) {
                    for (Item item : items) {
                        if (item.getName().equals(product)) {
                            if (player.getMoney() >= (item.getStorePrice() * quantity)) {
                                Item newItem = new Item();
                                newItem.setName(product);
                                newItem.setQuantity(quantity);
                                newItem.setStorePrice(item.getStorePrice());
                                newItem.setBasePrice(item.getBasePrice());
                                newItem.setType(item.getType());
                                newItem.setProperties(item.getProperties());
                                newItem.setPath(item.getPath());
                                player.getInventory().addItem(newItem);
                                player.setMoney(player.getMoney() - (newItem.getStorePrice()*quantity));
                                result.setSuccess(true);
                                result.setMessage("Bought " + quantity + " " + product);
                                return result;
                            } else {
                                result.setSuccess(false);
                                result.setMessage("Not enough money.");
                                return result;
                            }
                        }
                    }
                }
            }
            case "Carpenter'sShop" -> {
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

                        Map<String, Integer> materials = (Map<String, Integer>) item.getProperties().get("materials");
                        Map<String, Integer> lowerCaseMap = new HashMap<>();
                        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
                            String lowerKey = entry.getKey().toLowerCase();
                            lowerCaseMap.put(lowerKey, entry.getValue());
                        }
                        materials = lowerCaseMap;
                        boolean hasAllMaterials = true;
                        List<Item> requiredItems = new ArrayList<>();

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
                            player.setMoney(player.getMoney() - item.getStorePrice());

                            for (Item invItem : requiredItems) {
                                int needed = materials.get(invItem.getName().toLowerCase());
                                invItem.setQuantity(invItem.getQuantity() - needed);
                            }

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
                String[] parts = input.split(" ");

                int startIndex = -1;
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("-p")) {
                        startIndex = i + 1;
                        break;
                    }
                }

                int endIndex = parts.length;
                if (parts.length > 0 && parts[parts.length - 1].equalsIgnoreCase("Recipe")) {
                    endIndex = parts.length - 1;
                }

                String productName = String.join(" ", Arrays.copyOfRange(parts, startIndex, endIndex));

                if (input.toLowerCase().endsWith("recipe")) {
                    return handleRecipePurchase(player, productName);
                } else {
                    return handleFoodPurchase(player, productName);
                }
            }
            case "Fish Shop" -> {
                String[] parts = input.split(" ");
                String productName = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length));

                if (productName.contains("Recipe")) {
                    productName = productName.replace(" Recipe", "").trim();
                    if (isFishSmokerSold) {
                        result.setMessage("Recipe sold out for today");
                        result.setSuccess(false);
                        return result;
                    }
                    if (player.getMoney() < 10000) {
                        result.setMessage("not enough money");
                        result.setSuccess(false);
                        return result;
                    }
                    player.setMoney(player.getMoney() - 10000);
                    player.learnRecipe(productName);
                    isFishSmokerSold = true;
                    result.setSuccess(true);
                    result.setMessage("bought " + productName + " recipe");
                    return result;
                } else { // It's food
                    if (isTroutSoupSold) {
                        result.setMessage("Food sold out for today");
                        result.setSuccess(false);
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
                    foodItem.setQuantity(1);
                    foodItem.setPath("assets/Inventory/Food/Trout_Soup.png");
                    player.getInventory().addItem(foodItem);
                    player.setMoney(player.getMoney() - 250);
                    isTroutSoupSold = true;
                    result.setSuccess(true);
                    result.setMessage("Bought " + productName);
                    return result;
                }
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

        String lowerCaseRecipeName = recipeName.toLowerCase();
        if (!availableRecipes.stream().anyMatch(r -> r.equalsIgnoreCase(recipeName))) {
            result.setMessage("Recipe not available");
            result.setSuccess(false);
            return result;
        }

        int price = recipePrices.get(lowerCaseRecipeName);
        if (player.getMoney() < price) {
            result.setMessage("Not enough money");
            result.setSuccess(false);
            return result;
        }

        if (player.getCookRecipes().stream().anyMatch(r -> r.equalsIgnoreCase(recipeName))) {
            result.setMessage("Already know this recipe");
            result.setSuccess(false);
            return result;
        }

        player.setMoney(player.getMoney() - price);
        player.learnRecipe(recipeName);
        soldRecipes.put(recipeName, soldRecipes.getOrDefault(recipeName, 0) + 1);

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

    public Result upgradeTools(User player, int level, Tools tool) {
        Result result = new Result();
        if (!isOpen()) {
            result.setMessage(name + " is closed");
            result.setSuccess(false);
            return result;
        }

        if (name.equalsIgnoreCase("Fish Shop")) {
            return upgradeFishingPole(player, level, tool);
        }

        int sold = soldUpgrades.getOrDefault(level, 0);
        if (sold >= 1) {
            result.setMessage("This tool upgrade has already been sold today.");
            result.setSuccess(false);
            return result;
        }

        int cost = getUpgradePrice(level, false);
        if (player.getMoney() < cost) {
            result.setSuccess(false);
            result.setMessage("Not enough money.");
            return result;
        }

        String neededIngredient = getRequiredMaterialForUpgrade(level);
        if (!player.getInventory().hasItem(neededIngredient, 5)) {
            result.setSuccess(false);
            result.setMessage("Not enough materials. Required: 5 " + neededIngredient);
            return result;
        }

        player.setMoney(player.getMoney() - cost);
        player.getInventory().removeItemByName(neededIngredient, 5);

        upgradeToolStage(tool, level);
        soldUpgrades.put(level, soldUpgrades.getOrDefault(level, 0) + 1);

        result.setSuccess(true);
        result.setMessage("Upgrade successful for " + tool.getName());
        return result;
    }

    public Result upgradeTrashCan(User player, int level) {
        Result result = new Result();
        if (!isOpen()) {
            result.setMessage(name + " is closed");
            result.setSuccess(false);
            return result;
        }

        int sold = soldBinsUpgrades.getOrDefault(level, 0);
        if (sold >= 1) {
            result.setMessage("This bin upgrade has already been sold today.");
            result.setSuccess(false);
            return result;
        }

        int cost = getUpgradePrice(level, true);
        if (player.getMoney() < cost) {
            result.setSuccess(false);
            result.setMessage("Not enough money.");
            return result;
        }

        String neededIngredient = getRequiredMaterialForUpgrade(level);
        if (!player.getInventory().hasItem(neededIngredient, 5)) {
            result.setSuccess(false);
            result.setMessage("Not enough materials. Required: 5 " + neededIngredient);
            return result;
        }

        player.getInventory().setTrashCanStage(getBinStageFromLevel(level));
        player.setMoney(player.getMoney() - cost);
        player.getInventory().removeItemByName(neededIngredient, 5);
        soldBinsUpgrades.put(level, soldBinsUpgrades.getOrDefault(level, 0) + 1);

        result.setSuccess(true);
        result.setMessage("Trash Can upgrade successful!");
        return result;
    }

    private Result upgradeFishingPole(User player, int level, Tools tool) {
        Result result = new Result();
        int soldToday = soldPoleUpgrades.getOrDefault(level, 0);
        if (soldToday >= 1) {
            result.setMessage("This fishing pole upgrade is sold out for today.");
            result.setSuccess(false);
            return result;
        }

        if (player.getMoney() < upgradePoleCosts.get(level)) {
            result.setMessage("not enough money");
            result.setSuccess(false);
            return result;
        }

        if (tool != null) {
            int currentLevel = tool.getFishingpoleStage().ordinal();
            if (currentLevel != level - 1) {
                result.setMessage("upgrade level by level");
                result.setSuccess(false);
                return result;
            }
            if ((level == 2 && player.getFishingSkills() < 2) || (level == 3 && player.getFishingSkills() < 4)) {
                result.setMessage("not enough skills");
                result.setSuccess(false);
                return result;
            }

            player.setMoney(player.getMoney() - upgradePoleCosts.get(level));
            tool.setFishingpoleStage(getFishingPoleStageFromLevel(level));
            soldPoleUpgrades.put(level, 1);
            result.setSuccess(true);
            result.setMessage("Upgraded to " + tool.getFishingpoleStage().name());
            return result;

        } else {
            if (level != 0) {
                result.setMessage("buy training at first");
                result.setSuccess(false);
                return result;
            }

            player.setMoney(player.getMoney() - upgradePoleCosts.get(level));
            Tools firstPole = new Tools();
            firstPole.setName("fishingpole");
            firstPole.setType("Tool");
            firstPole.setQuantity(1);
            firstPole.setFishingpoleStage(Tools.FishingpoleStage.TRAINING);
            firstPole.setPath("assets/Inventory/ToolsAndUpgrade/Training_Rod.png");
            player.getInventory().addItem(firstPole);
            soldPoleUpgrades.put(level, 1);
            result.setSuccess(true);
            result.setMessage("bought Training Rod");
            return result;
        }
    }

    private void upgradeToolStage(Tools tool, int level) {
        if (tool.getName().equalsIgnoreCase("Axe")) {
            tool.setAxeStage(getAxeStageFromLevel(level));
        } else if (tool.getName().equalsIgnoreCase("Pickaxe")) {
            tool.setPickaxeStage(getPickaxeStageFromLevel(level));
        } else if (tool.getName().equalsIgnoreCase("Hoe")) {
            tool.setHoeStage(getHoeStageFromLevel(level));
        } else if (tool.getName().equalsIgnoreCase("Watering_Can")) {
            tool.setWateringcanStage(getWateringCanStageFromLevel(level));
        }
    }

    private Tools.AxeStage getAxeStageFromLevel(int level) {
        return switch (level) {
            case 1 -> Tools.AxeStage.COPPER;
            case 2 -> Tools.AxeStage.IRON;
            case 3 -> Tools.AxeStage.GOLD;
            case 4 -> Tools.AxeStage.IRIDIUM;
            default -> Tools.AxeStage.BEGINNER;
        };
    }

    private Tools.PickaxeStage getPickaxeStageFromLevel(int level) {
        return switch (level) {
            case 1 -> Tools.PickaxeStage.COPPER;
            case 2 -> Tools.PickaxeStage.IRON;
            case 3 -> Tools.PickaxeStage.GOLD;
            case 4 -> Tools.PickaxeStage.IRIDIUM;
            default -> Tools.PickaxeStage.BEGINNER;
        };
    }

    private Tools.HoeStage getHoeStageFromLevel(int level) {
        return switch (level) {
            case 1 -> Tools.HoeStage.COPPER;
            case 2 -> Tools.HoeStage.IRON;
            case 3 -> Tools.HoeStage.GOLD;
            case 4 -> Tools.HoeStage.IRIDIUM;
            default -> Tools.HoeStage.BEGINNER;
        };
    }

    private Tools.WateringcanStage getWateringCanStageFromLevel(int level) {
        return switch (level) {
            case 1 -> Tools.WateringcanStage.COPPER;
            case 2 -> Tools.WateringcanStage.IRON;
            case 3 -> Tools.WateringcanStage.GOLD;
            case 4 -> Tools.WateringcanStage.IRIDIUM;
            default -> Tools.WateringcanStage.BEGINNER;
        };
    }

    private Tools.FishingpoleStage getFishingPoleStageFromLevel(int level) {
        return switch (level) {
            case 0 -> Tools.FishingpoleStage.TRAINING;
            case 1 -> Tools.FishingpoleStage.BAMBOO;
            case 2 -> Tools.FishingpoleStage.FIBERGLASS;
            case 3 -> Tools.FishingpoleStage.IRIDIUM;
            default -> null;
        };
    }

    private Tools.TrashbinStage getBinStageFromLevel(int level) {
        return switch (level) {
            case 1 -> Tools.TrashbinStage.COPPER;
            case 2 -> Tools.TrashbinStage.STEEL;
            case 3 -> Tools.TrashbinStage.GOLD;
            case 4 -> Tools.TrashbinStage.IRIDIUM;
            default -> Tools.TrashbinStage.BEGINNER;
        };
    }

    public List<Tools> getUpgradableTools(User player, int level) {
        return player.getInventory().getItems().stream()
            .filter(item -> item instanceof Tools)
            .map(item -> (Tools) item)
            .filter(tool -> isToolUpgradable(tool, level))
            .collect(Collectors.toList());
    }

    private boolean isToolUpgradable(Tools tool, int level) {
        switch (level) {
            case 1: // Copper
                return tool.getAxeStage() == Tools.AxeStage.BEGINNER ||
                    tool.getPickaxeStage() == Tools.PickaxeStage.BEGINNER ||
                    tool.getHoeStage() == Tools.HoeStage.BEGINNER ||
                    tool.getWateringcanStage() == Tools.WateringcanStage.BEGINNER;
            case 2: // Steel
                return tool.getAxeStage() == Tools.AxeStage.COPPER ||
                    tool.getPickaxeStage() == Tools.PickaxeStage.COPPER ||
                    tool.getHoeStage() == Tools.HoeStage.COPPER ||
                    tool.getWateringcanStage() == Tools.WateringcanStage.COPPER;
            case 3: // Gold
                return tool.getAxeStage() == Tools.AxeStage.IRON ||
                    tool.getPickaxeStage() == Tools.PickaxeStage.IRON ||
                    tool.getHoeStage() == Tools.HoeStage.IRON ||
                    tool.getWateringcanStage() == Tools.WateringcanStage.IRON;
            case 4: // Iridium
                return tool.getAxeStage() == Tools.AxeStage.GOLD ||
                    tool.getPickaxeStage() == Tools.PickaxeStage.GOLD ||
                    tool.getHoeStage() == Tools.HoeStage.GOLD ||
                    tool.getWateringcanStage() == Tools.WateringcanStage.GOLD;
        }
        return false;
    }

    private String getUpgradeName(int level, boolean isBin) {
        return switch (level) {
            case 1 -> "Copper " + (isBin ? "Trashcan" : "Tool");
            case 2 -> "Steel " + (isBin ? "Trashcan" : "Tool");
            case 3 -> "Gold " + (isBin ? "Trashcan" : "Tool");
            case 4 -> "Iridium " + (isBin ? "Trashcan" : "Tool");
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
                    .append("Iridium Tool\n").append("Copper Trashcan\n").append("Steel Trashcan\n")
                    .append("Gold Trashcan\n").append("Iridium Trashcan\n");
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
                if (!isTroutSoupSold) output.append("Trout Soup:\n");
            }
        }
        return output.toString();
    }

    //GETTERS
    public String getName() {
        return name;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
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
