package models;

import java.util.List;

public class ForagingCrop extends ForageItem {
    private String name;
    private List<String> suitableSeasons;
    private int baseSellPrice;
    private int energy;

    public ForagingCrop(String name,List<String> suitableSeasons, int baseSellPrice, int energy) {
        this.name = name;
        this.suitableSeasons = suitableSeasons;
        this.baseSellPrice = baseSellPrice;
        this.energy = energy;
    }
    public ForagingCrop(ForagingCrop other) {
        this.name = other.name;
        this.suitableSeasons = other.suitableSeasons;
        this.baseSellPrice = other.baseSellPrice;
        this.energy = other.energy;
    }
    public ForagingCrop() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSuitableSeasons() {
        return suitableSeasons;
    }

    public void setSuitableSeasons(List<String> suitableSeasons) {
        this.suitableSeasons = suitableSeasons;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public void setBaseSellPrice(int baseSellPrice) {
        this.baseSellPrice = baseSellPrice;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}