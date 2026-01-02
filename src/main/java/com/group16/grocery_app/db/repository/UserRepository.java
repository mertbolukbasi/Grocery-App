package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Role;
import com.group16.grocery_app.model.User;
import com.group16.grocery_app.utils.PasswordHash;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Repository class for user database operations.
 * Handles CRUD operations for users in the database.
 *
 * @author Mert Bölükbaşı
 */
public class UserRepository {

    private Connection connection;

    /**
     * Creates a new UserRepository instance.
     *
     * @author Mert Bölükbaşı
     */
    public UserRepository() {
        this.connection = Database.getInstance().getConnection();
    }

    /**
     * Finds a user by username.
     *
     * @param username username to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database query fails
     * @author Mert Bölükbaşı
     */
    public User findByUsername(String username) throws SQLException {
        String query = "SELECT * FROM UserInfo WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr.toUpperCase());
                return new User(
                        rs.getInt("userID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getInt("loyalty_points")
                );
            }
        } catch (SQLException e) {
            throw new SQLException("User could not find.", e.getMessage());
        }
        return null;
    }

    /**
     * Validates user credentials for login.
     *
     * @param username username
     * @param password plain text password
     * @return true if credentials are valid
     * @author Mert Bölükbaşı
     */
    public boolean checkUserForLogin(String username, String password) {
        String hashPassword = PasswordHash.hash(password);

        String query = "SELECT userID FROM UserInfo WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Creates a new customer user.
     *
     * @param username   desired username
     * @param password   user password
     * @param firstName user's first name
     * @param lastName  user's last name
     * @param phoneNumber user's phone number (can be null)
     * @return true if user created successfully
     * @author Mert Bölükbaşı
     */
    public boolean createUser(String username, String password, String firstName, String lastName, String phoneNumber) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (connection == null) {
            System.err.println("Database connection is null in createUser");
            return false;
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty() && phoneNumberExists(phoneNumber, null)) {
            return false;
        }
        String query = "INSERT INTO UserInfo (username, password, role, first_name, last_name, phone_number, loyalty_points) VALUES (?, ?, 'customer', ?, ?, ?, 0)";
        String hashPassword = PasswordHash.hash(password);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, hashPassword);
            stmt.setString(3, firstName != null ? firstName.trim() : "");
            stmt.setString(4, lastName != null ? lastName.trim() : "");
            stmt.setString(5, phoneNumber != null && !phoneNumber.trim().isEmpty() ? phoneNumber.trim() : null);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage() != null ? e.getMessage() : "";
            int errorCode = e.getErrorCode();

            if (errorCode == 1062 || errorMessage.contains("Duplicate entry") || errorMessage.contains("Duplicate key")) {
                System.err.println("Duplicate entry error: " + errorMessage);
                return false;
            }

            if (errorCode == 1406 || errorCode == 1264 || errorMessage.contains("Data truncation") || errorMessage.contains("too long")) {
                System.err.println("Data truncation error - password column may be too small. Error: " + errorMessage);
                System.err.println("Please run: ALTER TABLE UserInfo MODIFY COLUMN password VARCHAR(64) NOT NULL;");
                return false;
            }

            System.err.println("SQL Error creating user (Code: " + errorCode + "): " + errorMessage);
            return false;
        }
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username username to check
     * @return true if username exists
     * @author Mert Bölükbaşı
     */
    public boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (connection == null) {
            System.err.println("Database connection is null in usernameExists");
            return false;
        }
        String trimmedUsername = username.trim();
        String query = "SELECT userID FROM UserInfo WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trimmedUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a phone number already exists for another user.
     *
     * @param phoneNumber   phone number to check
     * @param excludeUserId user ID to exclude from check
     * @return true if phone number exists for another user
     * @author Mert Bölükbaşı
     */
    public boolean phoneNumberExists(String phoneNumber, Integer excludeUserId) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        if (connection == null) {
            System.err.println("Database connection is null in phoneNumberExists");
            return false;
        }
        String trimmedPhone = phoneNumber.trim();
        String query;
        if (excludeUserId != null) {
            query = "SELECT userID FROM UserInfo WHERE phone_number = ? AND userID != ?";
        } else {
            query = "SELECT userID FROM UserInfo WHERE phone_number = ?";
        }
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trimmedPhone);
            if (excludeUserId != null) {
                stmt.setInt(2, excludeUserId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates user profile information.
     *
     * @param userId      user ID to update
     * @param address     new address
     * @param phoneNumber new phone number
     * @return true if update successful
     * @author Mert Bölükbaşı
     */
    public boolean updateProfile(int userId, String address, String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty() && phoneNumberExists(phoneNumber, userId)) {
            return false;
        }

        String query = "UPDATE UserInfo SET address = ?, phone_number = ? WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, address != null ? address.trim() : null);
            stmt.setString(2, phoneNumber != null && !phoneNumber.trim().isEmpty() ? phoneNumber.trim() : null);
            stmt.setInt(3, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
        if (connection == null) {
            System.err.println("Database connection is null in incrementLoyaltyPoints");
            return false;
        }
        String query = "UPDATE UserInfo SET loyalty_points = loyalty_points + ? WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, points);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets all carrier users from the database.
     *
     * @return list of carrier users
     * @throws SQLException if database query fails
     * @author Mert Bölükbaşı
     */
    public ObservableList<User> getCarriers() throws SQLException {
        ObservableList<User> carriers = FXCollections.observableArrayList();
        String query = "SELECT * FROM UserInfo WHERE role = 'carrier' ORDER BY username";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr.toUpperCase());
                carriers.add(new User(
                        rs.getInt("userID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getInt("loyalty_points")
                ));
            }
        }

        return carriers;
    }

    /**
     * Creates a new carrier user.
     *
     * @param username    desired username
     * @param password    user password
     * @param firstName   carrier's first name
     * @param lastName    carrier's last name
     * @param phoneNumber carrier's phone number (can be null)
     * @return true if carrier created successfully
     * @author Mert Bölükbaşı
     */
    public boolean createCarrier(String username, String password, String firstName, String lastName, String phoneNumber) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (connection == null) {
            System.err.println("Database connection is null in createCarrier");
            return false;
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty() && phoneNumberExists(phoneNumber, null)) {
            return false;
        }
        String query = "INSERT INTO UserInfo (username, password, role, first_name, last_name, phone_number, loyalty_points) VALUES (?, ?, 'carrier', ?, ?, ?, 0)";
        String hashPassword = PasswordHash.hash(password);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, hashPassword);
            stmt.setString(3, firstName != null ? firstName.trim() : "");
            stmt.setString(4, lastName != null ? lastName.trim() : "");
            stmt.setString(5, phoneNumber != null && !phoneNumber.trim().isEmpty() ? phoneNumber.trim() : null);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage() != null ? e.getMessage() : "";
            int errorCode = e.getErrorCode();

            if (errorCode == 1062 || errorMessage.contains("Duplicate entry") || errorMessage.contains("Duplicate key")) {
                System.err.println("Duplicate entry error: " + errorMessage);
                return false;
            }

            if (errorCode == 1406 || errorCode == 1264 || errorMessage.contains("Data truncation") || errorMessage.contains("too long")) {
                System.err.println("Data truncation error - password column may be too small. Error: " + errorMessage);
                System.err.println("Please run: ALTER TABLE UserInfo MODIFY COLUMN password VARCHAR(64) NOT NULL;");
                return false;
            }

            System.err.println("SQL Error creating carrier (Code: " + errorCode + "): " + errorMessage);
            return false;
        }
    }

    /**
     * Finds a user by their role.
     *
     * @param role role to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database query fails
     * @author Mert Bölükbaşı
     */
    public User findByRole(Role role) throws SQLException {
        String query = "SELECT * FROM UserInfo WHERE role = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, role.name().toLowerCase());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("userID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getInt("loyalty_points")
                );
            }
        }
        return null;
    }

    /**
     * Removes a carrier from the database.
     *
     * @param carrierId carrier user ID to remove
     * @return true if removal successful
     * @author Mert Bölükbaşı
     */
    public boolean removeCarrier(int carrierId) {
        String query = "DELETE FROM UserInfo WHERE userID = ? AND role = 'carrier'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, carrierId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId user ID to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database query fails
     * @author Mert Bölükbaşı
     */
    public User findById(int userId) throws SQLException {
        String query = "SELECT * FROM UserInfo WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr.toUpperCase());
                return new User(
                        rs.getInt("userID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getInt("loyalty_points")
                );
            }
        }
        return null;
    }
}