package com.StardewValley.models;

import java.util.HashMap;

public class Tools extends Item {
    private int toolLevel;
 //   private String type;
    private HashMap<String, Object> attributes;
    private int upgradeCost;
    private int energyCost;
    private HoeStage hoeStage;
    private AxeStage axeStage;
    private PickaxeStage pickaxeStage;
    private WateringcanStage wateringcanStage;
    private FishingpoleStage fishingpoleStage;
    private int currentUsage;
    private int maxUsage;
    private int radius;

    public static enum HoeStage {
        BEGINNER, COPPER, IRON, GOLD, IRIDIUM
    }
    public static enum AxeStage {
        BEGINNER, COPPER, IRON, GOLD, IRIDIUM
    }
    public static enum PickaxeStage {
        BEGINNER, COPPER, IRON, GOLD, IRIDIUM
    }
    public static enum WateringcanStage {
        BEGINNER, COPPER, IRON, GOLD, IRIDIUM
    }
    public static enum FishingpoleStage {
        TRAINING, BAMBOO, FIBERGLASS, IRIDIUM
    }
    public static enum TrashbinStage {
        BEGINNER, COPPER, STEEL, GOLD, IRIDIUM
    }
    public static void addBeginnerHoeToInventory(Inventory inventory) {
        Tools hoe = new Tools();
        hoe.setId(1);
        hoe.setName("Hoe");
        hoe.setType("Tool");
        hoe.setQuantity(1);
        hoe.setHoeStage(HoeStage.BEGINNER); // Add this line
        hoe.setPath("assets/Inventory/ToolsAndUpgrade/Hoe.png");
        inventory.getItems().add(hoe);
    }
    public static void addBeginnerPickaxeToInventory(Inventory inventory) {
        Tools pickaxe = new Tools();
        pickaxe.setId(2);
        pickaxe.setName("Pickaxe");
        pickaxe.setType("Tool");
        pickaxe.setQuantity(1);
        pickaxe.setPickaxeStage(PickaxeStage.BEGINNER); // Add this line
        pickaxe.setPath("assets/Inventory/ToolsAndUpgrade/Pickaxe.png");
        inventory.getItems().add(pickaxe);
    }

    public static void addBeginnerAxeToInventory(Inventory inventory) {
        Tools axe = new Tools();
        axe.setId(3);
        axe.setName("Axe");
        axe.setType("Tool");
        axe.setQuantity(1);
        axe.setAxeStage(AxeStage.BEGINNER); // Add this line
        axe.setPath("assets/Inventory/ToolsAndUpgrade/Axe.png");
        inventory.getItems().add(axe);
    }
    public static void addBeginnerWateringcanToInventory(Inventory inventory) {
        Tools wateringcan = new Tools();
        wateringcan.setId(4);
        wateringcan.setName("Watering_Can");
        wateringcan.setType("Tool");
        wateringcan.setQuantity(1);
        wateringcan.setWateringcanStage(WateringcanStage.BEGINNER);
        wateringcan.setPath("assets/Inventory/ToolsAndUpgrade/Watering_Can.png");
        wateringcan.setMaxUsage(40);
        wateringcan.setCurrentUsage(40);
        wateringcan.setRadius(40);
        inventory.getItems().add(wateringcan);
    }
    public static void addLearningFishingpoleToInventory(Inventory inventory) {
        Tools fishingpole = new Tools();
        fishingpole.setId(5);
        fishingpole.setName("fishingpole");
        fishingpole.setType("Tool");
        fishingpole.setQuantity(1);
        fishingpole.setFishingpoleStage(FishingpoleStage.TRAINING); // Add this line
        inventory.getItems().add(fishingpole);
    }
    public static void addBeginnerScytheToInventory(Inventory inventory) {
        Tools scythe = new Tools();
        scythe.setId(6); // Use a unique ID not used by other tools
        scythe.setName("Scythe");
        scythe.setType("Tool");
        scythe.setQuantity(1);
        scythe.setEnergyCost(2);
        scythe.setPath("assets/Inventory/ToolsAndUpgrade/Scythe.png");
        inventory.getItems().add(scythe);
    }
    public static void addBeginnerMilkPailToInventory(Inventory inventory) {
        Tools milkPail = new Tools();
        milkPail.setId(7); // Use a unique ID not used by other tools
        milkPail.setName("Milk_Pail");
        milkPail.setType("Tool");
        milkPail.setPath("assets/Inventory/ToolsAndUpgrade/Milk_Pail.png");
        milkPail.setQuantity(1);
        milkPail.setEnergyCost(4);
        inventory.getItems().add(milkPail);
    }
    public static void addBeginnerShearToInventory(Inventory inventory) {
        Tools shear = new Tools();
        shear.setId(8);
        shear.setName("Shears");
        shear.setType("Tool");
        shear.setPath("assets/Inventory/ToolsAndUpgrade/Shears.png");
        shear.setQuantity(1);
        shear.setEnergyCost(4);
        inventory.getItems().add(shear);
    }

