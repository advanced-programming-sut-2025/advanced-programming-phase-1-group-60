// StardewValley/models/Crop.java
package com.StardewValley.models;

import java.util.ArrayList;
import java.util.List;

public class Crop {
    private String name;
    private int currentGrowthStage; // Represents days grown
    private int[] growthStages; // Days at which the crop visually changes stages
    private boolean isWatered;
    private boolean hasFertilizer;
    private String suitableSeason;
    private boolean isGiant;
    //private LocalDate plantedDate; // Existing commented field - kept as is.

    // Added Field: To store the FruitsAndVegetables definition for harvest/regrowth logic
    private FruitsAndVegetables harvestedProduceInfo;
    // Added Field: Total days from planting to first harvest
    private int totalGrowthDaysRequired;
    // Added Field: Internal flag for harvest readiness
    private boolean readyToHarvestInternal;

    // Added Function: Constructor to initialize a Crop from Seeds
    public Crop(Seeds seed) {
        if (seed == null) {
            throw new IllegalArgumentException("Seeds cannot be null when creating a Crop.");
        }
        this.name = seed.getGrowsInto();
        this.suitableSeason = seed.getSuitableSeasons().isEmpty() ? "Any" : seed.getSuitableSeasons().get(0);
        this.isGiant = false; // Initial state
        this.currentGrowthStage = 0; // Start at 0 days grown
        this.totalGrowthDaysRequired = seed.getTotalHarvestTime();
        // Calculate visual growth stages based on total harvest time
        this.growthStages = calculateGrowthStages(this.totalGrowthDaysRequired);
        this.harvestedProduceInfo = seed.getAssociatedProduceInfo(); // Use the new getter from Seeds

        this.isWatered = false;
        this.hasFertilizer = false;
        this.readyToHarvestInternal = false;
    }

    // Helper method to determine visual growth stages
    private int[] calculateGrowthStages(int totalDays) {
        if (totalDays <= 0) return new int[]{};
        List<Integer> stages = new ArrayList<>();
        // Simple 3-stage visual progression example: 1/3, 2/3, and final day
        int stage1 = totalDays / 3;
        int stage2 = 2 * totalDays / 3;

        if (stage1 > 0) stages.add(stage1);
        if (stage2 > 0 && stage2 != stage1) stages.add(stage2);
        stages.add(totalDays); // Final stage is always the total days required

        return stages.stream().mapToInt(Integer::intValue).distinct().toArray();
    }


    public boolean isHasFertilizer() {
        return hasFertilizer;
    }

    public void setHasFertilizer(boolean hasFertilizer) {
        this.hasFertilizer = hasFertilizer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentGrowthStage() {
        return currentGrowthStage;
    }

    public void setCurrentGrowthStage(int currentGrowthStage) {
        this.currentGrowthStage = currentGrowthStage;
    }

    public int[] getGrowthStages() {
        return growthStages;
    }

    public void setGrowthStages(int[] growthStages) {
        this.growthStages = growthStages;
    }

    public boolean isWatered() {
        return isWatered;
    }

    public void setWatered(boolean watered) {
        isWatered = watered;
    }

    public String getSuitableSeason() {
        return suitableSeason;
    }

    public void setSuitableSeason(String suitableSeason) {
        this.suitableSeason = suitableSeason;
    }

    public boolean isGiant() {
        return isGiant;
    }

    public void setGiant(boolean giant) {
        isGiant = giant;
    }

    // Implemented Function: Advances the crop's growth by one day
    public void growDaily() {
        if (isWatered && !readyToHarvestInternal) {
            currentGrowthStage++;
            // Check if current growth stage makes it ready for harvest
            if (currentGrowthStage >= totalGrowthDaysRequired) {
                this.readyToHarvestInternal = true;
            }
            // Logic for fertilizer speed-up could be added here
        }
    }

    // Implemented Function: Checks if the crop is ready to be harvested
    public boolean isReadyToHarvest() {
        return readyToHarvestInternal;
    }

    // Implemented Function: Returns the name of the crop
    public String getName() { return name;}

    // Implemented Function: Returns days spent in current stage (simple implementation)
    public int getCurrentStageDays() {
        // This could be more complex, tracking days within each visual stage.
        // For simplicity, returning the total days grown if not ready to harvest, or total required if ready.
        return currentGrowthStage;
    }

    // Added Function: Performs the harvest action for the crop
    public List<Item> harvest() {
        List<Item> harvestedItems = new ArrayList<>();
        if (isReadyToHarvest() && harvestedProduceInfo != null) {
            // Add the main harvested item(s). Quantity and quality could be varied.
            harvestedItems.add(new Item(harvestedProduceInfo.getName(), 1)); // Assuming 1 unit for simplicity

            if (canRegrow()) {
                regrow(); // Reset internal state for regrowth
            } else {
                // If one-time harvest, reset crop state so it can be cleared from tile
                this.currentGrowthStage = 0;
                this.readyToHarvestInternal = false;
                this.isWatered = false; // No longer watered
                this.hasFertilizer = false; // Fertilizer effect consumed
            }
        }
        return harvestedItems;
    }

    // Added Function: Checks if the crop can regrow after harvest
    public boolean canRegrow() {
        return harvestedProduceInfo != null && !harvestedProduceInfo.isOneTime();
    }

    // Added Function: Resets the crop's growth for its next regrowth cycle
    public void regrow() {
        if (canRegrow() && harvestedProduceInfo.getRegrowthTime() != null) {
            // Set growth stage back so it needs 'regrowthTime' more days to be ready
            this.currentGrowthStage = totalGrowthDaysRequired - harvestedProduceInfo.getRegrowthTime();
            this.readyToHarvestInternal = false; // Not ready immediately
            this.isWatered = false; // Reset watered status for next growth cycle
            this.hasFertilizer = false; // Reset fertilizer
        } else if (canRegrow() && harvestedProduceInfo.getRegrowthTime() == null) {
            // Edge case for immediate regrowth if regrowthTime isn't set but can regrow
            this.currentGrowthStage = 0;
            this.readyToHarvestInternal = false;
            this.isWatered = false;
            this.hasFertilizer = false;
        }
    }

    // Added Function: Provides a character symbol for the crop's current visual state
    public char getSymbol() {
        if (isGiant) return 'G'; // Symbol for a giant crop
        if (currentGrowthStage == 0) return 's'; // Symbol for a newly planted seed/sprout
        if (isReadyToHarvest()) return 'H'; // Symbol for a crop ready to harvest

        // Determine symbol based on current growth stage relative to total stages
        for (int i = 0; i < growthStages.length; i++) {
            if (currentGrowthStage < growthStages[i]) {
                // Map stage index to a symbol
                switch (i) {
                    case 0: return '-'; // Early growth stage
                    case 1: return '+'; // Mid growth stage
                    case 2: return '*'; // Late growth stage (pre-harvest)
                    default: return '#'; // Default growing symbol
                }
            }
        }
        return '#'; // Default if no specific stage matches
    }

    // Added Function: Checks if the crop makes the tile impassable (assuming crops block movement)
    public boolean isPassable() {
        return false; // Growing crops are generally not passable in Stardew Valley
    }
}
