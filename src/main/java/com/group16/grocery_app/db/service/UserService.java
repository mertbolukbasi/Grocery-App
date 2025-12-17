package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.UserRepository;
import com.group16.grocery_app.model.User;

import java.sql.SQLException;

public class UserService {

    private UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String password) {
        if (userRepository.checkUserForLogin(username, password)) {
            try {
                return userRepository.findByUsername(username);
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }
}
