package controller;

import models.Map;
import models.User;

public class MapController {
    private GameController gameController;
    private Map currentMap;

    //Player Movement
    // TODO : calculate the shortest path for the player to move and check if the player can move to the new position
    public boolean movePlayer(int x, int y, User player) {
        return true;
    }

    // Tile Status
    // TODO : update the tile status with the new player position
    public boolean updateTileStatus(int x, int y, String newStatus) {
        return true;
    }

    // Tile Accessibility
    // TODO : both functions check if the tile is not out of bounds and if it can be accessed
    private boolean isValidMove(int x, int y) {
        return true;
    }
    private boolean isValidPosition(int x, int y) {
        return true;
    }
}
