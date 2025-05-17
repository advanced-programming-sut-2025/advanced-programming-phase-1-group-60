package repository;

import models.Fish;
import models.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FishingRepository {
    private static final List<Fish> fishes = new ArrayList<>();

    static {
        fishes.add(new Fish("Salmon", 75, "Fall", false));
        fishes.add(new Fish("Sardine", 40, "Fall", false));
        fishes.add(new Fish("Shad", 60, "Fall", false));
        fishes.add(new Fish("Blue Discus", 120, "Fall", false));
        fishes.add(new Fish("Midnight Carp", 150, "Winter", false));
        fishes.add(new Fish("Squid", 80, "Winter", false));
        fishes.add(new Fish("Tuna", 100, "Winter", false));
        fishes.add(new Fish("Perch", 55, "Winter", false));
        fishes.add(new Fish("Flounder", 100, "Spring", false));
        fishes.add(new Fish("Lionfish", 100, "Spring", false));
        fishes.add(new Fish("Herring", 30, "Spring", false));
        fishes.add(new Fish("Ghostfish", 45, "Spring", false));
        fishes.add(new Fish("Tilapia", 75, "Summer", false));
        fishes.add(new Fish("Dorado", 100, "Summer", false));
        fishes.add(new Fish("Sunfish", 30, "Summer", false));
        fishes.add(new Fish("Rainbow Trout", 65, "Summer", false));
        fishes.add(new Fish("Legend", 5000, "Spring", true));
        fishes.add(new Fish("Glacierfish", 1000, "Winter", true));
        fishes.add(new Fish("Angler", 900, "Fall", true));
        fishes.add(new Fish("Crimsonfish", 1500, "Summer", true));
    }

    public static double calculateFishAmount(int fishingSkill, String currentWeather) {
        if (currentWeather == null) currentWeather = "sunny";
        double M;
        switch (currentWeather) {
            case "sunny": M = 1.5; break;
            case "rain": M = 1.2; break;
            case "storm": M = 0.5; break;
            case "snow": M = 1.0; break;
            default: M = 1.0;
        }
        double R = Math.random();
        return R * M * (fishingSkill + 2);
    }

    public static double calculateFishValue(int fishingSkill, String currentWeather, Tools.FishingpoleStage poleStage) {
        if (currentWeather == null) currentWeather = "sunny";
        double M;
        switch (currentWeather) {
            case "sunny": M = 1.5; break;
            case "rain": M = 1.2; break;
            case "storm": M = 0.5; break;
            case "snow": M = 1.0; break;
            default: M = 1.0;
        }
        double pole;
        switch (poleStage) {
            case TRAINING: pole = 0.1; break;
            case BAMBOO: pole = 0.5; break;
            case FIBERGLASS: pole = 0.9; break;
            case IRIDIUM: pole = 1.2; break;
            default: pole = 0.1;
        }
        double R = Math.random();
        return (R * (fishingSkill + 2) * pole) / (7 - M);
    }

    public static List<Fish> getAvailableFish(List<Fish> allFish, String currentSeason, int fishingSkill) {
        return allFish.stream()
                .filter(fish -> fish.getSeason().equalsIgnoreCase(currentSeason))
                .filter(fish -> !fish.isLegendary() || fishingSkill == 4)
                .collect(Collectors.toList());
    }

    public static List<Fish> getFishes() {
        return new ArrayList<>(fishes);
    }
}