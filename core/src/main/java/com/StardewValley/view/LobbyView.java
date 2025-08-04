package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.Network.GameStateManager;
import com.StardewValley.Network.LobbyManager;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.models.Lobby;
import com.StardewValley.models.User;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class LobbyView implements Screen {
    private final Game game;
    private final LobbyController lobbyController;
    private final Stage stage;
    private final MenuManager menuManager;
    private final SpriteBatch batch;
    private Table lobbyPlayersTable;

    public LobbyView(Game game, LobbyController lobbyController) {
        this.game = game;
        this.lobbyController = lobbyController;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();

        createUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);

        // Title
        Label titleLabel = new Label("Lobby", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        // Button colors (same as MainView)
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        // Centered vertical buttons
        Table buttonTable = new Table(menuManager.getPixthulhuSkin());

        TextButton createLobbyBtn = new TextButton("Create Lobby", menuManager.getPixthulhuSkin());
        TextButton joinLobbyBtn = new TextButton("Join Lobby", menuManager.getPixthulhuSkin());
        TextButton onlinePlayersBtn = new TextButton("Online Players", menuManager.getPixthulhuSkin());
        TextButton backBtn = new TextButton("Back", menuManager.getPixthulhuSkin());

        // Apply color styling
        for (TextButton btn : new TextButton[]{createLobbyBtn, joinLobbyBtn, onlinePlayersBtn, backBtn}) {
            btn.setColor(buttonColor);
            btn.getLabel().setColor(textColor);
        }

        buttonTable.add(createLobbyBtn).width(350).height(90).padBottom(20).row();
        buttonTable.add(joinLobbyBtn).width(350).height(90).padBottom(20).row();
        buttonTable.add(onlinePlayersBtn).width(350).height(90).padBottom(20).row();
        buttonTable.add(backBtn).width(350).height(90).row();

        // Button listeners
        createLobbyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showCreateLobbyDialog(buttonColor, textColor);
            }
        });
        joinLobbyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showJoinLobbyDialog(buttonColor, textColor);
            }
        });
        onlinePlayersBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showOnlinePlayersDialog(buttonColor, textColor);
            }
        });
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lobbyController.onBack();
            }
        });

        root.add(titleLabel).padBottom(40).row();
        root.add(buttonTable).center().expand();

        stage.addActor(root);
    }
    private void showCreateLobbyDialog(Color buttonColor, Color textColor) {
        Dialog dialog = new Dialog("Create Lobby", menuManager.getPixthulhuSkin());
        Table content = new Table(menuManager.getPixthulhuSkin());

        TextField nameField = new TextField("", menuManager.getPixthulhuSkin());
        CheckBox publicBox = new CheckBox("Public", menuManager.getPixthulhuSkin());
        CheckBox visibleBox = new CheckBox("Visible", menuManager.getPixthulhuSkin());
        TextField passwordField = new TextField("", menuManager.getPixthulhuSkin());
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setDisabled(true);

        publicBox.setChecked(true);
        visibleBox.setChecked(true);

        publicBox.addListener(event -> {
            passwordField.setDisabled(publicBox.isChecked());
            return false;
        });

        content.add("Lobby Name:").left();
        content.add(nameField).width(200).row();
        content.add(publicBox).colspan(2).left().padTop(10).row();
        content.add("Password:").left();
        content.add(passwordField).width(200).row();
        content.add(visibleBox).colspan(2).left().padTop(10).row();

        dialog.getContentTable().add(content).pad(20);

        TextButton createBtn = new TextButton("Create", menuManager.getPixthulhuSkin());
        createBtn.setColor(buttonColor);
        createBtn.getLabel().setColor(textColor);

        createBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String name = nameField.getText();
                boolean isPublic = publicBox.isChecked();
                String password = isPublic ? null : passwordField.getText();
                boolean isVisible = visibleBox.isChecked();
                lobbyController.createLobby(name, isPublic, password, isVisible);
                dialog.hide();
            }
        });

        TextButton cancelBtn = new TextButton("Cancel", menuManager.getPixthulhuSkin());
        cancelBtn.setColor(buttonColor);
        cancelBtn.getLabel().setColor(textColor);
        cancelBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });

        dialog.getButtonTable().add(createBtn).width(150).height(70).pad(10);
        dialog.getButtonTable().add(cancelBtn).width(150).height(70).pad(10);

        dialog.show(stage);
    }

    private void showOnlinePlayersDialog(Color buttonColor, Color textColor) {
        List<String> onlinePlayers = lobbyController.getOnlinePlayers();
        Dialog dialog = new Dialog("Online Players", menuManager.getPixthulhuSkin()) {
            protected void result(Object object) {
                this.hide();
            }
        };
        Table content = new Table(menuManager.getPixthulhuSkin());
        if (onlinePlayers.isEmpty()) {
            content.add(new Label("No players online.", menuManager.getPixthulhuSkin())).pad(10);
        } else {
            for (String player : onlinePlayers) {
                content.add(new Label(player, menuManager.getPixthulhuSkin())).pad(5).row();
            }
        }
        dialog.getContentTable().add(content).pad(20);

        TextButton backBtn = new TextButton("Back", menuManager.getPixthulhuSkin());
        backBtn.setColor(buttonColor);
        backBtn.getLabel().setColor(textColor);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        dialog.getButtonTable().add(backBtn).width(200).height(90).padTop(20);

        dialog.show(stage);
    }
    private void showJoinLobbyDialog(Color buttonColor, Color textColor) {
        List<Lobby> lobbies = lobbyController.getLobbies();
        String currentUser = lobbyController.getCurrentUsername();

        Dialog dialog = new Dialog("Join Lobby", menuManager.getPixthulhuSkin());
        Table content = new Table(menuManager.getPixthulhuSkin());

        boolean hasVisible = false;
        for (Lobby lobby : lobbies) {
            if (!lobby.isVisible()) continue;
            hasVisible = true;
            StringBuilder info = new StringBuilder();
            info.append("Name: ").append(lobby.getName())
                .append(" | Players: ").append(lobby.getMembers().size())
                .append("\nMembers: ").append(String.join(", ", lobby.getMembers()));
            Label lobbyLabel = new Label(info.toString(), menuManager.getPixthulhuSkin());
            TextButton joinBtn = new TextButton("Join", menuManager.getPixthulhuSkin());
            joinBtn.setColor(buttonColor);
            joinBtn.getLabel().setColor(textColor);

            joinBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!lobby.isPublic()) {
                        showPasswordDialog(lobby, currentUser, buttonColor, textColor, dialog);
                    } else {
                        lobbyController.joinLobby(lobby, null, currentUser);
                        dialog.hide();
                        showJoinedLobbyDialog(lobby, currentUser);
                    }
                }
            });

            content.add(lobbyLabel).left().pad(10);
            content.add(joinBtn).width(120).height(60).pad(10).row();
        }

        if (!hasVisible) {
            content.add(new Label("No visible lobbies available.", menuManager.getPixthulhuSkin())).pad(20).row();
        }

        // Option to join by code (for invisible lobbies)
        TextButton joinByCodeBtn = new TextButton("Join by Code", menuManager.getPixthulhuSkin());
        joinByCodeBtn.setColor(buttonColor);
        joinByCodeBtn.getLabel().setColor(textColor);
        joinByCodeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showJoinByCodeDialog(buttonColor, textColor, dialog);
            }
        });
        content.add(joinByCodeBtn).colspan(2).width(250).height(60).padTop(20).row();

        dialog.getContentTable().add(content).pad(20);

        // Refresh button
        TextButton refreshBtn = new TextButton("Refresh", menuManager.getPixthulhuSkin());
        refreshBtn.setColor(buttonColor);
        refreshBtn.getLabel().setColor(textColor);
        refreshBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
                showJoinLobbyDialog(buttonColor, textColor);
            }
        });
        dialog.getButtonTable().add(refreshBtn).width(200).height(60).padTop(20);

        // Back button
        TextButton backBtn = new TextButton("Back", menuManager.getPixthulhuSkin());
        backBtn.setColor(buttonColor);
        backBtn.getLabel().setColor(textColor);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        dialog.getButtonTable().add(backBtn).width(200).height(60).padTop(20);

        dialog.show(stage);
    }
    private void showJoinedLobbyDialog(Lobby lobby, String username) {
        Dialog joinedDialog = new Dialog("Lobby Joined", menuManager.getPixthulhuSkin()) {
            @Override
            protected void result(Object object) {
                if ("LEAVE".equals(object)) {
                    lobbyController.leaveLobby(lobby, username);
                    this.hide();
                } else if ("REFRESH".equals(object)) {
                    checkAndJoinStartedGame(lobby, username);
                }
            }
        };

        joinedDialog.text("You have joined the Lobby of \"" + lobby.getName() + "\"\n\nWaiting for the host to start the game.\nClick Refresh to check if the game has started.");

        // Style buttons
        TextButton refreshBtn = new TextButton("Refresh", menuManager.getPixthulhuSkin());
        refreshBtn.setColor(new Color(0.38f, 0.55f, 0.27f, 1f));
        refreshBtn.getLabel().setColor(new Color(0.95f, 0.92f, 0.82f, 1f));

        TextButton leaveBtn = new TextButton("Leave", menuManager.getPixthulhuSkin());
        leaveBtn.setColor(new Color(0.38f, 0.55f, 0.27f, 1f));
        leaveBtn.getLabel().setColor(new Color(0.95f, 0.92f, 0.82f, 1f));

        joinedDialog.button(refreshBtn, "REFRESH");
        joinedDialog.button(leaveBtn, "LEAVE");
        joinedDialog.show(stage);
    }
    private void checkAndJoinStartedGame(Lobby lobby, String username) {
        // Check if a farm has been assigned to this user in the GameStateManager
        Integer farmIndex = com.StardewValley.Network.GameStateManager.getInstance().loadFromDB(username);

        if (farmIndex != null) {
            System.out.println("Game found! User " + username + " assigned to Farm " + farmIndex);

            // Launch the game for this user
            launchGameForJoinedPlayer(username, farmIndex);
        } else {
            // Show a message that the game hasn't started yet
            Dialog waitDialog = new Dialog("Waiting", menuManager.getPixthulhuSkin());
            waitDialog.text("The host hasn't started the game yet.\nPlease wait and try again.");
            waitDialog.button("OK");
            waitDialog.show(stage);
        }
    }
    private void launchGameForJoinedPlayer(String username, int farmIndex) {
        try {
            // Get the Game singleton instance and reset it
            com.StardewValley.models.Game gameInstance = com.StardewValley.models.Game.resetInstance();

            // Get the current user
            User currentUser = lobbyController.getUserByUsername(username);
            if (currentUser == null) {
                throw new Exception("User not found");
            }

            // Load all players who are part of this game from the database
            List<String> allPlayersInGame = new ArrayList<>();
            List<Lobby> allLobbies = LobbyManager.getInstance().getLobbies();
            Lobby currentLobby = null;

            // Find the lobby this player is in
            for (Lobby lobby : allLobbies) {
                if (lobby.getMembers().contains(username)) {
                    currentLobby = lobby;
                    allPlayersInGame.addAll(lobby.getMembers());
                    break;
                }
            }

            if (currentLobby == null) {
                throw new Exception("Lobby not found for user");
            }

            // Create game with all lobby members
            List<String> otherPlayers = new ArrayList<>(allPlayersInGame);
            otherPlayers.remove(username); // Remove current user from list of other players

            // Initialize the game with current user as creator and all other players
            gameInstance.newGame(currentUser, otherPlayers);

            // Add tools to the player
            com.StardewValley.models.Tools.addBeginnerHoeToInventory(currentUser.getInventory());
            com.StardewValley.models.Tools.addBeginnerPickaxeToInventory(currentUser.getInventory());
            com.StardewValley.models.Tools.addBeginnerAxeToInventory(currentUser.getInventory());
            com.StardewValley.models.Tools.addBeginnerWateringcanToInventory(currentUser.getInventory());
            com.StardewValley.models.Tools.addBeginnerScytheToInventory(currentUser.getInventory());

            // Initialize quests
            com.StardewValley.repository.QuestRepository.getInstance().initialize();

            // Assign map selections for all players from database
            for (String playerName : allPlayersInGame) {
                User player = lobbyController.getUserByUsername(playerName);
                if (player != null) {
                    Integer playerFarmIndex = GameStateManager.getInstance().loadFromDB(playerName);
                    if (playerFarmIndex != null) {
                        gameInstance.selectMap(player, playerFarmIndex + 1); // Convert to 1-based index
                        System.out.println("Assigned " + playerName + " to Farm " + (playerFarmIndex + 1));
                    }
                }
            }

            // Initialize the game map
            System.out.println("Initializing game map for joined player...");
            gameInstance.initializeGameMap();

            // Set the game state to IN_GAME
            gameInstance.setState(com.StardewValley.models.Game.GameState.IN_GAME);

            // Get the current map from the game instance
            com.StardewValley.models.GameMap gameMap = gameInstance.getCurrentMap();

            // Create the GameView
            GameView gameView = new GameView(game, lobbyController.getLoginController(), currentLobby);

            // Create the MapView
            MapView mapView = new MapView(gameMap, gameView::showMainMenu, gameView);

            // Set the correct farm index
            mapView.setCurrentFarmIndex(farmIndex);

            // Set the MapView in GameView
            gameView.setMapView(mapView);

            // Show the game screen
            game.setScreen(gameView);
            gameView.showGameplayScreen();

            // Connect to server
            com.StardewValley.Network.Client.ClientMain.connectToServer(username);

            System.out.println("Game launched for " + username + " in Farm " + farmIndex);
            System.out.println("Client instance ID: " + com.StardewValley.Network.Client.ClientMain.getInstanceId());
        } catch (Exception e) {
            System.err.println("Error launching game for joined player: " + e.getMessage());
            e.printStackTrace();

            // Show error dialog
            Dialog errorDialog = new Dialog("Error", menuManager.getPixthulhuSkin());
            errorDialog.text("Failed to start game: " + e.getMessage());
            errorDialog.button("OK");
            errorDialog.show(stage);
        }
    }
    private void showPasswordDialog(Lobby lobby, String currentUser, Color buttonColor, Color textColor, Dialog parentDialog) {
        Dialog pwdDialog = new Dialog("Enter Password", menuManager.getPixthulhuSkin());
        TextField pwdField = new TextField("", menuManager.getPixthulhuSkin());
        pwdField.setPasswordMode(true);
        pwdField.setPasswordCharacter('*');
        pwdDialog.getContentTable().add(pwdField).width(200).pad(20);

        TextButton joinBtn = new TextButton("Join", menuManager.getPixthulhuSkin());
        joinBtn.setColor(buttonColor);
        joinBtn.getLabel().setColor(textColor);
        joinBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (lobby.getPassword() != null && lobby.getPassword().equals(pwdField.getText())) {
                    lobbyController.joinLobby(lobby, pwdField.getText(), currentUser);
                    pwdDialog.hide();
                    parentDialog.hide();
                } else {
                    pwdField.setText("");
                    pwdField.setMessageText("Wrong password!");
                }
            }
        });

        TextButton cancelBtn = new TextButton("Cancel", menuManager.getPixthulhuSkin());
        cancelBtn.setColor(buttonColor);
        cancelBtn.getLabel().setColor(textColor);
        cancelBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pwdDialog.hide();
            }
        });

        pwdDialog.getButtonTable().add(joinBtn).width(120).height(60).pad(10);
        pwdDialog.getButtonTable().add(cancelBtn).width(120).height(60).pad(10);

        pwdDialog.show(stage);
    }

    private void showJoinByCodeDialog(Color buttonColor, Color textColor, Dialog parentDialog) {
        Dialog codeDialog = new Dialog("Join by Code", menuManager.getPixthulhuSkin());
        TextField codeField = new TextField("", menuManager.getPixthulhuSkin());
        TextField pwdField = new TextField("", menuManager.getPixthulhuSkin());
        pwdField.setPasswordMode(true);
        pwdField.setPasswordCharacter('*');
        codeDialog.getContentTable().add("Lobby Code:").left();
        codeDialog.getContentTable().add(codeField).width(200).row();
        codeDialog.getContentTable().add("Password (if private):").left();
        codeDialog.getContentTable().add(pwdField).width(200).row();

        TextButton joinBtn = new TextButton("Join", menuManager.getPixthulhuSkin());
        joinBtn.setColor(buttonColor);
        joinBtn.getLabel().setColor(textColor);
        joinBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Lobby lobby = lobbyController.getLobbyById(codeField.getText());
                String currentUser = lobbyController.getCurrentUsername();
                if (lobby == null) {
                    codeField.setText("");
                    codeField.setMessageText("Not found!");
                    return;
                }
                if (!lobby.isPublic() && (lobby.getPassword() == null || !lobby.getPassword().equals(pwdField.getText()))) {
                    pwdField.setText("");
                    pwdField.setMessageText("Wrong password!");
                    return;
                }
                lobbyController.joinLobby(lobby, pwdField.getText(), currentUser);
                codeDialog.hide();
                parentDialog.hide();
            }
        });

        TextButton cancelBtn = new TextButton("Cancel", menuManager.getPixthulhuSkin());
        cancelBtn.setColor(buttonColor);
        cancelBtn.getLabel().setColor(textColor);
        cancelBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                codeDialog.hide();
            }
        });

        codeDialog.getButtonTable().add(joinBtn).width(120).height(60).pad(10);
        codeDialog.getButtonTable().add(cancelBtn).width(120).height(60).pad(10);

        codeDialog.show(stage);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menuManager.updateBackgroundAnimation(delta);
        float[] positions = menuManager.getBackgroundPositions();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        batch.begin();
        for (float position : positions) {
            batch.draw(menuManager.getBackgroundLayer(), position, 0, width, height);
        }
        batch.draw(menuManager.getMiddlegroundLayer(), 0, 0, width, height);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
