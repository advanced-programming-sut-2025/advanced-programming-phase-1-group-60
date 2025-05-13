package models;

public class BarnStaticElement implements StaticElement {
    @Override
    public char symbol() { return 'B'; }
    @Override
    public boolean isPassable() { return false; }
}