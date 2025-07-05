package com.StardewValley.controller;

import com.StardewValley.models.Question;
import com.StardewValley.models.Result;
import com.StardewValley.models.User;
import com.StardewValley.repository.UserRepository;

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
            if (stayLoggedIn) {
                user.setStayLoggedIn(true);
            }
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
            System.out.println("Choose your password reset option:");
            System.out.println("1. Generate random password");
            System.out.println("2. Enter new password");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim();

            try {
                if (choice.equals("1")) {
                    String randomPass = generateRandomPassword();
                    user.setHashPassword(randomPass);
                    return new Result(true, "Your new password is: " + randomPass);
                } else if (choice.equals("2")) {
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine().trim();
                    if (!User.verifyPassword(newPassword)) {
                        return new Result(false, "Password is weak.");
                    }
                    user.setHashPassword(newPassword);
                    return new Result(true, "Password changed successfully.");
                } else {
                    return new Result(false, "Invalid choice.");
                }
            } catch (Exception e) {
                return new Result(false, "Error updating password: " + e.getMessage());
            }
        }
        return new Result(false, "Wrong answer to security question.");
    }

    private String generateRandomPassword() {
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
