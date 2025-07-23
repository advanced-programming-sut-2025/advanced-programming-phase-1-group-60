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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapView implements Screen {
    private final GameMap gameMap;
    private final com.StardewValley.models.Game gameInstance;
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

    public MapView(GameMap gameMap, Runnable onBackToMenu, GameView gameView) {
        this.gameMap = gameMap;
        this.gameView = gameView;
        this.gameInstance = com.StardewValley.models.Game.getInstance();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        font.setColor(Color.WHITE);
        this.onBackToMenu = onBackToMenu;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = DEFAULT_ZOOM;
        this.mapManager = MapManager.getInstance();

        // This method was in your colleague's code but is missing from your GamePlayController.
        // I've commented it out to prevent errors. Discuss with your colleague.
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

        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);

        pixmap.setColor(Color.RED);
        pixmap.fill();
        this.playerTexture = new Texture(pixmap);
        pixmap.dispose();

        initializeFarmOwnerMap();

        Vector2 farmCenter = getFarmCenter(currentFarmIndex); // Start at current player's farm center
        this.playerPos = new Vector2(farmCenter);
        centerCameraOnPlayer();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = MenuManager.getInstance().getPixthulhuSkin();
        createUI();
    }

    private void handleGameplayMechanics(float delta) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        Vector2 oldPos = new Vector2(playerPos);
        float speed = 200 * delta;
        Vector2 velocity = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocity.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) velocity.x += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) velocity.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) velocity.y -= 1;
        velocity.nor().scl(speed);

        if (velocity.len() > 0) {
            Vector2 newPos = new Vector2(playerPos);

            if (isAreaPassable(playerPos.x + velocity.x, playerPos.y)) {
                newPos.x += velocity.x;
            }
            if (isAreaPassable(playerPos.x, playerPos.y + velocity.y)) {
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
        camera.position.set(playerPos.x, playerPos.y, 0);
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);
        centerCameraOnPlayer();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderMap();
        batch.draw(playerTexture, playerPos.x, playerPos.y, TILE_SIZE, TILE_SIZE);
        renderUI();
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
        } else {
            width = FarmTemplate.WIDTH;
            height = FarmTemplate.HEIGHT;
        }

        Vector2 renderOffset = inVillage ? new Vector2(0, 0) : getFarmTopLeft(currentFarmIndex);

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
                    } else if (element instanceof SellingBin) {
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
                    } else if (element instanceof ForagingTree) {
                        ForagingTree foragingTree = (ForagingTree) element;
                        Texture foragingTreeTexture = mapManager.getForagingTreeTexture(foragingTree.getImagePath());
                        if (foragingTreeTexture != null) {
                            treesToRender.add(new TreeRenderData(foragingTreeTexture, posX, posY, true));
                        }
                    } else if (element instanceof ForagingMineral) {
                        ForagingMineral mineral = (ForagingMineral) element;
                        Texture mineralTexture = mapManager.getForagingMineralTexture(mineral.getImagePath());
                        if (mineralTexture != null) {
                            batch.draw(mineralTexture, posX, posY, TILE_SIZE, TILE_SIZE);
                        }
                    } else if (element instanceof ForagingCrop) {
                        ForagingCrop crop = (ForagingCrop) element;
                        Texture cropTexture = mapManager.getForagingCropTexture(crop.getImagePath());
                        if (cropTexture != null) {
                            batch.draw(cropTexture, posX, posY, TILE_SIZE, TILE_SIZE);
                        }
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            gameView.showInventoryScreen();
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
                }  else if (element instanceof Store) {
                    Store store = (Store) element;
                    if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                        if (store.isOpen()) {
                            if (store.getName().equalsIgnoreCase("Blacksmith")) {
                                gameView.showBlacksmithView(store);
                            } else if (store.getName().equalsIgnoreCase("Fish Shop")) {
                                gameView.showFishShopView(store);
                            } else if (store.getName().equalsIgnoreCase("The Stardrop Saloon")) {
                                gameView.showSaloonView(store);
                            } else if (store.getName().equalsIgnoreCase("Marin'sRanch")) {
                                gameView.showMarinsRanchView(store);
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
        float hitboxInset = TILE_SIZE * 0.1f;
        float hitboxX = worldX + hitboxInset;
        float hitboxY = worldY + hitboxInset;
        float hitboxWidth = TILE_SIZE - (2 * hitboxInset);
        float hitboxHeight = TILE_SIZE - (2 * hitboxInset);
        boolean bottomLeft = isTilePassable(hitboxX, hitboxY);
        boolean bottomRight = isTilePassable(hitboxX + hitboxWidth, hitboxY);
        boolean topLeft = isTilePassable(hitboxX, hitboxY + hitboxHeight);
        boolean topRight = isTilePassable(hitboxX + hitboxWidth, hitboxY + hitboxHeight);
        return bottomLeft && bottomRight && topLeft && topRight;
    }

    private boolean isTilePassable(float worldX, float worldY) {
        Tile tile;
        if (inVillage) {
            int tileX = (int) (worldX / TILE_SIZE);
            int tileY = (int) (worldY / TILE_SIZE);
            if (tileX < 0 || tileX >= 20 || tileY < 0 || tileY >= 20) {
                return false;
            }
            tile = gameMap.getVillage().getTile(tileX, 20 - 1 - tileY);
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            int localX = (int) ((worldX - farmTopLeft.x) / TILE_SIZE);
            int localY = (int) ((worldY - farmTopLeft.y) / TILE_SIZE);
            if (localX < 0 || localX >= FarmTemplate.WIDTH || localY < 0 || localY >= FarmTemplate.HEIGHT) {
                return false;
            }
            tile = gameMap.getFarm(currentFarmIndex).getTile(localX, FarmTemplate.HEIGHT - 1 - localY);
        }
        if (tile == null) return false;
        if (tile.getStaticElement().isPresent()) {
            Object element = tile.getStaticElement().get();
            if (element instanceof Cabin) {
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

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
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
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (fallbackTexture != null) fallbackTexture.dispose();
        if (playerTexture != null) playerTexture.dispose();
        if (stage != null) stage.dispose();
    }
}
