package view;

import controller.MainController;
import controller.MenuController;
import models.Result;
import view.commands.MainCommands;

import java.util.Scanner;

public class MainView {
    private final Scanner scanner;
    private final MainController mainController;
    private final MenuController menuController;

    public MainView(MainController mainController, MenuController menuController, Scanner scanner) {
        this.mainController = mainController;
        this.menuController = menuController;
        this.scanner = scanner;
    }

    public void display() {
        System.out.println("=== Welcome to Stardew Valley Main Menu ===");
        System.out.println("Available commands:");
        System.out.println("- user logout");
        System.out.println("- avatar");
        System.out.println("- profile");
        System.out.println("- game");
        System.out.println("- exit");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            MainCommands command = getCommand(input);

            if (command == null) {
                System.out.println("Invalid command. Please try again.");
                continue;
            }

            switch (command) {
                case USER_LOGOUT:
                    handleLogout();
                    break;

                case GO_TO_AVATAR:
                    goToAvatar();
                    break;

                case GO_TO_PROFILE:
                    goToProfile();
                    break;

                case GO_TO_GAME:
                    goToGame();
                    break;

                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private MainCommands getCommand(String input) {
        for (MainCommands command : MainCommands.values()) {
            if (input.equalsIgnoreCase(command.getCommandPrefix())) {
                return command;
            }
        }
        return null;
    }

    private void handleLogout() {
        Result result = mainController.logout();
        System.out.println(result.getMessage());
        if (result.isSuccess()) {
            menuController.exitMenu();
            System.out.println("Redirecting to login/register menu...");
        }

    }

    private void goToProfile() {
        Result result = mainController.goToProfile();
        System.out.println(result.getMessage());
        if (result.isSuccess()) {
            menuController.enterMenu("profile");
        }
    }

    private void goToGame() {
        Result result = mainController.goToGame();
        System.out.println(result.getMessage());
        if (result.isSuccess()) {
            menuController.enterMenu("game");
        }
    }

    private void goToAvatar() {
        System.out.println("Avatar menu is not implemented yet.");
    }
}