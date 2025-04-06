package view.commands;

public enum GameCommands {
    NEW_GAME("game new"),
    CHOOSE_MAP("game map"),
    LOAD_GAME("load game");

    private final String commandPrefix;

    GameCommands (String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }
}
