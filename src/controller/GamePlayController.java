package controller;

import models.*;
import repository.ItemRepository;

import java.util.List;
import java.util.Scanner;

public class GamePlayController {
    Scanner sc;

    private final Tile[][] tiles;
    private final User user;
    private int energyUsedThisTurn;
    private Game currentGame;

    public GamePlayController(Farm f, User u, Scanner sc, Game game) {
        this.tiles = f.getTiles();
        this.user = u;
        this.sc = sc;
        u.setPosition(tiles[0][0]);
        energyUsedThisTurn = 0;
        currentGame = game;
    }

    public void getAndProcessInput() {
        while (energyUsedThisTurn <= 50) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");

            if (parts[0].equalsIgnoreCase("tool")) {
                if (parts.length == 3 && parts[1].equalsIgnoreCase("equip")) {
                    try {
                        int toolId = Integer.parseInt(parts[2]);
                        equipTool(toolId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid tool ID.");
                    }
                }
                else if (parts.length >= 3 && parts[1].equalsIgnoreCase("upgrade")) {
                    try {
                        int toolId = Integer.parseInt(parts[2]);
                        boolean force = parts.length >= 4 && parts[3].equalsIgnoreCase("-f");
                        upgradeToolById(toolId, force);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid tool ID.");
                    }
                }
                else if (parts.length == 3 && parts[1].equalsIgnoreCase("use") && parts[2].equalsIgnoreCase("-r")) {
                    useEquippedToolRefillOnly();
                }
                else if (parts.length == 2 && parts[1].equalsIgnoreCase("current")) {
                    showCurrentTool();
                } else if (parts.length == 2 && parts[1].equalsIgnoreCase("available")) {
                    showAvailableTools();

                }
                else if (parts.length >= 4 && parts[1].equalsIgnoreCase("use") && parts[2].equalsIgnoreCase("-d")) {
                    try {
                        int direction = Integer.parseInt(parts[3]);
                        boolean refill = parts.length >= 5 && parts[4].equalsIgnoreCase("-r");
                        useEquippedTool(direction, refill);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid direction.");
                    }
                }
                else {
                    System.out.println("Unknown tool command.");
                }
            } else if (parts[0].equalsIgnoreCase("walk")) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                walkTo(x, y);
            } else if (parts[0].equalsIgnoreCase("next")) {
                break;
            }else if (parts[0].equalsIgnoreCase("print")) {
                currentGame.getCurrentMap().printRegion(user.getPosition().getPositionX(),user.getPosition().getPositionY()
                        , Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            }else {
                System.out.println("Unknown command.");
            }
        }
    }

    public void equipTool(int toolId) {
        Inventory inventory = user.getInventory();
        if (inventory == null || inventory.getItems() == null) {
            System.out.println("No inventory found.");
            return;
        }
        Item tool = null;
        for (Item item : inventory.getItems()) {
            if (item.getId() == toolId && item instanceof Tools) {
                tool = item;
                break;
            }
        }
        if (tool == null) {
            System.out.println("You don't have this tool in your inventory.");
            return;
        }
        user.setEquippedTool(tool);
        System.out.println("Equipped tool: " + tool.getName());
    }
    private void useEquippedTool(int direction, boolean refill) {
        Item equipped = user.getEquippedTool();
        if (!(equipped instanceof Tools)) {
            System.out.println("No tool equipped or equipped item is not a tool.");
            return;
        }
        Tools tool = (Tools) equipped;
        int[] dx = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 0, 1, 1, 1};

