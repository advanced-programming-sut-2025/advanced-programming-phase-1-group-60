package models;

import java.time.DayOfWeek;
import java.util.List;
import java.time.LocalTime;

public class Store {
    private String type;
    private Npc creator;
    private WorkTime workTime;
    private List<Product> products;
    private int upgradeLevel;

    public boolean isOpen(LocalTime currentTime) {
        return false;
    }

    public boolean purchaseProduct(User player, Product product, int quantity) {
        return false;
    }

    ;


    public boolean sellProduct(User player, Product product, int quantity) {
        return false;
    }

    ;

    public boolean upgradeStore(User player) {
        return false;
    }

    ;

    public boolean hasProductInStock(Product product, int quantity) {
        return false;
    }

    ;

    public void addProduct(Product product) {
    }

    public void removeProduct(Product product) {
        return;
    }

    public String getType() {
        return null;
    }

    public Npc getCreator() {
        return null;
    }

    public WorkTime getWorkTime() {
        return null;
    }

    ;

    public List<Product> getAvailableProducts() {
        return null;
    }

    public int getUpgradeLevel() {
        return 0;
    }

    ;

    public boolean isOpen(LocalTime time, DayOfWeek day) {
        return false;
    }

    ;


    private class WorkTime {
        private int openTime;
        private int closeTime;
        private boolean[] workingDays;


    }
}