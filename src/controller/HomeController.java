package controller;

import models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//int direction ;
//int[] dx = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
//int[] dy = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
//int tx = user.getPosition().getPositionX() + dx[direction - 1];
//int ty = user.getPosition().getPositionY() + dy[direction - 1];
//            if (ty < 0 || tx < 0 || ty >= tiles.length || tx >= tiles[0].length) {
//        System.out.println("Target tile is out of bounds.");
//                return;
//                        }if (!('C' == target.getRandomElement().map(RandomElement::symbol).orElse(' '))) {
//        }
//Tile target = tiles[ty][tx];


public class HomeController {
    private List<ProcessingMachine> processingMachineList = new ArrayList<>();
    private GameMap currentGameMap;
    private static User currentPlayer = Game.getCurrentGame().getCurrentPlayer();;

    // جدول طرح‌ها و مواد اولیه لازم برای کرفت
    private static final Map<String, Map<String, Integer>> craftingRecipes = new HashMap<>();

    // انرژی موردنیاز برای ساخت هر آیتم
    private static final int CRAFT_ENERGY_COST = 2;
    private static final Map<String, Map<String, Integer>> lockedRecipes = new HashMap<>();
    private static final Map<String, Map<String, Integer>> unlockedRecipes = new HashMap<>();

    static {
        // Mining Level Recipes
        lockedRecipes.put("Cherry_Bomb", Map.of("Copper_Ore", 4, "Coal", 1));
        lockedRecipes.put("Bomb", Map.of("Iron_Ore", 4, "Coal", 1));
        lockedRecipes.put("Mega_Bomb", Map.of("Gold_Ore", 4, "Coal", 1));

        // Farming Level Recipes
        lockedRecipes.put("Sprinkler", Map.of("Copper_Bar", 1, "Iron_Bar", 1));
        lockedRecipes.put("Quality_Sprinkler", Map.of("Iron_Bar", 1, "Gold_Bar", 1));
        lockedRecipes.put("Iridium_Sprinkler", Map.of("Gold_Bar", 1, "Iridium_Bar", 1));

        // Foraging Level Recipes
        lockedRecipes.put("Charcoal_Kiln", Map.of("Wood", 20, "Copper_Bar", 2));
        lockedRecipes.put("Furnace", Map.of("Copper_Ore", 20, "Stone", 25));

        // Scarecrows
        lockedRecipes.put("Scarecrow", Map.of("Wood", 50, "Coal", 1, "Fiber", 20));
        lockedRecipes.put("Deluxe_Scarecrow", Map.of("Wood", 50, "Coal", 1, "Fiber", 20, "Iridium_Ore", 1));

        // Bee House & Cheese Press
        lockedRecipes.put("Bee_House", Map.of("Wood", 40, "Coal", 8, "Iron_Bar", 1));
        lockedRecipes.put("Cheese_Press", Map.of("Wood", 45, "Stone", 45, "Copper_Bar", 1));

        // Keg & Loom
        lockedRecipes.put("Keg", Map.of("Wood", 30, "Copper_Bar", 1, "Iron_Bar", 1));
        lockedRecipes.put("Loom", Map.of("Wood", 60, "Fiber", 30));

        // Machines
        lockedRecipes.put("Mayonnaise_Machine", Map.of("Wood", 15, "Stone", 15, "Copper_Bar", 1));
        lockedRecipes.put("Oil_Maker", Map.of("Gold_Bar", 1, "Iron_Bar", 1, "Wood", 100));
        lockedRecipes.put("Preserves_Jar", Map.of("Wood", 50, "Stone", 40, "Coal", 8));
        lockedRecipes.put("Dehydrator", Map.of("Wood", 30, "Stone", 20, "Fiber", 30));

        // Special Items
        lockedRecipes.put("Grass_Starter", Map.of("Fiber", 1));
        lockedRecipes.put("Fish_Smoker", Map.of("Wood", 50, "Iron_Bar", 3, "Coal", 10));
        lockedRecipes.put("Mystic_Tree_Seed", Map.of("Acorn", 5, "Maple_Seed", 5, "Pine_Cone", 5, "Mahogany_Seed", 5));
    }

//    static {
//        // Bee House Recipes
//        lockedRecipes.put("Honey", Map.of());
//
//        // Cheese Press Recipes
//        lockedRecipes.put("Cheese", Map.of("Milk", 1));
//        lockedRecipes.put("Goat_Cheese", Map.of("Goat_Milk", 1));
//
//        // Keg Recipes
//        lockedRecipes.put("Beer", Map.of("Wheat", 1));
//        lockedRecipes.put("Vinegar", Map.of("Rice", 1));
//        lockedRecipes.put("Coffee", Map.of("Coffee_Bean", 5));
//        lockedRecipes.put("Juice", Map.of("Any_Vegetable", 1));
//        lockedRecipes.put("Mead", Map.of("Honey", 1));
//        lockedRecipes.put("Pale_Ale", Map.of("Hops", 1));
//        lockedRecipes.put("Wine", Map.of("Any_Fruit", 1));
//
//        // Loom Recipes
//        lockedRecipes.put("Cloth", Map.of("Wool", 1));
//
//        // Mayonnaise Machine Recipes
//        lockedRecipes.put("Mayonnaise", Map.of("Egg", 1));
//        lockedRecipes.put("Duck_Mayonnaise", Map.of("Duck_Egg", 1));
//        lockedRecipes.put("Dinosaur_Mayonnaise", Map.of("Dinosaur_Egg", 1));
//
//        // Oil Maker Recipes
//        lockedRecipes.put("Truffle_Oil", Map.of("Truffle", 1));
//        lockedRecipes.put("Oil", Map.of("Corn", 1));
//
//        // Preserves Jar Recipes
//        lockedRecipes.put("Pickles", Map.of("Any_Vegetable", 1));
//        lockedRecipes.put("Jelly", Map.of("Any_Fruit", 1));
//
//        // Fish Smoker Recipes
//        lockedRecipes.put("Smoked_Fish", Map.of("Any_Fish", 1, "Coal", 1));
//
//        // Furnace Recipes
//        lockedRecipes.put("Metal_Bar", Map.of("Ore", 5, "Coal", 1));
//
//        // Dehydrator Recipes
//        lockedRecipes.put("Dried_Mushrooms", Map.of("Any_Mushroom", 1));
//        lockedRecipes.put("Dried_Fruit", Map.of("Any_Fruit", 1));
//        lockedRecipes.put("Raisins", Map.of("Grapes", 1));
//
//        // Charcoal Kiln Recipes
//        lockedRecipes.put("Coal", Map.of("Wood", 10));
//    }