    public Tools() {
        // Default to Beginner Hoe
        this.hoeStage = HoeStage.BEGINNER;
        this.energyCost = 5;
        setName("Hoe");
        setType("Tool");
    }
    public HoeStage getHoeStage() {
        return hoeStage;
    }
    public AxeStage getAxeStage() {
        return axeStage;
    }
    public PickaxeStage getPickaxeStage() {
        return pickaxeStage;
    }
    public WateringcanStage getWateringcanStage() {
        return wateringcanStage;
    }
    public FishingpoleStage getFishingpoleStage() {
        return fishingpoleStage;
    }
    public void setHoeStage(HoeStage hoeStage) {
        this.hoeStage = hoeStage;
        switch (hoeStage) {
            case BEGINNER: this.energyCost = 5; setPath("assets/Inventory/ToolsAndUpgrade/Hoe.png"); break;
            case COPPER: this.energyCost = 4; setPath("assets/Inventory/ToolsAndUpgrade/Copper_Hoe.png"); break;
            case IRON: this.energyCost = 3; setPath("assets/Inventory/ToolsAndUpgrade/Steel_Hoe.png"); break;
            case GOLD: this.energyCost = 2; setPath("assets/Inventory/ToolsAndUpgrade/Gold_Hoe.png"); break;
            case IRIDIUM: this.energyCost = 1; setPath("assets/Inventory/ToolsAndUpgrade/Iridium_Hoe.png"); break;
        }
    }
    public void setAxeStage(AxeStage axeStage) {
        this.axeStage = axeStage;
        switch (axeStage) {
            case BEGINNER: this.energyCost = 5; setPath("assets/Inventory/ToolsAndUpgrade/Axe.png"); break;
            case COPPER: this.energyCost = 4; setPath("assets/Inventory/ToolsAndUpgrade/Copper_Axe.png"); break;
            case IRON: this.energyCost = 3; setPath("assets/Inventory/ToolsAndUpgrade/Steel_Axe.png"); break;
            case GOLD: this.energyCost = 2; setPath("assets/Inventory/ToolsAndUpgrade/Gold_Axe.png"); break;
            case IRIDIUM: this.energyCost = 1; setPath("assets/Inventory/ToolsAndUpgrade/Iridium_Axe.png"); break;
        }
    }
    public void setPickaxeStage(PickaxeStage pickaxeStage) {
        this.pickaxeStage = pickaxeStage;
        switch (pickaxeStage) {
            case BEGINNER: this.energyCost = 5; setPath("assets/Inventory/ToolsAndUpgrade/Pickaxe.png"); break;
            case COPPER: this.energyCost = 4; setPath("assets/Inventory/ToolsAndUpgrade/Copper_Pickaxe.png"); break;
            case IRON: this.energyCost = 3; setPath("assets/Inventory/ToolsAndUpgrade/Steel_Pickaxe.png"); break;
            case GOLD: this.energyCost = 2; setPath("assets/Inventory/ToolsAndUpgrade/Gold_Pickaxe.png"); break;
            case IRIDIUM: this.energyCost = 1; setPath("assets/Inventory/ToolsAndUpgrade/Iridium_Pickaxe.png"); break;
        }
    }
    public void setWateringcanStage(WateringcanStage wateringcanStage) {
        this.wateringcanStage = wateringcanStage;
        switch (wateringcanStage) {
            case BEGINNER:
                this.energyCost = 5;
                this.maxUsage = 40;
                this.radius = 1;
                setPath("assets/Inventory/ToolsAndUpgrade/Watering_Can.png");
                break;
            case COPPER:
                this.energyCost = 4;
                this.maxUsage = 55;
                this.radius = 3;
                setPath("assets/Inventory/ToolsAndUpgrade/Copper_Watering_Can.png");
                break;
            case IRON:
                this.energyCost = 3;
                this.maxUsage = 70;
                this.radius = 5;
                setPath("assets/Inventory/ToolsAndUpgrade/Steel_Watering_Can.png");
                break;
            case GOLD:
                this.energyCost = 2;
                this.maxUsage = 85;
                this.radius = 9;
                setPath("assets/Inventory/ToolsAndUpgrade/Gold_Watering_Can.png");
                break;
            case IRIDIUM:
                this.energyCost = 1;
                this.maxUsage = 100;
                this.radius = 18;
                setPath("assets/Inventory/ToolsAndUpgrade/Iridium_Watering_Can.png");
                break;
        }
        this.currentUsage = this.maxUsage;
    }
    public void setFishingpoleStage(FishingpoleStage fishingpoleStage) {
        this.fishingpoleStage = fishingpoleStage;
        switch (fishingpoleStage) {
            case TRAINING: this.energyCost = 8; setPath("assets/Inventory/ToolsAndUpgrade/Training_Rod.png"); break;
            case BAMBOO: this.energyCost = 8; setPath("assets/Inventory/ToolsAndUpgrade/Bamboo_Pole.png"); break;
            case FIBERGLASS: this.energyCost = 6; setPath("assets/Inventory/ToolsAndUpgrade/Fiberglass_Rod.png"); break;
            case IRIDIUM: this.energyCost = 4; setPath("assets/Inventory/ToolsAndUpgrade/Iridium_Rod.png"); break;
        }
    }

    private void initDefaultAttributes() {

    }

    public void upgrade() {
        toolLevel++;
    }

    private void updateAttributesAfterUpgrade() {}

    public int getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(int currentUsage) {
        this.currentUsage = currentUsage;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public void setMaxUsage(int maxUsage) {
        this.maxUsage = maxUsage;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

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


    public String getType() {
        return type;
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
