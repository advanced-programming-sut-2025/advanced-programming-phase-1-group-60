package com.StardewValley.models;

import com.StardewValley.exceptions.GameException;
import com.StardewValley.repository.UserRepository;

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
    // New: Map to store kick votes
    private final Map<User, Set<User>> kickVotes = new HashMap<>();


    private Game() {}

    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
    public static Game resetInstance() {
        instance = new Game();
        User.resetChatHistories();
        return instance;
    }
    public void setCurrentMap(GameMap map) {
        this.currentMap = map;
    }

    public void setState(GameState state) {
        this.state = state;
    }
    public enum GameState {
        LOBBY, MAP_SELECTION, IN_GAME, TERMINATED
    }
    public int getMapSelection(User player) {
        // Return the map ID (1-4) selected by this player
        Integer mapId = selectedMaps.get(player);
        return mapId != null ? mapId : 0;
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
        // Initialize kickVotes for all players
        for (User player : this.players) {
            kickVotes.put(player, new HashSet<>());
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

    public void initializeGameMap() {
        // Create farms with proper templates directly
        List<Farm> farms = new ArrayList<>();

        // Create all 4 farms with their templates
        farms.add(new Farm(FarmTemplate.template1(), 25, 25)); // Farm 1
        farms.add(new Farm(FarmTemplate.template2(), 25, 25)); // Farm 2
        farms.add(new Farm(FarmTemplate.template3(), 25, 25)); // Farm 3
        farms.add(new Farm(FarmTemplate.template4(), 25, 25)); // Farm 4

        // Assign farms to players based on their map selection
        for (User player : players) {
            int mapId = selectedMaps.get(player);
            if (mapId > 0 && mapId <= farms.size()) {
                Farm farm = farms.get(mapId - 1);
                farm.setOwner(player);
                player.setFarm(farm);
            }
        }

        // Create the GameMap with the farms and village template
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
        if (players.isEmpty()) {
            terminateGame();
            return -1; // Or handle appropriately
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return currentPlayerIndex;
    }

    public User getCurrentPlayer() {
        if (players.isEmpty() || currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
            return null;
        }
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

    /**
     * New method to remove a player from the game.
     * It adjusts the currentPlayerIndex if the removed player was before the current one.
     * If no players are left, the game is terminated.
     */
    public void removePlayer(User playerToRemove) {
        if (playerToRemove == null || !players.contains(playerToRemove)) {
            return; // Player not in the game
        }

        int removedPlayerIndex = players.indexOf(playerToRemove);
        players.remove(playerToRemove);
        kickVotes.remove(playerToRemove); // Also remove from kick tracking

        if (players.isEmpty()) {
            terminateGame();
            return;
        }

        // Adjust current player index
        if (removedPlayerIndex < currentPlayerIndex) {
            currentPlayerIndex--;
        } else if (removedPlayerIndex == currentPlayerIndex) {
            // If the current player is removed, advance to the next player's turn
            // The index is now pointing to the next player, so no change needed unless it was the last player
            if (currentPlayerIndex >= players.size()) {
                currentPlayerIndex = 0;
            }
        }
        notifyPlayers(playerToRemove.getUsername() + " has left the game.");
    }
    /**
     * New method to handle voting to kick a player.
     * @param voter The player who is voting.
     * @param target The player being voted against.
     * @return A message indicating the result of the vote.
     */
    public String voteToKick(User voter, User target) {
        if (state != GameState.IN_GAME) {
            return "Voting is only allowed during the game.";
        }
        if (voter.equals(target)) {
            return "You cannot vote to kick yourself.";
        }
        if (!players.contains(target)) {
            return "Target player is not in this game.";
        }

        Set<User> votesForTarget = kickVotes.get(target);
        if (votesForTarget.contains(voter)) {
            return "You have already voted to kick this player.";
        }

        votesForTarget.add(voter);
        int requiredVotes = (players.size() / 2) + 1;

        if (votesForTarget.size() >= requiredVotes) {
            removePlayer(target);
            return target.getUsername() + " has been kicked from the game.";
        } else {
            return "Vote to kick " + target.getUsername() + " has been registered. " +
                (requiredVotes - votesForTarget.size()) + " more votes needed.";
        }
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
