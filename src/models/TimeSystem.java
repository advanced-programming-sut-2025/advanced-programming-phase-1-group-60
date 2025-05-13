package models;

public class TimeSystem {
    // نمونه singleton
    private static final TimeSystem INSTANCE = new TimeSystem();

    // فیلدهای زمان بازی
    private int currentHour;
    private int currentDay;
    private String currentSeason;
    private String dayOfWeek;
    private int currentYear;

    // سازنده خصوصی با مقداردهی اولیه
    private TimeSystem() {
        this.currentHour = 9;            // ساعت شروع: 9 صبح
        this.currentDay = 1;             // روز اول فصل
        this.currentSeason = "Spring"; // فصل بهار
        this.dayOfWeek = "Monday";     // دوشنبه
        this.currentYear = 1;            // سال اول
    }

    /** دسترسی به نمونه singleton */
    public static TimeSystem getInstance() {
        return INSTANCE;
    }

    /**
     * جلو بردن زمان بازی به تعداد ساعت مشخص
     * @param hours تعداد ساعت (عدد منفی غیرمجاز است)
     */
    public synchronized void advanceTime(int hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Cannot advance negative hours");
        }
        currentHour += hours;
        while (currentHour >= 24) {
            currentHour -= 24;
            advanceDate(1);
        }
    }

    /**
     * جلو بردن تاریخ بازی به تعداد روز مشخص
     * @param days تعداد روز (عدد منفی غیرمجاز است)
     */
    public synchronized void advanceDate(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Cannot advance negative days");
        }
        currentDay += days;
        while (currentDay > 28) {
            currentDay -= 28;
            updateSeason();
        }
        calculateDayOfWeek();
    }

    // محاسبه روز هفته براساس currentDay
    private void calculateDayOfWeek() {
        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        int index = (currentDay - 1) % 7;
        dayOfWeek = days[index];
    }

    // به‌روزرسانی فصل و سال
    private void updateSeason() {
        String[] seasons = {"Spring","Summer","Fall","Winter"};
        int idx = java.util.Arrays.asList(seasons).indexOf(currentSeason);
        currentSeason = seasons[(idx + 1) % seasons.length];
        if ("Spring".equals(currentSeason)) {
            currentYear++;
        }
    }

    /** نمایش ساعت فعلی با فرمت HH:00 */
    public synchronized String getCurrentTime() {
        return String.format("%02d:00", currentHour);
    }

    /** نمایش تاریخ فعلی */
    public synchronized String getCurrentDate() {
        return String.format("Year %d, %s %d", currentYear, currentSeason, currentDay);
    }

    /** نمایش روز هفته */
    public synchronized String getDayOfWeek() {
        return dayOfWeek;
    }

    /** نمایش ترکیبی ساعت و تاریخ */
    public synchronized String getDateTime() {
        return getCurrentDate() + " " + getCurrentTime() + " (" + dayOfWeek + ")";
    }

    /** نمایش فصل فعلی */
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
}
