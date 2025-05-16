package models;

import exceptions.GameException;
import repository.UserRepository;

import java.util.*;

public class Game {
    private static Game instance;
    private List<User> players;
    private User creator;
    private Map<User, Integer> selectedMaps = new HashMap<>();
    private GameState state = GameState.LOBBY;
    private int currentPlayerIndex = 0;
    private GameMap currentMap;
    private boolean forceTerminateVote = false;
    private Set<User> terminationVotes = new HashSet<>();

    private Game() {}

    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public enum GameState {
        LOBBY, MAP_SELECTION, IN_GAME, TERMINATED
    }

    public static Game getCurrentGame() {
        return instance;
    }
    // ایجاد بازی جدید
    public void newGame(User creator, List<String> usernames) throws GameException {
        validateNewGame(creator, usernames);

        this.creator = creator;
        this.players = new ArrayList<>();
        this.players.add(creator);

        for (String username : usernames) {
            User user = UserRepository.getInstance().getUserByUsername(username);
            if (user == null) throw new GameException("Invalid username: " + username);
            if (user.getCurrentGame() != null) throw new GameException("User already in game: " + username);
            this.players.add(user);
        }

        this.state = GameState.MAP_SELECTION;
    }

    private void validateNewGame(User creator, List<String> usernames) throws GameException {
        if (usernames.size() < 1 || usernames.size() > 3) {
            throw new GameException("Invalid number of players (1-3 required)");
        }
        if (creator.getCurrentGame() != null) {
            throw new GameException("Creator is already in a game");
        }
    }

    // انتخاب نقشه
    public void selectMap(User user, int mapNumber) throws GameException {
        if (state != GameState.MAP_SELECTION) {
            throw new GameException("Map selection not allowed in current state");
        }
        if (mapNumber < 1 || mapNumber > 4) {
            throw new GameException("Invalid map number (1-4)");
        }
        selectedMaps.put(user, mapNumber);

        if (selectedMaps.size() == players.size()) {
            initializeGameMap();
            startGame();
        }
    }

    private void initializeGameMap() {
        FarmManager farmManager = new FarmManager();
        List<Farm> farms = farmManager.getAllFarms();

        for (User player : players) {
            int mapId = selectedMaps.get(player);
            Farm farm = farms.get(mapId - 1);
            farm.setOwner(player);
            player.setFarm(farm);
        }

        this.currentMap = new GameMap(farms, VillageTemplate.createDefaultVillage());
    }

    // شروع بازی
    private void startGame() {
        this.state = GameState.IN_GAME;
        currentPlayerIndex = 0;
        notifyPlayers("Game started! First turn: " + getCurrentPlayer().getUsername());
    }

    // مدیریت نوبت‌ها
    public Integer nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return currentPlayerIndex;
    }

    public User getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // سیستم رای‌گیری حذف
    public void startTerminationVote(User initiator) {
        if (state != GameState.IN_GAME) return;
        forceTerminateVote = true;
        terminationVotes.clear();
        voteTermination(initiator);
    }

    public void voteTermination(User user) {
        if (!forceTerminateVote) return;
        terminationVotes.add(user);

        if (terminationVotes.size() == players.size()) {
            terminateGame();
        }
    }

    public void terminateGame() {
        state = GameState.TERMINATED;
        players.forEach(user -> user.setCurrentGame(null));
        notifyPlayers("Game terminated by vote");
    }

    private void notifyPlayers(String message) {
        players.forEach(player ->
                System.out.println("[System] " + player.getUsername() + ": " + message));
    }

    public GameState getState () {
        return state;
    }

    public User getCreator() {
        return creator;
    }

    public List<User> getPlayers() {
        return players;
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public Map<User, Integer> getSelectedMaps() {
        return selectedMaps;
    }
}