    public static Map<String, Map<String, Integer>> getLockedRecipes() {
        return lockedRecipes;
    }

    public static String unlockRecipe(String recipeName) {
        if (!lockedRecipes.containsKey(recipeName)) {
            return "Error: Recipe not found or already unlocked.";
        }
        unlockedRecipes.put(recipeName, lockedRecipes.remove(recipeName));
        return "Recipe unlocked: " + recipeName;
    }

    public static Map<String, Map<String, Integer>> getUnlockedRecipes() {
        return unlockedRecipes;
    }


    // نمایش رسپی‌های قفل‌شده
    public static String showLockedRecipes() {
        if (lockedRecipes.isEmpty()) {
            return "All recipes are unlocked.";
        }

        StringBuilder recipes = new StringBuilder("Locked Recipes:\n");
        for (String recipe : lockedRecipes.keySet()) {
            recipes.append("- ").append(recipe).append("\n");
        }
        return recipes.toString();
    }

    public static String crafting(String command, Object... args) {
        switch (command.toLowerCase()) {
            case "show_recipes":
                return showRecipes();

            case "learn_recipe":
                if (args.length != 2 || !(args[0] instanceof String) || !(args[1] instanceof Map)) {
                    return "Error: Invalid arguments for 'learn_recipe'.";
                }
                String recipeName = (String) args[0];
                Map<String, Integer> materials = (Map<String, Integer>) args[1];
                return learnRecipe(recipeName, materials);

            case "craft":
                if (args.length != 2 || !(args[0] instanceof String) || !(args[1] instanceof User)) {
                    return "Error: Invalid arguments for 'craft'.";
                }
                return craftItem((String) args[0]);

            case "cheat add item":
                if (args.length != 2 || !(args[0] instanceof String) || !(args[1] instanceof Integer)) {
                    return "Error: Invalid arguments for 'cheat_add_item'. Usage: cheat_add_item <item_name> <quantity>";
                }
                return addItemToInventory((String) args[0], (Integer) args[1], currentPlayer);


            case "place item":
                if (args.length != 4 || !(args[0] instanceof String) || !(args[1] instanceof Integer) || !(args[2] instanceof User) || !(args[3] instanceof GameMap)) {
                    return "Error: Invalid arguments for 'place_item'.";
                }
                String placeItemName = (String) args[0];
                int direction = (Integer) args[1];
                User placingUser = (User) args[2];
                GameMap gameMap = (GameMap) args[3];
                return placeItem(placeItemName, direction, placingUser, gameMap);
            //چیت برای تست
            case "unlock recipe":
                if (args.length != 1 || !(args[0] instanceof String)) {
                    return "Error: Invalid arguments for 'cheat_unlock_recipe'.";
                }

                return unlockRecipe((String) args[0]);
                // return "Error: Unknown crafting command.";
            default:
                return "Error: Unknown crafting command.";
        }
    }

