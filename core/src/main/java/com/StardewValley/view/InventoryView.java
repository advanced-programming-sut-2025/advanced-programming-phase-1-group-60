package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
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
import com.StardewValley.repository.NpcRepository;
import com.StardewValley.repository.UserRepository;

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

    public InventoryView(Game game, LoginMenuController loginController, GameView gameView) {
        this.game = game;
        this.loginController = loginController;
        this.player = loginController.getLoggedInUser();
        this.gameView = gameView;
        this.stage = new Stage(new ScreenViewport());
        this.batch = new SpriteBatch();
        this.menuManager = MenuManager.getInstance();
        this.dragAndDrop = new DragAndDrop();
        this.textureCache = new HashMap<>();

        // ساخت تصویر جایگزین (Fallback)
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.SLATE);
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);
        pixmap.dispose();

        // پاک کردن آیتم های قبلی برای جلوگیری از تکرار
        player.getInventory().getItems().clear();

        // اضافه کردن آیتم ها با مسیر و نام دقیق از روی لیست فایل شما
        player.getInventory().addItem(new Item("Warrior_Ring", 1, "assets/Inventory/Warrior_Ring.png"));
        player.getInventory().addItem(new Item("Stone_Fence", 1, "assets/Inventory/Stone_Fence.png"));
        player.getInventory().addItem(new Item("Quartz", 1, "assets/Inventory/Quartz.png"));
        player.getInventory().addItem(new Item("Bus_Ticket", 1, "assets/Inventory/Bus_Ticket.png"));
        player.getInventory().addItem(new Item("Dolomite", 1, "assets/Inventory/Dolomite.png"));
        player.getInventory().addItem(new Item("Alamite", 1, "assets/Inventory/Alamite.png"));
        player.getInventory().addItem(new Item("Baryte", 1, "assets/Inventory/Baryte.png")); // با حرف بزرگ
        player.getInventory().addItem(new Item("Oil", 1, "assets/Inventory/Oil.png"));
        player.getInventory().addItem(new Item("Rice", 1, "assets/Inventory/Rice.png"));
        player.getInventory().addItem(new Item("Wheat_Floor", 1, "assets/Inventory/Wheat_Flour.png")); // اصلاح شده به Floor

        // بارگذاری اولیه تصاویر
        preloadItemTextures();

        createUI();
    }

    private void preloadItemTextures() {
        Gdx.app.log("InventoryView", "شروع بارگذاری تصاویر آیتم ها...");
        for (Item item : player.getInventory().getItems()) {
            String path = item.getPath();
            if (path != null && !path.isEmpty() && !textureCache.containsKey(item.getName())) {
                try {
                    if (Gdx.files.internal(path).exists()) {
                        textureCache.put(item.getName(), new Texture(Gdx.files.internal(path)));
                        Gdx.app.log("TextureLoader", "موفق: " + item.getName() + " از " + path);
                    } else {
                        Gdx.app.error("TextureLoader", "پیدا نشد: " + path);
                    }
                } catch (Exception e) {
                    Gdx.app.error("TextureLoader", "خطا در بارگذاری " + item.getName() + " از " + path, e);
                }
            }
        }
        Gdx.app.log("InventoryView", "پایان بارگذاری تصاویر.");
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))));

        Table sidebarContent = createSidebarContent();
        ScrollPane sidebar = new ScrollPane(sidebarContent, menuManager.getPixthulhuSkin());
        sidebar.setFadeScrollBars(false);
        sidebar.setScrollingDisabled(true, false);
        sidebar.setForceScroll(false, true);

        Table contentArea = new Table();
        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, menuManager.getPixthulhuSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        contentArea.add(scrollPane).expand().fill().pad(20).row();
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
        showItems();
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
        for (TextButton btn : new TextButton[]{itemsButton, skillsButton, socialButton, mapButton, questsButton, settingsButton, exitButton}) {
            btn.getLabel().setAlignment(Align.center);
        }
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
        final int ITEMS_PER_ROW = 2; // افزایش تعداد آیتم در هر ردیف
        for (Item item : player.getInventory().getItems()) {
            Image itemImage = createItemImage(item);
            itemImage.setUserObject(item);
            itemImage.addListener(new TextTooltip(item.getName() + " x" + item.getQuantity(), menuManager.getPixthulhuSkin()));
            contentTable.add(itemImage).size(64).pad(8);
            dragAndDrop.addSource(new DragAndDrop.Source(itemImage) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(getActor().getUserObject());
                    payload.setDragActor(createItemImage((Item) getActor().getUserObject()));
                    getActor().setColor(Color.GRAY);
                    return payload;
                }
                @Override public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    getActor().setColor(Color.WHITE);
                }
            });
            col++;
            if (col % ITEMS_PER_ROW == 0) contentTable.row();
        }
    }

    private Image createItemImage(Item item) {
        Texture itemTexture = textureCache.getOrDefault(item.getName(), fallbackTexture);
        return new Image(itemTexture);
    }

    private void showSkills() {
        contentTable.clear();
        contentTable.pad(20).top().left();
        contentTable.add(new Label("Skills", menuManager.getPixthulhuSkin(), "title")).padBottom(20).colspan(2).row();
        for (Skill skill : player.getSkills()) {
            Label skillLabel = new Label(skill.getName() + " - Level: " + skill.getLevel(), menuManager.getPixthulhuSkin());
            contentTable.add(skillLabel).left().pad(10).row();
            skillLabel.addListener(new TextTooltip(skill.getDescription(), menuManager.getPixthulhuSkin()));
        }
    }

    private void showSocial() {
        contentTable.clear();
        contentTable.pad(20).top().left();
        contentTable.add(new Label("Social", menuManager.getPixthulhuSkin(), "title")).padBottom(20).colspan(2).row();
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

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (batch != null) batch.dispose();
        if (fallbackTexture != null) fallbackTexture.dispose();

        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
    }
}
