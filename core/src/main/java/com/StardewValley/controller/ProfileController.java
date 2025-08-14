package com.StardewValley.controller;

import com.StardewValley.models.Result;
import com.StardewValley.models.User;
import com.StardewValley.repository.UserRepository;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ProfileController {
    private User user;
    private final UserRepository userRepository;

    public ProfileController(User user) {
        this.user = user;
        this.userRepository = UserRepository.getInstance();
    }

    /**
     * Attempts to change the username.
     * Rules:
     *  - Not null / not empty
     *  - Matches regex ^[a-zA-Z0-9_]{3,20}$
     *  - Must not already exist in repository (unless it's the current user's own name)
     * NOTE: If UserRepository indexes users by username internally, simply changing the
     *       field on User without updating the repository will leave stale mappings.
     *       If that is the case, add a proper rename support method in UserRepository
     *       (e.g., userRepository.renameUser(oldUsername, newUsername, user)).
     */
    public Result changeUserName(String newUserName) {
        if (newUserName == null || newUserName.trim().isEmpty()) {
            return new Result(false, "Username cannot be empty.");
        }
        newUserName = newUserName.trim();

        if (!isValidUsername(newUserName)) {
            return new Result(false,
                "Invalid username. Use 3-20 chars: letters, digits or underscore.");
        }

        if (newUserName.equals(user.getUsername())) {
            return new Result(true, "Username unchanged.");
        }

        // Check repository for existing user
        User existing = userRepository.getUserByUsername(newUserName);
        if (existing != null) {
            return new Result(false, "Username already exists.");
        }

        // WARNING (see note above)
        String old = user.getUsername();
        user.setUsername(newUserName);
        // TODO: If repository stores by username key, implement a rename method there.
        return new Result(true, "Username changed successfully from '" + old + "' to '" + newUserName + "'.");
    }

    public Result changeNickname(String newNickName) {
        if (newNickName == null || newNickName.trim().isEmpty()) {
            return new Result(false, "Nickname cannot be empty.");
        }
        user.setNickname(newNickName.trim());
        return new Result(true, "Nickname changed successfully.");
    }

    public Result changeEmail(String newEmail) {
        if (newEmail == null || !User.verifyEmail(newEmail.trim())) {
            return new Result(false, "Invalid email format.");
        }
        user.setEmail(newEmail.trim());
        return new Result(true, "Email changed successfully.");
    }

    public Result changePassword(String newPassword, String oldPassword) {
        try {
            if (oldPassword == null || newPassword == null) {
                return new Result(false, "Password fields cannot be empty.");
            }
            if (!user.getHashPassword().equals(User.hashedPassword(oldPassword))) {
                return new Result(false, "Old password is incorrect.");
            }
            if (!User.verifyPassword(newPassword)) {
                return new Result(false, "New password is weak (needs upper, lower, digit, special, length >= 8).");
            }
            user.setHashPassword(newPassword);
            return new Result(true, "Password changed successfully.");
        } catch (Exception e) {
            return new Result(false, "Internal error during password change.");
        }
    }

    public Result showUserInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Username: ").append(user.getUsername()).append("\n");
        info.append("Nickname: ").append(user.getNickname()).append("\n");
        info.append("Email: ").append(user.getEmail()).append("\n");
        info.append("Gender: ").append(user.getGender());
        return new Result(true, info.toString());
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    /**
     * Generate a strong random password similar to registration logic.
     * Ensures at least one upper, one lower, one digit, one special, length >= 10.
     */
    public String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specials = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String all = upper + lower + digits + specials;
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        // Guarantee at least one of each
        sb.append(upper.charAt(r.nextInt(upper.length())));
        sb.append(lower.charAt(r.nextInt(lower.length())));
        sb.append(digits.charAt(r.nextInt(digits.length())));
        sb.append(specials.charAt(r.nextInt(specials.length())));

        while (sb.length() < 12) {
            sb.append(all.charAt(r.nextInt(all.length())));
        }

        // Optionally shuffle
        char[] chars = sb.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = r.nextInt(chars.length);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }

    public User getUser() {
        return user;
    }
}
