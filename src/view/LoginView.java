package view;

import controller.LoginMenuController;
import models.Result;
import view.commands.LoginCommands;

import java.util.Scanner;

public class LoginView extends View {
    private final LoginMenuController loginMenuController;
    private final Scanner scanner;

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

        for (int i = 1; i < parts.length; i++) {
            if (parts[i].equals("-u") && i + 1 < parts.length) {
                username = parts[i + 1];
            } else if (parts[i].equals("-p") && i + 1 < parts.length) {
                password = parts[i + 1];
            }
        }

        if (username == null || password == null) {
            System.out.println("Missing username or password.");
            return;
        }

        Result result = loginMenuController.Login(username, password, false);

        if (result.isSuccess()) {
            System.out.println("Login successful! Welcome, " + username + "!");
            // بعدا میفرستیمش به Main Menu
        } else {
            System.out.println(result.getMessage());
        }

        System.out.println("Invalid login command format.");
    }

    private void handleForgetPassword(String input) {
        String[] parts = input.split("\\s+");
        String username = null;

        for (int i = 2; i < parts.length; i++) {
            if (parts[i].equals("-u") && i + 1 < parts.length) {
                username = parts[i + 1];
            }
        }

        if (username == null) {
            System.out.println("Missing username.");
            return;
        }

     //   String result = loginMenuController.forgetPassword(username, question);
      //  System.out.println(result);

        //برای سوال امنیتی  فراموش کردن رمز

        System.out.println("Invalid forget password command format.");
    }

        private void handleAnswerQuestion (String input){
            System.out.println("Answering security question... (Functionality to be expanded)");
            // این قسمت بعداً کامل‌تر میشه
        }
    }