    private static String showRecipes() {
            if (lockedRecipes.isEmpty()) {
                return "All recipes are unlocked.";
            }

            StringBuilder recipes = new StringBuilder("Locked Recipes:\n");
            for (String recipe : lockedRecipes.keySet()) {
                recipes.append("- ").append(recipe).append("\n");
            }
            return recipes.toString();
        }

        public static String showUnlockedRecipes() {
            if (unlockedRecipes.isEmpty()) {
                return "No recipes unlocked yet.";
            }

            StringBuilder recipes = new StringBuilder("Unlocked Recipes:\n");
            for (String recipe : unlockedRecipes.keySet()) {
                recipes.append("- ").append(recipe).append("\n");
            }
            return recipes.toString();
        }
    private static String learnRecipe(String recipeName, Map<String, Integer> materials) {
        if (craftingRecipes.containsKey(recipeName)) {
            return "Error: Recipe '" + recipeName + "' already exists.";
        }

        craftingRecipes.put(recipeName, materials);
        return "Successfully learned new recipe: '" + recipeName + "'.";
    }

    // متدی برای چیت جهت آزاد کردن یک رسپی خاص
    public static String cheatUnlockRecipe(String recipeName) {
        if (lockedRecipes.containsKey(recipeName)) {
            unlockedRecipes.put(recipeName, lockedRecipes.remove(recipeName));
            return "Recipe unlocked: " + recipeName;
        } else if (unlockedRecipes.containsKey(recipeName)) {
            return "Recipe is already unlocked.";
        } else {
            return "Error: Recipe not found.";
        }
    }
    public static String craftItem(String itemName) {
        if (!unlockedRecipes.containsKey(itemName)) {
            return "Error: Recipe for '" + itemName + "' not found.";
        }

        Map<String, Integer> materials = unlockedRecipes.get(itemName);
        Inventory inventory = currentPlayer.getInventory();

        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
            if (!inventory.hasItem(entry.getKey(), entry.getValue())) {
                return "Error: Not enough " + entry.getKey() + ".";
            }
        }

