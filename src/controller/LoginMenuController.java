package controller;

import models.Question;
import models.Result;
import models.User;
import repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginMenuController {
    //Login
    private Map<String, User> users;
    private User loggedInUser;
    private final UserRepository userRepository;

    public LoginMenuController(Scanner scanner) {
        users = new HashMap<>();
        this.userRepository = UserRepository.getInstance();
    }

    public Result Login(String username, String password, boolean stayLoggedIn) {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPlainPassword().trim().equals(password.trim())) {
            loggedInUser = user;
            return new Result(true, "Login successful.", user);
        }
        return new Result(false, "Invalid username or password.");
    }


    public Result forgetPassword(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new Result(false, "No user found with username: " + username);
        }
        return new Result(true, user.getSecurityQuestionInfo());
    }

    public Result checkSecurityAnswer(String username, String answer) {
        User user = UserRepository.getInstance().getUserByUsername(username);
        if (user == null) {
            return new Result(false, "User not found.");
        }

        if (user.verifySecurityQuestion(answer)) {
            return new Result(true, "Your password is: " + user.getPlainPassword()
             + "\nYour hashed password is: " + user.getHashPassword());
        }
        return new Result(false, "Wrong answer to security question.");
    }
    public Result processAnswerQuestion(String question, String answer) {
        // Assume correct answer for simplicity
        return new Result(true, "Correct answer.");
    }
    public UserRepository getUserRepository() {
        return userRepository;
    }
    public User getLoggedInUser() {
        return loggedInUser;
    }
}
