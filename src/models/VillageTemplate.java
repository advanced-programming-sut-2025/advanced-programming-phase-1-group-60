// models/VillageTemplate.java
package models;

import repository.NpcRepository;
import repository.StoreRepository;

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
        StoreRepository storeRepo = StoreRepository.getInstance();
        List<Placement> placements = new ArrayList<>();

        // 1) ثبت فروشگاه‌ها با استفاده از کانستراکتور صحیح
        List<Store> defaultStores = List.of(
                new Store("Blacksmith", 2, 2),
                new Store("Marin'sRanch", 8, 2),
                new Store("Carpenter'sShop", 14, 2),
                new Store("Fish Shop", 2, 8),
                new Store("The Stardrop Saloon", 8, 8),
                new Store("Animal Shop", 14, 8),
                new Store("Wizard Tower", 8, 14)
        );
        for (Store store : defaultStores) {
            storeRepo.addStore(store);
            placements.add(new Placement(
                    store,
                    store.getLeftCornerX(),
                    store.getLeftCornerY(),
                    4, 4
            ));
        }
        // مقداردهی آیتم‌ها پس از ثبت
        storeRepo.initializeStoreItems();


        for (Npc npc : repository.NpcRepository.getInstance().getAllNpcs()) {
            placements.add(new Placement(
                    npc,
                    npc.getPositionX(),
                    npc.getPositionY(),
                    1, 1
            ));
        }

        return new VillageTemplate(20, 20, placements);
    }
}
