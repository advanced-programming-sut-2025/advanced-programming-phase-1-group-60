package controller;

import models.Farm;
import models.Map;
import models.Tile;
import models.User;

public class MapController {
    private GameController gameController;
    private Map currentMap;
    private Tile[][] view;
    public MapController(Farm f) { this.view = f.getTiles(); }
    public void helpReadingMap() {
        System.out.println("T=Tree, S=Stone, F=Forage, C=Cabin, G=Greenhouse, L=Lake, Q=Quarry");
    }
    public void printMap(int cx,int cy,int size){
        for(int y=cy;y<cy+size;y++){
            for(int x=cx;x<cx+size;x++){
                var t=view[y][x];
                if(t.getStaticElement().isPresent()) System.out.print(t.getStaticElement().get().symbol());
                else if(t.getRandomElement().isPresent()) System.out.print(t.getRandomElement().get().symbol());
                else System.out.print('.');
            }
            System.out.println();
        }
    }



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
