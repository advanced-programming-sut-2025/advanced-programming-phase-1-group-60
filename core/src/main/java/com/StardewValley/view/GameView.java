package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.GameController;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.models.*;
import com.StardewValley.repository.UserRepository;
import com.StardewValley.view.util.MiniMapRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.*;
import java.util.List;

/**
 * Updates in this version:
 * 1. Username validation improvements when starting a game:
 *      - Now enforces 2-4 total players (including the logged-in user)
 *      - Detects duplicate added usernames
 *      - Gives clear error if any entered username does not exist
 * 2. Map selection preview:
 *      - For each player's map selection row, shows a small preview image of the CURRENTLY SELECTED map.
 *      - Previews update live when the select box value changes.
 *      - Uses images: assets/Map/Map/1.png ... 4.png
 *      - Scaled down to a small thumbnail (default 160x90).
 * 3. Proper disposal of loaded preview textures.
 */
public class GameView implements Screen {
    private final com.badlogic.gdx.Game game;
    private final Stage stage;
    private final MenuManager menuManager;
    private final SpriteBatch batch;
    private final LoginMenuController loginController;
    private final GameController gameController;
    private Texture miniMapTexture;
    // Screens
    private MapView mapView;
    private InventoryView inventoryView;
    private CraftingMenuScreenView craftingMenuScreenView;
    private BlacksmithView blacksmithView;
    private FishShopView fishShopView;
    private SaloonView saloonView;
    private MarinsRanchView marinsRanchView;
    private CarpenterShopView carpenterShopView;
    private KitchenView kitchenView;
    private GiftToPlayerView giftToPlayerView;
    private SettingsView settingsView;
    private static final float MAP_PREVIEW_WIDTH  = 240f;          // Base width
    private static final float MAP_PREVIEW_ASPECT = 16f / 9f;
    private static final float MAP_PREVIEW_HEIGHT = MAP_PREVIEW_WIDTH / MAP_PREVIEW_ASPECT;
    private static final float ZOOM_FACTOR        = 1.35f;         // Hover scale
    private static final float ANIM_TIME          = 0.18f;         // Animation duration
    private static final float HOVER_ELEVATION    = 12f;
    // UI
    private Table mainMenuTable;
    private Table newGameTable;
    private Table mapSelectionTable;
    private Array<TextField> playerFields;
    private List<String> selectedPlayers;
    private Array<SelectBox<String>> mapSelectionBoxes;
    private Array<Image> mapPreviewImages; // NEW: holds preview image widgets
    private Label statusLabel;
    private Label mapStatusLabel;
    private boolean isGameRunning = false;
    private Texture lastFrameTexture;

    // Map preview textures
    private final Texture[] mapPreviewTextures = new Texture[4]; // index 0 => 1.png, etc.
    private boolean previewsLoaded = false;

    public GameView(com.badlogic.gdx.Game game, LoginMenuController loginController) {
        this.game = game;
        this.loginController = loginController;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();
        this.playerFields = new Array<>();
        this.selectedPlayers = new ArrayList<>();
        this.mapSelectionBoxes = new Array<>();
        this.mapPreviewImages = new Array<>();
        this.gameController = new GameController(loginController.getLoggedInUser(), null);

        loadMapPreviews();
        createUI();
        Gdx.input.setInputProcessor(stage);
    }
    public Texture getOrCreateMiniMapTexture() {
        if (miniMapTexture == null) {
            refreshMiniMapTexture();
        }
        return miniMapTexture;
    }
    public void refreshMiniMapTexture() {
        // Dispose old
        if (miniMapTexture != null) {
            miniMapTexture.dispose();
            miniMapTexture = null;
        }
        if (mapView != null) {
            // Use current active GameMap through Game instance
            GameMap gm = com.StardewValley.models.Game.getInstance().getCurrentMap();
            if (gm != null) {
                miniMapTexture = MiniMapRenderer.generate(gm, mapView.getCurrentFarmIndex(),
                    mapView.isInVillage(), mapView.getPlayerWorldPosition());
            }
        }
    }
    public void invalidateMiniMapCache() {
        if (miniMapTexture != null) {
            miniMapTexture.dispose();
            miniMapTexture = null;
        }
    }

