package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.models.Animal;
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
import java.util.Map;

public class MarinsRanchView implements Screen {
    private final Game game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final MenuManager menuManager;
    private final User player;
    private final GameView gameView;
    private final Store marinsRanch;

    private Table contentTable;
    private final Map<String, Texture> textureCache = new HashMap<>();
    private final Texture slotBackgroundTexture;
    private final Texture darkOverlayTexture;

    private boolean showAll = true;

    public MarinsRanchView(Game game, User player, GameView gameView, Store marinsRanch) {
        this.game = game;
        this.player = player;
        this.gameView = gameView;
        this.marinsRanch = marinsRanch;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();

        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 0.7f));
        pixmap.fill();
        this.slotBackgroundTexture = new Texture(pixmap);

        Pixmap darkPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        darkPixmap.setColor(0, 0, 0, 0.6f);
        darkPixmap.fill();
        this.darkOverlayTexture = new Texture(darkPixmap);
        darkPixmap.dispose();

        pixmap.dispose();

        preloadTextures();
        createUI();
    }

    private void preloadTextures() {
        for (Item item : marinsRanch.getItems()) {
            if (item.getPath() != null && !item.getPath().isEmpty() && Gdx.files.internal(item.getPath()).exists()) {
                textureCache.put(item.getName(), new Texture(Gdx.files.internal(item.getPath())));
            }
        }
        for (Animal animal : marinsRanch.animals) {
            if (animal.getPath() != null && !animal.getPath().isEmpty() && Gdx.files.internal(animal.getPath()).exists()) {
                textureCache.put(animal.getType(), new Texture(Gdx.files.internal(animal.getPath())));
            }
        }
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))));

        Label titleLabel = new Label("Marin's Ranch", menuManager.getPixthulhuSkin(), "title");
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
        root.add(scrollPane).expand().fill();

        stage.addActor(root);
        populateContent();
    }

    private void populateContent() {
        contentTable.clear();
        contentTable.top().left().pad(20);
        final int ITEMS_PER_ROW = 4;
        int col = 0;

        contentTable.add(new Label("Items", menuManager.getPixthulhuSkin(), "title")).colspan(ITEMS_PER_ROW).padBottom(10).row();
        for (Item item : marinsRanch.getItems()) {
            boolean isSold = (item.getName().equals("Milk Pail") && marinsRanch.isMilkPailSold) || (item.getName().equals("Shears") && marinsRanch.isShearsSold);
            contentTable.add(createItemSlot(item, isSold)).size(120).pad(15);
            if (++col % ITEMS_PER_ROW == 0) contentTable.row();
        }
        if (col % ITEMS_PER_ROW != 0) contentTable.row();
        col = 0;

        contentTable.add(new Label("Animals", menuManager.getPixthulhuSkin(), "title")).colspan(ITEMS_PER_ROW).padTop(30).padBottom(10).row();
        for (Animal animal : marinsRanch.animals) {
            contentTable.add(createAnimalSlot(animal)).size(120).pad(15);
            if (++col % ITEMS_PER_ROW == 0) contentTable.row();
        }
    }

    private Stack createItemSlot(Item item, boolean isSold) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));

        Texture itemTexture = textureCache.get(item.getName());
        if (itemTexture != null) {
            stack.add(new Image(itemTexture));
        }

        Table infoTable = new Table();
        infoTable.bottom();
        infoTable.add(new Label(item.getName().replace("_", " "), menuManager.getPixthulhuSkin())).row();
        infoTable.add(new Label(item.getStorePrice() + "g", menuManager.getPixthulhuSkin()));
        stack.add(infoTable);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (isSold) {
                    showResultDialog("This item is sold out for today.");
                    return;
                }
                String command = "buy -p " + item.getName() + " -n 1";
                Result result = marinsRanch.purchaseProduct(player, command);
                showResultDialog(result.getMessage());
                populateContent();
            }
        });
        stack.add(button);

        if (!showAll && isSold) {
            stack.setVisible(false);
        }
        if (isSold) {
            stack.add(new Image(darkOverlayTexture));
        }
        return stack;
    }

    private Stack createAnimalSlot(Animal animal) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));

        Texture animalTexture = textureCache.get(animal.getType());
        if (animalTexture != null) {
            stack.add(new Image(animalTexture));
        }

        Table infoTable = new Table();
        infoTable.bottom();
        infoTable.add(new Label(animal.getType(), menuManager.getPixthulhuSkin())).row();
        infoTable.add(new Label(animal.getPrice() + "g", menuManager.getPixthulhuSkin()));
        stack.add(infoTable);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showNameAnimalDialog(animal);
            }
        });
        stack.add(button);
        return stack;
    }

    private void showNameAnimalDialog(final Animal animal) {
        Dialog dialog = new Dialog("Name Your " + animal.getType(), menuManager.getPixthulhuSkin()) {
            @Override
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    TextField nameField = findActor("nameField");
                    String animalName = nameField.getText();
                    if (animalName == null || animalName.trim().isEmpty()) {
                        showResultDialog("Animal name cannot be empty.");
                        return;
                    }
                    String command = "buy -p " + animal.getType() + " -n " + animalName;
                    Result result = marinsRanch.purchaseProduct(player, command);
                    showResultDialog(result.getMessage());
                    populateContent();
                }
            }
        };
        TextField nameField = new TextField("", menuManager.getPixthulhuSkin());
        nameField.setName("nameField");
        dialog.text("Enter a name for your new animal:");
        dialog.getContentTable().row();
        dialog.getContentTable().add(nameField).width(200).pad(20);
        dialog.button("OK", true);
        dialog.button("Cancel", false);

        dialog.show(stage);
    }

    private void showResultDialog(String message) {
        Dialog dialog = new Dialog("Result", menuManager.getPixthulhuSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }
    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
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
