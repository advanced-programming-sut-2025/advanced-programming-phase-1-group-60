package view;

import controller.RegisterController;
import models.Result;
import view.commands.RegisterCommands;

import java.util.Map;
import java.util.Scanner;

public class RegisterView extends View {
    private final RegisterController registerController;
    private final Scanner scanner;

    public RegisterView(RegisterController registerController, Scanner scanner) {
        this.registerController = registerController;
        this.scanner = scanner;
    }

    public void display() {
        System.out.println("=== Welcome to Stardew Valley Register Menu ===");
//        System.out.println("Available commands:");
//        System.out.println("- register -u <username> -p <password> <password_confirm> -n <nickname> -e <email> -g <gender>");
//        System.out.println("- pick question -q <question_number> -a <answer> -c <answer_confirm>");
        System.out.println("- exit");

        while (true) {
           // System.out.print("> ");
            String input = scanner.nextLine().trim();
            RegisterCommands command = RegisterCommands.getCommand(input);

            if (command == null) {
                System.out.println("Invalid command. Please try again.");
                continue;
            }

            if (command == RegisterCommands.REGISTER) {
                handleRegister(input);
            } else if (command == RegisterCommands.PICK_QUESTION) {
               handlePickQuestion(input);
            } else if (command == RegisterCommands.EXIT) {  // Remove the extra parenthesis
                System.out.println("Exiting Register Menu. Goodbye!");
                break;
            } else {
                System.out.println("Unknown command. Please try again.");
            }
        }
    }

    private void handleRegister(String input) {
        try {
            String[] parts = input.split(" -");

            String username = getValue(parts, "u");
            String password = getValue(parts, "p");
            String passwordConfirm = getValue(parts, "p", 2);
            String nickname = getValue(parts, "n");
            String email = getValue(parts, "e");
            String gender = getValue(parts, "g");

            Result result = registerController.register(username, password, passwordConfirm, nickname, email, gender);
            System.out.println(result.getMessage());
            if (result.isSuccess()) {
                displaySecurityQuestions();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private void displaySecurityQuestions() {
        System.out.println("\nPlease select one of the following security questions:");
        for (Map.Entry<Integer, String> entry : registerController.getSecurityQuestions().entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
        System.out.println("\nUse command: pick question -q <number> -a <answer> -c <confirm>");
    }
    private void handlePickQuestion(String input) {
        try {
            String[] parts = input.split(" -");

            int questionNumber = Integer.parseInt(getValue(parts, "q"));
            String answer = getValue(parts, "a");
            String confirmAnswer = getValue(parts, "c");

            Result result = registerController.pickQuestion(questionNumber, answer, confirmAnswer);
            System.out.println(result.getMessage());

            if (result.isSuccess()) {
                System.out.println("You can now proceed to login.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid question number. Please choose 1, 2, or 3.");
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