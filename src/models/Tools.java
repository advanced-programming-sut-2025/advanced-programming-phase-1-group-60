package models;

public class Tools extends Item {
    private int toolLevel;
    private String type;
    private Map <String, Object>  attributes;
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

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
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
}
