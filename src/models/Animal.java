package models;

import java.util.HashMap;
import java.util.Random;

public class Animal {
    private static final Random RANDOM = new Random();

    private User owner;
    private String name;
    private int positionX;
    private int positionY;
    private String type; // مانند Chicken, Cow, Sheep
    private String buildingType; // Barn یا Coop
    private String[] buildings;
    private int friendship;
    private boolean isFed;
    private boolean isPettedToday;
    public boolean isOutside;
    private int daysSinceLastProduct;
    private int baseProductPrice;
    private String primaryProduct;
    private String secondaryProduct;
    private boolean hasProducedToday;
    private Item currentProduct;

    public Animal(String type, String buildingType,String buildings[], int basePrice) {
        this.type = type;
        this.buildings = buildings;
        this.buildingType = buildingType;
        this.friendship = 0;
        this.baseProductPrice = basePrice;
        initializeProducts();
    }

    private void initializeProducts() {
        switch (this.type) {
            case "Chicken":
                primaryProduct = "Egg";
                secondaryProduct = "Large Egg";
                break;
            case "Duck":
                primaryProduct = "Duck Egg";
                secondaryProduct = "Feather";
                break;
            case "Rabbit":
                primaryProduct = "Wool";
                secondaryProduct = "Rabbit's Foot";
                break;
            case "Dinosaur":
                primaryProduct = "Dinosaur Egg";
                secondaryProduct = "Dinosaur Egg";
                break;
            case "Cow":
                primaryProduct = "Milk";
                secondaryProduct = "Large Milk";
                break;
            case "Goat":
                primaryProduct = "Goat Milk";
                secondaryProduct = "Large Goat Milk";
                break;
            case "Sheep":
                primaryProduct = "Wool";
                secondaryProduct = "Wool";
                break;
            case "Pig":
                primaryProduct = "Truffle";
                secondaryProduct = "Truffle";
                break;
            default:
                primaryProduct = "Unknown";
                secondaryProduct = "Unknown";
                break;
        }
    }

    // افزایش رابطه دوستی
    public void increaseFriendship(int amount) {
        friendship = Math.min(1000, friendship + amount);
    }

    // کاهش رابطه دوستی
    public void decreaseFriendship(int amount) {
        friendship = Math.max(0, friendship - amount);
    }

    // نوازش حیوان
    public void pet() {
        if (!isPettedToday) {
            increaseFriendship(15);
            isPettedToday = true;
        }
    }

    // تغذیه حیوان
    public void feed(boolean ateOutside) {
        isFed = true;
        if (ateOutside) {
            increaseFriendship(8);
        }
    }

    public void produce() {
        if (!isFed || hasProducedToday || currentProduct != null) {
            return;
        }

        boolean canProduce = false;
        int requiredDays = 0;

        switch (this.type) {
            case "Chicken":
            case "Cow":
                requiredDays = 1;
                canProduce = (daysSinceLastProduct >= requiredDays);
                break;
            case "Duck":
            case "Goat":
                requiredDays = 2;
                canProduce = (daysSinceLastProduct >= requiredDays);
                break;
            case "Rabbit":
                requiredDays = 4;
                canProduce = (daysSinceLastProduct >= requiredDays);
                break;
            case "Dinosaur":
                requiredDays = 7;
                canProduce = (daysSinceLastProduct >= requiredDays);
                break;
            case "Sheep":
                requiredDays = 3;
                canProduce = (daysSinceLastProduct >= requiredDays) && (friendship >= 700);
                break;
            case "Pig":
                TimeSystem timeSystem = TimeSystem.getInstance();
                String currentSeason = timeSystem.getCurrentSeason();
                canProduce = isOutside && !currentSeason.equals("Winter");
                break;
            default:
                canProduce = false;
        }

        if (!canProduce) {
            return;
        }

        currentProduct = new Item();
        currentProduct.setName(determineProductType());
        currentProduct.setType("AnimalProduct");
        currentProduct.setBasePrice(calculateProductPrice());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("quality", determineQuality());
        properties.put("animal", this.type);
        currentProduct.setProperties(properties);

        hasProducedToday = true;

        if (!this.type.equals("Pig")) {
            daysSinceLastProduct = 0;
        }
    }

    private String determineProductType() {
        if (friendship < 100) return primaryProduct;
        double chance = (friendship + RANDOM.nextDouble() * 150) / 1500;
        return chance > 0.5 ? secondaryProduct : primaryProduct;
    }

    private String determineQuality() {
        double qualityValue = (RANDOM.nextDouble() * 0.5 + 0.5) * (friendship / 1000.0);
        if (qualityValue > 0.9) return "Iridium";
        else if (qualityValue > 0.7) return "Gold";
        else if (qualityValue > 0.5) return "Silver";
        else return "Normal";
    }

    private int calculateProductPrice() {
        double multiplier = switch (determineQuality()) {
            case "Iridium" -> 2.0;
            case "Gold" -> 1.5;
            case "Silver" -> 1.25;
            default -> 1.0;
        };
        return (int) (baseProductPrice * multiplier);
    }

    public boolean requiresToolForCollection() {
        return switch (type) {
            case "Cow", "Goat", "Sheep" -> true;
            default -> false;
        };
    }

    public Item collectProduct() {
        // افزودن شرط های برداشت
        Item product = currentProduct;
        currentProduct = null;
        return product;
    }

    public int calculateSellPrice() {
        return (int) (baseProductPrice * (0.3 + (friendship / 1000.0)));
    }

    public void resetDailyStatus() {
        produce();
        if (!isFed) decreaseFriendship(20);
        if (!isPettedToday) decreaseFriendship(10);
        if (isFed) decreaseFriendship(5);

        isFed = false;
        isPettedToday = false;
        hasProducedToday = false;
        daysSinceLastProduct++;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getPositionX () { return positionX;}
    public int getPositionY () { return positionY;}
    public void setPositionX (int newPositionX) { positionX = newPositionX; produce(); }
    public void setPositionY (int newPositionY) { positionY = newPositionY; }
    public int getFriendship() { return friendship; }
    public boolean isFed() { return isFed; }
    public void setOutside(boolean outside) { isOutside = outside; }
    public boolean hasProducedToday () { return hasProducedToday; }
    public Item getCurrentProduct () { return currentProduct; }
    public int getPrice() { return baseProductPrice; }
    public String[] getBuildings() { return buildings; }
    public String getBuildingType() { return buildingType; }

    public void setFriendship(int friendship) {
        this.friendship = Math.min(1000, Math.max(0, friendship));
    }

    public void setOwner (User owner) {
        this.owner = owner;
    }

    public void setName (String newName) {
        name = newName;
    }



    @Override
    public String toString() {
        return String.format("%s (%s) - Friendship: %d - Fed: %b - Petted: %b",
                name, type, friendship, isFed, isPettedToday);
    }
}