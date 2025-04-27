package models;

public class Menu {
        private String currentMenu;

        public Menu() {
            this.currentMenu = "login/register";
        }

        public void enterMenu(String menuName) {
            this.currentMenu = menuName;
        }

        public void exitMenu() {
            this.currentMenu = "login/register";
        }

        public String getCurrentMenu() {
            return currentMenu;
        }
    }

