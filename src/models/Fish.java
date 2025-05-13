package models;

public class Fish {
    private String name;
    private int basePrice;
    private String season;
    private boolean isLegendary;

    public Fish(String name, int basePrice, String season, boolean isLegendary) {
        this.name = name;
        this.basePrice = basePrice;
        this.season = season;
        this.isLegendary = isLegendary;
    }

    // Getters
    public String getName() { return name; }
    public int getBasePrice() { return basePrice; }
    public String getSeason() { return season; }
    public boolean isLegendary() { return isLegendary; }
}
