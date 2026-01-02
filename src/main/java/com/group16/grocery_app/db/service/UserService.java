package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.UserRepository;
import com.group16.grocery_app.model.User;
import javafx.collections.ObservableList;

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

    public boolean register(String username, String password, String firstName, String lastName) {
        if (userRepository.usernameExists(username)) {
            return false;
        }
        return userRepository.createUser(username, password, firstName, lastName, null);
    }

    public boolean usernameExists(String username) {
        return userRepository.usernameExists(username);
    }

    public boolean updateProfile(int userId, String address, String phoneNumber) {
        return userRepository.updateProfile(userId, address, phoneNumber);
    }

    public ObservableList<User> getCarriers() {
        try {
            return userRepository.getCarriers();
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    public boolean createCarrier(String username, String password, String firstName, String lastName) {
        if (userRepository.usernameExists(username)) {
            return false;
        }
        return userRepository.createCarrier(username, password, firstName, lastName, null);
    }

    public boolean phoneNumberExists(String phoneNumber, Integer excludeUserId) {
        return userRepository.phoneNumberExists(phoneNumber, excludeUserId);
    }

    public boolean incrementLoyaltyPoints(int userId, int points) {
        return userRepository.incrementLoyaltyPoints(userId, points);
    }

    public boolean removeCarrier(int carrierId) {
        return userRepository.removeCarrier(carrierId);
    }

    public User findOwner() {
        try {
            return userRepository.findByRole(com.group16.grocery_app.model.Role.OWNER);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findById(int userId) {
        try {
            return userRepository.findById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
