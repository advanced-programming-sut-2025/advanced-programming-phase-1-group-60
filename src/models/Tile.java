package models;

package models;

public class Tile {
    private int positionX;
    private int positionY;
    private String type;
    private boolean isOccupied;
    // private Plant plantedPlant;
    private Item placedItem;
    private boolean isWatered;
    private boolean isFertilized;
    private boolean isPlowed;
    private String weatherEffect;

    // متدهای getter و setter
    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
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

    public String getWeatherEffect() {
        return weatherEffect;
    }

    public void setWeatherEffect(String weatherEffect) {
        this.weatherEffect = weatherEffect;
    }

    public boolean isEmpty() {
    }

    public boolean canPlant() {
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

    @Override
    public String toString() {
    }
}
