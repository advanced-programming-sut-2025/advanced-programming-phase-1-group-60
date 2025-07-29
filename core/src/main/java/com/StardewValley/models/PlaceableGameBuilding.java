package com.StardewValley.models;

import java.util.HashMap; // Required for initializing properties map

/**
 * This class represents a building that can be placed on the map (implements StaticElement)
 * and has game-specific mechanics (extends Buildings).
 * You will need to fill in the specific logic for the abstract methods inherited from Buildings.
 */
public class PlaceableGameBuilding extends Buildings implements StaticElement {
    private String displaySymbol; // Used for StaticElement's symbol() and getDisplaySymbol()

    /**
     * Constructor for creating a new PlaceableGameBuilding instance.
     * @param name The display name of the building (e.g., "Bee House").
     * @param symbol A single character or short string representing the building on the map (e.g., "B", "BH").
     * @param width The width of the building in tiles.
     * @param height The height of the building in tiles.
     * @param constructionCost The cost to construct this building.
     * @param maintenanceCost The daily/weekly maintenance cost for this building.
     */
    public PlaceableGameBuilding(String name, String symbol, int width, int height,
                                 int constructionCost, int maintenanceCost) {
        // Set properties inherited from the abstract Buildings class
        this.setBuildingType(name);
        this.setWidth(width);
        this.setHeight(height);
        this.setConstructionCost(constructionCost);
        this.setMaintenanceCost(maintenanceCost);
        this.setBuilt(false); // Buildings are typically not built initially when defined as a blueprint
        this.setUpgradeLevel(0); // Initial upgrade level
        this.setProperties(new HashMap<>()); // Initialize properties map if it's null by default

        // Set properties for the StaticElement interface
        this.displaySymbol = symbol;
    }

    // --- Implementations for StaticElement Interface ---

    @Override
    public char symbol() {
        if (displaySymbol != null && !displaySymbol.isEmpty()) {
            return displaySymbol.charAt(0);
        }
        return '?'; // Fallback symbol
    }

    @Override
    public boolean isPassable() {
        return false;
    }

    @Override
    public String getName() {
        // Use the buildingType inherited from the Buildings class as the common name
        return getBuildingType();
    }

    // getWidth() and getHeight() are already provided by the Buildings class.


    public String getDisplaySymbol() {
        return displaySymbol;
    }

    // --- Implementations for abstract methods inherited from your Buildings class ---
    // You will need to expand on these based on your game's specific logic.

    @Override
    public void construct() {
        System.out.println(getName() + " construction started. Cost: " + getConstructionCost());
        this.setBuilt(true);
        // Add actual construction logic here (e.g., consume resources, set position x,y)
    }

    @Override
    public boolean upgrade() {
        // Example: check if current level is less than a max level, and if resources are available
        if (canUpgrade()) {
            setUpgradeLevel(getUpgradeLevel() + 1);
            System.out.println(getName() + " upgraded to level " + getUpgradeLevel());
            // Add upgrade costs and effects
            return true;
        }
        System.out.println(getName() + " cannot be upgraded further or lacks resources.");
        return false;
    }

    @Override
    public void demolish() {
        System.out.println(getName() + " at (" + getX() + "," + getY() + ") demolished.");
        // Add logic to remove building from map, potentially refund resources, etc.
        this.setBuilt(false);
    }

    @Override
    public boolean canUpgrade() {
        // Placeholder for upgrade eligibility logic (e.g., max upgrade level)
        return getUpgradeLevel() < 3; // Example: assuming a max of 3 upgrade levels (0, 1, 2)
    }

    @Override
    public int calculateMaintenanceCost() {
        // Example: maintenance cost might increase with upgrade level
        return getMaintenanceCost() * (getUpgradeLevel() + 1);
    }

    @Override
    public void applySpecialEffects() {
        System.out.println(getName() + " applying its special effects.");
        // Implement effects specific to this building type (e.g., Bee House producing honey)
    }

    @Override
    public boolean canWithstandWeather(String weatherType) {
        // Implement weather resistance logic (e.g., certain buildings are immune to storms)
        return true; // Default to true
    }

    // You will also need setters for x and y in your Buildings class if you don't have them
    // or add them here if Buildings' fields are protected/public:
    // public void setX(int x) { this.x = x; }
    // public void setY(int y) { this.y = y; }
}
