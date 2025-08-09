// StardewValley/models/Farm.java
package com.StardewValley.models;

import com.StardewValley.repository.TreeRepository;
import com.StardewValley.repository.ForagingRepository; // Added for clarity, as it's used in scatter

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.Optional; // Added for Optional type hints for isEmpty() checks

public class Farm {
    private Tile[][] tiles = new Tile[FarmTemplate.HEIGHT][FarmTemplate.WIDTH];
    private static final Random RNG = new Random();
    private User owner;
    private List<Buildings> buildings; // This list will be initialized and populated
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
        this.width = FarmTemplate.WIDTH; // Initialize width based on template
        this.height = FarmTemplate.HEIGHT; // Initialize height based on template
        this.buildings = new ArrayList<>(); // Initialize the buildings list here

        // Place static elements FIRST (structures from template)
        for (var p : tpl.getPlacements()) {
            for (int dy = 0; dy < p.h; dy++) {
                for (int dx = 0; dx < p.w; dx++) {
                    if (p.y + dy < FarmTemplate.HEIGHT && p.y + dy >= 0
                        && p.x + dx < FarmTemplate.WIDTH && p.x + dx >= 0) {
                        tiles[p.y + dy][p.x + dx].setStaticElement(p.element);
                        // If the placed element is a Building, add it to the buildings list
                        if (p.element instanceof Buildings) {
                            this.buildings.add((Buildings) p.element);
                        }
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
            var minerals = ForagingRepository.foragingMinerals; // Corrected repository usage
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
            // Check if tile is completely empty (no static element AND no random element AND no crop)
            if (t.getStaticElement().isEmpty() && t.getRandomElement().isEmpty() && t.getCrop().isEmpty()) {
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
            // Check if tile is completely empty (no static element AND no random element AND no crop)
            if (t.getStaticElement().isEmpty() && t.getRandomElement().isEmpty() && t.getCrop().isEmpty()) {
                RandomElement forage;
                if (rng.nextBoolean()) {
                    var crops = ForagingRepository.foragingCrops; // Corrected repository usage
                    int idx = rng.nextInt(crops.size());
                    forage = crops.get(idx);
                } else {
                    var minerals = ForagingRepository.foragingMinerals; // Corrected repository usage
                    int idx = rng.nextInt(minerals.size());
                    forage = minerals.get(idx);
                }
                t.setRandomElement(forage);
                // t.setType("F"); // Removed as setting type directly might override base tile type
                placed++;
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < FarmTemplate.WIDTH && y >= 0 && y < FarmTemplate.HEIGHT) {
            return tiles[y][x]; // Access as tiles[row][col]
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
        // Existing empty method - kept as is.
    }

    public boolean addTile(Tile tile) {
        // Existing empty method - kept as is.
        return false;
    }

    // Implemented logic to clear elements on the tile
    public boolean removeTile(int x, int y) {
        Tile t = getTile(x, y);
        if (t != null) {
            t.setStaticElement(null); // Clear any static element
            t.setRandomElement(null); // Clear any random element
            t.setCrop(null);          // Clear any crop
            return true;
        }
        return false;
    }

    // Implemented to call getTile for consistency
    public Tile getTileAt(int x, int y) {
        return getTile(x, y);
    }

    /**
     * Attempts to place a StaticElement (like a Building) at the specified coordinates.
     * Clears any random elements or crops that were on the tiles.
     *
     * @param building The StaticElement to place.
     * @param startX The X-coordinate of the top-left corner.
     * @param startY The Y-coordinate of the top-left corner.
     * @return true if the building was successfully placed, false otherwise.
     */
    public boolean placeBuilding(StaticElement building, int startX, int startY) {
        if (building == null) {
            System.out.println("Cannot place null building.");
            return false;
        }

        // Check if the area is valid for building
        if (!canBuildAt(startX, startY, building.getWidth(), building.getHeight())) {
            System.out.println("Cannot build " + building.getClass().getSimpleName() + " at [" + startX + "," + startY + "]: Area not valid.");
            return false;
        }

        // Place the building on the tiles
        for (int dy = 0; dy < building.getHeight(); dy++) {
            for (int dx = 0; dx < building.getWidth(); dx++) {
                Tile targetTile = getTile(startX + dx, startY + dy);
                if (targetTile != null) {
                    targetTile.setStaticElement(building);
                    targetTile.setRandomElement(null); // Clear any random elements (stones, trees, forage)
                    targetTile.setCrop(null); // Clear any crops
                }
            }
        }

        // Add to the list of buildings if it's an instance of Buildings
        if (building instanceof Buildings) {
            this.buildings.add((Buildings) building);
            System.out.println(building.getClass().getSimpleName() + " added to buildings list.");
        }
        return true;
    }

    // Implemented to add building to the list. For actual placement on map, use placeBuilding.
    public boolean addBuilding(Buildings building) {
        if (building != null && !this.buildings.contains(building)) {
            this.buildings.add(building);
            return true;
        }
        return false;
    }

    // Implemented logic to remove a building from tiles and the list
    public boolean removeBuilding(Buildings building) {
        if (building == null || !this.buildings.contains(building)) {
            System.out.println("Building is null or not found in the list for removal.");
            return false;
        }

        // Find the building's top-left position on the grid
        int foundX = -1, foundY = -1;
        for (int y = 0; y < FarmTemplate.HEIGHT; y++) {
            for (int x = 0; x < FarmTemplate.WIDTH; x++) {
                Tile t = tiles[y][x];
                // Check if this tile contains the specific building instance
                if (t.getStaticElement().isPresent() && t.getStaticElement().get() == building) {
                    foundX = x;
                    foundY = y;
                    break; // Found the top-left most tile of the building
                }
            }
            if (foundX != -1) break;
        }

        if (foundX != -1) {
            // Clear tiles occupied by the building
            for (int dy = 0; dy < building.getHeight(); dy++) {
                for (int dx = 0; dx < building.getWidth(); dx++) {
                    Tile targetTile = getTile(foundX + dx, foundY + dy);
                    if (targetTile != null && targetTile.getStaticElement().isPresent() && targetTile.getStaticElement().get() == building) {
                        targetTile.setStaticElement(null); // Clear the reference
                        // Optionally, reset tile properties like type, if needed, e.g., to "grass"
                        // targetTile.setType("G");
                    }
                }
            }
            this.buildings.remove(building); // Remove from the farm's list of buildings
            System.out.println(building.getClass().getSimpleName() + " removed from farm and tiles.");
            return true;
        }
        System.out.println("Building " + building.getClass().getSimpleName() + " not found on farm for removal (after initial list check).");
        return false;
    }

    /**
     * Checks if a building of a given width and height can be built at the specified starting coordinates.
     *
     * @param x The X-coordinate of the top-left corner for placement.
     * @param y The Y-coordinate of the top-left corner for placement.
     * @param buildingWidth The width of the building.
     * @param buildingHeight The height of the building.
     * @return true if the area is clear and within bounds, false otherwise.
     */
    public boolean canBuildAt(int x, int y, int buildingWidth, int buildingHeight) {
        // 1. Check if the proposed area is within farm bounds
        if (x < 0 || y < 0 || x + buildingWidth > FarmTemplate.WIDTH || y + buildingHeight > FarmTemplate.HEIGHT) {
            System.out.println("CanBuild: Out of bounds. Proposed: [" + x + "," + y + "], Size: " + buildingWidth + "x" + buildingHeight + ". Farm Max: " + FarmTemplate.WIDTH + "x" + FarmTemplate.HEIGHT);
            return false;
        }

        // 2. Check each tile in the proposed area for obstructions
        for (int dy = 0; dy < buildingHeight; dy++) {
            for (int dx = 0; dx < buildingWidth; dx++) {
                Tile targetTile = getTile(x + dx, y + dy);

                // Defensive check, though bounds check above should prevent null
                if (targetTile == null) {
                    System.out.println("CanBuild: Null tile encountered at " + (x+dx) + "," + (y+dy));
                    return false;
                }

                // Check if tile is occupied by any element or is a special restricted tile
                if (targetTile.getStaticElement().isPresent() || // Occupied by another static element (e.g., house, other building)
                    targetTile.getRandomElement().isPresent() || // Occupied by a random element (e.g., tree, stone, forage)
                    targetTile.getCrop().isPresent() ||          // Occupied by a crop
                    (targetTile.getX() == homeX && targetTile.getY() == homeY) // Cannot build on the player's home entry tile
                    // Add more conditions here if other tile types or positions are restricted (e.g., water tiles, specific immovable features)
                ) {
                    System.out.println("CanBuild: Tile " + (x+dx) + "," + (y+dy) + " is occupied or special.");
                    // Provide details for debugging
                    if (targetTile.getStaticElement().isPresent()) System.out.println("  - Reason: Occupied by StaticElement: " + targetTile.getStaticElement().get().getName());
                    if (targetTile.getRandomElement().isPresent()) System.out.println("  - Reason: Occupied by RandomElement: ");
                    if (targetTile.getCrop().isPresent()) System.out.println("  - Reason: Occupied by Crop: " + targetTile.getCrop().get().getName());
                    if (targetTile.getX() == homeX && targetTile.getY() == homeY) System.out.println("  - Reason: Is player's home entry tile.");
                    return false;
                }
            }
        }
        return true; // All checks passed, area is clear to build
    }


    public void setRandomItem(Item item) {
        // Existing empty method - kept as is.
    }


    public Item getStaticItem() {
        // Existing empty method - kept as is.
        return null;
    }


    public void setStaticItem(Item item) {
        // Existing empty method - kept as is.
    }


    public int calculateDailyIncome() {
        // Existing empty method - kept as is.
        return 0;
    }


    public void updateDaily() {
        for (int y = 0; y < FarmTemplate.HEIGHT; y++) {
            for (int x = 0; x < FarmTemplate.WIDTH; x++) {
                Tile t = tiles[y][x];
                if (t == null) continue;

                // No seed planted -> nothing to do
                Seeds seed = t.getPlantedSeed();
                if (seed == null) continue;

                // If in regrowth cooldown: just tick it down
                if (t.isInRegrowthCooldown()) {
                    t.decrementRegrowthCooldown();
                    // Skip normal growth while cooling down
                    continue;
                }

                // Normal growth (pre-first-harvest OR after just finishing regrowth)
                int total = seed.getTotalHarvestTime();
                if (t.getDaysGrown() < total) {
                    if (t.isWatered()) {
                        t.incrementDaysGrown();
                    }
                }
                // Reset watered status daily (standard farming loop)
                t.setWatered(false);
            }
        }
    }


    public void transferOwnership(User newOwner) {
        this.owner = newOwner;
     //   System.out.println("Farm ownership transferred to " + newOwner.getName());
    }

    private void setRandomItems() {
        // Existing empty method - kept as is.
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
            // Check if tile is completely empty (no static element AND no random element AND no crop)
            if (t.getStaticElement().isEmpty() && t.getRandomElement().isEmpty() && t.getCrop().isEmpty()) {
                var minerals = ForagingRepository.foragingMinerals; // Corrected repository usage
                int idx = dailyRNG.nextInt(minerals.size());
                t.setRandomElement(new Stone(minerals.get(idx)));
                // t.setType("S"); // Removed as setting type directly might override base tile type
                placed++;
            }
        }
    }
    public List<Item> getStaticItems() {
        return staticItems; // Currently always empty based on current usage
    }

    public List<Item> getRandomItem() {
        return randomItem;
    }

    public void processAnimalProducts() {
        // Existing empty method - kept as is.
    }

    public void updateTreesGrowth() {
        // Existing empty method - kept as is.
    }
    public int getHomeX() { return homeX; }
    public int getHomeY() { return homeY; }
}
