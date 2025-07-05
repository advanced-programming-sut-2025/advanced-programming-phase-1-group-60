package com.StardewValley.models;

import java.util.List;

public class ForagingTree extends ForageItem {
    private String name;
    private List<String> suitableSeasons;

    public ForagingTree(String name, List<String> suitableSeasons) {
        this.name = name;
        this.suitableSeasons = suitableSeasons;
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
}
