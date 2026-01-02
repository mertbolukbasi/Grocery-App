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
 * Repository class for coupon-related database operations.
 * Handles CRUD operations and queries for active/valid coupons.
 *
 * @author Yiğit Emre Ünlüçerçi
 */
public class CouponRepository {
    private final Connection connection;

    /**
     * Creates a new CouponRepository instance and initializes the database connection.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    public CouponRepository() {
        this.connection = Database.getInstance().getConnection();
    }

    /**
     * Retrieves all coupons from the database ordered by coupon code.
     *
     * @return observable list of all coupons
     * @throws SQLException if a database access error occurs
     * @author Yiğit Emre Ünlüçerçi
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
     * Inserts a new coupon into the database.
     * Newly created coupons are marked as active by default.
     *
     * @param code coupon code
     * @param discountAmount discount amount of the coupon
     * @param expiryDate expiry date of the coupon
     * @return true if the coupon is inserted successfully, false otherwise
     * @throws SQLException if a database access error occurs
     * @author Yiğit Emre Ünlüçerçi
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
     * @param couponId the coupon ID to update
     * @param code updated coupon code
     * @param discountAmount updated discount amount
     * @param expiryDate updated expiry date
     * @param isActive updated active status
     * @return true if the update is successful, false otherwise
     * @throws SQLException if a database access error occurs
     * @author Yiğit Emre Ünlüçerçi
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
     * Retrieves active and non-expired coupons from the database.
     * A coupon is considered valid if it is active and its expiry date is not before today.
     *
     * @return observable list of active and valid coupons
     * @throws SQLException if a database access error occurs
     * @author Yiğit Emre Ünlüçerçi
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
     * Deletes a coupon from the database by its ID.
     *
     * @param couponId the coupon ID to delete
     * @return true if the coupon is deleted successfully, false otherwise
     * @throws SQLException if a database access error occurs
     * @author Yiğit Emre Ünlüçerçi
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
