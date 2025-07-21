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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlacksmithView implements Screen {
    private final Game game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final MenuManager menuManager;
    private final User player;
    private final GameView gameView;
    private final Store blacksmith;

    private Table contentTable;
    private final Map<String, Texture> textureCache = new HashMap<>();
    private final Texture slotBackgroundTexture;
    private final Texture darkOverlayTexture;

    private boolean showAll = true;

    private Table itemControlsTable;
    private Label itemQuantityLabel;
    private Item selectedItem;
    private int itemQuantity = 1;

    public BlacksmithView(Game game, LoginMenuController loginController, GameView gameView, Store blacksmith) {
        this.game = game;
        this.player = loginController.getLoggedInUser();
        this.gameView = gameView;
        this.blacksmith = blacksmith;
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
        textureCache.put("Copper_Ore", new Texture(Gdx.files.internal("assets/Inventory/Copper_Ore.png")));
        textureCache.put("Iron_Ore", new Texture(Gdx.files.internal("assets/Inventory/Iron_Ore.png")));
        textureCache.put("Coal", new Texture(Gdx.files.internal("assets/Inventory/Coal.png")));
        textureCache.put("Gold_Ore", new Texture(Gdx.files.internal("assets/Inventory/Gold_Ore.png")));

        textureCache.put("Copper Tool", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Copper_Axe.png")));
        textureCache.put("Steel Tool", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Steel_Pickaxe.png")));
        textureCache.put("Gold Tool", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Gold_Hoe.png")));
        textureCache.put("Iridium Tool", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Iridium_Watering_Can.png")));

        textureCache.put("Copper Trashcan", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Copper.png")));
        textureCache.put("Steel Trashcan", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Steel.png")));
        textureCache.put("Gold Trashcan", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Gold.png")));
        textureCache.put("Iridium Trashcan", new Texture(Gdx.files.internal("assets/Inventory/ToolsAndUpgrade/Trash_Can_Iridium.png")));
    }


    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))));

        Label titleLabel = new Label("Blacksmith", menuManager.getPixthulhuSkin(), "title");
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

        itemControlsTable = new Table(menuManager.getPixthulhuSkin());
        createItemControls();
        itemControlsTable.setVisible(false);
        root.add(itemControlsTable).expandX().bottom().pad(10);

        stage.addActor(root);
        populateContent();
    }
    private void populateContent() {
        contentTable.clear();
        contentTable.top().left().pad(20);

        contentTable.add(new Label("Items for Sale", menuManager.getPixthulhuSkin(), "title")).colspan(4).padBottom(10).row();
        int col = 0;
        for (Item item : blacksmith.getItems()) {
            contentTable.add(createItemSlot(item)).size(100).pad(5);
            col++;
            if (col % 4 == 0) contentTable.row();
        }
        contentTable.row();

        contentTable.add(new Label("Upgrades", menuManager.getPixthulhuSkin(), "title")).colspan(4).padTop(20).padBottom(10).row();

        contentTable.add(createUpgradeSlot("Copper Tool", 1, false)).size(100).pad(100);
        contentTable.add(createUpgradeSlot("Steel Tool", 2, false)).size(100).pad(100);
        contentTable.add(createUpgradeSlot("Gold Tool", 3, false)).size(100).pad(100);
        contentTable.add(createUpgradeSlot("Iridium Tool", 4, false)).size(100).pad(100).row();

        contentTable.add(createUpgradeSlot("Copper Trashcan", 1, true)).size(100).pad(100);
        contentTable.add(createUpgradeSlot("Steel Trashcan", 2, true)).size(100).pad(100);
        contentTable.add(createUpgradeSlot("Gold Trashcan", 3, true)).size(100).pad(100);
        contentTable.add(createUpgradeSlot("Iridium Trashcan", 4, true)).size(100).pad(100);
    }

    private Stack createItemSlot(Item item) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));

        Image itemImage = new Image(textureCache.get(item.getName()));
        stack.add(itemImage);

        Table infoTable = new Table();
        infoTable.bottom();
        infoTable.add(new Label(item.getName().replace("_", " "), menuManager.getPixthulhuSkin())).row();
        infoTable.add(new Label(item.getStorePrice() + "g", menuManager.getPixthulhuSkin()));
        stack.add(infoTable);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedItem = item;
                itemQuantity = 1;
                itemQuantityLabel.setText("1");
                itemControlsTable.setVisible(true);
            }
        });
        stack.add(button);

        return stack;
    }

    private Stack createUpgradeSlot(String upgradeName, int level, boolean isBin) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));

        Image itemImage = new Image(textureCache.get(upgradeName));
        stack.add(itemImage);

        Table infoTable = new Table();
        infoTable.bottom();
        infoTable.add(new Label(upgradeName, menuManager.getPixthulhuSkin())).row();

        int price = blacksmith.getUpgradePrice(level, isBin);
        infoTable.add(new Label(price + "g", menuManager.getPixthulhuSkin())).row();
        infoTable.add(new Label("5 " + upgradeName.split(" ")[0] + "_Bar", menuManager.getPixthulhuSkin())).row();

        stack.add(infoTable);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleUpgradeClick(level, isBin);
            }
        });
        stack.add(button);

        boolean isSoldToday = isBin ? blacksmith.soldBinsUpgrades.getOrDefault(level, 0) > 0 : blacksmith.soldUpgrades.getOrDefault(level, 0) > 0;

        if (!showAll && isSoldToday) {
            stack.setVisible(false);
        }
        if (isSoldToday) {
            Image darkOverlay = new Image(darkOverlayTexture);
            stack.add(darkOverlay);
        }

        return stack;
    }

    private void handleUpgradeClick(int level, boolean isBin) {
        boolean isSoldToday = isBin ? blacksmith.soldBinsUpgrades.getOrDefault(level, 0) > 0 : blacksmith.soldUpgrades.getOrDefault(level, 0) > 0;
        if (isSoldToday) {
            showResultDialog("This upgrade is sold out for today.");
            return;
        }

        if (isBin) {
            if (player.getInventory().getTrashCanStage().ordinal() == level - 1) {
                Result result = blacksmith.upgradeTrashCan(player, level);
                showResultDialog(result.getMessage());
                populateContent();
            } else {
                showResultDialog("Your trashcan is not at the required level for this upgrade.");
            }
        } else {
            if (!blacksmith.isUpgradeAvailable(player, level, false)) {
                showResultDialog("You don't have enough money or materials for this upgrade.");
                return;
            }
            List<Tools> upgradableTools = blacksmith.getUpgradableTools(player, level).stream()
                .filter(tool -> tool.getName().equalsIgnoreCase("Axe") ||
                    tool.getName().equalsIgnoreCase("Hoe") ||
                    tool.getName().equalsIgnoreCase("Pickaxe") ||
                    tool.getName().equalsIgnoreCase("Watering_Can"))
                .collect(Collectors.toList());
            if (upgradableTools.isEmpty()) {
                showResultDialog("You have no tools eligible for this upgrade.");
                return;
            }
            showToolSelectionDialog(upgradableTools, level);
        }
    }

    private void showToolSelectionDialog(List<Tools> tools, int level) {
        Dialog dialog = new Dialog("Select a Tool to Upgrade", menuManager.getPixthulhuSkin());
        Table table = new Table();
        for (Tools tool : tools) {
            TextButton button = new TextButton(tool.getName(), menuManager.getPixthulhuSkin());
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Result result = blacksmith.upgradeTools(player, level, tool);
                    showResultDialog(result.getMessage());
                    populateContent();
                    dialog.hide();
                }
            });
            table.add(button).row();
        }
        dialog.getContentTable().add(table);
        dialog.button("Cancel");
        dialog.show(stage);
    }

    private void showResultDialog(String message) {
        Dialog dialog = new Dialog("Result", menuManager.getPixthulhuSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    private void createItemControls() {
        TextButton minusButton = new TextButton("-", menuManager.getPixthulhuSkin());
        itemQuantityLabel = new Label("1", menuManager.getPixthulhuSkin());
        TextButton plusButton = new TextButton("+", menuManager.getPixthulhuSkin());
        TextButton confirmButton = new TextButton("Buy", menuManager.getPixthulhuSkin());

        itemControlsTable.add(new Label("Quantity:", menuManager.getPixthulhuSkin())).padRight(10);
        itemControlsTable.add(minusButton).size(40).pad(5);
        itemControlsTable.add(itemQuantityLabel).width(100).align(Align.center).pad(5);
        itemControlsTable.add(plusButton).size(40).pad(5);
        itemControlsTable.add(confirmButton).width(120).padLeft(20);

        minusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (itemQuantity > 1) {
                    itemQuantity--;
                    itemQuantityLabel.setText(String.valueOf(itemQuantity));
                }
            }
        });

        plusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                itemQuantity++;
                itemQuantityLabel.setText(String.valueOf(itemQuantity));
            }
        });

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedItem != null) {
                    Result result = blacksmith.purchaseProduct(player, "buy -p " + selectedItem.getName() + " -n " + itemQuantity);
                    Dialog resultDialog = new Dialog("Purchase Result", menuManager.getPixthulhuSkin());
                    resultDialog.text(result.getMessage()).pad(20);
                    resultDialog.button("OK", true);
                    resultDialog.show(stage);
                    itemControlsTable.setVisible(false);
                    selectedItem = null;
                }
            }
        });
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
