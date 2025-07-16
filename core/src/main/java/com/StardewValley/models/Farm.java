package com.StardewValley.models;

import com.StardewValley.repository.TreeRepository;

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
        // Use a different seed for each farm instance
        Random farmRNG = new Random(System.nanoTime() + homeX + homeY);

        for (int y = 0; y < FarmTemplate.HEIGHT; y++) {
            for (int x = 0; x < FarmTemplate.WIDTH; x++) {
                tiles[y][x] = new Tile(x, y);
            }
        }
        this.homeX = homeX;
        this.homeY = homeY;

        // Place static elements FIRST (structures from template)
        for (var p : tpl.getPlacements()) {
            for (int dy = 0; dy < p.h; dy++) {
                for (int dx = 0; dx < p.w; dx++) {
                    if (p.y + dy < FarmTemplate.HEIGHT && p.y + dy >= 0
                        && p.x + dx < FarmTemplate.WIDTH && p.x + dx >= 0) {
                        tiles[p.y + dy][p.x + dx].setStaticElement(p.element);
                    }
                }
            }
        }

        // THEN spawn random elements - they will avoid static elements
        scatter(30, farmRNG, () -> {
            var repo = TreeRepository.trees;
            int idx = farmRNG.nextInt(repo.size());
            return new Tree(repo.get(idx));
        });
        scatter(20, farmRNG, () -> {
            var minerals = com.StardewValley.repository.ForagingRepository.foragingMinerals;
            int idx = farmRNG.nextInt(minerals.size());
            return new Stone(minerals.get(idx));
        });
        scatterForageItems(60, farmRNG);
    }

    private void scatter(int count, Random rng, Supplier<RandomElement> f) {
        int placed = 0;
        int attempts = 0;
        int maxAttempts = count * 10; // Prevent infinite loops

        while (placed < count && attempts < maxAttempts) {
            int x = rng.nextInt(FarmTemplate.WIDTH);
            int y = rng.nextInt(FarmTemplate.HEIGHT);
            attempts++;

            // Skip spawn position and origin
            if ((x == homeX && y == homeY) || (x == 0 && y == 0)) {
                continue;
            }

            Tile t = tiles[y][x];
            // Check if tile is completely empty (no static element AND no random element)
            if (t.getStaticElement().isEmpty() && t.getRandomElement().isEmpty()) {
                t.setRandomElement(f.get());
                placed++;
            }
        }
    }
    private void scatterForageItems(int count, Random rng) {
        int placed = 0;
        int attempts = 0;
        int maxAttempts = count * 10; // Prevent infinite loops

        while (placed < count && attempts < maxAttempts) {
            int x = rng.nextInt(FarmTemplate.WIDTH);
            int y = rng.nextInt(FarmTemplate.HEIGHT);
            attempts++;

            // Skip spawn position and origin
            if ((x == homeX && y == homeY) || (x == 0 && y == 0)) {
                continue;
            }

            Tile t = tiles[y][x];
            // Check if tile is completely empty (no static element AND no random element)
            if (t.getStaticElement().isEmpty() && t.getRandomElement().isEmpty()) {
                RandomElement forage;
                if (rng.nextBoolean()) {
                    var crops = com.StardewValley.repository.ForagingRepository.foragingCrops;
                    int idx = rng.nextInt(crops.size());
                    forage = crops.get(idx);
                } else {
                    var minerals = com.StardewValley.repository.ForagingRepository.foragingMinerals;
                    int idx = rng.nextInt(minerals.size());
                    forage = minerals.get(idx);
                }
                t.setRandomElement(forage);
                t.setType("F");
                placed++;
            }
        }
    }
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < FarmTemplate.WIDTH && y >= 0 && y < FarmTemplate.HEIGHT) {
            return tiles[y][x];
        }
        return null;
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
    public void spawnDailyForageItems() {
        Random dailyRNG = new Random(System.nanoTime());
        scatterForageItems(5, dailyRNG);
    }
    public void spawnDailyStones(int count) {
        Random dailyRNG = new Random(System.nanoTime() + homeX + homeY);
        int placed = 0;
        int attempts = 0;
        int maxAttempts = count * 10; // Prevent infinite loops

        while (placed < count && attempts < maxAttempts) {
            int x = dailyRNG.nextInt(FarmTemplate.WIDTH);
            int y = dailyRNG.nextInt(FarmTemplate.HEIGHT);
            attempts++;

            Tile t = tiles[y][x];
            // Check if tile is completely empty (no static element AND no random element)
            if (t.getStaticElement().isEmpty() && t.getRandomElement().isEmpty()) {
                var minerals = com.StardewValley.repository.ForagingRepository.foragingMinerals;
                int idx = dailyRNG.nextInt(minerals.size());
                t.setRandomElement(new Stone(minerals.get(idx)));
                t.setType("S");
                placed++;
            }
        }
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
