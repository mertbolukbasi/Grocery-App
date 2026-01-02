package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Coupon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for coupon database operations.
 * Handles CRUD operations for coupons in the database.
 *
 * @author Ege Usug
 */
public class CouponRepository {
    private final Connection connection;

    /**
     * Initializes the repository with a database connection.
     *
     * @author Ege Usug
     */
    public CouponRepository() {
        this.connection = Database.getInstance().getConnection();
    }

    /**
     * Retrieves all coupons from the database.
     *
     * @return ObservableList of all coupons
     * @throws SQLException If a database error occurs
     * @author Ege Usug
     */
    public ObservableList<Coupon> findAll() throws SQLException {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT * FROM Coupons ORDER BY code";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Date expiryDate = rs.getDate("expired_date");
                LocalDate expiry = expiryDate != null ? expiryDate.toLocalDate() : null;

                coupons.add(new Coupon(
                        rs.getInt("couponID"),
                        rs.getString("code"),
                        rs.getDouble("discount_amount"),
                        expiry,
                        rs.getBoolean("is_active")
                ));
            }
        }

        return FXCollections.observableArrayList(coupons);
    }

    /**
     * Adds a new coupon to the database.
     *
     * @param code The coupon code
     * @param discountAmount The discount amount
     * @param expiryDate The expiry date of the coupon
     * @return true if the coupon was added successfully, false otherwise
     * @throws SQLException If a database error occurs
     * @author Ege Usug
     */
    public boolean addCoupon(String code, double discountAmount, LocalDate expiryDate) throws SQLException {
        String query = "INSERT INTO Coupons (code, discount_amount, expired_date, is_active) VALUES (?, ?, ?, TRUE)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, code);
            stmt.setDouble(2, discountAmount);
            stmt.setDate(3, java.sql.Date.valueOf(expiryDate));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Updates an existing coupon in the database.
     *
     * @param couponId The ID of the coupon to update
     * @param code The new coupon code
     * @param discountAmount The new discount amount
     * @param expiryDate The new expiry date
     * @param isActive Whether the coupon is active
     * @return true if the coupon was updated successfully, false otherwise
     * @throws SQLException If a database error occurs
     * @author Ege Usug
     */
    public boolean updateCoupon(int couponId, String code, double discountAmount, LocalDate expiryDate, boolean isActive) throws SQLException {
        String query = "UPDATE Coupons SET code = ?, discount_amount = ?, expired_date = ?, is_active = ? WHERE couponID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, code);
            stmt.setDouble(2, discountAmount);
            stmt.setDate(3, java.sql.Date.valueOf(expiryDate));
            stmt.setBoolean(4, isActive);
            stmt.setInt(5, couponId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves all active and non-expired coupons from the database.
     *
     * @return ObservableList of active coupons
     * @throws SQLException If a database error occurs
     * @author Ege Usug
     */
    public ObservableList<Coupon> getActiveCoupons() throws SQLException {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT * FROM Coupons WHERE is_active = TRUE AND expired_date >= CURDATE() ORDER BY code";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Date expiryDate = rs.getDate("expired_date");
                LocalDate expiry = expiryDate != null ? expiryDate.toLocalDate() : null;

                coupons.add(new Coupon(
                        rs.getInt("couponID"),
                        rs.getString("code"),
                        rs.getDouble("discount_amount"),
                        expiry,
                        true
                ));
            }
        }

        return FXCollections.observableArrayList(coupons);
    }

    /**
     * Deletes a coupon from the database.
     *
     * @param couponId The ID of the coupon to delete
     * @return true if the coupon was deleted successfully, false otherwise
     * @throws SQLException If a database error occurs
     * @author Ege Usug
     */
    public boolean deleteCoupon(int couponId) throws SQLException {
        String query = "DELETE FROM Coupons WHERE couponID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, couponId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
