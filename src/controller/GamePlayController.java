package controller;

import models.*;
import repository.NpcRepository;
import repository.UserRepository;

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
            String unread = user.getUnreadMessage();
            if (!unread.isEmpty()) {
                System.out.println("You have unread messages: \n" + unread);
            }

            String unreadMarriage = user.getUnreadMarriageRequests();
            if (!unreadMarriage.isEmpty()) {
                System.out.println("Marriage requests:\n" + unreadMarriage);
            }

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
            } else if (input.startsWith("print map")) {
                int width = Integer.parseInt(parts[2]);
                int height = Integer.parseInt(parts[3]);
                printRegion(width, height);
            } else if (parts.length >= 3 && parts[0].equalsIgnoreCase("go") &&
                    parts[1].equalsIgnoreCase("to") && parts[2].equalsIgnoreCase("village")) {
                goToVillage();
            } else if (parts.length >= 3 && parts[0].equalsIgnoreCase("go") &&
                    parts[1].equalsIgnoreCase("to") && parts[2].equalsIgnoreCase("farm")) {
                goToFarm();
            } else if (input.equalsIgnoreCase("print all map")) {
                currentGame.getCurrentMap().printRegion(0, 0, 119, 119);
            } else if (input.startsWith("meet npc")) {
                String npcName = parts[2];
                meetNpc(npcName);
            } else if (input.startsWith("gift npc")) {
                String npcName = parts[2];
                String itemName = parts[3];
                int itemQuantity = Integer.parseInt(parts[4]);
                giftNpc(npcName, itemName, itemQuantity);
            } else if (input.equalsIgnoreCase("friendship NPC list")) {
                System.out.println(user.showFriendshipLevelsWithNpcs());
            } else if (input.equalsIgnoreCase("friendship USER list")) {
                System.out.println(user.showFriendshipLevelsWithUsers());
            } else if (input.startsWith("quests list")) {
                listQuests(parts[2]);
            } else if (parts[0].equalsIgnoreCase("talk") && parts[1].equalsIgnoreCase("-u")) {
                String targetUsername = parts[2];
                User target = UserRepository.getInstance().getUserByUsername(targetUsername);
                if (target == null) {
                    System.out.println("User not found!");
                    continue;
                }

                StringBuilder message = new StringBuilder();
                for (int i = 4; i < parts.length; i++) {
                    message.append(parts[i]).append(" ");
                }
                String finalMessage = message.toString().trim();

                Result talkResult = user.talk(target, finalMessage);
                if (!talkResult.isSuccess()) {
                    System.out.println(talkResult.getMessage());
                } else {
                    System.out.println("Message sent to " + target.getNickname());
                }
            } else if (parts[0].equalsIgnoreCase("talk") && parts[1].equalsIgnoreCase("history")) {
                User target = UserRepository.getInstance().getUserByUsername(parts[3]);
                if (target == null) {
                    System.out.println("User not found!");
                    continue;
                }
                StringBuilder history = user.getAllMessages(target);
                if (history.length() == 0) {
                    System.out.println("No message history with " + target.getNickname());
                } else {
                    System.out.println("Chat history with " + target.getNickname() + ":");
                    System.out.println(history.toString());
                }
            } else if (input.contains("product")) {
                handleStoreCommands(currentGame.getCurrentMap(), input);
            } else if (input.startsWith("hug")) {
                System.out.println(processHugCommand(parts[2]));
            } else if (input.startsWith("flower")) {
                System.out.println(processFlowerCommand(parts[2]));
            } else if (input.startsWith("ask marriage")) {
                System.out.println(processMarriageCommand(parts[3], parts[5]));
            } else if (input.startsWith("respond")) {
                String response = parts[1];
                String username = parts[3];
                System.out.println(processRespondCommand(response, username));
            } else {
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

    private void printRegion(int width, int height) {
        GameMap map = currentGame.getCurrentMap();
        Tile currentTile = user.getPosition();
        int userX = currentTile.getPositionX();
        int userY = currentTile.getPositionY();
        int activeFarmIndex = currentGame.getSelectedMaps().get(user);

        // تعیین محدوده فارم فعال
        int farmStartX = 0, farmEndX = 0, farmStartY = 0, farmEndY = 0;
        int globalX = userX, globalY = userY;
        switch (activeFarmIndex) {
            case 1:
                farmEndX = 49;
                farmEndY = 49;
                break;
            case 2:
                farmStartX = 70;
                farmEndX = 119;
                farmEndY = 49;
                globalX = userX + 70;
                break;
            case 3:
                farmEndX = 49;
                farmStartY = 70;
                farmEndY = 119;
                globalY = userY + 70;
                break;
            case 4:
                farmStartX = 70;
                farmEndX = 119;
                farmStartY = 70;
                farmEndY = 119;
                globalX = userX + 70;
                globalY = userY + 70;
                break;
        }

        if (user.isInVillage) {
            // محدوده پرینت در دهکده
            int endX = Math.min(map.getVilX() + map.getVilW() - 1, userX + 50 + width - 1);
            int endY = Math.min(map.getVilY() + map.getVilH() - 1, userY + 50 + height - 1);
            int printWidth = endX - (userX + 50) + 1;
            int printHeight = endY - (userY + 50) + 1;
            map.printRegion(userX + 50, userY + 50, printWidth, printHeight);
        } else {
            // محدوده پرینت در فارم فعال
            int endX = Math.min(farmEndX, globalX + width - 1);
            int endY = Math.min(farmEndY, globalY + height - 1);
            int printWidth = endX - globalX + 1;
            int printHeight = endY - globalY + 1;
            map.printRegion(globalX, globalY, printWidth, printHeight);
        }
    }

    private void goToVillage() {
        GameMap map = currentGame.getCurrentMap();
        int activeFarmIndex = currentGame.getSelectedMaps().get(user);
        int userX = user.getPosition().getPositionX();
        int userY = user.getPosition().getPositionY();


        if (user.isInVillage) {
            System.out.println("You must be in your farm to go to the village.");
            return;
        }
        Tile targetTile = null;
        boolean canGoToVillage = false;
        switch (activeFarmIndex) {
            case 1:
                targetTile = map.getTile(50, 50);
                canGoToVillage = (userX == 49 && userY == 49);
                break;
            case 2:
                targetTile = map.getTile(69, 50);
                canGoToVillage = (userX == 0 && userY == 49);
                break;
            case 3:
                targetTile = map.getTile(50, 69);
                canGoToVillage = (userX == 49 && userY == 0);
                break;
            case 4:
                targetTile = map.getTile(69, 69);
                canGoToVillage = (userX == 0 && userY == 0);
                break;
        }

        if (!canGoToVillage) {
            System.out.println("Cannot move to village entrance.");
            return;
        }

        user.setPosition(targetTile);
        user.isInVillage = true;
        System.out.println("Moved to village entrance.");
    }

    private void goToFarm() {
        GameMap map = currentGame.getCurrentMap();
        int activeFarmIndex = currentGame.getSelectedMaps().get(user);
        int userX = user.getPosition().getPositionX();
        int userY = user.getPosition().getPositionY();
        if (!user.isInVillage) {
            System.out.println("You must be in village to go to the farm.");
            return;
        }
        Tile targetTile = null;
        boolean canGoToFarm = switch (activeFarmIndex) {
            case 1 -> {
                targetTile = map.getTile(49, 49);
                yield (userX == 0 && userY == 0);
            }
            case 2 -> {
                targetTile = map.getTile(70, 49);
                yield (userX == 19 && userY == 0);
            }
            case 3 -> {
                targetTile = map.getTile(49, 70);
                yield (userX == 0 && userY == 19);
            }
            case 4 -> {
                targetTile = map.getTile(70, 70);
                yield (userX == 19 && userY == 19);
            }
            default -> false;
        };

        if (!canGoToFarm) {
            System.out.println("Cannot move to farm.");
        }

        user.setPosition(targetTile);
        user.isInVillage = false;
        System.out.println("Moved to farm.");
    }

    private void meetNpc(String npcName) {
        npcName = npcName.toUpperCase();
        GameMap map = currentGame.getCurrentMap();
        if (!user.isInVillage) {
            System.out.println("You must be in village to talk to the npc.");
        }
        boolean isThereNpc = isNpcAvailable(npcName, map);
        if (isThereNpc) {
            Npc npc = NpcRepository.getInstance().getNpcByName(npcName);
            String prompt = npc.startConversation(user);
            System.out.println(prompt);
            if (prompt.contains("(")) {
                String playerReply = sc.nextLine();
                String npcAnswer = npc.replyConversation(playerReply);
                System.out.println(npcAnswer);
            }
        } else System.out.println("No npc found.");
    }

    private void giftNpc(String npcName, String itemName, int quantity) {
        npcName = npcName.toUpperCase();
        GameMap map = currentGame.getCurrentMap();
        if (!user.isInVillage) {
            System.out.println("You must be in village to gift to the npc.");
        }
        boolean isThereNpc = isNpcAvailable(npcName, map);
        if (isThereNpc) {
            String result = GiftController.getInstance().giftToNpc(user, npcName, itemName, quantity);
            System.out.println(result);
        }
    }

    private void listQuests(String npcName) {
        npcName = npcName.toUpperCase();
        GameMap map = currentGame.getCurrentMap();
        boolean isThereNpc = isNpcAvailable(npcName, map);
        if (isThereNpc) {
            Npc npc = NpcRepository.getInstance().getNpcByName(npcName);
            StringBuilder quests = new StringBuilder();
            for (Quest quest : npc.getQuests()) {
                quests.append(quest.toString()).append("\n");
            }
            System.out.println(quests.toString());
        } else System.out.println("No npc found.");
    }

    private boolean isNpcAvailable(String npcName, GameMap map) {
        int playerX = user.getPosition().getPositionX();
        int playerY = user.getPosition().getPositionY();
        boolean isThereNpc = false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                // Skip the current tile
                if (dx == 0 && dy == 0) continue;

                int x = playerX + dx;
                int y = playerY + dy;

                // Ensure indices are within bounds [0..19]
                if (x < 0 || x > 19 || y < 0 || y > 19) continue;

                Tile tile = map.getTile(x + 50, y + 50);
                // Check if this tile contains the NPC symbol ('s')
                if (tile.getStaticElement()
                        .map(StaticElement::symbol)
                        .orElse('\0') == npcName.charAt(0)) {
                    isThereNpc = true;
                    break;
                }
            }
            if (isThereNpc) break;
        }
        return isThereNpc;
    }

    public void handleStoreCommands(GameMap map, String command) {
        int playerX = user.getPosition().getPositionX();
        int playerY = user.getPosition().getPositionY();
        Store nearStore = null;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int x = playerX + dx;
                int y = playerY + dy;

                if (x < 0 || x > 19 || y < 0 || y > 19) continue;

                Tile tile = map.getTile(x + 50, y + 50);
                if (tile.getStaticElement().isPresent() && tile.getStaticElement().get() instanceof Store store) {
                    nearStore = store;
                }
            }
        }

        if (command.equalsIgnoreCase("show all products")) {
            System.out.println(nearStore.showAllProducts());
        } else if (command.equalsIgnoreCase("show available products")) {
            System.out.println(nearStore.showAvailableProducts());
        }
    }

    private String placeAnimalPlace(String animalPlaceName, int x, int y) {

        Item animalPlace = user.getAnimalPlaces().stream()
                .filter(item -> item.getName().equals(animalPlaceName))
                .findFirst()
                .orElse(null);

        if (animalPlace == null) {
            return "Error: Animal place not found";
        }


        // دریافت ابعاد آیتم
        int width, height;
        if (animalPlace instanceof Coop) {
            Coop coop = (Coop) animalPlace;
            width = coop.getWidth();
            height = coop.getHeight();
        } else {
            Barn barn = (Barn) animalPlace;
            width = barn.getWidth();
            height = barn.getHeight();
        }

        // بررسی محدوده مختصات
        if (x < 0 || y < 0 || x + width > 50 || y + height > 50) {
            return "Error: Position out of bounds";
        }

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Tile tile = tiles[i][j];
                if (!tile.isPassable() || tile.isOccupied()) {
                    return "Error: Tile at (" + i + ", " + j + ") is blocked or occupied";
                }
            }
        }

        StaticElement element;
        if (animalPlace instanceof Coop) {
            element = new CoopStaticElement();
        } else {
            element = new BarnStaticElement();
        }

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                Tile tile = tiles[i][j];
                tile.setStaticElement(element);
                tile.setOccupied(true);
            }
        }

        if (animalPlace instanceof Coop) {
            ((Coop) animalPlace).setPosition(x, y);
        } else {
            ((Barn) animalPlace).setPosition(x, y);
        }

        user.removeAnimalPlace(animalPlace);
        return "Successfully placed " + animalPlaceName + " at (" + x + ", " + y + ")";
    }

    public String processAnimalCommands(String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0) return "Invalid command";

        try {
            switch (parts[0].toLowerCase()) {
                case "pet":
                    return processPetCommand(parts);
                case "shepherd":
                    return processShepherdCommand(parts);
                case "feed":
                    return processFeedCommand(parts);
                case "produces":
                    return processProducesCommand();
                case "collect":
                    return processCollectCommand(parts);
                case "sell":
                    return processSellCommand(parts);
                case "cheat":
                    return processCheatCommand(parts);
                case "animals":
                    return processAnimalsCommand();
                default:
                    return "Unknown command";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String processPetCommand(String[] parts) {
        if (parts.length < 3 || !parts[1].equals("-n")) {
            return "Invalid pet command format. Use: pet -n <name>";
        }
        String name = parts[2];
        Animal animal = null;
        for (Animal a : user.getAnimals()) {
            if (a.getName().equalsIgnoreCase(name)) {
                animal = a;
            }
        }

        if (animal == null) return "Animal not found: " + name;

        if (Math.abs(user.getPosition().getPositionX() - animal.getPositionX()) > 1 ||
                Math.abs(user.getPosition().getPositionY() - animal.getPositionY()) > 1) {
            return "far away";
        }

        animal.pet();
        return "Petted " + name + " successfully!";
    }

    private String processShepherdCommand(String[] parts) {
        String name = parts[3];
        String coordinates = parts[5];

        if (name == null || coordinates == null) {
            return "Invalid shepherd command format. Use: shepherd animals -n <name> -l <x,y>";
        }

        if (!coordinates.matches("\\d+,\\d+")) {
            return "Invalid coordinates format";
        }

        Animal animal = null;
        for (Animal a : user.getAnimals()) {
            if (a.getName().equalsIgnoreCase(name)) {
                animal = a;
                break;
            }
        }
        if (animal == null) {
            return "Animal not found: " + name;
        }

        String[] coordParts = coordinates.split(",");
        int newX, newY;
        try {
            newX = Integer.parseInt(coordParts[0]);
            newY = Integer.parseInt(coordParts[1]);
        } catch (NumberFormatException e) {
            return "Invalid coordinates format";
        }

        if (newX < 0 || newX >= 50 || newY < 0 || newY >= 50) {
            return "Invalid coordinates";
        }

        // بررسی تایل فعلی (مبدا)
        int currentX = animal.getPositionX();
        int currentY = animal.getPositionY();
        Tile currentTile = tiles[currentX][currentY];
        char currentSymbol = currentTile.getStaticElement()
                .map(StaticElement::symbol)
                .orElse('\0');

        Tile newTile = tiles[newX][newY];
        char newSymbol = newTile.getStaticElement()
                .map(StaticElement::symbol)
                .orElse('\0');

        if ((currentSymbol == 'O' || currentSymbol == 'B') && (newSymbol == 'O' || newSymbol == 'B')) {
            return "Cannot move animal within it's home";
        }

        animal.setPositionX(newX);
        animal.setPositionY(newY);

        boolean newOutside = !((newSymbol == 'B') || (newSymbol == 'O'));
        animal.setOutside(newOutside);

        boolean currentOutside = !((currentSymbol == 'O') || (currentSymbol == 'B'));

        if (newOutside && !currentOutside) {
            animal.feed(true);
        }

        // تولید پیام مناسب
        if (!newOutside && currentOutside) {
            return name + " has returned home at " + coordinates;
        } else if (newOutside && !currentOutside) {
            return name + " is taken out to graze at " + coordinates;
        } else {
            return name + " is now outside at " + coordinates;
        }
    }

    private String processFeedCommand(String[] parts) {
        if (parts.length < 3 || !parts[1].equals("hay") || !parts[2].equals("-n")) {
            return "Invalid feed command format. Use: feed hay -n <name>";
        }
        String name = parts[3];
        Animal animal = null;
        for (Animal a : user.getAnimals()) {
            if (a.getName().equalsIgnoreCase(name)) {
                animal = a;
            }
        }
        if (animal == null) return "Animal not found: " + name;

        animal.feed(false);
        return "Fed " + name + " with hay";
    }

    private String processProducesCommand() {
        StringBuilder result = new StringBuilder();
        boolean anyProducts = false;

        result.append("Animals with ready-to-collect products:\n");

        for (Animal animal : user.getAnimals()) {
            if (animal.hasProducedToday() && animal.getCurrentProduct() != null) {
                anyProducts = true;
                String productName = animal.getCurrentProduct().getName();
                result.append(String.format(
                        "- %s: %s (Quality: %s)\n",
                        animal.getName(),
                        productName,
                        animal.getCurrentProduct().getProperties().get("quality")
                ));
            }
        }

        if (!anyProducts) {
            return "No animals have ready-to-collect products.";
        }

        return result.toString();
    }

    private String processCollectCommand(String[] parts) {
        // TODO : برسی موجود بودن ابزار و اضافه کردن به یوزر
        return null;
    }

    private String processSellCommand(String[] parts) {
        if (parts.length < 4 || !parts[1].equals("animal") || !parts[2].equals("-n")) {
            return "Invalid sell command format. Use: sell animal -n <name>";
        }
        String name = parts[3];
        Animal animal = null;
        for (Animal a : user.getAnimals()) {
            if (a.getName().equalsIgnoreCase(name)) {
                animal = a;
            }
        }
        if (animal == null) return "Animal not found: " + name;

        int price = animal.calculateSellPrice();
        user.setMoney(price + user.getMoney());
        user.getAnimals().remove(animal);
        return "Sold " + name + " for " + price + " golds";
    }

    private String processCheatCommand(String[] parts) {
        if (parts.length < 6 || !parts[1].equals("set") || !parts[2].equals("friendship")) {
            return "Invalid cheat command format";
        }
        String name = null;
        int amount = 0;
        for (int i = 3; i < parts.length; i++) {
            if (parts[i].equals("-n") && i + 1 < parts.length) {
                name = parts[++i];
            } else if (parts[i].equals("-c") && i + 1 < parts.length) {
                amount = Integer.parseInt(parts[++i]);
            }
        }

        if (name == null) return "Missing animal name";
        Animal animal = null;
        for (Animal a : user.getAnimals()) {
            if (a.getName().equalsIgnoreCase(name)) {
                animal = a;
            }
        }
        if (animal == null) return "Animal not found: " + name;

        animal.setFriendship(amount);
        return "Friendship set to " + amount + " for " + name;
    }

    private String processAnimalsCommand() {
        StringBuilder sb = new StringBuilder("Animals:\n");
        for (Animal animal : user.getAnimals()) {
            sb.append(animal.toString()).append("\n");
        }
        return sb.toString();
    }

    private String processHugCommand(String to) {
        User UTo = UserRepository.getInstance().getUserByUsername(to);
        if (UTo == null) {
            return "User not found: " + to;
        }

        if (Math.abs(UTo.getPosition().getPositionX() - user.getPosition().getPositionX()) > 1 ||
                Math.abs(UTo.getPosition().getPositionY() - user.getPosition().getPositionY()) > 1) {
            return "far away";
        }

        if (user.getFriendshipXpsWithUsers(UTo) < 300 || UTo.getFriendshipXpsWithUsers(user) < 300) {
            return "you are not intimate enough";
        }

        user.increaseFriendshipXpsWithUsers(UTo, 60);
        UTo.increaseFriendshipXpsWithUsers(user, 60);

        return user.getNickname() + " hugged " + UTo.getNickname();
    }

    private String processFlowerCommand(String to) {
        User UTo = UserRepository.getInstance().getUserByUsername(to);
        if (UTo == null) {
            return "User not found: " + to;
        }

        if (Math.abs(UTo.getPosition().getPositionX() - user.getPosition().getPositionX()) > 1 ||
                Math.abs(UTo.getPosition().getPositionY() - user.getPosition().getPositionY()) > 1) {
            return "far away";
        }

        if (user.getFriendshipXpsWithUsers(UTo) < 600 || UTo.getFriendshipXpsWithUsers(user) < 600) {
            return "you are not intimate enough";
        }

        user.getInventory().removeItemByName("flower", 1);
        UTo.getInventory().addItemByName("flower", 1);

        user.setFriendshipLevelWithUsers(UTo, 3);
        UTo.setFriendshipLevelWithUsers(user, 3);

        return user.getNickname() + " flowed " + UTo.getNickname();
    }

    private String processMarriageCommand(String to, String ring) {
        User UTo = UserRepository.getInstance().getUserByUsername(to);
        if (UTo == null) {
            return "User not found: " + to;
        }

        if (UTo.getGender().equals("male")) {
            return "che ghalata";
        }

        if (user.getFriendshipLevelWithUsers(UTo) < 3) {
            return "aval mokhesho bezan";
        }

        Item item = null;
        for (Item i : user.getInventory().getItems()) {
            if (i.getName().equalsIgnoreCase(ring)) {
                item = i;
            }
        }

        if (item == null) return "ring not found: " + ring;

        UTo.addMarriageRequest(user, item);
        return "Marriage request sent to " + UTo.getNickname();
    }

    private String processRespondCommand(String response, String username) {
        User sender = UserRepository.getInstance().getUserByUsername(username);
        User.MarriageRequest request = user.getMarriageRequests().get(sender);

        if (request == null) return "No pending request";
        if (request.isResponded()) return "Already responded";

        request.setResponded(true);

        if (response.equalsIgnoreCase("--accept")) {
            sender.getInventory().removeItem(request.getRing());
            user.getInventory().addItem(request.getRing());

            sender.setFriendshipLevelWithUsers(user, 4);
            user.setFriendshipLevelWithUsers(sender, 4);

            int totalMoney = sender.getMoney() + user.getMoney();
            sender.setMoney(totalMoney);
            user.setMoney(totalMoney);

            user.setSpouse(sender);
            sender.setSpouse(user);

            return "Marriage accepted! Resources merged.";
        } else {
            // رد درخواست
            sender.setFriendshipLevelWithUsers(user, 0);
            user.setFriendshipLevelWithUsers(sender, 0);
            sender.applyEnergyPenalty();
            return "Marriage rejected. Friendship reset.";
        }
    }
}