        if (currentPlayer.getEnergy().getCurrentEnergy() < CRAFT_ENERGY_COST) {
            return "Error: Not enough energy.";
        }

        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
            inventory.removeItem(new Item(entry.getKey(), entry.getValue()));
        }

        if (!inventory.tryAddItem(new Item(itemName, 1))) {
            return "Error: Inventory is full. Crafted item could not be added.";
        }


        currentPlayer.getEnergy().decreaseEnergy(CRAFT_ENERGY_COST);
        inventory.addItem(new Item(itemName, 1));
        return "Successfully crafted '" + itemName + "'.";
    }


    private static String placeItem(String itemName, int direction, User user, GameMap gameMap) {
        Inventory inventory = user.getInventory();

        // بررسی اینکه آیا کاربر آیتم مورد نظر را در موجودی خود دارد
        if (!inventory.hasItem(itemName, 1)) {
            return "Error: You don't have the item '" + itemName + "' in your inventory.";
        }

        // بررسی مقدار معتبر بودن جهت
        if (direction < 1 || direction > 8) {
            return "Error: Invalid direction. Must be between 1 and 8.";
        }

        // محاسبه موقعیت جدید بر اساس جهت
        int currentPositionX = user.getPosition().getPositionX();
        int currentPositionY = user.getPosition().getPositionY();
        int[] newPosition = calculateNewPosition(currentPositionX, currentPositionY, direction);

        // بررسی اینکه آیا سلول خالی است یا نه
        if (!gameMap.isCellEmpty(newPosition[0], newPosition[1])) {
            return "Error: The target cell is not empty.";
        }

        // قرار دادن آیتم در نقشه
        gameMap.placeItemOnMap(itemName, newPosition[0], newPosition[1]);

        // حذف آیتم از موجودی کاربر
        inventory.removeItem(new Item(itemName, 1));

        return "Successfully placed '" + itemName + "' on the ground in direction " + direction + ".";
    }
    private static int[] calculateNewPosition(int x, int y, int direction) {
        switch (direction) {
            case 1: // بالا (North)
                return new int[]{x, y - 1};
            case 2: // بالا-راست (North-East)
                return new int[]{x + 1, y - 1};
            case 3: // راست (East)
                return new int[]{x + 1, y};
            case 4: // پایین-راست (South-East)
                return new int[]{x + 1, y + 1};
            case 5: // پایین (South)
                return new int[]{x, y + 1};
            case 6: // پایین-چپ (South-West)
                return new int[]{x - 1, y + 1};
            case 7: // چپ (West)
                return new int[]{x - 1, y};
            case 8: // بالا-چپ (North-West)
                return new int[]{x - 1, y - 1};
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    public static String addItemToInventory(String itemName, int count, User user) {
        if (count <= 0) {
            return "Error: Count must be greater than zero.";
        }

        Inventory inventory = user.getInventory();
        inventory.addItem(new Item(itemName, count));

        return "Successfully added " + count + " '" + itemName + "' to inventory.";
    }

    public static void unlockRecipesByLevel(String levelType, int level) {
        switch (levelType.toLowerCase()) {
            case "mining":
                if (level >= 1) unlockedRecipes.put("Cherry_Bomb", lockedRecipes.remove("Cherry_Bomb"));
                if (level >= 2) unlockedRecipes.put("Bomb", lockedRecipes.remove("Bomb"));
                if (level >= 3) unlockedRecipes.put("Mega_Bomb", lockedRecipes.remove("Mega_Bomb"));
                break;

            case "farming":
                if (level >= 1) unlockedRecipes.put("Sprinkler", lockedRecipes.remove("Sprinkler"));
                if (level >= 2) unlockedRecipes.put("Quality_Sprinkler", lockedRecipes.remove("Quality_Sprinkler"));
                if (level >= 3) unlockedRecipes.put("Iridium_Sprinkler", lockedRecipes.remove("Iridium_Sprinkler"));
                break;

            case "foraging":
                if (level >= 1) unlockedRecipes.put("Charcoal_Kiln", lockedRecipes.remove("Charcoal_Kiln"));
                break;

            default:
                System.out.println("Error: Unknown level type.");
        }
    }
}