package models;

import java.util.Map;

public abstract class Buildings {
    private String buildingType;
    private int width;
    private int height;
    private int x;
    private int y;
    private Map<String, Object> properties;
    private int upgradeLevel;
    private boolean isBuilt;
    private int constructionCost;
    private int maintenanceCost;

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public void setBuilt(boolean built) {
        isBuilt = built;
    }

    public int getConstructionCost() {
        return constructionCost;
    }

    public void setConstructionCost(int constructionCost) {
        this.constructionCost = constructionCost;
    }

    public int getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(int maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public void construct(){}


    public boolean upgrade(){
        return false;
    }


    public void demolish(){}


    public boolean canUpgrade(){
        return false;
    }


    public int calculateMaintenanceCost(){
        return 0;
    }


    public void applySpecialEffects(){}


    public boolean canWithstandWeather(String weatherType){
        return false;
    }

}
