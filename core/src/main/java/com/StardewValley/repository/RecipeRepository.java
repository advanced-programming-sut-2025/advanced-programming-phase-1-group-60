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
        addRecipe(new Recipe("Fried egg",
                Map.of("egg", 1),
                50, "Starter", 35));

        addRecipe(new Recipe("Baked Fish",
                Map.of("Sardine", 1, "Salmon", 1, "wheat", 1),
                75, "Starter", 100));

        addRecipe(new Recipe("Salad",
                Map.of("leek", 1, "dandelion", 1),
                113, "Starter", 110));

        // Stardrop Saloon Recipes
        addRecipe(new Recipe("Omelet",
                Map.of("egg", 1, "milk", 1),
                100, "Stardrop Saloon", 125));

        addRecipe(new Recipe("pumpkin pie",
                Map.of("pumpkin", 1, "wheat flour", 1, "milk", 1, "sugar", 1),
                225, "Stardrop Saloon", 385));

        addRecipe(new Recipe("spaghetti",
                Map.of("wheat flour", 1, "tomato", 1),
                75, "Stardrop Saloon", 120));

        addRecipe(new Recipe("pizza",
                Map.of("wheat flour", 1, "tomato", 1, "cheese", 1),
                150, "Stardrop Saloon", 300));

        addRecipe(new Recipe("Tortilla",
                Map.of("corn", 1),
                50, "Stardrop Saloon", 50));

        addRecipe(new Recipe("Maki Roll",
                Map.of("any fish", 1, "rice", 1, "fiber", 1),
                100, "Stardrop Saloon", 220));

        addRecipe(new Recipe("Triple Shot Espresso",
                Map.of("coffee", 3),
                200, "Stardrop Saloon", 450));

        addRecipe(new Recipe("Cookie",
                Map.of("wheat flour", 1, "sugar", 1, "egg", 1),
                90, "Stardrop Saloon", 140));

        addRecipe(new Recipe("hash browns",
                Map.of("potato", 1, "oil", 1),
                90, "Stardrop Saloon", 120));

        addRecipe(new Recipe("pancakes",
                Map.of("wheat flour", 1, "egg", 1),
                90, "Stardrop Saloon", 80));

        addRecipe(new Recipe("fruit salad",
                Map.of("blueberry", 1, "melon", 1, "apricot", 1),
                263, "Stardrop Saloon", 450));

        addRecipe(new Recipe("red plate",
                Map.of("red cabbage", 1, "radish", 1),
                240, "Stardrop Saloon", 400));

        addRecipe(new Recipe("bread",
                Map.of("wheat flour", 1),
                50, "Stardrop Saloon", 60));

        // Special Recipes
        addRecipe(new Recipe("salmon dinner",
                Map.of("salmon", 1, "Amaranth", 1, "Kale", 1),
                125, "Leah reward", 300));

        addRecipe(new Recipe("vegetable medley",
                Map.of("tomato", 1, "beet", 1),
                165, "Foraging Level 2", 120));

        addRecipe(new Recipe("farmer's lunch",
                Map.of("omelet", 1, "parsnip", 1),
                200, "Farming level 1", 150));

        addRecipe(new Recipe("survival burger",
                Map.of("bread", 1, "carrot", 1, "eggplant", 1),
                125, "Foraging level 3", 180));

        addRecipe(new Recipe("dish O' the Sea",
                Map.of("sardines", 2, "hash browns", 1),
                150, "Fishing level 2", 220));

        addRecipe(new Recipe("seaform Pudding",
                Map.of("Flounder", 1, "midnight carp", 1),
                175, "Fishing level 3", 300));

        addRecipe(new Recipe("miner's treat",
                Map.of("carrot", 2, "sugar", 1, "milk", 1),
                125, "Mining level 1", 200));
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
