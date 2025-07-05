package com.StardewValley.view.commands;

public enum LoginCommands {
    LOGIN("login"),
    FORGET_PASSWORD("forget password"),
    ANSWER("answer"),
    EXIT("exit");

    private final String commandPrefix;

    LoginCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    public static LoginCommands getCommand(String input) {
        input = input.toLowerCase().trim();
        for (LoginCommands command : LoginCommands.values()) {
            if (input.startsWith(command.commandPrefix)) {
                return command;
            }
        }
        return null;
    }
}
