package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.models.Item;
import com.StardewValley.models.Result;
import com.StardewValley.models.Store;
import com.StardewValley.models.Tools;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

public class FishShopView implements Screen {
    private final Game game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final MenuManager menuManager;
    private final User player;
    private final GameView gameView;
    private final Store fishShop;

    private Table contentTable;
    private final Map<String, Texture> textureCache = new HashMap<>();
    private final Texture slotBackgroundTexture;
    private final Texture darkOverlayTexture;

    private boolean showAll = true;

    public FishShopView(Game game, LoginMenuController loginController, GameView gameView, Store fishShop) {
        this.game = game;
        this.player = loginController.getLoggedInUser();
        this.gameView = gameView;
        this.fishShop = fishShop;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();

        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 0.7f));
        pixmap.fill();
        this.slotBackgroundTexture = new Texture(pixmap);

        Pixmap darkPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        darkPixmap.setColor(0, 0, 0, 0.5f);
        darkPixmap.fill();
        this.darkOverlayTexture = new Texture(darkPixmap);
        darkPixmap.dispose();

        pixmap.dispose();

        preloadTextures();
        createUI();
    }

    private void preloadTextures() {
        textureCache.put("Training Rod", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Training_Rod.png")));
        textureCache.put("Bamboo Pole", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Bamboo_Pole.png")));
        textureCache.put("Fiberglass Rod", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Fiberglass_Rod.png")));
        textureCache.put("Iridium Rod", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Iridium_Rod.png")));

        textureCache.put("Trout Soup", new Texture(Gdx.files.internal("assets/Inventory/Food/Trout_Soup.png")));

        textureCache.put("Fish Smoker Recipe", new Texture(Gdx.files.internal("assets/Inventory/Recipes/Smoked_Fish.png")));
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))));

        Label titleLabel = new Label("Fish Shop", menuManager.getPixthulhuSkin(), "title");
        TextButton toggleButton = new TextButton("Show Available", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        toggleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showAll = !showAll;
                toggleButton.setText(showAll ? "Show Available" : "Show All");
                populateContent();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.showMapView();
            }
        });

        Table header = new Table();
        header.add(titleLabel).expandX().center();
        header.add(toggleButton).pad(10);
        header.add(backButton).pad(10);
        root.add(header).fillX().row();

        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, menuManager.getPixthulhuSkin());
        root.add(scrollPane).expand().fill().row();

        stage.addActor(root);
        populateContent();
    }

    private void populateContent() {
        contentTable.clear();
        contentTable.top().left().pad(20);

        contentTable.add(new Label("Upgrades", menuManager.getPixthulhuSkin(), "title")).colspan(4).padTop(20).padBottom(10).row();
        contentTable.add(createUpgradeSlot("Training Rod", 0)).size(100).pad(70);
        contentTable.add(createUpgradeSlot("Bamboo Pole", 1)).size(100).pad(70);
        contentTable.add(createUpgradeSlot("Fiberglass Rod", 2)).size(100).pad(70);
        contentTable.add(createUpgradeSlot("Iridium Rod", 3)).size(100).pad(70).row();

        contentTable.add(new Label("Foods", menuManager.getPixthulhuSkin(), "title")).colspan(4).padTop(20).padBottom(10).row();
        contentTable.add(createItemSlot("Trout Soup", 250, "Food", fishShop.isTroutSoupSold)).size(100).pad(10).row();

        contentTable.add(new Label("Recipe", menuManager.getPixthulhuSkin(), "title")).colspan(4).padTop(20).padBottom(10).row();
        contentTable.add(createItemSlot("Fish Smoker Recipe", 10000, "Recipe", fishShop.isFishSmokerSold)).size(100).pad(10).row();
    }

    private Stack createItemSlot(String itemName, int price, String itemType, boolean isSold) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));

        Image itemImage = new Image(textureCache.get(itemName));
        stack.add(itemImage);

        Table infoTable = new Table();
        infoTable.bottom();
        infoTable.add(new Label(itemName.replace("_", " "), menuManager.getPixthulhuSkin())).row();
        infoTable.add(new Label(price + "g", menuManager.getPixthulhuSkin()));
        stack.add(infoTable);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String command = "buy -p " + itemName;
                Result result = fishShop.purchaseProduct(player, command);
                showResultDialog(result.getMessage());
                populateContent(); // Refresh view
            }
        });
        stack.add(button);

        if (!showAll && isSold) {
            stack.setVisible(false);
        }
        if (isSold) {
            Image darkOverlay = new Image(darkOverlayTexture);
            stack.add(darkOverlay);
        }

        return stack;
    }

    private Stack createUpgradeSlot(String upgradeName, int level) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));

        Image itemImage = new Image(textureCache.get(upgradeName));
        stack.add(itemImage);

        Table infoTable = new Table();
        infoTable.bottom();
        infoTable.add(new Label(upgradeName, menuManager.getPixthulhuSkin())).row();

        int price = fishShop.upgradePoleCosts.getOrDefault(level, 0);
        infoTable.add(new Label(price + "g", menuManager.getPixthulhuSkin())).row();

        stack.add(infoTable);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Item currentTool = player.getInventory().getItems().stream()
                    .filter(i -> i.getName().equalsIgnoreCase("fishingpole"))
                    .findFirst().orElse(null);

                Result result = fishShop.upgradeTools(player, level, (Tools) currentTool);
                showResultDialog(result.getMessage());
                populateContent(); // Refresh view after attempting upgrade
            }
        });
        stack.add(button);

        boolean isShopItemAvailable = fishShop.soldPoleUpgrades.getOrDefault(level, 0) == 0;

        if (!showAll && !isShopItemAvailable) {
            stack.setVisible(false);
        }
        if (!isShopItemAvailable) {
            Image darkOverlay = new Image(darkOverlayTexture);
            stack.add(darkOverlay);
        }
        return stack;
    }

    private void showResultDialog(String message) {
        Dialog resultDialog = new Dialog("Result", menuManager.getPixthulhuSkin());
        resultDialog.text(message).pad(20);
        resultDialog.button("OK", true);
        resultDialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (stage.getBatch() != null) {
            stage.getBatch().setColor(Color.WHITE);
        }
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
        stage.dispose();
        batch.dispose();
        slotBackgroundTexture.dispose();
        darkOverlayTexture.dispose();
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
    }
}
