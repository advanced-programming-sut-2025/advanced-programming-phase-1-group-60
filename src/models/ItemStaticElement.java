package models;

public class ItemStaticElement implements StaticElement {
    private final char symbol;
    private final boolean passable;

    // سازنده
    public ItemStaticElement(char symbol, boolean isPassable) {
        this.symbol = symbol;
        this.passable = isPassable;
    }

    // پیاده‌سازی متد symbol
    @Override
    public char symbol() {
        return this.symbol;
    }

    // پیاده‌سازی متد isPassable
    @Override
    public boolean isPassable() {
        return this.passable;
    }
}