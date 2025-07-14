package com.StardewValley.view;

import com.StardewValley.exceptions.GameException;
import com.StardewValley.models.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.StardewValley.controller.GameController;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameView implements Screen {
    private final com.badlogic.gdx.Game game;
    private final Stage stage;
    private final MenuManager menuManager;
    private final SpriteBatch batch;
    private final LoginMenuController loginController;
    private final GameController gameController;
    private MapView mapView;
    private Table gameplayTable;

    private Table mainMenuTable;
    private Table newGameTable;
    private Table mapSelectionTable;
    private Array<TextField> playerFields;
    private List<String> selectedPlayers;
    private Array<SelectBox<String>> mapSelectionBoxes;
    private Label statusLabel;
    private Label mapStatusLabel;

    public GameView(com.badlogic.gdx.Game game, LoginMenuController loginController) {
        this.game = game;
        this.loginController = loginController;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();
        this.playerFields = new Array<>();
        this.selectedPlayers = new ArrayList<>();
        this.mapSelectionBoxes = new Array<>();
        this.gameController = new GameController(loginController.getLoggedInUser(), null);

        createUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        createMainMenu();
        createNewGameMenu();
        createMapSelectionMenu();

        // Show main menu initially
        mainMenuTable.setVisible(true);
        newGameTable.setVisible(false);
        mapSelectionTable.setVisible(false);

        stage.addActor(mainMenuTable);
        stage.addActor(newGameTable);
        stage.addActor(mapSelectionTable);
    }

    private void createMainMenu() {
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.top().padTop(50);

        // Title
        Label titleLabel = new Label("Game Menu", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        // Status label
        statusLabel = new Label("", menuManager.getPixthulhuSkin());
        statusLabel.setColor(0.2f, 0.8f, 0.2f, 1f);

        // Buttons
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        TextButton startNewGameButton = new TextButton("Start New Game", menuManager.getPixthulhuSkin());
        TextButton loadLastGameButton = new TextButton("Load Last Game", menuManager.getPixthulhuSkin());
        TextButton showCurrentGameButton = new TextButton("Show Current Game", menuManager.getPixthulhuSkin());
        TextButton terminateGameButton = new TextButton("Terminate Current Game", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        // Apply button styling
        for (TextButton button : new TextButton[]{startNewGameButton, loadLastGameButton,
            showCurrentGameButton, terminateGameButton, backButton}) {
            button.setColor(buttonColor);
            button.getLabel().setColor(textColor);
        }

        // Layout
        mainMenuTable.add(titleLabel).padBottom(30).row();
        mainMenuTable.add(statusLabel).padBottom(20).row();
        mainMenuTable.add(startNewGameButton).width(500).height(90).padBottom(20).row();
        mainMenuTable.add(loadLastGameButton).width(500).height(90).padBottom(20).row();
        mainMenuTable.add(showCurrentGameButton).width(600).height(90).padBottom(20).row();
        mainMenuTable.add(terminateGameButton).width(700).height(90).padBottom(20).row();
        mainMenuTable.add(backButton).width(200).height(90).padTop(20);

        // Button listeners
        startNewGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showNewGameMenu();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new MainView(game, loginController));
            }
        });
    }

    private void createNewGameMenu() {
        newGameTable = new Table();
        newGameTable.setFillParent(true);
        newGameTable.top().padTop(50);

        // Title
        Label titleLabel = new Label("Start New Game", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        // Current player label
        Label currentPlayerLabel = new Label("Current Player: " + loginController.getLoggedInUser().getUsername(),
            menuManager.getPixthulhuSkin());
        currentPlayerLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

        // Instructions
        Label instructionLabel = new Label("Add 1-3 other players (2-4 players total):", menuManager.getPixthulhuSkin());
        instructionLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

        // Player input fields
        for (int i = 0; i < 3; i++) {
            TextField playerField = new TextField("", menuManager.getPixthulhuSkin());
            playerField.setMessageText("Enter player " + (i + 2) + " username");
            playerFields.add(playerField);
        }

        // Buttons
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        TextButton nextButton = new TextButton("Next: Select Maps", menuManager.getPixthulhuSkin());
        TextButton backToMenuButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        nextButton.setColor(buttonColor);
        backToMenuButton.setColor(buttonColor);
        nextButton.getLabel().setColor(textColor);
        backToMenuButton.getLabel().setColor(textColor);

        // Layout
        newGameTable.add(titleLabel).padBottom(30).row();
        newGameTable.add(currentPlayerLabel).padBottom(20).row();
        newGameTable.add(instructionLabel).padBottom(20).row();

        for (TextField field : playerFields) {
            newGameTable.add(field).width(300).padBottom(10).row();
        }

        newGameTable.add(nextButton).width(400).height(90).padTop(20).row();
        newGameTable.add(backToMenuButton).width(200).height(90).padTop(10);

        // Button listeners
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                validateAndProceedToMapSelection();
            }
        });

        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showMainMenu();
            }
        });
    }

    private void createMapSelectionMenu() {
        mapSelectionTable = new Table();
        mapSelectionTable.setFillParent(true);
        mapSelectionTable.top().padTop(50);
        mapSelectionTable.setVisible(false);
    }

    private void createMapSelectionMenuContent() {
        mapSelectionTable.clear();
        mapSelectionBoxes.clear();

        // Title
        Label titleLabel = new Label("Select Maps", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        // Instructions
        Label instructionLabel = new Label("Each player must choose a different map (1-4):", menuManager.getPixthulhuSkin());
        instructionLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

        // Status label for map selection
        mapStatusLabel = new Label("", menuManager.getPixthulhuSkin());
        mapStatusLabel.setColor(0.2f, 0.8f, 0.2f, 1f);

        // Add title and instructions
        mapSelectionTable.add(titleLabel).colspan(2).padBottom(30).row();
        mapSelectionTable.add(instructionLabel).colspan(2).padBottom(20).row();
        mapSelectionTable.add(mapStatusLabel).colspan(2).padBottom(20).row();

        // Create map options
        String[] mapOptions = {"Map 1", "Map 2", "Map 3", "Map 4"};

        // Add current player first if not already in the list
        if (!selectedPlayers.contains(loginController.getLoggedInUser().getUsername())) {
            selectedPlayers.add(0, loginController.getLoggedInUser().getUsername());
        }

        // Create selection boxes for each player
        for (int i = 0; i < selectedPlayers.size(); i++) {
            String playerName = selectedPlayers.get(i);

            Label playerLabel = new Label(playerName + ":", menuManager.getPixthulhuSkin());
            playerLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

            SelectBox<String> mapSelectBox = new SelectBox<>(menuManager.getPixthulhuSkin());
            mapSelectBox.setItems(mapOptions);
            mapSelectBox.setSelectedIndex(i % 4);  // Use modulo to ensure valid index
            mapSelectionBoxes.add(mapSelectBox);

            mapSelectionTable.add(playerLabel).padRight(20);
            // Make the select box wider to show full map names
            mapSelectionTable.add(mapSelectBox).width(250).padBottom(10).row();
        }

        // Buttons
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        TextButton startGameButton = new TextButton("Start Game", menuManager.getPixthulhuSkin());
        TextButton backToPlayersButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        startGameButton.setColor(buttonColor);
        backToPlayersButton.setColor(buttonColor);
        startGameButton.getLabel().setColor(textColor);
        backToPlayersButton.getLabel().setColor(textColor);

        // Make start game button larger
        mapSelectionTable.add(startGameButton).width(400).height(90).colspan(2).padTop(20).row();
        mapSelectionTable.add(backToPlayersButton).width(200).height(90).colspan(2).padTop(10);

        // Button listeners
        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createGameWithSelectedMaps();
            }
        });

        backToPlayersButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showNewGameMenu();
            }
        });
    }

    private void showNewGameMenu() {
        mainMenuTable.setVisible(false);
        mapSelectionTable.setVisible(false);
        newGameTable.setVisible(true);

        // Clear only the text fields
        for (TextField field : playerFields) {
            field.setText("");
        }
        statusLabel.setText("");
    }

    private void showMainMenu() {
        newGameTable.setVisible(false);
        mapSelectionTable.setVisible(false);
        mainMenuTable.setVisible(true);
        clearFields();
    }

    private void showMapSelectionMenu() {
        mainMenuTable.setVisible(false);
        newGameTable.setVisible(false);
        createMapSelectionMenuContent();
        mapSelectionTable.setVisible(true);
    }

    private void validateAndProceedToMapSelection() {
        selectedPlayers.clear();

        // Collect non-empty player names
        for (TextField field : playerFields) {
            String username = field.getText().trim();
            if (!username.isEmpty()) {
                // Check if user exists
                User user = UserRepository.getInstance().getUserByUsername(username);
                if (user == null) {
                    statusLabel.setText("User '" + username + "' not found!");
                    statusLabel.setColor(Color.RED);
                    return;
                }
                selectedPlayers.add(username);
            }
        }

        // Validate player count (need at least 1 other player)
        if (selectedPlayers.isEmpty()) {
            statusLabel.setText("Please add at least one other player");
            statusLabel.setColor(Color.RED);
            return;
        }

        // All validations passed, proceed to map selection
        showMapSelectionMenu();
    }

    private void createGameWithSelectedMaps() {
        List<Integer> selectedMaps = new ArrayList<>();

        // Collect selected maps
        for (SelectBox<String> selectBox : mapSelectionBoxes) {
            String selectedMap = selectBox.getSelected();
            int mapNumber = Integer.parseInt(selectedMap.split(" ")[1]);
            selectedMaps.add(mapNumber);
        }

        // Check for duplicate maps
        if (selectedMaps.size() != selectedMaps.stream().distinct().count()) {
            mapStatusLabel.setText("Each player must choose a different map!");
            mapStatusLabel.setColor(Color.RED);
            return;
        }

        try {
            // Get the Game singleton instance and reset it
            Game gameInstance = Game.resetInstance();

            // Create new game using the current user as creator and selected players list
            gameInstance.newGame(loginController.getLoggedInUser(), selectedPlayers);

            // Add tools to all players
            for (User user : gameInstance.getPlayers()) {
                Tools.addBeginnerHoeToInventory(user.getInventory());
                Tools.addBeginnerPickaxeToInventory(user.getInventory());
                Tools.addBeginnerAxeToInventory(user.getInventory());
                Tools.addBeginnerWateringcanToInventory(user.getInventory());
                Tools.addLearningFishingpoleToInventory(user.getInventory());
                Tools.addBeginnerScytheToInventory(user.getInventory());
                Tools.addBeginnerMilkPailToInventory(user.getInventory());
                Tools.addBeginnerShearToInventory(user.getInventory());
                Tools.addBeginnerTrashbinToInventory(user.getInventory());
            }

            // Directly create farms and assign to players
            FarmManager farmManager = new FarmManager();
            List<Farm> farms = new ArrayList<>();

            // Create a map of username to selected map number
            Map<String, Integer> playerMapChoices = new HashMap<>();
            for (int i = 0; i < selectedPlayers.size(); i++) {
                String username = selectedPlayers.get(i);
                int mapNumber = selectedMaps.get(i);
                playerMapChoices.put(username, mapNumber);
                System.out.println("Player " + username + " selected map " + mapNumber);
            }

            // Get all players and assign them to their selected farms
            List<User> players = gameInstance.getPlayers();
            for (User player : players) {
                // Get the map number this player selected
                int mapId = playerMapChoices.getOrDefault(player.getUsername(), 1);

                // Get the farm and set ownership
                Farm farm = farmManager.getFarm(mapId);
                farm.setOwner(player);
                player.setFarm(farm);
                farms.add(farm);

                // Update the game's map selection record
                gameInstance.selectMap(player, mapId);
                System.out.println("Assigned " + player.getUsername() + " to Farm " + mapId);
            }

            // Create the village and game map
            VillageTemplate village = VillageTemplate.createDefaultVillage();
            GameMap gameMap = new GameMap(farms, village);

            // Set the map in the game instance
            gameInstance.setCurrentMap(gameMap);
            gameInstance.setState(Game.GameState.IN_GAME);

            // Create the MapView with the initialized map
            mapView = new MapView(gameMap);

            // Set first farm as active and center camera on it
            mapView.setCurrentFarmIndex(0);

            System.out.println("Map created successfully with " + farms.size() + " farms");
            mapStatusLabel.setText("Game started successfully!");
            mapStatusLabel.setColor(Color.GREEN);

            // Navigate to gameplay screen
            showGameplayScreen();
        }
        catch (Exception e) {
            System.err.println("Error creating game: " + e.getMessage());
            e.printStackTrace();
            mapStatusLabel.setText("Error: " + e.getMessage());
            mapStatusLabel.setColor(Color.RED);
        }
    }

    private void clearFields() {
        for (TextField field : playerFields) {
            field.setText("");
        }
        selectedPlayers.clear();
        mapSelectionBoxes.clear();
        statusLabel.setText("");
    }
    private void showGameplayScreen() {
        // Hide other screens
        mainMenuTable.setVisible(false);
        newGameTable.setVisible(false);
        mapSelectionTable.setVisible(false);

        // Create gameplay table if it doesn't exist
        if (gameplayTable == null) {
            gameplayTable = new Table();
            gameplayTable.setFillParent(true);

            // Add controls (example: back button)
            TextButton backButton = new TextButton("Back to Menu", menuManager.getPixthulhuSkin());
            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showMainMenu();
                    // Optional: cleanup or save game state
                }
            });

            Table controlsTable = new Table();
            controlsTable.top().right();
            controlsTable.add(backButton).pad(10);

            gameplayTable.add(controlsTable).expand().fill().row();
            stage.addActor(gameplayTable);
        }

        gameplayTable.setVisible(true);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background rendering
        menuManager.updateBackgroundAnimation(delta);
        float[] positions = menuManager.getBackgroundPositions();

        batch.begin();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        for (float position : positions) {
            batch.draw(menuManager.getBackgroundLayer(),
                position, 0,
                width, height);
        }

        batch.draw(menuManager.getMiddlegroundLayer(), 0, 0, width, height);
        batch.end();

        // Render map if MapView exists and gameplay screen is visible
        if (mapView != null && gameplayTable != null && gameplayTable.isVisible()) {
            mapView.render();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (mapView != null) {
            mapView.resize(width, height);
        }
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
        if (mapView != null) {
            mapView.dispose();
        }
    }
}
