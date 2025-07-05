package com.StardewValley;

import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.controller.RegisterController;
import com.StardewValley.view.LoginView;
import com.StardewValley.view.RegisterView;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LoginMenuController loginMenuController = new LoginMenuController(scanner);
        RegisterController registerController = new RegisterController();

        while (true) {
            System.out.println("\n=== Welcome to Stardew Valley ===");
            System.out.println("1. Login Menu");
            System.out.println("2. Register Menu");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n=== Login Menu ===");
                    System.out.println("Available commands:");
                    System.out.println("- login -u <username> -p <password>");
                    System.out.println("- forget password -u <username>");
                    System.out.println("- exit");
                    LoginView loginView = new LoginView(loginMenuController, scanner);
                    loginView.display();
                    break;

                case "2":
                    System.out.println("\n=== Register Menu ===");
                    System.out.println("Available commands:");
                    System.out.println("- register -u <username> -p <password> <password_confirm> -n <nickname> -e <email> -g <gender>");
                    System.out.println("- pick question -q <question_number> -a <answer> -c <answer_confirm>");
                    System.out.println("- exit");
                    RegisterView registerView = new RegisterView(registerController, scanner);
                    registerView.display();
                    break;

                case "3":
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
