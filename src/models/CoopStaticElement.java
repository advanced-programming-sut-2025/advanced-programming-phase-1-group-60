package models;

public class CoopStaticElement implements StaticElement {
    @Override
    public char symbol() { return 'O'; }
    @Override
    public boolean isPassable() { return false; }
}
