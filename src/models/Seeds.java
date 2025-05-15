package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Seeds extends Item implements RandomElement {
    private String growsInto;
    private List<String> suitableSeasons;
    private int totalHarvestTime;

    public Seeds(){
        this.growsInto = "";
        this.suitableSeasons = new ArrayList<>();
        this.totalHarvestTime = 0;
    }
    public Seeds(FruitsAndVegetables fv) {
        this.setName(fv.getSource());
        this.setGrowsInto(fv.getName());
        this.setSuitableSeasons(fv.getSuitableSeasons());
        this.setTotalHarvestTime(fv.getTotalHarvestTime());
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
}