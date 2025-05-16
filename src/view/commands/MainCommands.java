package view.commands;

public enum MainCommands {
    USER_LOGOUT("user logout"),
    GO_TO_AVATAR("avatar"),
    GO_TO_PROFILE("profile"),
    GO_TO_GAME("game")
    ,PLACE_ITEM("place item"); // اضافه کردن دستور جدید


    private final String commandPrefix;

    MainCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }
}
