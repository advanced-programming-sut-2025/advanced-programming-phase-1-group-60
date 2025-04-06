package view.commands;

public enum GamePlayCommands {
    EXIT_GAME("exit game"),
    NEXT_TURN("next turn");

    private final String commandPrefix;

    GamePlayCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
