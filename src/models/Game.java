package models;

import java.util.List;
import java.util.Map;

public class Game {
    private User host;
    private Map map;
    private User currentTurn;
    private TimeSystem timeSystem;
    private Weather weather;
    private String season;
    private boolean isActive;

    private List<User> players;

    //+++
    private Map globalMap;
    private Game state;
    private int currentTurnPlayerIndex; //??
    private List<Quest> activeQuests; //??
    private TimeSystem startTime;
    private TimeSystem lastSavedTime;

    public void startNewGame(User host, List<User> invitedPlayers) {  }
    public void saveGame() {  }
    public void loadGame(String gameId) {  }
    public void endGame() {  }


    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public User getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(User currentTurn) {
        this.currentTurn = currentTurn;
    }

    public TimeSystem getTimeSystem() {
        return timeSystem;
    }

    public void setTimeSystem(TimeSystem timeSystem) {
        this.timeSystem = timeSystem;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public Map getGlobalMap() {
        return globalMap;
    }

    public void setGlobalMap(Map globalMap) {
        this.globalMap = globalMap;
    }

    public Game getState() {
        return state;
    }

    public void setState(Game state) {
        this.state = state;
    }

    public int getCurrentTurnPlayerIndex() {
        return currentTurnPlayerIndex;
    }

    public void setCurrentTurnPlayerIndex(int currentTurnPlayerIndex) {
        this.currentTurnPlayerIndex = currentTurnPlayerIndex;
    }

    public List<Quest> getActiveQuests() {
        return activeQuests;
    }

    public void setActiveQuests(List<Quest> activeQuests) {
        this.activeQuests = activeQuests;
    }

    public TimeSystem getStartTime() {
        return startTime;
    }

    public void setStartTime(TimeSystem startTime) {
        this.startTime = startTime;
    }

    public TimeSystem getLastSavedTime() {
        return lastSavedTime;
    }

    public void setLastSavedTime(TimeSystem lastSavedTime) {
        this.lastSavedTime = lastSavedTime;
    }

    public void addPlayer(User player) { }
    public void removePlayer(User player) { }


    public void nextTurn() { }
    public User getCurrentTurnPlayer() { }
    // public void updatePlayerPosition(User player, Point newPosition) { }


}
