package com.StardewValley.view;

import com.StardewValley.models.*;
import com.StardewValley.repository.FruitsAndVegetablesRepository;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.List;
import java.util.StringJoiner;

public class CraftInfoDialog extends Dialog {

    public CraftInfoDialog(String title, Skin skin) {
        super(title, skin);
        button("Close", true);
        setModal(true);
    }

    public void setContent(String text) {
        getContentTable().clear();
        Label label = new Label(text, getSkin());
        label.setWrap(true);
        getContentTable().add(label).width(320).pad(10);
        pack();
    }

    // Helper for building consistent season strings
    private static String seasons(List<String> seasons) {
        if (seasons == null || seasons.isEmpty()) return "N/A";
        StringJoiner sj = new StringJoiner(", ");
        for (String s : seasons) sj.add(s);
        return sj.toString();
    }

    public static CraftInfoDialog forTree(Tree tree, Skin skin) {
        CraftInfoDialog dlg = new CraftInfoDialog("Tree Info", skin);
        StringBuilder sb = new StringBuilder();
        sb.append("Type: Tree\n");
        sb.append("Name: ").append(tree.getName()).append("\n");
        sb.append("Source: ").append(nullToNA(tree.getSource())).append("\n");
        sb.append("Stages: ").append(arrayToString(tree.getStages())).append("\n");
        sb.append("Total Harvest Time: ").append(tree.getTotalHarvestTime()).append("\n");
        sb.append("Fruit: ").append(nullToNA(tree.getFruit())).append("\n");
        sb.append("Fruit Harvest Cycle: ").append(tree.getFruitHarvestCycle()).append("\n");
        sb.append("Fruit Base Sell Price: ").append(tree.getFruitBaseSellPrice()).append("\n");
        sb.append("Is Fruit Edible: ").append(tree.isFruitEdible()).append("\n");
        sb.append("Fruit Energy: ").append(tree.getFruitEnergy()).append("\n");
        sb.append("Season(s): ").append(seasons(tree.getSuitableSeasons())).append("\n");
        dlg.setContent(sb.toString());
        return dlg;
    }

    public static CraftInfoDialog forForagingCrop(ForagingCrop crop, Skin skin) {
        CraftInfoDialog dlg = new CraftInfoDialog("Foraging Crop Info", skin);
        StringBuilder sb = new StringBuilder();
        sb.append("Type: Foraging Crop\n");
        sb.append("Name: ").append(crop.getName()).append("\n");
        sb.append("Base Sell Price: ").append(crop.getBaseSellPrice()).append("\n");
        sb.append("Energy: ").append(crop.getEnergy()).append("\n");
        sb.append("Season(s): ").append(seasons(crop.getSuitableSeasons())).append("\n");
        dlg.setContent(sb.toString());
        return dlg;
    }

    public static CraftInfoDialog forForagingTree(ForagingTree tree, Skin skin) {
        CraftInfoDialog dlg = new CraftInfoDialog("Foraging Tree Info", skin);
        StringBuilder sb = new StringBuilder();
        sb.append("Type: Foraging Tree\n");
        sb.append("Name: ").append(tree.getName()).append("\n");
        sb.append("Season(s): ").append(seasons(tree.getSuitableSeasons())).append("\n");
        dlg.setContent(sb.toString());
        return dlg;
    }

    public static CraftInfoDialog forForagingMineral(ForagingMineral mineral, Skin skin) {
        CraftInfoDialog dlg = new CraftInfoDialog("Foraging Mineral Info", skin);
        StringBuilder sb = new StringBuilder();
        sb.append("Type: Foraging Mineral\n");
        sb.append("Name: ").append(mineral.getName()).append("\n");
        sb.append("Base Sell Price: ").append(mineral.getBaseSellPrice()).append("\n");
        dlg.setContent(sb.toString());
        return dlg;
    }

    public static CraftInfoDialog forStone(Stone stone, Skin skin) {
        CraftInfoDialog dlg = new CraftInfoDialog("Stone Info", skin);
        StringBuilder sb = new StringBuilder();
        sb.append("Type: Stone\n");
        sb.append("Variant: ").append(stone.getStoneVariant()).append("\n");
        // Optional: If stone has a mineral, we can show its name too (if exposed in your Stone class)
        dlg.setContent(sb.toString());
        return dlg;
    }

    public static CraftInfoDialog forPlantedSeed(Tile tile, Skin skin) {
        Seeds seed = tile.getPlantedSeed();
        CraftInfoDialog dlg = new CraftInfoDialog("Crop/Seed Info", skin);

        StringBuilder sb = new StringBuilder();
        sb.append("Type: Planted Seed\n");
        if (seed != null) {
            sb.append("Seed: ").append(seed.getName()).append("\n");
            sb.append("Grows Into: ").append(seed.getGrowsInto()).append("\n");
            sb.append("Total Harvest Time: ").append(seed.getTotalHarvestTime()).append("\n");
            sb.append("Season(s): ").append(seasons(seed.getSuitableSeasons())).append("\n");
        }

        // Tile status details (growth/water/harvest info)
        sb.append("\nTile Status:\n");
        sb.append("Watered: ").append(tile.isWatered() ? "Yes" : "No").append("\n");
        sb.append("Days Grown: ").append(tile.getDaysGrown()).append("\n");
        sb.append("Ready to Harvest: ").append(tile.isReadyToHarvest() ? "Yes" : "No").append("\n");
        sb.append("Giant Crop: ").append(tile.isGiantCrop() ? "Yes" : "No").append("\n");

        // Try to add fruit/veg crop static data if available
        if (seed != null) {
            FruitsAndVegetables fv = FruitsAndVegetablesRepository.getCropByName(seed.getGrowsInto());
            if (fv != null) {
                sb.append("\nCrop Data:\n");
                sb.append("Name: ").append(fv.getName()).append("\n");
                sb.append("Source (Seed): ").append(nullToNA(fv.getSource())).append("\n");
                sb.append("Growth Stages: ").append(arrayToString(fv.getGrowthStages())).append("\n");
                sb.append("Total Harvest Time: ").append(fv.getTotalHarvestTime()).append("\n");
                sb.append("One Time: ").append(fv.isOneTime()).append("\n");
                sb.append("Regrowth Time: ").append(fv.getRegrowthTime() == null ? "N/A" : fv.getRegrowthTime()).append("\n");
                sb.append("Base Sell Price: ").append(fv.getSellPrice()).append("\n");
                sb.append("Is Edible: ").append(fv.isEdible()).append("\n");
                sb.append("Energy: ").append(fv.getBaseEnergy()).append("\n");
                sb.append("Season(s): ").append(seasons(fv.getSuitableSeasons())).append("\n");
                sb.append("Can Become Giant: ").append(fv.isCanBeGiant()).append("\n");
            }
        }

        dlg.setContent(sb.toString());
        return dlg;
    }

    private static String arrayToString(int[] arr) {
        if (arr == null || arr.length == 0) return "N/A";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append("-");
        }
        return sb.toString();
    }

    private static String nullToNA(String s) {
        return (s == null || s.isEmpty()) ? "N/A" : s;
    }
}
