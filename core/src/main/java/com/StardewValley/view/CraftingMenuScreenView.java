package com.StardewValley.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap; // Needed for creating 1x1 pixel textures
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton; // Essential for clickable sprites
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable; // For general drawable types
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling; // NEW: Import Scaling
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.StardewValley.Main;
import com.StardewValley.controller.HomeController;
import com.StardewValley.models.Recipe;
import com.StardewValley.models.User;
import com.StardewValley.models.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CraftingMenuScreenView implements Screen {
    private final User player;
    private final GameView gameView;
    private Stage stage;
    private Skin skin;
    private Recipe selectedRecipe;
    private Texture grassStarterTexture;
    private Label recipeNameLabel;
    private Label ingredientsLabel;
    private Label detailsLabel;
    private TextButton craftButton;
    private Label messageLabel;
    private Table recipeListTable;
    // REMOVED: private Table inventoryGridTable; // Inventory grid table is removed

    private Map<String, Integer> playerInventory;
    private List<Recipe> allRecipesForDisplay;
    private Map<String, Map<String, Integer>> currentUnlockedRecipes;

    // Main panel background (background.png)
    private Texture backgroundTexture;
    private Drawable backgroundDrawable;

    // Individual slot background (slot.png) - Loaded ONCE globally
    private Texture slotTexture;
    private Drawable slotDrawable;

    // Placeholder for missing item/recipe icons (1x1 pixel)
    private Texture placeholderTexture;
    private TextureRegionDrawable placeholderDrawable;

    // Textures for Pixmap-based ColorDrawable fallbacks (if images fail to load)
    private Texture fallbackBackgroundTexture;
    private Texture fallbackSlotTexture;

    private TextureAtlas itemAtlas;
    private Map<String, TextureRegion> itemIconRegions;

    private static final Color LOCKED_RECIPE_COLOR = new Color(0.5f, 0.5f, 0.5f, 1f); // Darker gray
    private static final Color UNLOCKED_RECIPE_COLOR = Color.WHITE; // Normal white

    // Define consistent sizes for slots and icons
    private static final float RECIPE_SLOT_SIZE = 90f; // Overall slot size for recipes (now without text, can adjust)
    private static final float RECIPE_ICON_SIZE = 70f; // Icon size within the recipe slot


    public CraftingMenuScreenView(User player, GameView gameView) {
        this.player = player;
        this.gameView = gameView;
        Gdx.app.log("CraftingMenuScreenView", "CraftingMenuScreenView created for user: " + player.getUsername());
    }

    // Helper to create a 1x1 pixel Pixmap of a given color
    private Pixmap createPixmap(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return pixmap;
    }

    @Override
    public void show() {
        Gdx.app.log("CraftingMenuScreenView", "show() called.");
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("assets/Pixthulhu/skin/pixthulhu-ui.json"));
        this.selectedRecipe = null;
        Gdx.input.setInputProcessor(stage);

        // Load main panel background texture (background.png)
        try {
            backgroundTexture = new Texture(Gdx.files.internal("assets/Background/layers/background.png"));
            backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        } catch (Exception e) {
            Gdx.app.error("CraftingMenuScreenView", "Failed to load main background texture: " + e.getMessage());
            // Fallback to a solid color if background.png fails
            fallbackBackgroundTexture = new Texture(createPixmap(new Color(0.1f, 0.2f, 0.3f, 1f)));
            backgroundDrawable = new TextureRegionDrawable(new TextureRegion(fallbackBackgroundTexture));
            Gdx.app.log("CraftingMenuScreenView", "Using Pixmap-based ColorDrawable as fallback for main background.");
        }

        // Load individual slot background texture (slot.png) - LOADED ONCE HERE
        try {
            slotTexture = new Texture(Gdx.files.internal("assets/Background/layers/slot.png"));
            slotDrawable = new TextureRegionDrawable(new TextureRegion(slotTexture));
        } catch (Exception e) {
            Gdx.app.error("CraftingMenuScreenView", "Failed to load slot texture: " + e.getMessage());
            // Fallback to a solid color if slot.png fails
            fallbackSlotTexture = new Texture(createPixmap(new Color(0.3f, 0.3f, 0.3f, 1f)));
            slotDrawable = new TextureRegionDrawable(new TextureRegion(fallbackSlotTexture));
            Gdx.app.log("CraftingMenuScreenView", "Using Pixmap-based ColorDrawable as fallback for slot background.");
        }

        // Create a 1x1 white pixel texture for generic placeholders (for missing icons)
        try {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            placeholderTexture = new Texture(pixmap);
            pixmap.dispose();
            placeholderDrawable = new TextureRegionDrawable(new TextureRegion(placeholderTexture));
            Gdx.app.log("CraftingMenuScreenView", "Placeholder texture created successfully.");
        } catch (Exception e) {
            Gdx.app.error("CraftingMenuScreenView", "Failed to create placeholder texture: " + e.getMessage());
            placeholderDrawable = null; // Should not happen, but as a last resort
        }

        try {
            itemAtlas = new TextureAtlas(Gdx.files.internal("assets/crafting.atlas"));
            itemIconRegions = new HashMap<>();
            // Populate itemIconRegions map from the atlas
            // IMPORTANT: These names must EXACTLY match the region names in your .atlas file.
            itemIconRegions.put("Wood", itemAtlas.findRegion("Wood"));
            itemIconRegions.put("Stone", itemAtlas.findRegion("Stone"));
            itemIconRegions.put("Coal", itemAtlas.findRegion("Coal"));
            itemIconRegions.put("Iron_Bar", itemAtlas.findRegion("Iron_Bar"));
            itemIconRegions.put("Maple_Syrup", itemAtlas.findRegion("Maple_Syrup"));
            itemIconRegions.put("Bee_House", itemAtlas.findRegion("Bee_House"));
            itemIconRegions.put("Scarecrow", itemAtlas.findRegion("Scarecrow"));
            itemIconRegions.put("Sprinkler", itemAtlas.findRegion("Sprinkler"));
            itemIconRegions.put("Quality_Sprinkler", itemAtlas.findRegion("Quality_Sprinkler"));
            itemIconRegions.put("Iridium_Sprinkler", itemAtlas.findRegion("Iridium_Sprinkler"));
            itemIconRegions.put("Cherry_Bomb", itemAtlas.findRegion("Cherry_Bomb"));
            itemIconRegions.put("Bomb", itemAtlas.findRegion("Bomb"));
            itemIconRegions.put("Mega_Bomb", itemAtlas.findRegion("Mega_Bomb"));
            itemIconRegions.put("Charcoal_Kiln", itemAtlas.findRegion("Charcoal_Kiln"));
            itemIconRegions.put("Deluxe_Scarecrow", itemAtlas.findRegion("Deluxe_Scarecrow"));
            itemIconRegions.put("Keg", itemAtlas.findRegion("Keg"));
            itemIconRegions.put("Oil_Maker", itemAtlas.findRegion("Oil_Maker"));
            itemIconRegions.put("Preserves_Jar", itemAtlas.findRegion("Preserves_Jar"));
            itemIconRegions.put("Dehydrator", itemAtlas.findRegion("Dehydrator"));
            itemIconRegions.put("Grass_Starter", itemAtlas.findRegion("Grass_Starter"));
            itemIconRegions.put("Fish_Smoker", itemAtlas.findRegion("Fish_Smoker"));
            itemIconRegions.put("Mystic_Tree_Seed", itemAtlas.findRegion("Mystic_Tree_Seed"));

            Gdx.app.log("CraftingMenuScreenView", "Item atlas loaded successfully.");

        } catch (Exception e) {
            Gdx.app.error("CraftingMenuScreenView", "Failed to load item atlas: " + e.getMessage());
            itemAtlas = null;
        }

        updateInventoryAndRecipes();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.debug();
        rootTable.pad(10);

        Table topPanel = new Table(skin);
        topPanel.setBackground(backgroundDrawable); // Uses background.png or its Pixmap-based fallback
        topPanel.pad(10);
        topPanel.top().left();

        recipeListTable = new Table(skin);
        recipeListTable.align(Align.top);
        // No defaults here, each recipeSlot will define its own size and padding.

        ScrollPane recipeScrollPane = new ScrollPane(recipeListTable, skin);
        recipeScrollPane.setFadeScrollBars(false);
        recipeScrollPane.setScrollingDisabled(false, false); // Allow scrolling if many recipes

        topPanel.add(new Label("Crafting Recipes:", skin, "title")).left().padBottom(5).row();
        topPanel.add(recipeScrollPane).expand().fill().row();


        Table rightPanel = new Table(skin);
        rightPanel.setBackground(backgroundDrawable); // Uses background.png or its Pixmap-based fallback
        rightPanel.pad(10);
        rightPanel.top().left();

        recipeNameLabel = new Label("Select a recipe", skin, "title");
        rightPanel.add(recipeNameLabel).growX().padBottom(10).row();

        ingredientsLabel = new Label("Ingredients:\n", skin); // Added newline for initial formatting
        ingredientsLabel.setWrap(true);
        rightPanel.add(ingredientsLabel).growX().padBottom(5).row();

        detailsLabel = new Label("Details:", skin);
        detailsLabel.setWrap(true);
        rightPanel.add(detailsLabel).growX().padBottom(20).row();

        craftButton = new TextButton("Craft", skin);
        craftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedRecipe != null) {
                    attemptCrafting(selectedRecipe);
                } else {
                    messageLabel.setText("Please select a recipe first!");
                    messageLabel.setColor(Color.YELLOW);
                }
            }
        });
        rightPanel.add(craftButton).width(200).height(60).padBottom(10).row();

        messageLabel = new Label("", skin);
        messageLabel.setWrap(true);
        rightPanel.add(messageLabel).growX().padBottom(10).row();

        rightPanel.add(new Label("Press 'C' or 'Escape' to exit", skin)).expandY().bottom().row();

        // Adjust panel heights for better visual balance and larger recipe area
        // The sizes here are percentages of the screen height/width.
        rootTable.add(topPanel).expand().fill().width(Gdx.graphics.getWidth() * 0.6f); // Recipes take more space, expand height to fill available
        rootTable.add(rightPanel).expand().fill().width(Gdx.graphics.getWidth() * 0.4f).row(); // Right panel fills remaining height

        // REMOVED: The row for the bottomPanel
        // rootTable.add(bottomPanel).expandX().fillX().height(Gdx.graphics.getHeight() * 0.35f).colspan(2).row();

        stage.addActor(rootTable);

        populateRecipeList();
        updateRecipeDetails();
        Gdx.app.log("CraftingMenuScreenView", "Crafting menu UI setup complete.");
    }

    private int getDummyEnergyCost(String recipeNameKey) {
        return 2;
    }

    private void updateInventoryAndRecipes() {
        Gdx.app.log("CraftingMenuScreenView", "updateInventoryAndRecipes() called.");
        playerInventory = HomeController.getInventoryItemMap(player); // Keep this line as it's needed for canCraft
        Gdx.app.log("CraftingMenuScreenView", "Player inventory fetched. Items: " + playerInventory.keySet());

        Map<String, Map<String, Integer>> lockedRecipesMap = HomeController.getLockedRecipes();
        currentUnlockedRecipes = HomeController.getUnlockedRecipes();

        allRecipesForDisplay = new ArrayList<>();

        // Add locked recipes first, then unlocked, to maintain consistent display if sorting changes
        for (Map.Entry<String, Map<String, Integer>> entry : lockedRecipesMap.entrySet()) {
            String recipeNameKey = entry.getKey();
            // Skip if this recipe is actually unlocked
            if (currentUnlockedRecipes.containsKey(recipeNameKey)) continue;

            Map<String, Integer> ingredientsMap = entry.getValue();
            int energy = getDummyEnergyCost(recipeNameKey);
            String source = "Crafting";
            int sellPrice = 0;
            allRecipesForDisplay.add(new Recipe(recipeNameKey.replace("_", " "), ingredientsMap, energy, source, sellPrice));
        }

        for (Map.Entry<String, Map<String, Integer>> entry : currentUnlockedRecipes.entrySet()) {
            String recipeNameKey = entry.getKey();
            Map<String, Integer> ingredientsMap = entry.getValue();
            int energy = getDummyEnergyCost(recipeNameKey);
            String source = "Crafting";
            int sellPrice = 0;
            allRecipesForDisplay.add(new Recipe(recipeNameKey.replace("_", " "), ingredientsMap, energy, source, sellPrice));
        }

        allRecipesForDisplay.sort((r1, r2) -> {
            boolean r1Unlocked = currentUnlockedRecipes.containsKey(r1.getName().replace(" ", "_"));
            boolean r2Unlocked = currentUnlockedRecipes.containsKey(r2.getName().replace(" ", "_"));

            // Unlocked recipes first
            if (r1Unlocked && !r2Unlocked) return -1;
            if (!r1Unlocked && r2Unlocked) return 1;

            // Then sort alphabetically
            return r1.getName().compareToIgnoreCase(r2.getName());
        });

        Gdx.app.log("CraftingMenuScreenView", "All recipes populated for display. Count: " + allRecipesForDisplay.size());
        Gdx.app.log("CraftingMenuScreenView", "Unlocked recipes count: " + currentUnlockedRecipes.size());
    }

    private void populateRecipeList() {
        Gdx.app.log("CraftingMenuScreenView", "populateRecipeList() called. allRecipesForDisplay size: " + allRecipesForDisplay.size());
        recipeListTable.clearChildren();

        int recipeColumns = 4; // Number of columns in the recipe grid

        if (allRecipesForDisplay.isEmpty()) {
            recipeListTable.add(new Label("No recipes available.", skin)).colspan(recipeColumns).center().pad(20);
            Gdx.app.log("CraftingMenuScreenView", "No recipes to display.");
            return;
        }

        int i = 0;
        for (final Recipe recipe : allRecipesForDisplay) {
            Table recipeSlotWrapper = new Table(skin);
            recipeSlotWrapper.setBackground(slotDrawable); // Set the background for the wrapper table
            recipeSlotWrapper.defaults().center(); // Center content within this wrapper table

            boolean isUnlocked = currentUnlockedRecipes.containsKey(recipe.getName().replace(" ", "_"));

            TextureRegion recipeIconRegion = null;
            if (itemAtlas != null) {
                recipeIconRegion = itemAtlas.findRegion(recipe.getName().replace(" ", "_"));
                if (recipeIconRegion == null) {
                    Gdx.app.log("CraftingMenuScreenView", "Icon not found in atlas for recipe: " + recipe.getName());
                }
            }

            // Manually create ImageButton.ImageButtonStyle
            ImageButton.ImageButtonStyle recipeButtonStyle = new ImageButton.ImageButtonStyle();

            if (recipeIconRegion != null) {
                recipeButtonStyle.imageUp = new TextureRegionDrawable(recipeIconRegion);
                recipeButtonStyle.imageDown = new TextureRegionDrawable(recipeIconRegion);
                recipeButtonStyle.imageChecked = new TextureRegionDrawable(recipeIconRegion);
            } else {
                recipeButtonStyle.imageUp = placeholderDrawable;
                recipeButtonStyle.imageDown = placeholderDrawable;
                recipeButtonStyle.imageChecked = placeholderDrawable;
                if (recipeButtonStyle.imageUp instanceof TextureRegionDrawable) {
                    ((TextureRegionDrawable) recipeButtonStyle.imageUp).tint(Color.RED);
                }
            }

            final ImageButton recipeButton = new ImageButton(recipeButtonStyle);
            recipeButton.getImage().setScaling(Scaling.fit);
            recipeButton.getImage().setColor(isUnlocked ? UNLOCKED_RECIPE_COLOR : LOCKED_RECIPE_COLOR);

            // Inside populateRecipeList()
            recipeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float floatY) {
                    Gdx.app.log("CraftingMenuScreenView", "CLICKED: Recipe button for '" + recipe.getName() + "' clicked.");
                    selectedRecipe = recipe;
                    // IMMEDIATE CHECK AFTER SETTING selectedRecipe
                    if (selectedRecipe != null) {
                        if (selectedRecipe.getIngredients() == null) {
                            Gdx.app.error("CraftingMenuScreenView", "IMMEDIATE NULL INGREDIENTS AFTER CLICK: selectedRecipe.getIngredients() is NULL right after setting for: '" + selectedRecipe.getName() + "'");
                            new Exception("DEBUG: Stack trace for immediate NULL ingredients after click").printStackTrace();
                        } else {
                            Gdx.app.log("CraftingMenuScreenView", "IMMEDIATE CHECK: selectedRecipe.getIngredients() is NOT NULL after setting for: '" + selectedRecipe.getName() + "'.");
                        }
                    } else {
                        Gdx.app.log("CraftingMenuScreenView", "IMMEDIATE NULL selectedRecipe AFTER CLICK: selectedRecipe became NULL after assignment (unexpected).");
                    }
                    updateRecipeDetails(); // This call will now also log its entry
                }
            });

            // Add the ImageButton (icon) directly to the wrapper table.
            recipeSlotWrapper.add(recipeButton).size(RECIPE_ICON_SIZE, RECIPE_ICON_SIZE).pad(2); // No .row() here as we want only the icon

            // Add the completed recipeSlotWrapper to the main recipeListTable
            recipeListTable.add(recipeSlotWrapper).size(RECIPE_SLOT_SIZE, RECIPE_SLOT_SIZE).pad(5);

            i++;
            if (i % recipeColumns == 0) {
                recipeListTable.row();
            }
            Gdx.app.log("CraftingMenuScreenView", "Added recipe slot for: " + recipe.getName() + " (Unlocked: " + isUnlocked + ")");
        }
    }


    // Inside com.StardewValley.view.CraftingMenuScreenView.java

