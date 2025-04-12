package controller;

import models.Game;
import models.User;
import models.Result;

import java.util.List;

public class GameController {
    private Game currentGame;

    // Start a new game
    // TODO : start a new game with the given players
    public boolean startNewGame(List<User> players) {
        currentGame = new Game();
        return true;
    }

    // Load an existing game
    // TODO : Load the saved game from the JSON file
    public boolean loadGame(int gameId) {
        return true;
    }



    public Result chooseMap (int number){
        return null;
    }

}
