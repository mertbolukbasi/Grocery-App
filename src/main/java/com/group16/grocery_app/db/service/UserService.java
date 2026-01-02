package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.UserRepository;
import com.group16.grocery_app.model.User;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * Service class for user-related operations.
 * Handles login, registration, profile updates, and user management.
 *
 * @author Mert Bölükbaşı
 */
public class UserService {

    private UserRepository userRepository;

    /**
     * Creates a new UserService instance.
     *
     * @author Mert Bölükbaşı
     */
    public UserService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username user's username
     * @param password user's password
     * @return User object if login successful, null otherwise
     * @author Mert Bölükbaşı
     */
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

    /**
     * Registers a new customer user.
     *
     * @param username desired username
     * @param password user password
     * @param firstName user's first name
     * @param lastName user's last name
     * @return true if registration successful, false if username exists
     * @author Mert Bölükbaşı
     */
    public boolean register(String username, String password, String firstName, String lastName) {
        if (userRepository.usernameExists(username)) {
            return false;
        }
        return userRepository.createUser(username, password, firstName, lastName, null);
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check
     * @return true if username exists, false otherwise
     * @author Mert Bölükbaşı
     */
    public boolean usernameExists(String username) {
        return userRepository.usernameExists(username);
    }

    /**
     * Updates user profile information.
     *
     * @param userId user ID to update
     * @param address new address
     * @param phoneNumber new phone number
     * @return true if update successful
     * @author Mert Bölükbaşı
     */
    public boolean updateProfile(int userId, String address, String phoneNumber) {
        return userRepository.updateProfile(userId, address, phoneNumber);
    }

    /**
     * Gets all carrier users.
     *
     * @return list of carrier users, empty list on error
     * @author Mert Bölükbaşı
     */
    public ObservableList<User> getCarriers() {
        try {
            return userRepository.getCarriers();
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    /**
     * Creates a new carrier user.
     *
     * @param username desired username
     * @param password user password
     * @param firstName carrier's first name
     * @param lastName carrier's last name
     * @return true if creation successful, false if username exists
     * @author Mert Bölükbaşı
     */
    public boolean createCarrier(String username, String password, String firstName, String lastName) {
        if (userRepository.usernameExists(username)) {
            return false;
        }
        return userRepository.createCarrier(username, password, firstName, lastName, null);
    }

    /**
     * Checks if a phone number already exists for another user.
     *
     * @param phoneNumber phone number to check
     * @param excludeUserId user ID to exclude from check
     * @return true if phone number exists for another user
     * @author Mert Bölükbaşı
     */
    public boolean phoneNumberExists(String phoneNumber, Integer excludeUserId) {
        return userRepository.phoneNumberExists(phoneNumber, excludeUserId);
    }

    /**
     * Adds loyalty points to a user's account.
     *
     * @param userId user ID
     * @param points points to add
     * @return true if update successful
     * @author Mert Bölükbaşı
     */
    public boolean incrementLoyaltyPoints(int userId, int points) {
        return userRepository.incrementLoyaltyPoints(userId, points);
    }

    /**
     * Removes a carrier from the system.
     *
     * @param carrierId carrier user ID to remove
     * @return true if removal successful
     * @author Mert Bölükbaşı
     */
    public boolean removeCarrier(int carrierId) {
        return userRepository.removeCarrier(carrierId);
    }

    /**
     * Finds the owner user in the system.
     *
     * @return owner user, null if not found or error occurs
     * @author Mert Bölükbaşı
     */
    public User findOwner() {
        try {
            return userRepository.findByRole(com.group16.grocery_app.model.Role.OWNER);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId user ID to search for
     * @return User object if found, null otherwise
     * @author Mert Bölükbaşı
     */
    public User findById(int userId) {
        try {
            return userRepository.findById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
