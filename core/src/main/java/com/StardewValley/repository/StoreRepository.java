package com.StardewValley.repository;

import com.StardewValley.models.Animal;
import com.StardewValley.models.Item;
import com.StardewValley.models.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRepository {
    private static final StoreRepository INSTANCE = new StoreRepository();
    private final List<Store> stores = new ArrayList<>();

    public static StoreRepository getInstance() {
        return INSTANCE;
    }

    public void addStore(Store store) {
        stores.add(store);
    }

    public void initializeStoreItems() {
        for (Store store : stores) {
            if ("Blacksmith".equalsIgnoreCase(store.getName())) {
                store.getItems().addAll(List.of(
                    createItem(13, "Copper_Ore", 75, "assets/Inventory/Copper_Ore.png"),
                    createItem(14, "Iron_Ore", 150, "assets/Inventory/Iron_Ore.png"),
                    createItem(17, "Coal", 150, "assets/Inventory/Coal.png"),
                    createItem(15, "Gold_Ore", 400, "assets/Inventory/Gold_Ore.png")
                ));
                store.setUpgradeCosts(Map.of(1, 2000, 2, 5000, 3, 10000, 4, 25000));
                store.setUpgradeBinsCosts(Map.of(1, 1000, 2, 2500, 3, 5000, 4, 12500));
                store.soldUpgrades.clear();
                store.soldBinsUpgrades.clear();
                store.workTime = new Store.WorkTime(9, 16);
            } else if ("Marin'sRanch".equalsIgnoreCase(store.getName())) {
                store.getItems().addAll(List.of(
                    createItem(220, "Hay", 50, "assets/Inventory/ToolsAndUpgrade/Hay.png"),
                    createItem(227, "Milk Pail", 1000, "assets/Inventory/ToolsAndUpgrade/Milk_Pail.png"),
                    createItem(228, "Shears", 1000, "assets/Inventory/ToolsAndUpgrade/Shears.png")
                ));
                store.animals.addAll(List.of(
                    new Animal("Chicken", "Coop", new String[]{"Coop"}, 800,
                        "assets/Inventory/Animals/Chicken.png"),
                    new Animal("Duck", "Coop", new String[]{"Big Coop", "Deluxe Coop"}, 1200,
                        "assets/Inventory/Animals/Duck.png"),
                    new Animal("Rabbit", "Coop", new String[]{"Deluxe Coop"}, 8000,
                        "assets/Inventory/Animals/Rabbit.png"),
                    new Animal("Dinosaur", "Coop", new String[]{"Big Coop"}, 14000,
                        "assets/Inventory/Animals/Dinosaur.png"),
                    new Animal("Cow", "Barn", new String[]{"Barn"}, 1500,
                        "assets/Inventory/Animals/Cow.png"),
                    new Animal("Goat", "Barn", new String[]{"Big Barn", "Deluxe"}, 4000,
                        "assets/Inventory/Animals/Goat.png"),
                    new Animal("Sheep", "Barn", new String[]{"Deluxe Barn"}, 8000,
                        "assets/Inventory/Animals/Sheep.png"),
                    new Animal("Pig", "Barn", new String[]{"Deluxe Barn"}, 16000,
                        "assets/Inventory/Animals/Pig.png")
                ));
                store.workTime = new Store.WorkTime(9, 16);
            } else if ("Carpenter'sShop".equalsIgnoreCase(store.getName())) {
                store.getItems().addAll(List.of(
                    createBuilding("Barn", 6000, 7, 4, Map.of("Wood", 350, "Stone", 150)),
                    createBuilding("Big Barn", 12000, 7, 4, Map.of("Wood", 450, "Stone", 200)),
                    createBuilding("Deluxe Barn", 25000, 7, 4, Map.of("Wood", 550, "Stone", 300)),
                    createBuilding("Coop", 4000, 6, 3, Map.of("Wood", 300, "Stone", 100)),
                    createBuilding("Big Coop", 10000, 6, 3, Map.of("Wood", 400, "Stone", 150)),
                    createBuilding("Deluxe Coop", 20000, 6, 3, Map.of("Wood", 500, "Stone", 200)),
                    createBuilding("Well", 1000, 3, 3, Map.of("Stone", 75)),
                    createBuilding("Shipping Bin", 250, 1, 1, Map.of("Wood", 150))
                ));
                store.soldBuildings.clear();
                store.workTime = new Store.WorkTime(9, 20);
            } else if ("Fish Shop".equalsIgnoreCase(store.getName())) {
                store.upgradePoleCosts = Map.of(0, 25, 1, 500, 2, 1800, 3, 7500);
                store.isFishSmokerSold = false;
                store.isTroutSoupSold = false;
                store.soldPoleUpgrades.clear();
                store.workTime = new Store.WorkTime(9, 17);
            } else if ("The Stardrop Saloon".equalsIgnoreCase(store.getName())) {
                store.addRecipe("Hashbrowns", 50);
                store.addRecipe("Omelet", 100);
                store.addRecipe("Pancakes", 100);
                store.addRecipe("Bread", 100);
                store.addRecipe("Tortilla", 100);
                store.addRecipe("Pizza", 150);
                store.addRecipe("Maki Roll", 300);
                store.addRecipe("Triple Shot Espresso", 5000);
                store.addRecipe("Cookie", 300);
                store.addFoodItem(createFoodItem(
                    "Beer",
                    400,
                    0,
                    "assets/Inventory/Food/Beer.png"
                ));

                store.addFoodItem(createFoodItem(
                    "Salad",
                    220,
                    113,
                    "assets/Inventory/FoodRecipes/Salad.png"
                ));

                store.addFoodItem(createFoodItem(
                    "Bread",
                    120,
                    50,
                    "assets/Inventory/FoodRecipes/Bread.png"
                ));
                store.addFoodItem(createFoodItem(
                    "Spaghetti",
                    240,
                    75,
                    "assets/Inventory/FoodRecipes/Spaghetti.png"
                ));
                store.addFoodItem(createFoodItem(
                    "Pizza",
                    240,
                    150,
                    "assets/Inventory/FoodRecipes/Pizza.png"
                ));
                store.addFoodItem(createFoodItem(
                    "Coffee",
                    300,
                    60,
                    "assets/Inventory/Food/Coffee.png"
                ));
                store.soldRecipes.clear();
                store.workTime = new Store.WorkTime(12, 24);
            }
        }
    }

    private Item createItem(int id, String name, int storePrice, String path) {
        Item item = new com.StardewValley.models.Item();
        item.setId(id);
        item.setName(name);
        item.setStorePrice(storePrice);
        item.setPath(path);
        return item;
    }

    private Item createBuilding(String name, int cost, int width, int height, Map<String, Integer> materials) {
        Item item = new Item();
        item.setName(name);
        item.setStorePrice(cost);

        HashMap<String, Object> props = new HashMap<>();
        props.put("width", width);
        props.put("height", height);
        props.put("materials", materials);
        item.setProperties(props);

        return item;
    }

    private Item createFoodItem(String name, int price, int energy, String path) {
        Item item = new Item();
        item.setName(name);
        item.setStorePrice(price);
        item.setType("Food");
        item.getProperties().put("energy", energy);
        item.setPath(path);
        item.setQuantity(1);
        return item;
    }
}
