package models;

import java.util.List;

public class Animals {
    private String name;
    private int price;
    private List<Product> products;
    private Buildings home;
    private int friendshipLevel;
    private boolean isFed;
    private boolean isPetToday;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Buildings getHome() {
        return home;
    }

    public void setHome(Buildings home) {
        this.home = home;
    }

    public int getFriendshipLevel() {
        return friendshipLevel;
    }

    public void setFriendshipLevel(int friendshipLevel) {
        this.friendshipLevel = friendshipLevel;
    }

    public boolean isFed() {
        return isFed;
    }

    public void setFed(boolean fed) {
        isFed = fed;
    }

    public void setPetToday(boolean petToday) {
        isPetToday = petToday;
    }

    public void pet() {}


    public void feed(boolean useHay) {}


    public Product produceProduct() { return null; }


    public int sell() { return 0; }


    public boolean isReadyToProduce() { return false; }


    public void moveToNewHome(Buildings newHome) {}




    public boolean isPetToday() { return false; }


    public boolean isFedToday() { return false; }


    public void increaseFriendship(int amount) {}
}
