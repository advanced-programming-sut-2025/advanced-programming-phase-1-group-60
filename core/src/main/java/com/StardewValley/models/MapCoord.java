package com.StardewValley.models;

/**
 * Simple holder for map coordinates.
 */
public class MapCoord {
    public final int x;
    public final int y;
    public MapCoord(int x, int y) { this.x = x; this.y = y; }

    public int getX() { return x; }
    public int getY() { return y; }
}
