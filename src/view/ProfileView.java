package view;

import java.util.Scanner;

import controller.ProfileController;
import models.Result;
import models.User;
import view.commands.ProfileCommands;

public class ProfileView extends View {
    private final ProfileController profileController;
    private final Scanner scanner;

    public ProfileView(ProfileController profileController, Scanner scanner) {
        this.profileController = profileController;
        this.scanner = scanner;
    }

    @Override
    public void display() {
        System.out.println("=== Profile Menu ===");
        System.out.println("- change username -u <newusername>");
        System.out.println("- change nickname -n <newnickname>");
        System.out.println("- change email -e <email>");
        System.out.println("- change password -p <newpassword> -o <oldpassword>");
        System.out.println("- show info");
        System.out.println("- exit");

        while (true) {
            String input = scanner.nextLine().trim();
            ProfileCommands command = ProfileCommands.getCommand(input);

            if (input.equals("exit")) {
                break;
            }

            if (command == null) {
                System.out.println("Invalid command. Please try again.");
                continue;
            }

            switch (command) {
                case CHANGE_USERNAME:
                    if (input.matches("change username -u .+")) {
                        String newUsername = input.substring(input.indexOf("-u ") + 3).trim();
                        Result result = profileController.changeUserName(newUsername);
                        System.out.println(result.getMessage());
                    } else {
                        System.out.println("Invalid format. Use: change username -u <newusername>");
                    }
                    break;

                case CHANGE_NICKNAME:
                    if (input.matches("change nickname -n .+")) {
                        String newNickname = input.substring(input.indexOf("-n ") + 3).trim();
                        Result result = profileController.changeNickname(newNickname);
                        System.out.println(result.getMessage());
                    } else {
                        System.out.println("Invalid format. Use: change nickname -n <newnickname>");
                    }
                    break;

                case CHANGE_EMAIL:
                    if (input.matches("change email -e .+")) {
                        String newEmail = input.substring(input.indexOf("-e ") + 3).trim();
                        Result result = profileController.changeEmail(newEmail);
                        System.out.println(result.getMessage());
                    } else {
                        System.out.println("Invalid format. Use: change email -e <email>");
                    }
                    break;

                case CHANGE_PASSWORD:
                    if (input.matches("change password -p .+ -o .+")) {
                        String newPassword = input.substring(input.indexOf("-p ") + 3, input.indexOf(" -o")).trim();
                        String oldPassword = input.substring(input.indexOf("-o ") + 3).trim();
                        Result result = profileController.changePassword(newPassword, oldPassword);
                        System.out.println(result.getMessage());
                    } else {
                        System.out.println("Invalid format. Use: change password -p <newpassword> -o <oldpassword>");
                    }
                    break;

                case SHOW_INFO:
                    Result result = profileController.showUserInfo();
                    System.out.println(result.getMessage());
                    break;
            }
        }
    }
}
