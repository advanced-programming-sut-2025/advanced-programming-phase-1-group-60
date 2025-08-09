package com.StardewValley.view;

import com.StardewValley.AssetsManager.CropManager;
import com.StardewValley.models.Item;
import com.StardewValley.models.Seeds;
import com.StardewValley.models.User;
import com.StardewValley.repository.FruitsAndVegetablesRepository;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.ArrayList;
import java.util.List;

public class FarmingDialog extends Dialog {

    public interface OnSeedSelected {
        void onSeedSelected(Seeds seed);
    }

    private User currentUser; // replaces 'user'
    private static final List<String> FERTILIZER_NAMES = List.of(
        "Basic_Fertilizer","Deluxe_Fertilizer","Quality_Fertilizer"
    );
    private OnSeedSelected onSeedSelected;

    private final Table listTable;
    private final Label cheatStatusLabel;
    private final TextButton cheatToggleButton;
    private final Label feedbackLabel;

    private boolean cheatActive = false;
    private Runnable onClosed; // notify MapView when dialog closes to unblock input

    public FarmingDialog(Skin skin, User user) {
        super("Farming - Seeds", skin);
        this.currentUser = user;

        setModal(true);
        setResizable(true);
        setMovable(true);
        getTitleLabel().setWrap(true);

        // Seed list (scrollable)
        listTable = new Table(getSkin());
        listTable.top().left().pad(5);
        ScrollPane scrollPane = new ScrollPane(listTable, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // Cheat controls (button toggle + status)
        cheatStatusLabel = new Label("Cheat: OFF", getSkin());
        cheatToggleButton = new TextButton("Activate Cheat", getSkin());
        cheatToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleCheat();
            }
        });

        Table cheatRow = new Table(getSkin());
        cheatRow.add(cheatToggleButton).padRight(10);
        cheatRow.add(cheatStatusLabel);

        // One-line feedback label (only shows latest action)
        feedbackLabel = new Label("", getSkin());
        feedbackLabel.setWrap(true);

        // Button row
        button("Close", false);

        // Layout
        getContentTable().clear();
        getContentTable().add(new Label("Select a seed to plant:", getSkin())).left().pad(10).row();
        getContentTable().add(scrollPane).growX().height(360).pad(10).row();
        getContentTable().add(cheatRow).growX().pad(10).row();
        getContentTable().add(feedbackLabel).growX().pad(10).row();

        refreshSeedList();
    }
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    public void setOnSeedSelected(OnSeedSelected onSeedSelected) {
        this.onSeedSelected = onSeedSelected;
    }

    public void setOnClosed(Runnable onClosed) {
        this.onClosed = onClosed;
    }

    // Only override the no-arg hide() to avoid signature mismatches across libGDX versions
    @Override
    public void hide() {
        super.hide();
        if (onClosed != null) onClosed.run();
    }

    private void refreshSeedList() {
        listTable.clear();
        // Section: Seeds
        Label seedsHeader = new Label("Seeds", getSkin());
        seedsHeader.setColor(Color.CYAN);
        listTable.add(seedsHeader).left().pad(4).row();
        List<Seeds> seeds = FruitsAndVegetablesRepository.seeds;
        for (Seeds s : seeds) {
            Table row = makeSeedRow(s);
            listTable.add(row).growX().pad(4).row();
        }
        // Section: Fertilizers
        Label fertHeader = new Label("Fertilizers", getSkin());
        fertHeader.setColor(Color.GOLD);
        listTable.add(fertHeader).left().padTop(10).row();
        for (String fert : FERTILIZER_NAMES) {
            Table fertRow = makeFertilizerRow(fert);
            listTable.add(fertRow).growX().pad(4).row();
        }
    }
    private Table makeFertilizerRow(String fertName) {
        Table row = new Table(getSkin());
        row.left().pad(6);
        // icon
        Image icon = new Image();
        String path = "assets/Map/FruitsAndVegetables/Fertilizer/" + fertName + ".png";
        if (Gdx.files.internal(path).exists()) {
            icon.setDrawable(new Image(new Texture(Gdx.files.internal(path))).getDrawable());
        }
        icon.setSize(32,32);
        Label nameLabel = new Label(fertName, getSkin());
        row.add(icon).size(32,32).padRight(10);
        row.add(nameLabel).left().expandX();

        if (cheatActive) {
            TextButton addBtn = new TextButton("Add", getSkin());
            row.add(addBtn).right();
            addBtn.addListener(new ClickListener(){
                @Override public void clicked(InputEvent event, float x, float y){
                    addFertilizerToInventory(fertName);
                }
            });
        }
        row.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y){
                if (cheatActive) {
                    addFertilizerToInventory(fertName);
                } else {
                    // Just feedback; fertilizer application happens from MapView via quick slot.
                    feedbackLabel.setText("Select and place fertilizer from quick bar.");
                }
            }
        });
        return row;
    }
    private void addFertilizerToInventory(String fertName) {
        if (currentUser == null || currentUser.getInventory() == null) {
            feedbackLabel.setText("No player inventory found.");
            pack(); return;
        }
        Item fert = new Item(fertName, 1, "assets/Map/FruitsAndVegetables/Fertilizer/" + fertName + ".png");
        boolean added = currentUser.getInventory().tryAddItem(fert);
        feedbackLabel.setText(added ? "Added 1x " + fertName : "Inventory full for " + fertName);
        pack();
    }
    private Table makeSeedRow(Seeds seed) {
        Table row = new Table(getSkin());
        // SAFE background: only set if exists in Skin to avoid runtime error
        Drawable bg = null;
        if (getSkin().has("default-round", Drawable.class)) {
            bg = getSkin().getDrawable("default-round");
        } else if (getSkin().has("window", Drawable.class)) {
            bg = getSkin().getDrawable("window");
        } else if (getSkin().has("textfield", Drawable.class)) {
            bg = getSkin().getDrawable("textfield");
        }
        if (bg != null) row.setBackground(bg);

        row.left().pad(6);

        // Image (icon)
        Image seedImg;
        if (CropManager.getInstance().getSeedTexture(seed) != null) {
            seedImg = new Image(CropManager.getInstance().getSeedTexture(seed));
        } else {
            seedImg = new Image(); // fallback empty
        }
        seedImg.setSize(32, 32);

        // Label and conditional action button
        Label nameLabel = new Label(seed.getName(), getSkin());

        row.add(seedImg).size(32, 32).padRight(10);
        row.add(nameLabel).left().expandX();

        // Show no button initially; when cheat is active, show an "Add" button
        if (cheatActive) {
            TextButton addBtn = new TextButton("Add", getSkin());
            row.add(addBtn).right();

            addBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onSeedRowAction(seed);
                }
            });
        }

        // Clicking the row: plant normally, or add if cheat is on
        row.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSeedRowAction(seed);
            }
        });

        return row;
    }

    private void onSeedRowAction(Seeds seed) {
        if (cheatActive) {
            addSeedToInventory(seed);
            // Keep dialog open for adding multiple seeds
        } else {
            // Normal flow: select for planting and close
            chooseSeed(seed);
        }
    }

    private void toggleCheat() {
        cheatActive = !cheatActive;
        if (cheatActive) {
            cheatStatusLabel.setText("Cheat: ON (click seeds to add)");
            cheatToggleButton.setText("Deactivate Cheat");
        } else {
            cheatStatusLabel.setText("Cheat: OFF");
            cheatToggleButton.setText("Activate Cheat");
        }
        // Rebuild rows to show/hide the "Add" buttons
        refreshSeedList();
        pack();
    }

    private void chooseSeed(Seeds seed) {
        if (onSeedSelected != null) {
            onSeedSelected.onSeedSelected(seed);
        }
        hide();
    }

    private void addSeedToInventory(Seeds src) {
        if (currentUser == null || currentUser.getInventory() == null) {
            feedbackLabel.setText("No player inventory found.");
            pack();
            return;
        }

        // Build a concrete Seeds item to store, with proper path for InventoryView
        Seeds copy = new Seeds();
        copy.setName(src.getName());
        copy.setGrowsInto(src.getGrowsInto());
        copy.setSuitableSeasons(new ArrayList<>(src.getSuitableSeasons()));
        copy.setTotalHarvestTime(src.getTotalHarvestTime());
        copy.setImagePath(src.getImagePath());
        copy.setQuantity(1);

        String fullPath = resolveSeedTexturePath(src.getImagePath());
        if (fullPath != null) {
            copy.setPath(fullPath);
        }

        boolean added = currentUser.getInventory().tryAddItem(copy);
        if (added) {
            feedbackLabel.setText("Added 1x " + src.getName() + " to inventory.");
        } else {
            feedbackLabel.setText("Inventory full. Could not add " + src.getName() + ".");
        }
        pack();
    }

    private String resolveSeedTexturePath(String imageFileName) {
        if (imageFileName == null || imageFileName.isEmpty()) return null;

        // Primary path (as used by CropManager)
        String primary = "assets/Map/FruitsAndVegetables/Seed/" + imageFileName;
        FileHandle f1 = Gdx.files.internal(primary);
        if (f1.exists()) return primary;

        // Alternate path (without assets/ prefix) just in case of different packing
        String alt = "Map/FruitsAndVegetables/Seed/" + imageFileName;
        FileHandle f2 = Gdx.files.internal(alt);
        if (f2.exists()) return alt;

        return null;
    }
}
