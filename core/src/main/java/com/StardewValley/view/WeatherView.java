package com.StardewValley.view;

import com.StardewValley.controller.WeatherController;
import com.StardewValley.view.commands.WeatherCommands;

import java.util.Scanner;

public class WeatherView {
    private final Scanner scanner;
    private final WeatherController weatherController;

    public WeatherView(Scanner scanner, WeatherController weatherController) {
        this.scanner = scanner;
        this.weatherController = weatherController;
    }

    public void display() {
        System.out.println("=== Weather System ===");
//        System.out.println("Available commands:");
//        for (WeatherCommands command : WeatherCommands.values()) {
//            System.out.println("- " + command.getCommandPrefix());
//        }
//        System.out.println("- cheat weather set <Type>: Set specific weather (cheat)");
//        System.out.println("- exit: Exit weather system");

        while (true) {
           // System.out.print("> ");
            String input = scanner.nextLine().trim();

            WeatherCommands command = getCommand(input);
            if (command != null) {
                System.out.println(weatherController.handleCommand(command));
            } else if (input.startsWith("cheat weather set")) {
                handleCheatSetWeather(input);
            } else if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting Weather System...");
                break;
            } else {
                System.out.println("Invalid command. Please try again.");
            }
        }
    }

    private WeatherCommands getCommand(String input) {
        for (WeatherCommands command : WeatherCommands.values()) {
            if (input.equalsIgnoreCase(command.getCommandPrefix())) {
                return command;
            }
        }
        return null;
    }

    private void handleCheatSetWeather(String input) {
        try {
            String[] parts = input.split(" ");
            String weatherType = parts[3];
            if (weatherController.setWeather(weatherType)) {
                System.out.println("Weather set to: " + weatherType);
            } else {
                System.out.println("Invalid weather type. Valid types are: sunny, rain, snow, storm.");
            }
        } catch (Exception e) {
            System.out.println("Invalid format. Use: cheat weather set <Type>");
        }
    }
}
