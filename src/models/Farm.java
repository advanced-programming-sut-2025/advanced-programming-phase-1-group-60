package models;

import java.util.ArrayList;
import java.util.List;

public class Farm {
    private List<Tile> tiles;
    private User owner;
    private List<Buildings> buildings;
    private int width;
    private int height;
    private int id;
    private final List<Item> randomItem = new ArrayList<Item>();
    private final List<Item> staticItems = new ArrayList<Item>();


    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Buildings> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Buildings> buildings) {
        this.buildings = buildings;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void initializeFarm(int width, int height) {}


    public boolean addTile(Tile tile) { return false; }


    public boolean removeTile(int x, int y) { return false; }


    public Tile getTileAt(int x, int y) { return null; }




    public boolean addBuilding(Buildings building) { return false; }


    public boolean removeBuilding(Buildings building) { return false; }


    public boolean canBuildAt(int x, int y, int buildingWidth, int buildingHeight) { return false; }




    public void setRandomItem(Item item) {}


    public Item getStaticItem() { return null; }


    public void setStaticItem(Item item) {}




    public int calculateDailyIncome() { return 0; }


    public void updateDaily() {}


    public void transferOwnership(User newOwner) {}

    private void setRandomItems () {}

    public List<Item> getStaticItems() {
        return staticItems;
    }

    public List<Item> getRandomItem() {
        return randomItem;
    }
}
