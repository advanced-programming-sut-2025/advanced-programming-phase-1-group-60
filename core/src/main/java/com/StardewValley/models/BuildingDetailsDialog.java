// com.StardewValley.view.ui.BuildingDetailsDialog.java (or similar path)
package com.StardewValley.models;

import com.StardewValley.controller.GamePlayController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BuildingDetailsDialog extends Dialog {

    private GamePlayController gamePlayController;
    private PlaceableGameBuilding selectedBuilding;
    private Skin skin;
    private Label recipeInfoLabel;
    private SelectBox<String> inventorySelectBox;
    private SelectBox<Integer> useItemQuantitySelectBox;
    private Label currentSelectedItemQuantityLabel;
    private Item selectedInventoryItem;
    private TextButton startProductionButton;
    private TextButton getProductionButton;
    private Label buildingNameLabel;

    private ProgressBar productionProgressBar;
    private Label productionPercentageLabel;
    private Label productionStatusLabel;

    private TextButton instantProduceCheatButton;
    // RENAME: from demolishButton to cancelProductionButton
    private TextButton cancelProductionButton;


    public BuildingDetailsDialog(String title, Skin skin, GamePlayController gamePlayController) {
        super(title, skin);
        this.gamePlayController = gamePlayController;
        this.skin = skin;
        setModal(true);
        pad(20);
        setSize(500, 680); // Increased height to accommodate new button/changed button
        setMovable(true);
        setResizable(true);

        initializeUI();
    }

    private void initializeUI() {
        Table contentTable = getContentTable();
        contentTable.clear();

        buildingNameLabel = new Label("Building: ", skin);
        contentTable.add(buildingNameLabel).padBottom(15).colspan(2).row();

        productionStatusLabel = new Label("Status: Idle", skin);
        contentTable.add(productionStatusLabel).colspan(2).padBottom(5).row();

        productionProgressBar = new ProgressBar(0f, 1f, 0.01f, false, skin);
        productionProgressBar.setValue(0);
        contentTable.add(new Label("Progress:", skin)).left().padRight(10);
        contentTable.add(productionProgressBar).growX().padBottom(5).row();

        productionPercentageLabel = new Label("0%", skin);
        contentTable.add(productionPercentageLabel).colspan(2).center().padBottom(15).row();
        recipeInfoLabel = new Label("Recipes:", skin);
        recipeInfoLabel.setWrap(true);
        recipeInfoLabel.setAlignment(Align.topLeft);
        contentTable.add(recipeInfoLabel).colspan(2).growX().padBottom(15).row();
        contentTable.add(new Label("Select Item:", skin)).left().padRight(10);
        inventorySelectBox = new SelectBox<>(skin);
        inventorySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedInputMaterialName = inventorySelectBox.getSelected();
                selectedInventoryItem = null;
                int quantityInInventory = 0;

                User currentPlayer = gamePlayController.getUser();
                if (currentPlayer != null && selectedInputMaterialName != null) {
                    Optional<Item> itemOptional = currentPlayer.getInventory().getItems().stream()
                        .filter(item -> item.getName().equals(selectedInputMaterialName))
                        .findFirst();
                    if (itemOptional.isPresent()) {
                        selectedInventoryItem = itemOptional.get();
                        quantityInInventory = selectedInventoryItem.getQuantity();
                    }
                }
                currentSelectedItemQuantityLabel.setText("In Inventory: " + quantityInInventory);
                if (!useItemQuantitySelectBox.getItems().isEmpty()) {
                    useItemQuantitySelectBox.setSelectedIndex(0);
                }
            }
        });
        contentTable.add(inventorySelectBox).width(250).height(50).padBottom(10).row();

        contentTable.add(new Label("Quantity:", skin)).left().padRight(10);
        useItemQuantitySelectBox = new SelectBox<>(skin);
        Array<Integer> quantities = new Array<>();
        quantities.add(1);
        quantities.add(5);
        quantities.add(10);
        useItemQuantitySelectBox.setItems(quantities);
        contentTable.add(useItemQuantitySelectBox).width(150).height(50).padBottom(15).row();

        currentSelectedItemQuantityLabel = new Label("In Inventory: 0", skin);
        contentTable.add(currentSelectedItemQuantityLabel).colspan(2).padBottom(15).row();

        TextButton useGenericItemButton = new TextButton("Use Item", skin);
        useGenericItemButton.setName("useGenericItemButton");
        useGenericItemButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedBuilding != null && selectedInventoryItem != null) {
                    int quantityToUse = useItemQuantitySelectBox.getSelected();

                    if (quantityToUse <= 0) {
                        showResultDialog("Please select a valid quantity.");
                        return;
                    }

                    User currentPlayer = gamePlayController.getUser();
                    if (currentPlayer != null && currentPlayer.getInventory().hasItem(selectedInventoryItem.getName(), quantityToUse)) {
                        System.out.println("Using " + quantityToUse + " of " + selectedInventoryItem.getName() + " on " + selectedBuilding.getName());
                        currentPlayer.getInventory().removeItemByName(selectedInventoryItem.getName(), quantityToUse);
                        showResultDialog("You used " + quantityToUse + " " + selectedInventoryItem.getName() + " on the " + selectedBuilding.getName() + "!");
                        hide();
                    } else {
                        showResultDialog("You don't have enough " + selectedInventoryItem.getName() + " or it's not selected.");
                    }
                } else {
                    showResultDialog("Please select an item and ensure a building is selected.");
                }
            }
        });
        contentTable.add(useGenericItemButton).width(200).height(60).pad(5).colspan(2).row();

        startProductionButton = new TextButton("Start Production", skin);

        startProductionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedBuilding != null && selectedInventoryItem != null) {
                    String outputProductName = CraftingData.getOutputProductForInputAndBuilding(selectedBuilding.getName(), selectedInventoryItem.getName());
                    Map<String, Integer> materialsForOneUnit = CraftingData.requiredMaterials.get(outputProductName);
                    int quantityToUse = materialsForOneUnit.get(selectedInventoryItem.getName());
                    User currentPlayer = gamePlayController.getUser();
                    if (quantityToUse > currentPlayer.getInventory().getItemQuantityByName(selectedInventoryItem.getName())) {
                        showResultDialog("Quantity to use must be at least "+quantityToUse);
                        return;
                    }


                    if (currentPlayer != null) {

                        if (outputProductName == null) {
                            showResultDialog("This building cannot produce an item from " + selectedInventoryItem.getName() + ".");
                            return;
                        }

                        if (!currentPlayer.getInventory().hasItem(selectedInventoryItem.getName(), quantityToUse)) {
                            showResultDialog("You don't have enough " + selectedInventoryItem.getName() + ".");
                            return;
                        }

                        String result = "";
                        boolean success = true;
                        // Assuming each call to processArtisanUse is for one unit of production,
                        // and materials are consumed per unit.
                        // If quantityToUse means producing 'quantityToUse' products in one go,
                        // you might need to adjust processArtisanUse to handle quantity.
                        for (int i = 0; i < quantityToUse; i++) { // Loop to start multiple productions
                            // Get required materials for the SINGLE unit production (before consuming)
                            if (materialsForOneUnit != null && currentPlayer.getInventory().hasMaterials(materialsForOneUnit)) {
                                result = gamePlayController.processArtisanUse(selectedBuilding.getName(), outputProductName);
                                if (result.startsWith("Error")) {
                                    success = false;
                                    break;
                                }
                            } else {
                                result = "Error: Not enough materials for all requested productions.";
                                success = false;
                                break;
                            }
                        }

                        showResultDialog(result);
                        if (success) {
                            currentPlayer.getInventory().removeItemByName(selectedInventoryItem.getName(),quantityToUse);
                            hide();
                        }
                    } else {
                        showResultDialog("Player not found.");
                    }
                } else {
                    showResultDialog("Please select an item to start production.");
                }
            }
        });
        contentTable.add(startProductionButton).width(200).height(60).pad(5).colspan(2).row();

        getProductionButton = new TextButton("Get Product", skin);
        getProductionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedBuilding != null) {
                    String result = gamePlayController.processArtisanGet(selectedBuilding.getName());
                    showResultDialog(result);
                    if (!result.startsWith("Error")) {
                        hide();
                    }
                }
            }
        });
        contentTable.add(getProductionButton).width(200).height(60).pad(5).colspan(2).row();

        instantProduceCheatButton = new TextButton("CHEAT", skin);
        instantProduceCheatButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedBuilding != null && gamePlayController.isArtisanBuilding(selectedBuilding.getName())) {
                    ProductionTask task = gamePlayController.getProductionTask(selectedBuilding.getName());
                    if (task != null && !task.isComplete()) {
                        task.setComplete(true);
                        User currentPlayer = gamePlayController.getUser();
                        if (currentPlayer != null) {
                            String productName = task.getItemName();
                            int quantity = 1;
                            Item producedItem = new Item(productName, quantity);
                            currentPlayer.getInventory().addItem(producedItem);
                            gamePlayController.removeProductionTask(selectedBuilding.getName());
                            showResultDialog("Cheat activated! " + quantity + " " + productName + "(s) instantly produced!");
                            updateBuildingState();
                        } else {
                            showResultDialog("Error: Player not found for cheat.");
                        }
                    } else {
                        showResultDialog("No active production task to instantly complete on this building.");
                    }
                } else {
                    showResultDialog("This is not an artisan building or no building selected.");
                }
            }
        });
        contentTable.add(instantProduceCheatButton).width(200).height(60).pad(5).colspan(2).row();
        instantProduceCheatButton.setVisible(false);

        // RENAME AND REFUNCTIONALIZE: Demolish button to Cancel Production
        cancelProductionButton = new TextButton("Cancel", skin);
        cancelProductionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedBuilding != null && gamePlayController.isArtisanBuilding(selectedBuilding.getName())) {
                    ProductionTask taskToCancel = gamePlayController.getProductionTask(selectedBuilding.getName());
                    if (taskToCancel != null && !taskToCancel.isComplete()) {
                        User currentPlayer = gamePlayController.getUser();
                        if (currentPlayer != null) {
                            Map<String, Integer> refundedMaterials = taskToCancel.getConsumedMaterials();
                            if (refundedMaterials != null && !refundedMaterials.isEmpty()) {
                                gamePlayController.addItemsToUserInventory(refundedMaterials);
                                showResultDialog("Production canceled for " + selectedBuilding.getName() + " and materials refunded!");
                            } else {
                                showResultDialog("Production canceled for " + selectedBuilding.getName() + " (no materials to refund or already refunded).");
                            }
                            gamePlayController.removeProductionTask(selectedBuilding.getName());

                            updateBuildingState(); // Refresh UI
                            hide(); // Hide the dialog after canceling
                        } else {
                            showResultDialog("Error: Player not found for canceling production.");
                        }
                    } else {
                        showResultDialog("No active production to cancel on this building.");
                    }
                } else {
                    showResultDialog("This is not an artisan building or no building selected with an active production.");
                }
            }
        });
        contentTable.add(cancelProductionButton).width(200).height(60).pad(5).colspan(2).row();
        cancelProductionButton.setVisible(false); // Initially hide

        // You might still want a Demolish button, perhaps as a separate button or
        // if this button remains the 'Demolish' one for non-artisan buildings.
        // For now, the old Demolish functionality is removed from here.

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
        contentTable.add(closeButton).width(200).height(60).pad(5).colspan(2).row();

        getButtonTable().add(closeButton);
        button("Close");
        key(com.badlogic.gdx.Input.Keys.ESCAPE, "Close");
    }

    public void showForBuilding(PlaceableGameBuilding building, com.badlogic.gdx.scenes.scene2d.Stage stage) {
        this.selectedBuilding = building;
        buildingNameLabel.setText("Building: " + building.getName());
        updateBuildingState();
        show(stage);
        setPosition(Gdx.graphics.getWidth() / 2f - getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - getHeight() / 2f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isVisible()) {
            updateBuildingState();
        }
    }

    private void updateBuildingState() {
        if (selectedBuilding == null) return;

        boolean isArtisanBuilding = gamePlayController.isArtisanBuilding(selectedBuilding.getName());
        boolean isProducing = false;

        if (isArtisanBuilding) {
            isProducing = gamePlayController.isArtisanProducing(selectedBuilding.getName());
        }

        inventorySelectBox.setVisible(false);
        useItemQuantitySelectBox.setVisible(false);
        currentSelectedItemQuantityLabel.setVisible(false);
        findActor("useGenericItemButton").setVisible(false);
        startProductionButton.setVisible(false);
        recipeInfoLabel.setVisible(false);
        getProductionButton.setVisible(false);
        instantProduceCheatButton.setVisible(false);
        cancelProductionButton.setVisible(false); // NEW: Hide cancel button by default

        productionProgressBar.setVisible(false);
        productionPercentageLabel.setVisible(false);
        productionStatusLabel.setVisible(false);


        if (isArtisanBuilding) {
            productionProgressBar.setVisible(true);
            productionPercentageLabel.setVisible(true);
            productionStatusLabel.setVisible(true);
            recipeInfoLabel.setVisible(true);
            if (isProducing) {
                getProductionButton.setVisible(true);
                ProductionTask task = gamePlayController.getProductionTask(selectedBuilding.getName());
                if (task != null) {
                    float currentAbsHours = (float) TimeSystem.getInstance().getCurrentDay() * 24 + TimeSystem.getInstance().getCurrentHour();
                    float startAbsHours = (float) task.getStartDayInitial() * 24 + task.getStartHourActual();
                    float totalTaskHours = task.getTotalCraftingDurationHours();
                    float elapsedHours = currentAbsHours - startAbsHours;

                    float progress = 0.0f;
                    if (totalTaskHours > 0) {
                        progress = Math.min(1.0f, Math.max(0.0f, elapsedHours / totalTaskHours));
                    }

                    productionProgressBar.setValue(progress);
                    productionPercentageLabel.setText((int)(progress * 100) + "%");

                    if (progress >= 1.0f) {
                        productionStatusLabel.setText("Status: Ready to Collect!");
                        startProductionButton.setVisible(false);
                        getProductionButton.setVisible(true);
                        instantProduceCheatButton.setVisible(false);
                        cancelProductionButton.setVisible(false); // NEW: Hide cancel button when ready to collect
                    } else {
                        productionStatusLabel.setText("Status: Producing " + task.getItemName());
                        startProductionButton.setVisible(false);
                        getProductionButton.setVisible(false);
                        instantProduceCheatButton.setVisible(true);
                        cancelProductionButton.setVisible(true); // NEW: Show cancel button if producing
                    }
                } else {
                    productionStatusLabel.setText("Status: Idle (Error in task data)");
                    productionProgressBar.setValue(0);
                    productionPercentageLabel.setText("0%");
                    startProductionButton.setVisible(true);
                    instantProduceCheatButton.setVisible(false);
                    cancelProductionButton.setVisible(false); // NEW: Hide cancel button if idle (error)
                }
            } else {
                productionStatusLabel.setText("Status: Idle");
                productionProgressBar.setValue(0);
                productionPercentageLabel.setText("0%");

                inventorySelectBox.setVisible(true);
                useItemQuantitySelectBox.setVisible(true);
                currentSelectedItemQuantityLabel.setVisible(true);
                startProductionButton.setVisible(true);
                instantProduceCheatButton.setVisible(false);
                cancelProductionButton.setVisible(false); // NEW: Hide cancel button if idle

                User currentPlayer = gamePlayController.getUser();
                if (currentPlayer != null) {
                    List<String> availableInputMaterials = new ArrayList<>();
                    Set<String> addedMaterialNames = new HashSet<>();

                    Map<String, List<String>> buildingToProductsMap = new HashMap<>();
                    buildingToProductsMap.put("Bee_House", Arrays.asList("Honey"));
                    buildingToProductsMap.put("Dehydrator", Arrays.asList("Dried_Mushrooms" , "Dried_Fruit"," Raisins"));
                    buildingToProductsMap.put("Charcoal_Kiln", Arrays.asList("Coal"));
                    buildingToProductsMap.put("Oil_Maker", Arrays.asList("Truffle_Oil", "Oil"));
                    buildingToProductsMap.put("Cheese_Press", Arrays.asList("Cheese", "Goat_Cheese"));
                    buildingToProductsMap.put("Mayonnaise_Machine", Arrays.asList("Mayonnaise", "Duck_Mayonnaise", "Dinosaur_Mayonnaise"));
                    buildingToProductsMap.put("Loom", Arrays.asList("Cloth"));
                    buildingToProductsMap.put("Furnace", Arrays.asList("Coal", "Any Metal Bar"));
                    buildingToProductsMap.put("Keg", Arrays.asList("Beer", "Vinegar", "Coffee", "Mead", "Pale_Ale", "Wine", "Juice"));
                    buildingToProductsMap.put("Preserves_Jar", Arrays.asList("Pickles", "Jelly"));
                    buildingToProductsMap.put("Smoker", Arrays.asList("Smoked_Fish"));

                    List<String> productsThisBuildingCanMake = buildingToProductsMap.get(selectedBuilding.getName());
                    StringBuilder recipeText = new StringBuilder("Recipes for " + selectedBuilding.getName() + ":\n");
                    boolean hasRecipes = false;
                    if (productsThisBuildingCanMake != null) {
                        for (String productName : productsThisBuildingCanMake) {
                            Map<String, Integer> requiredMaterialsMap = CraftingData.requiredMaterials.get(productName);
                            if (requiredMaterialsMap != null) {
                                recipeText.append("- ").append(productName).append(" requires: ");
                                List<String> ingredientsList = new ArrayList<>();
                                for (Map.Entry<String, Integer> entry : requiredMaterialsMap.entrySet()) {
                                    ingredientsList.add(entry.getKey() + " x" + entry.getValue());
                                }
                                recipeText.append(String.join(", ", ingredientsList)).append("\n");
                                hasRecipes = true;
                                for (String inputMaterialName : requiredMaterialsMap.keySet()) {
                                    if (!addedMaterialNames.contains(inputMaterialName) &&
                                        currentPlayer.getInventory().hasItem(inputMaterialName, 1)) {
                                        availableInputMaterials.add(inputMaterialName);
                                        addedMaterialNames.add(inputMaterialName);
                                    }
                                }
                            }
                        }
                    }
                    if (!hasRecipes) {
                        recipeText.append("No specific recipes found for this building.");
                    }
                    recipeInfoLabel.setText(recipeText.toString());
                    inventorySelectBox.setItems(availableInputMaterials.toArray(new String[0]));
                    if (!availableInputMaterials.isEmpty()) {
                        inventorySelectBox.setSelectedIndex(0);
                        selectedInventoryItem = currentPlayer.getInventory().getItems().stream()
                            .filter(item -> item.getName().equals(inventorySelectBox.getSelected()))
                            .findFirst().orElse(null);
                        currentSelectedItemQuantityLabel.setText("In Inventory: " + (selectedInventoryItem != null ? selectedInventoryItem.getQuantity() : 0));
                    } else {
                        inventorySelectBox.setItems();
                        selectedInventoryItem = null;
                        currentSelectedItemQuantityLabel.setText("In Inventory: 0");
                    }
                }
                if (!useItemQuantitySelectBox.getItems().isEmpty()) {
                    useItemQuantitySelectBox.setSelectedIndex(0);
                }
            }
        } else {
            productionProgressBar.setVisible(false);
            productionPercentageLabel.setVisible(false);
            productionStatusLabel.setVisible(false);

            inventorySelectBox.setVisible(true);
            useItemQuantitySelectBox.setVisible(true);
            currentSelectedItemQuantityLabel.setVisible(true);
            findActor("useGenericItemButton").setVisible(true);
            recipeInfoLabel.setVisible(false);
            startProductionButton.setVisible(false);
            getProductionButton.setVisible(false);
            instantProduceCheatButton.setVisible(false);
            cancelProductionButton.setVisible(false); // NEW: Hide cancel button for non-artisan buildings


            User currentPlayer = gamePlayController.getUser();
            if (currentPlayer != null) {
                List<String> itemNames = new ArrayList<>();
                for (Item item : currentPlayer.getInventory().getItems()) {
                    itemNames.add(item.getName());
                }
                inventorySelectBox.setItems(itemNames.toArray(new String[0]));
                if (!itemNames.isEmpty()) {
                    inventorySelectBox.setSelectedIndex(0);
                    selectedInventoryItem = currentPlayer.getInventory().getItems().stream()
                        .filter(item -> item.getName().equals(inventorySelectBox.getSelected()))
                        .findFirst().orElse(null);
                    currentSelectedItemQuantityLabel.setText("In Inventory: " + (selectedInventoryItem != null ? selectedInventoryItem.getQuantity() : 0));
                } else {
                    inventorySelectBox.setItems();
                    selectedInventoryItem = null;
                    currentSelectedItemQuantityLabel.setText("In Inventory: 0");
                }
            }
            if (!useItemQuantitySelectBox.getItems().isEmpty()) {
                useItemQuantitySelectBox.setSelectedIndex(0);
            }
        }
    }

    private void showResultDialog(String message) {
        new Dialog("Result", skin) {
            {
                text(message);
                button("OK");
            }
            @Override
            protected void result(Object object) {
                // This method is called when a button is clicked.
            }
        }.show(getStage());
    }
}
