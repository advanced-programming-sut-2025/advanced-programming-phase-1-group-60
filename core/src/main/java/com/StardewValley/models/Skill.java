package com.StardewValley.models;

public class Skill {
    private String name;
    private int level;
    private double experience;
    private double maxExperience;
    private int upgradePrice;

    public static final int MAX_LEVEL = 4;

    public Skill(String name) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.maxExperience = 20; // XP needed for level 1->2
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getExperience() {
        return experience;
    }

    public void setExperience(double experience) {
        this.experience = experience;
    }

    public double getMaxExperience() {
        return maxExperience;
    }

    public void setMaxExperience(double maxExperience) {
        this.maxExperience = maxExperience;
    }

    public int getUpgradePrice() {
        return upgradePrice;
    }

    public void setUpgradePrice(int upgradePrice) {
        this.upgradePrice = upgradePrice;
    }

    public void gainExperience(double amount) {
        if (level >= MAX_LEVEL) return;
        experience += amount;
        while (experience >= maxExperience && level < MAX_LEVEL) {
            experience -= maxExperience;
            levelUp();
        }
        if (level == MAX_LEVEL) {
            experience = 0; // or keep at maxExperience if you prefer
        }
    }
    private boolean maxLevelNotified = false;

    public boolean isMaxLevelNotified() {
        return maxLevelNotified;
    }
    public void setMaxLevelNotified(boolean notified) {
        this.maxLevelNotified = notified;
    }
    private void levelUp() {
        if (level < MAX_LEVEL) {
            level++;
            maxExperience = 20 * level; // e.g., level 2: 40, level 3: 60, etc.
        }
    }
}
