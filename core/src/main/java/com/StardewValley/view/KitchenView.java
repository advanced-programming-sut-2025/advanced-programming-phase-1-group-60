package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.CookController;
import com.StardewValley.models.Item;
import com.StardewValley.models.Recipe;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

public class KitchenView implements Screen {
    private final Game game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final MenuManager menuManager;
    private final User player;
    private final GameView gameView;
    private final CookController cookController;
    private final RecipeRepository recipeRepository;

    private Table contentTable;
    private final Map<String, Texture> textureCache = new HashMap<>();
    private final Texture slotBackgroundTexture;
    private final Texture darkOverlayTexture;

    private enum KitchenState {COOKING, REFRIGERATOR, INVENTORY}
    private KitchenState currentState = KitchenState.COOKING;

    public KitchenView(Game game, User player, GameView gameView) {
        this.game = game;
        this.player = player;
        this.gameView = gameView;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.cookController = new CookController();
        this.recipeRepository = new RecipeRepository();

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
        for (Recipe recipe : recipeRepository.getRecipes().values()) {
            if (recipe.getPath() != null && !recipe.getPath().isEmpty() && Gdx.files.internal(recipe.getPath()).exists()) {
                textureCache.put(recipe.getName(), new Texture(Gdx.files.internal(recipe.getPath())));
            }
        }
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))));

        Table sidebar = createSidebar();
        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, menuManager.getPixthulhuSkin());

        root.add(sidebar).width(250).growY();
        root.add(scrollPane).expand().fill();

        stage.addActor(root);
        updateContent();
    }

    private Table createSidebar() {
        Table sidebar = new Table();
        sidebar.top().pad(10);
        sidebar.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("assets/Background/layers/middleground.png"))));

        TextButton cookingButton = new TextButton("Cooking", menuManager.getPixthulhuSkin());
        TextButton fridgeButton = new TextButton("Refrigerator", menuManager.getPixthulhuSkin());
        TextButton inventoryButton = new TextButton("Inventory", menuManager.getPixthulhuSkin());
        TextButton exitButton = new TextButton("Exit Kitchen", menuManager.getPixthulhuSkin());

        sidebar.add(cookingButton).fillX().pad(5).row();
        sidebar.add(fridgeButton).fillX().pad(5).row();
        sidebar.add(inventoryButton).fillX().pad(5).row();
        sidebar.add().expandY().row();
        sidebar.add(exitButton).fillX().pad(5);

        cookingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentState = KitchenState.COOKING;
                updateContent();
            }
        });
        fridgeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentState = KitchenState.REFRIGERATOR;
                updateContent();
            }
        });
        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentState = KitchenState.INVENTORY;
                updateContent();
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.showMapView();
            }
        });

        return sidebar;
    }

    private void updateContent() {
        contentTable.clear();
        contentTable.top().left().pad(20).padLeft(40); // Added padLeft to move content to the right
        switch (currentState) {
            case COOKING:
                showCooking();
                break;
            case REFRIGERATOR:
                showRefrigerator();
                break;
            case INVENTORY:
                showInventory();
                break;
        }
    }

    private void showCooking() {
        contentTable.add(new Label("Recipes", menuManager.getPixthulhuSkin(), "title")).colspan(5).padBottom(20).row();
        int col = 0;
        for (Recipe recipe : recipeRepository.getRecipes().values()) {
            boolean isUnlocked = player.getCookRecipes().contains(recipe.getName());
            contentTable.add(createRecipeSlot(recipe, isUnlocked)).size(120).pad(70); // Increased size and padding
            if (++col % 5 == 0) contentTable.row();
        }
    }

    private void showRefrigerator() {
        contentTable.add(new Label("Refrigerator", menuManager.getPixthulhuSkin(), "title")).colspan(5).padBottom(20).row();
        int col = 0;
        for (Item item : player.getRefrigeratorItems()) {
            contentTable.add(createItemSlot(item, false)).size(110).pad(70); // Increased size and padding
            if (++col % 5 == 0) contentTable.row();
        }
    }

    private void showInventory() {
        contentTable.add(new Label("Inventory", menuManager.getPixthulhuSkin(), "title")).colspan(5).padBottom(20).row();
        int col = 0;
        for (Item item : player.getInventory().getItems()) {
            contentTable.add(createItemSlot(item, true)).size(110).pad(70); // Increased size and padding
            if (++col % 5 == 0) contentTable.row();
        }
    }

    private Stack createRecipeSlot(Recipe recipe, boolean isUnlocked) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));
        Texture texture = textureCache.get(recipe.getName());
        if (texture != null) {
            stack.add(new Image(texture));
        }
        Label nameLabel = new Label(recipe.getName(), menuManager.getPixthulhuSkin());
        nameLabel.setAlignment(Align.bottom);
        stack.add(nameLabel);

        if (!isUnlocked) {
            stack.add(new Image(darkOverlayTexture));
        }

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (isUnlocked) {
                    String result = cookController.prepareFood(player, recipe.getName());
                    showResultDialog(result);
                    updateContent(); // Refresh view after cooking
                } else {
                    showResultDialog("You haven't learned this recipe yet.");
                }
            }
        });
        stack.add(button);
        return stack;
    }

    private Stack createItemSlot(Item item, boolean fromInventory) {
        Stack stack = new Stack();
        stack.add(new Image(slotBackgroundTexture));
        if (item.getPath() != null && Gdx.files.internal(item.getPath()).exists()) {
            stack.add(new Image(new Texture(Gdx.files.internal(item.getPath()))));
        }
        Label nameLabel = new Label("x" + item.getQuantity(), menuManager.getPixthulhuSkin());
        nameLabel.setAlignment(Align.bottom);
        stack.add(nameLabel);

        Button button = new Button(new Button.ButtonStyle());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fromInventory) {
                    if (item.isEdible()) {
                        showConfirmationDialog("Move to Refrigerator?", () -> {
                            cookController.handleRefrigerator(player, new String[]{"put", item.getName()});
                            updateContent();
                        });
                    } else {
                        showResultDialog("This item is not edible and cannot be stored in the refrigerator.");
                    }
                } else {
                    showConfirmationDialog("Move to Inventory?", () -> {
                        String result = cookController.handleRefrigerator(player, new String[]{"pick", item.getName()});
                        if (!result.contains("successfully")) {
                            showResultDialog(result);
                        }
                        updateContent();
                    });
                }
            }
        });
        stack.add(button);
        return stack;
    }

    private void showConfirmationDialog(String message, Runnable onConfirm) {
        Dialog dialog = new Dialog("Confirm", menuManager.getPixthulhuSkin()) {
            @Override
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    onConfirm.run();
                }
            }
        };
        dialog.text(message);
        dialog.button("Yes", true);
        dialog.button("No", false);
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
