package models;

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
}