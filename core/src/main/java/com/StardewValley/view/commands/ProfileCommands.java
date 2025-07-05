package com.StardewValley.view.commands;

public enum ProfileCommands {
        CHANGE_USERNAME("change username"),
        CHANGE_NICKNAME("change nickname"),
        CHANGE_EMAIL("change email"),
        CHANGE_PASSWORD("change password"),
        SHOW_INFO("show info");

        private final String commandPrefix;

        ProfileCommands(String commandPrefix) {
            this.commandPrefix = commandPrefix;
        }

        public String getCommandPrefix() {
            return commandPrefix;
        }

        public static ProfileCommands getCommand(String input) {
            input = input.toLowerCase().trim();
            for (ProfileCommands command : ProfileCommands.values()) {
                if (input.startsWith(command.commandPrefix)) {
                    return command;
                }
            }
            return null;
        }
    }
