package com.StardewValley.repository;

import com.StardewValley.models.User;

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

    // مثال متد برای اضافه کردن کاربر
    public void addUser(User user) {
        allUsers.add(user);
    }
}
