package controller;

import models.Result;
import models.Weather;

public class WeatherController {
    private Weather currentWeather;

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
    public String getForecast() {
        return currentWeather.getForecast();
    }

    // Displaying Weather
    // TODO : Show the current weather so the user is aware of it's effects.
    public String displayWeather() {
        return currentWeather.getType();
    }

    // Validations
    // TODO : check if the weather type we want to set in cheat code is valid
    private boolean isValidWeatherType(String type) {
        String[] validTypes = {"Sunny", "Rain", "Storm", "Snow"};
        for (String valid : validTypes) {
            if (valid.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
