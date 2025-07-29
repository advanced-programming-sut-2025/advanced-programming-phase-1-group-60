// StardewValley/models/StaticElement.java
package com.StardewValley.models;

public interface StaticElement {
    char symbol();
    boolean isPassable();
    String getName(); // Added: Name for the static element
    int getWidth();   // Added: Width for static elements, useful for placement
    int getHeight();  // Added: Height for static elements, useful for placement
}
