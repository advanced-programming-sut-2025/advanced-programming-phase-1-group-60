package controller;

import models.Item;
import models.Recipe;
import models.User;
import repository.RecipeRepository;

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

    private String handleRefrigerator(User user, String[] args) {
        if (args.length < 2) return ("Invalid refrigerator command");

        String action = args[0];
        String itemName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        switch (action.toLowerCase()) {
            case "put" -> {
                Item item = user.getInventory().getItem(itemName);
                if (item == null) return ("Item not found in inventory");
                if (!item.isEdible()) return ("Item is not edible");

                user.getInventory().removeItemByName(itemName, 1);
                Item itemToPut = new Item();
                itemToPut.setName(itemName);
                itemToPut.setQuantity(1);
                itemToPut.setType("Food");
                user.addItemToRefrigerator(itemToPut);
                return "put successfully " + itemName;
            }
            case "pick" -> {
                Item item = user.getRefrigeratorItem(itemName);
                if (item == null) return ("Item not found in refrigerator");

                user.removeItemFromRefrigerator(itemName, 1);
                Item itemToPick = new Item();
                itemToPick.setName(itemName);
                itemToPick.setQuantity(1);
                itemToPick.setType("Food");
                user.getInventory().addItem(itemToPick);
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

    private String prepareFood(User user, String recipeName) {
        Recipe recipe = recipeRepository.getRecipe(recipeName);
        if (recipe == null) return ("Recipe does not exist");
        if (!user.getCookRecipes().contains(recipeName)) return ("Recipe not learned");
        if (user.getEnergy().getCurrentEnergy() < 3) return ("Not enough energy");

        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String ingredient = entry.getKey();
            int requiredQty = entry.getValue();

            int inInventory = user.getInventory().getItemQuantityByName(ingredient);
            int inFridge    = user.getRefrigeratorItemQuantity(ingredient);
            if (inInventory + inFridge < requiredQty) {
                return "Insufficient " + ingredient;
            }
        }

        recipe.getIngredients().forEach((ingredient, required) -> {
            int fromInventory = Math.min(required, user.getInventory().getItemQuantityByName(ingredient));
            user.getInventory().removeItemByName(ingredient, fromInventory);

            int fromRefrigerator = required - fromInventory;
            if (fromRefrigerator > 0) {
                user.removeItemFromRefrigerator(ingredient, fromRefrigerator);
            }
        });

        user.getEnergy().decreaseEnergy(3);
        Item food = new Item();
        food.setName(recipeName);
        food.setQuantity(1);
        food.setType("Food");
        food.getProperties().put("energy", recipe.getEnergy());
        user.getInventory().addItem(food);
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