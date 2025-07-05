package com.StardewValley.models;

public class Energy {
    private int currentEnergy;
    private final int maxEnergy = 200;
    private boolean isUnlimited;

    public Energy() {
        currentEnergy = maxEnergy;
        isUnlimited = false;
    }

    public void increaseEnergy(int amount) {
        currentEnergy += amount;
        if (currentEnergy > maxEnergy) {
            currentEnergy = maxEnergy;
        }
    }

    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public void resetEnergy(boolean hasFaintedLastDay) {
        this.currentEnergy = maxEnergy;
        if (hasFaintedLastDay) {
            this.currentEnergy = (int) (maxEnergy * 0.75);
        }
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public boolean isUnlimited() {
        return isUnlimited;
    }


    public void decreaseEnergy(int amount) {
        currentEnergy = Math.max(0,currentEnergy - amount);
    }


    public boolean isExhausted() {
        return false;
    }


    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public void setUnlimited(boolean unlimited) {
        isUnlimited = unlimited;
    }

    @Override
    public String toString() {
        return currentEnergy + "";
    }
}
