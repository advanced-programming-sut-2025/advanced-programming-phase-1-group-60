package controller;

import models.User;
import models.Result;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterController {
    private Map<String, User> users;

    public RegisterController() {
        this.users = new HashMap<>();
    }

    public Result processRegister(String username, String password, String passwordConfirm, String nickname, String email, String gender) {
        if (users.containsKey(username)) {
            return new Result(false, "Username already exists.");
        }
        if (!User.verifyEmail(email)) {
            return new Result(false, "Invalid email format.");
        }
        if (!User.verifyPassword(password)) {
            return new Result(false, "Weak password. It must be at least 8 characters, include uppercase," +
                    " lowercase, digit, and special character.");
        }
        if (!password.equals(passwordConfirm)) {
            return new Result(false, "Passwords do not match.");
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

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        return User.hashedPassword(password);
    } //لازمه؟؟؟

    // انتخاب سوال امنیتی (الان فرضی میزنیم که سوال واقعی تو پروژه باشه)
    public Result pickQuestion(int questionNumber, String answer, String answerConfirm) {
        if (!answer.equals(answerConfirm)) {
            return new Result(false, "Answers do not match.");
        }
        // سوالات رو جداگانه مدیریت میکنیم.
        return new Result(true, "Security question selected successfully.");
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

    public Map<String, User> getUsers() {
        return users;
    }
}
