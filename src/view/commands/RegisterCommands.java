package view.commands;

public enum RegisterCommands {
    REGISTER("register"),
    PICK_QUESTION("pick question");

    private final String commandPrefix;

    RegisterCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
