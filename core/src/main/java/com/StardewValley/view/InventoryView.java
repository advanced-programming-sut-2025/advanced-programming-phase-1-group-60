package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.GiftController;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.controller.SellingController;
import com.StardewValley.models.Item;
import com.StardewValley.models.Npc;
import com.StardewValley.models.Quest;
import com.StardewValley.models.Skill;
import com.StardewValley.models.Tools;
import com.StardewValley.models.User;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class InventoryView implements Screen {
    private final Game game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final MenuManager menuManager;
    private final LoginMenuController loginController;
    private final User player;
    private final GameView gameView;

    private Table quickAccessTable;
    private static final int QUICK_ACCESS_SLOTS = 6;
    private Texture panelTexture;
    private Item selectedItem = null;

    private Table contentTable;
    private ScrollPane contentScrollPane;
    private Table contentOuterContainer; // holds scroll pane (easier re-layout)
    private DragAndDrop dragAndDrop;

    private final Map<String, Texture> textureCache;
    private final Texture fallbackTexture;
    private final Texture slotBackgroundTexture;

    // Trash can textures
    private Texture trashCanBeginnerTexture;
    private Texture trashCanCopperTexture;
    private Texture trashCanSteelTexture;
    private Texture trashCanGoldTexture;
    private Texture trashCanIridiumTexture;

    private Npc giftingTarget;
    private Table giftControlsTable;
    private Label giftQuantityLabel;
    private Item selectedGiftItem;
    private int giftQuantity = 1;

    private boolean sellingMode = false;
    private Table sellControlsTable;
    private Label sellQuantityLabel;
    private Item selectedSellItem;
    private int sellQuantity = 1;

    private Texture gameBackgroundTexture;
    private Image trashBinImage;
    private Texture inventoryTexture;
    private Texture inventoryNothingTexture;
    private Label sectionTitleLabel;
    private int questsCurrentPage = 1;
    private boolean isDisposed = false;

    // Map tab fields
    private Image miniMapImage;
    private Table mapLegendTable;

    public InventoryView(Game game, LoginMenuController loginController, GameView gameView, Npc giftingTarget, User currentPlayer) {
        this.game = game;
        this.loginController = loginController;
        this.player = currentPlayer;
        this.gameView = gameView;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.dragAndDrop = new DragAndDrop();
        this.textureCache = new HashMap<>();
        this.giftingTarget = giftingTarget;

        inventoryTexture = new Texture(Gdx.files.internal("assets/Map/Inventory/Inventory.png"));
        inventoryNothingTexture = new Texture(Gdx.files.internal("assets/Map/Inventory/Inventory_nothing.png"));

        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.MAGENTA);
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);

        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 0.7f));
        pixmap.fill();
        this.slotBackgroundTexture = new Texture(pixmap);
        pixmap.dispose();
        preloadAllTextures();

        addTestItems();
        createUI();
    }

    public boolean isDisposed() { return isDisposed; }
    public void setGiftingTarget(Npc npc) {
        this.giftingTarget = npc;
        this.sellingMode = false;
        showItems();
    }
    public void setSellingMode(boolean sellingMode) {
        this.sellingMode = sellingMode;
        this.giftingTarget = null;
        showItems();
    }

    private void addTestItems() {
        // Example test items (commented original seeds)
        player.getInventory().addItem(new Item("Copper_Bar", 100, "assets/Inventory/Copper_Bar.png"));
        player.getInventory().addItem(new Item("Wood", 2000, "assets/Inventory/Wood.png"));
        player.getInventory().addItem(new Item("Stone", 5000, "assets/Inventory/Stone.png"));
    }

    private void preloadAllTextures() {
        for (Texture texture : textureCache.values()) texture.dispose();
        textureCache.clear();
        panelTexture = new Texture(Gdx.files.internal("assets/Map/Inventory/Panel.png"));
        for (Item item : player.getInventory().getItems()) {
            String path = item.getPath();
            if (path != null && !path.isEmpty() && !textureCache.containsKey(path)) {
                if (Gdx.files.internal(path).exists()) {
                    textureCache.put(path, new Texture(Gdx.files.internal(path)));
                }
            }
        }
        trashCanBeginnerTexture = new Texture(Gdx.files.internal("assets/Inventory/Bin.png"));
        trashCanCopperTexture   = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Copper.png"));
        trashCanSteelTexture    = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Steel.png"));
        trashCanGoldTexture     = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Gold.png"));
        trashCanIridiumTexture  = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Iridium.png"));
    }

    private void createUI() {
        stage.clear();

        if (gameBackgroundTexture != null) {
            Image backgroundImage = new Image(gameBackgroundTexture);
            backgroundImage.setFillParent(true);
            stage.addActor(backgroundImage);
            Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            overlayPixmap.setColor(0, 0, 0, 0.6f);
            overlayPixmap.fill();
            Texture overlayTexture = new Texture(overlayPixmap);
            overlayPixmap.dispose();
            Image darkOverlay = new Image(overlayTexture);
            darkOverlay.setFillParent(true);
            stage.addActor(darkOverlay);
        }

        Image inventoryImage = new Image(inventoryTexture);
        float inventoryWidth = 1000;
        float inventoryHeight = 600;
        inventoryImage.setSize(inventoryWidth, inventoryHeight);
        inventoryImage.setPosition(
            (Gdx.graphics.getWidth() - inventoryWidth) / 2f,
            (Gdx.graphics.getHeight() - inventoryHeight) / 2f
        );
        stage.addActor(inventoryImage);

        sectionTitleLabel = new Label("Items", menuManager.getPixthulhuSkin(), "title");
        sectionTitleLabel.setAlignment(Align.center);
        sectionTitleLabel.setFontScale(0.8f);
        sectionTitleLabel.setPosition(1045, 315);
        sectionTitleLabel.setSize(280, 140);
        stage.addActor(sectionTitleLabel);

        Table sidebarContent = createSidebarContent();
        Table sidebarTable = new Table();
        sidebarTable.setFillParent(true);
        sidebarTable.top().left().pad(200, 100, 0, 0);
        sidebarTable.add(sidebarContent).width(200).top().left();
        stage.addActor(sidebarTable);

        createQuickAccessToolbar();

        // CONTENT AREA (scrollable)
        contentTable = new Table();
        contentTable.top().left().defaults().pad(2);

        contentScrollPane = new ScrollPane(contentTable, menuManager.getPixthulhuSkin());
        contentScrollPane.setFadeScrollBars(false);
        contentScrollPane.setScrollingDisabled(false, false);
        contentScrollPane.setOverscroll(false, true);
        contentScrollPane.setForceScroll(false, true);

        contentOuterContainer = new Table();
        contentOuterContainer.setPosition(550, 720 - 400);
        contentOuterContainer.setSize(370, 400); // Slightly wider to fit scroll bars
        contentOuterContainer.add(contentScrollPane).expand().fill();
        stage.addActor(contentOuterContainer);

        giftControlsTable = new Table(menuManager.getPixthulhuSkin());
        createGiftControls();
        giftControlsTable.setVisible(false);
        Table giftControlsContainer = new Table();
        giftControlsContainer.setFillParent(true);
        giftControlsContainer.bottom().padBottom(20);
        giftControlsContainer.add(giftControlsTable);
        stage.addActor(giftControlsContainer);

        sellControlsTable = new Table(menuManager.getPixthulhuSkin());
        createSellControls();
        sellControlsTable.setVisible(false);
        Table sellControlsContainer = new Table();
        sellControlsContainer.setFillParent(true);
        sellControlsContainer.bottom().padBottom(20);
        sellControlsContainer.add(sellControlsTable);
        stage.addActor(sellControlsContainer);

        trashBinImage = new Image(trashCanBeginnerTexture);
        Table trashContainer = new Table();
        trashContainer.setFillParent(true);
        trashContainer.bottom().right().pad(50);
        trashContainer.add(trashBinImage).size(70);
        stage.addActor(trashContainer);

        setupDragAndDrop();
        showItems();
    }

    private void createQuickAccessToolbar() {
        quickAccessTable = new Table();
        quickAccessTable.top().left();
        quickAccessTable.setPosition(550, 180);
        quickAccessTable.setSize(420, 70);

        Label quickAccessLabel = new Label("Quick Access (1-6)", menuManager.getPixthulhuSkin());
        quickAccessLabel.setFontScale(0.6f);
        quickAccessLabel.setAlignment(Align.center);
        quickAccessTable.add(quickAccessLabel).colspan(QUICK_ACCESS_SLOTS).padBottom(8).row();

        for (int i = 0; i < QUICK_ACCESS_SLOTS; i++) {
            final int slotIndex = i;
            Stack slotStack = createQuickAccessSlot(slotIndex);
            quickAccessTable.add(slotStack).size(60, 60).pad(2);

            dragAndDrop.addTarget(new DragAndDrop.Target(slotStack) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    return payload.getObject() instanceof Item;
                }
                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    Item draggedItem = (Item) payload.getObject();
                    player.getInventory().setQuickAccessSlot(slotIndex, draggedItem);
                    refreshQuickAccessSlots();
                }
            });

            slotStack.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Input.Buttons.RIGHT) {
                        player.getInventory().clearQuickAccessSlot(slotIndex);
                        refreshQuickAccessSlots();
                        return true;
                    }
                    return false;
                }
            });
        }
        stage.addActor(quickAccessTable);
    }

    private Stack createQuickAccessSlot(int slotIndex) {
        Stack stack = new Stack();
        Image bgImage = new Image(panelTexture);
        bgImage.setColor(Color.LIGHT_GRAY);
        stack.add(bgImage);

        Item item = player.getInventory().getQuickAccessSlot(slotIndex);
        if (item != null) {
            Texture itemTexture = textureCache.get(item.getPath());
            if (itemTexture != null) {
                stack.add(new Image(itemTexture));
            } else {
                Label nameLabel = new Label(item.getName(), menuManager.getPixthulhuSkin());
                nameLabel.setWrap(true);
                nameLabel.setAlignment(Align.center);
                nameLabel.setFontScale(0.5f);
                stack.add(nameLabel);
            }
        }
        Label slotNumberLabel = new Label(String.valueOf(slotIndex + 1), menuManager.getPixthulhuSkin());
        slotNumberLabel.setAlignment(Align.topLeft);
        slotNumberLabel.setColor(Color.CYAN);
        slotNumberLabel.setFontScale(0.6f);
        stack.add(slotNumberLabel);
        return stack;
    }

    private void refreshQuickAccessSlots() {
        quickAccessTable.clearChildren();
        Label quickAccessLabel = new Label("Quick Access (1-6)", menuManager.getPixthulhuSkin());
        quickAccessLabel.setFontScale(0.6f);
        quickAccessLabel.setAlignment(Align.center);
        quickAccessTable.add(quickAccessLabel).colspan(QUICK_ACCESS_SLOTS).padBottom(5).row();
        for (int i = 0; i < QUICK_ACCESS_SLOTS; i++) {
            final int slotIndex = i;
            Stack slotStack = createQuickAccessSlot(slotIndex);
            quickAccessTable.add(slotStack).size(60, 60).pad(2);
            dragAndDrop.addTarget(new DragAndDrop.Target(slotStack) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    return payload.getObject() instanceof Item;
                }
                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    Item draggedItem = (Item) payload.getObject();
                    player.getInventory().setQuickAccessSlot(slotIndex, draggedItem);
                    refreshQuickAccessSlots();
                }
            });
            slotStack.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Input.Buttons.RIGHT) {
                        player.getInventory().clearQuickAccessSlot(slotIndex);
                        refreshQuickAccessSlots();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void setupDragAndDrop() {
        dragAndDrop = new DragAndDrop();
        dragAndDrop.addTarget(new DragAndDrop.Target(trashBinImage) {
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
                String result = player.getInventory().trashItem(droppedItem);
                showResultDialog(result);
                showItems();
            }
        });
    }

    private Table createSidebarContent() {
        Table sidebar = new Table();
        sidebar.top().pad(5);
        float buttonHeight = 90;
        float pad = 6;

        TextButton itemsButton     = new TextButton("Items", menuManager.getPixthulhuSkin());
        TextButton skillsButton    = new TextButton("Skills", menuManager.getPixthulhuSkin());
        TextButton socialButton    = new TextButton("Social", menuManager.getPixthulhuSkin());
        TextButton mapButton       = new TextButton("Map", menuManager.getPixthulhuSkin());
        TextButton questsButton    = new TextButton("Quests", menuManager.getPixthulhuSkin());
        TextButton settingsButton  = new TextButton("Settings", menuManager.getPixthulhuSkin());
        TextButton exitButton      = new TextButton("Exit Inventory", menuManager.getPixthulhuSkin());

        sidebar.add(itemsButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(skillsButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(socialButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(mapButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(questsButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add(settingsButton).fillX().height(buttonHeight).pad(pad).row();
        sidebar.add().expandY().row();
        sidebar.add(exitButton).fillX().height(buttonHeight).pad(pad);

        itemsButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { showItems(); }});
        skillsButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { showSkills(); }});
        socialButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { showSocial(); }});
        mapButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { showMap(); }});
        questsButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                questsCurrentPage = 1;
                showQuests();
            }
        });
        settingsButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                gameView.showSettingsView();
            }
        });
        exitButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { gameView.showMapView(); }});

        return sidebar;
    }

    private void showItems() {
        sectionTitleLabel.setText("Items");
        updateBackgroundToItems();
        resetContent();
        int col = 0;
        final int ITEMS_PER_ROW = 6;

        if (giftingTarget != null) {
            contentTable.add(new Label("Select an item to gift to " + giftingTarget.getName(),
                menuManager.getPixthulhuSkin())).colspan(ITEMS_PER_ROW).padBottom(5).row();
        } else if (sellingMode) {
            contentTable.add(new Label("Select an item to sell",
                menuManager.getPixthulhuSkin())).colspan(ITEMS_PER_ROW).padBottom(5).row();
        }

        for (Item item : player.getInventory().getItems()) {
            Button itemButton = new Button(new Button.ButtonStyle());
            Stack itemSlot = createItemSlot(item);
            itemButton.add(itemSlot);
            contentTable.add(itemButton).size(60, 60);

            if (giftingTarget == null && !sellingMode) {
                dragAndDrop.addSource(new DragAndDrop.Source(itemButton) {
                    @Override
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
                        payload.setObject(item);
                        Stack dragStack = new Stack();
                        Image dragBg = new Image(panelTexture);
                        dragBg.setColor(Color.LIGHT_GRAY);
                        dragStack.add(dragBg);
                        Texture itemTexture = textureCache.get(item.getPath());
                        if (itemTexture != null) {
                            dragStack.add(new Image(itemTexture));
                        }
                        dragStack.setSize(50, 50);
                        payload.setDragActor(dragStack);
                        return payload;
                    }
                    @Override
                    public void dragStop(InputEvent event, float x, float y, int pointer,
                                         DragAndDrop.Payload payload, DragAndDrop.Target target) {
                        getActor().setColor(Color.WHITE);
                    }
                });
            } else if (giftingTarget != null) {
                itemButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        selectedGiftItem = item;
                        giftQuantity = 1;
                        giftQuantityLabel.setText("1");
                        giftControlsTable.setVisible(true);
                        sellControlsTable.setVisible(false);
                    }
                });
            } else { // selling
                itemButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        selectedSellItem = item;
                        sellQuantity = 1;
                        sellQuantityLabel.setText("1");
                        sellControlsTable.setVisible(true);
                        giftControlsTable.setVisible(false);
                    }
                });
            }

            col++;
            if (col % ITEMS_PER_ROW == 0) contentTable.row();
        }
        refreshQuickAccessSlots();
        contentScrollPane.layout();
    }

    private Stack createItemSlot(Item item) {
        Stack stack = new Stack();
        Image bgImage = new Image(slotBackgroundTexture);
        bgImage.setColor(Color.WHITE);
        stack.add(bgImage);

        Texture itemTexture = textureCache.get(item.getPath());
        if (itemTexture != null) {
            stack.add(new Image(itemTexture));
        } else {
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

    private void resetContent() {
        giftControlsTable.setVisible(false);
        sellControlsTable.setVisible(false);
        contentTable.clear();
    }

    // MAP TAB
    private void showMap() {
        resetContent();
        sectionTitleLabel.setText("Map");
        updateBackgroundToEmpty();

        // Acquire/refresh minimap
        Texture mm = gameView.getOrCreateMiniMapTexture();
        if (miniMapImage == null) {
            miniMapImage = new Image(mm);
        } else {
            miniMapImage.setDrawable(new TextureRegionDrawable(new TextureRegion(mm)));
        }

        // Scale to fit width ~350 while preserving aspect
        float targetWidth = 340f;
        float scale = targetWidth / mm.getWidth();
        miniMapImage.setSize(mm.getWidth() * scale, mm.getHeight() * scale);

        // Container
        Table mapTable = new Table();
        mapTable.add(miniMapImage).padBottom(10).row();

        // Refresh button
        TextButton refreshBtn = new TextButton("Refresh Minimap", menuManager.getPixthulhuSkin());
        refreshBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.refreshMiniMapTexture();
                Texture updated = gameView.getOrCreateMiniMapTexture();
                miniMapImage.setDrawable(new TextureRegionDrawable(new TextureRegion(updated)));
            }
        });
        mapTable.add(refreshBtn).padBottom(12).row();

        // Legend
        buildMapLegend();
        mapTable.add(mapLegendTable).left();

        contentTable.add(mapTable).left().top();
        contentScrollPane.layout();
    }

    private void buildMapLegend() {
        if (mapLegendTable != null) mapLegendTable.clear();
        else mapLegendTable = new Table(menuManager.getPixthulhuSkin());

        mapLegendTable.top().left();
        mapLegendTable.add(new Label("Legend:", menuManager.getPixthulhuSkin())).left().row();
        mapLegendTable.add(colorChip(Color.valueOf("4CAF50"), "Ground / Grass")).left().row();
        mapLegendTable.add(colorChip(Color.valueOf("1E88E5"), "Water")).left().row();
        mapLegendTable.add(colorChip(Color.valueOf("8D6E63"), "Plowed")).left().row();
        mapLegendTable.add(colorChip(Color.valueOf("FFEB3B"), "Crop Growing")).left().row();
        mapLegendTable.add(colorChip(Color.valueOf("F44336"), "Building")).left().row();
        mapLegendTable.add(colorChip(Color.valueOf("FFFFFF"), "Player")).left().row();
    }

    private Table colorChip(Color c, String label) {
        Pixmap pm = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pm.setColor(c);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        Image img = new Image(t);
        img.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){ // Dispose on stage dispose automatically
        });
        Table row = new Table();
        row.add(img).size(16).padRight(6);
        row.add(new Label(label, menuManager.getPixthulhuSkin())).left();
        return row;
    }

    private void loadNewItemTextures() {
        for (Item item : player.getInventory().getItems()) {
            String path = item.getPath();
            if (path != null && !path.isEmpty() && !textureCache.containsKey(path)) {
                if (Gdx.files.internal(path).exists()) {
                    textureCache.put(path, new Texture(Gdx.files.internal(path)));
                }
            }
        }
    }

    private void updateTrashCanTexture() {
        Texture currentTrashCanTexture;
        Tools.TrashbinStage stageEnum = player.getInventory().getTrashCanStage();
        switch (stageEnum) {
            case COPPER -> currentTrashCanTexture = trashCanCopperTexture;
            case STEEL -> currentTrashCanTexture = trashCanSteelTexture;
            case GOLD -> currentTrashCanTexture = trashCanGoldTexture;
            case IRIDIUM -> currentTrashCanTexture = trashCanIridiumTexture;
            default -> currentTrashCanTexture = trashCanBeginnerTexture;
        }
        if (trashBinImage != null) {
            trashBinImage.setDrawable(new TextureRegionDrawable(currentTrashCanTexture));
        }
    }

    private void showSkills() {
        resetContent();
        sectionTitleLabel.setText("Skills");
        sectionTitleLabel.setFontScale(0.8f);
        updateBackgroundToEmpty();
        contentTable.pad(10).top().left();
        for (Skill skill : player.getSkills()) {
            Label skillLabel = new Label(skill.getName() + " - Level: " + skill.getLevel(),
                menuManager.getPixthulhuSkin());
            skillLabel.setFontScale(0.7f);
            TextTooltip tooltip = new TextTooltip(skill.getDescription(), menuManager.getPixthulhuSkin());
            tooltip.setInstant(true);
            skillLabel.addListener(tooltip);
            contentTable.add(skillLabel).left().pad(8).row();
        }
        refreshQuickAccessSlots();
    }

    private void showSocial() {
        resetContent();
        sectionTitleLabel.setText("Social");
        sectionTitleLabel.setFontScale(0.8f);
        updateBackgroundToEmpty();
        contentTable.pad(10).top().left();

        Table socialTable = new Table();
        socialTable.top().left();
        Table playersColumn = new Table();
        playersColumn.top().left();
        Label playersHeader = new Label("--- Players ---", menuManager.getPixthulhuSkin());
        playersHeader.setFontScale(0.7f);
        playersColumn.add(playersHeader).pad(10).row();

        for (Map.Entry<User, Integer> entry : player.getFriendshipXpsWithUsers().entrySet()) {
            if (entry.getKey() != player) {
                Label playerLabel = new Label(entry.getKey().getUsername() + ": " + entry.getValue() + " XP",
                    menuManager.getPixthulhuSkin());
                playerLabel.setFontScale(0.6f);
                playersColumn.add(playerLabel).left().pad(5).row();
            }
        }

        Table npcsColumn = new Table();
        npcsColumn.top().left();
        Label npcsHeader = new Label("--- NPCs ---", menuManager.getPixthulhuSkin());
        npcsHeader.setFontScale(0.7f);
        npcsColumn.add(npcsHeader).pad(10).row();

        for (Map.Entry<Npc, Integer> entry : player.getFriendshipXpsWithNPCs().entrySet()) {
            Label npcLabel = new Label(entry.getKey().getName() + ": " + entry.getValue() + " XP",
                menuManager.getPixthulhuSkin());
            npcLabel.setFontScale(0.6f);
            npcsColumn.add(npcLabel).left().pad(5).row();
        }

        socialTable.add(playersColumn).top().left().padRight(60);
        socialTable.add(npcsColumn).top().left();
        contentTable.add(socialTable).left();
        refreshQuickAccessSlots();
    }

    private void showQuests() {
        resetContent();
        sectionTitleLabel.setText("Available Quests");
        sectionTitleLabel.setFontScale(0.7f);
        updateBackgroundToEmpty();
        contentTable.pad(10).top().left();

        List<Quest> availableQuests = new ArrayList<>();
        // Assuming an NPC repository pattern; if absent adapt accordingly
        com.StardewValley.repository.NpcRepository npcRepo =
            com.StardewValley.repository.NpcRepository.getInstance();

        for (Npc npc : npcRepo.getAllNpcs()) {
            for (Quest quest : npc.getQuests()) {
                if (quest.getCompletedBy() == null) {
                    availableQuests.add(quest);
                }
            }
        }

        if (availableQuests.isEmpty()) {
            contentTable.add(new Label("No available quests at the moment.", menuManager.getPixthulhuSkin())).row();
            return;
        }

        final int questsPerPage = 3;
        int totalPages = (int) Math.ceil((double) availableQuests.size() / questsPerPage);
        if (questsCurrentPage < 1) questsCurrentPage = 1;
        if (questsCurrentPage > totalPages) questsCurrentPage = totalPages;

        int startIndex = (questsCurrentPage - 1) * questsPerPage;
        int endIndex = Math.min(startIndex + questsPerPage, availableQuests.size());

        for (int i = startIndex; i < endIndex; i++) {
            Quest quest = availableQuests.get(i);
            Label questLabel = new Label(quest.toString(), menuManager.getPixthulhuSkin());
            questLabel.setWrap(true);
            questLabel.setFontScale(0.6f);
            contentTable.add(questLabel).width(340).left().padBottom(15).row();
        }

        Table paginationTable = new Table();
        TextButton prevButton = new TextButton("<< Prev", menuManager.getPixthulhuSkin());
        Label pageLabel = new Label("Page " + questsCurrentPage + " / " + totalPages, menuManager.getPixthulhuSkin());
        TextButton nextButton = new TextButton("Next >>", menuManager.getPixthulhuSkin());

        if (questsCurrentPage <= 1) { prevButton.setDisabled(true); prevButton.setColor(Color.GRAY); }
        if (questsCurrentPage >= totalPages) { nextButton.setDisabled(true); nextButton.setColor(Color.GRAY); }

        prevButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (questsCurrentPage > 1) { questsCurrentPage--; showQuests(); }
            }
        });
        nextButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (questsCurrentPage < totalPages) { questsCurrentPage++; showQuests(); }
            }
        });

        paginationTable.add(prevButton).pad(10);
        paginationTable.add(pageLabel).pad(10);
        paginationTable.add(nextButton).pad(10);
        contentTable.add(paginationTable).center().padTop(20).row();
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
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (giftQuantity > 1) { giftQuantity--; giftQuantityLabel.setText(String.valueOf(giftQuantity)); }
            }
        });
        plusButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (selectedGiftItem != null && giftQuantity < selectedGiftItem.getQuantity()) {
                    giftQuantity++; giftQuantityLabel.setText(String.valueOf(giftQuantity));
                }
            }
        });
        confirmButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (selectedGiftItem != null && giftingTarget != null) {
                    String result = GiftController.getInstance().giftToNpc(player, giftingTarget,
                        selectedGiftItem.getName(), giftQuantity);
                    Dialog resultDialog = new Dialog("Gift Result", menuManager.getPixthulhuSkin());
                    resultDialog.text(result).pad(20);
                    resultDialog.button("OK", true);
                    resultDialog.show(stage);
                    giftControlsTable.setVisible(false);
                    selectedGiftItem = null;
                    giftingTarget = null;
                    showItems();
                }
            }
        });
    }

    private void createSellControls() {
        TextButton minusButton = new TextButton("-", menuManager.getPixthulhuSkin());
        sellQuantityLabel = new Label("1", menuManager.getPixthulhuSkin());
        TextButton plusButton = new TextButton("+", menuManager.getPixthulhuSkin());
        TextButton confirmButton = new TextButton("Sell", menuManager.getPixthulhuSkin());

        sellControlsTable.add(new Label("Quantity:", menuManager.getPixthulhuSkin())).padRight(10);
        sellControlsTable.add(minusButton).size(40).pad(5);
        sellControlsTable.add(sellQuantityLabel).width(50).align(Align.center).pad(5);
        sellControlsTable.add(plusButton).size(40).pad(5);
        sellControlsTable.add(confirmButton).width(120).padLeft(20);

        minusButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (sellQuantity > 1) { sellQuantity--; sellQuantityLabel.setText(String.valueOf(sellQuantity)); }
            }
        });
        plusButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (selectedSellItem != null && sellQuantity < selectedSellItem.getQuantity()) {
                    sellQuantity++; sellQuantityLabel.setText(String.valueOf(sellQuantity));
                }
            }
        });
        confirmButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (selectedSellItem != null) {
                    String result = SellingController.getInstance()
                        .sellItem(player, selectedSellItem.getName(), sellQuantity);
                    Dialog resultDialog = new Dialog("Sale Result", menuManager.getPixthulhuSkin());
                    resultDialog.text(result).pad(20);
                    resultDialog.button("OK", true);
                    resultDialog.show(stage);

                    sellControlsTable.setVisible(false);
                    selectedSellItem = null;
                    sellingMode = false;
                    showItems();
                }
            }
        });
    }

    private void showResultDialog(String message) {
        Dialog dialog = new Dialog("Result", menuManager.getPixthulhuSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    public void setBackgroundTexture(Texture texture) {
        this.gameBackgroundTexture = texture;
        if (stage != null) {
            stage.clear();
            createUI();
        }
        refreshQuickAccessSlots();
    }

    private void updateBackgroundToEmpty() {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Image) {
                Drawable drawable = ((Image)actor).getDrawable();
                if (drawable instanceof TextureRegionDrawable trd) {
                    TextureRegion region = trd.getRegion();
                    if (region != null && region.getTexture() == inventoryTexture) {
                        ((Image)actor).setDrawable(new TextureRegionDrawable(inventoryNothingTexture));
                        break;
                    }
                }
            }
        }
    }

    private void updateBackgroundToItems() {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Image) {
                Drawable drawable = ((Image)actor).getDrawable();
                if (drawable instanceof TextureRegionDrawable trd) {
                    TextureRegion region = trd.getRegion();
                    if (region != null && region.getTexture() == inventoryNothingTexture) {
                        ((Image)actor).setDrawable(new TextureRegionDrawable(inventoryTexture));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        if (stage.getBatch() != null) stage.getBatch().setColor(Color.WHITE);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        preloadAllTextures();
        loadNewItemTextures();
        showItems();
        updateTrashCanTexture();
        refreshQuickAccessSlots();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (batch != null) batch.dispose();
        if (fallbackTexture != null) fallbackTexture.dispose();
        if (slotBackgroundTexture != null) slotBackgroundTexture.dispose();
        if (trashCanBeginnerTexture != null) trashCanBeginnerTexture.dispose();
        if (trashCanCopperTexture != null) trashCanCopperTexture.dispose();
        if (trashCanSteelTexture != null) trashCanSteelTexture.dispose();
        if (trashCanGoldTexture != null) trashCanGoldTexture.dispose();
        if (trashCanIridiumTexture != null) trashCanIridiumTexture.dispose();
        if (inventoryTexture != null) inventoryTexture.dispose();
        if (inventoryNothingTexture != null) inventoryNothingTexture.dispose();
        for (Texture texture : textureCache.values()) texture.dispose();
        textureCache.clear();
        isDisposed = true;
    }
}
