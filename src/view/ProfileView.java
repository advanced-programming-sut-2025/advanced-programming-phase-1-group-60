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
        System.out.println("- change username");
        System.out.println("- change nickname");
        System.out.println("- change email");
        System.out.println("- change password");
        System.out.println("- show info");
        System.out.println("- exit");
//ست کردن مشخصات کاربر از فایل؟؟
       // User currentUser;
        while (true) {
            String input = scanner.nextLine().trim();
            ProfileCommands command = ProfileCommands.getCommand(input);

            if (command == null) {
                    System.out.println("Invalid command. Please try again.");
                continue;
            }
            if (command == ProfileCommands.CHANGE_USERNAME) {
                String newUsername = input.trim();
                Result result = profileController.changeUserName(newUsername);
                System.out.println(result.getMessage());

            } else if (command == ProfileCommands.CHANGE_NICKNAME) {
                String newNickname = input.trim();
                Result result = profileController.changeNickname(newNickname);
                System.out.println(result.getMessage());

            } else if (command == ProfileCommands.CHANGE_EMAIL) {
                String newEmail = input.trim();
                Result result = profileController.changeEmail(newEmail);
                System.out.println(result.getMessage());

            } else if (command == ProfileCommands.CHANGE_PASSWORD) {
                //اینجا باید رمز قبلی کاربر رو از فایل بگیریم
                //or
                String oldPassword = input.trim();
               String newPassword = input.trim();
                Result result = profileController.changePassword(oldPassword, newPassword);
                System.out.println(result.getMessage());

            } else if (command == ProfileCommands.SHOW_INFO) {
                Result result = profileController.showUserInfo();
                System.out.println(result.getMessage());
            }
        }
    }
}
