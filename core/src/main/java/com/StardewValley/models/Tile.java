// StardewValley/models/Tile.java
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
    private boolean isOccupied; // Existing field
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

    // Added Field: The actual growing crop instance on the tile
    private Crop crop;

    public Tile(int x, int y) {
        this.positionX= x;
        this.positionY = y;
        this.type = "G"; // Default type: Grass
    }

    public Optional<StaticElement> getStaticElement() { return Optional.ofNullable(staticElement); }
    public Optional<RandomElement> getRandomElement() { return Optional.ofNullable(randomElement); }

    // Implemented / Modified: Checks passability based on all elements including crop
    public boolean isPassable() {
        if (staticElement != null && !staticElement.isPassable()) return false;
        if (randomElement != null && !randomElement.isPassable()) return false;
        if (crop != null && !crop.isPassable()) return false; // Check crop's passability
        if (plantedSeed != null) return false; // A tile with a planted seed is not passable
        if (placedItem != null) return false; // An item on the tile might make it impassable (e.g., tool, furniture)

        // Fallback to the tile's own passable property, updated by setters
        return passable;
    }

    // Modified: Updates passability based on the element, considering other contents
    public void setStaticElement(StaticElement e) {
        this.staticElement = e;
        // If the new element is not passable, or if there's an existing crop/seed/item, set to false
        this.passable = (e == null || e.isPassable()) && (crop == null || crop.isPassable()) && plantedSeed == null && placedItem == null;
        updateOccupiedStatus(); // Update occupied status
    }

    // Modified: Updates passability and ensures no other major elements are present
    public void setRandomElement(RandomElement e) {
        // Only set if currently empty of other major blocking elements (staticElement not null, but could be passable)
        // Also ensure no crop, seed, or placed item is present before adding random element.
        if (this.staticElement == null && this.crop == null && this.plantedSeed == null && this.placedItem == null) {
            this.randomElement = e;
            this.passable = (e == null || e.isPassable()); // Update passability based on new random element
            updateOccupiedStatus();
        }
    }

    // Modified: Resets all dynamic elements and state
    public void setToNormalTile() {
        this.randomElement = null;
        this.crop = null; // Clear crop
        this.plantedSeed = null;
        this.placedItem = null;
        this.type = "G"; // Revert to default Grass type
        this.passable = (staticElement == null) || staticElement.isPassable(); // Passable based on static element or default
        updateOccupiedStatus();
        resetCropFields(); // Reset all crop-related flags and counters
        this.isPlowed = false; // Also unplow
        this.isWatered = false;
        this.isFertilized = false;
        this.isGiantCrop = false;
    }

    // Getter and setter methods (unchanged signatures)
    public int getPositionX() { return positionX; }
    public int getPositionY() { return positionY; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Existing `isOccupied` and `setOccupied` are kept as is, but their usage might be derived via `updateOccupiedStatus()`
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; } // This setter can be called externally

    // Helper to keep isOccupied field consistent with content
    private void updateOccupiedStatus() {
        this.isOccupied = (staticElement != null || randomElement != null || plantedSeed != null || crop != null || placedItem != null);
    }

    public Seeds getPlantedSeed() { return plantedSeed; }

    // Modified: Now correctly plants a seed and creates a Crop object
    public void setPlantedSeed(Seeds seed) {
        this.plantedSeed = seed;
        if (seed != null) {
            this.type = "S"; // S for Seeded/Growing
            this.crop = new Crop(seed); // Instantiate the actual Crop object
            this.passable = false; // Planted tile is not passable
            updateOccupiedStatus(); // It's now occupied
            resetCropFields(); // Reset growth fields for the new crop
        } else {
            this.type = (isPlowed ? "P" : "G"); // Revert type if no seed
            this.crop = null; // Clear the crop instance
            this.passable = (staticElement == null || staticElement.isPassable()); // Revert passability based on static element
            updateOccupiedStatus(); // Update occupied status
            resetCropFields(); // Clear crop fields if seed is removed
        }
    }

    // Added Function: Getter for the actual Crop object
    public Optional<Crop> getCrop() {
        return Optional.ofNullable(crop);
    }

    // Added Function: Setter for the actual Crop object (for direct manipulation)
    public void setCrop(Crop crop) {
        this.crop = crop;
        if (crop == null) {
            this.plantedSeed = null; // If crop is removed, seed info is also cleared
            this.type = (isPlowed ? "P" : "G"); // Revert type
            this.passable = (staticElement == null || staticElement.isPassable()); // Revert passability
            updateOccupiedStatus();
            resetCropFields();
        } else {
            this.type = "C"; // 'C' for actively growing crop
            this.passable = false; // Growing crop makes tile impassable
            updateOccupiedStatus();
            // No resetCropFields here as setting a new crop implies new state
        }
    }

    public Item getPlacedItem() { return placedItem; }

    // Modified: Updates occupied status
    public void setPlacedItem(Item placedItem) {
        this.placedItem = placedItem;
        this.passable = (placedItem == null); // Assuming Item has isPassable
        updateOccupiedStatus(); // Update occupied status
    }

    public boolean isWatered() { return isWatered; }
    public void setWatered(boolean watered) { this.isWatered = watered; }

    public boolean isFertilized() { return isFertilized; }
    public void setFertilized(boolean fertilized) { this.isFertilized = fertilized; }

    public boolean isPlowed() { return isPlowed; }

    // Modified: Updates type and passability when plowed
    public void setPlowed(boolean plowed) {
        this.isPlowed = plowed;
        if (plowed) {
            this.type = "P"; // 'P' for Plowed soil
            this.passable = true; // Plowed soil is usually passable
        } else if (crop == null && plantedSeed == null) { // Only revert to grass if no crop/seed
            this.type = "G"; // Revert to grass if unplowed
        }
        updateOccupiedStatus();
    }

    // Implemented Function: Checks if the tile is truly empty (no dynamic elements)
    public boolean isEmpty() {
        // A tile is truly empty if it has no random element, no planted seed,
        // no current crop, and no placed item. Static elements like rocks/trees might persist.
        return randomElement == null && plantedSeed == null && crop == null && placedItem == null;
    }

    // Implemented Function: Determines if a seed can be planted on this tile
    public boolean canPlant() {
        return isPlowed() &&
            isEmpty() && // Must be empty of other items/crops
            isPassable() && // Should be passable for planting operations
            !isGiantCrop(); // Cannot plant on a giant crop space
    }

    // Modified: Clears all dynamic elements and related states
    public void clearTile() {
        this.placedItem = null;
        this.randomElement = null;
        this.crop = null; // Clear the crop
        this.plantedSeed = null;
        this.isWatered = false;
        this.isFertilized = false;
        this.isPlowed = false; // Clear plowed status
        this.isGiantCrop = false;
        this.type = "G"; // Reset to default type (Grass)
        this.passable = (staticElement == null) || staticElement.isPassable(); // Reset passability based on static element or default
        updateOccupiedStatus();
        resetCropFields(); // Reset all crop growth related fields
    }

    public boolean isGiantCrop() { return isGiantCrop; }
    public void setGiantCrop(boolean giant) { this.isGiantCrop = giant; }
    public void applyWeatherEffect(String effect) {
        // Existing empty method - kept as is.
    }

    public boolean isGreenHouseTile() { return isGreenHouseTile; }
    public void setGreenHouseTile(boolean greenHouseTile) { isGreenHouseTile = greenHouseTile; }
    public int getLastWateredDay() { return lastWateredDay; }
    public void setLastWateredDay(int day) { this.lastWateredDay = day; }
    public void incrementDaysGrown() { daysGrown++; } // Days for the tile's crop
    public int getDaysGrown() { return daysGrown; }
    public void setDaysGrown(int daysGrown) { this.daysGrown = daysGrown; } // For loading/saving
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
        // This method is called after a regrowing crop is harvested
        daysGrown = 0; // Days grown resets for next growth cycle
        readyToHarvest = false; // Not ready immediately after harvest
        harvested = false; // Reset harvested flag
        // regrowthCounter is handled by the Crop itself or within harvestCrop logic
    }

    // Modified: Delegates to the Crop object to check if it's ready
    public boolean isReadyToHarvest() {
        return crop != null && crop.isReadyToHarvest();
    }

    // Added Function: Performs the harvest operation on the crop on this tile
    public Optional<Item> harvestCrop() {
        if (crop == null || !crop.isReadyToHarvest()) {
            System.out.println("No crop or crop not ready to harvest at [" + positionX + "," + positionY + "]");
            return Optional.empty();
        }

        List<Item> harvestedItems = crop.harvest(); // The Crop object handles its own harvest logic

        if (harvestedItems.isEmpty()) {
            System.out.println("Crop at [" + positionX + "," + positionY + "] yielded no items.");
        }

        if (crop.canRegrow()) {
            resetForRegrowth(); // Reset tile flags for next growth cycle
            incrementRegrowthCounter(); // Track number of regrowths
            System.out.println("Crop at [" + positionX + "," + positionY + "] harvested. Will regrow.");
            // The Crop object itself has been reset by its own `harvest()` method's call to `regrow()`.
        } else {
            this.setCrop(null); // Clear the Crop object from the tile (one-time harvest)
            this.setPlantedSeed(null); // Clear the seed type
            this.isPlowed = true; // Keep the tile plowed for next planting
            this.type = "P"; // Set type to plowed soil
            System.out.println("Crop at [" + positionX + "," + positionY + "] harvested. Tile is now empty plowed soil.");
        }

        return harvestedItems.isEmpty() ? Optional.empty() : Optional.of(harvestedItems.get(0));
    }


    // Added Function: Checks if the tile is truly empty of dynamic elements (same as isEmpty())
    // This is redundant with isEmpty() but kept as per original user snippet.
    public boolean isTrulyEmpty() {
        return isEmpty();
    }

    // Added Function: Checks if the tile is available for placing a building
    public boolean isAvailableForBuilding() {
        // A tile must be truly empty and passable to be considered available for building.
        // It also must not have a static element (e.g., rock, tree already there).
        return staticElement == null && isEmpty() && isPassable();
    }

    // Modified: Updates toString to reflect the Crop's symbol if present
    @Override
    public String toString() {
        if (isGiantCrop()) return "G"; // Represent giant crop
        if (crop != null) {
            return String.valueOf(crop.getSymbol()); // Use crop's symbol for display
        }
        if (staticElement != null) {
            return String.valueOf(staticElement.symbol()); // Use static element's symbol
        }
        if (randomElement != null) {
            return String.valueOf(randomElement.symbol()); // Use random element's symbol
        }
        if (plantedSeed != null) {
            return String.valueOf(plantedSeed.symbol()); // Use seed's symbol
        }
        if (placedItem != null) {
            return "I"; // Generic symbol for an item placed
        }
        // Fallback to type if nothing else is present
        return type != null && !type.isEmpty() ? type : ".";
    }
    public int getX(){
        return this.positionX;
    }
    public int getY(){
        return this.positionY;
    }
    public void setProperty(String key, Object value) { properties.put(key, value); }
    public Object getProperty(String key) { return properties.get(key); }
}
