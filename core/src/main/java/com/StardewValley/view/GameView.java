package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.GameController;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.models.*;
import com.StardewValley.repository.UserRepository;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private InventoryView inventoryView;

    private Table mainMenuTable;
    private Table newGameTable;
    private Table mapSelectionTable;
    private Array<TextField> playerFields;
    private List<String> selectedPlayers;
    private Array<SelectBox<String>> mapSelectionBoxes;
    private Label statusLabel;
    private Label mapStatusLabel;
    private boolean isGameRunning = false;

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
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        // Apply button styling
        for (TextButton button : new TextButton[]{startNewGameButton, backButton}) {
            button.setColor(buttonColor);
            button.getLabel().setColor(textColor);
        }

        // Layout
        mainMenuTable.add(titleLabel).padBottom(30).row();
        mainMenuTable.add(statusLabel).padBottom(20).row();
        mainMenuTable.add(startNewGameButton).width(500).height(90).padBottom(20).row();
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

        Label titleLabel = new Label("Start New Game", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        Label currentPlayerLabel = new Label("Current Player: " + loginController.getLoggedInUser().getUsername(), menuManager.getPixthulhuSkin());
        currentPlayerLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

        Label instructionLabel = new Label("Add 1-3 other players (2-4 players total):", menuManager.getPixthulhuSkin());
        instructionLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

        for (int i = 0; i < 3; i++) {
            TextField playerField = new TextField("", menuManager.getPixthulhuSkin());
            playerField.setMessageText("Enter player " + (i + 2) + " username");
            playerFields.add(playerField);
        }

        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);
        TextButton nextButton = new TextButton("Next: Select Maps", menuManager.getPixthulhuSkin());
        TextButton backToMenuButton = new TextButton("Back", menuManager.getPixthulhuSkin());
        nextButton.setColor(buttonColor);
        backToMenuButton.setColor(buttonColor);
        nextButton.getLabel().setColor(textColor);
        backToMenuButton.getLabel().setColor(textColor);

        newGameTable.add(titleLabel).padBottom(30).row();
        newGameTable.add(currentPlayerLabel).padBottom(20).row();
        newGameTable.add(instructionLabel).padBottom(20).row();
        for (TextField field : playerFields) {
            newGameTable.add(field).width(300).padBottom(10).row();
        }
        newGameTable.add(nextButton).width(400).height(90).padTop(20).row();
        newGameTable.add(backToMenuButton).width(200).height(90).padTop(10);

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

        Label titleLabel = new Label("Select Maps", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        Label instructionLabel = new Label("Each player must choose a different map (1-4):", menuManager.getPixthulhuSkin());
        instructionLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

        mapStatusLabel = new Label("", menuManager.getPixthulhuSkin());
        mapStatusLabel.setColor(Color.RED);

        mapSelectionTable.add(titleLabel).colspan(2).padBottom(30).row();
        mapSelectionTable.add(instructionLabel).colspan(2).padBottom(20).row();
        mapSelectionTable.add(mapStatusLabel).colspan(2).padBottom(20).row();

        String[] mapOptions = {"Map 1", "Map 2", "Map 3", "Map 4"};

        List<String> allPlayersForUI = new ArrayList<>(selectedPlayers);
        allPlayersForUI.add(0, loginController.getLoggedInUser().getUsername());

        for (int i = 0; i < allPlayersForUI.size(); i++) {
            Label playerLabel = new Label(allPlayersForUI.get(i) + ":", menuManager.getPixthulhuSkin());
            playerLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

            SelectBox<String> mapSelectBox = new SelectBox<>(menuManager.getPixthulhuSkin());
            mapSelectBox.setItems(mapOptions);
            mapSelectBox.setSelectedIndex(i % 4);
            mapSelectionBoxes.add(mapSelectBox);

            mapSelectionTable.add(playerLabel).padRight(20);
            mapSelectionTable.add(mapSelectBox).width(250).padBottom(10).row();
        }

        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);
        TextButton startGameButton = new TextButton("Start Game", menuManager.getPixthulhuSkin());
        TextButton backToPlayersButton = new TextButton("Back", menuManager.getPixthulhuSkin());
        startGameButton.setColor(buttonColor);
        backToPlayersButton.setColor(buttonColor);
        startGameButton.getLabel().setColor(textColor);
        backToPlayersButton.getLabel().setColor(textColor);

        mapSelectionTable.add(startGameButton).width(400).height(90).colspan(2).padTop(20).row();
        mapSelectionTable.add(backToPlayersButton).width(200).height(90).colspan(2).padTop(10);

        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createGameWithSelectedMaps(allPlayersForUI);
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
        isGameRunning = false;
        mainMenuTable.setVisible(false);
        mapSelectionTable.setVisible(false);
        newGameTable.setVisible(true);
        for (TextField field : playerFields) {
            field.setText("");
        }
        statusLabel.setText("");
        Gdx.input.setInputProcessor(stage);
    }

    private void showMainMenu() {
        isGameRunning = false;
        newGameTable.setVisible(false);
        mapSelectionTable.setVisible(false);
        mainMenuTable.setVisible(true);
        if (mapView != null) {
            mapView.dispose();
            mapView = null;
        }
        Gdx.input.setInputProcessor(stage);
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
        for (TextField field : playerFields) {
            String username = field.getText().trim();
            if (!username.isEmpty()) {
                if (UserRepository.getInstance().getUserByUsername(username) == null) {
                    statusLabel.setText("User '" + username + "' not found!");
                    return;
                }
                if (selectedPlayers.contains(username) || username.equals(loginController.getLoggedInUser().getUsername())) {
                    statusLabel.setText("Duplicate username: " + username);
                    return;
                }
                selectedPlayers.add(username);
            }
        }
        if (selectedPlayers.isEmpty()) {
            statusLabel.setText("Please add at least one other player.");
            return;
        }
        showMapSelectionMenu();
    }

    private void createGameWithSelectedMaps(List<String> allPlayerUsernames) {
        List<Integer> selectedMapsNumbers = new ArrayList<>();
        for (SelectBox<String> selectBox : mapSelectionBoxes) {
            selectedMapsNumbers.add(Integer.parseInt(selectBox.getSelected().split(" ")[1]));
        }

        if (selectedMapsNumbers.size() != selectedMapsNumbers.stream().distinct().count()) {
            mapStatusLabel.setText("Each player must choose a different map!");
            return;
        }

        try {
            com.StardewValley.models.Game gameInstance = com.StardewValley.models.Game.resetInstance();
            gameInstance.newGame(loginController.getLoggedInUser(), selectedPlayers);

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

            FarmManager farmManager = new FarmManager();
            List<Farm> farms = new ArrayList<>();
            Map<String, Integer> playerMapChoices = new HashMap<>();
            for (int i = 0; i < allPlayerUsernames.size(); i++) {
                playerMapChoices.put(allPlayerUsernames.get(i), selectedMapsNumbers.get(i));
            }

            for (User player : gameInstance.getPlayers()) {
                int mapId = playerMapChoices.get(player.getUsername());
                Farm farm = farmManager.getFarm(mapId - 1);
                farm.setOwner(player);
                player.setFarm(farm);
                farms.add(farm);
                gameInstance.selectMap(player, mapId);
            }

            VillageTemplate village = VillageTemplate.createDefaultVillage();
            GameMap gameMap = new GameMap(farms, village);
            gameInstance.setCurrentMap(gameMap);
            gameInstance.setState(com.StardewValley.models.Game.GameState.IN_GAME);

            Runnable backToMenuCallback = this::showMainMenu;
            mapView = new MapView(gameMap, backToMenuCallback);
            mapView.setCurrentFarmIndex(0);

            showGameplayScreen();

        } catch (Exception e) {
            System.err.println("Error creating game: " + e.getMessage());
            e.printStackTrace();
            mapStatusLabel.setText("Error: " + e.getMessage());
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
        isGameRunning = true;
        mainMenuTable.setVisible(false);
        newGameTable.setVisible(false);
        mapSelectionTable.setVisible(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (isGameRunning) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                if (inventoryView == null) {
                    inventoryView = new InventoryView(game, loginController, this);
                }
                game.setScreen(inventoryView);
            }
            if (mapView != null) {
                mapView.render(delta);
            }
        } else {
            menuManager.updateBackgroundAnimation(delta);
            float[] positions = menuManager.getBackgroundPositions();
            batch.begin();
            float width = Gdx.graphics.getWidth();
            float height = Gdx.graphics.getHeight();
            for (float position : positions) {
                batch.draw(menuManager.getBackgroundLayer(), position, 0, width, height);
            }
            batch.draw(menuManager.getMiddlegroundLayer(), 0, 0, width, height);
            batch.end();
            stage.act(delta);
            stage.draw();
        }
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
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        if (mapView != null) {
            mapView.dispose();
        }
    }
}
