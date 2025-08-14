package com.StardewValley.controller;

import com.StardewValley.models.Result;
import com.StardewValley.models.User;
import com.StardewValley.repository.UserRepository;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginMenuController {
    //Login
    private Map<String, User> users;
    private User loggedInUser;
    private final UserRepository userRepository;

    private static final String PREFS_NAME = "stardew_login_prefs";
    private static final String PREF_KEY_STAY_USER = "stay_user";

    public LoginMenuController(Scanner scanner) {
        users = new HashMap<>();
        this.userRepository = UserRepository.getInstance();
    }

    /**
     * Normal login flow with optional stay-logged-in persistence.
     */
    public Result Login(String username, String password, boolean stayLoggedIn) {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPlainPassword().trim().equals(password.trim())) {
            loggedInUser = user;

            // Apply choice
            user.setStayLoggedIn(stayLoggedIn);

            Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
            if (stayLoggedIn) {
                prefs.putString(PREF_KEY_STAY_USER, user.getUsername());
            } else {
                prefs.remove(PREF_KEY_STAY_USER);
            }
            prefs.flush();

            return new Result(true, "Login successful.", user);
        }
        return new Result(false, "Invalid username or password.");
    }

    /**
     * Try to auto-login using stored preference (if user still exists and flag is active).
     * Returns the logged-in User or null.
     */
    public User tryAutoLogin() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        String savedUsername = prefs.getString(PREF_KEY_STAY_USER, null);
        if (savedUsername == null) return null;

        User user = userRepository.getUserByUsername(savedUsername);
        if (user != null && user.isStayLoggedIn()) {
            loggedInUser = user;
            return user;
        } else {
            // Clean up invalid preference
            prefs.remove(PREF_KEY_STAY_USER);
            prefs.flush();
        }
        return null;
    }

    /**
     * Clear current session and persistence.
     */
    public void logout() {
        if (loggedInUser != null) {
            loggedInUser.setStayLoggedIn(false);
        }
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.remove(PREF_KEY_STAY_USER);
        prefs.flush();
        loggedInUser = null;
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
        return new Result(true, "Correct answer."); // placeholder
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
