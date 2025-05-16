package models;

import java.util.ArrayList;
import java.util.List;

public class Tree implements RandomElement {
    private String name;
    private String source;
    private int[] stages;
    private int totalHarvestTime;
    private String fruit;
    private int fruitHarvestCycle;
    private int fruitBaseSellPrice;
    private boolean isFruitEdible;
    private int fruitEnergy;
    private List<String> suitableSeasons;
    private int fruitsHarvestedToday = 0;
    private boolean struckByLightning = false;

    public Tree() {
        this.name = "";
        this.source = "";
        this.stages = new int[0];
        this.totalHarvestTime = 0;
        this.fruit = "";
        this.fruitHarvestCycle = 0;
        this.fruitBaseSellPrice = 0;
        this.isFruitEdible = false;
        this.fruitEnergy = 0;
        this.suitableSeasons = new ArrayList<>();
    }
    public Tree(Tree other) {
        this.setName(other.getName());
        this.setSource(other.getSource());
        this.setStages(other.getStages().clone());
        this.setTotalHarvestTime(other.getTotalHarvestTime());
        this.setFruit(other.getFruit());
        this.setFruitHarvestCycle(other.getFruitHarvestCycle());
        this.setFruitBaseSellPrice(other.getFruitBaseSellPrice());
        this.setFruitEdible(other.isFruitEdible());
        this.setFruitEnergy(other.getFruitEnergy());
        this.setSuitableSeasons(new ArrayList<>(other.getSuitableSeasons()));
    }

    public char symbol() { return 'T'; }
    public boolean isPassable() { return false; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int[] getStages() {
        return stages;
    }

    public void setStages(int[] stages) {
        this.stages = stages;
    }

    public int getTotalHarvestTime() {
        return totalHarvestTime;
    }

    public void setTotalHarvestTime(int totalHarvestTime) {
        this.totalHarvestTime = totalHarvestTime;
    }

    public String getFruit() {
        return fruit;
    }

    public void setFruit(String fruit) {
        this.fruit = fruit;
    }

    public int getFruitHarvestCycle() {
        return fruitHarvestCycle;
    }

    public void setFruitHarvestCycle(int fruitHarvestCycle) {
        this.fruitHarvestCycle = fruitHarvestCycle;
    }

    public int getFruitBaseSellPrice() {
        return fruitBaseSellPrice;
    }

    public void setFruitBaseSellPrice(int fruitBaseSellPrice) {
        this.fruitBaseSellPrice = fruitBaseSellPrice;
    }

    public boolean isFruitEdible() {
        return isFruitEdible;
    }

    public void setFruitEdible(boolean fruitEdible) {
        isFruitEdible = fruitEdible;
    }

    public int getFruitEnergy() {
        return fruitEnergy;
    }

    public void setFruitEnergy(int fruitEnergy) {
        this.fruitEnergy = fruitEnergy;
    }

    public List<String> getSuitableSeasons() {
        return suitableSeasons;
    }

    public int getFruitsHarvestedToday() {
        return fruitsHarvestedToday;
    }

    public void setFruitsHarvestedToday(int count) {
        this.fruitsHarvestedToday = count;
    }

    public void incrementFruitsHarvestedToday() {
        this.fruitsHarvestedToday++;
    }

    public void setSuitableSeasons(List<String> suitableSeasons) {
        this.suitableSeasons = suitableSeasons;
    }

    public boolean isStruckByLightning() {
        return struckByLightning;
    }

    public void setStruckByLightning(boolean struck) {
        this.struckByLightning = struck;
    }
}