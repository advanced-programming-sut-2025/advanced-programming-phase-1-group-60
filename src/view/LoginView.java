package view;

import controller.LoginMenuController;
import models.Result;
import models.User;
import view.commands.LoginCommands;

import java.util.Scanner;

public class LoginView extends View {
    private final LoginMenuController loginMenuController;
    private final Scanner scanner;
    private String forgottenPasswordUsername = null;

    public LoginView(LoginMenuController loginMenuController, Scanner scanner) {
        this.loginMenuController = loginMenuController;
        this.scanner = scanner;
    }

    @Override
    public void display() {
//        System.out.println("=== Welcome to Stardew Valley Login Menu ===");
//        System.out.println("- login");
//        System.out.println("- forget password");
//        System.out.println("- exit");

        while (true) {
            String input = scanner.nextLine().trim();
            LoginCommands command = LoginCommands.getCommand(input);

            if (command == null) {
                System.out.println("Invalid command. Please try again.");
            } else if (command == LoginCommands.LOGIN) {
                handleLogin(input);
            } else if (command == LoginCommands.FORGET_PASSWORD) {
                handleForgetPassword(input);
            } else if (command == LoginCommands.ANSWER) {
                handleAnswerQuestion(input);
            } else if (command == LoginCommands.EXIT) {
                System.out.println("Exiting login menu...");
                break;
            }
        }
    }

    private void handleLogin(String input) {
        String[] parts = input.split("\\s+");
        String username = null, password = null;

        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].equals("-u")) {
                username = parts[i + 1];
            }
            if (parts[i].equals("-p")) {
                password = parts[i + 1];
            }
        }

        if (username == null || password == null) {
            System.out.println("Invalid login command format.");
            return;
        }

        Result result = loginMenuController.Login(username, password, false);
        if (result.isSuccess()) {
            System.out.println("Login successful! Welcome, " + username + "!");
        } else {
            System.out.println(result.getMessage());
        }
    }

    private void handleForgetPassword(String input) {
        String[] parts = input.split(" -");
        try {
            String username = getValue(parts, "u");
            Result result = loginMenuController.forgetPassword(username);
            System.out.println(result.getMessage());
            if (result.isSuccess()) {
                forgottenPasswordUsername = username;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleAnswerQuestion(String input) {
        if (forgottenPasswordUsername == null) {
            System.out.println("Please use 'forget password -u <username>' first.");
            return;
        }

        String[] parts = input.split(" -");
        try {
            String answer = getValue(parts, "a");
            Result result = loginMenuController.checkSecurityAnswer(forgottenPasswordUsername, answer);
            System.out.println(result.getMessage());
            if (result.isSuccess()) {
                forgottenPasswordUsername = null;  // Reset after successful recovery
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private String getValue(String[] parts, String key) {
        for (String part : parts) {
            if (part.startsWith(key + " ")) {
                String[] values = part.substring(2).trim().split(" ");
                if (values.length == 0) {
                    throw new IllegalArgumentException("Missing value for -" + key + ".");
                }
                return values[0];
            }
        }
        throw new IllegalArgumentException("Missing key: -" + key + ".");
    }

    private String getValue(String[] parts, String key, int occurrence) {
        for (String part : parts) {
            if (part.startsWith(key + " ")) {
                String[] values = part.substring(2).trim().split(" ");
                if (values.length < occurrence) {
                    throw new IllegalArgumentException("Missing value for -" + key + ".");
                }
                return values[occurrence - 1];
            }
        }
        throw new IllegalArgumentException("Missing key: -" + key + ".");
    }
}
