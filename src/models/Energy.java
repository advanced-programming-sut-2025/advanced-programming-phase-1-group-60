package models;

public class Energy {
    private int currentEnergy;
    private int maxEnergy;
    private boolean isUnlimited;

    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = currentEnergy;
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


    public void restoreEnergy() {
    }

    public void setMaxEnergy(int maxEnergy) {
    }

    public boolean isExhausted() {
        return false;
    }


    public int getCurrentEnergy() {return 0; }

    public void setUnlimited(boolean unlimited) {
        isUnlimited = unlimited;
    }
}
