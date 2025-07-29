package com.StardewValley.models;

import java.util.Map;

public class ProductionTask {
    private String itemName;
    private int startDayInitial; // NEW: The day production truly started (before any internal adjustments)
    private int startHourActual; // NEW: The hour production truly started
    private int day; // روزی که تولید تمام می‌شود (This field now represents the adjusted finish day after constructor logic)
    private int finishHour; // ساعتی که تولید تمام می‌شود (This field now represents the adjusted finish hour after constructor logic)
    private int totalCraftingDurationHours; // NEW: The total duration this item takes to craft
    private boolean isComplete; // To track if the task is finished
    private int quantity;
    private Map<String, Integer> consumedMaterials;
    /**
     * Constructs a ProductionTask.
     * The constructor will adjust 'day' and 'finishHour' based on the game's custom time rules (e.g., if finishHour exceeds 22).
     *
     * @param itemName The name of the item being produced.
     * @param startDayInitial The actual day the production started.
     * @param startHourActual The actual hour the production started.
     * @param calculatedFinishHour The calculated finish hour based on startHourActual + totalCraftingDurationHours,
     * before any internal adjustments by the ProductionTask itself.
     * @param totalCraftingDurationHours The total duration in hours for this crafting task.
     */
    public ProductionTask(String itemName, int startDayInitial, int startHourActual, int calculatedFinishHour, int totalCraftingDurationHours , int quantity) {
        this.itemName = itemName;
        this.startDayInitial = startDayInitial;
        this.startHourActual = startHourActual;
        this.totalCraftingDurationHours = totalCraftingDurationHours;
        this.quantity = quantity;
        // Initialize 'day' and 'finishHour' with the values that will be adjusted by the while loop
        this.day = startDayInitial; // Start day for the internal adjustment loop
        this.finishHour = calculatedFinishHour; // The raw calculated finish hour

        // اگر ساعت بیش از 22 باشد، به روز بعد منتقل شود
        // This is the original logic that modifies the finishHour and day
        while (this.finishHour >= 22) { // Use 'this' to refer to instance fields
            this.finishHour -= 13; // ساعت‌ها را کاهش می‌دهیم
            this.day++; // یک روز به جلو می‌رویم
        }
    }

    public String getItemName() {
        return itemName;
    }

    public int getDay() {
        return day; // This now returns the adjusted finish day
    }

    public int getFinishHour() {
        return finishHour; // This now returns the adjusted finish hour
    }

    // NEW Getters for progress calculation
    public int getStartDayInitial() {
        return startDayInitial;
    }

    public int getStartHourActual() {
        return startHourActual;
    }
    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) { // Optional: A setter for quantity if needed
        this.quantity = quantity;
    }
    public int getTotalCraftingDurationHours() {
        return totalCraftingDurationHours;
    }
    public Map<String, Integer> getConsumedMaterials() {
        return consumedMaterials;
    }
}
