package view.commands;

public enum RegisterCommands {
    REGISTER("register -u <username> -p <password> <password_confirm> -n <nickname> -e <email> -g <gender>"),
    PICK_QUESTION("pick question -q <question_number> -a <answer> -c <answer_confirm>"),
    EXIT("exit");
    private final String commandPrefix;

    RegisterCommands(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    public static RegisterCommands getCommand(String input) {
        input = input.toLowerCase().trim();
        for (RegisterCommands command : RegisterCommands.values()) {
            if (input.startsWith(command.commandPrefix.split(" ")[0])) {
                return command;
            }
        }
        return null;
    }
}