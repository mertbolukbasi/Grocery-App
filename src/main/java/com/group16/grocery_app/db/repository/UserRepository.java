package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Role;
import com.group16.grocery_app.model.User;
import com.group16.grocery_app.utils.PasswordHash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    private Connection connection;

    public UserRepository() {
        this.connection = Database.getInstance().getConnection();
    }

    public User findByUsername(String username) throws SQLException {
        String query = "SELECT * FROM UserInfo WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("userID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getObject("role", Role.class),
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

    public boolean checkUserForLogin(String username, String password) {
        String query = "SELECT userID FROM UserInfo WHERE username = ? AND password = ?";
        String hashPassword = PasswordHash.hash(password);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}
