package com.StardewValley.controller;

import com.StardewValley.models.Item;
import com.StardewValley.models.Recipe;
import com.StardewValley.models.User;
import com.StardewValley.repository.RecipeRepository;

import java.util.*;

public class CookController {
    private final RecipeRepository recipeRepository = new RecipeRepository();

    public String handleCommand(User user, String command) {
        String[] parts = command.split(" ");
        try {

            String subCommand = parts[1].toLowerCase();
            switch (subCommand) {
                // kitchen refrigerator pick/put item
                case "refrigerator" -> {
                    return (handleRefrigerator(user, Arrays.copyOfRange(parts, 2, parts.length)));
                }
                // kitchen show_recipes
                case "show_recipes" -> {
                    return (showRecipes(user));
                }
                // kitchen prepare food name
                case "prepare" -> {
                    return (prepareFood(user, String.join(" ", Arrays.copyOfRange(parts, 2, parts.length))));
                }
                // kitchen eat food name
                case "eat" -> {
                    return (eatFood(user, String.join(" ", Arrays.copyOfRange(parts, 2, parts.length))));
                }
                default -> {
                    return "Unknown kitchen command";
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String handleRefrigerator(User user, String[] args) {
        if (args.length < 2) return ("Invalid refrigerator command");

        String action = args[0];
        String itemName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        switch (action.toLowerCase()) {
            case "put" -> {
                Item item = user.getInventory().getItem(itemName);
                if (item == null) return ("Item not found in inventory");
                if (!item.isEdible()) return ("Item is not edible");

                user.getInventory().removeItemByName(itemName, 1);
                Item itemToPut = new Item(item.getName(), 1, item.getPath());
                itemToPut.setType("Food");
                user.addItemToRefrigerator(itemToPut);
                itemToPut.setPath(item.getPath());
                return "put successfully " + itemName;
            }
            case "pick" -> {
                Item item = user.getRefrigeratorItem(itemName);
                if (item == null) return ("Item not found in refrigerator");

                Item itemToPick = new Item(item.getName(), 1, item.getPath()); // Copy path
                itemToPick.setType("Food");
                if (!user.getInventory().addItem(itemToPick)) return "inventory is full";
                user.removeItemFromRefrigerator(itemName, 1);
                return "pick successfully " + itemName;
            }
            default -> {
                return ("Invalid refrigerator action");
            }
        }
    }

    public String showRecipes(User user) {
        StringBuilder sb = new StringBuilder();
        for (String recipeName : user.getCookRecipes()) {
            Recipe recipe = recipeRepository.getRecipe(recipeName);
            sb.append(String.format("- %s ingredients: %s %n",
                recipe.getName(),
                formatIngredients(recipe.getIngredients())));
        }
        return sb.toString();
    }

    public String prepareFood(User user, String recipeName) {
        Recipe recipe = recipeRepository.getRecipe(recipeName);
        if (recipe == null) return ("Recipe does not exist");
        if (!user.getCookRecipes().contains(recipe.getName())) return ("Recipe not learned");
        if (user.getEnergy().getCurrentEnergy() < 3) return ("Not enough energy");

        // Check for ingredients in both inventory and refrigerator
        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String ingredient = entry.getKey();
            int requiredQty = entry.getValue();
            int inInventory = user.getInventory().getItemQuantityByName(ingredient);
            int inFridge = user.getRefrigeratorItemQuantity(ingredient);
            if (inInventory + inFridge < requiredQty) {
                return "Insufficient " + ingredient;
            }
        }

        // Consume ingredients
        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String ingredient = entry.getKey();
            int required = entry.getValue();
            int fromInventory = Math.min(required, user.getInventory().getItemQuantityByName(ingredient));
            if (fromInventory > 0) {
                user.getInventory().removeItemByName(ingredient, fromInventory);
            }
            int fromFridge = required - fromInventory;
            if (fromFridge > 0) {
                user.removeItemFromRefrigerator(ingredient, fromFridge);
            }
        }

        user.getEnergy().decreaseEnergy(3);
        Item food = new Item();
        food.setName(recipeName);
        food.setQuantity(1);
        food.setType("Food");
        food.setPath(recipe.getPath()); // Set image path from recipe
        food.getProperties().put("energy", recipe.getEnergy());
        if (!user.getInventory().addItem(food)) return "inventory is full";
        return recipeName + " prepared successfully";
    }

    private String eatFood(User user, String foodName) {
        Item food = user.getInventory().getItem(foodName);
        if (food == null) return ("Food not found");

        int energyValue = (int) food.getProperties().getOrDefault("energy", 0);
        user.getEnergy().increaseEnergy(energyValue);
        user.getInventory().removeItemByName(foodName, 1);
        return foodName + " ate successfully";
    }

    private String formatIngredients(Map<String, Integer> ingredients) {
        return ingredients.entrySet().stream()
            .map(e -> e.getKey() + " x" + e.getValue())
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
}
