package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.models.Item;
import com.StardewValley.models.Recipe;
import com.StardewValley.models.Result;
import com.StardewValley.models.Store;
import com.StardewValley.models.User;
import com.StardewValley.repository.RecipeRepository;
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

public class SaloonView implements Screen {
    private final Game game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final MenuManager menuManager;
    private final User player;
    private final GameView gameView;
    private final Store saloon;
    private final RecipeRepository recipeRepository = new RecipeRepository();

    private Table contentTable;
    private final Map<String, Texture> textureCache = new HashMap<>();
    private final Texture slotBackgroundTexture;
    private final Texture darkOverlayTexture;

    private boolean showAll = true;

    public SaloonView(Game game, LoginMenuController loginController, GameView gameView, Store saloon) {
        this.game = game;
        this.player = loginController.getLoggedInUser();
        this.gameView = gameView;
        this.saloon = saloon;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();

        // پس‌زمینه‌ی سل
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 0.7f));
        pixmap.fill();
        this.slotBackgroundTexture = new Texture(pixmap);
        pixmap.dispose();

        // لایه‌ی تیره برای آیتم‌های فروخته‌شده
        Pixmap darkPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        darkPixmap.setColor(0, 0, 0, 0.5f);
        darkPixmap.fill();
        this.darkOverlayTexture = new Texture(darkPixmap);
        darkPixmap.dispose();

        preloadTextures();
        createUI();
    }

    private void preloadTextures() {
        // بارگذاری بافت‌ها برای رسپی‌ها
        for (Recipe recipe : recipeRepository.getRecipes().values()) {
            if (recipe.getPath() != null && !recipe.getPath().isEmpty() && Gdx.files.internal(recipe.getPath()).exists()) {
                textureCache.put(recipe.getName(), new Texture(Gdx.files.internal(recipe.getPath())));
            }
        }
        // بارگذاری بافت برای نوشیدنی‌های یکتا
        String beerPath = "assets/Inventory/Food/Beer.png";
        if (Gdx.files.internal(beerPath).exists()) {
            textureCache.put("Beer", new Texture(Gdx.files.internal(beerPath)));
        }
        String coffeePath = "assets/Inventory/Food/Coffee.png";
        if (Gdx.files.internal(coffeePath).exists()) {
            textureCache.put("Coffee", new Texture(Gdx.files.internal(coffeePath)));
        }
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))));

        Label titleLabel = new Label("The Stardrop Saloon", menuManager.getPixthulhuSkin(), "title");
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
        final int ITEMS_PER_ROW = 5;

        // Foods Section
        contentTable.add(new Label("Foods", menuManager.getPixthulhuSkin(), "title"))
            .colspan(ITEMS_PER_ROW).padBottom(10).row();
        for (Item item : saloon.getItems()) {
            contentTable.add(createItemSlot(item.getName(), item.getStorePrice(), "Food", false))
                .size(100).pad(5,25,5,25);  // pad(top, left, bottom, right)
            col++;
            if (col % ITEMS_PER_ROW == 0) contentTable.row();
        }
        if (col % ITEMS_PER_ROW != 0) contentTable.row();
        col = 0;

        // Recipes Section
        contentTable.add(new Label("Recipes", menuManager.getPixthulhuSkin(), "title"))
            .colspan(ITEMS_PER_ROW).padTop(20).padBottom(10).row();
        for (Recipe recipe : recipeRepository.getRecipes().values()) {
            if ("Stardrop Saloon".equals(recipe.getSource())) {
                boolean isSold = saloon.soldRecipes.getOrDefault(recipe.getName(), 0) > 0;
                contentTable.add(createItemSlot(recipe.getName(), recipe.getSellPrice(), "Recipe", isSold))
                    .size(100).pad(5,25,5,25);
                col++;
                if (col % ITEMS_PER_ROW == 0) contentTable.row();
            }
        }
    }

    private Stack createItemSlot(String itemName, int price, String itemType, boolean isSold) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));

        Texture itemTexture = textureCache.get(itemName);
        if (itemTexture != null) {
            stack.add(new Image(itemTexture));
        }

        Table infoTable = new Table();
        infoTable.bottom();
        infoTable.add(new Label(itemName.replace("_", " "), menuManager.getPixthulhuSkin())).row();
        infoTable.add(new Label(price + "g", menuManager.getPixthulhuSkin()));
        stack.add(infoTable);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String command = "buy -p " + itemName + (itemType.equals("Recipe") ? " Recipe" : "");
                Result result = saloon.purchaseProduct(player, command);
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

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

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
