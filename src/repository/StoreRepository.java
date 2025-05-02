package repository;

import models.Item;
import models.Store;

import java.util.HashMap;
import java.util.List;

public class StoreRepository {
    private static StoreRepository instance;
    private List<Store> stores;

    static {

        // blacksmith
        Store blacksmithStore = new Store();
        Item bsItem1 = new Item();
        bsItem1.setName("Copper");
        bsItem1.setId(13);
        bsItem1.setBasePrice(75);
        blacksmithStore.getItems().add(bsItem1);
        Item bsItem2 = new Item();
        bsItem2.setName("Iron");
        bsItem2.setId(14);
        bsItem2.setBasePrice(150);
        blacksmithStore.getItems().add(bsItem2);
        Item bsItem3 = new Item();
        bsItem3.setName("Coal");
        bsItem3.setId(17);
        bsItem3.setBasePrice(150);
        blacksmithStore.getItems().add(bsItem3);
        Item bsItem4 = new Item();
        bsItem4.setName("Gold");
        bsItem4.setId(15);
        bsItem4.setBasePrice(400);
        blacksmithStore.getItems().add(bsItem4);
        HashMap <Integer , Integer> upgradeCosts = new HashMap<>();
        upgradeCosts.put(1, 2000); upgradeCosts.put(2, 5000); upgradeCosts.put(3, 10000); upgradeCosts.put(4, 25000);
        blacksmithStore.setUpgradeCosts(upgradeCosts);
        HashMap <Integer , Integer> upgradeBinsCosts = new HashMap<>();
        upgradeBinsCosts.put(1, 1000); upgradeBinsCosts.put(2, 2500); upgradeBinsCosts.put(3, 5000);
        upgradeBinsCosts.put(4, 12500);
        blacksmithStore.setUpgradeBinsCosts(upgradeBinsCosts);
        // ---------------------------------------------------------------------------
        // marin's ranch
        Store marinesRanchStore = new Store();
        Item mrItem1 = new Item();
        mrItem1.setName("Hay");
        mrItem1.setId(220);
        mrItem1.setBasePrice(50);
        marinesRanchStore.getItems().add(mrItem1);
        Item mrItem2 = new Item();




    }


}
