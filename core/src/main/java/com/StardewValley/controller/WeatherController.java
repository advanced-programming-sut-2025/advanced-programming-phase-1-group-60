package com.StardewValley.controller;

import com.StardewValley.models.Result;
import com.StardewValley.models.Tile;
import com.StardewValley.view.commands.WeatherCommands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WeatherController {
    private static WeatherController instance;
    private final Set<String> weathersSet = new HashSet<>(Arrays.asList("sunny", "rain", "snow", "storm"));
    private String currentWeather = "sunny";
    public String forecastWeather;

    public static WeatherController getInstance() {
        if (instance == null) {
            instance = new WeatherController();
        }
        return instance;
    }

    public String handleCommand(WeatherCommands command) {
        switch (command) {
            case SHOW_WEATHER:
                return displayWeather();
            case PREDICT_WEATHER:
                return "Weather forecast for tomorrow: " + getForecast();
            default:
                return "Unknown command.";
        }
    }

    public boolean updateWeather() {
        if (currentWeather == null) {
            currentWeather = "sunny";
        }
        Random random = new Random();
        String[] weatherOptions = weathersSet.toArray(new String[0]);
        currentWeather = weatherOptions[random.nextInt(weatherOptions.length)];
        return true;
    }

    public boolean setWeather(String weatherType) {
        if (weatherType == null || !isValidWeatherType(weatherType)) {
            return false;
        }
        currentWeather = weatherType;
        return true;
    }

    public String getForecast() {
        Random random = new Random();
        String[] weatherOptions = weathersSet.toArray(new String[0]);
        forecastWeather = weatherOptions[random.nextInt(weatherOptions.length)];
        return forecastWeather;
    }


    public String displayWeather() {
        return currentWeather != null ? currentWeather : "Weather not set.";
    }


    private boolean isValidWeatherType(String type) {
        return weathersSet.contains(type.toLowerCase());
    }


    public Result thunder(Tile tile) {
        Random random = new Random();
        return new Result(true,"Thunder applied to tile: " + (tile != null ? tile.toString() : "random tile"));
    }

    public Result irrigation() {
        return new Result(true,"Crops irrigated successfully.");
    }

    public String getCurrentWeather() { return currentWeather; }
}
