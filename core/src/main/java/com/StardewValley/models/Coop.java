package com.StardewValley.models;

import java.util.ArrayList;
import java.util.List;

public class Coop extends Item {
    private int capacity;
    private int width;
    private int height;
    private int leftCornerX;
    private int leftCornerY;
    private List<Animal> animals = new ArrayList<>();

    public Coop(String name, int capacity, int width, int height) {
        setName(name);
        setType("Coop");
        this.capacity = capacity;
        this.width = width;
        this.height = height;
    }

    public boolean addAnimal(Animal animal) {
        if (animals.size() < capacity) {
            animals.add(animal);
            return true;
        }
        return false;
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
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
