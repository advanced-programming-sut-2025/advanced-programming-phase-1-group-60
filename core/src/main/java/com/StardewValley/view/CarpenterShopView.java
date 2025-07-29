package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.models.Item;
import com.StardewValley.models.Result;
import com.StardewValley.models.Store;
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

public class CarpenterShopView implements Screen {
    private final Game game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final MenuManager menuManager;
    private final User player;
    private final GameView gameView;
    private final Store carpenterShop;

    private Table contentTable;
    private final Map<String, Texture> textureCache = new HashMap<>();
    private final Texture slotBackgroundTexture;
    private final Texture darkOverlayTexture;

    private boolean showAll = true;

    public CarpenterShopView(Game game, User player, GameView gameView, Store carpenterShop) {
        this.game = game;
        this.player = player;
        this.gameView = gameView;
        this.carpenterShop = carpenterShop;
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
        for (Item item : carpenterShop.getItems()) {
            if (item.getPath() != null && !item.getPath().isEmpty() && Gdx.files.internal(item.getPath()).exists()) {
                textureCache.put(item.getName(), new Texture(Gdx.files.internal(item.getPath())));
            }
        }
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))));

        Label titleLabel = new Label("Carpenter's Shop", menuManager.getPixthulhuSkin(), "title");
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

        int col = 0;
        for (Item item : carpenterShop.getItems()) {
            contentTable.add(createItemSlot(item)).size(150).pad(10);
            if (++col % 4 == 0) {
                contentTable.row();
            }
        }
    }

    private Stack createItemSlot(Item item) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));

        Image itemImage = new Image(textureCache.get(item.getName()));
        stack.add(itemImage);

        Table infoTable = new Table();
        infoTable.bottom();
        infoTable.add(new Label(item.getName().replace("_", " "), menuManager.getPixthulhuSkin())).row();
        infoTable.add(new Label(item.getStorePrice() + "g", menuManager.getPixthulhuSkin())).row();
        if (item.getProperties().containsKey("materials")) {
            Map<String, Integer> materials = (Map<String, Integer>) item.getProperties().get("materials");
            for (Map.Entry<String, Integer> entry : materials.entrySet()) {
                infoTable.add(new Label(entry.getValue() + " " + entry.getKey(), menuManager.getPixthulhuSkin())).row();
            }
        }

        stack.add(infoTable);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String command = "buy -p " + item.getName();
                Result result = carpenterShop.purchaseProduct(player, command);
                showResultDialog(result.getMessage());
                populateContent(); // Refresh view
            }
        });
        stack.add(button);

        boolean isSold = carpenterShop.soldBuildings.getOrDefault(item.getName(), 0) > 0;

        if (!showAll && isSold) {
            stack.setVisible(false);
        }
        if (isSold) {
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
