package com.StardewValley.models;

import java.util.*;
public class FarmManager {
    private final List<Farm> farms = new ArrayList<>();
    public FarmManager() {
        farms.add(new Farm(FarmTemplate.template1(), 2, 2));
        farms.add(new Farm(FarmTemplate.template2(), 5, 5));
        farms.add(new Farm(FarmTemplate.template3(), 3, 40));
        farms.add(new Farm(FarmTemplate.template4(), 40, 2));
    }
    public Farm getFarm(int index) { return farms.get(index); }

    public List<Farm> getAllFarms() { return farms; }
}
