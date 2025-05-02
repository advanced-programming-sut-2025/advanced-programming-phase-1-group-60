package controller;

import models.TimeSystem;

public class TimeController {
    private final TimeSystem timeSystem;

    public TimeController(TimeSystem timeSystem) {
        this.timeSystem = timeSystem;
    }

    public String getCurrentSeason() {
        return timeSystem.getCurrentSeason();
    }


    public String getCurrentTime() {
        return timeSystem.getCurrentTime();
    }


    public String getCurrentDate() {
        return timeSystem.getCurrentDate();
    }

    public String getDayOfWeek() {
        return timeSystem.getDayOfWeek();
    }


    public String advanceTime(int hours) {
        int minutes = hours * 60; // تبدیل ساعت به دقیقه
        timeSystem.advanceTime(minutes);
        return "Time advanced by " + hours + " hours.";
    }

    public String advanceDate(int days) {
        timeSystem.advanceDate(days);
        return "Date advanced by " + days + " days.";
    }
}