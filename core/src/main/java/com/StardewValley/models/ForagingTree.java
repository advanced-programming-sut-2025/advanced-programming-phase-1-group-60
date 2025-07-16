package com.StardewValley.models;

import java.util.List;

public class ForagingTree extends ForageItem {
    private String name;
    private List<String> suitableSeasons;
    private String imagePath;

    public ForagingTree(String name, List<String> suitableSeasons) {
        this.name = name;
        this.suitableSeasons = suitableSeasons;
    }

    public ForagingTree(ForagingTree other) {
        this.name = other.name;
        this.suitableSeasons = other.suitableSeasons;
        this.imagePath = other.imagePath;
    }

    public ForagingTree() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSuitableSeasons() {
        return suitableSeasons;
    }

    public void setSuitableSeasons(List<String> suitableSeasons) {
        this.suitableSeasons = suitableSeasons;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
