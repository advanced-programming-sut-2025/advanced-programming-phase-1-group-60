package com.StardewValley.view.commands;

public enum WeatherCommands {
    SHOW_SEASON("season"),
    SHOW_WEATHER("weather"),
    PREDICT_WEATHER("weather forecast");

    private final String commandPrefix;

    WeatherCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
