package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Seeds extends Item implements RandomElement {
    private String growsInto;
    private List<String> suitableSeasons;
    private int totalHarvestTime;
    private boolean oneTime;
    private Integer regrowthTime;
    private FruitsAndVegetables crop;

    public Seeds(){
        this.growsInto = "";
        this.suitableSeasons = new ArrayList<>();
        this.totalHarvestTime = 0;
    }
    public Seeds(FruitsAndVegetables fv) {
        super.setName(fv.getSource());
        this.growsInto = fv.getName();
        this.suitableSeasons = fv.getSuitableSeasons();
        this.totalHarvestTime = fv.getTotalHarvestTime();
        this.crop = crop;
    }
    public String getGrowsInto() { return growsInto; }
    public void setGrowsInto(String growsInto) { this.growsInto = growsInto; }
    @Override
    public char symbol() {
        return 'F';
    }

    @Override
    public boolean isPassable() {
        return false;
    }
    public List<String> getSuitableSeasons() { return suitableSeasons; }
    public void setSuitableSeasons(List<String> suitableSeasons) { this.suitableSeasons = suitableSeasons; }

    public int getTotalHarvestTime() { return totalHarvestTime; }
    public void setTotalHarvestTime(int totalHarvestTime) { this.totalHarvestTime = totalHarvestTime; }

    public boolean isOneTime() {
        return crop != null && crop.isOneTime();
    }
    public Integer getRegrowthTime() {
        return crop != null ? crop.getRegrowthTime() : null;
    }
    public void setOneTime(boolean oneTime) { this.oneTime = oneTime; }
    public void setRegrowthTime(int regrowthTime) { this.regrowthTime = regrowthTime; }
}