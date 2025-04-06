package view.commands;

public enum TimeCommands {
    SHOW_TIME("time"),
    SHOW_DATE("date"),
    SHOW_DATE_AND_TIME("datetime"),
    SHOW_DAY_OF_THE_WEEK("day of the week");

    private final String commandPrefix;

    TimeCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
