package view;

import controller.TimeController;
import view.commands.TimeCommands;

import java.util.Scanner;

public class TimeView {
    private final Scanner scanner;
    private final TimeController timeController;

    public TimeView(Scanner scanner, TimeController timeController) {
        this.scanner = scanner;
        this.timeController = timeController;
    }

    public void display() {
        System.out.println("=== Time and Date System ===");
//        System.out.println("Available commands:");
//        for (TimeCommands command : TimeCommands.values()) {
//            System.out.println("- " + command.getCommandPrefix());
//        }
//        System.out.println("- cheat advance time <X>h: Advance time by X hours");
//        System.out.println("- cheat advance date <X>d: Advance date by X days");
//        System.out.println("- exit: Exit time system");

        while (true) {
            //System.out.print("> ");
            String input = scanner.nextLine().trim();

            TimeCommands command = getCommand(input);
            if (command != null) {
                executeCommand(command);
            } else if (input.startsWith("cheat advance time")) {
                handleCheatAdvanceTime(input);
            } else if (input.startsWith("cheat advance date")) {
                handleCheatAdvanceDate(input);
            } else if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting Time System...");
                break;
            } else {
                System.out.println("Invalid command. Please try again.");
            }
        }
    }

    private TimeCommands getCommand(String input) {
        for (TimeCommands command : TimeCommands.values()) {
            if (input.equalsIgnoreCase(command.getCommandPrefix())) {
                return command;
            }
        }
        return null;
    }

    private void executeCommand(TimeCommands command) {
        switch (command) {
            case SHOW_TIME:
                System.out.println("Current time: " + timeController.getCurrentTime());
                break;
            case SHOW_DATE:
                System.out.println("Current date: " + timeController.getCurrentDate());
                break;
            case SHOW_DATE_AND_TIME:
                System.out.println("Current date and time: " + timeController.getCurrentDate() + ", " + timeController.getCurrentTime());
                break;
            case SHOW_DAY_OF_THE_WEEK:
                System.out.println("Today is: " + timeController.getDayOfWeek());
                break;
            case SHOW_SEASON:
                System.out.println("Current season: " + timeController.getCurrentSeason());
                break;
            default:
                System.out.println("Unknown command.");
        }
    }

    private void handleCheatAdvanceTime(String input) {
        try {
            String[] parts = input.split(" ");
            int hours = Integer.parseInt(parts[3].replace("h", ""));
            System.out.println(timeController.advanceTime(hours));
        } catch (Exception e) {
            System.out.println("Invalid format. Use: cheat advance time <X>h");
        }
    }

    private void handleCheatAdvanceDate(String input) {
        try {
            String[] parts = input.split(" ");
            int days = Integer.parseInt(parts[3].replace("d", ""));
            System.out.println(timeController.advanceDate(days));
        } catch (Exception e) {
            System.out.println("Invalid format. Use: cheat advance date <X>d");
        }
    }
}