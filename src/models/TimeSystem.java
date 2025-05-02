package models;

public class TimeSystem {
    private int currentHour = 9;
    private int currentMinute = 0;
    private int currentDay = 1;
    private String currentSeason = "Spring";
    private String dayOfWeek = "Monday";
    private int currentYear = 1;

    public void advanceTime(int minutes) {
        currentMinute += minutes;
        while (currentMinute >= 60) {
            currentMinute -= 60;
            currentHour++;
        }
        if (currentHour >= 24) {
            currentHour -= 24;
            advanceDate(1);
        }

    }

    public void advanceDate(int days) {
        currentDay += days;
        while (currentDay > 28) {
            currentDay -= 28;
            updateSeason();
        }
        calculateDayOfWeek();

    }


    private void calculateDayOfWeek() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int index = (currentDay - 1) % 7;
        dayOfWeek = days[index];
    }

    private void updateSeason() {
        String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
        int index = 0;
        for (int i = 0; i < seasons.length; i++) {
            if (seasons[i].equals(currentSeason)) {
                index = i;
                break;
            }
        }
        currentSeason = seasons[(index + 1) % seasons.length];
        if (currentSeason.equals("Spring")) {
            currentYear++;
        }
    }


    public String getCurrentTime() {
        return String.format("%02d:%02d", currentHour, currentMinute);
    }


    public String getCurrentDate() {
        return String.format("Year %d, %s %d", currentYear, currentSeason, currentDay);
    }

    public String getDayOfWeek() {
        return dayOfWeek;
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

}

