package controller;

import models.*;
import repository.ItemRepository;

import java.util.List;
import java.util.Scanner;

public class GamePlayController {
    Scanner sc;

    private final Tile[][] tiles;
    private final User user;
    private final Scanner in = new Scanner(System.in);

    public GamePlayController(Farm f, User u, Scanner sc) {
        this.tiles = f.getTiles();
        this.user = u;
        this.sc = sc;
    }

    public void getAndProcessInput() {
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
            } else if (parts.length == 2 && parts[1].equalsIgnoreCase("current")) {
                showCurrentTool();
            } else if (parts.length == 2 && parts[1].equalsIgnoreCase("available")) {
                showAvailableTools();
            } else {
                System.out.println("Unknown tool command.");
            }
        } else {
            System.out.println("Unknown command.");
        }
    }

    public void equipTool(int toolId) {
        Item tool = ItemRepository.getItemById(toolId);
        if (tool == null) {
            System.out.println("Invalid tool ID.");
            return;
        }

        // Check if tool is in backpack
        boolean hasToolInBackpack = user.getBackpackItems().stream()
                .anyMatch(item -> item.getId() == toolId);

        if (!hasToolInBackpack) {
            System.out.println("You don't have this tool in your backpack.");
            return;
        }

        user.setEquippedTool(tool);
        System.out.println("Equipped tool: " + tool.getName());
    }

    private void showCurrentTool() {
        Item currentTool = user.getEquippedTool();
        if (currentTool == null) {
            System.out.println("No tool equipped.");
        } else {
            System.out.println("Current tool: " + currentTool.getName() + " (ID: " + currentTool.getId() + ")");
        }
    }

    private void showAvailableTools() {
        List<Item> backpackItems = user.getBackpackItems();
        if (backpackItems == null || backpackItems.isEmpty()) {
            System.out.println("No tools in backpack.");
            return;
        }

        System.out.println("Available tools in backpack:");
        for (Item item : backpackItems) {
            System.out.println("- " + item.getName() + " (ID: " + item.getId() + ")");
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
        if (!in.nextLine().equalsIgnoreCase("y")) return;
        if (user.getEnergy().getCurrentEnergy() >= need || user.getEnergy().isUnlimited()) {
            user.consumeEnergy(need);
            user.setPosition(target);
            System.out.println("حرکت شد. انرژی=" + user.getEnergy());
            if (user.getEnergy().getCurrentEnergy() == 0) user.faint();
        } else user.faintAlong(path);
    }












































    public Result saveGame() {
        return null;
    }

    public Result forceTerminate() {
        return null;
    }

    public Result nextTurn() {
        updateGameState();
        return null;
    }

    // Current Game State
    // TODO : Update the game state such as turns, time, weather ,etc.
    private void updateGameState() {
    }

    public Result walk() {

        // if (energy <= 0)
        faint();
        return null;
    }

    public Result printMap() {
        return null;
    }

    public Result helpReadingMap() {
        return null;
    }

    public Result showEnergy() {
        return null;
    }

    private Result faint() {
        return null;
    }


    // cheat functions :
}