        if (direction < 1 || direction > 9) {
            System.out.println("Invalid direction.");
            return;
        }
        int tx = user.getPosition().getPositionX() + dx[direction - 1];
        int ty = user.getPosition().getPositionY() + dy[direction - 1];
        if (ty < 0 || tx < 0 || ty >= tiles.length || tx >= tiles[0].length) {
            System.out.println("Target tile is out of bounds.");
            return;
        }
        Tile target = tiles[ty][tx];
        if (tool.getName().toLowerCase().contains("hoe")) {
            if (user.getEnergy().getCurrentEnergy() < tool.getEnergyCost() && !user.getEnergy().isUnlimited()) {
                System.out.println("Not enough energy to use the hoe.");
                return;
            }
            if (!target.isPlowed()) {
                target.setPlowed(true);
                user.consumeEnergy(tool.getEnergyCost());
                System.out.println("Tile plowed at (" + tx + ", " + ty + "). Energy left: " + user.getEnergy());
            } else {
                System.out.println("Tile is already plowed.");
            }
        } else if (tool.getName().toLowerCase().contains("pickaxe")) {
            if (target.isPlowed()) {
                target.setPlowed(false);
                System.out.println("Hoe effect removed from tile at (" + tx + ", " + ty + ").");
                return;
            }
            if (!('S' == target.getRandomElement().map(RandomElement::symbol).orElse(' '))) {
                System.out.println("Pickaxe can only be used on stones (tile 'S').");
                return;
            }
            int energyCost = tool.getEnergyCost();
            if (!target.isPlowed()) {
                energyCost = Math.max(1, energyCost - 1);
            }
            if (user.getEnergy().getCurrentEnergy() < energyCost && !user.getEnergy().isUnlimited()) {
                System.out.println("Not enough energy to use the pickaxe.");
                return;
            }
            target.setToNormalTile();
            target.setType(".");
            // Optionally, you can add logic to remove the stone here if needed
            user.consumeEnergy(energyCost);
            System.out.println("Used pickaxe on stone at (" + tx + ", " + ty + "). Energy left: " + user.getEnergy());
        }
        else if (tool.getName().toLowerCase().contains("axe")) {
            if ((!('T' == target.getRandomElement().map(RandomElement::symbol).orElse(' '))) &&
                    (!('F' == target.getRandomElement().map(RandomElement::symbol).orElse(' '))))  {
                System.out.println("Axe can only be used on trees ('F') or branches ('T').");
                return;
            }
            int energyCost = tool.getEnergyCost();
            // If the tile is not plowed, watered, or fertilized, use 1 less energy
            if (!target.isPlowed() && !target.isWatered() && !target.isFertilized()) {
                energyCost = Math.max(1, energyCost - 1);
            }
            if (user.getEnergy().getCurrentEnergy() < energyCost && !user.getEnergy().isUnlimited()) {
                System.out.println("Not enough energy to use the axe.");
                return;
            }
            target.setToNormalTile();
            target.setType(".");
            user.consumeEnergy(energyCost);
            System.out.println("Used axe on " + (target.getType().equals("F") ? "tree" : "branch") +
                    " at (" + tx + ", " + ty + "). Energy left: " + user.getEnergy());
        }
        else if (tool.getName().toLowerCase().contains("wateringcan")) {
            // Watering logic as before...
            int radius = tool.getRadius();
            int centerX = user.getPosition().getPositionX() + dx[direction - 1];
            int centerY = user.getPosition().getPositionY() + dy[direction - 1];
            int watered = 0;
            for (int y = Math.max(0, centerY - radius); y <= Math.min(tiles.length - 1, centerY + radius); y++) {
                for (int x = Math.max(0, centerX - radius); x <= Math.min(tiles[0].length - 1, centerX + radius); x++) {
                    double dist = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                    if (dist <= radius && !tiles[y][x].isWatered()) {
                        tiles[y][x].setWatered(true);
                        watered++;
                    }
                }
            }
            tool.setCurrentUsage(tool.getCurrentUsage() - 1);
            user.consumeEnergy(tool.getEnergyCost());
            System.out.println("Watered " + watered + " tiles. Usage left: " + tool.getCurrentUsage() + ". Energy left: " + user.getEnergy());
        }
        else if (tool.getName().toLowerCase().contains("fishingpole")) {
            boolean isLake = target.getRandomElement().map(RandomElement::symbol).orElse(' ') == 'L'
                    || target.getStaticElement().map(StaticElement::symbol).orElse(' ') == 'L';
            if (!isLake) {
                System.out.println("You must use the fishing pole next to a lake (L) tile.");
                return;
            }
            if (user.getEnergy().getCurrentEnergy() < tool.getEnergyCost() && !user.getEnergy().isUnlimited()) {
                System.out.println("Not enough energy to fish.");
                return;
            }
            user.consumeEnergy(tool.getEnergyCost());
            if (Math.random() < 0.1) {
                System.out.println("No fish caught this time.");
            } else {
                System.out.println("You successfully caught a fish!");
                // Optionally: add fish to inventory here
            }
            System.out.println("Energy left: " + user.getEnergy());
        }
        else if (tool.getName().toLowerCase().contains("scythe")) {
            if (user.getEnergy().getCurrentEnergy() < tool.getEnergyCost() && !user.getEnergy().isUnlimited()) {
                System.out.println("Not enough energy to use the scythe.");
                return;
            }
            // For now, only allow cutting 'T' (branches)
            if (target.getRandomElement().map(RandomElement::symbol).orElse(' ') == 'T') {
                target.setToNormalTile();
                target.setType(".");
                user.consumeEnergy(tool.getEnergyCost());
                System.out.println("Used scythe on branch at (" + tx + ", " + ty + "). Energy left: " + user.getEnergy());
            } else {
                System.out.println("Scythe can only be used on branches ('T') for now.");
            }
        }
        else if (tool.getName().toLowerCase().contains("milk pail")) {
            if (user.getEnergy().getCurrentEnergy() < tool.getEnergyCost() && !user.getEnergy().isUnlimited()) {
                System.out.println("Not enough energy to use the milk pail.");
                return;
            }
            user.consumeEnergy(tool.getEnergyCost());
            System.out.println("Used milk pail. (Milking animals will be implemented later.) Energy left: " + user.getEnergy());
        }
        else if (tool.getName().toLowerCase().contains("shear")) {
            if (user.getEnergy().getCurrentEnergy() < tool.getEnergyCost() && !user.getEnergy().isUnlimited()) {
                System.out.println("Not enough energy to use the shear.");
                return;
            }
            user.consumeEnergy(tool.getEnergyCost());
            System.out.println("Used milk pail. (Milking animals will be implemented later.) Energy left: " + user.getEnergy());
        }
        else {
            System.out.println("Wrong Tool!");
        }
    }
    private void useEquippedToolRefillOnly() {
        Item equipped = user.getEquippedTool();
        if (!(equipped instanceof Tools) || !equipped.getName().toLowerCase().contains("wateringcan")) {
            System.out.println("You must equip a watering can to refill.");
            return;
        }
        Tools tool = (Tools) equipped;
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
        int px = user.getPosition().getPositionX();
        int py = user.getPosition().getPositionY();
        boolean nextToLake = false;
        for (int i = 0; i < 8; i++) {
            int nx = px + dx[i];
            int ny = py + dy[i];
            if (nx >= 0 && ny >= 0 && ny < tiles.length && nx < tiles[0].length) {
                Tile adj = tiles[ny][nx];
                if (adj.getRandomElement().map(RandomElement::symbol).orElse(' ') == 'L' ||
                        adj.getStaticElement().map(StaticElement::symbol).orElse(' ') == 'L') {
                    nextToLake = true;
                    break;
                }
            }
        }
        if (nextToLake) {
            tool.setCurrentUsage(tool.getMaxUsage());
            System.out.println("Watering can refilled!");
        } else {
            System.out.println("You must be next to a lake (L) tile to refill.");
        }
    }
    private void showCurrentTool() {
        Item currentTool = user.getEquippedTool();
        if (!(currentTool instanceof Tools)) {
            System.out.println("No tool equipped.");
            return;
        }
        Tools t = (Tools) currentTool;
        String stage = "UNKNOWN";
        if (t.getName().toLowerCase().contains("hoe")) {
            stage = t.getHoeStage() != null ? t.getHoeStage().name() : "UNKNOWN";
        } else if (t.getName().toLowerCase().contains("pickaxe")) {
            stage = t.getPickaxeStage() != null ? t.getPickaxeStage().name() : "UNKNOWN";
        } else if (t.getName().toLowerCase().contains("axe")) {
            stage = t.getAxeStage() != null ? t.getAxeStage().name() : "UNKNOWN";
        } else if (t.getName().toLowerCase().contains("wateringcan")) {
            stage = t.getWateringcanStage() != null ? t.getWateringcanStage().name() : "UNKNOWN";
        } else if (t.getName().toLowerCase().contains("fishingpole")) {
            stage = t.getFishingpoleStage() != null ? t.getFishingpoleStage().name() : "UNKNOWN";
        }
        System.out.printf("Current tool: %s (ID: %d, Stage: %s, Energy: %d, Usage: %d/%d, Radius: %d)\n",
                t.getName(), t.getId(), stage, t.getEnergyCost(), t.getCurrentUsage(), t.getMaxUsage(), t.getRadius());
    }

    private void showAvailableTools() {
        Inventory inventory = user.getInventory();
        if (inventory == null || inventory.getItems() == null || inventory.getItems().isEmpty()) {
            System.out.println("No tools in inventory.");
            return;
        }

        System.out.println("Available tools in inventory:");
        for (Item item : inventory.getItems()) {
            if (item instanceof Tools) {
                Tools t = (Tools) item;
                String stage = "UNKNOWN";
                if (t.getName().toLowerCase().contains("hoe")) {
                    stage = t.getHoeStage() != null ? t.getHoeStage().name() : "UNKNOWN";
                } else if (t.getName().toLowerCase().contains("pickaxe")) {
                    stage = t.getPickaxeStage() != null ? t.getPickaxeStage().name() : "UNKNOWN";
                } else if (t.getName().toLowerCase().contains("axe")) {
                    stage = t.getAxeStage() != null ? t.getAxeStage().name() : "UNKNOWN";
                } else if (t.getName().toLowerCase().contains("wateringcan")) {
                    stage = t.getWateringcanStage() != null ? t.getWateringcanStage().name() : "UNKNOWN";
                } else if (t.getName().toLowerCase().contains("fishingpole")) {
                    stage = t.getFishingpoleStage() != null ? t.getFishingpoleStage().name() : "UNKNOWN";
                }

                System.out.printf("- %s (ID: %d, Stage: %s, Energy: %d, Usage: %d/%d, Radius: %d)\n",
                        t.getName(), t.getId(), stage, t.getEnergyCost(), t.getCurrentUsage(), t.getMaxUsage(), t.getRadius());
            }
        }
    }
    // Add this method to GamePlayController
    private void upgradeToolById(int toolId, boolean force) {
        Item tool = user.getInventory().getItems().stream()
                .filter(i -> i instanceof Tools && i.getId() == toolId)
                .findFirst().orElse(null);
        if (!(tool instanceof Tools)) {
            System.out.println("Tool not found.");
            return;
        }
        Tools t = (Tools) tool;

        // If not force, check for blacksmith (to be implemented)
        if (!force) {
            if (t.getName().toLowerCase().contains("fishingpole")){
                System.out.println("You must be in Willy's Workshop.");
                return;
            }
            System.out.println("You must be at the blacksmith to upgrade .");
            return;
        }

        // Upgrade logic for each tool type
        if (t.getName().toLowerCase().contains("hoe")) {
            Tools.HoeStage stage = t.getHoeStage();
            switch (stage) {
                case BEGINNER: t.setHoeStage(Tools.HoeStage.COPPER); break;
                case COPPER: t.setHoeStage(Tools.HoeStage.IRON); break;
                case IRON: t.setHoeStage(Tools.HoeStage.GOLD); break;
                case GOLD: t.setHoeStage(Tools.HoeStage.IRIDIUM); break;
                case IRIDIUM: System.out.println("Already at max stage."); return;
            }
            System.out.println("Hoe upgraded to " + t.getHoeStage());
        } else if (t.getName().toLowerCase().contains("pickaxe")) {
            Tools.PickaxeStage stage = t.getPickaxeStage();
            switch (stage) {
                case BEGINNER: t.setPickaxeStage(Tools.PickaxeStage.COPPER); break;
                case COPPER: t.setPickaxeStage(Tools.PickaxeStage.IRON); break;
                case IRON: t.setPickaxeStage(Tools.PickaxeStage.GOLD); break;
                case GOLD: t.setPickaxeStage(Tools.PickaxeStage.IRIDIUM); break;
                case IRIDIUM: System.out.println("Already at max stage."); return;
            }
            System.out.println("Pickaxe upgraded to " + t.getPickaxeStage());
        } else if (t.getName().toLowerCase().contains("axe")) {
            Tools.AxeStage stage = t.getAxeStage();
            switch (stage) {
                case BEGINNER: t.setAxeStage(Tools.AxeStage.COPPER); break;
                case COPPER: t.setAxeStage(Tools.AxeStage.IRON); break;
                case IRON: t.setAxeStage(Tools.AxeStage.GOLD); break;
                case GOLD: t.setAxeStage(Tools.AxeStage.IRIDIUM); break;
                case IRIDIUM: System.out.println("Already at max stage."); return;
            }
            System.out.println("Axe upgraded to " + t.getAxeStage());
        } else if (t.getName().toLowerCase().contains("wateringcan")) {
            Tools.WateringcanStage stage = t.getWateringcanStage();
            switch (stage) {
                case BEGINNER: t.setWateringcanStage(Tools.WateringcanStage.COPPER); break;
                case COPPER: t.setWateringcanStage(Tools.WateringcanStage.IRON); break;
                case IRON: t.setWateringcanStage(Tools.WateringcanStage.GOLD); break;
                case GOLD: t.setWateringcanStage(Tools.WateringcanStage.IRIDIUM); break;
                case IRIDIUM: System.out.println("Already at max stage."); return;
            }
            System.out.println("Watering can upgraded to " + t.getWateringcanStage());
        } else if (t.getName().toLowerCase().contains("fishingpole")) {
            Tools.FishingpoleStage stage = t.getFishingpoleStage();
            switch (stage) {
                case LEARNING: t.setFishingpoleStage(Tools.FishingpoleStage.BAMBO); break;
                case BAMBO: t.setFishingpoleStage(Tools.FishingpoleStage.FIBERGLASS); break;
                case FIBERGLASS: t.setFishingpoleStage(Tools.FishingpoleStage.IRIDIUM); break;
                case IRIDIUM: System.out.println("Already at max stage."); return;
            }
            System.out.println("Fishing pole upgraded to " + t.getFishingpoleStage());
        } else {
            System.out.println("Unknown tool type.");
        }
    }
    public void walkTo(int tx, int ty) {
        if (tx < 0 || ty < 0 || ty >= tiles.length || tx >= tiles[0].length) {
            System.out.println("خارج از مرز مزرعه!");
            return;
        }
        Tile target = tiles[ty][tx];
        if (!target.isPassable()) {
            System.out.println("غیرقابل عبور");
            return;
        }
        var opt = PathFinder.findShortest(tiles, user.getPosition(), target);
        if (opt.isEmpty()) {
            System.out.println("مسیر نیست");
            return;
        }
        var path = opt.get();
        int need = (path.getDistance() + 10 * path.getTurns()) / 20;
        System.out.printf("مسافت=%d، پیچ=%d، انرژی=%d. ادامه؟ (y/n)\n",
                path.getDistance(), path.getTurns(), need);
        String response = sc.nextLine();
        if (!response.equalsIgnoreCase("y")) return;
        if (user.getEnergy().getCurrentEnergy() >= need || user.getEnergy().isUnlimited()) {
            user.consumeEnergy(need);
            energyUsedThisTurn += need;
            user.setPosition(target);
            System.out.println("حرکت شد. انرژی=" + user.getEnergy());
            if (user.getEnergy().getCurrentEnergy() == 0) user.faint();
        } else user.faintAlong(path);
    }
}
