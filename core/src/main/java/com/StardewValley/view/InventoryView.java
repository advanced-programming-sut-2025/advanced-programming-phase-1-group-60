package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.GiftController;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.controller.SellingController;
import com.StardewValley.models.Item;
import com.StardewValley.models.Npc;
import com.StardewValley.models.Quest; // Import Quest
import com.StardewValley.models.Skill;
import com.StardewValley.models.Tools;
import com.StardewValley.models.User;
import com.StardewValley.repository.NpcRepository; // Import NpcRepository
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // Textures for Trash Can levels
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
    private boolean isDisposed = false;

    private boolean sellingMode = false;
    private Table sellControlsTable;
    private Label sellQuantityLabel;
    private Item selectedSellItem;
    private int sellQuantity = 1;
    private Texture gameBackgroundTexture;
    // Field for the trash can image to allow for dynamic updates
    private Image trashBinImage;
    private Texture inventoryTexture;
    private Texture inventoryNothingTexture;
    private Label sectionTitleLabel;
    private int questsCurrentPage = 1; // برای مدیریت صفحه‌بندی ماموریت‌ها

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

    private void addTestItems() {
      //  player.getInventory().addItem(new Item("Egg", 10, "assets/Inventory/Egg.png").setType("Food"));
      //  player.getInventory().addItem(new Item("Milk", 10, "assets/Inventory/Milk.png").setType("Food"));
        player.getInventory().addItem(new Item("Copper_Bar", 100, "assets/Inventory/Copper_Bar.png"));
        player.getInventory().addItem(new Item("Wood", 2000, "assets/Inventory/Wood.png"));
        player.getInventory().addItem(new Item("Stone", 5000, "assets/Inventory/Stone.png"));
    }

    public boolean isDisposed() {
        return isDisposed;
    }

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


    private void preloadAllTextures() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();

        for (Item item : player.getInventory().getItems()) {
            String path = item.getPath();
            if (path != null && !path.isEmpty() && !textureCache.containsKey(path)) {
                try {
                    if (Gdx.files.internal(path).exists()) {
                        textureCache.put(path, new Texture(Gdx.files.internal(path)));
                    } else {
                        Gdx.app.error("TextureLoader", "File not found for item " + item.getName() + ": " + path);
                    }
                } catch (Exception e) {
                    Gdx.app.error("TextureLoader", "Failed to load texture for " + item.getName(), e);
                }
            }
        }

        trashCanBeginnerTexture = new Texture(Gdx.files.internal("assets/Inventory/Bin.png"));
        trashCanCopperTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Copper.png"));
        trashCanSteelTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Steel.png"));
        trashCanGoldTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Gold.png"));
        trashCanIridiumTexture = new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Iridium.png"));
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
        sidebarTable.top().left().pad(0, 100, 0, 0);
        sidebarTable.add(sidebarContent).width(200).top().left();
        stage.addActor(sidebarTable);

        contentTable = new Table();
        contentTable.top().left();
        contentTable.setPosition(550, 720 - 400);
        contentTable.setSize(300, 400);
        stage.addActor(contentTable);

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
    private void showResultDialog(String message) {
        Dialog dialog = new Dialog("Result", menuManager.getPixthulhuSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
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
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (sellQuantity > 1) {
                    sellQuantity--;
                    sellQuantityLabel.setText(String.valueOf(sellQuantity));
                }
            }
        });

        plusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedSellItem != null && sellQuantity < selectedSellItem.getQuantity()) {
                    sellQuantity++;
                    sellQuantityLabel.setText(String.valueOf(sellQuantity));
                }
            }
        });

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedSellItem != null) {
                    String result = SellingController.getInstance().sellItem(player, selectedSellItem.getName(), sellQuantity);

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


    private Table createSidebarContent() {
        Table sidebar = new Table();
        sidebar.top().pad(5);
        float buttonHeight = 90;
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
        questsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                questsCurrentPage = 1; // ریست کردن صفحه هنگام کلیک
                showQuests();
            }
        });
        settingsButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { game.setScreen(new MainView(game, loginController)); } });
        exitButton.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { gameView.showMapView(); } });

        return sidebar;
    }

    private void showItems() {
        contentTable.clear();
        contentTable.top().left();
        sectionTitleLabel.setText("Items");

        updateBackgroundToItems();

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
                        payload.setDragActor(createItemSlot(item));
                        getActor().setColor(Color.GRAY);
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
            } else {
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
    }

    private Stack createItemSlot(Item item) {
        Stack stack = new Stack();

        Image bgImage = new Image(slotBackgroundTexture);
        bgImage.setColor(Color.WHITE);
        stack.add(bgImage);

        Texture itemTexture = textureCache.get(item.getPath());
        if (itemTexture != null) {
            Image itemImage = new Image(itemTexture);
            itemImage.setColor(Color.WHITE);
            stack.add(itemImage);
        }  else {
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

    private void updateTrashCanTexture() {
        Texture currentTrashCanTexture;
        Tools.TrashbinStage stage = player.getInventory().getTrashCanStage();
        switch (stage) {
            case COPPER:
                currentTrashCanTexture = trashCanCopperTexture;
                break;
            case STEEL:
                currentTrashCanTexture = trashCanSteelTexture;
                break;
            case GOLD:
                currentTrashCanTexture = trashCanGoldTexture;
                break;
            case IRIDIUM:
                currentTrashCanTexture = trashCanIridiumTexture;
                break;
            default:
                currentTrashCanTexture = trashCanBeginnerTexture;
                break;
        }
        if (trashBinImage != null) {
            trashBinImage.setDrawable(new TextureRegionDrawable(currentTrashCanTexture));
        }
    }

    private void showSkills() {
        giftControlsTable.setVisible(false);
        sellControlsTable.setVisible(false);
        contentTable.clear();
        sectionTitleLabel.setText("Skills");
        sectionTitleLabel.setFontScale(0.8f);
        updateBackgroundToEmpty();

        contentTable.pad(30).top().left();
        for (Skill skill : player.getSkills()) {
            Label skillLabel = new Label(skill.getName() + " - Level: " + skill.getLevel(),
                menuManager.getPixthulhuSkin());
            skillLabel.setFontScale(0.7f); // Make skills text smaller
            contentTable.add(skillLabel).left().pad(10).row();
        }
    }

    private void showSocial() {
        giftControlsTable.setVisible(false);
        sellControlsTable.setVisible(false);
        contentTable.clear();
        sectionTitleLabel.setText("Social");
        sectionTitleLabel.setFontScale(0.8f);
        updateBackgroundToEmpty();

        contentTable.pad(30).top().left();

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
        socialTable.add(playersColumn).top().left().padRight(100);
        socialTable.add(npcsColumn).top().left();
        contentTable.add(socialTable);
    }

    private void showQuests() {
        giftControlsTable.setVisible(false);
        sellControlsTable.setVisible(false);
        contentTable.clear();
        sectionTitleLabel.setText("Available Quests");
        sectionTitleLabel.setFontScale(0.7f);
        updateBackgroundToEmpty();

        contentTable.pad(20).top().left();

        NpcRepository npcRepository = NpcRepository.getInstance();
        List<Quest> availableQuests = new ArrayList<>();

        for (Npc npc : npcRepository.getAllNpcs()) {
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

        // اطمینان از معتبر بودن شماره صفحه
        if (questsCurrentPage < 1) questsCurrentPage = 1;
        if (questsCurrentPage > totalPages) questsCurrentPage = totalPages;

        int startIndex = (questsCurrentPage - 1) * questsPerPage;
        int endIndex = Math.min(startIndex + questsPerPage, availableQuests.size());

        for (int i = startIndex; i < endIndex; i++) {
            Quest quest = availableQuests.get(i);
            Label questLabel = new Label(quest.toString(), menuManager.getPixthulhuSkin());
            questLabel.setWrap(true);
            questLabel.setFontScale(0.6f);
            contentTable.add(questLabel).width(400).left().padBottom(15).row();
        }

        // افزودن دکمه‌های صفحه‌بندی
        Table paginationTable = new Table();
        TextButton prevButton = new TextButton("<< Prev", menuManager.getPixthulhuSkin());
        Label pageLabel = new Label("Page " + questsCurrentPage + " / " + totalPages, menuManager.getPixthulhuSkin());
        TextButton nextButton = new TextButton("Next >>", menuManager.getPixthulhuSkin());

        if (questsCurrentPage <= 1) {
            prevButton.setDisabled(true);
            prevButton.setColor(Color.GRAY);
        }
        if (questsCurrentPage >= totalPages) {
            nextButton.setDisabled(true);
            nextButton.setColor(Color.GRAY);
        }

        prevButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (questsCurrentPage > 1) {
                    questsCurrentPage--;
                    showQuests();
                }
            }
        });

        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (questsCurrentPage < totalPages) {
                    questsCurrentPage++;
                    showQuests();
                }
            }
        });

        paginationTable.add(prevButton).pad(10);
        paginationTable.add(pageLabel).pad(10);
        paginationTable.add(nextButton).pad(10);

        contentTable.add(paginationTable).colspan(1).center().padTop(300).row();
    }

    public void setBackgroundTexture(Texture texture) {
        this.gameBackgroundTexture = texture;
        if (stage != null) {
            stage.clear();
            createUI();
        }
    }
    private void updateBackgroundToEmpty() {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Image) {
                Drawable drawable = ((Image)actor).getDrawable();
                if (drawable instanceof TextureRegionDrawable) {
                    TextureRegion region = ((TextureRegionDrawable)drawable).getRegion();
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
                if (drawable instanceof TextureRegionDrawable) {
                    TextureRegion region = ((TextureRegionDrawable)drawable).getRegion();
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
        if (stage.getBatch() != null) {
            stage.getBatch().setColor(Color.WHITE);
        }
    }



    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        preloadAllTextures();
        showItems();
        updateTrashCanTexture();
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
        if (trashCanBeginnerTexture != null) trashCanBeginnerTexture.dispose();
        if (trashCanCopperTexture != null) trashCanCopperTexture.dispose();
        if (trashCanSteelTexture != null) trashCanSteelTexture.dispose();
        if (trashCanGoldTexture != null) trashCanGoldTexture.dispose();
        if (trashCanIridiumTexture != null) trashCanIridiumTexture.dispose();
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
        isDisposed = true;
    }
}
