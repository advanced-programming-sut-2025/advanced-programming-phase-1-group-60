package com.StardewValley.view;

import com.StardewValley.AssetsManager.MapManager;
import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.GamePlayController;
import com.StardewValley.controller.HomeController;
import com.StardewValley.models.*;
import com.StardewValley.models.Tree;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;

public class MapView implements Screen {
    private final GameMap gameMap;
    private final com.StardewValley.models.Game gameInstance;
    private GamePlayController gamePlayController;
    private final GameView gameView;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private MapManager mapManager;
    private static final float DEFAULT_ZOOM = 0.6f;
    private Map<Integer, String> farmOwners = new HashMap<>();
    private float messageTimer = 0f;
    private static final float MESSAGE_DISPLAY_TIME = 5f;
    private Texture fallbackTexture;
    private Texture playerTexture;
    private Vector2 playerPos;
    private Map<User, GamePlayController> playerControllers = new HashMap<>();
    private Map<User, Vector2> playerPositions = new HashMap<>();
    private Map<User, Boolean> playerInVillageState = new HashMap<>();
    private GamePlayController currentPlayerController;
    private static final float WALK_ENERGY_COST = 0.5f; // 0.5 energy per tile moved
    private static final int ENERGY_LIMIT_PER_TURN = 50;
    private float energyUsedThisTurn = 0;
    private Vector2 lastEnergyTile = new Vector2(-1, -1);

    private static final float TILE_SIZE = 32f;
    private int currentFarmIndex = 0;
    private boolean inVillage = false;

    private Stage stage;
    private Dialog travelDialog;
    private Label npcSpeechLabel;
    private boolean speechIsShowing = false;
    private Skin skin;
    private final Runnable onBackToMenu;

    private Dialog npcContextMenu;
    private Dialog friendshipDialog;
    private Dialog questDialog;
    private Npc selectedNpc;
    private Label turnInfoLabel;
    private Label messageLabel;
    private String lastTurnMessage = "";
    private ShapeRenderer shapeRenderer;
    // Player animation fields
    private Animation<TextureRegion> currentPlayerAnimation;
    private float animationTime = 0f;
    private int lastDirection = 0; // 0=down, 1=right, 2=up, 3=left
    private boolean isMoving = false;
    private SelectBox<String> buildingSelectBox;
    private String selectedBuildingType;
    private Pixmap lastFramePixmap;
    private Texture lastFrameTexture;
    private static final String[] ALL_POSSIBLE_BUILDING_NAMES = {
        "Bee_House", "Cheese_Press", "Keg", "Dehydrator", "Charcoal_Kiln",
        "Loom", "Mayonnaise_Machine", "Oil_Maker", "Preserves_Jar",
        "Fish_Smoker", "Furnace"
    };
    private Dialog buildingContextMenu;
    private PlaceableGameBuilding selectedBuilding;

    public MapView(GameMap gameMap, Runnable onBackToMenu, GameView gameView) {
        this.gameMap = gameMap;
        this.gameView = gameView;
        this.gameInstance = com.StardewValley.models.Game.getInstance();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        skin = MenuManager.getInstance().getPixthulhuSkin();
        font.setColor(Color.WHITE);
        this.onBackToMenu = onBackToMenu;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = DEFAULT_ZOOM;
        this.mapManager = MapManager.getInstance();
        currentPlayerAnimation = MapManager.getInstance().getIdleAnimation();
        for (GamePlayController controller : playerControllers.values()) {
            controller.setMapViewControlled(true);
        }

        for (User player : gameInstance.getPlayers()) {
            GamePlayController controller = new GamePlayController(player.getFarm(), player, null, gameInstance);
            playerControllers.put(player, controller);

            int farmId = gameInstance.getSelectedMaps().get(player) - 1;
            Vector2 farmOffset = getFarmTopLeft(farmId);
            Vector2 initialPos = new Vector2(farmOffset.x + (25 * TILE_SIZE), farmOffset.y + (25 * TILE_SIZE));
            playerPositions.put(player, initialPos);

            playerInVillageState.put(player, false);
            player.isInVillage = false;
            player.setPosition(new Tile(25, 25));
        }

        User currentPlayer = gameInstance.getCurrentPlayer();
        currentFarmIndex = gameInstance.getSelectedMaps().get(currentPlayer) - 1;
        playerPos = new Vector2(playerPositions.get(currentPlayer));
        inVillage = playerInVillageState.get(currentPlayer);
        currentPlayerController = playerControllers.get(gameInstance.getCurrentPlayer());
        this.gamePlayController =playerControllers.get(gameInstance.getCurrentPlayer());
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);

        pixmap.setColor(Color.RED);
        pixmap.fill();
        this.playerTexture = new Texture(pixmap);
        pixmap.dispose();

        initializeFarmOwnerMap();

