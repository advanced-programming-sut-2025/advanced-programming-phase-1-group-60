package controller;

import models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

public class HomeController {
        private List<ProcessingMachine> processingMachineList = new ArrayList<>();

        private GameMap currentGameMap;
        private User currentPlayer;

        // جدول طرح‌ها و مواد اولیه لازم برای کرفت
        private static final Map<String, Map<String, Integer>> craftingRecipes = new HashMap<>();

        // انرژی موردنیاز برای ساخت هر آیتم
        private static final int CRAFT_ENERGY_COST = 1;

        // متد مرکزی برای مدیریت تمام عملیات مربوط به کرفتینگ
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

                case "craft_item":
                    if (args.length != 2 || !(args[0] instanceof String) || !(args[1] instanceof User)) {
                        return "Error: Invalid arguments for 'craft_item'.";
                    }
                    String itemName = (String) args[0];
                    User user = (User) args[1];
                    return craftItem(itemName, user);

                case "place_item":
                    if (args.length != 3 || !(args[0] instanceof String) || !(args[1] instanceof Integer) || !(args[2] instanceof User)) {
                        return "Error: Invalid arguments for 'place_item'.";
                    }
                    String placeItemName = (String) args[0];
                    int direction = (Integer) args[1];
                    User placingUser = (User) args[2];
                    return placeItem(placeItemName, direction, placingUser);

                default:
                    return "Error: Unknown crafting command.";
            }
        }

        // نمایش تمامی طرح‌های موجود
        private static String showRecipes() {
            if (craftingRecipes.isEmpty()) {
                return "No recipes available.";
            }

            StringBuilder recipes = new StringBuilder("Available Crafting Recipes:\n");
            for (Map.Entry<String, Map<String, Integer>> recipe : craftingRecipes.entrySet()) {
                recipes.append("- ").append(recipe.getKey()).append(": ");
                recipe.getValue().forEach((material, amount) -> recipes.append(material).append(" x").append(amount).append(", "));
                recipes.setLength(recipes.length() - 2); // حذف کامای اضافی
                recipes.append("\n");
            }
            return recipes.toString();
        }

        // یادگیری طرح جدید
        private static String learnRecipe(String recipeName, Map<String, Integer> materials) {
            if (craftingRecipes.containsKey(recipeName)) {
                return "Error: Recipe '" + recipeName + "' already exists.";
            }

            craftingRecipes.put(recipeName, materials);
            return "Successfully learned new recipe: '" + recipeName + "'.";
        }

        // فرآیند ساخت آیتم
        private static String craftItem(String itemName, User user) {
            if (!craftingRecipes.containsKey(itemName)) {
                return "Error: Recipe for '" + itemName + "' not found.";
            }

            Map<String, Integer> recipe = craftingRecipes.get(itemName);
            Inventory inventory = user.getInventory();

            // بررسی مواد اولیه
            for (Map.Entry<String, Integer> entry : recipe.entrySet()) {
                String material = entry.getKey();
                int requiredAmount = entry.getValue();

                if (!inventory.hasItem(material, requiredAmount)) {
                    return "Error: Not enough " + material + ". You need " + requiredAmount + ".";
                }
            }

            // بررسی انرژی کاربر
            if (user.getEnergy().getCurrentEnergy() < CRAFT_ENERGY_COST) {
                return "Error: Not enough energy to craft.";
            }

            // حذف مواد اولیه از اینونتوری
            for (Map.Entry<String, Integer> entry : recipe.entrySet()) {
                String material = entry.getKey();
                int requiredAmount = entry.getValue();

                // ساخت یک شیء Item با نام material و تعداد requiredAmount
                Item itemToRemove = new Item(material, requiredAmount);

                // حذف آیتم از اینونتوری
                inventory.removeItem(itemToRemove);
            }

            // کم کردن انرژی
            user.getEnergy().decreaseEnergy(CRAFT_ENERGY_COST);

            // اضافه کردن آیتم ساخته‌شده به اینونتوری
            inventory.addItem(new Item(itemName, 1));

            return "Successfully crafted '" + itemName + "'!";
        }

        // قرار دادن آیتم روی زمین
        private static String placeItem(String itemName, int direction, User user) {
            Inventory inventory = user.getInventory();

            // ساخت یک شیء Item برای آیتم موردنظر با تعداد 1
            Item itemToPlace = new Item(itemName, 1);

            // بررسی وجود آیتم در اینونتوری
            if (!inventory.hasItem(itemName, 1)) {
                return "Error: You don't have the item '" + itemName + "' in your inventory.";
            }

            // حذف آیتم از اینونتوری
            inventory.removeItem(itemToPlace);

            // منطق قرار دادن آیتم روی زمین (اینجا مختصات و جهت را مدیریت کنید)
            return "Successfully placed '" + itemName + "' on the ground in direction " + direction + ".";
        }

    public static String addItemToInventory(String itemName, int count, User user) {
        if (count <= 0) {
            return "Error: Count must be greater than zero.";
        }

        Inventory inventory = user.getInventory();
        inventory.addItem(new Item(itemName, count));

        return "Successfully added " + count + " '" + itemName + "' to inventory.";
    }
    }