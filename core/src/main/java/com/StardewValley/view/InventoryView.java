package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.GiftController;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.models.Item;
import com.StardewValley.models.Npc;
import com.StardewValley.models.Skill;
import com.StardewValley.models.User;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

public class InventoryView implements Screen {
    private final Game game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final MenuManager menuManager;
    private final LoginMenuController loginController;
    private final User player;
    private final GameView gameView;

    private Table contentTable;
    private DragAndDrop dragAndDrop;

    private final Map<String, Texture> textureCache;
    private final Texture fallbackTexture;
    private final Texture slotBackgroundTexture;

    private Npc giftingTarget;
    private Table giftControlsTable;
    private Label giftQuantityLabel;
    private Item selectedGiftItem;
    private int giftQuantity = 1;
    private boolean isDisposed = false;

    public InventoryView(Game game, LoginMenuController loginController, GameView gameView, Npc giftingTarget) {
        this.game = game;
        this.loginController = loginController;
        this.player = loginController.getLoggedInUser();
        this.gameView = gameView;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.dragAndDrop = new DragAndDrop();
        this.textureCache = new HashMap<>();
        this.giftingTarget = giftingTarget;

        // Fallback texture creation for missing item icons
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.MAGENTA); // A bright color to indicate a missing texture
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);

        // A simple background for slots, created from a pixmap to avoid file not found errors
        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 0.7f));
        pixmap.fill();
        this.slotBackgroundTexture = new Texture(pixmap);
        pixmap.dispose();


        // Add test items every time, as requested
        addTestItems();

        preloadItemTextures();
        createUI();
    }

    private void addTestItems() {
        // Clear previous items to avoid duplication on re-entry
        player.getInventory().getItems().clear();
        player.getInventory().addItem(new Item("Warrior_Ring", 1, "assets/Inventory/Warrior_Ring.png"));
        player.getInventory().addItem(new Item("Stone_Fence", 10, "assets/Inventory/Stone_Fence.png"));
        player.getInventory().addItem(new Item("Quartz", 5, "assets/Inventory/Quartz.png"));
        player.getInventory().addItem(new Item("Bus_Ticket", 1, "assets/Inventory/Bus_Ticket.png"));
        player.getInventory().addItem(new Item("Dolomite", 1, "assets/Inventory/Dolomite.png"));
        player.getInventory().addItem(new Item("Alamite", 1, "assets/Inventory/Alamite.png"));
        player.getInventory().addItem(new Item("Baryte", 1, "assets/Inventory/Baryte.png"));
        player.getInventory().addItem(new Item("Oil", 3, "assets/Inventory/Oil.png"));
        player.getInventory().addItem(new Item("Rice", 20, "assets/Inventory/Rice.png"));
        player.getInventory().addItem(new Item("Wheat_Flour", 150, "assets/Inventory/Wheat_Flour.png")); // Corrected name
        player.getInventory().addItem(new Item("Iron_Bar", 100, "assets/Inventory/Iron_Bar.png"));
        player.getInventory().addItem(new Item("Wood", 200, "assets/Inventory/Wood.png"));
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    public void setGiftingTarget(Npc npc) {
        this.giftingTarget = npc;
        showItems(); // Refresh UI for gifting mode
    }

    private void preloadItemTextures() {
        for (Item item : player.getInventory().getItems()) {
            String path = item.getPath();
            if (path != null && !path.isEmpty() && !textureCache.containsKey(item.getName())) {
                try {
                    if (Gdx.files.internal(path).exists()) {
                        textureCache.put(item.getName(), new Texture(Gdx.files.internal(path)));
                    } else {
                        Gdx.app.error("TextureLoader", "File not found for item " + item.getName() + ": " + path);
                    }
                } catch (Exception e) {
                    Gdx.app.error("TextureLoader", "Failed to load texture for " + item.getName(), e);
                }
            }
        }
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))));

        Table sidebarContent = createSidebarContent();
        ScrollPane sidebar = new ScrollPane(sidebarContent, menuManager.getPixthulhuSkin());

        Table contentArea = new Table();
        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, menuManager.getPixthulhuSkin());
        contentArea.add(scrollPane).expand().fill().pad(20).row();

        giftControlsTable = new Table(menuManager.getPixthulhuSkin());
        createGiftControls();
        giftControlsTable.setVisible(false);
        contentArea.add(giftControlsTable).expandX().bottom().pad(10).row();

        Image trashBin = new Image(new Texture(Gdx.files.internal("assets/Inventory/Bin.png")));
        contentArea.add(trashBin).size(96).expandX().bottom().right().pad(15);

        root.add(sidebar).width(450).growY();
        root.add(contentArea).expand().fill();
        stage.addActor(root);

        dragAndDrop.addTarget(new DragAndDrop.Target(trashBin) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.RED);
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.WHITE);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Item droppedItem = (Item) payload.getObject();
                player.getInventory().removeItem(droppedItem);
                showItems();
            }
        });

        showItems(); // Initial display
    }

    private void createGiftControls() {
        TextButton minusButton = new TextButton("-", menuManager.getPixthulhuSkin());
        giftQuantityLabel = new Label("1", menuManager.getPixthulhuSkin());
        TextButton plusButton = new TextButton("+", menuManager.getPixthulhuSkin());
        TextButton confirmButton = new TextButton("Gift", menuManager.getPixthulhuSkin());

        giftControlsTable.add(new Label("Quantity:", menuManager.getPixthulhuSkin())).padRight(10);
        giftControlsTable.add(minusButton).size(40).pad(5);
        giftControlsTable.add(giftQuantityLabel).width(50).align(Align.center).pad(5);
        giftControlsTable.add(plusButton).size(40).pad(5);
        giftControlsTable.add(confirmButton).width(120).padLeft(20);

        minusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (giftQuantity > 1) {
                    giftQuantity--;
                    giftQuantityLabel.setText(String.valueOf(giftQuantity));
                }
            }
        });

        plusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedGiftItem != null && giftQuantity < selectedGiftItem.getQuantity()) {
                    giftQuantity++;
                    giftQuantityLabel.setText(String.valueOf(giftQuantity));
                }
            }
        });

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedGiftItem != null && giftingTarget != null) {
                    String result = GiftController.getInstance().giftToNpc(player, giftingTarget, selectedGiftItem.getName(), giftQuantity);

                    Dialog resultDialog = new Dialog("Gift Result", menuManager.getPixthulhuSkin());
                    resultDialog.text(result).pad(20);
                    resultDialog.button("OK", true);
                    resultDialog.show(stage);

                    giftControlsTable.setVisible(false);
                    selectedGiftItem = null;
                    giftingTarget = null; // Exit gifting mode after gifting
                    showItems(); // Refresh inventory view
                }
            }
        });
    }

    private Table createSidebarContent() {
        Table sidebar = new Table();
        sidebar.top().pad(5);
        sidebar.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/middleground.png"))));
        float buttonHeight = 48;
        float pad = 6;

        TextButton itemsButton = new TextButton("Items", menuManager.getPixthulhuSkin());
        TextButton skillsButton = new TextButton("Skills", menuManager.getPixthulhuSkin());
        TextButton socialButton = new TextButton("Social", menuManager.getPixthulhuSkin());
        TextButton mapButton = new TextButton("Map", menuManager.getPixthulhuSkin());
        TextButton questsButton = new TextButton("Quests", menuManager.getPixthulhuSkin());
        TextButton settingsButton = new TextButton("Settings", menuManager.getPixthulhuSkin());
        TextButton exitButton = new TextButton("Exit Inventory", menuManager.getPixthulhuSkin());

        sidebar.add(itemsButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(skillsButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(socialButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(mapButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(questsButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(settingsButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add().expandY().row();
        sidebar.add(exitButton).fillX().height(buttonHeight).pad(pad);

        itemsButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { showItems(); } });
        skillsButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { showSkills(); } });
        socialButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { showSocial(); } });
        mapButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { /* map logic */ } });
        questsButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { /* quests logic */ } });
        settingsButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { game.setScreen(new MainView(game, loginController)); } });
        exitButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { gameView.showMapView(); } });

        return sidebar;
    }

    private void showItems() {
        contentTable.clear();
        contentTable.top().left().pad(10);
        int col = 0;
        final int ITEMS_PER_ROW = 5;

        if (giftingTarget != null) {
            contentTable.add(new Label("Select an item to gift to " + giftingTarget.getName(), menuManager.getPixthulhuSkin(), "title")).colspan(ITEMS_PER_ROW).padBottom(20).row();
        }

        for (Item item : player.getInventory().getItems()) {
            Button itemButton = new Button(new Button.ButtonStyle()); // Use a simple button for click detection
            Stack itemSlot = createItemSlot(item);
            itemButton.add(itemSlot); // Add the visual stack to the button

            contentTable.add(itemButton).size(80).pad(8);

            if (giftingTarget == null) {
                dragAndDrop.addSource(new DragAndDrop.Source(itemButton) {
                    @Override
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
                        payload.setObject(item);
                        payload.setDragActor(createItemSlot(item));
                        getActor().setColor(Color.GRAY);
                        return payload;
                    }

                    @Override
                    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                        getActor().setColor(Color.WHITE);
                    }
                });
            } else {
                itemButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        selectedGiftItem = item;
                        giftQuantity = 1;
                        giftQuantityLabel.setText("1");
                        giftControlsTable.setVisible(true);
                    }
                });
            }

            col++;
            if (col % ITEMS_PER_ROW == 0) contentTable.row();
        }
    }

    private Stack createItemSlot(Item item) {
        Stack stack = new Stack();

        Image bgImage = new Image(slotBackgroundTexture);
        bgImage.setColor(Color.WHITE);
        stack.add(bgImage);

        Texture itemTexture = textureCache.get(item.getName());
        if (itemTexture != null) {
            Image itemImage = new Image(itemTexture);
            itemImage.setColor(Color.WHITE);
            stack.add(itemImage);
        }  else {
            // If texture is missing, show item name
            Label nameLabel = new Label(item.getName(), menuManager.getPixthulhuSkin());
            nameLabel.setWrap(true);
            nameLabel.setAlignment(Align.center);
            nameLabel.setFontScale(0.7f);
            stack.add(nameLabel);
        }

        Label quantityLabel = new Label(String.valueOf(item.getQuantity()), menuManager.getPixthulhuSkin());
        quantityLabel.setAlignment(Align.bottomRight);
        quantityLabel.setColor(Color.YELLOW);
        quantityLabel.setFontScale(0.8f);
        stack.add(quantityLabel);

        return stack;
    }

    private void showSkills() {
        giftControlsTable.setVisible(false);
        contentTable.clear();
        contentTable.pad(20).top().left();
        contentTable.add(new Label("Skills", menuManager.getPixthulhuSkin(), "title")).padBottom(20).row();
        for (Skill skill : player.getSkills()) {
            Label skillLabel = new Label(skill.getName() + " - Level: " + skill.getLevel(), menuManager.getPixthulhuSkin());
            contentTable.add(skillLabel).left().pad(10).row();
        }
    }

    private void showSocial() {
        giftControlsTable.setVisible(false);
        contentTable.clear();
        contentTable.pad(20).top().left();
        contentTable.add(new Label("Social", menuManager.getPixthulhuSkin(), "title")).padBottom(20).row();
        Table socialTable = new Table();
        socialTable.top().left();
        socialTable.add(new Label("--- Players ---", menuManager.getPixthulhuSkin())).pad(10).row();
        for (Map.Entry<User, Integer> entry : player.getFriendshipXpsWithUsers().entrySet()) {
            if (entry.getKey() != player) {
                socialTable.add(new Label(entry.getKey().getUsername() + ": " + entry.getValue() + " XP", menuManager.getPixthulhuSkin())).left().pad(5).row();
            }
        }
        socialTable.add(new Label("\n--- NPCs ---", menuManager.getPixthulhuSkin())).pad(10).row();
        for (Map.Entry<Npc, Integer> entry : player.getFriendshipXpsWithNPCs().entrySet()) {
            socialTable.add(new Label(entry.getKey().getName() + ": " + entry.getValue() + " XP", menuManager.getPixthulhuSkin())).left().pad(5).row();
        }
        contentTable.add(socialTable);
    }

    @Override
    public void render(float delta) {
        // پاک کردن صفحه
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // ریست OpenGL state
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // ریست رنگ و blend batch
        batch.setColor(1f, 1f, 1f, 1f);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // بروزرسانی و رسم UI
        stage.act(delta);
        stage.draw();
    }



    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (batch != null) batch.dispose();
        if (fallbackTexture != null) fallbackTexture.dispose();
        if (slotBackgroundTexture != null) slotBackgroundTexture.dispose();
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
        isDisposed = true;
    }
}
