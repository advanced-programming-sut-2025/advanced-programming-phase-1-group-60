package models;

public class Crop {
    private String name;
    private int currentGrowthStage;
    private int[] growthStages;
    private boolean isWatered;
    private boolean hasFertilizer;
    private String suitableSeason;
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

    public void growDaily() {
    }


    public boolean isReadyToHarvest() {
        return false;
    }


    public String getName() { return null;}
    public int getCurrentStageDays() {return 0;}

}