        Vector2 farmCenter = getFarmCenter(currentFarmIndex);
        this.playerPos = new Vector2(farmCenter);
        centerCameraOnPlayer();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        createUI();
    }

    private void handleGameplayMechanics(float delta) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        Vector2 oldPos = new Vector2(playerPos);
        float speed = 200 * delta;
        Vector2 velocity = new Vector2();

        isMoving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x -= 1;
            lastDirection = 3; // Left
            isMoving = true;
            currentPlayerAnimation = MapManager.getInstance().getWalkLeftAnimation();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x += 1;
            lastDirection = 1; // Right
            isMoving = true;
            currentPlayerAnimation = MapManager.getInstance().getWalkRightAnimation();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y += 1;
            lastDirection = 2; // Up
            isMoving = true;
            currentPlayerAnimation = MapManager.getInstance().getWalkUpAnimation();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y -= 1;
            lastDirection = 0; // Down
            isMoving = true;
            currentPlayerAnimation = MapManager.getInstance().getWalkDownAnimation();
        }
        if (!isMoving) {
            currentPlayerAnimation = MapManager.getInstance().getIdleAnimation();
        }
        velocity.nor().scl(speed);

        if (velocity.len() > 0) {
            Vector2 newPos = new Vector2(playerPos);

            if (isAreaPassable(newPos.x + velocity.x, newPos.y)) {
                newPos.x += velocity.x;
            }
            if (isAreaPassable(newPos.x, newPos.y + velocity.y)) {
                newPos.y += velocity.y;
            }

            if (!oldPos.equals(newPos)) {
                boolean canMove = true;
                if (!currentPlayer.getEnergy().isUnlimited()) {
                    int currentTileX = (int)(newPos.x / TILE_SIZE);
                    int currentTileY = (int)(newPos.y / TILE_SIZE);
                    int lastTileX = (int)(lastEnergyTile.x);
                    int lastTileY = (int)(lastEnergyTile.y);
                    boolean enteredNewTile = (currentTileX != lastTileX || currentTileY != lastTileY);

                    if (enteredNewTile && energyUsedThisTurn < ENERGY_LIMIT_PER_TURN &&
                        currentPlayer.getEnergy().getCurrentEnergy() > 0) {
                        int energyToConsume = 1;
                        if (currentPlayer.getEnergy().getCurrentEnergy() >= energyToConsume &&
                            energyUsedThisTurn + energyToConsume <= ENERGY_LIMIT_PER_TURN) {
                            currentPlayer.getEnergy().setCurrentEnergy(
                                currentPlayer.getEnergy().getCurrentEnergy() - energyToConsume
                            );
                            energyUsedThisTurn += energyToConsume;
                            lastEnergyTile.set(currentTileX, currentTileY);
                            if (energyUsedThisTurn >= ENERGY_LIMIT_PER_TURN * 0.8f) {
                                lastTurnMessage = "Energy getting low! " + (int)(ENERGY_LIMIT_PER_TURN - energyUsedThisTurn) + " left this turn";
                                messageTimer = MESSAGE_DISPLAY_TIME;
                            }
                        } else {
                            canMove = false;
                            if (energyUsedThisTurn >= ENERGY_LIMIT_PER_TURN) {
                                lastTurnMessage = "Turn energy limit reached! Press K to end turn.";
                            } else {
                                lastTurnMessage = "Not enough energy to move!";
                            }
                            messageTimer = MESSAGE_DISPLAY_TIME;
                        }
                    } else if (enteredNewTile && (energyUsedThisTurn >= ENERGY_LIMIT_PER_TURN ||
                        currentPlayer.getEnergy().getCurrentEnergy() <= 0)) {
                        canMove = false;
                        lastTurnMessage = "Not enough energy to move!";
                        messageTimer = MESSAGE_DISPLAY_TIME;
                    }
                }
                if (canMove) {
                    playerPos.set(newPos);
                }
            }
        }
        animationTime += delta;
        if (messageTimer > 0) {
            messageTimer -= delta;
        }
    }

    private void createUI() {
        createTravelDialog();
        createBackButton();
        createNpcContextMenu();
        createFriendshipDialog();
        createQuestDialog();
        createBuildingContextMenu();
        npcSpeechLabel = new Label("", skin);
        npcSpeechLabel.setWrap(true);
        npcSpeechLabel.setAlignment(Align.center);
        npcSpeechLabel.setVisible(false);
        stage.addActor(npcSpeechLabel);
    }
    private void createQuestDialog() {
        questDialog = new Dialog("Quests", skin);
        questDialog.setModal(true);
        ScrollPane scrollPane = new ScrollPane(null, skin);
        questDialog.getContentTable().add(scrollPane).grow().pad(10);
        questDialog.getButtonTable().add(new TextButton("Close", skin)).pad(10).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                questDialog.hide();
            }
        });
    }

    private void showQuestDialog(Npc npc) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        Table questListTable = new Table(skin);
        questListTable.top().left();

        if (npc.getQuests() == null || npc.getQuests().isEmpty()) {
            questListTable.add("This person has no quests.").pad(20);
        } else {
            for (Quest quest : npc.getQuests()) {
                String questText = "ID " + quest.getId() + ": Bring " + quest.getRequiredItems().getQuantity() + " " + quest.getRequiredItems().getName();
                TextButton questButton = new TextButton(questText, skin);
                User completer = quest.getCompletedBy();
                if (completer != null) {
                    if (completer.equals(currentPlayer)) {
                        questButton.getLabel().setColor(Color.GREEN);
                    } else {
                        questButton.getLabel().setColor(Color.RED);
                    }
                    questButton.setDisabled(true);
                } else {
                    questButton.getLabel().setColor(Color.LIGHT_GRAY);
                    questButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            completeQuest(currentPlayer, npc, quest);
                            questDialog.hide();
                            showQuestDialog(npc);
                        }
                    });
                }
                questListTable.add(questButton).left().pad(5).row();
            }
        }
        ((ScrollPane) questDialog.getContentTable().getCells().first().getActor()).setActor(questListTable);
        questDialog.show(stage);
    }

    private void completeQuest(User user, Npc npc, Quest quest) {
        if (quest.getActivationFriendLevel() > 0 && user.getFriendshipLevelWithNpc(npc) < quest.getActivationFriendLevel()) {
            showResultDialog("Quest not active yet. You need a higher friendship level.");
            return;
        }
        Item requiredItem = quest.getRequiredItems();
        if (requiredItem != null) {
            if (!user.getInventory().hasItem(requiredItem.getName(), requiredItem.getQuantity())) {
                showResultDialog("You don't have the required items: " + requiredItem.getQuantity() + " " + requiredItem.getName());
                return;
            }
            user.getInventory().removeItemByName(requiredItem.getName(), requiredItem.getQuantity());
        }
        Reward reward = quest.getReward();
        if (reward != null) {
            if (reward.getMoney() > 0) {
                user.setMoney(user.getMoney() + reward.getMoney());
            }
            if (reward.getItems() != null) {
                user.getInventory().addItem(reward.getItems());
            }
            if (reward.getFriendshipXp() > 0) {
                user.increaseFriendshipXpsWithNpc(npc, reward.getFriendshipXp());
            }
        }
        quest.complete(user);
        showResultDialog("Quest '" + quest.getId() + "' completed successfully!");
    }

    private void showResultDialog(String message) {
        Dialog dialog = new Dialog("Result", skin);
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    private void createBackButton() {
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().right();
        TextButton backButton = new TextButton("Back to Menu", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBackToMenu.run();
            }
        });
        uiTable.add(backButton).pad(10);
        buildingSelectBox = new SelectBox<>(skin);
        buildingSelectBox.setVisible(true); // Initially hide it
        buildingSelectBox.setSize(200, 30);
        buildingSelectBox.setPosition(Gdx.graphics.getWidth() - buildingSelectBox.getWidth() - 20,
            Gdx.graphics.getHeight() - 50);

        buildingSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedBuildingType = buildingSelectBox.getSelected();
                gamePlayController.setSelectedBuildingType(selectedBuildingType);
                System.out.println("Selected building: " + selectedBuildingType);
            }
        });
        stage.addActor(buildingSelectBox); // Add SelectBox to stage

        // Initialize the selection box with available items
        updateBuildingSelectBoxItems();
        // Set initial selected type if there are available buildings
        if (buildingSelectBox.getItems().size > 0) {
            selectedBuildingType = buildingSelectBox.getItems().first();
            buildingSelectBox.setSelected(selectedBuildingType);
            gamePlayController.setSelectedBuildingType(selectedBuildingType);
        } else {
            selectedBuildingType = null; // No buildings available initially
            gamePlayController.setSelectedBuildingType(null);
        }
        uiTable.add(buildingSelectBox).pad(10);
        stage.addActor(uiTable);
    }

    private void createNpcContextMenu() {
        npcContextMenu = new Dialog("NPC Menu", skin);
        TextButton giftButton = new TextButton("Gift", skin);
        TextButton questButton = new TextButton("Quest", skin);
        TextButton friendshipButton = new TextButton("Friendship", skin);
        npcContextMenu.getContentTable().add(giftButton).row();
        npcContextMenu.getContentTable().add(questButton).row();
        npcContextMenu.getContentTable().add(friendshipButton).row();
        giftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedNpc != null) {
                    gameView.showInventoryForGifting(selectedNpc);
                }
                npcContextMenu.hide();
            }
        });
        questButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedNpc != null) {
                    showQuestDialog(selectedNpc);
                }
                npcContextMenu.hide();
            }
        });
        friendshipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showFriendshipDialog();
                npcContextMenu.hide();
            }
        });
        npcContextMenu.setModal(true);
    }

    private void createFriendshipDialog() {
        friendshipDialog = new Dialog("Friendship", skin);
        friendshipDialog.text("Friendship details will be shown here.");
        friendshipDialog.button("OK");
        friendshipDialog.setModal(true);
    }

    private void showFriendshipDialog() {
        if (selectedNpc != null) {
            User currentPlayer = gameInstance.getCurrentPlayer();
            int friendshipXp = currentPlayer.getFriendshipXpsWithNPCs().getOrDefault(selectedNpc, 0);
            int friendshipLevel = currentPlayer.getFriendshipLevelWithNpc(selectedNpc);
            Label content = new Label("Friendship with " + selectedNpc.getName() + ":\n" +
                "Level: " + friendshipLevel + "\n" +
                "XP: " + friendshipXp, skin);
            friendshipDialog.getContentTable().clear();
            friendshipDialog.getContentTable().add(content);
            friendshipDialog.show(stage);
        }
    }
    private void updateBuildingSelectBoxItems() {
        List<String> availableBuildingNames = new ArrayList<>();
        Set<String> inventoryItemNames = new HashSet<>();

        // Debug: Print current player and if inventory exists
        if (gamePlayController == null || Game.getInstance().getCurrentPlayer() == null || Game.getInstance().getCurrentPlayer().getInventory() == null) {
            System.out.println("DEBUG: GamePlayController, User, or Inventory is null. Cannot update select box.");
            return;
        }
        // Get all item names from the player's inventory
        for (Item item : Game.getInstance().getCurrentPlayer().getInventory().getItems()) {
            inventoryItemNames.add(item.getName()); // Assuming Item has getName()
        }

        // Filter the list of all possible buildings to only include those in inventory
        for (String buildingName : ALL_POSSIBLE_BUILDING_NAMES) {
            if (inventoryItemNames.contains(buildingName)) {
                availableBuildingNames.add(buildingName);
            }
        }

        // Convert List to Array for SelectBox
        String[] itemsArray = availableBuildingNames.toArray(new String[0]);
        buildingSelectBox.setItems(itemsArray); // This is where items are set
        // If no items are available, make sure the select box doesn't display anything invalid
        if (itemsArray.length > 0) {
            // Keep current selection if it's still available, otherwise set to first item
            if (selectedBuildingType == null || !availableBuildingNames.contains(selectedBuildingType)) {
                selectedBuildingType = itemsArray[0];
            }
            buildingSelectBox.setSelected(selectedBuildingType);
            gamePlayController.setSelectedBuildingType(selectedBuildingType);
        } else {
            selectedBuildingType = null;
            buildingSelectBox.setSelected(""); // Clear selection visually
            gamePlayController.setSelectedBuildingType(null);
        }
    }
    private void showNpcSpeech(Npc npc, User user, float npcWorldX, float npcWorldY) {
        if (speechIsShowing) return;
        speechIsShowing = true;
        String prompt = npc.startConversation(user);
        npcSpeechLabel.setText(prompt);
        npcSpeechLabel.pack();
        npcSpeechLabel.setWidth(250);
        npcSpeechLabel.setHeight(npcSpeechLabel.getPrefHeight());
        Vector3 npcScreenPos = camera.project(new Vector3(npcWorldX, npcWorldY, 0));
        npcSpeechLabel.setPosition(
            npcScreenPos.x - npcSpeechLabel.getWidth() / 2f + (TILE_SIZE / 2f),
            npcScreenPos.y + TILE_SIZE
        );
        npcSpeechLabel.setVisible(true);
        npc.recordTalkTime();
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                npcSpeechLabel.setVisible(false);
                speechIsShowing = false;
            }
        }, 3);
    }

    private void createTravelDialog() {
        travelDialog = new Dialog("Travel", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    performTravel();
                }
            }
        };
        Label contentLabel = new Label("", skin);
        contentLabel.setWrap(true);
        contentLabel.setAlignment(Align.center);
        travelDialog.getContentTable().add(contentLabel).width(250).row();
    }

    public Stage getStage() {
        return this.stage;
    }

    private void showTravelDialog(String destination) {
        if (speechIsShowing) return;
        String dialogText;
        if (destination.startsWith("Farm")) {
            int farmNum = Integer.parseInt(destination.split(" ")[1]);
            String ownerName = farmOwners.getOrDefault(farmNum - 1, "Unknown");
            dialogText = "Do you want to travel to " + destination + " (Owner: " + ownerName + ")?";
        } else {
            dialogText = "Do you want to travel to " + destination + "?";
        }
        ((Label) travelDialog.getContentTable().getCells().first().getActor()).setText(dialogText);
        travelDialog.getButtonTable().clearChildren();
        travelDialog.button("Yes", true);
        travelDialog.button("No", false);
        travelDialog.show(stage);
    }

    private void performTravel() {
        if (inVillage) {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            float destX = 0, destY = 0;
            switch (currentFarmIndex) {
                case 0:
                    destX = farmTopLeft.x + (49 * TILE_SIZE);
                    destY = farmTopLeft.y + (0 * TILE_SIZE);
                    break;
                case 1:
                    destX = farmTopLeft.x + (0 * TILE_SIZE);
                    destY = farmTopLeft.y + (0 * TILE_SIZE);
                    break;
                case 2:
                    destX = farmTopLeft.x + (49 * TILE_SIZE);
                    destY = farmTopLeft.y + (49 * TILE_SIZE);
                    break;
                case 3:
                    destX = farmTopLeft.x + (0 * TILE_SIZE);
                    destY = farmTopLeft.y + (49 * TILE_SIZE);
                    break;
            }
            playerPos.set(destX, destY);
            inVillage = false;
        } else {
            float destX = 0, destY = 0;
            switch (currentFarmIndex) {
                case 0:
                    destX = 0 * TILE_SIZE;
                    destY = 19 * TILE_SIZE;
                    break;
                case 1:
                    destX = 19 * TILE_SIZE;
                    destY = 19 * TILE_SIZE;
                    break;
                case 2:
                    destX = 0 * TILE_SIZE;
                    destY = 0 * TILE_SIZE;
                    break;
                case 3:
                    destX = 19 * TILE_SIZE;
                    destY = 0 * TILE_SIZE;
                    break;
            }
            playerPos.set(destX, destY);
            inVillage = true;
        }
        centerCameraOnPlayer();
    }

    private void initializeFarmOwnerMap() {
        try {
            List<User> players = gameInstance.getPlayers();
            for (User player : players) {
                int mapId = gameInstance.getMapSelection(player);
                if (mapId > 0) {
                    farmOwners.put(mapId - 1, player.getUsername());
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing farm owners: " + e.getMessage());
        }
    }

    private Vector2 getFarmCenter(int farmIndex) {
        Vector2 topLeft = getFarmTopLeft(farmIndex);
        return new Vector2(topLeft.x + (FarmTemplate.WIDTH / 2f) * TILE_SIZE,
            topLeft.y + (FarmTemplate.HEIGHT / 2f) * TILE_SIZE);
    }

    private void centerCameraOnPlayer() {
        if (inVillage) {
            // Village - normal camera following
            camera.position.set(playerPos.x, playerPos.y, 0);
        } else {
            // Farm - constrain camera but allow viewing water border
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            float farmWidth = FarmTemplate.WIDTH * TILE_SIZE;
            float farmHeight = FarmTemplate.HEIGHT * TILE_SIZE;

            // Add buffer to show water beyond farm borders (5-10 tiles)
            float waterBuffer = 8 * TILE_SIZE; // Show 8 tiles of water beyond farm

            // Calculate camera bounds (half viewport size)
            float halfViewWidth = (Gdx.graphics.getWidth() * camera.zoom) / 2f;
            float halfViewHeight = (Gdx.graphics.getHeight() * camera.zoom) / 2f;

            // Calculate constrained camera position with water buffer
            float targetX = playerPos.x;
            float targetY = playerPos.y;

            // Constrain X within farm bounds + water buffer
            float minX = farmTopLeft.x - waterBuffer + halfViewWidth;
            float maxX = farmTopLeft.x + farmWidth + waterBuffer - halfViewWidth;
            targetX = Math.max(minX, Math.min(maxX, targetX));

            // Constrain Y within farm bounds + water buffer
            float minY = farmTopLeft.y - waterBuffer + halfViewHeight;
            float maxY = farmTopLeft.y + farmHeight + waterBuffer - halfViewHeight;
            targetY = Math.max(minY, Math.min(maxY, targetY));

            camera.position.set(targetX, targetY, 0);
        }
        camera.update();
    }

    public void setCurrentFarmIndex(int index) {
        if (index >= 0 && index <= 3) {
            currentFarmIndex = index;
            inVillage = false;
            gameMap.setActiveFarm(index);
            playerPos.set(getFarmCenter(index));
            centerCameraOnPlayer();
        }
    }
    private void renderPlayer() {
        TextureRegion currentFrame = currentPlayerAnimation.getKeyFrame(animationTime);

        // Make character taller - 1.5x height ratio
        float playerWidth = TILE_SIZE * 0.8f;
        float playerHeight = TILE_SIZE * 1.5f;

        // Adjust Y position so character stands on ground properly
        float adjustedY = playerPos.y - (playerHeight - TILE_SIZE) * 0.5f;

        batch.draw(currentFrame, playerPos.x, adjustedY, playerWidth, playerHeight);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput(delta);
        centerCameraOnPlayer();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderMap();
        renderPlayer();
        renderBuildModeHighlight();
        renderUI();
        updateBuildingSelectBoxItems();
        batch.end();

        stage.act(delta);
        stage.draw();

        if (stage.getBatch() != null) {
            stage.getBatch().setColor(Color.WHITE);
        }
    }
    private void renderMap() {
        int width, height;
        List<Vector2> npcChatIconPositions = new ArrayList<>();
        List<TreeRenderData> treesToRender = new ArrayList<>();
        List<StructureRenderData> structuresToRender = new ArrayList<>();

        if (inVillage) {
            width = 20;
            height = 20;
        }
        else {
            width = FarmTemplate.WIDTH;
            height = FarmTemplate.HEIGHT;
        }

        Vector2 renderOffset = inVillage ? new Vector2(0, 0) : getFarmTopLeft(currentFarmIndex);
        int waterBorderSize = 100; // How many tiles of water around the area
        int totalWidth = width + (waterBorderSize * 2);
        int totalHeight = height + (waterBorderSize * 2);

        for (int y = -waterBorderSize; y < height + waterBorderSize; y++) {
            for (int x = -waterBorderSize; x < width + waterBorderSize; x++) {
                float posX = renderOffset.x + (x * TILE_SIZE);
                float posY = renderOffset.y + (y * TILE_SIZE);
                batch.draw(mapManager.getWaterTexture(), posX, posY, TILE_SIZE, TILE_SIZE);
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int finalX = x;
                final int finalY = y;
                float posX = renderOffset.x + (x * TILE_SIZE);
                float posY = renderOffset.y + (y * TILE_SIZE);
                batch.draw(mapManager.getGrassTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                Tile tile;
                if (inVillage) {
                    tile = gameMap.getVillage().getTile(x, 20 - 1 - y);
                } else {
                    tile = gameMap.getFarm(currentFarmIndex).getTile(x, FarmTemplate.HEIGHT - 1 - y);
                }
                if (tile == null) continue;

                tile.getStaticElement().ifPresent(element -> {
                    if (element instanceof Cabin) {
                        if (isStructureOrigin(finalX, finalY, element, width, height)) {
                            structuresToRender.add(new StructureRenderData(
                                mapManager.getCabinTexture(), posX, posY, 4, 4
                            ));
                        }
                    } else if (element instanceof Greenhouse) {
                        if (isStructureOrigin(finalX, finalY, element, width, height)) {
                            structuresToRender.add(new StructureRenderData(
                                mapManager.getGreenhouseTexture(), posX, posY, 5, 6
                            ));
                        }
                    } else if (element instanceof Lake) {
                        batch.draw(mapManager.getWaterTexture(), posX, posY, TILE_SIZE, TILE_SIZE);
                    } else if (element instanceof Quarry) {
                        batch.draw(mapManager.getQuarryTexture(), posX, posY, TILE_SIZE, TILE_SIZE);
                    } else if (element instanceof Npc) {
                        Npc npc = (Npc) element;
                        Texture texture = mapManager.getNpcTexture(npc.getName());
                        if (texture != null) {
                            batch.draw(texture, posX, posY, TILE_SIZE, TILE_SIZE);
                            if (npc.isDialogueReady()) {
                                npcChatIconPositions.add(new Vector2(posX, posY));
                            }
                        }
                    } else if (element instanceof Store) {
                        Texture texture = mapManager.getStoreTexture();
                        if (texture != null) {
                            batch.draw(texture, posX, posY, TILE_SIZE, TILE_SIZE);
                        }
                    }
                    else if (element instanceof PlaceableGameBuilding) {
                        PlaceableGameBuilding building = (PlaceableGameBuilding) element;
                        // Assuming MapManager has a method like getBuildingTexture that takes the building's name
                        Texture buildingTexture = mapManager.getBuildingTexture(building.getName());
                        if (buildingTexture != null) {
                            // Draw the building using its actual width and height (in tiles, scaled by TILE_SIZE)
                            float buildingWidth = building.getWidth() * TILE_SIZE;
                            float buildingHeight = building.getHeight() * TILE_SIZE;
                            batch.draw(buildingTexture, posX, posY, buildingWidth, buildingHeight);
                        } else {
                            // Fallback: If texture is not found, draw a placeholder (e.g., magenta square)
                            batch.setColor(Color.MAGENTA);
                            batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                            batch.setColor(Color.WHITE); // Reset color
                        }
                    }
                    else if (element instanceof SellingBin) {
                        Texture texture = mapManager.getSellingBinTexture();
                        if (texture != null) {
                            batch.draw(texture, posX, posY, TILE_SIZE, TILE_SIZE);
                        }
                    } else {
                        batch.setColor(Color.BROWN);
                        batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                        batch.setColor(Color.WHITE);
                    }
                });

                tile.getRandomElement().ifPresent(element -> {
                    if (element instanceof Stone) {
                        batch.draw(mapManager.getStoneTile(((Stone) element).getStoneVariant()), posX, posY, TILE_SIZE, TILE_SIZE);
                    } else if (element instanceof Tree) {
                        Tree tree = (Tree) element;
                        Texture treeTexture = mapManager.getTreeTexture(tree.getImagePath());
                        if (treeTexture != null) {
                            treesToRender.add(new TreeRenderData(treeTexture, posX, posY, false));
                        }
                    }
                    else if (element instanceof ForagingTree) {
                        ForagingTree foragingTree = (ForagingTree) element;
                        Texture foragingTreeTexture = mapManager.getForagingTreeTexture(foragingTree.getImagePath());
                        if (foragingTreeTexture != null) {
                            treesToRender.add(new TreeRenderData(foragingTreeTexture, posX, posY, true));
                        }
                    }
                    else if (element instanceof ForagingMineral) {
                        ForagingMineral mineral = (ForagingMineral) element;
                        Texture texture = mapManager.getForagingMineralTexture(mineral.getImagePath());
                        float mineralSize = TILE_SIZE * 0.6f;
                        float offsetX = (TILE_SIZE - mineralSize) / 2f;
                        float offsetY = (TILE_SIZE - mineralSize) / 2f;
                        batch.draw(texture, posX + offsetX, posY + offsetY, mineralSize, mineralSize);
                    }
                    else if (element instanceof ForagingCrop) {
                        ForagingCrop crop = (ForagingCrop) element;
                        Texture texture = mapManager.getForagingCropTexture(crop.getImagePath());
                        float cropSize = TILE_SIZE * 0.7f;
                        float offsetX = (TILE_SIZE - cropSize) / 2f;
                        float offsetY = (TILE_SIZE - cropSize) / 2f;
                        batch.draw(texture, posX + offsetX, posY + offsetY, cropSize, cropSize);
                    }
                });
            }
        }

        for (StructureRenderData structure : structuresToRender) {
            renderStructure(structure.texture, structure.posX, structure.posY, structure.width, structure.height);
        }

        for (TreeRenderData tree : treesToRender) {
            float treeWidth = TILE_SIZE * 1.8f;
            float treeHeight = TILE_SIZE * 2.5f;
            float treeX = tree.posX - (treeWidth - TILE_SIZE) / 2f;
            float treeY = tree.posY;
            batch.draw(tree.texture, treeX, treeY, treeWidth, treeHeight);
        }

        for (Vector2 pos : npcChatIconPositions) {
            batch.draw(mapManager.getChatIconTexture(), pos.x + TILE_SIZE / 4, pos.y + TILE_SIZE, TILE_SIZE / 2, TILE_SIZE / 2);
        }

        // --- NEW LOGIC: DRAW HIGHLIGHTS FOR BUILD MODE AND GHOST IMAGE ---
        // Make sure gamePlayController and gameMap are correctly initialized in MapView's constructor
        if (gamePlayController != null && gameMap != null &&
            gamePlayController.getCurrentGameState() == GamePlayController.GameState.BUILD_MODE) {

            StaticElement selectedBlueprint = gamePlayController.getSelectedBuildingBlueprint();

            // 1. Draw highlights for valid placement spots
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile tile;
                    // Get the correct tile based on current view (village or farm)
                    if (inVillage) {
                        tile = gameMap.getVillage().getTile(x, 20 - 1 - y); // Adjust for origin (bottom-left vs top-left) if needed
                    } else {
                        tile = gameMap.getFarm(currentFarmIndex).getTile(x, FarmTemplate.HEIGHT - 1 - y); // Adjust for origin
                    }

                    if (tile != null && tile.isAvailableForBuilding()) { // Use the new method from Tile class
                        float posX = renderOffset.x + (x * TILE_SIZE);
                        float posY = renderOffset.y + (y * TILE_SIZE);

                        batch.setColor(new Color(0f, 1f, 0f, 0.4f)); // Semi-transparent green highlight
                        // Assuming mapManager.getTexture("pixel_white") provides a 1x1 white pixel texture
                        // which can be colored by batch.setColor()
                        batch.draw(mapManager.getTexture("pixel_white"), posX, posY, TILE_SIZE, TILE_SIZE);
                        batch.setColor(Color.WHITE); // Reset batch color
                    }
                }
            }

            // 2. Draw a "ghost" image of the selected building following the mouse cursor
            if (selectedBlueprint != null) {
                // Convert mouse screen coordinates to world coordinates
                Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

                // Adjust for renderOffset to get the correct map-relative position for ghost drawing
                float ghostDrawX = worldCoordinates.x - renderOffset.x;
                float ghostDrawY = worldCoordinates.y - renderOffset.y;

                // Snap the ghost image to the tile grid (optional, but usually desired for building)
                int snappedTileX = (int) (ghostDrawX / TILE_SIZE);
                int snappedTileY = (int) (ghostDrawY / TILE_SIZE);

                // Calculate actual drawing position for the ghost
                float finalGhostPosX = renderOffset.x + (snappedTileX * TILE_SIZE);
                float finalGhostPosY = renderOffset.y + (snappedTileY * TILE_SIZE);

                // Get the texture for the blueprint. Assuming StaticElement has a way to get its texture path.
                // If your StaticElement/Building doesn't have `getTexturePath()`, you'll need to adapt this.
                Texture blueprintTexture = null;
                // Example: try to get texture based on type
                if (selectedBlueprint instanceof Cabin) {
                    blueprintTexture = mapManager.getCabinTexture();
                } else if (selectedBlueprint instanceof Greenhouse) {
                    blueprintTexture = mapManager.getGreenhouseTexture();
                } // ... add other building types here

                if (blueprintTexture != null) {
                    batch.setColor(new Color(1f, 1f, 1f, 0.6f)); // Semi-transparent ghost effect
                    batch.draw(blueprintTexture, finalGhostPosX, finalGhostPosY, TILE_SIZE, TILE_SIZE); // Assuming 1x1 size for simplicity
                    batch.setColor(Color.WHITE); // Reset color for subsequent draws
                }
            }
        }
        // --- END NEW LOGIC ---
    }

    private boolean isStructureOrigin(int x, int y, Object element, int mapWidth, int mapHeight) {
        if (element instanceof Cabin) {
            return isTopLeftOfStructure(x, y, 4, 4, mapWidth, mapHeight, Cabin.class);
        } else if (element instanceof Greenhouse) {
            return isTopLeftOfStructure(x, y, 5, 6, mapWidth, mapHeight, Greenhouse.class);
        }
        return false;
    }

    private boolean isTopLeftOfStructure(int x, int y, int structWidth, int structHeight,
                                         int mapWidth, int mapHeight, Class<?> structureType) {
        for (int dy = 0; dy < structHeight; dy++) {
            for (int dx = 0; dx < structWidth; dx++) {
                int checkX = x + dx;
                int checkY = y + dy;
                if (checkX >= mapWidth || checkY >= mapHeight) return false;
                Tile checkTile;
                if (inVillage) {
                    checkTile = gameMap.getVillage().getTile(checkX, 20 - 1 - checkY);
                } else {
                    checkTile = gameMap.getFarm(currentFarmIndex).getTile(checkX, FarmTemplate.HEIGHT - 1 - checkY);
                }
                if (checkTile == null || !checkTile.getStaticElement().isPresent() ||
                    !structureType.isInstance(checkTile.getStaticElement().get())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static class StructureRenderData {
        final Texture texture;
        final float posX;
        final float posY;
        final int width;
        final int height;

        StructureRenderData(Texture texture, float posX, float posY, int width, int height) {
            this.texture = texture;
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
        }
    }

    private void renderStructure(Texture texture, float startX, float startY, int width, int height) {
        float structureWidth = width * TILE_SIZE;
        float structureHeight = height * TILE_SIZE;
        batch.draw(texture, startX, startY, structureWidth, structureHeight);
    }

    private static class TreeRenderData {
        final Texture texture;
        final float posX;
        final float posY;
        final boolean isForagingTree;

        TreeRenderData(Texture texture, float posX, float posY, boolean isForagingTree) {
            this.texture = texture;
            this.posX = posX;
            this.posY = posY;
            this.isForagingTree = isForagingTree;
        }
    }

    private boolean isMyTurn() {
        return true; // Simplified for now
    }

    private void renderUI() {
        float textX = camera.position.x - Gdx.graphics.getWidth() / 2f * camera.zoom + 10;
        float textY = camera.position.y + Gdx.graphics.getHeight() / 2f * camera.zoom - 10;

        String ownerName = farmOwners.getOrDefault(currentFarmIndex, "Unknown");
        font.draw(batch, "Location: " + (inVillage ? "Village" : "Farm " + (currentFarmIndex + 1) + " - Owner: " + ownerName), textX, textY);
        font.draw(batch, "WASD: Move | +/-: Zoom | K: Next Turn", textX, textY - 20);
        font.draw(batch, "1-4: Switch Farm | I: Inventory | B: Crafting", textX, textY - 40);

        User currentPlayer = gameInstance.getCurrentPlayer();
        TimeSystem timeSystem = TimeSystem.getInstance();
        String turnInfo = String.format("Player: %s | Day: %d | Time: %02d:00 | Energy: %d/%d (Used: %.1f/%.0f) | Money: %d",
            currentPlayer.getUsername(),
            timeSystem.getCurrentDay(),
            timeSystem.getCurrentHour(),
            currentPlayer.getEnergy().getCurrentEnergy(),
            currentPlayer.getEnergy().getMaxEnergy(),
            energyUsedThisTurn,
            (float)ENERGY_LIMIT_PER_TURN,
            currentPlayer.getMoney()
        );
        font.draw(batch, turnInfo, textX, textY - 60);

        String farmStats = String.format("Farm: %s | Location: %s | Animals: %d | Spouse: %s",
            "Farm" + (gameInstance.getSelectedMaps().get(currentPlayer)),
            currentPlayer.isInVillage ? "Village" : "Farm",
            currentPlayer.getPutAnimals().size(),
            currentPlayer.getSpouse() != null ? currentPlayer.getSpouse().getNickname() : "None"
        );
        font.draw(batch, farmStats, textX, textY - 80);

        if (messageTimer > 0) {
            font.draw(batch, lastTurnMessage, textX, textY - 100);
        }
    }

    private void handleInput(float delta) {
        if (speechIsShowing) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) speechIsShowing = false;
            return;
        }

        User currentPlayer = Game.getInstance().getCurrentPlayer();
        handleGameplayMechanics(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            if (isMyTurn()) {
                playerPositions.put(currentPlayer, new Vector2(playerPos));
                playerInVillageState.put(currentPlayer, inVillage);
                if (inVillage) {
                    currentPlayer.setPosition(new Tile((int)(playerPos.x / TILE_SIZE), (int)(playerPos.y / TILE_SIZE)));
                } else {
                    Vector2 farmOffset = getFarmTopLeft(currentFarmIndex);
                    int localX = (int)((playerPos.x - farmOffset.x) / TILE_SIZE);
                    int localY = (int)((playerPos.y - farmOffset.y) / TILE_SIZE);
                    currentPlayer.setPosition(new Tile(localX, localY));
                }
                currentPlayer.isInVillage = inVillage;
                User previousPlayer = gameInstance.getCurrentPlayer();
                gameInstance.nextTurn();
                User newCurrentPlayer = gameInstance.getCurrentPlayer();
                boolean completedRound = false;
                List<User> players = gameInstance.getPlayers();
                if (players.indexOf(newCurrentPlayer) == 0 && !previousPlayer.equals(newCurrentPlayer)) {
                    completedRound = true;
                }
                if (completedRound) {
                    TimeSystem.getInstance().advanceTime(1);
                    if (TimeSystem.getInstance().getCurrentHour() >= 12) {
                        for (GamePlayController controller : playerControllers.values()) {
                            controller.initializeNextDay();
                        }
                    }
                }
                currentPlayerController = playerControllers.get(newCurrentPlayer);
                currentFarmIndex = gameInstance.getSelectedMaps().get(newCurrentPlayer) - 1;
                playerPos = new Vector2(playerPositions.get(newCurrentPlayer));
                inVillage = playerInVillageState.get(newCurrentPlayer);
                centerCameraOnPlayer();
                lastTurnMessage = "Now playing as: " + newCurrentPlayer.getUsername();
                messageTimer = MESSAGE_DISPLAY_TIME;
                energyUsedThisTurn = 0;
                lastEnergyTile.set(-1, -1);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            int playerTileX, playerTileY;
            if (inVillage) {
                playerTileX = (int) (playerPos.x / TILE_SIZE);
                playerTileY = (int) (playerPos.y / TILE_SIZE);
            } else {
                Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
                playerTileX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
                playerTileY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);
            }

            // Check adjacent tiles for a Cabin
            boolean nextToCabin = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int checkX = playerTileX + dx;
                    int checkY = playerTileY + dy;

                    Tile adjacentTile;
                    if(inVillage){
                        adjacentTile = gameMap.getVillage().getTile(checkX, 20 - 1 - checkY);
                    } else {
                        adjacentTile = gameMap.getFarm(currentFarmIndex).getTile(checkX, FarmTemplate.HEIGHT - 1 - checkY);
                    }

                    if (adjacentTile != null && adjacentTile.getStaticElement().isPresent() && adjacentTile.getStaticElement().get() instanceof Cabin) {
                        nextToCabin = true;
                        break;
                    }
                }
                if (nextToCabin) break;
            }

            if (nextToCabin) {
                gameView.showKitchenView();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            boolean isUnlimited = !currentPlayer.getEnergy().isUnlimited();
            currentPlayer.getEnergy().setUnlimited(isUnlimited);
            if (isUnlimited) {
                lastTurnMessage = "Infinite energy activated!";
                currentPlayer.getEnergy().setCurrentEnergy(currentPlayer.getEnergy().getMaxEnergy());
            } else {
                lastTurnMessage = "Infinite energy deactivated!";
            }
            messageTimer = MESSAGE_DISPLAY_TIME;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            captureCurrentFrame(); // Take screenshot
            gameView.setLastFrameTexture(lastFrameTexture); // Pass to GameView
            gameView.showInventoryScreen();
            return;// Use existing method
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)){
            Item coal = new Item("Coal",5);
            currentPlayer.getInventory().addItem(coal);
            currentPlayer.getInventory().addItem(new Item("Copper_Ore",10));
            HomeController.unlockRecipesByLevel("mining",1);
            HomeController.unlockRecipesByLevel("farming",1);
            HomeController.unlockRecipesByLevel("foraging",1);
            gameView.showCraftingMenu(currentPlayer);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            currentPlayer.getInventory().addItem(new Item("Keg",10));
            if (gamePlayController.isInBuildMode()) {
                gamePlayController.exitBuildMode();
            } else {
                if(selectedBuildingType == null){
                    gamePlayController.exitBuildMode();
                }
                else{
                    gamePlayController.enterBuildMode(gamePlayController.getBuildingDefinition(selectedBuildingType)); // Example blueprint
                }
            }
        }
        // Inside your handleInput(float delta) method:

        if (gamePlayController.isInBuildMode() && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos); // Converts screen coords to world coords

            // --- NEW: Get the current farm's render offset ---
            Vector2 renderOffset = inVillage ? new Vector2(0, 0) : getFarmTopLeft(currentFarmIndex);

            // --- NEW: Adjust touchPos relative to the farm's origin ---
            float adjustedTouchX = touchPos.x - renderOffset.x;
            float adjustedTouchY = touchPos.y - renderOffset.y;

            // Calculate tile coordinates based on adjusted position
            int tileX = (int) (adjustedTouchX / TILE_SIZE);
            int tileY = (int) (adjustedTouchY / TILE_SIZE); // This is the Y from the bottom of the farm's local coords

            // Invert tileY for array access (if array row 0 is at the top of the map)
            int finalTileY;
            // Check if the calculated tileY is within the positive range of map height
            if (tileY >= 0 && tileY < FarmTemplate.HEIGHT) {
                finalTileY = FarmTemplate.HEIGHT - 1 - tileY;
            } else {
                // If it's outside this range, it's genuinely out of bounds relative to the farm map
                finalTileY = -1; // Or throw an error/log a message in attemptToPlaceBuilding
            }
            // Pass the calculated tileX and finalTileY to the controller
            gamePlayController.attemptToPlaceBuilding(tileX, finalTileY);

            return; // Consume the right-click event
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            System.out.println("DEBUG: Left mouse button clicked.");
            // Corrected UI hit detection: Check if any UI element on the stage was clicked
            Actor hitActor = stage.hit(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), true);

            if (hitActor == null) { // If no UI element was hit, proceed with map click
                System.out.println("DEBUG: Click was on the map (no UI element hit).");
                Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                System.out.println("DEBUG: World coordinates of click: " + worldCoordinates.x + ", " + worldCoordinates.y);

                // Get the farm associated with the current player
                Farm currentPlayerFarm = gamePlayController.getUser().getFarm();
                if (currentPlayerFarm != null) {
                    // Adjust world coordinates based on the current farm's top-left offset
                    Vector2 farmOffset = getFarmTopLeft(currentFarmIndex);
                    System.out.println("DEBUG: Current farm offset: " + farmOffset.x + ", " + farmOffset.y);

                    float adjustedMouseX = worldCoordinates.x - farmOffset.x;
                    float adjustedMouseY = worldCoordinates.y - farmOffset.y;
                    System.out.println("DEBUG: Adjusted mouse coordinates (relative to farm): " + adjustedMouseX + ", " + adjustedMouseY);

                    int tileX = (int) (adjustedMouseX / TILE_SIZE);
                    int tileY = (int) (adjustedMouseY / TILE_SIZE);
                    System.out.println("DEBUG: Clicked tile (relative to farm): " + tileX + ", " + tileY);

                    // Ensure tile coordinates are within the current farm's bounds
                    if (tileX >= 0 && tileX < FarmTemplate.WIDTH &&
                        tileY >= 0 && tileY < FarmTemplate.HEIGHT) {

                        // Get the Tile object from the current player's farm
                        // Assuming [row][col] or [y][x] for getTiles()
                        Tile clickedTile = currentPlayerFarm.getTiles()[tileY][tileX];
                        System.out.println("DEBUG: Tile at (" + tileX + ", " + tileY + ") type: " + clickedTile.getType());

                        // Check if the tile contains a PlaceableGameBuilding
                        Optional<StaticElement> staticElement = clickedTile.getStaticElement();
                        if (staticElement.isPresent()) {
                            System.out.println("DEBUG: Static element present on tile: " + staticElement.get().getName() + " (Type: " + staticElement.get().getClass().getSimpleName() + ")");
                            if (staticElement.get() instanceof PlaceableGameBuilding) {
                                PlaceableGameBuilding clickedBuilding = (PlaceableGameBuilding) staticElement.get();
                                System.out.println("DEBUG: PlaceableGameBuilding clicked: " + clickedBuilding.getName());
                                showBuildingContextMenu(clickedBuilding); // Open the building menu
                            } else {
                                System.out.println("DEBUG: Static element is not a PlaceableGameBuilding.");
                            }
                        } else {
                            System.out.println("DEBUG: No static element on clicked tile.");
                        }
                    } else {
                        System.out.println("DEBUG: Clicked tile coordinates are outside farm bounds.");
                    }
                } else {
                    System.out.println("DEBUG: Current player's farm is null. Cannot detect building clicks.");
                }
            } else {
                System.out.println("DEBUG: Click was on a UI element: " + hitActor.getName() + " (Type: " + hitActor.getClass().getSimpleName() + ")");
            }
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);
            int clickedTileX, playerTileX, clickedTileY, playerTileY;

            if (inVillage) {
                clickedTileX = (int) (clickPos.x / TILE_SIZE);
                clickedTileY = (int) (clickPos.y / TILE_SIZE);
                playerTileX = (int) (playerPos.x / TILE_SIZE);
                playerTileY = (int) (playerPos.y / TILE_SIZE);
            } else {
                Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
                clickedTileX = (int) ((clickPos.x - farmTopLeft.x) / TILE_SIZE);
                clickedTileY = (int) ((clickPos.y - farmTopLeft.y) / TILE_SIZE);
                playerTileX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
                playerTileY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);
            }

            Tile clickedTile;
            if (inVillage) {
                if (clickedTileX >= 0 && clickedTileX < 20 && clickedTileY >= 0 && clickedTileY < 20) {
                    clickedTile = gameMap.getVillage().getTile(clickedTileX, 20 - 1 - clickedTileY);
                } else {
                    clickedTile = null;
                }
            } else {
                if (clickedTileX >= 0 && clickedTileX < FarmTemplate.WIDTH &&
                    clickedTileY >= 0 && clickedTileY < FarmTemplate.HEIGHT) {
                    clickedTile = gameMap.getFarm(currentFarmIndex).getTile(clickedTileX, FarmTemplate.HEIGHT - 1 - clickedTileY);
                } else {
                    clickedTile = null;
                }
            }

            if (clickedTile != null && clickedTile.getStaticElement().isPresent()) {
                StaticElement element = clickedTile.getStaticElement().get();
                if (element instanceof Npc) {
                    Npc npc = (Npc) element;
                    if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                        if (npc.isDialogueReady()) {
                            float npcWorldX = clickPos.x;
                            float npcWorldY = clickPos.y;
                            showNpcSpeech(npc, gameInstance.getCurrentPlayer(), npcWorldX, npcWorldY);
                        }
                    }
                } else if (element instanceof SellingBin) {
                    if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                        gameView.showInventoryForSelling();
                    }
                } else if (element instanceof Store) {
                    Store store = (Store) element;
                    if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                        if (store.isOpen()) {
                            if (store.getName().equalsIgnoreCase("Blacksmith")) {
                                gameView.showBlacksmithView(store);
                            } else if (store.getName().equalsIgnoreCase("Fish Shop")) {
                                gameView.showFishShopView(store);
                            } else if (store.getName().equalsIgnoreCase("The Stardrop Saloon")) {
                                gameView.showSaloonView(store);
                            }
                        } else {
                            showResultDialog("The " + store.getName() + " is closed.");
                        }
                    }
                }
            }
        }


        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);
            int clickedTileX, playerTileX, clickedTileY, playerTileY;
            if (inVillage) {
                clickedTileX = (int) (clickPos.x / TILE_SIZE);
                clickedTileY = (int) (clickPos.y / TILE_SIZE);
                playerTileX = (int) (playerPos.x / TILE_SIZE);
                playerTileY = (int) (playerPos.y / TILE_SIZE);
            } else {
                Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
                clickedTileX = (int) ((clickPos.x - farmTopLeft.x) / TILE_SIZE);
                clickedTileY = (int) ((clickPos.y - farmTopLeft.y) / TILE_SIZE);
                playerTileX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
                playerTileY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);
            }
            Tile clickedTile;
            if (inVillage) {
                if (clickedTileX >= 0 && clickedTileX < 20 && clickedTileY >= 0 && clickedTileY < 20) {
                    clickedTile = gameMap.getVillage().getTile(clickedTileX, 20 - 1 - clickedTileY);
                } else {
                    clickedTile = null;
                }
            } else {
                if (clickedTileX >= 0 && clickedTileX < FarmTemplate.WIDTH &&
                    clickedTileY >= 0 && clickedTileY < FarmTemplate.HEIGHT) {
                    clickedTile = gameMap.getFarm(currentFarmIndex).getTile(clickedTileX, FarmTemplate.HEIGHT - 1 - clickedTileY);
                } else {
                    clickedTile = null;
                }
            }
            if (clickedTile != null && clickedTile.getStaticElement().isPresent() &&
                clickedTile.getStaticElement().get() instanceof Npc) {
                Npc npc = (Npc) clickedTile.getStaticElement().get();
                if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                    selectedNpc = npc;
                    npcContextMenu.show(stage);
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS))
            camera.zoom -= 0.02f;
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) camera.zoom += 0.02f;
        camera.zoom = Math.max(0.3f, Math.min(2f, camera.zoom));
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) setCurrentFarmIndex(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) setCurrentFarmIndex(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) setCurrentFarmIndex(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) setCurrentFarmIndex(3);

        checkTravel();
    }

    private boolean isAreaPassable(float worldX, float worldY) {
        // Check all four corners with a smaller hitbox to prevent clipping
        float hitboxInset = TILE_SIZE * 0.15f; // Increased inset for better boundary detection
        float hitboxX = worldX + hitboxInset;
        float hitboxY = worldY + hitboxInset;
        float hitboxWidth = TILE_SIZE - (2 * hitboxInset);
        float hitboxHeight = TILE_SIZE - (2 * hitboxInset);

        // If ANY corner is not passable, the whole area is not passable
        boolean bottomLeft = isTilePassable(hitboxX, hitboxY);
        boolean bottomRight = isTilePassable(hitboxX + hitboxWidth, hitboxY);
        boolean topLeft = isTilePassable(hitboxX, hitboxY + hitboxHeight);
        boolean topRight = isTilePassable(hitboxX + hitboxWidth, hitboxY + hitboxHeight);

        // Additional center point check for small objects
        boolean center = isTilePassable(worldX + TILE_SIZE/2, worldY + TILE_SIZE/2);

        return bottomLeft && bottomRight && topLeft && topRight && center;
    }

    private boolean isTilePassable(float worldX, float worldY) {
        // First, absolute boundary check without even checking tiles
        if (inVillage) {
            // Village boundaries (0,0 to 19,19)
            if (worldX < 0 || worldX >= 20 * TILE_SIZE ||
                worldY < 0 || worldY >= 20 * TILE_SIZE) {
                return false;
            }
        } else {
            // Farm boundaries based on current farm
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            float farmWidth = FarmTemplate.WIDTH * TILE_SIZE;
            float farmHeight = FarmTemplate.HEIGHT * TILE_SIZE;

            // Adjust hitbox boundaries - extend top/bottom detection further
            float topBoundaryAdjustment = TILE_SIZE * 0.8f; // Extend top detection
            float bottomBoundaryAdjustment = TILE_SIZE * 0.00001f; // Extend bottom detection

            if (worldX < farmTopLeft.x || worldX >= farmTopLeft.x + farmWidth ||
                worldY < farmTopLeft.y - bottomBoundaryAdjustment ||
                worldY >= farmTopLeft.y + farmHeight + topBoundaryAdjustment) {
                return false;
            }
        }

        // If we pass the hard boundary check, proceed with normal tile check
        Tile tile;
        if (inVillage) {
            int tileX = (int) (worldX / TILE_SIZE);
            int tileY = (int) (worldY / TILE_SIZE);
            tile = gameMap.getVillage().getTile(tileX, 20 - 1 - tileY);
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            int localX = (int) ((worldX - farmTopLeft.x) / TILE_SIZE);
            int localY = (int) ((worldY - farmTopLeft.y) / TILE_SIZE);
            tile = gameMap.getFarm(currentFarmIndex).getTile(localX, FarmTemplate.HEIGHT - 1 - localY);
        }

        if (tile == null) return false;
        if (tile.getStaticElement().isPresent()) {
            Object element = tile.getStaticElement().get();
            if (element instanceof Cabin || element instanceof Greenhouse || element instanceof Lake) {
                return false;
            }
        }
        return tile.isPassable();
    }

    private void checkTravel() {
        if (speechIsShowing) return;
        if (inVillage) {
            int playerTileX = (int) (playerPos.x / TILE_SIZE);
            int playerTileY = (int) (playerPos.y / TILE_SIZE);
            if (playerTileX == 0 && playerTileY == 19) {
                currentFarmIndex = 0;
                showTravelDialog("Farm 1");
            } else if (playerTileX == 19 && playerTileY == 19) {
                currentFarmIndex = 1;
                showTravelDialog("Farm 2");
            } else if (playerTileX == 0 && playerTileY == 0) {
                currentFarmIndex = 2;
                showTravelDialog("Farm 3");
            } else if (playerTileX == 19 && playerTileY == 0) {
                currentFarmIndex = 3;
                showTravelDialog("Farm 4");
            }
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            int localX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
            int localY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);
            boolean onPortal = false;
            switch (currentFarmIndex) {
                case 0:
                    if (localX == 49 && localY == 0) onPortal = true;
                    break;
                case 1:
                    if (localX == 0 && localY == 0) onPortal = true;
                    break;
                case 2:
                    if (localX == 49 && localY == 49) onPortal = true;
                    break;
                case 3:
                    if (localX == 0 && localY == 49) onPortal = true;
                    break;
            }
            if (onPortal) {
                showTravelDialog("the Village");
            }
        }
    }

    // --- NEW: Create Building Context Menu ---
    // --- NEW: Create Building Context Menu ---
    private void createBuildingContextMenu() {
        buildingContextMenu = new Dialog("Building Actions", skin);
        buildingContextMenu.setModal(true); // Blocks input to other UI elements
        buildingContextMenu.pad(10); // Add some padding

        // Add a label to display the building name
        Label buildingNameLabel = new Label("Building: ", skin);
        buildingNameLabel.setName("buildingNameLabel"); // Set a name to retrieve it later
        buildingContextMenu.getContentTable().add(buildingNameLabel).padBottom(10).row();

        // Add action buttons
        TextButton useButton = new TextButton("Use", skin);
        useButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedBuilding != null) {
                    // Handle 'Use' action based on building type
                    System.out.println("Using building: " + selectedBuilding.getName());
                    // You'll need to implement logic in GamePlayController
                    // E.g., gamePlayController.useBuilding(selectedBuilding);
                    showResultDialog("You used the " + selectedBuilding.getName() + "!");
                }
                buildingContextMenu.hide();
            }
        });
        buildingContextMenu.getContentTable().add(useButton).width(150).height(40).pad(5).row();

        TextButton demolishButton = new TextButton("Demolish", skin);
        demolishButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedBuilding != null) {
                    System.out.println("Demolishing building: " + selectedBuilding.getName());
                    // You'll need to implement logic in GamePlayController to remove it from the map
                    // and possibly refund resources.
                    // E.g., gamePlayController.demolishBuilding(selectedBuilding);

                    // --- Placeholder for demolish logic in MapView ---
                    // To remove the building from the map visually and logically:
                    Farm currentFarm = gamePlayController.getUser().getFarm();
                    if (currentFarm != null) {
                        // Assuming building stores its top-left tile coordinates (x, y)
                        // If not, you'll need to find it on the map.
                        // For a 1x1 building, just clear that tile.
                        // For multi-tile buildings, you need to clear all tiles it occupies.
                        int buildingX = selectedBuilding.getX(); // Assuming getX() method exists on PlaceableGameBuilding
                        int buildingY = selectedBuilding.getY(); // Assuming getY() method exists on PlaceableGameBuilding
                        int buildingWidth = selectedBuilding.getWidth(); // Assuming getWidth() method exists
                        int buildingHeight = selectedBuilding.getHeight(); // Assuming getHeight() method exists

                        for (int dy = 0; dy < buildingHeight; dy++) {
                            for (int dx = 0; dx < buildingWidth; dx++) {
                                int tileToClearX = buildingX + dx;
                                int tileToClearY = buildingY + dy;
                                if (tileToClearX >= 0 && tileToClearX < currentFarm.getTiles()[0].length &&
                                    tileToClearY >= 0 && tileToClearY < currentFarm.getTiles().length) {
                                    currentFarm.getTiles()[tileToClearY][tileToClearX].setStaticElement(null); // Clear the tile
                                }
                            }
                        }
                    }
                    // --- End placeholder ---

                    showResultDialog("You demolished the " + selectedBuilding.getName() + "!");
                }
                buildingContextMenu.hide();
            }
        });
        buildingContextMenu.getContentTable().add(demolishButton).width(150).height(40).pad(5).row();

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buildingContextMenu.hide();
            }
        });
        buildingContextMenu.getContentTable().add(closeButton).width(150).height(40).pad(5).row();

        // Add to stage later when shown
        // stage.addActor(buildingContextMenu); // Don't add here, show() will add/handle it
    }

    private void showBuildingContextMenu(PlaceableGameBuilding building) {
        this.selectedBuilding = building; // Store the clicked building

        // Update the building name label in the dialog
        Label buildingNameLabel = buildingContextMenu.findActor("buildingNameLabel");
        if (buildingNameLabel != null) {
            buildingNameLabel.setText("Building: " + building.getName());
        }

        // Position the dialog in the center of the screen
        buildingContextMenu.show(stage);
        buildingContextMenu.setPosition(Gdx.graphics.getWidth() / 2f - buildingContextMenu.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - buildingContextMenu.getHeight() / 2f);
    }
    // --- END NEW ---
    private Vector2 getFarmTopLeft(int farmIndex) {
        int farmW = FarmTemplate.WIDTH;
        int farmH = FarmTemplate.HEIGHT;
        int vilW = gameMap.getVilW();
        int vilH = gameMap.getVilH();
        float x_offset, y_offset;
        switch (farmIndex) {
            case 0:
                x_offset = 0;
                y_offset = vilH * TILE_SIZE;
                break;
            case 1:
                x_offset = (vilW + farmW) * TILE_SIZE;
                y_offset = vilH * TILE_SIZE;
                break;
            case 2:
                x_offset = 0;
                y_offset = 0;
                break;
            case 3:
                x_offset = (vilW + farmW) * TILE_SIZE;
                y_offset = 0;
                break;
            default:
                x_offset = 0;
                y_offset = 0;
        }
        return new Vector2(x_offset, y_offset);
    }
    private void captureCurrentFrame() {
        if (lastFrameTexture != null) {
            lastFrameTexture.dispose();
        }
        if (lastFramePixmap != null) {
            lastFramePixmap.dispose();
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderMap();
        renderPlayer();
        renderUI();
        batch.end();
        int width = Gdx.graphics.getBackBufferWidth();
        int height = Gdx.graphics.getBackBufferHeight();
        lastFramePixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);
        ByteBuffer buffer = lastFramePixmap.getPixels();
        buffer.clear();
        buffer.put(pixels);
        buffer.position(0);
        lastFrameTexture = new Texture(lastFramePixmap);
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        if (buildingSelectBox != null) {
            buildingSelectBox.setPosition(Gdx.graphics.getWidth() - buildingSelectBox.getWidth() - 20,
                Gdx.graphics.getHeight() - 50);
        }
        // --- NEW: Reposition buildingContextMenu on resize ---
        if (buildingContextMenu != null) {
            buildingContextMenu.setPosition(Gdx.graphics.getWidth() / 2f - buildingContextMenu.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - buildingContextMenu.getHeight() / 2f);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


    // In MapView.java, add this new private method
    private void renderBuildModeHighlight() {
        // Only highlight if we are in build mode
        if (gamePlayController.isInBuildMode()) {
            // Get current mouse position in world coordinates (using the same logic as for placing buildings)
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);

            // Get the current farm's render offset
            Vector2 renderOffset = inVillage ? new Vector2(0, 0) : getFarmTopLeft(currentFarmIndex);

            // Adjust mousePos relative to the farm's origin
            float adjustedMouseX = mousePos.x - renderOffset.x;
            float adjustedMouseY = mousePos.y - renderOffset.y;

            // Calculate raw tile coordinates (0 at bottom of farm's local coords)
            int tileX = (int) (adjustedMouseX / TILE_SIZE);
            int tileY = (int) (adjustedMouseY / TILE_SIZE); // Use adjustedTouchY from your input handling if you named it that

            // Ensure calculated tile coordinates are within the farm's bounds (0 to WIDTH/HEIGHT - 1)
            if (tileX >= 0 && tileX < FarmTemplate.WIDTH && tileY >= 0 && tileY < FarmTemplate.HEIGHT) {
                // Get the actual Tile object using the inverted Y for array lookup
                // This needs to match how gamePlayController.attemptToPlaceBuilding fetches the tile
                int actualTileYForLookup = FarmTemplate.HEIGHT - 1 - tileY; // Invert to array index

                Tile targetTile;
                if (inVillage) {
                    // For village, you might use a different HEIGHT constant if it's not FarmTemplate.HEIGHT
                    // And ensure your getTile method expects the inverted Y for village as well.
                    targetTile = gameMap.getVillage().getTile(tileX, 20 - 1 - tileY); // Adjust 20 to your Village height
                } else {
                    targetTile = gameMap.getFarm(currentFarmIndex).getTile(tileX, actualTileYForLookup);
                }

                if (targetTile != null) {
                    // Determine highlight color based on buildability, mirroring GamePlayController's logic
                    Color highlightColor;
                    if (targetTile.isAvailableForBuilding()) { // Use the method from Tile.java
                        highlightColor = Color.GREEN; // Buildable
                    } else {
                        highlightColor = Color.RED;   // Not buildable/occupied
                    }

                    // Calculate the world coordinates for drawing the highlight rectangle
                    // This uses the raw tileX and tileY (from adjustedMouse) because that's how renderMap draws them:
                    // renderOffset.x + (tileX * TILE_SIZE), renderOffset.y + (tileY * TILE_SIZE)
                    float drawX = renderOffset.x + (tileX * TILE_SIZE);
                    float drawY = renderOffset.y + (tileY * TILE_SIZE);

                    // Start drawing shapes
                    shapeRenderer.setProjectionMatrix(camera.combined); // Use camera's matrix for world coordinates
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(highlightColor.r, highlightColor.g, highlightColor.b, 0.5f); // Semi-transparent color

                    // Draw the rectangle over the tile
                    shapeRenderer.rect(drawX, drawY, TILE_SIZE, TILE_SIZE);

                    shapeRenderer.end(); // End drawing shapes
                }
            }
        }
    }
    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (fallbackTexture != null) fallbackTexture.dispose();
        if (stage != null) stage.dispose();
        if (lastFrameTexture != null) lastFrameTexture.dispose();
        if (lastFramePixmap != null) lastFramePixmap.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
