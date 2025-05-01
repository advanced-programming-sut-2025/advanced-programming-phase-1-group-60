// src/controller/ProfileController.java
package controller;

import models.User;
import models.Result;

public class ProfileController {
    private User user;

    public ProfileController(User user) {
        this.user = user;
    }

    public Result changeUserName(String newUserName) {
        if (newUserName == null || newUserName.isEmpty()) {
            return new Result(false, "Username cannot be empty.");
        }
        if(!User.verifyName(newUserName)) {
            return new Result(false, "Username format is incorrect.");
        }
        user.setUsername(newUserName);
        return new Result(true, "Username changed successfully.");
    }

    public Result changeNickname(String newNickName) {
        if (newNickName == null || newNickName.isEmpty()) {
            return new Result(false, "Nickname cannot be empty.");
        }
        user.setNickname(newNickName);
        return new Result(true, "Nickname changed successfully.");
    }

    public Result changeEmail(String newEmail) {
        if (!User.verifyEmail(newEmail)) {
            return new Result(false, "Invalid email format.");
        }
        user.setEmail(newEmail);
        return new Result(true, "Email changed successfully.");
    }

    public Result changePassword(String newPassword, String oldPassword) {
        try {
            if (!user.getHashPassword().equals(User.hashedPassword(oldPassword))) {
                return new Result(false, "Old password is incorrect.");
            }
            if (!User.verifyPassword(newPassword)) {
                return new Result(false, "New password is weak.");
            }
            user.setHashPassword(newPassword);
            return new Result(true, "Password changed successfully.");
        } catch (Exception e) {
            return new Result(false, "Internal error during password change.");
        }
    }

    public Result showUserInfo() {
        String info = "Username: " + user.getUsername() +
                "\nNickname: " + user.getNickname() +
                "\nEmail: " + user.getEmail() +
                "\nGender: " + user.getGender();
        return new Result(true, info);
    }
}
