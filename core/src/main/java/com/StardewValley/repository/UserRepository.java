package com.StardewValley.repository;

import com.StardewValley.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    // تنها نمونه‌ی موجود از کلاس
    private static UserRepository instance;

    // لیست کاربران (غیر‌استاتیک)
    private List<User> allUsers;

    // سازنده خصوصی تا از بیرون نتوان مثالی ساخت
    private UserRepository() {
        this.allUsers = new ArrayList<>();
    }

    // متد دسترسی به Singleton؛ در صورت null بودن، نمونه می‌سازد
    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public User getUserByUsername(String username) {
        for (User user : allUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("core/src/main/java/com/StardewValley/Network/Database/Users.csv"))) {
            for (User user : allUsers) {
                writer.println(user.getUsername() + "," +
                    user.getPlainPassword() + "," +
                    user.getNickname() + "," +
                    user.getEmail() + "," +
                    user.getGender());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadUsers() {
        // allUsers.clear(); // This line is removed to prevent clearing existing user data.
        File file = new File("core/src/main/java/com/StardewValley/Network/Database/Users.csv");
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String username = parts[0];

                    // Check if user already exists in memory to avoid duplicates and overwriting.
                    boolean userExists = false;
                    for (User existingUser : this.allUsers) {
                        if (existingUser.getUsername().equals(username)) {
                            userExists = true;
                            break;
                        }
                    }

                    // If user doesn't exist, create and add them to the list.
                    if (!userExists) {
                        try {
                            User user = new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
                            allUsers.add(user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // مثال متد برای اضافه کردن کاربر
    public void addUser(User user) {
        allUsers.add(user);
    }
}
