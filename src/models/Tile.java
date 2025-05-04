package models;

import java.util.Optional;

public class Tile {
    private final int positionX;
    private final int positionY;
    private StaticElement staticElement;
    private RandomElement randomElement;
    private boolean passable = true;
    private String type;
    private boolean isOccupied;
    private FruitsAndVegtables plantedPlant;
    private Item placedItem;
    private boolean isWatered;
    private boolean isFertilized;
    private boolean isPlowed;
    private boolean isGreenHouseTile;

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

//    public Plant getPlantedPlant() {
//        return plantedPlant;
//    }

//    public void setPlantedPlant(Plant plantedPlant) {
//        this.plantedPlant = plantedPlant;
//        this.isOccupied = (plantedPlant != null);
//    }

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
        this.plantedPlant = null;
        this.placedItem = null;
        this.isOccupied = false;
        this.isWatered = false;
        this.isFertilized = false;
        this.isPlowed = false;
    }

    public void applyWeatherEffect(String effect) {
    }

    public boolean isGreenHouseTile() {
        return isGreenHouseTile;
    }

    public void setGreenHouseTile(boolean greenHouseTile) {
        isGreenHouseTile = greenHouseTile;
    }

    @Override
    public String toString() {
        String elementInfo = (staticElement != null)
                ? staticElement.getClass().getSimpleName()
                : "Empty";
        return String.format("Tile[x=%d, y=%d, Element=%s]", positionX, positionY, elementInfo);
    }
}
