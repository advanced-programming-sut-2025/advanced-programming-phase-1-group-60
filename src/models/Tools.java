package models;

import java.util.HashMap;

public class Tools extends Item {
    private int toolLevel;
    private String type;
    private HashMap<String, Object> attributes;
    private int upgradeCost;
    private int energyCost;



    private void initDefaultAttributes() {

    }

    public void upgrade() {

    }

    private void updateAttributesAfterUpgrade() {}


    @Override
    public void use() {}

    public void setAttribute(String key, Object value) {}


    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public int getToolLevel() {
        return toolLevel;
    }

    public void setToolLevel(int toolLevel) {
        this.toolLevel = toolLevel;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public void setUpgradeCost(int upgradeCost) {
        this.upgradeCost = upgradeCost;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public void setEnergyCost(int energyCost) {
        this.energyCost = energyCost;
    }

    public Result UseTool(){
        return null;
    }
    public Result upgradeTool() {
        return null;
    }
    public Result equipTool() {
        return null;
    }
}
