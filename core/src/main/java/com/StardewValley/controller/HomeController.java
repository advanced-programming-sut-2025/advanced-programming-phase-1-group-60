package com.StardewValley.controller;

import com.StardewValley.models.*; // Assuming Item, Inventory, Energy, GameMap, User, etc., are here

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeController {
    // Removed: private static User currentPlayer = Game.getCurrentGame().getCurrentPlayer();;
    // User objects will now be passed explicitly to methods that need them.

    private List<ProcessingMachine> processingMachineList = new ArrayList<>();
    // private GameMap currentGameMap; // This field doesn't seem to be used anywhere in the provided code.
    // Consider removing it if not used, or pass GameMap explicitly where needed.

    // Table of recipes and their required materials for crafting
    // Note: 'craftingRecipes' seems unused; 'lockedRecipes' and 'unlockedRecipes' are used.
    // private static final Map<String, Map<String, Integer>> craftingRecipes = new HashMap<>();

    // Energy cost for crafting each item
    private static final int CRAFT_ENERGY_COST = 2;

    // Static maps for locked and unlocked recipes, globally accessible
    private static final Map<String, Map<String, Integer>> lockedRecipes = new HashMap<>();
    private static final Map<String, Map<String, Integer>> unlockedRecipes = new HashMap<>();

    // Static initializer block: runs once when the class is loaded
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

    /**
     * Returns a read-only view of the currently locked recipes.
     * The internal maps for ingredients are immutable (from Map.of()).
     *
     * @return A map of locked recipe names to their ingredient maps.
     */
    public static Map<String, Map<String, Integer>> getLockedRecipes() {
        return lockedRecipes;
    }

    /**
     * Attempts to unlock a specific recipe by name.
     * Moves the recipe from lockedRecipes to unlockedRecipes.
     *
     * @param recipeName The name of the recipe to unlock.
     * @return A message indicating success or failure.
     */
    public static String unlockRecipe(String recipeName) {
        if (!lockedRecipes.containsKey(recipeName)) {
            return "Error: Recipe not found or already unlocked.";
        }
        // Move recipe from locked to unlocked
        unlockedRecipes.put(recipeName, lockedRecipes.remove(recipeName));
        return "Recipe unlocked: " + recipeName;
    }

    /**
     * Returns a read-only view of the currently unlocked recipes.
     * The internal maps for ingredients are immutable (from Map.of()).
     *
     * @return A map of unlocked recipe names to their ingredient maps.
     */
    public static Map<String, Map<String, Integer>> getUnlockedRecipes() {
        return unlockedRecipes;
    }

    /**
     * Displays all currently locked recipes.
     *
     * @return A string listing all locked recipes.
     */
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

    /**
     * Main entry point for crafting-related commands.
     * @param command The command to execute (e.g., "craft", "unlock recipe").
     * @param args Variable arguments depending on the command. User object must be passed for operations requiring it.
     * @return A status message.
     */
    public static String crafting(String command, Object... args) {
        switch (command.toLowerCase()) {
            case "show_recipes":
                return showRecipes(); // This seems to be a duplicate of showLockedRecipes()

            case "learn_recipe":
                // This command doesn't seem to have a corresponding learnRecipe method in HomeController,
                // and 'craftingRecipes' map is unused. Consider if this command is still needed.
                if (args.length != 2 || !(args[0] instanceof String) || !(args[1] instanceof Map)) {
                    return "Error: Invalid arguments for 'learn_recipe'.";
                }
                String recipeName = (String) args[0];
                Map<String, Integer> materials = (Map<String, Integer>) args[1];
                // Assuming learnRecipe(String, Map) exists elsewhere or should be implemented
                return "Error: 'learn_recipe' not fully implemented or map 'craftingRecipes' is unused.";

            case "craft":
                if (args.length != 2 || !(args[0] instanceof String) || !(args[1] instanceof User)) {
                    return "Error: Invalid arguments for 'craft'. Expected: craft <itemName> <User>";
                }
                String itemToCraft = (String) args[0];
                User craftingUser = (User) args[1]; // Correctly cast and pass the User
                return craftItem(itemToCraft, craftingUser); // Pass the User object

            case "cheat add item":
                if (args.length != 3 || !(args[0] instanceof String) || !(args[1] instanceof Integer) || !(args[2] instanceof User)) {
                    return "Error: Invalid arguments for 'cheat add item'. Usage: cheat add item <item_name> <quantity> <User>";
                }
                String cheatItemName = (String) args[0];
                Integer cheatQuantity = (Integer) args[1];
                User cheatUser = (User) args[2]; // Correctly cast and pass the User
                return addItemToInventory(cheatItemName, cheatQuantity, cheatUser); // Pass the User object

            case "place item":
                if (args.length != 4 || !(args[0] instanceof String) || !(args[1] instanceof Integer) || !(args[2] instanceof User) || !(args[3] instanceof GameMap)) {
                    return "Error: Invalid arguments for 'place_item'. Expected: place item <itemName> <direction> <User> <GameMap>";
                }
                String placeItemName = (String) args[0];
                int direction = (Integer) args[1];
                User placingUser = (User) args[2];
                GameMap gameMap = (GameMap) args[3];
                return placeItem(placeItemName, direction, placingUser, gameMap);

            case "unlock recipe":
                if (args.length != 1 || !(args[0] instanceof String)) {
                    return "Error: Invalid arguments for 'unlock recipe'. Expected: unlock recipe <recipeName>";
                }
                return unlockRecipe((String) args[0]);

            default:
                return "Error: Unknown crafting command.";
        }
    }

    /**
     * Displays all recipes that are currently locked (same as showLockedRecipes).
     * @return A string listing all locked recipes.
     */
    private static String showRecipes() {
        // This seems to be a duplicate of showLockedRecipes(). Consider consolidating.
        return showLockedRecipes();
    }

    /**
     * Displays all currently unlocked recipes.
     * @return A string listing all unlocked recipes.
     */
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

    // This method relates to the 'craftingRecipes' map which seems unused.
    // Consider if this method and the 'craftingRecipes' map are actually needed.
    // private static String learnRecipe(String recipeName, Map<String, Integer> materials) {
    //     if (craftingRecipes.containsKey(recipeName)) {
    //         return "Error: Recipe '" + recipeName + "' already exists.";
    //     }
    //     craftingRecipes.put(recipeName, materials);
    //     return "Successfully learned new recipe: '" + recipeName + "'.";
    // }


    /**
     * Attempts to craft an item for a given user.
     * @param itemName The name of the item to craft.
     * @param user The user attempting to craft.
     * @return A message indicating success or failure.
     */
    public static String craftItem(String itemName, User user) { // User parameter added
        if (!unlockedRecipes.containsKey(itemName)) {
            return "Error: Recipe for '" + itemName + "' not found.";
        }

        Map<String, Integer> materials = unlockedRecipes.get(itemName);
        Inventory inventory = user.getInventory(); // Use the passed user's inventory

        // Check if user has enough materials
        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
            if (!inventory.hasItem(entry.getKey(), entry.getValue())) {
                return "Error: Not enough " + entry.getKey() + ".";
            }
        }

        // Check if user has enough energy
        if (user.getEnergy().getCurrentEnergy() < CRAFT_ENERGY_COST) { // Use the passed user's energy
            return "Error: Not enough energy.";
        }

        // Consume materials
        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
            inventory.removeItem(new Item(entry.getKey(), entry.getValue()));
        }

        // Decrease energy
        user.getEnergy().decreaseEnergy(CRAFT_ENERGY_COST); // Use the passed user's energy

        // Add crafted item to inventory
        if (!inventory.tryAddItem(new Item(itemName, 1))) {
            // Note: If tryAddItem returns false, the item was not added.
            // However, materials and energy were already consumed.
            // You might want to consider rolling back the transaction here if addItem fails.
            return "Error: Inventory is full. Crafted item could not be added. Materials and energy consumed.";
        }

        return "Successfully crafted '" + itemName + "'.";
    }

    /**
     * Gets a map of item names to quantities in a user's inventory.
     * @param user The user whose inventory is to be mapped.
     * @return A map representing the user's inventory.
     */
    public static Map<String, Integer> getInventoryItemMap(User user) { // User parameter added
        Map<String, Integer> map = new HashMap<>();
        for (Item item : user.getInventory().getItems()) { // Use the passed user's inventory
            map.put(item.getName(), item.getQuantity());
        }
        return map;
    }

    /**
     * Attempts to place an item for a given user on the game map.
     * @param itemName The name of the item to place.
     * @param direction The direction to place the item (1-8).
     * @param user The user placing the item.
     * @param gameMap The current game map.
     * @return A message indicating success or failure.
     */
    private static String placeItem(String itemName, int direction, User user, GameMap gameMap) {
        Inventory inventory = user.getInventory(); // Use the passed user's inventory

        // Check if user has the item
        if (!inventory.hasItem(itemName, 1)) {
            return "Error: You don't have the item '" + itemName + "' in your inventory.";
        }

        // Validate direction
        if (direction < 1 || direction > 8) {
            return "Error: Invalid direction. Must be between 1 and 8.";
        }

        // Calculate new position
        int currentPositionX = user.getPosition().getPositionX();
        int currentPositionY = user.getPosition().getPositionY();
        int[] newPosition = calculateNewPosition(currentPositionX, currentPositionY, direction);

        // TODO: Add logic to check if the target tile (newPosition) is valid for placement
        // e.g., gameMap.getTile(newPosition[0], newPosition[1]).isPlaceable()
        // And then actually place the item on the map: gameMap.placeItem(itemName, newPosition[0], newPosition[1])

        // Remove item from user's inventory
        inventory.removeItem(new Item(itemName, 1));

        return "Successfully placed '" + itemName + "' on the ground in direction " + direction + ".";
    }

    /**
     * Helper method to calculate a new position based on current position and direction.
     * @param x Current X coordinate.
     * @param y Current Y coordinate.
     * @param direction Direction (1-8).
     * @return An array containing the new [X, Y] coordinates.
     */
    private static int[] calculateNewPosition(int x, int y, int direction) {
        switch (direction) {
            case 1: return new int[]{x, y - 1};     // Up (North)
            case 2: return new int[]{x + 1, y - 1}; // Up-Right (North-East)
            case 3: return new int[]{x + 1, y};     // Right (East)
            case 4: return new int[]{x + 1, y + 1}; // Down-Right (South-East)
            case 5: return new int[]{x, y + 1};     // Down (South)
            case 6: return new int[]{x - 1, y + 1}; // Down-Left (South-West)
            case 7: return new int[]{x - 1, y};     // Left (West)
            case 8: return new int[]{x - 1, y - 1}; // Up-Left (North-West)
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    /**
     * Adds a specified quantity of an item to a user's inventory.
     * @param itemName The name of the item to add.
     * @param count The quantity to add.
     * @param user The user to whom the item will be added.
     * @return A message indicating success or failure.
     */
    public static String addItemToInventory(String itemName, int count, User user) { // User parameter added
        if (count <= 0) {
            return "Error: Count must be greater than zero.";
        }
        Inventory inventory = user.getInventory(); // Use the passed user's inventory
        inventory.addItem(new Item(itemName, count));
        return "Successfully added " + count + " '" + itemName + "' to inventory.";
    }

    /**
     * Unlocks recipes based on a given skill type and level.
     * These unlocks are global (affecting static 'unlockedRecipes').
     * @param levelType The type of skill (e.g., "mining", "farming", "foraging").
     * @param level The skill level achieved.
     */
    public static void unlockRecipesByLevel(String levelType, int level) {
        switch (levelType.toLowerCase()) {
            case "mining":
                if (level >= 1 && lockedRecipes.containsKey("Cherry_Bomb")) unlockedRecipes.put("Cherry_Bomb", lockedRecipes.remove("Cherry_Bomb"));
                if (level >= 2 && lockedRecipes.containsKey("Bomb")) unlockedRecipes.put("Bomb", lockedRecipes.remove("Bomb"));
                if (level >= 3 && lockedRecipes.containsKey("Mega_Bomb")) unlockedRecipes.put("Mega_Bomb", lockedRecipes.remove("Mega_Bomb"));
                break;

            case "farming":
                if (level >= 1 && lockedRecipes.containsKey("Sprinkler")) unlockedRecipes.put("Sprinkler", lockedRecipes.remove("Sprinkler"));
                if (level >= 2 && lockedRecipes.containsKey("Quality_Sprinkler")) unlockedRecipes.put("Quality_Sprinkler", lockedRecipes.remove("Quality_Sprinkler"));
                if (level >= 3 && lockedRecipes.containsKey("Iridium_Sprinkler")) unlockedRecipes.put("Iridium_Sprinkler", lockedRecipes.remove("Iridium_Sprinkler"));
                break;

            case "foraging":
                if (level >= 1 && lockedRecipes.containsKey("Charcoal_Kiln")) unlockedRecipes.put("Charcoal_Kiln", lockedRecipes.remove("Charcoal_Kiln"));
                break;

            default:
                System.out.println("Error: Unknown level type for recipe unlocking: " + levelType);
        }
    }
}
