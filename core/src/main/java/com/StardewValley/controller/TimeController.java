package com.StardewValley.controller;

import com.StardewValley.models.TimeSystem;

public class TimeController {
    private final TimeSystem timeSystem;

    public TimeController() {
        // دسترسی به یک نمونه singleton از TimeSystem
        this.timeSystem = TimeSystem.getInstance();
    }

    /** نمایش فصل فعلی بازی */
    public String getCurrentSeason() {
        return timeSystem.getCurrentSeason();
    }

    /** نمایش ساعت فعلی بازی */
    public String getCurrentTime() {
        return timeSystem.getCurrentTime();
    }

    /** نمایش تاریخ فعلی بازی */
    public String getCurrentDate() {
        return timeSystem.getCurrentDate();
    }

    /** نمایش روز هفته */
    public String getDayOfWeek() {
        return timeSystem.getDayOfWeek();
    }

    /** جلو بردن زمان به اندازه X ساعت */
    public String advanceTime(int hours) {
        try {
            timeSystem.advanceTime(hours);
            return "Time advanced by " + hours + " hours.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }

    /** جلو بردن تاریخ به اندازه X روز */
    public String advanceDate(int days) {
        try {
            timeSystem.advanceDate(days);
            return "Date advanced by " + days + " days.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
}