// ... (your existing code)

    private void updateRecipeDetails() {
        Gdx.app.log("CraftingMenuScreenView", ">> Entering updateRecipeDetails() method. Timestamp: " + System.currentTimeMillis());

        if (selectedRecipe == null) {
            Gdx.app.log("CraftingMenuScreenView", "  selectedRecipe is NULL. Setting default UI elements.");
            recipeNameLabel.setText("Select a recipe");
            ingredientsLabel.setText("Ingredients:");
            detailsLabel.setText("Details:");
            craftButton.setDisabled(true);
            craftButton.setColor(Color.GRAY);
            messageLabel.setText("");
            return;
        }

        // selectedRecipe is NOT null at this point
        Gdx.app.log("CraftingMenuScreenView", "  selectedRecipe is NOT NULL. Name: '" + selectedRecipe.getName() + "'. Class: " + selectedRecipe.getClass().getName());

        // Attempt to get ingredients map
        Map<String, Integer> recipeIngredients = selectedRecipe.getIngredients();

        // THIS IS THE CRITICAL CHECKPOINT
        if (recipeIngredients == null) {
            // This block should theoretically *never* be hit if Recipe.java is compiled correctly.
            Gdx.app.error("CraftingMenuScreenView", "!!! CRITICAL ERROR: selectedRecipe.getIngredients() returned NULL for recipe: '" + selectedRecipe.getName() + "' !!!");
            Gdx.app.error("CraftingMenuScreenView", "!!! This means the Recipe object's internal ingredients map is null. !!!");
            // Print a stack trace to see how this method was called, might give clues if called at a strange time.
            new Exception("DEBUG: Stack trace for NULL ingredients from " + selectedRecipe.getName()).printStackTrace();

            // Defensive fallback for the UI
            recipeNameLabel.setText(selectedRecipe.getName() + " (DATA ERROR)");
            ingredientsLabel.setText("Ingredients: (Error loading data)");
            detailsLabel.setText("Details: (Error)");
            craftButton.setDisabled(true);
            craftButton.setColor(Color.GRAY);
            messageLabel.setText("Recipe data corrupted. Cannot craft.");
            messageLabel.setColor(Color.RED);
            return; // Prevent the NullPointerException
        }

        // If we reach here, recipeIngredients is definitely NOT null.
        Gdx.app.log("CraftingMenuScreenView", "  selectedRecipe.getIngredients() is NOT NULL. Size: " + recipeIngredients.size() + ". Proceeding to entrySet().");
        // This is the line that caused the original crash.
        // If it still crashes here, it implies recipeIngredients somehow became null *between* the if check and this line,
        // which is practically impossible in single-threaded Java unless an external thread interfered, or memory corruption.
        StringBuilder ingredientsText = new StringBuilder("Ingredients:\n");
        for (Map.Entry<String, Integer> entry : recipeIngredients.entrySet()) { // <-- This is original line 385 or close to it
            String ingredientName = entry.getKey().replace("_", " ");
            int requiredQuantity = entry.getValue();
            int ownedQuantity = playerInventory.getOrDefault(entry.getKey(), 0);
            ingredientsText.append("- ").append(ingredientName).append(" (").append(requiredQuantity).append(")");
            ingredientsText.append(" (You have: ").append(ownedQuantity).append(")\n");
        }
        ingredientsLabel.setText(ingredientsText.toString());

        StringBuilder detailsText = new StringBuilder();
        detailsText.append("Energy Cost: ").append(selectedRecipe.getEnergy()).append("\n");
        detailsText.append("Source: ").append(selectedRecipe.getSource()).append("\n");
        detailsLabel.setText(detailsText.toString());

        boolean isSelectedRecipeUnlocked = currentUnlockedRecipes.containsKey(selectedRecipe.getName().replace(" ", "_"));

        if (isSelectedRecipeUnlocked && canCraft(selectedRecipe)) {
            craftButton.setDisabled(false);
            craftButton.setColor(Color.GREEN);
            messageLabel.setText("Ready to be crafted!");
            messageLabel.setColor(Color.GREEN);
        } else if (!isSelectedRecipeUnlocked) {
            craftButton.setDisabled(true);
            craftButton.setColor(Color.GRAY);
            messageLabel.setText("Recipe not unlocked!");
            messageLabel.setColor(Color.YELLOW);
        } else {
            craftButton.setDisabled(true);
            craftButton.setColor(Color.GRAY);
            messageLabel.setText("Not enough ingredients.");
            messageLabel.setColor(Color.RED);
        }
        Gdx.app.log("CraftingMenuScreenView", "<< Exiting updateRecipeDetails() method.");
    }

    private boolean canCraft(Recipe recipe) {
        if (recipe == null) return false;
        boolean isUnlocked = currentUnlockedRecipes.containsKey(recipe.getName().replace(" ", "_"));
        if (!isUnlocked) {
            return false;
        }

        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String ingredientName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int owned = playerInventory.getOrDefault(ingredientName, 0);
            if (owned < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    private void attemptCrafting(Recipe recipe) {
        Gdx.app.log("CraftingMenuScreenView", "Attempting to craft: " + recipe.getName());

        boolean isUnlocked = currentUnlockedRecipes.containsKey(recipe.getName().replace(" ", "_"));
        if (!isUnlocked) {
            messageLabel.setText("Cannot craft: Recipe is locked!");
            messageLabel.setColor(Color.YELLOW);
            Gdx.app.log("Crafting", "Attempted to craft locked recipe: " + recipe.getName());
            return;
        }
        String result = HomeController.crafting("craft", recipe.getName().replace(" ", "_"), this.player);
        if (result.startsWith("Successfully crafted")) {
            messageLabel.setText(result);
            messageLabel.setColor(Color.CYAN);
            Gdx.app.log("Crafting", result);
            updateInventoryAndRecipes(); // Re-fetch inventory and recipes
            populateRecipeList();       // Re-draw recipe list
            updateRecipeDetails();      // Re-draw recipe details (in case selected recipe changed or became craftable)
            // REMOVED: updateInventoryDisplay() call
            // updateInventoryDisplay();
        } else {
            messageLabel.setText(result);
            messageLabel.setColor(Color.RED);
            Gdx.app.log("Crafting", "Failed to craft: " + result);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.C) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameView.showMapView();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        Gdx.app.log("CraftingMenuScreenView", "Resized to " + width + "x" + height);
    }

    @Override
    public void pause() {
        Gdx.app.log("CraftingMenuScreenView", "Crafting menu paused.");
    }

    @Override
    public void resume() {
        Gdx.app.log("CraftingMenuScreenView", "Crafting menu resumed.");
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        Gdx.app.log("CraftingMenuScreenView", "Hiding crafting menu.");
        //dispose(); // Clean up resources - commented out based on previous discussions
    }

    @Override
    public void dispose() {
        Gdx.app.log("CraftingMenuScreenView", "Disposing resources.");
        stage.dispose();
        // skin.dispose(); // Do not dispose the shared skin here - commented out based on previous discussions
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (slotTexture != null) slotTexture.dispose(); // Still needed for recipe slots
        if (placeholderTexture != null) placeholderTexture.dispose();
        if (fallbackBackgroundTexture != null) fallbackBackgroundTexture.dispose();
        if (fallbackSlotTexture != null) fallbackSlotTexture.dispose(); // Still needed for recipe slots
        if (grassStarterTexture != null) grassStarterTexture.dispose();
        // if (itemAtlas != null) itemAtlas.dispose(); // Do not dispose the shared atlas here - commented out based on previous discussions
    }
}
