package com.StardewValley.controller;

import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.models.Question;
import com.StardewValley.models.Result;
import com.StardewValley.models.User;
import com.StardewValley.repository.UserRepository;
import com.StardewValley.Network.SessionManager;

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

    public Result Login(String username, String password, boolean stayLoggedIn,String clientId) {
        userRepository.loadUsers();
        User user = userRepository.getUserByUsername(username);
        SessionManager sessionManager = SessionManager.getInstance();
        if (user != null && user.getPlainPassword().trim().equals(password.trim())) {
            if (sessionManager.isLoggedIn(username)) {
                String loggedClient = sessionManager.getClientForUser(username);
                return new Result(false, "User is already logged in from: " + loggedClient);
            }
            loggedInUser = user;
            if (stayLoggedIn) {
                user.setStayLoggedIn(true);
            }
            sessionManager.login(username, clientId);
            ClientMain.connectToServer(loggedInUser.getUsername());
            System.out.println("Client on this PC is now associated with user: " + loggedInUser.getUsername());
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
            return new Result(true, "Answer correct");
        }
        return new Result(false, "Wrong answer to security question.");
    }
    public Result setNewPassword(String username, String newPassword) {
        User user = UserRepository.getInstance().getUserByUsername(username);
        if (user == null) {
            return new Result(false, "User not found.");
        }

        try {
            user.setHashPassword(newPassword);
            return new Result(true, "Password changed successfully.");
        } catch (Exception e) {
            return new Result(false, e.getMessage());
        }
    }
    public String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
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
