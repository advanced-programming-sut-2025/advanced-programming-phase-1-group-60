package view;

import controller.RegisterController;
import models.Result;
import view.commands.RegisterCommands;

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
               // handlePickQuestion(input);
            } else if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting Register Menu. Goodbye!");
                //خروج از منوی ثبتنام
                break;
            } else {
                System.out.println("Unknown command. Please try again.");
            }
        }
    }

    private void handleRegister(String input) {
        try {
            String[] parts = input.split(" -");
            if (parts.length < 7) {
                System.out.println("Error: Incomplete command. Please provide all required fields.");
                return;
            }

            String username = getValue(parts, "u");
            String password = getValue(parts, "p");
            String passwordConfirm = getValue(parts, "p", 2);
            String nickname = getValue(parts, "n");
            String email = getValue(parts, "e");
            String gender = getValue(parts, "g");

            Result result = registerController.register(username, password, passwordConfirm, nickname, email, gender);
            System.out.println(result.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

//    private void handlePickQuestion(String input) {
//        try {
//            String[] parts = input.split(" -");
//            if (parts.length < 4) {
//                System.out.println("Error: Incomplete command. Please provide all required fields.");
//                return;
//            }
//
//            int questionNumber = Integer.parseInt(getValue(parts, "q"));
//            String answer = getValue(parts, "a");
//            String answerConfirm = getValue(parts, "c");
//
//            Result result = registerController.pickQuestion(questionNumber, answer, answerConfirm);
//            System.out.println(result.getMessage());
//        } catch (NumberFormatException e) {
//            System.out.println("Error: Invalid question number. It must be a number.");
//        } catch (IllegalArgumentException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
//    }

    private String getValue(String[] parts, String key) {
        return getValue(parts, key, 1);
    }

    private String getValue(String[] parts, String key, int occurrence) {
        for (String part : parts) {
            if (part.startsWith(key + " ")) {
                String[] values = part.split(" ", 2);
                if (values.length < occurrence + 1) {
                    throw new IllegalArgumentException("Missing value for -" + key + ".");
                }
                return values[occurrence - 1].trim();
            }
        }
        throw new IllegalArgumentException("Missing key: -" + key + ".");
    }
}