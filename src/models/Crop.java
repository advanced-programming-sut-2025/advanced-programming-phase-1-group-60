package models;

public class Crop {
    private String name;
    private int currentGrowthStage;
    private int[] growthStages;
    private boolean isWatered;
    private boolean hasFertilizer;
    private Season suitableSeason;
    private boolean isGiant;
    //private LocalDate plantedDate;


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

    public Season getSuitableSeason() {
        return suitableSeason;
    }

    public void setSuitableSeason(Season suitableSeason) {
        this.suitableSeason = suitableSeason;
    }

    public boolean isGiant() {
        return isGiant;
    }

    public void setGiant(boolean giant) {
        isGiant = giant;
    }

    public void growDaily() {
    }


    public boolean isReadyToHarvest() {
    }


    public String getName() { }
    public int getCurrentStageDays() {}

}
