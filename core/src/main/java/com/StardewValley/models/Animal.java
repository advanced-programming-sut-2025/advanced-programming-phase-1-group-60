package com.StardewValley.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Animal {
    private static final Random RANDOM = new Random();

    private User owner;
    private String name;
    // Position is now float for smooth movement
    private float positionX;
    private float positionY;
    private String type; // e.g., Chicken, Cow, Sheep
    private String buildingType; // Barn or Coop
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
    private String path; // Path for static icon in shop

    // New Fields for Follow & Animation
    private String spriteSheetPath; // Path for animated sprite sheet
    private boolean isFollowing = false;
    private transient User targetToFollow = null;
    private transient List<Tile> currentPath = null;
    private float animationStateTime = 0f;
    private int lastDirection = 0; // 0:down, 1:right, 2:up, 3:left
    private boolean isMoving = false;
    private static final float MOVE_SPEED = 75f; // Speed in pixels per second

    // New Fields for Petting Animation
    private boolean isBeingPetted = false;
    private float petAnimationTimer = 0f;

    // New Fields for Feeding Animation
    private boolean isBeingFed = false;
    private float feedAnimationTimer = 0f;


    public Animal(String type, String buildingType, String[] buildings, int basePrice, String path) {
        this.type = type;
        this.buildings = buildings;
        this.buildingType = buildingType;
        this.friendship = 0;
        this.baseProductPrice = basePrice;
        this.path = path;
        initializeProducts();
    }

    // New overloaded constructor to include spriteSheetPath
    public Animal(String type, String buildingType, String[] buildings, int basePrice, String path, String spriteSheetPath) {
        this.type = type;
        this.buildings = buildings;
        this.buildingType = buildingType;
        this.friendship = 0;
        this.baseProductPrice = basePrice;
        this.path = path;
        this.spriteSheetPath = spriteSheetPath; // Set the new field
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

    public void update(float delta, Farm farm, float playerFarmX, float playerFarmY) {
        // Handle petting animation timer
        if (isBeingPetted) {
            petAnimationTimer -= delta;
            if (petAnimationTimer <= 0) {
                isBeingPetted = false;
            }
            // While being petted, the animal should not move
            isMoving = false;
            return;
        }

        if (isBeingFed) {
            feedAnimationTimer -= delta;
            if (feedAnimationTimer <= 0) {
                isBeingFed = false;
            }
        }

        if (!isFollowing || !isOutside) {
            isMoving = false;
            return;
        }

        float TILE_SIZE = 32f;
        float targetX = playerFarmX;
        float targetY = playerFarmY;

        float dx = targetX - this.positionX;
        float dy = targetY - this.positionY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Stop following if too far (5 tiles * 32 pixels/tile)
        if (distance > 5 * TILE_SIZE) {
            isFollowing = false;
            targetToFollow = null;
            isMoving = false;
            return;
        }

        // Move if not close enough (1.5 tiles buffer)
        if (distance > 1.5 * TILE_SIZE) {
            isMoving = true;
            animationStateTime += delta;

            float angle = (float) Math.atan2(dy, dx);
            float moveX = (float) Math.cos(angle) * MOVE_SPEED * delta;
            float moveY = (float) Math.sin(angle) * MOVE_SPEED * delta;

            this.positionX += moveX;
            this.positionY += moveY;

            // Update direction for animation
            if (Math.abs(dx) > Math.abs(dy)) {
                this.lastDirection = dx > 0 ? 1 : 3; // Right or Left
            } else {
                this.lastDirection = dy > 0 ? 2 : 0; // Up or Down
            }
        } else {
            isMoving = false;
        }
    }


    // Getters and setters for new fields
    public String getSpriteSheetPath() { return spriteSheetPath; }
    public void setSpriteSheetPath(String spriteSheetPath) { this.spriteSheetPath = spriteSheetPath; }
    public boolean isFollowing() { return isFollowing; }
    public void setFollowing(boolean following, User target) {
        this.isFollowing = following;
        this.targetToFollow = following ? target : null;
    }
    public float getAnimationStateTime() { return animationStateTime; }
    public int getLastDirection() { return lastDirection; }
    public boolean isMoving() { return isMoving; }
    public boolean isBeingPetted() { return isBeingPetted; }
    public boolean isBeingFed() { return isBeingFed; }


    // Other existing methods...
    public void increaseFriendship(int amount) {
        friendship = Math.min(1000, friendship + amount);
    }

    public void decreaseFriendship(int amount) {
        friendship = Math.max(0, friendship - amount);
    }

    public void pet() {
        if (!isPettedToday) {
            increaseFriendship(15);
            isPettedToday = true;
            isBeingPetted = true;
            petAnimationTimer = 1.0f; // 1 second animation
        }
    }
    public Result feed(boolean ateOutside, Inventory playerInventory) {
        if (isFed) {
            return new Result(false, this.name + " has already eaten today.");
        }
        if (ateOutside) {
            isFed = true;
            increaseFriendship(8);
            isBeingFed = true;
            feedAnimationTimer = 1.0f; // 1 second animation
            return new Result(true, this.name + " ate fresh grass outside.");
        } else {
            if (playerInventory.hasItem("Hay", 1)) {
                playerInventory.removeItemByName("Hay", 1);
                isFed = true;
                increaseFriendship(4);
                isBeingFed = true;
                feedAnimationTimer = 1.0f; // 1 second animation
                return new Result(true, "You fed " + this.name + " some Hay.");
            } else {
                return new Result(false, "You don't have any Hay to feed " + this.name + ".");
            }
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
        String productName = determineProductType();
        currentProduct.setName(productName);
        currentProduct.setType("AnimalProduct");
        currentProduct.setBasePrice(calculateProductPrice());
        currentProduct.setQuantity(1);
        currentProduct.setPath("assets/Animals/Products/" + productName.replace(" ", "_") + ".png");


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
        if (currentProduct != null) {
            Item productToCollect = currentProduct;
            currentProduct = null;
            hasProducedToday = true; // Mark as collected for the day
            return productToCollect;
        }
        return null;
    }

    public int calculateSellPrice() {
        return (int) (baseProductPrice * (0.3 + (friendship / 1000.0)));
    }

    public void resetDailyStatus() {
        produce();
        if (!isFed) decreaseFriendship(20);
        if (!isPettedToday) decreaseFriendship(10);
        if (isFed) decreaseFriendship(5);

        isPettedToday = false;
        isFed = false;
        hasProducedToday = false;
        daysSinceLastProduct++;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public float getPositionX () { return positionX;}
    public float getPositionY () { return positionY;}
    public void setPositionX (float newPositionX) { positionX = newPositionX; }
    public void setPositionY (float newPositionY) { positionY = newPositionY; }
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

    public void setPath (String newPath) { path = newPath; }

    public String getPath() { return path; }

    public void bringInside() {
        this.isOutside = false;
    }

    public void bringOutside(float relativeToFarmX, float relativeToFarmY) {
        this.isOutside = true;
        this.positionX = relativeToFarmX;
        this.positionY = relativeToFarmY;
    }

    public int sell() {
        return this.baseProductPrice / 2;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Friendship: %d - Fed: %b - Petted: %b",
            name, type, friendship, isFed, isPettedToday);
    }

    public boolean isPettedToday() {
        return isPettedToday;
    }
}
