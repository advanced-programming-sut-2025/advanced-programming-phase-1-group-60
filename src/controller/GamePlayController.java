package controller;

import models.Result;

import java.util.Scanner;

public class GamePlayController {
    Scanner sc;

    public GamePlayController(Scanner sc) {
        this.sc = sc;
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
    private void updateGameState() {}

    public Result walk() {

        // if (energy <= 0)
        faint();
        return null;
    }

    public Result printMap () {
        return null;
    }

    public Result helpReadingMap() {
        return null;
    }

    public Result showEnergy() {
        return null;
    }

    private Result faint () {
        return null;
    }









    // cheat functions :
}
