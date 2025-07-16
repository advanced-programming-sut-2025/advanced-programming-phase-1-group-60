// StardewValley/view/MapView.java
package com.StardewValley.view;

import com.StardewValley.AssetsManager.MapManager;
import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.models.*;
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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
    private final Game gameInstance;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private MapManager mapManager;
    private static final float DEFAULT_ZOOM = 0.6f;
    private Map<Integer, String> farmOwners = new HashMap<>();

    private Texture fallbackTexture;
    private Texture playerTexture;
    private Vector2 playerPos;

    private static final float TILE_SIZE = 32f;
    private int currentFarmIndex = 0;
    private boolean inVillage = false;

    private Stage stage;
    private Dialog travelDialog;
    private Label npcSpeechLabel; // استفاده از لیبل ساده
    private boolean speechIsShowing = false;
    private Skin skin;
    private final Runnable onBackToMenu;

    private Dialog npcContextMenu;
    private Dialog friendshipDialog;
    private Npc selectedNpc;

    public MapView(GameMap gameMap, Runnable onBackToMenu) {
        this.gameMap = gameMap;
        this.gameInstance = Game.getInstance();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        font.setColor(Color.WHITE);
        this.onBackToMenu = onBackToMenu;

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = DEFAULT_ZOOM;
        this.mapManager = MapManager.getInstance();

        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);

        pixmap.setColor(Color.RED);
        pixmap.fill();
        this.playerTexture = new Texture(pixmap);
        pixmap.dispose();

        initializeFarmOwnerMap();

        Vector2 farmCenter = getFarmCenter(0);
        this.playerPos = new Vector2(farmCenter);
        centerCameraOnPlayer();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = MenuManager.getInstance().getPixthulhuSkin();
        createUI();
    }

    private void createUI() {
        createTravelDialog();
        createBackButton();
        createNpcContextMenu();
        createFriendshipDialog();

        // ایجاد لیبل برای نمایش دیالوگ NPC
        npcSpeechLabel = new Label("", skin);
        npcSpeechLabel.setWrap(true); // فعال کردن شکستن خطوط
        npcSpeechLabel.setAlignment(Align.center);
        npcSpeechLabel.setVisible(false);
        stage.addActor(npcSpeechLabel);
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
                // Placeholder for now
                npcContextMenu.hide();
            }
        });

        questButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Placeholder for now
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

    private void showNpcSpeech(Npc npc, User user, float npcWorldX, float npcWorldY) {
        if (speechIsShowing) return;
        speechIsShowing = true;

        String prompt = npc.startConversation(user);
        npcSpeechLabel.setText(prompt);

        // ***[مهم] تنظیم عرض لیبل برای جلوگیری از نمایش ستونی***
        npcSpeechLabel.getStyle().background = skin.newDrawable("white", 0, 0, 0, 0.7f); // یک پس زمینه نیمه شفاف
        npcSpeechLabel.pack(); // محاسبه اندازه اولیه
        npcSpeechLabel.setWidth(250); // تنظیم یک عرض مشخص برای شکستن متن
        npcSpeechLabel.setHeight(npcSpeechLabel.getPrefHeight()); // تنظیم ارتفاع بر اساس متن

        // تبدیل مختصات دنیای بازی به مختصات صفحه
        Vector3 npcScreenPos = camera.project(new Vector3(npcWorldX, npcWorldY, 0));
        npcSpeechLabel.setPosition(
            npcScreenPos.x - npcSpeechLabel.getWidth() / 2f + (TILE_SIZE / 2f),
            npcScreenPos.y + TILE_SIZE
        );
        npcSpeechLabel.setVisible(true);

        npc.recordTalkTime();

        // تایمر برای محو کردن لیبل پس از 4 ثانیه
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                npcSpeechLabel.setVisible(false);
                speechIsShowing = false;
            }
        }, 4);
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

        ((Label) travelDialog.getContentTable().getCells().first().getActor()).setText("Do you want to travel to " + destination + "?");

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
                    destX = farmTopLeft.x + (48 * TILE_SIZE);
                    destY = farmTopLeft.y + (48 * TILE_SIZE);
                    break;
                case 1:
                    destX = farmTopLeft.x + (1 * TILE_SIZE);
                    destY = farmTopLeft.y + (48 * TILE_SIZE);
                    break;
                case 2:
                    destX = farmTopLeft.x + (48 * TILE_SIZE);
                    destY = farmTopLeft.y + (1 * TILE_SIZE);
                    break;
                case 3:
                    destX = farmTopLeft.x + (1 * TILE_SIZE);
                    destY = farmTopLeft.y + (1 * TILE_SIZE);
                    break;
            }
            playerPos.set(destX, destY);
            inVillage = false;
        } else {
            MapCoord villagePortal = gameMap.getEntrance(currentFarmIndex);
            playerPos.set(villagePortal.getX() * TILE_SIZE + 50, villagePortal.getY() * TILE_SIZE);
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
    }

    private void renderMap() {
        int startX, startY, width, height;
        List<Vector2> npcChatIconPositions = new ArrayList<>();

        if (inVillage) {
            startX = gameMap.getVilX();
            startY = gameMap.getVilY();
            width = gameMap.getVilW();
            height = gameMap.getVilH();
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            startX = (int) (farmTopLeft.x / TILE_SIZE);
            startY = (int) (farmTopLeft.y / TILE_SIZE);
            width = FarmTemplate.WIDTH;
            height = FarmTemplate.HEIGHT;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int tileX = startX + x;
                int tileY = startY + y;
                float posX = tileX * TILE_SIZE;
                float posY = tileY * TILE_SIZE;

                batch.draw(mapManager.getGrassTile(), posX, posY, TILE_SIZE, TILE_SIZE);

                Tile tile = gameMap.getTile(tileX, tileY);
                if (tile == null) continue;

                tile.getStaticElement().ifPresent(element -> {
                    Texture texture = null;
                    if (element instanceof Npc) {
                        Npc npc = (Npc) element;
                        texture = mapManager.getNpcTexture(npc.getName());
                        if (texture != null) {
                            batch.draw(texture, posX, posY, TILE_SIZE, TILE_SIZE);
                            // ذخیره موقعیت برای رندر آیکون چت در مرحله بعد
                            if (npc.isDialogueReady()) {
                                npcChatIconPositions.add(new Vector2(posX, posY));
                            }
                        }
                    } else if (element instanceof Store) {
                        texture = mapManager.getStoreTexture();
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
                        batch.setColor(Color.GREEN);
                        batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                        batch.setColor(Color.WHITE);
                    }
                });
            }
        }
        // رندر کردن آیکون‌های چت پس از رندر کامل نقشه
        for (Vector2 pos : npcChatIconPositions) {
            batch.draw(mapManager.getChatIconTexture(), pos.x + TILE_SIZE / 4, pos.y + TILE_SIZE, TILE_SIZE / 2, TILE_SIZE / 2);
        }
    }

    private void renderUI() {
        float textX = camera.position.x - Gdx.graphics.getWidth() / 2f * camera.zoom + 10;
        float textY = camera.position.y + Gdx.graphics.getHeight() / 2f * camera.zoom - 10;
        String ownerName = farmOwners.getOrDefault(currentFarmIndex, "Unknown");
        font.draw(batch, "Location: " + (inVillage ? "Village" : "Farm " + (currentFarmIndex + 1) + " - Owner: " + ownerName), textX, textY);
        font.draw(batch, "WASD to move, +/- to zoom", textX, textY - 20);
        font.draw(batch, "Press 1-4 to switch farms", textX, textY - 40);
    }

    private void handleInput(float delta) {
        if (speechIsShowing) { // اگر دیالوگ در حال نمایش است، فقط حرکت را غیرفعال کن
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) speechIsShowing = false;
            return;
        }

        float speed = 200 * delta;

        // --- محاسبه سرعت حرکت ---
        Vector2 velocity = new Vector2();
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) velocity.x -= 1;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) velocity.x += 1;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) velocity.y += 1;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) velocity.y -= 1;
        velocity.nor().scl(speed);

        // --- بررسی کلیک موس برای صحبت با NPC ---
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);

            int clickedTileX = (int) (clickPos.x / TILE_SIZE);
            int clickedTileY = (int) (clickPos.y / TILE_SIZE);

            int playerTileX = (int) (playerPos.x / TILE_SIZE);
            int playerTileY = (int) (playerPos.y / TILE_SIZE);

            Tile clickedTile = gameMap.getTile(clickedTileX, clickedTileY);
            if (clickedTile != null && clickedTile.getStaticElement().isPresent() && clickedTile.getStaticElement().get() instanceof Npc) {
                Npc npc = (Npc) clickedTile.getStaticElement().get();
                if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                    if (npc.isDialogueReady()) {
                        float npcWorldX = clickedTileX * TILE_SIZE;
                        float npcWorldY = clickedTileY * TILE_SIZE;
                        showNpcSpeech(npc, gameInstance.getCurrentPlayer(), npcWorldX, npcWorldY);
                    }
                }
            }
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);

            int clickedTileX = (int) (clickPos.x / TILE_SIZE);
            int clickedTileY = (int) (clickPos.y / TILE_SIZE);

            Tile clickedTile = gameMap.getTile(clickedTileX, clickedTileY);
            if (clickedTile != null && clickedTile.getStaticElement().isPresent() && clickedTile.getStaticElement().get() instanceof Npc) {
                Npc npc = (Npc) clickedTile.getStaticElement().get();
                int playerTileX = (int) (playerPos.x / TILE_SIZE);
                int playerTileY = (int) (playerPos.y / TILE_SIZE);

                if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                    selectedNpc = npc;
                    npcContextMenu.show(stage);
                }
            }
        }


        // --- بررسی برخورد و اعمال حرکت ---
        if (isAreaPassable(playerPos.x + velocity.x, playerPos.y)) {
            playerPos.x += velocity.x;
        }
        if (isAreaPassable(playerPos.x, playerPos.y + velocity.y)) {
            playerPos.y += velocity.y;
        }

        // --- کنترل‌های دیگر ---
        if (inVillage) {
            float minX = gameMap.getVilX() * TILE_SIZE;
            float minY = gameMap.getVilY() * TILE_SIZE;
            float maxX = (gameMap.getVilX() + gameMap.getVilW()) * TILE_SIZE - TILE_SIZE;
            float maxY = (gameMap.getVilY() + gameMap.getVilH()) * TILE_SIZE - TILE_SIZE;
            playerPos.x = Math.max(minX, Math.min(maxX, playerPos.x));
            playerPos.y = Math.max(minY, Math.min(maxY, playerPos.y));
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            float minX = farmTopLeft.x;
            float minY = farmTopLeft.y;
            float maxX = farmTopLeft.x + FarmTemplate.WIDTH * TILE_SIZE - TILE_SIZE;
            float maxY = farmTopLeft.y + FarmTemplate.HEIGHT * TILE_SIZE - TILE_SIZE;
            playerPos.x = Math.max(minX, Math.min(maxX, playerPos.x));
            playerPos.y = Math.max(minY, Math.min(maxY, playerPos.y));
        }

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.PLUS) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.EQUALS))
            camera.zoom -= 0.02f;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.MINUS)) camera.zoom += 0.02f;
        camera.zoom = Math.max(0.3f, Math.min(2f, camera.zoom));

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) setCurrentFarmIndex(0);
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) setCurrentFarmIndex(1);
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3)) setCurrentFarmIndex(2);
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_4)) setCurrentFarmIndex(3);

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
        int tileX = (int) (worldX / TILE_SIZE);
        int tileY = (int) (worldY / TILE_SIZE);

        try {
            Tile tile = gameMap.getTile(tileX, tileY);
            if (tile == null) return false;

            return tile.isPassable();
        } catch (Exception e) {
            return false;
        }
    }


    private void checkTravel() {
        if (speechIsShowing) return;

        int playerGlobalTileX = (int) (playerPos.x / TILE_SIZE);
        int playerGlobalTileY = (int) (playerPos.y / TILE_SIZE);

        if (inVillage) {
            MapCoord portal = gameMap.getEntrance(currentFarmIndex);
            if (playerGlobalTileX == portal.getX() && playerGlobalTileY == portal.getY()) {
                showTravelDialog("Farm " + (currentFarmIndex + 1));
            }
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            int farmStartX = (int) (farmTopLeft.x / TILE_SIZE);
            int farmStartY = (int) (farmTopLeft.y / TILE_SIZE);

            int playerLocalX = playerGlobalTileX - farmStartX;
            int playerLocalY = playerGlobalTileY - farmStartY;

            boolean onPortal = false;
            switch (currentFarmIndex) {
                case 0:
                    if (playerLocalX >= 49 && playerLocalY >= 49) onPortal = true;
                    break;
                case 1:
                    if (playerLocalX <= 0 && playerLocalY >= 49) onPortal = true;
                    break;
                case 2:
                    if (playerLocalX >= 49 && playerLocalY <= 0) onPortal = true;
                    break;
                case 3:
                    if (playerLocalX <= 0 && playerLocalY <= 0) onPortal = true;
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

        float x_offset = (farmIndex % 2 == 1) ? (farmW + vilW) * TILE_SIZE : 0;
        float y_offset = (farmIndex / 2 == 1) ? (farmH + vilH) * TILE_SIZE : 0;

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
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (fallbackTexture != null) fallbackTexture.dispose();
        if (playerTexture != null) playerTexture.dispose();
        if (stage != null) stage.dispose();
    }
}
