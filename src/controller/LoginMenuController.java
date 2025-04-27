package controller;

import models.Question;
import models.Result;
import models.User;

import java.util.HashMap;
import java.util.Map;

public class LoginMenuController {
    //Login
    private Map<String, User> users;
    private User loggedInUser;

    public LoginMenuController() {
        users = new HashMap<>();
    }

    public Result Login(String username, String password, boolean stayLoggedIn) {
        User user = users.get(username);
        if (user != null) {
            try {
                if (user.getHashPassword().equals(User.hashedPassword(password))) {
                    loggedInUser = user;
                    return new Result(true, "Login successful.", user);
                }
            } catch (Exception e) {
                return new Result(false, "Internal error during login.");
            }
        }
        return new Result(false, "Invalid username or password.");
    }


    public String forgetPassword(String username, String question) {
        User user = users.get(username);
        if (user != null) {
            return "Security question for password recovery.";
        }
        return "User not found.";
    }

    public Result processAnswerQuestion(String question, String answer) {
        // Assume correct answer for simplicity
        return new Result(true, "Correct answer.");
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
