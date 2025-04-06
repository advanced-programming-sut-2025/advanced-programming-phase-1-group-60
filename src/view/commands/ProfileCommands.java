package view.commands;

public enum ProfileCommands {
    CHANGE_USERNAME("change username"),
    CHANGE_NICKNAME("change nickname"),
    CHANGE_EMAIL("change email"),
    CHANGE_PASSWORD("change password"),
    USER_INFO("uesr info");

    private final String commandPrefix;

    ProfileCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }
}
