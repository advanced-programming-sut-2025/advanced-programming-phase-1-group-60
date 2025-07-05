package com.StardewValley.models;

import java.util.ArrayList;
import java.util.List;

public class Barn extends Item {
    private int capacity;
    private int width;
    private int height;
    private int leftCornerX;
    private int leftCornerY;
    private List<Animal> animals = new ArrayList<>();

    public Barn(String name, int capacity, int width, int height) {
        setName(name);
        setType("Barn");
        this.capacity = capacity;
        this.width = width;
        this.height = height;
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getLeftCornerX() { return leftCornerX; }
    public int getLeftCornerY() { return leftCornerY; }
    public List<Animal> getAnimals() { return animals; }
    public int getCapacity() { return capacity; }

    // Setters
    public void setPosition(int x, int y) {
        this.leftCornerX = x;
        this.leftCornerY = y;
    }
}
