package com.StardewValley.view;

import com.StardewValley.controller.GamePlayController;
import com.StardewValley.controller.TimeController;
import com.StardewValley.models.Game;
import com.StardewValley.models.Tile;
import com.StardewValley.models.User;
import com.StardewValley.models.TimeSystem;
import com.StardewValley.repository.UserRepository;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Scanner;

public class GamePlayView {
    private GamePlayController gamePlayController;
    private TimeController timeController;
    private User currentUser;
    private Game gameInstance;
    private UserRepository userRepository;
    private Scanner dummyScanner;

    public GamePlayView(User user) {
        this.currentUser = user;
        this.gameInstance = Game.getInstance();
        this.userRepository = UserRepository.getInstance();
        this.timeController = new TimeController();
        this.dummyScanner = new Scanner("");

        if (userRepository.getUserByUsername(user.getUsername()) == null) {
            throw new IllegalArgumentException("User not found in repository: " + user.getUsername());
        }

        this.gamePlayController = new GamePlayController(
            user.getFarm(),
            user,
            dummyScanner,
            gameInstance
        );
    }

    public String processTurn() {
        if (!isMyTurn()) {
            return "It's not your turn! Current player: " + gameInstance.getCurrentPlayer().getUsername();
        }

        try {
            // Advance time only
            timeController.advanceTime(1);

            // Only initialize next day if hour reaches 12
            if (TimeSystem.getInstance().getCurrentHour() >= 12) {
                gamePlayController.initializeNextDay();
            }

            // Switch to next player's turn WITHOUT changing current player's position
            gameInstance.nextTurn();

            return "Turn completed. Next player: " + gameInstance.getCurrentPlayer().getUsername();
        } catch (Exception e) {
            return "Error processing turn: " + e.getMessage();
        }
    }

    public String getCurrentPlayerInfo() {
        User currentPlayer = gameInstance.getCurrentPlayer();

        return String.format("Player: %s | Time: %s | Energy: %d/%d | Money: %d",
            currentPlayer.getUsername(),
            timeController.getCurrentDate() + " " + timeController.getCurrentTime(),
            currentPlayer.getEnergy().getCurrentEnergy(),
            currentPlayer.getEnergy().getMaxEnergy(),
            currentPlayer.getMoney()
        );
    }

    public boolean isMyTurn() {
        return gameInstance.getCurrentPlayer().getUsername().equals(currentUser.getUsername());
    }

    public GamePlayController getController() {
        return gamePlayController;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void updateCurrentUser() {
        User newCurrentPlayer = gameInstance.getCurrentPlayer();
        if (!newCurrentPlayer.getUsername().equals(currentUser.getUsername())) {
            this.currentUser = newCurrentPlayer;

            // Create new controller for the new current player
            this.gamePlayController = new GamePlayController(
                newCurrentPlayer.getFarm(),
                newCurrentPlayer,
                dummyScanner,
                gameInstance
            );

            // The player should already be in their correct position from their last turn
            // No need to move them - they stay where they were when their turn ended
        }
    }

    public String getPlayerStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Player Status\n");
        status.append("Username: ").append(currentUser.getUsername()).append("\n");
        status.append("Energy: ").append(currentUser.getEnergy().getCurrentEnergy())
            .append("/").append(currentUser.getEnergy().getMaxEnergy()).append("\n");
        status.append("Money: ").append(currentUser.getMoney()).append("\n");

        if (currentUser.isEnergyPenaltyActive()) {
            status.append("Energy Penalty Active\n");
        }

        /*String unreadMessages = currentUser.getUnreadMessage();
        if (!unreadMessages.isEmpty()) {
            status.append("New Messages:\n").append(unreadMessages);
        }*/

        String marriageRequests = currentUser.getUnreadMarriageRequests();
        if (!marriageRequests.isEmpty()) {
            status.append("Marriage Requests:\n").append(marriageRequests);
        }

        return status.toString();
    }

    public String getAllPlayersInfo() {
        StringBuilder info = new StringBuilder();
        info.append("All Players\n");

        for (User user : userRepository.getAllUsers()) {
            if (user.getCurrentGame() != null && user.getCurrentGame().equals(gameInstance)) {
                info.append(user.getUsername())
                    .append(" - Energy: ").append(user.getEnergy().getCurrentEnergy())
                    .append("/").append(user.getEnergy().getMaxEnergy())
                    .append("\n");
            }
        }

        return info.toString();
    }

    public void dispose() {
        if (dummyScanner != null) {
            dummyScanner.close();
        }
    }
}


