package controller;

import exceptions.GameException;
import models.Game;
import models.Tools;
import models.User;

import java.util.*;

public class GameController {
    private final Game game = Game.getInstance();

    Scanner scanner;
    User Cuser;
    private Map<Integer, User> indexes = new HashMap<>();
    private Map<User, GamePlayController> controllers = new HashMap<>();

    public GameController(User user, Scanner scanner) {
        this.scanner = scanner;
        Cuser = user;
    }

    public void displayGame() {
        while (true) {
            String input = scanner.nextLine();
            handleCommand(Cuser, input);
        }
    }

    public void handleCommand(User user, String command) {
        try {
            String[] parts = command.split(" ");

            if (parts[0].equalsIgnoreCase("game")) {
                if (parts[1].equalsIgnoreCase("new")) {
                    handleNewGame(user, Arrays.copyOfRange(parts, 2, parts.length));
                }
                else if (parts[1].equalsIgnoreCase("map")) {
                    handleMapSelection(user, Integer.parseInt(parts[2]));
                }
            }
            else if (command.equalsIgnoreCase("exit game")) {
                handleExitGame(user);
            }
        } catch (GameException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleNewGame(User creator, String[] args) throws GameException {
        List<String> players = new ArrayList<>();
        boolean userFlag = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-u")) {
                userFlag = true;
            } else if (userFlag) {
                players.add(arg);
            }
        }
        game.newGame(creator, players);
        for (User user : game.getPlayers()) {
            Tools.addBeginnerHoeToInventory(user.getInventory());
            Tools.addBeginnerPickaxeToInventory(user.getInventory());
            Tools.addBeginnerAxeToInventory(user.getInventory());
            Tools.addBeginnerWateringcanToInventory(user.getInventory());
            //Tools.addLearningFishingpoleToInventory(user.getInventory());
            Tools.addBeginnerScytheToInventory(user.getInventory());
            //Tools.addBeginnerMilkPailToInventory(user.getInventory());
            //Tools.addBeginnerShearToInventory(user.getInventory());
        }
        if (game.getState() == Game.GameState.MAP_SELECTION) {
            for (User user : game.getPlayers()) {
                String input = scanner.nextLine();
                String[] parts = input.split(" ");
                handleMapSelection(user, Integer.parseInt(parts[2]));
            }
        }
        if (game.getState() == Game.GameState.IN_GAME) {
            handleInGame();
        }
    }

    private void handleMapSelection(User user, int mapNumber) throws GameException {
        game.selectMap(user, mapNumber);
    }

    private void handleExitGame(User user) throws GameException {
        if (game.getCreator().equals(user) && game.getState() == Game.GameState.IN_GAME) {
            game.terminateGame();
        } else {
            throw new GameException("Only creator can exit game during play");
        }
    }

    private void handleInGame () {
        int index = 0;
        for (User user : game.getPlayers()) {
            controllers.put(user, new GamePlayController(user.getFarm(), user, scanner, game));
            indexes.put(index, user);
            index++;
        }
        int currentTurn = 0;
        while (true) {
            GamePlayController controller = controllers.get(indexes.get(currentTurn));
            controller.getAndProcessInput();
            currentTurn = game.nextTurn();
        }
    }
}