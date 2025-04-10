package models;

public class TimeSystem {
    private int currentHour;
    private int currentMinute;
    private int currentDay;
    private String currentSeason;
    private String dayOfWeek;
    private int currentYear;

    public void advanceTime(int minutes) {
    }

    public void advanceDate(int days) {

    }


    private void calculateDayOfWeek() {
    }

    private void updateSeason() {

    }


    public String getCurrentTime() {
    }

    public String getCurrentDate() {
    }

    public String getDayOfWeek() {
    }

    public int getCurrentHour() {
        return currentHour;
    }

    public void setCurrentHour(int currentHour) {
        this.currentHour = currentHour;
    }

    public int getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(int currentMinute) {
        this.currentMinute = currentMinute;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public String getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(String currentSeason) {
        this.currentSeason = currentSeason;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public void cheatAdvanceTime(int hours) {
        advanceTime(hours * 60);
    }

    public void cheatAdvanceDate(int days) {
        advanceDate(days);
    }
}
}
