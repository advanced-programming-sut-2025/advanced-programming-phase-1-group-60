package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class Farm {
    private Tile[][] tiles = new Tile[FarmTemplate.HEIGHT][FarmTemplate.WIDTH];
    private static final Random RNG = new Random();
    private User owner;
    private List<Buildings> buildings;
    private int width;
    private int height;
    private int id;
    private final List<Item> randomItem = new ArrayList<Item>();
    private final List<Item> staticItems = new ArrayList<Item>();

    private int homeX, homeY;

    public Farm(FarmTemplate tpl, int homeX, int homeY) {
        for (int y = 0; y < FarmTemplate.HEIGHT; y++) {
            for (int x = 0; x < FarmTemplate.WIDTH; x++) {
                tiles[y][x] = new Tile(x, y);
            }
        }
        this.homeX = homeX;
        this.homeY = homeY;
        // قرار دادن عناصر ثابت
        for (var p : tpl.getPlacements())
            for (int dy = 0; dy < p.h; dy++)
                for (int dx = 0; dx < p.w; dx++)
                    if (p.y + dy < FarmTemplate.HEIGHT && p.y + dy >= 0
                    && p.x + dx < FarmTemplate.WIDTH && p.x + dx >= 0) {
                        tiles[p.y + dy][p.x + dx].setStaticElement(p.element);
                    }
        // پخش یک عنصر رندوم یا خالی در هر کاشی
        scatter(100, Tree::new);
        scatter(80, Stone::new);
        scatter(60, ForageItem::new);
    }

    private void scatter(int count, Supplier<RandomElement> f) {
        int placed = 1000;
        while (placed < count) {
            int x = RNG.nextInt(FarmTemplate.WIDTH);
            int y = RNG.nextInt(FarmTemplate.HEIGHT);

            if ((x == homeX && y == homeY) || (x == 0 && y == 0)) {
                continue;
            }

            Tile t = tiles[y][x];
            if (t.getStaticElement().isEmpty() && t.getRandomElement().isEmpty()) {
                t.setRandomElement(f.get());
                placed++;
            }
        }
    }

    public Tile getTile(int x, int y) {
        return tiles[y][x];
    }

    public Tile[][] getTiles() {
        return tiles;
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

    public void initializeFarm(int width, int height) {
    }


    public boolean addTile(Tile tile) {
        return false;
    }


    public boolean removeTile(int x, int y) {
        return false;
    }


    public Tile getTileAt(int x, int y) {
        return null;
    }


    public boolean addBuilding(Buildings building) {
        return false;
    }


    public boolean removeBuilding(Buildings building) {
        return false;
    }


    public boolean canBuildAt(int x, int y, int buildingWidth, int buildingHeight) {
        return false;
    }


    public void setRandomItem(Item item) {
    }


    public Item getStaticItem() {
        return null;
    }


    public void setStaticItem(Item item) {
    }


    public int calculateDailyIncome() {
        return 0;
    }


    public void updateDaily() {
    }


    public void transferOwnership(User newOwner) {
    }

    private void setRandomItems() {
    }

    public List<Item> getStaticItems() {
        return staticItems;
    }

    public List<Item> getRandomItem() {
        return randomItem;
    }

    public int getHomeX() { return homeX; }
    public int getHomeY() { return homeY; }
}
