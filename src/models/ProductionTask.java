package models;
public class ProductionTask {
    private String itemName;
    private int day; // روزی که تولید تمام می‌شود
    private int finishHour; // ساعتی که تولید تمام می‌شود

    public ProductionTask(String itemName, int day, int finishHour) {
        this.itemName = itemName;
        this.day = day;

        // اگر ساعت بیش از 22 باشد، به روز بعد منتقل شود
        while (finishHour >= 22) {
            finishHour -= 13; // ساعت‌ها را کاهش می‌دهیم
            day++; // یک روز به جلو می‌رویم
        }

        this.finishHour = finishHour;
        this.day = day;
    }

    public String getItemName() {
        return itemName;
    }

    public int getDay() {
        return day;
    }

    public int getFinishHour() {
        return finishHour;
    }
}