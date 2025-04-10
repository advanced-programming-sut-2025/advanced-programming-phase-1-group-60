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
    }

    public void restoreEnergy() {
    }

    public void setMaxEnergy(int maxEnergy) {
    }

    public boolean isExhausted() {
    }


    public int getCurrentEnergy() { }
    public void setUnlimited(boolean unlimited) {  }
}
