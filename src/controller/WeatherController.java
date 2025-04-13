package controller;

import models.Result;
import models.Tile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WeatherController {
    private Set<String> weathersSet = new HashSet<>(Arrays.asList("sunny", "rain", "snow", "storm"));
    private String currentWeather;

    // Current Weather
    // TODO : Update weather base on 1.season 2.effects 3.forecast
    public boolean updateWeather() {
        if (currentWeather == null) {
            return false;
        }
        return true;
    }

    // Setting Weather
    // TODO : set a specific weather (cheat code)
    public boolean setWeather(String weatherType) {
        if (weatherType == null || !isValidWeatherType(weatherType)) {
            return false;
        }
        return true;
    }

    // Forecast
    // TODO : Get the forecast for the upcoming day.
    public String getForecast(){
        return null;
    }

    // Displaying Weather
    // TODO : Show the current weather so the user is aware of it's effects.
    public String displayWeather() {
        return currentWeather;
    }

    // Validations
    // TODO : check if the weather type we want to set in cheat code is valid
    private boolean isValidWeatherType(String type) {
        return false;
    }

    // TODO : choose 3 tiles and applying thunder, if tile == null -> random select
    public Result thunder(Tile tile) {
        return null;
    }

    // TODO : automatic irrigation of crops
    public Result irrigation () { return null; }
}
