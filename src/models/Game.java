package models;

import java.util.Map;

public class Game {
    private User owner;
    private Map map;
    private User currentTurn;
    private TimeSystem timeSystem;
    private Weather weather;
    private Season season;
    private boolean isActive;

    private List<User> users;
}
