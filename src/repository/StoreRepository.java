package repository;

import models.Item;
import models.Store;

import java.util.ArrayList;
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
            if ("Blacksmith".equals(store.getName())) {
                store.getItems().addAll(List.of(
                        createItem(13, "Copper", 75),
                        createItem(14, "Iron", 150),
                        createItem(17, "Coal", 150),
                        createItem(15, "Gold", 400)
                ));
                store.setUpgradeCosts(Map.of(1,2000,2,5000,3,10000,4,25000));
                store.setUpgradeBinsCosts(Map.of(1,1000,2,2500,3,5000,4,12500));
            }
            // سایر فروشگاه‌ها را می‌توان اینجا مقداردهی کرد
        }
    }

    private Item createItem(int id, String name, int basePrice) {
        Item item = new models.Item();
        item.setId(id);
        item.setName(name);
        item.setBasePrice(basePrice);
        return item;
    }
}
