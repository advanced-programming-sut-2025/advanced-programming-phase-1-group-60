package com.StardewValley.controller;

import com.StardewValley.models.Menu;

public class MenuController {
    private Menu menu;

    public MenuController() {
        this.menu = new Menu();
    }

    public void enterMenu(String menuName) {
        menu.enterMenu(menuName);
    }

    public void exitMenu() {
        menu.exitMenu();
    }

    public String showCurrentMenu() {
        return menu.getCurrentMenu();
    }
}
