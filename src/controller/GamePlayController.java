package controller;

import models.*;

import java.util.Scanner;

public class GamePlayController {
    Scanner sc;

    private final Tile[][] tiles;
    private final User user;
    private final Scanner in = new Scanner(System.in);

    public GamePlayController(Farm f, User u, Scanner sc) {
        this.tiles = f.getTiles();
        this.user = u;
        this.sc = sc;
    }

    public void getAndProcessInput() {
        String input = sc.nextLine();
        

    }

    public void walkTo(int tx, int ty) {
        if (tx < 0 || ty < 0 || ty >= tiles.length || tx >= tiles[0].length) {
            System.out.println("خارج از مرز مزرعه!");
            return;
        }
        Tile target = tiles[ty][tx];
        if (!target.isPassable()) {
            System.out.println("غیرقابل عبور");
            return;
        }
        var opt = PathFinder.findShortest(tiles, user.getPosition(), target);
        if (opt.isEmpty()) {
            System.out.println("مسیر نیست");
            return;
        }
        var path = opt.get();
        int need = (path.getDistance() + 10 * path.getTurns()) / 20;
        System.out.printf("مسافت=%d، پیچ=%d، انرژی=%d. ادامه؟ (y/n)\n",
                path.getDistance(), path.getTurns(), need);
        if (!in.nextLine().equalsIgnoreCase("y")) return;
        if (user.getEnergy().getCurrentEnergy() >= need || user.getEnergy().isUnlimited()) {
            user.consumeEnergy(need);
            user.setPosition(target);
            System.out.println("حرکت شد. انرژی=" + user.getEnergy());
            if (user.getEnergy().getCurrentEnergy() == 0) user.faint();
        } else user.faintAlong(path);
    }












































    public Result saveGame() {
        return null;
    }

    public Result forceTerminate() {
        return null;
    }

    public Result nextTurn() {
        updateGameState();
        return null;
    }

    // Current Game State
    // TODO : Update the game state such as turns, time, weather ,etc.
    private void updateGameState() {
    }

    public Result walk() {

        // if (energy <= 0)
        faint();
        return null;
    }

    public Result printMap() {
        return null;
    }

    public Result helpReadingMap() {
        return null;
    }

    public Result showEnergy() {
        return null;
    }

    private Result faint() {
        return null;
    }


    // cheat functions :
}
