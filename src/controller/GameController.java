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
        if (players == null || players.size() > 4 || players.size() < 2) {
            return false;
        }
        currentGame = new Game();
        return true;
    }

    // Load an existing game
    // TODO : Load the saved game from the JSON file
    public boolean loadGame(int gameId) {
        return true;
    }

    // Save the game and exit it
    // TODO : Save the game into a JSON file (or any other alternatives)
    public boolean exitGame() {
        if (currentGame == null) {
            return false;
        }
        currentGame = null;
        return true;
    }

    // Current Game State
    // TODO : Update the game state such as turns, time, weather ,etc.
    public void updateGameState() {
        if (currentGame != null) {
        }
    }

}
