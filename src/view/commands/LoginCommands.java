package view.commands;

public enum LoginCommands {
    LOGIN("login"),
    FORGET_PASSWORD("forget password"),
    ANSWER("answer");

    private final String commandPrefix;

    LoginCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

}
