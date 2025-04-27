package models;

public class Stone implements RandomElement {
    public char symbol() { return 'S'; }
    public boolean isPassable() { return false; }
}