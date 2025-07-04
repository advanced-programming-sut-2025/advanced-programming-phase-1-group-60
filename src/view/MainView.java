package view;

import controller.GameController;
import controller.MainController;
import controller.ProfileController;
import models.Result;
import repository.UserRepository;
import view.commands.MainCommands;

import java.util.Scanner;

public class MainView {
    private final Scanner scanner;
    private final MainController mainController;
    private boolean running = true;
    public MainView(MainController mainController, Scanner scanner) {
        this.mainController = mainController;
        this.scanner = scanner;
    }

    public void display() {
        System.out.println("=== Welcome to Stardew Valley Main Menu ===");
        System.out.println("Available commands:");
        System.out.println("- user logout");
        System.out.println("- profile");
        System.out.println("- game");
        System.out.println("- exit");

        while (running) {
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
            System.out.println("Redirecting to login/register menu...");
            breakLoop();
        }
    }

    private void goToProfile() {
        Result result = mainController.goToProfile();
        if (result.isSuccess()) {
            ProfileController profileController = new ProfileController(mainController.getLoggedInUser());
            ProfileView profileView = new ProfileView(profileController, scanner);
            profileView.display();
        } else {
            System.out.println(result.getMessage());
        }
    }

    private void goToGame() {
        Result result = mainController.goToGame();
        if (result.isSuccess()) {
            GameController gameController = new GameController(mainController.getLoggedInUser(), scanner);
            gameController.displayGame();
        } else {
            System.out.println(result.getMessage());
        }
    }

    private void goToAvatar() {
        System.out.println("Avatar menu is not implemented yet.");
    }

    // Helper to break the display loop after logout
    private void breakLoop() {
        running = false;
    }
}