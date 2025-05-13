package repository;

import models.Animal;
import models.Item;
import models.Store;

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
                        createItem(13, "Copper", 75),
                        createItem(14, "Iron", 150),
                        createItem(17, "Coal", 150),
                        createItem(15, "Gold", 400)
                ));
                store.setUpgradeCosts(Map.of(1,2000,2,5000,3,10000,4,25000));
                store.setUpgradeBinsCosts(Map.of(1,1000,2,2500,3,5000,4,12500));
                store.soldUpgrades.clear();
                store.soldBinsUpgrades.clear();
            } else if ("Marin'sRanch".equalsIgnoreCase(store.getName())) {
                store.getItems().addAll(List.of(
                        createItem(220, "Hay", 50),
                        createItem(227, "Milk Pail", 1000),
                        createItem(228, "Shears", 1000)
                ));
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
            }
        }
    }

    private Item createItem(int id, String name, int storePrice) {
        Item item = new models.Item();
        item.setId(id);
        item.setName(name);
        item.setStorePrice(storePrice);
        return item;
    }

    private Item createBuilding(String name, int cost, int width, int height, Map<String, Integer> materials) {
        Item item = new Item();
        item.setName(name);
        item.setStorePrice(cost);

        // ذخیره خصوصیات در properties
        HashMap<String, Object> props = new HashMap<>();
        props.put("width", width);
        props.put("height", height);
        props.put("materials", materials);
        item.setProperties(props);

        return item;
    }
}
