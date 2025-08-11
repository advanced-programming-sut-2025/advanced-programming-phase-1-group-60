package com.StardewValley.models;

import java.util.Arrays;

public class TimeSystem {
    private static final TimeSystem INSTANCE = new TimeSystem();

    private int currentHour;
    private int currentDay;
    private String currentSeason;
    private String dayOfWeek;
    private int currentYear;
    public boolean oneSeasonPassed = false;

    private TimeSystem() {
        this.currentHour = 9;
        this.currentDay = 1;
        this.currentSeason = "Spring";
        this.dayOfWeek = "Monday";
        this.currentYear = 1;
    }

    public static TimeSystem getInstance() {
        return INSTANCE;
    }

    public long getTotalHoursSinceStart() {
        int seasonIndex = Arrays.asList("Spring", "Summer", "Fall", "Winter").indexOf(currentSeason);
        long totalDays = (long)(currentYear - 1) * 112 + (long)seasonIndex * 28 + (currentDay - 1);
        return totalDays * 24 + currentHour;
    }

    public synchronized boolean advanceTime(int hours) {
        if (hours < 0) throw new IllegalArgumentException();
        int previousDay = currentDay;
        currentHour += hours;
        while (currentHour >= 22) {
            currentHour -= 13;
            advanceDate(1);
        }
        return (currentDay != previousDay);
    }

    public synchronized boolean advanceDate(int days) {
        if (days < 0) throw new IllegalArgumentException();

        int previousDay = currentDay;
        currentDay += days;

        while (currentDay > 28) {
            currentDay -= 28;
            // Season naturally rolls
            updateSeasonInternal();
            oneSeasonPassed = true;
        }

        calculateDayOfWeek();
        return (currentDay != previousDay);
    }

    private void calculateDayOfWeek() {
        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        int index = (currentDay - 1) % 7;
        dayOfWeek = days[index];
    }

    private void updateSeasonInternal() {
        String[] seasons = {"Spring","Summer","Fall","Winter"};
        int idx = Arrays.asList(seasons).indexOf(currentSeason);
        currentSeason = seasons[(idx + 1) % seasons.length];
        if ("Spring".equals(currentSeason)) {
            currentYear++;
        }
    }

    /**
     * Cheat / external season advance:
     * - Advances to next season
     * - Resets day to 1
     * - Resets hour to 9 (start-of-day) (adjust if you want different behavior)
     * - Recalculates dayOfWeek (starts Monday)
     * - Increments year if wrapped from Winter to Spring
     */
    public synchronized void advanceSeason() {
        String prevSeason = currentSeason;
        updateSeasonInternal();
        currentDay = 1;
        currentHour = 9;
        dayOfWeek = "Monday";
        if (!prevSeason.equals(currentSeason) && "Spring".equals(currentSeason) && !"Spring".equals(prevSeason)) {
            // Year increment already handled in updateSeasonInternal
        }
    }

    public synchronized String getCurrentTime() {
        return String.format("%02d:00", currentHour);
    }

    public synchronized String getCurrentDate() {
        return String.format("Year %d, %s %d", currentYear, currentSeason, currentDay);
    }

    public synchronized String getDayOfWeek() {
        return dayOfWeek;
    }

    public synchronized String getDateTime() {
        return getCurrentDate() + " " + getCurrentTime() + " (" + dayOfWeek + ")";
    }

    public synchronized String getCurrentSeason() {
        return currentSeason;
    }

    public synchronized int getCurrentYear() {
        return currentYear;
    }

    public synchronized int getCurrentHour() {
        return currentHour;
    }

    public synchronized int getCurrentDay() {
        return currentDay;
    }

    public synchronized void setCurrentSeason(String season) {
        currentSeason = season;
    }

    public synchronized void setCurrentYear(int year) {
        this.currentYear = year;
    }
    public synchronized void setCurrentHour(int hour) { this.currentHour = hour; }
    public synchronized void setCurrentDay(int day) { this.currentDay = day;}
    public synchronized void setDayOfWeek(String day) { this.dayOfWeek = day; }
}
