package models;

package models;

import java.util.List;

public class Product {

    private String name;
    private int energy;
    private String description;
    private List<Item> ingredients;
    private int price;
    private String quality;
    // private ProcessingTime processingTime;
    private int growthStage;
    private int[] growthStages;
    private boolean isRegrowable;
    private int regrowTime;
    private boolean isEdible;
    private String suitableSeason;
    private boolean canBeGiant;
    private String source;
    private boolean isSellable;
    private boolean isAvailable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Item> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Item> ingredients) {
        this.ingredients = ingredients;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public void setGrowthStage(int growthStage) {
        this.growthStage = growthStage;
    }

    public int[] getGrowthStages() {
        return growthStages;
    }

    public void setGrowthStages(int[] growthStages) {
        this.growthStages = growthStages;
    }

    public boolean isRegrowable() {
        return isRegrowable;
    }

    public void setRegrowable(boolean regrowable) {
        isRegrowable = regrowable;
    }

    public int getRegrowTime() {
        return regrowTime;
    }

    public void setRegrowTime(int regrowTime) {
        this.regrowTime = regrowTime;
    }

    public boolean isEdible() {
        return isEdible;
    }

    public void setEdible(boolean edible) {
        isEdible = edible;
    }

    public String getSuitableSeason() {
        return suitableSeason;
    }

    public void setSuitableSeason(String suitableSeason) {
        this.suitableSeason = suitableSeason;
    }

    public boolean isCanBeGiant() {
        return canBeGiant;
    }

    public void setCanBeGiant(boolean canBeGiant) {
        this.canBeGiant = canBeGiant;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isSellable() {
        return isSellable;
    }

    public void setSellable(boolean sellable) {
        isSellable = sellable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isReadyToHarvest();


    public void growDaily();


    //  public Product harvest();


    public boolean canGrowInSeason(String currentSeason);


    public int getFinalPrice();


    public boolean isProcessingComplete();


    public void startProcessing();


}