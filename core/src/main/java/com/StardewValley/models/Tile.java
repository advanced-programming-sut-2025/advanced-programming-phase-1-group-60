package com.StardewValley.models;

import java.util.HashMap;
import java.util.List;
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
    private String fertilizerType;
    private boolean fertilizerApplied;

    // Multi-harvest regrowth support (new):
    private int regrowthCooldownDays = 0; // >0 means in regrowth waiting period (show Stage_0)
    private boolean showMultiHarvestBase = false;

    private Crop crop;

    public Tile(int x, int y) {
        this.positionX= x;
        this.positionY = y;
        this.type = "G";
        this.fertilizerType = null;
        this.fertilizerApplied = false;
    }

    public Optional<StaticElement> getStaticElement() { return Optional.ofNullable(staticElement); }
    public Optional<RandomElement> getRandomElement() { return Optional.ofNullable(randomElement); }

    public boolean isPassable() {
        if (staticElement != null && !staticElement.isPassable()) return false;
        if (randomElement != null && !randomElement.isPassable()) return false;
        if (crop != null && !crop.isPassable()) return false;
        if (plantedSeed != null) return false;
        if (placedItem != null) return false;
        return passable;
    }

    public void setStaticElement(StaticElement e) {
        this.staticElement = e;
        this.passable = (e == null || e.isPassable()) && (crop == null || crop.isPassable()) && plantedSeed == null && placedItem == null;
        updateOccupiedStatus();
    }

    public void setRandomElement(RandomElement e) {
        if (this.staticElement == null && this.crop == null && this.plantedSeed == null && this.placedItem == null) {
            this.randomElement = e;
            this.passable = (e == null || e.isPassable());
            updateOccupiedStatus();
        }
    }

    public void setToNormalTile() {
        this.randomElement = null;
        this.crop = null;
        this.plantedSeed = null;
        this.placedItem = null;
        this.type = "G";
        this.passable = (staticElement == null) || staticElement.isPassable();
        updateOccupiedStatus();
        resetCropFields();
        this.isPlowed = false;
        this.isWatered = false;
        this.isFertilized = false;
        this.isGiantCrop = false;
        this.regrowthCooldownDays = 0;
        this.showMultiHarvestBase = false;
    }

    public int getPositionX() { return positionX; }
    public int getPositionY() { return positionY; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }

    private void updateOccupiedStatus() {
        this.isOccupied = (staticElement != null || randomElement != null || plantedSeed != null || crop != null || placedItem != null);
    }

    public Seeds getPlantedSeed() { return plantedSeed; }

    public void setPlantedSeed(Seeds seed) {
        this.plantedSeed = seed;
        if (seed != null) {
            this.type = "S";
            this.crop = new Crop(seed);
            this.passable = false;
            updateOccupiedStatus();
            resetCropFields();
            this.regrowthCooldownDays = 0;
            this.showMultiHarvestBase = false;
        } else {
            this.type = (isPlowed ? "P" : "G");
            this.crop = null;
            this.passable = (staticElement == null || staticElement.isPassable());
            updateOccupiedStatus();
            resetCropFields();
            this.regrowthCooldownDays = 0;
            this.showMultiHarvestBase = false;
        }
    }

    // Fertilizer state
    public boolean hasFertilizer() { return fertilizerType != null; }
    public String getFertilizerType() { return fertilizerType; }
    public void setFertilizerType(String fertilizerType) { this.fertilizerType = fertilizerType; }
    public boolean isFertilizerApplied() { return fertilizerApplied; }
    public void setFertilizerApplied(boolean fertilizerApplied) { this.fertilizerApplied = fertilizerApplied; }

    public Optional<Crop> getCrop() { return Optional.ofNullable(crop); }
    public void setCrop(Crop crop) {
        this.crop = crop;
        if (crop == null) {
            this.plantedSeed = null;
            this.type = (isPlowed ? "P" : "G");
            this.passable = (staticElement == null || staticElement.isPassable());
            updateOccupiedStatus();
            resetCropFields();
            this.regrowthCooldownDays = 0;
            this.showMultiHarvestBase = false;
        } else {
            this.type = "C";
            this.passable = false;
            updateOccupiedStatus();
        }
    }

    public Item getPlacedItem() { return placedItem; }
    public void setPlacedItem(Item placedItem) {
        this.placedItem = placedItem;
        this.passable = (placedItem == null);
        updateOccupiedStatus();
    }

    public boolean isWatered() { return isWatered; }
    public void setWatered(boolean watered) { this.isWatered = watered; }

    public boolean isFertilized() { return isFertilized; }
    public void setFertilized(boolean fertilized) { this.isFertilized = fertilized; }

    public boolean isPlowed() { return isPlowed; }
    public void setPlowed(boolean plowed) {
        this.isPlowed = plowed;
        if (plowed) {
            this.type = "P";
            this.passable = true;
        } else if (crop == null && plantedSeed == null) {
            this.type = "G";
        }
        updateOccupiedStatus();
    }

    public boolean isEmpty() {
        return randomElement == null && plantedSeed == null && crop == null && placedItem == null;
    }

    public boolean canPlant() {
        return isPlowed() && isEmpty() && isPassable() && !isGiantCrop();
    }

    public void clearTile() {
        this.placedItem = null;
        this.randomElement = null;
        this.crop = null;
        this.plantedSeed = null;
        this.isWatered = false;
        this.isFertilized = false;
        this.isPlowed = false;
        this.isGiantCrop = false;
        this.type = "G";
        this.passable = (staticElement == null) || staticElement.isPassable();
        updateOccupiedStatus();
        resetCropFields();
        this.regrowthCooldownDays = 0;
        this.showMultiHarvestBase = false;
    }

    public boolean isGiantCrop() { return isGiantCrop; }
    public void setGiantCrop(boolean giant) { this.isGiantCrop = giant; }

    public boolean isGreenHouseTile() { return isGreenHouseTile; }
    public void setGreenHouseTile(boolean greenHouseTile) { isGreenHouseTile = greenHouseTile; }

    public int getLastWateredDay() { return lastWateredDay; }
    public void setLastWateredDay(int day) { this.lastWateredDay = day; }

    public void incrementDaysGrown() { daysGrown++; }
    public int getDaysGrown() { return daysGrown; }
    public void setDaysGrown(int daysGrown) { this.daysGrown = daysGrown; }

    public void setReadyToHarvest(boolean readyToHarvest) { this.readyToHarvest = readyToHarvest; }
    public boolean isHarvested() { return harvested; }
    public void setHarvested(boolean harvested) { this.harvested = harvested; }
    public void incrementRegrowthCounter() { regrowthCounter++; }
    public int getRegrowthCounter() { return regrowthCounter; }
    public void setRegrowthCounter(int regrowthCounter) { this.regrowthCounter = regrowthCounter; }

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
    }

    public boolean isReadyToHarvest() {
        return crop != null && crop.isReadyToHarvest();
    }

    public Optional<Item> harvestCrop() {
        if (crop == null || !crop.isReadyToHarvest()) return Optional.empty();
        java.util.List<Item> harvestedItems = crop.harvest();
        if (crop.canRegrow()) {
            resetForRegrowth();
            incrementRegrowthCounter();
        } else {
            this.setCrop(null);
            this.setPlantedSeed(null);
            this.isPlowed = true;
            this.type = "P";
        }
        return harvestedItems.isEmpty() ? Optional.empty() : Optional.of(harvestedItems.get(0));
    }

    public boolean isTrulyEmpty() { return isEmpty(); }
    public boolean isAvailableForBuilding() {
        return staticElement == null && isEmpty() && isPassable();
    }

    public boolean isInRegrowthCooldown() {
        return regrowthCooldownDays > 0;
    }

    public int getRegrowthCooldownDays() {
        return regrowthCooldownDays;
    }

    public void setRegrowthCooldownDays(int days) {
        this.regrowthCooldownDays = Math.max(0, days);
    }

    public void decrementRegrowthCooldown() {
        if (regrowthCooldownDays > 0) regrowthCooldownDays--;
        if (regrowthCooldownDays == 0) {
            showMultiHarvestBase = false;
            // Instantly return to harvest-ready (last stage) for regrowable crops.
            if (plantedSeed != null) {
                // Set daysGrown directly to totalHarvestTime so rendering shows final stage
                this.daysGrown = Math.max(1, plantedSeed.getTotalHarvestTime());
                // Optionally mark watered false; player must re‑water if your logic requires it.
                // this.isWatered = false;
            }
        }
    }

    public boolean shouldShowMultiHarvestBase() {
        return showMultiHarvestBase && regrowthCooldownDays > 0;
    }

    public void activateMultiHarvestBase(int cooldownDays) {
        this.showMultiHarvestBase = true;
        this.regrowthCooldownDays = cooldownDays;
        this.daysGrown = 0; // restart growth after cooldown finishes
    }

    @Override
    public String toString() {
        if (isGiantCrop()) return "G";
        if (crop != null) return String.valueOf(crop.getSymbol());
        if (staticElement != null) return String.valueOf(staticElement.symbol());
        if (randomElement != null) return String.valueOf(randomElement.symbol());
        if (plantedSeed != null) return String.valueOf(plantedSeed.symbol());
        if (placedItem != null) return "I";
        return type != null && !type.isEmpty() ? type : ".";
    }

    public int getX(){ return this.positionX; }
    public int getY(){ return this.positionY; }

    public void setProperty(String key, Object value) { properties.put(key, value); }
    public Object getProperty(String key) { return properties.get(key); }
}