    // --------------------------------------------------
    // Preview assets
    // --------------------------------------------------
    private void loadMapPreviews() {
        // Avoid re-loading if already loaded
        if (previewsLoaded) return;
        for (int i = 1; i <= 4; i++) {
            String path = "assets/Map/Map/" + i + ".png";
            if (Gdx.files.internal(path).exists()) {
                mapPreviewTextures[i - 1] = new Texture(Gdx.files.internal(path));
            } else {
                System.err.println("Map preview image missing: " + path);
            }
        }
        previewsLoaded = true;
    }

    private Texture getPreviewTextureForMapLabel(String mapLabel) {
        if (mapLabel == null) return null;
        // Expect format "Map X"
        String[] parts = mapLabel.split(" ");
        if (parts.length < 2) return null;
        try {
            int num = Integer.parseInt(parts[1]);
            if (num >= 1 && num <= 4) {
                return mapPreviewTextures[num - 1];
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }

    // --------------------------------------------------
    // UI creation
    // --------------------------------------------------
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

        playerFields.clear();
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
        mapPreviewImages.clear();

        Label titleLabel = new Label("Select Maps", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        Label instructionLabel = new Label("Each player must choose a different map (1-4):", menuManager.getPixthulhuSkin());
        instructionLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

        mapStatusLabel = new Label("", menuManager.getPixthulhuSkin());
        mapStatusLabel.setColor(Color.RED);

        mapSelectionTable.add(titleLabel).colspan(3).padBottom(30).row();
        mapSelectionTable.add(instructionLabel).colspan(3).padBottom(20).row();
        mapSelectionTable.add(mapStatusLabel).colspan(3).padBottom(20).row();

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

            // Base preview image
            Texture initialTexture = getPreviewTextureForMapLabel(mapSelectBox.getSelected());
            Image preview = (initialTexture != null) ? new Image(initialTexture) : new Image();
            preview.setScaling(Scaling.fit);
            preview.setSize(MAP_PREVIEW_WIDTH, MAP_PREVIEW_HEIGHT);
            preview.setOrigin(Align.center);  // Needed so scale happens from center
            mapPreviewImages.add(preview);

            // Wrap in a fixed-size container so layout does not shift when scaling
            Container<Image> previewContainer = new Container<>(preview);
            previewContainer.size(MAP_PREVIEW_WIDTH, MAP_PREVIEW_HEIGHT);
            previewContainer.fill(); // Child uses full container size

            // Add hover zoom behavior
            addHoverZoom(previewContainer, preview);

            // Update preview when selection changes
            mapSelectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Texture tex = getPreviewTextureForMapLabel(mapSelectBox.getSelected());
                    if (tex != null) {
                        preview.setDrawable(new Image(tex).getDrawable());
                    }
                }
            });

