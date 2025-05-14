package models;

import java.util.List;

public class Seeds extends Item {
    private String growsInto;
    private List<String> suitableSeasons;
    private int totalHarvestTime;

    public String getGrowsInto() { return growsInto; }
    public void setGrowsInto(String growsInto) { this.growsInto = growsInto; }

    public List<String> getSuitableSeasons() { return suitableSeasons; }
    public void setSuitableSeasons(List<String> suitableSeasons) { this.suitableSeasons = suitableSeasons; }

    public int getTotalHarvestTime() { return totalHarvestTime; }
    public void setTotalHarvestTime(int totalHarvestTime) { this.totalHarvestTime = totalHarvestTime; }
}