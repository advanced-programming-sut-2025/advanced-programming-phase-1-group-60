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

    // References to the other screens
    private MapView mapView;
    private InventoryView inventoryView;
    private CraftingMenuScreenView craftingMenuScreenView; // Assumed you will add this class
    private BlacksmithView blacksmithView;
    private FishShopView fishShopView;
    private SaloonView saloonView;
    private KitchenView kitchenView;

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

        Label titleLabel = new Label("Game Menu", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        statusLabel = new Label("", menuManager.getPixthulhuSkin());
        statusLabel.setColor(Color.GREEN);

        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        TextButton startNewGameButton = new TextButton("Start New Game", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        for (TextButton button : new TextButton[]{startNewGameButton, backButton}) {
            button.setColor(buttonColor);
            button.getLabel().setColor(textColor);
        }

        mainMenuTable.add(titleLabel).padBottom(30).row();
        mainMenuTable.add(statusLabel).padBottom(20).row();
        mainMenuTable.add(startNewGameButton).width(500).height(90).padBottom(20).row();
        mainMenuTable.add(backButton).width(200).height(90).padTop(20);

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

        final List<String> allPlayersForUI = new ArrayList<>(selectedPlayers);
        allPlayersForUI.add(0, loginController.getLoggedInUser().getUsername());

        for (int i = 0; i < allPlayersForUI.size(); i++) {
            Label playerLabel = new Label(allPlayersForUI.get(i) + ":", menuManager.getPixthulhuSkin());
            playerLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

            SelectBox<String> mapSelectBox = new SelectBox<>(menuManager.getPixthulhuSkin());
            mapSelectBox.setItems(mapOptions);
            mapSelectBox.setSelectedIndex(i % mapOptions.length);
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

        // Validate player count (need at least 1 player total including the logged-in user)
        if (selectedPlayers.size() < 0) { // Changed from 1 to 0 since we're not adding logged-in user here
            statusLabel.setText("Please add at least one other player");
            statusLabel.setColor(Color.RED);
            return;
        }

        // All validations passed, proceed to map selection
        showMapSelectionMenu();
    }

    private void createGameWithSelectedMaps(List<String> allPlayerUsernames) {
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
            com.StardewValley.models.Game gameInstance = com.StardewValley.models.Game.resetInstance();

            // Create new game using the current user as creator and selected players list
            gameInstance.newGame(loginController.getLoggedInUser(), selectedPlayers);

            // Add tools to all players
            for (User user : gameInstance.getPlayers()) {
                Tools.addBeginnerHoeToInventory(user.getInventory());
                Tools.addBeginnerPickaxeToInventory(user.getInventory());
                Tools.addBeginnerAxeToInventory(user.getInventory());
                Tools.addBeginnerWateringcanToInventory(user.getInventory());
                //      Tools.addLearningFishingpoleToInventory(user.getInventory());
                Tools.addBeginnerScytheToInventory(user.getInventory());
                Tools.addBeginnerMilkPailToInventory(user.getInventory());
                Tools.addBeginnerShearToInventory(user.getInventory());
                //     Tools.addBeginnerTrashbinToInventory(user.getInventory());
            }

            // FIX: Initialize quests BEFORE creating the map
            com.StardewValley.repository.QuestRepository.getInstance().initialize();

            // Get all players and assign them to their selected farms
            List<User> players = gameInstance.getPlayers();

            // Create a map of username to selected map number
            Map<String, Integer> playerMapChoices = new HashMap<>();
            for (int i = 0; i < allPlayerUsernames.size(); i++) {
                String username = allPlayerUsernames.get(i);
                int mapNumber = selectedMaps.get(i);
                playerMapChoices.put(username, mapNumber);
                System.out.println("Player " + username + " selected map " + mapNumber);
            }

            // Assign maps to players using the Game's selectMap method
            for (User player : players) {
                int mapId = playerMapChoices.getOrDefault(player.getUsername(), 1);
                gameInstance.selectMap(player, mapId);
                System.out.println("Assigned " + player.getUsername() + " to Farm " + mapId);
            }

            // IMPORTANT: Initialize the game map after all players have selected their maps
            System.out.println("Initializing game map...");
            gameInstance.initializeGameMap();

            // Set the game state to IN_GAME
            gameInstance.setState(com.StardewValley.models.Game.GameState.IN_GAME);

            // Get the current map from the game instance
            GameMap gameMap = gameInstance.getCurrentMap();

            // Check if gameMap is null before proceeding
            if (gameMap == null) {
                throw new RuntimeException("GameMap is null after initialization");
            }

            // Create the MapView with the initialized map
            Runnable backToMenuCallback = this::showMainMenu;
            mapView = new MapView(gameMap, backToMenuCallback, this);

            // Set first farm as active and center camera on it
            mapView.setCurrentFarmIndex(0);

            System.out.println("Map created successfully with game map");
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
        isGameRunning = true;
        game.setScreen(mapView);
    }

    public void showInventoryScreen() {
        if (inventoryView == null || inventoryView.isDisposed()) {
            inventoryView = new InventoryView(game, loginController, this, null);
        }
        game.setScreen(inventoryView);
    }

    public void showInventoryForGifting(Npc targetNpc) {
        if (inventoryView == null || inventoryView.isDisposed()) {
            inventoryView = new InventoryView(game, loginController, this, targetNpc);
        } else {
            inventoryView.setGiftingTarget(targetNpc);
        }
        game.setScreen(inventoryView);
    }

    public void showInventoryForSelling() {
        if (inventoryView == null || inventoryView.isDisposed()) {
            inventoryView = new InventoryView(game, loginController, this, null);
            inventoryView.setSellingMode(true);
        } else {
            inventoryView.setSellingMode(true);
        }
        game.setScreen(inventoryView);
    }

    public void showBlacksmithView(Store blacksmith) {
        if (blacksmithView == null) { // or some isDisposed check
            blacksmithView = new BlacksmithView(game, loginController, this, blacksmith);
        }
        game.setScreen(blacksmithView);
    }

    public void showFishShopView(Store fishShop) {
        if (fishShopView == null) { // or some isDisposed check
            fishShopView = new FishShopView(game, loginController, this, fishShop);
        }
        game.setScreen(fishShopView);
    }

    public void showSaloonView(Store saloon) {
        if (saloonView == null) { // or some isDisposed check
            saloonView = new SaloonView(game, loginController, this, saloon);
        }
        game.setScreen(saloonView);
    }

    public void showKitchenView() {
        if (kitchenView == null) {
            kitchenView = new KitchenView(game, loginController, this);
        }
        game.setScreen(kitchenView);
    }


    // You will need to create this class yourself based on your colleague's code
    public void showCraftingMenu(User player) {
        if (craftingMenuScreenView == null) {
            craftingMenuScreenView = new CraftingMenuScreenView(player, this);
        }
        game.setScreen(craftingMenuScreenView);
    }

    public void showMapView() {
        if (mapView != null) {
            game.setScreen(mapView);
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (isGameRunning) {
            // The active screen (MapView, InventoryView, etc.) will render itself.
            // This GameView's render is now only for the menus.
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
        if (inventoryView != null) {
            inventoryView.dispose();
        }
        if (craftingMenuScreenView != null) {
            craftingMenuScreenView.dispose();
        }
        if (kitchenView != null) {
            kitchenView.dispose();
        }
    }
}
