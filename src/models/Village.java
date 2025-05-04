// models/Village.java
package models;

import java.util.ArrayList;
import java.util.List;

/**
 * A single, centrally-placed village on the world map.
 * Contains fixed StaticElements (e.g., Store) and ≥50% walkable tiles.
 */
public class Village {
    private final Tile[][] tiles;
    private final int width;
    private final int height;
    private final List<StaticElement> elements = new ArrayList<>();

    public Village(VillageTemplate tpl) {
        this.width = tpl.getWidth();
        this.height = tpl.getHeight();
        this.tiles = new Tile[height][width];
        // init empty walkable tiles
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile(x, y);
            }
        }
        // place fixed elements
        for (VillageTemplate.Placement p : tpl.getPlacements()) {
            StaticElement e = p.getElement();
            elements.add(e);
            for (int dy = 0; dy < p.getHeight(); dy++) {
                for (int dx = 0; dx < p.getWidth(); dx++) {
                    tiles[p.getY() + dy][p.getX() + dx].setStaticElement(e);
                }
            }
        }
        // >50% empty by design
    }

    public Tile getTile(int x, int y) {
        return tiles[y][x];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<StaticElement> getElements() { return elements; }
}