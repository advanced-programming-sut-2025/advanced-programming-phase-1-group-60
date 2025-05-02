package controller;

import models.User;
import models.Result;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterController {
    private Map<String, User> users;
    private Map<Integer, String> securityQuestions;

    public RegisterController() {
        this.users = new HashMap<>();
        this.securityQuestions = new HashMap<>();
//        populateSecurityQuestions();
    }

//    private void populateSecurityQuestions() {
//
//    }

    public Result register(String username, String password, String passwordConfirm, String nickname, String email, String gender) {
        if (!isValidUsername(username)) {
            return new Result(false, "Invalid username. It must be at least 3 characters and include only letters, digits, or underscores.");
        }
        if (users.containsKey(username)) {
            return new Result(false, "Username already exists.");
        }
        if (!User.verifyEmail(email)) {
            return new Result(false, "Invalid email format.");
        }
        if (!User.verifyPassword(password)) {
            return new Result(false, "Weak password. It must be at least 8 characters, include uppercase, lowercase, digit, and special character.");
        }
        if (!password.equals(passwordConfirm)) {
            return new Result(false, "Passwords do not match.");
        }
        if (!isValidGender(gender)) {
            return new Result(false, "Invalid gender. It must be 'Male' or 'Female'.");
        }
        if (!isValidNickname(nickname)) {
            return new Result(false, "Invalid nickname. It cannot be empty and must only include letters or digits.");
        }

        try {
            String hashedPassword = hashPassword(password);
            User newUser = new User(username, hashedPassword, nickname, email, gender);
            users.put(username, newUser);
            return new Result(true, "User registered successfully.", newUser);
        } catch (Exception e) {
            return new Result(false, "Internal error during registration.");
        }
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    private boolean isValidGender(String gender) {
        return gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female");
    }

    private boolean isValidNickname(String nickname) {
        return nickname != null && nickname.matches("^[a-zA-Z0-9]{1,30}$");
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        return User.hashedPassword(password);
    }

    public Result pickQuestion(int questionNumber, String answer, String answerConfirm) {
        if (!securityQuestions.containsKey(questionNumber)) {
            return new Result(false, "Invalid question number.");
        }
        if (!answer.equals(answerConfirm)) {
            return new Result(false, "Answers do not match.");
        }
        // سوال امنیتی انتخاب و پاسخ ذخیره شود (فرضی)
        return new Result(true, "Security question selected successfully.");
    }

    public String getSecurityQuestion(int questionNumber) {
        return securityQuestions.getOrDefault(questionNumber, "Question not found.");
    }

    public String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specials = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String allChars = upper + lower + digits + specials;
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specials.charAt(random.nextInt(specials.length())));

        while (password.length() < 10) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return password.toString();
    }

    public Result deleteUser(String username) {
        if (!users.containsKey(username)) {
            return new Result(false, "User not found.");
        }
        users.remove(username);
        return new Result(true, "User deleted successfully.");
    }

    public Result updateUser(String username, String newNickname, String newEmail) {
        User user = users.get(username);
        if (user == null) {
            return new Result(false, "User not found.");
        }
        if (newNickname != null && !newNickname.trim().isEmpty()) {
            user.setNickname(newNickname);
        }
        if (newEmail != null && User.verifyEmail(newEmail)) {
            user.setEmail(newEmail);
        } else if (newEmail != null) {
            return new Result(false, "Invalid email format.");
        }
        return new Result(true, "User updated successfully.", user);
    }

    public User findUser(String username) {
        return users.get(username);
    }

    public Map<String, User> getUsers() {
        return users;
    }
}