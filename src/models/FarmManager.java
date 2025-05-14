package models;

import java.util.*;
public class FarmManager {
    private final List<Farm> farms = new ArrayList<>();
    public FarmManager() {
        farms.add(new Farm(FarmTemplate.template1(), 1, 4));
        farms.add(new Farm(FarmTemplate.template2(), 4, 7));
        farms.add(new Farm(FarmTemplate.template3(), 2, 42));
        farms.add(new Farm(FarmTemplate.template4(), 39, 4));
    }
    public Farm getFarm(int index) { return farms.get(index); }

    public List<Farm> getAllFarms() { return farms; }
}