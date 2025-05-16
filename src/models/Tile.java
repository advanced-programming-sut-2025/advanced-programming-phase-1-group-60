package models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Tile {
    private final int positionX;
    private final int positionY;
    private StaticElement staticElement;
    private RandomElement randomElement;
    private boolean passable = true;
    private String type;
    private boolean isOccupied;
    private Seeds plantedSeed;
    private Item placedItem;
    private boolean isWatered;
    private boolean isFertilized;
    private boolean isPlowed;
    private boolean isGreenHouseTile;
    private boolean isGiantCrop = false;
    private int daysGrown = 0;
    private int lastWateredDay = -1;
    private boolean readyToHarvest = false;
    private boolean harvested = false;
    private int regrowthCounter = 0;
    private Map<String, Object> properties = new HashMap<>();

    public Tile(int x, int y) { this.positionX= x; this.positionY = y; }

    public Optional<StaticElement> getStaticElement() { return Optional.ofNullable(staticElement); }
    public Optional<RandomElement> getRandomElement() { return Optional.ofNullable(randomElement); }
    public boolean isPassable() { return passable; }

    public void setStaticElement(StaticElement e) {
        this.staticElement = e;
        this.passable = e.isPassable();
    }
    public void setRandomElement(RandomElement e) {
        if (this.randomElement == null && staticElement == null) {
            this.randomElement = e;
            if (!e.isPassable()) passable = false;
        }
    }
    public void setToNormalTile() {
        this.randomElement = null;
        this.type = ".";
        this.passable = true;
    }
    // متدهای getter و setter
    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public Seeds getPlantedSeed() {
        return plantedSeed;
    }

    public void setPlantedSeed(Seeds seed) {
        this.plantedSeed = seed;
        if (seed != null) {
            this.type = "*";
        } else {
            this.type = ".";
        }
    }

    public Item getPlacedItem() {
        return placedItem;
    }

    public void setPlacedItem(Item placedItem) {
        this.placedItem = placedItem;
        this.isOccupied = (placedItem != null);
    }

    public boolean isWatered() {
        return isWatered;
    }

    public void setWatered(boolean watered) {
        isWatered = watered;
    }

    public boolean isFertilized() {
        return isFertilized;
    }

    public void setFertilized(boolean fertilized) {
        isFertilized = fertilized;
    }

    public boolean isPlowed() {
        return isPlowed;
    }

    public void setPlowed(boolean plowed) {
        isPlowed = plowed;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean canPlant() {
        return false;
    }

    public void clearTile() {
        this.placedItem = null;
        this.isOccupied = false;
        this.isWatered = false;
        this.isFertilized = false;
        this.isPlowed = false;
    }
    public boolean isGiantCrop() { return isGiantCrop; }
    public void setGiantCrop(boolean giant) { this.isGiantCrop = giant; }
    public void applyWeatherEffect(String effect) {
    }

    public boolean isGreenHouseTile() {
        return isGreenHouseTile;
    }

    public void setGreenHouseTile(boolean greenHouseTile) {
        isGreenHouseTile = greenHouseTile;
    }
    public int getLastWateredDay() { return lastWateredDay; }
    public void setLastWateredDay(int day) { this.lastWateredDay = day; }
    public void incrementDaysGrown() { daysGrown++; }
    public int getDaysGrown() { return daysGrown; }
    public void setReadyToHarvest(boolean readyToHarvest) {
        this.readyToHarvest = readyToHarvest;
    }
    public boolean isHarvested() { return harvested; }
    public void setDaysGrown(int daysGrown) {
        this.daysGrown = daysGrown;
    }
    public void setHarvested(boolean harvested) { this.harvested = harvested; }
    public void incrementRegrowthCounter() { regrowthCounter++; }
    public int getRegrowthCounter() {
        return regrowthCounter;
    }
    public void setRegrowthCounter(int regrowthCounter) {
        this.regrowthCounter = regrowthCounter;
    }
    public void resetCropFields() {
        daysGrown = 0;
        lastWateredDay = -1;
        readyToHarvest = false;
        harvested = false;
        regrowthCounter = 0;
    }
    public void resetForRegrowth() {
        daysGrown = 0;
        readyToHarvest = false;
        harvested = false;
        regrowthCounter = 0;
    }
    public boolean isReadyToHarvest() {
        return readyToHarvest;
    }

    @Override
    public String toString() {
        if (plantedSeed != null) return "*";
        String elementInfo = (staticElement != null)
                ? staticElement.getClass().getSimpleName()
                : "Empty";
        return String.format("Tile[x=%d, y=%d, Element=%s]", positionX, positionY, elementInfo);
    }
    public void setProperty(String key, Object value) { properties.put(key, value); }
    public Object getProperty(String key) { return properties.get(key); }
}
