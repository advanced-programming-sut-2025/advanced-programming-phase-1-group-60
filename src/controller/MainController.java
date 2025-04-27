package controller;

import models.Result;
import models.User;

public class MainController {
    private User loggedInUser;

    public MainController(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public Result logout() {
        if (loggedInUser != null) {
            loggedInUser = null;
            return new Result(true, "Logout successful.");
        }
        return new Result(false, "No user is currently logged in.");
    }

    public Result goToGame() {
        if (loggedInUser != null) {
            return new Result(true, "Entering the game...");
        }
        return new Result(false, "You must be logged in to enter the game.");
    }

    public Result goToProfile() {
        if (loggedInUser != null) {
            return new Result(true, "Opening profile menu...");
        }
        return new Result(false, "You must be logged in to view profile.");
    }

    public Result exitGame() {
        return new Result(true, "Exiting game. Goodbye!");
    }
}
