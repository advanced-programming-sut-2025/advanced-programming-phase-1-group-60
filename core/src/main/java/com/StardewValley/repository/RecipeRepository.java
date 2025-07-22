package com.StardewValley.repository;

import com.StardewValley.models.Recipe;

import java.util.HashMap;
import java.util.Map;

public class RecipeRepository {
    private final Map<String, Recipe> recipes = new HashMap<>();

    public RecipeRepository() {
        initializeRecipes();
    }

    private void initializeRecipes() {
        // Starter Recipes
        addRecipe(new Recipe("Fried Egg",
            Map.of("egg", 1),
            50, "Starter", 35, "assets/Inventory/FoodRecipes/Fried_Egg.png"));

        addRecipe(new Recipe("Baked Fish",
            Map.of("Sardine", 1, "Salmon", 1, "wheat", 1),
            75, "Starter", 100, "assets/Inventory/FoodRecipes/Baked_Fish.png"));

        addRecipe(new Recipe("Salad",
            Map.of("leek", 1, "dandelion", 1),
            113, "Starter", 110, "assets/Inventory/FoodRecipes/Salad.png"));

        // Stardrop Saloon Recipes
        addRecipe(new Recipe("Omelet",
            Map.of("egg", 1, "milk", 1),
            100, "Stardrop Saloon", 125, "assets/Inventory/FoodRecipes/Omelet.png"));

        addRecipe(new Recipe("Pumpkin Pie",
            Map.of("pumpkin", 1, "wheat flour", 1, "milk", 1, "sugar", 1),
            225, "not known", 385, "assets/Inventory/FoodRecipes/Pumpkin_Pie.png"));

        addRecipe(new Recipe("Spaghetti",
            Map.of("wheat flour", 1, "tomato", 1),
            75, "not known", 120, "assets/Inventory/FoodRecipes/Spaghetti.png"));

        addRecipe(new Recipe("Pizza",
            Map.of("wheat flour", 1, "tomato", 1, "cheese", 1),
            150, "Stardrop Saloon", 300, "assets/Inventory/FoodRecipes/Pizza.png"));

        addRecipe(new Recipe("Tortilla",
            Map.of("corn", 1),
            50, "Stardrop Saloon", 50, "assets/Inventory/FoodRecipes/Tortilla.png"));

        addRecipe(new Recipe("Maki Roll",
            Map.of("any fish", 1, "rice", 1, "fiber", 1),
            100, "Stardrop Saloon", 220, "assets/Inventory/FoodRecipes/Maki_Roll.png"));

        addRecipe(new Recipe("Triple Shot Espresso",
            Map.of("coffee", 3),
            200, "Stardrop Saloon", 450, "assets/Inventory/FoodRecipes/Triple_Shot_Espresso.png"));

        addRecipe(new Recipe("Cookie",
            Map.of("wheat flour", 1, "sugar", 1, "egg", 1),
            90, "Stardrop Saloon", 140, "assets/Inventory/FoodRecipes/Cookie.png"));

        addRecipe(new Recipe("Hashbrowns",
            Map.of("potato", 1, "oil", 1),
            90, "Stardrop Saloon", 120, "assets/Inventory/FoodRecipes/Hashbrowns.png"));

        addRecipe(new Recipe("Pancakes",
            Map.of("wheat flour", 1, "egg", 1),
            90, "Stardrop Saloon", 80, "assets/Inventory/FoodRecipes/Pancakes.png"));

        addRecipe(new Recipe("Fruit Salad",
            Map.of("blueberry", 1, "melon", 1, "apricot", 1),
            263, "not known", 450, "assets/Inventory/FoodRecipes/Fruit_Salad.png"));

        addRecipe(new Recipe("Red Plate",
            Map.of("red cabbage", 1, "radish", 1),
            240, "not known", 400, "assets/Inventory/FoodRecipes/Red_Plate.png"));

        addRecipe(new Recipe("Bread",
            Map.of("wheat flour", 1),
            50, "Stardrop Saloon", 60, "assets/Inventory/FoodRecipes/Bread.png"));

        // Special Recipes
        addRecipe(new Recipe("Salmon Dinner",
            Map.of("salmon", 1, "Amaranth", 1, "Kale", 1),
            125, "Leah reward", 300, "assets/Inventory/FoodRecipes/Salmon_Dinner.png"));

        addRecipe(new Recipe("Vegetable Medley",
            Map.of("tomato", 1, "beet", 1),
            165, "Foraging Level 2", 120, "assets/Inventory/FoodRecipes/Vegetable_Medley.png"));

        addRecipe(new Recipe("Farmer's Lunch",
            Map.of("omelet", 1, "parsnip", 1),
            200, "Farming level 1", 150, "assets/Inventory/FoodRecipes/Farmer's_Lunch.png"));

        addRecipe(new Recipe("Survival Burger",
            Map.of("bread", 1, "carrot", 1, "eggplant", 1),
            125, "Foraging level 3", 180, "assets/Inventory/FoodRecipes/Survival_Burger.png"));

        addRecipe(new Recipe("Dish O' The Sea",
            Map.of("sardines", 2, "hash browns", 1),
            150, "Fishing level 2", 220, "assets/Inventory/FoodRecipes/Dish_O'_The_Sea.png"));

        addRecipe(new Recipe("Seafoam Pudding",
            Map.of("Flounder", 1, "midnight carp", 1),
            175, "Fishing level 3", 300, "assets/Inventory/FoodRecipes/Seafoam_Pudding.png"));

        addRecipe(new Recipe("Miner's Treat",
            Map.of("carrot", 2, "sugar", 1, "milk", 1),
            125, "Mining level 1", 200, "assets/Inventory/FoodRecipes/Miner's_Treat.png"));
    }

    private void addRecipe(Recipe recipe) {
        recipes.put(recipe.getName().toLowerCase(), recipe);
    }

    public Recipe getRecipe(String name) {
        return recipes.get(name.toLowerCase());
    }

    public Map<String, Recipe> getRecipes() {
        return recipes;
    }
}