            // Layout: Player | SelectBox | PreviewContainer
            mapSelectionTable.add(playerLabel).left().padRight(15);
            mapSelectionTable.add(mapSelectBox).width(200).padRight(25).left();
            mapSelectionTable.add(previewContainer)
                .width(MAP_PREVIEW_WIDTH)
                .height(MAP_PREVIEW_HEIGHT)
                .padBottom(12)
                .left()
                .row();
        }

        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);
        TextButton startGameButton = new TextButton("Start Game", menuManager.getPixthulhuSkin());
        TextButton backToPlayersButton = new TextButton("Back", menuManager.getPixthulhuSkin());
        startGameButton.setColor(buttonColor);
        backToPlayersButton.setColor(buttonColor);
        startGameButton.getLabel().setColor(textColor);
        backToPlayersButton.getLabel().setColor(textColor);

        mapSelectionTable.add(startGameButton).width(400).height(90).colspan(3).padTop(20).row();
        mapSelectionTable.add(backToPlayersButton).width(200).height(90).colspan(3).padTop(10);

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
    private void addHoverZoom(Container<Image> container, Image image) {
        container.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // Bring to front visually
                container.toFront();
                // Clear pending actions for responsiveness
                image.clearActions();
                container.clearActions();

                // Animate scale & slight lift
                image.addAction(Actions.scaleTo(ZOOM_FACTOR, ZOOM_FACTOR, ANIM_TIME, Interpolation.sineOut));
                container.addAction(Actions.moveBy(0, HOVER_ELEVATION, ANIM_TIME, Interpolation.sineOut));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // Revert scale & position
                image.clearActions();
                container.clearActions();
                image.addAction(Actions.scaleTo(1f, 1f, ANIM_TIME, Interpolation.sineIn));
                container.addAction(Actions.moveBy(0, -HOVER_ELEVATION, ANIM_TIME, Interpolation.sineIn));
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

    public void showMainMenu() {
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

    // --------------------------------------------------
    // Validation logic update
    // --------------------------------------------------
    private void validateAndProceedToMapSelection() {
        selectedPlayers.clear();

        Set<String> duplicatesCheck = new HashSet<>();
        String currentUser = loginController.getLoggedInUser().getUsername();

        for (TextField field : playerFields) {
            String username = field.getText().trim();
            if (!username.isEmpty()) {
                if (username.equalsIgnoreCase(currentUser)) {
                    statusLabel.setText("You already are player 1. Remove '" + username + "' from extra slots.");
                    statusLabel.setColor(Color.RED);
                    return;
                }
                if (!User.verifyName(username)) {
                    statusLabel.setText("Invalid username format: " + username);
                    statusLabel.setColor(Color.RED);
                    return;
                }
                // existence check
                User user = UserRepository.getInstance().getUserByUsername(username);
                if (user == null) {
                    statusLabel.setText("User '" + username + "' not found!");
                    statusLabel.setColor(Color.RED);
                    return;
                }
                if (!duplicatesCheck.add(username.toLowerCase())) {
                    statusLabel.setText("Duplicate username: " + username);
                    statusLabel.setColor(Color.RED);
                    return;
                }
                selectedPlayers.add(username);
            }
        }

        int totalPlayers = 1 + selectedPlayers.size(); // include logged in user
        if (totalPlayers < 2) {
            statusLabel.setText("Need at least 2 players total.");
            statusLabel.setColor(Color.RED);
            return;
        }
        if (totalPlayers > 4) {
            statusLabel.setText("Maximum 4 players total.");
            statusLabel.setColor(Color.RED);
            return;
        }

        // Success
        statusLabel.setText("Players OK. Proceed to map selection.");
        statusLabel.setColor(Color.GREEN);
        showMapSelectionMenu();
    }

    // --------------------------------------------------
    // Game creation
    // --------------------------------------------------
    private void createGameWithSelectedMaps(List<String> allPlayerUsernames) {
        List<Integer> selectedMaps = new ArrayList<>();

        for (SelectBox<String> selectBox : mapSelectionBoxes) {
            String selectedMap = selectBox.getSelected();
            int mapNumber = Integer.parseInt(selectedMap.split(" ")[1]);
            selectedMaps.add(mapNumber);
        }

        if (selectedMaps.size() != selectedMaps.stream().distinct().count()) {
            mapStatusLabel.setText("Each player must choose a different map!");
            mapStatusLabel.setColor(Color.RED);
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
                Tools.addBeginnerScytheToInventory(user.getInventory());
            }

            com.StardewValley.repository.QuestRepository.getInstance().initialize();

            Map<String, Integer> playerMapChoices = new HashMap<>();
            for (int i = 0; i < allPlayerUsernames.size(); i++) {
                String username = allPlayerUsernames.get(i);
                int mapNumber = selectedMaps.get(i);
                playerMapChoices.put(username, mapNumber);
            }

            for (User player : gameInstance.getPlayers()) {
                int mapId = playerMapChoices.getOrDefault(player.getUsername(), 1);
                gameInstance.selectMap(player, mapId);
            }

            gameInstance.initializeGameMap();
            gameInstance.setState(com.StardewValley.models.Game.GameState.IN_GAME);

            GameMap gameMap = gameInstance.getCurrentMap();
            if (gameMap == null) throw new RuntimeException("GameMap is null after initialization");

            Runnable backToMenuCallback = this::showMainMenu;
            mapView = new MapView(gameMap, backToMenuCallback, this);
            mapView.setCurrentFarmIndex(0);

            mapStatusLabel.setText("Game started successfully!");
            mapStatusLabel.setColor(Color.GREEN);

            showGameplayScreen();
        } catch (Exception e) {
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
        mapPreviewImages.clear();
        statusLabel.setText("");
    }

    private void showGameplayScreen() {
        isGameRunning = true;
        game.setScreen(mapView);
    }

    // --------------------------------------------------
    // Screen switching helpers
    // --------------------------------------------------
    public void showInventoryScreen() {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (inventoryView != null) inventoryView.dispose();
        inventoryView = new InventoryView(game, loginController, this, null, currentPlayer);
        if (lastFrameTexture != null) inventoryView.setBackgroundTexture(lastFrameTexture);
        game.setScreen(inventoryView);
    }

    public void showInventoryForGifting(Npc targetNpc) {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (inventoryView != null) inventoryView.dispose();
        inventoryView = new InventoryView(game, loginController, this, targetNpc, currentPlayer);
        if (lastFrameTexture != null) inventoryView.setBackgroundTexture(lastFrameTexture);
        game.setScreen(inventoryView);
    }

    public void showInventoryForSelling() {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (inventoryView != null) inventoryView.dispose();
        inventoryView = new InventoryView(game, loginController, this, null, currentPlayer);
        inventoryView.setSellingMode(true);
        if (lastFrameTexture != null) inventoryView.setBackgroundTexture(lastFrameTexture);
        game.setScreen(inventoryView);
    }

    public void showBlacksmithView(Store blacksmith) {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (blacksmithView != null) blacksmithView.dispose();
        blacksmithView = new BlacksmithView(game, currentPlayer, this, blacksmith);
        game.setScreen(blacksmithView);
    }

    public void showFishShopView(Store fishShop) {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (fishShopView != null) fishShopView.dispose();
        fishShopView = new FishShopView(game, currentPlayer, this, fishShop);
        game.setScreen(fishShopView);
    }

    public void showSaloonView(Store saloon) {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (saloonView != null) saloonView.dispose();
        saloonView = new SaloonView(game, currentPlayer, this, saloon);
        game.setScreen(saloonView);
    }

    public void showMarinsRanchView(Store marinsRanch) {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (marinsRanchView != null) marinsRanchView.dispose();
        marinsRanchView = new MarinsRanchView(game, currentPlayer, this, marinsRanch);
        game.setScreen(marinsRanchView);
    }

    public void showCarpenterShopView(Store carpenterShop) {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (carpenterShopView != null) carpenterShopView.dispose();
        carpenterShopView = new CarpenterShopView(game, currentPlayer, this, carpenterShop);
        game.setScreen(carpenterShopView);
    }

    public void showKitchenView() {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (kitchenView != null) kitchenView.dispose();
        kitchenView = new KitchenView(game, currentPlayer, this);
        game.setScreen(kitchenView);
    }

    public void showGiftToPlayerView() {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (giftToPlayerView != null) giftToPlayerView.dispose();
        giftToPlayerView = new GiftToPlayerView(game, currentPlayer, this);
        game.setScreen(giftToPlayerView);
    }

    public void showCraftingMenu(User player) {
        if (craftingMenuScreenView != null) craftingMenuScreenView.dispose();
        craftingMenuScreenView = new CraftingMenuScreenView(player, this);
        game.setScreen(craftingMenuScreenView);
    }

    public void showMapView() {
        if (mapView != null) game.setScreen(mapView);
    }

    public void showMessage(String message) {
        if (mapView != null) {
            mapView.showMessage(message, 0.2f);
        }
    }

    public void setLastFrameTexture(Texture texture) {
        this.lastFrameTexture = texture;
    }

    public void showSettingsView() {
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        if (settingsView != null) settingsView.dispose();
        settingsView = new SettingsView(game, this, currentPlayer);
        game.setScreen(settingsView);
    }

    // --------------------------------------------------
    // Render & lifecycle
    // --------------------------------------------------
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isGameRunning) {
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

    public void showFishingMinigameScreen(User user, String currentSeason, int fishingSkill) {
        FishingMiniGame fishingMinigameScreen = new FishingMiniGame(this, user, currentSeason, fishingSkill, user.hasSonar());
        game.setScreen(fishingMinigameScreen);
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }
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
        if (mapView != null) mapView.dispose();
        if (inventoryView != null) inventoryView.dispose();
        if (craftingMenuScreenView != null) craftingMenuScreenView.dispose();
        if (kitchenView != null) kitchenView.dispose();
        if (lastFrameTexture != null) lastFrameTexture.dispose();
        if (miniMapTexture != null) miniMapTexture.dispose(); // NEW
        for (Texture t : mapPreviewTextures) {
            if (t != null) t.dispose();
        }
    }
}
