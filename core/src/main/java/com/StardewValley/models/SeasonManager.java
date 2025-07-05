package com.StardewValley.models;

public class SeasonManager {

    private String currentSeason = "Spring";
    private int currentDay = 1;
    private int currentYear = 1;

    public void advanceDay() {
        currentDay++;
        if (currentDay > 28) {
            currentDay = 1;
            onSeasonChanged();
        }
    }

    private void onSeasonChanged() {
        String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
        int currentIndex = -1;
        for (int i = 0; i < seasons.length; i++) {
            if (seasons[i].equals(currentSeason)) {
                currentIndex = i;
                break;
            }
        }
        currentSeason = seasons[(currentIndex + 1) % seasons.length];
        if (currentSeason.equals("Spring")) {
            currentYear++;
        }
    }

    public String getNextSeason() {
        String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
        int currentIndex = -1;
        for (int i = 0; i < seasons.length; i++) {
            if (seasons[i].equals(currentSeason)) {
                currentIndex = i;
                break;
            }
        }
        return seasons[(currentIndex + 1) % seasons.length];
    }

    public String getCurrentDate() {
        return String.format("Year %d, %s %d", currentYear, currentSeason, currentDay);
    }

    public String getCurrentSeason() {
        return currentSeason;
    }
}
