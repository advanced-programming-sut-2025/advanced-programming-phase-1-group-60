package models;

import java.util.*;
public class FarmManager {
    private final List<Farm> farms = new ArrayList<>();
    public FarmManager() {
        farms.add(new Farm(FarmTemplate.template1()));
        farms.add(new Farm(FarmTemplate.template2()));
        farms.add(new Farm(FarmTemplate.template3()));
        farms.add(new Farm(FarmTemplate.template4()));
    }
    public Farm getFarm(int index) { return farms.get(index); }

    public List<Farm> getAllFarms() { return farms; }
}