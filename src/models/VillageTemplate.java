// models/VillageTemplate.java
package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a single village layout (center of the map), placing StaticElements.
 */
public class VillageTemplate {
    private final int width;
    private final int height;
    private final List<Placement> placements;

    public static class Placement {
        private final StaticElement element;
        private final int x, y, width, height;
        public Placement(StaticElement element, int x, int y, int w, int h) {
            this.element = element;
            this.x = x; this.y = y;
            this.width = w; this.height = h;
        }
        public StaticElement getElement() { return element; }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
    }

    public VillageTemplate(int w, int h, List<Placement> placements) {
        this.width = w;
        this.height = h;
        this.placements = placements;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Placement> getPlacements() { return placements; }

    /**
     * Build a 20×20 village using provided elements and their coords.
     * Placement list must cover exactly 7 elements and maintain ≥50% empties.
     */
    public static VillageTemplate withPlacements(List<Placement> placements) {
        return new VillageTemplate(20, 20, placements);
    }

    public static VillageTemplate createDefaultVillage() {
        List<Placement> placements = new ArrayList<>();

        // ایجاد 7 استور با مکان‌های فرضی
        placements.add(new Placement(new Store("General Store"), 2, 2, 4, 4));
        placements.add(new Placement(new Store("Blacksmith"), 8, 2, 4, 4));
        placements.add(new Placement(new Store("Carpenter"), 14, 2, 4, 4));
        placements.add(new Placement(new Store("Saloon"), 2, 8, 4, 4));
        placements.add(new Placement(new Store("Hospital"), 8, 8, 4, 4));
        placements.add(new Placement(new Store("Animal Shop"), 14, 8, 4, 4));
        placements.add(new Placement(new Store("Wizard Tower"), 8, 14, 4, 4));

        return new VillageTemplate(20, 20, placements);
    }
}
