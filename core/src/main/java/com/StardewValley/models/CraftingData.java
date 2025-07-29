package com.StardewValley.models;

import java.util.HashMap;
import java.util.Map;


public class CraftingData {
    // زمان تولید برای هر آیتم
    public static final Map<String, Integer> craftingTimes = new HashMap<>();
    // انرژی موردنیاز برای هر آیتم
    public static final Map<String, Object> energyCosts = new HashMap<>();
    // مواد اولیه موردنیاز برای هر آیتم
    public static final Map<String, Map<String, Integer>> requiredMaterials = new HashMap<>();
    // قیمت فروش هر آیتم
    public static final Map<String, Object> prices = new HashMap<>();

    static {
        // زمان تولید (به ساعت یا روز)
        craftingTimes.put("Honey", 52);
        craftingTimes.put("Cheese", 3);
        craftingTimes.put("Goat Cheese", 3);
        craftingTimes.put("Cloth", 4);
        craftingTimes.put("Mayonnaise", 3);
        craftingTimes.put("Duck Mayonnaise", 3);
        craftingTimes.put("Dinosaur Mayonnaise", 3);
        craftingTimes.put("Beer", 13);
        craftingTimes.put("Vinegar", 10);
        craftingTimes.put("Coffee", 2);
        craftingTimes.put("Juice", 52); // Needs further calculation for "Any Vegetable"
        craftingTimes.put("Mead", 10);
        craftingTimes.put("Pale Ale", 39);
        craftingTimes.put("Wine", 91); // Needs further calculation for "Any Fruit"
//        craftingTimes.put("Dried Mushrooms", "Ready the next morning");
//        craftingTimes.put("Dried Fruit", "Ready the next morning");
//        craftingTimes.put("Raisins", "Ready the next morning");
        craftingTimes.put("Coal", 1);
        craftingTimes.put("Pickles", 9); // Needs further calculation for "Any Vegetable"
        craftingTimes.put("Jelly", 39); // Needs further calculation for "Any Fruit"
        craftingTimes.put("Smoked Fish",1);
        craftingTimes.put("Any Metal Bar", 4);

        // انرژی موردنیاز
        energyCosts.put("Honey", 75);
        energyCosts.put("Cheese", 100);
        energyCosts.put("Goat Cheese", 100);
        energyCosts.put("Cloth", "Inedible");
        energyCosts.put("Mayonnaise", 50);
        energyCosts.put("Duck Mayonnaise", 75);
        energyCosts.put("Dinosaur Mayonnaise", 125);
        energyCosts.put("Beer", 50);
        energyCosts.put("Vinegar", 13);
        energyCosts.put("Coffee", 75);
        //energyCosts.put("Juice", "2 × Base Ingredient Energy"); // Needs calculation
        energyCosts.put("Mead", 100);
        energyCosts.put("Pale Ale", 50);
        //energyCosts.put("Wine", "1.75 × Base Fruit Energy"); // Needs calculation
        energyCosts.put("Dried Mushrooms", 50);
        energyCosts.put("Dried Fruit", 75);
        energyCosts.put("Raisins", 125);
        energyCosts.put("Coal", "Inedible");
        //energyCosts.put("Pickles", "1.75 × Base Ingredient Energy"); // Needs calculation
        //energyCosts.put("Jelly", "2 × Base Fruit Energy"); // Needs calculation
        //energyCosts.put("Smoked Fish", "1.5 × Fish Energy"); // Needs calculation
        energyCosts.put("Any Metal Bar", "Inedible");

        // مواد اولیه موردنیاز
        requiredMaterials.put("Honey", Map.of(
                "Wood", 0 // یا Large Milk
        ));
        requiredMaterials.put("Cheese", Map.of(
                "Milk", 1 // یا Large Milk
        ));
        requiredMaterials.put("Goat Cheese", Map.of(
                "Goat Milk", 1 // یا Large Goat Milk
        ));
        requiredMaterials.put("Cloth", Map.of(
                "Wool", 1
        ));
        requiredMaterials.put("Mayonnaise", Map.of(
                "Egg", 1 // یا Large Egg
        ));
        requiredMaterials.put("Duck Mayonnaise", Map.of(
                "Duck Egg", 1
        ));
        requiredMaterials.put("Dinosaur Mayonnaise", Map.of(
                "Dinosaur Egg", 1
        ));
        requiredMaterials.put("Beer", Map.of(
                "Wheat", 1
        ));
        requiredMaterials.put("Vinegar", Map.of(
                "Rice", 1
        ));
        requiredMaterials.put("Coffee", Map.of(
                "Coffee Bean", 5
        ));
        requiredMaterials.put("Mead", Map.of(
                "Honey", 1
        ));
        requiredMaterials.put("Pale Ale", Map.of(
                "Hops", 1
        ));
//        requiredMaterials.put("Wine", Map.of(
//                "Any Fruit", 1 // Needs clarification for specific fruits
//        ));
//        requiredMaterials.put("Dried Mushrooms", Map.of(
//                "Any Mushroom", 5 // Needs clarification
//        ));
//        requiredMaterials.put("Dried Fruit", Map.of(
//                "Any Fruit", 5 // Except Grapes
//        ));
        requiredMaterials.put("Raisins", Map.of(
                "Grapes", 5
        ));
        requiredMaterials.put("Coal", Map.of(
                "Wood", 10
        ));
//        requiredMaterials.put("Pickles", Map.of(
//                "Any Vegetable", 1 // Needs clarification
//        ));
//        requiredMaterials.put("Jelly", Map.of(
//                "Any Fruit", 1 // Needs clarification
//        ));
//        requiredMaterials.put("Smoked Fish", Map.of(
//                "Any Fish", 1, // Needs clarification
//                "Coal", 1
//        ));
//        requiredMaterials.put("Any Metal Bar", Map.of(
//                "Any Ore", 5, // Needs clarification
//                "Coal", 1
//        )
        //);

        // قیمت فروش
        prices.put("Honey", 350);
        prices.put("Cheese", 230); // یا 345
        prices.put("Goat Cheese", 400); // یا 600
        prices.put("Cloth", 470);
        prices.put("Mayonnaise", 190); // یا 237
        prices.put("Duck Mayonnaise", 375);
        prices.put("Dinosaur Mayonnaise", 800);
        prices.put("Beer", 200);
        prices.put("Vinegar", 100);
        prices.put("Coffee", 150);
        prices.put("Mead", 300);
        prices.put("Pale Ale", 300);
//        prices.put("Wine", "3 × Fruit Base Price"); // Needs calculation
//        prices.put("Dried Mushrooms", "7.5 × Mushroom Base Price + 25"); // Needs calculation
//        prices.put("Dried Fruit", "7.5 × Fruit Base Price + 25"); // Needs calculation
        prices.put("Raisins", 600);
        prices.put("Coal", 50);
//        prices.put("Pickles", "2 × Base Price + 50"); // Needs calculation
//        prices.put("Jelly", "2 × Base Fruit Price + 50"); // Needs calculation
//        prices.put("Smoked Fish", "2 × Fish Price"); // Needs calculation
//        prices.put("Any Metal Bar", "10 × Ore Price"); // Needs calculation
    }
    // --- NEW: Helper method to get output product name ---
    /**
     * Determines the output product name for a given artisan building and input material.
     * This is a simplified mapping; in a full game, this might be more robust.
     * @param buildingName The name of the artisan building (e.g., "Furnace").
     * @param inputMaterialName The name of the input material (e.g., "Wood").
     * @return The name of the output product (e.g., "Coal"), or null if no match.
     */
    public static String getOutputProductForInputAndBuilding(String buildingName, String inputMaterialName) {
        // This mapping should ideally be defined centrally, perhaps in CraftingData or GamePlayController.
        // For demonstration, hardcode a few examples.
        // This needs to be comprehensive for all your artisan buildings and their crafts.

        // Furnace example
        if (buildingName.equals("Furnace")) {
            if (inputMaterialName.equals("Wood") && CraftingData.requiredMaterials.containsKey("Coal") &&
                CraftingData.requiredMaterials.get("Coal").containsKey("Wood")) {
                return "Coal";
            }
            // Add other furnace outputs if applicable (e.g., metal bars from ore)
            // if (inputMaterialName.equals("Copper Ore") && ...) return "Copper Bar";
        } else if (buildingName.equals("Bee House")) {
            if (inputMaterialName.equals("Wood") && CraftingData.requiredMaterials.containsKey("Honey") &&
                CraftingData.requiredMaterials.get("Honey").containsKey("Wood")) {
                return "Honey";
            }
        } else if (buildingName.equals("Cheese Press")) {
            if (inputMaterialName.equals("Milk") && CraftingData.requiredMaterials.containsKey("Cheese") &&
                CraftingData.requiredMaterials.get("Cheese").containsKey("Milk")) {
                return "Cheese";
            } else if (inputMaterialName.equals("Goat Milk") && CraftingData.requiredMaterials.containsKey("Goat Cheese") &&
                CraftingData.requiredMaterials.get("Goat Cheese").containsKey("Goat Milk")) {
                return "Goat Cheese";
            }
        } else if (buildingName.equals("Mayonnaise Machine")) {
            if (inputMaterialName.equals("Egg") && CraftingData.requiredMaterials.containsKey("Mayonnaise") &&
                CraftingData.requiredMaterials.get("Mayonnaise").containsKey("Egg")) {
                return "Mayonnaise";
            } else if (inputMaterialName.equals("Duck Egg") && CraftingData.requiredMaterials.containsKey("Duck Mayonnaise") &&
                CraftingData.requiredMaterials.get("Duck Mayonnaise").containsKey("Duck Egg")) {
                return "Duck Mayonnaise";
            } else if (inputMaterialName.equals("Dinosaur Egg") && CraftingData.requiredMaterials.containsKey("Dinosaur Mayonnaise") &&
                CraftingData.requiredMaterials.get("Dinosaur Mayonnaise").containsKey("Dinosaur Egg")) {
                return "Dinosaur Mayonnaise";
            }
        } else if (buildingName.equals("Loom")) {
            if (inputMaterialName.equals("Wool") && CraftingData.requiredMaterials.containsKey("Cloth") &&
                CraftingData.requiredMaterials.get("Cloth").containsKey("Wool")) {
                return "Cloth";
            }
        } else if (buildingName.equals("Keg")) {
            if (inputMaterialName.equals("Wheat") && CraftingData.requiredMaterials.containsKey("Beer") && CraftingData.requiredMaterials.get("Beer").containsKey("Wheat")) {
                return "Beer";
            } else if (inputMaterialName.equals("Hops") && CraftingData.requiredMaterials.containsKey("Pale Ale") && CraftingData.requiredMaterials.get("Pale Ale").containsKey("Hops")) {
                return "Pale Ale";
            } else if (inputMaterialName.equals("Honey") && CraftingData.requiredMaterials.containsKey("Mead") && CraftingData.requiredMaterials.get("Mead").containsKey("Honey")) {
                return "Mead";
            } else if (inputMaterialName.equals("Coffee Bean") && CraftingData.requiredMaterials.containsKey("Coffee") && CraftingData.requiredMaterials.get("Coffee").containsKey("Coffee Bean")) {
                return "Coffee";
            } else if (inputMaterialName.equals("Rice") && CraftingData.requiredMaterials.containsKey("Vinegar") && CraftingData.requiredMaterials.get("Vinegar").containsKey("Rice")) {
                return "Vinegar";
            }
            // You'll need to add logic for "Wine" and "Juice" if they consume "Any Fruit" or "Any Vegetable"
        }
        // ... add more conditions for other artisan buildings and their inputs/outputs

        return null; // No matching output product found for this input material and building
    }
    // --- END NEW ---
}
