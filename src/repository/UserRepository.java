package repository;

import models.User;

import java.util.HashMap;
import java.util.List;

public class UserRepository {
    private static List<User> allUsers;

    public List<User> getAllUsers() {
        return allUsers;
    }
}
