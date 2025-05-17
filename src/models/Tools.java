package models;

import java.util.HashMap;

public class Tools extends Item {
    private int toolLevel;
    private String type;
    private HashMap<String, Object> attributes;
    private int upgradeCost;
    private int energyCost;
    private HoeStage hoeStage;
    private AxeStage axeStage;
    private PickaxeStage pickaxeStage;
    private WateringcanStage wateringcanStage;
    private FishingpoleStage fishingpoleStage;
    private TrashbinStage trashbinStage;
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
        BEGINNER, COPPER, IRON, GOLD, IRIDIUM
    }
    public static void addBeginnerHoeToInventory(Inventory inventory) {
        Tools hoe = new Tools();
        hoe.setId(1);
        hoe.setName("Hoe");
        hoe.setType("Tool");
        hoe.setQuantity(1);
        hoe.setHoeStage(HoeStage.BEGINNER); // Add this line
        inventory.getItems().add(hoe);
    }
    public static void addBeginnerPickaxeToInventory(Inventory inventory) {
        Tools pickaxe = new Tools();
        pickaxe.setId(2);
        pickaxe.setName("Pickaxe");
        pickaxe.setType("Tool");
        pickaxe.setQuantity(1);
        pickaxe.setPickaxeStage(PickaxeStage.BEGINNER); // Add this line
        inventory.getItems().add(pickaxe);
    }

    public static void addBeginnerAxeToInventory(Inventory inventory) {
        Tools axe = new Tools();
        axe.setId(3);
        axe.setName("Axe");
        axe.setType("Tool");
        axe.setQuantity(1);
        axe.setAxeStage(AxeStage.BEGINNER); // Add this line
        inventory.getItems().add(axe);
    }
    public static void addBeginnerWateringcanToInventory(Inventory inventory) {
        Tools wateringcan = new Tools();
        wateringcan.setId(4);
        wateringcan.setName("Wateringcan");
        wateringcan.setType("Tool");
        wateringcan.setQuantity(1);
        wateringcan.setWateringcanStage(WateringcanStage.BEGINNER);
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
        inventory.getItems().add(scythe);
    }
    public static void addBeginnerMilkPailToInventory(Inventory inventory) {
        Tools milkPail = new Tools();
        milkPail.setId(7); // Use a unique ID not used by other tools
        milkPail.setName("Milk Pail");
        milkPail.setType("Tool");
        milkPail.setQuantity(1);
        milkPail.setEnergyCost(4);
        inventory.getItems().add(milkPail);
    }
    public static void addBeginnerShearToInventory(Inventory inventory) {
        Tools shear = new Tools();
        shear.setId(8);
        shear.setName("shear");
        shear.setType("Tool");
        shear.setQuantity(1);
        shear.setEnergyCost(4);
        inventory.getItems().add(shear);
    }
    public static void addBeginnerTrashbinToInventory(Inventory inventory) {
        Tools trashbin = new Tools();
        trashbin.setId(9); // Use a unique ID
        trashbin.setName("Trashbin");
        trashbin.setType("Tool");
        trashbin.setQuantity(1);
        trashbin.setTrashbinStage(TrashbinStage.BEGINNER);
        inventory.getItems().add(trashbin);
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
    public TrashbinStage getTrashbinStage() {
        return trashbinStage;
    }
    public void setHoeStage(HoeStage hoeStage) {
        this.hoeStage = hoeStage;
        switch (hoeStage) {
            case BEGINNER: this.energyCost = 5; break;
            case COPPER: this.energyCost = 4; break;
            case IRON: this.energyCost = 3; break;
            case GOLD: this.energyCost = 2; break;
            case IRIDIUM: this.energyCost = 1; break;
        }
    }
    public void setAxeStage(AxeStage axeStage) {
        this.axeStage = axeStage;
        switch (axeStage) {
            case BEGINNER: this.energyCost = 5; break;
            case COPPER: this.energyCost = 4; break;
            case IRON: this.energyCost = 3; break;
            case GOLD: this.energyCost = 2; break;
            case IRIDIUM: this.energyCost = 1; break;
        }
    }
    public void setPickaxeStage(PickaxeStage pickaxeStage) {
        this.pickaxeStage = pickaxeStage;
        switch (pickaxeStage) {
            case BEGINNER: this.energyCost = 5; break;
            case COPPER: this.energyCost = 4; break;
            case IRON: this.energyCost = 3; break;
            case GOLD: this.energyCost = 2; break;
            case IRIDIUM: this.energyCost = 1; break;
        }
    }
    public void setWateringcanStage(WateringcanStage wateringcanStage) {
        this.wateringcanStage = wateringcanStage;
        switch (wateringcanStage) {
            case BEGINNER:
                this.energyCost = 5;
                this.maxUsage = 40;
                this.radius = 40;
                break;
            case COPPER:
                this.energyCost = 4;
                this.maxUsage = 40;
                this.radius = 55;
                break;
            case IRON:
                this.energyCost = 3;
                this.maxUsage = 40;
                this.radius = 70;
                break;
            case GOLD:
                this.energyCost = 2;
                this.maxUsage = 40;
                this.radius = 85;
                break;
            case IRIDIUM:
                this.energyCost = 1;
                this.maxUsage = 40;
                this.radius = 100;
                break;
        }
        this.currentUsage = this.maxUsage;
    }
    public void setFishingpoleStage(FishingpoleStage fishingpoleStage) {
        this.fishingpoleStage = fishingpoleStage;
        switch (fishingpoleStage) {
            case TRAINING: this.energyCost = 8; break;
            case BAMBOO: this.energyCost = 8; break;
            case FIBERGLASS: this.energyCost = 6; break;
            case IRIDIUM: this.energyCost = 4; break;
        }
    }
    public void setTrashbinStage(TrashbinStage trashbinStage) {
        this.trashbinStage = trashbinStage;
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
