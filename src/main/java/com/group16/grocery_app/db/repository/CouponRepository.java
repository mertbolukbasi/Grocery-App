package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Coupon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CouponRepository {
    private final Connection connection;

    public CouponRepository() {
        this.connection = Database.getInstance().getConnection();
    }

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

    public boolean deleteCoupon(int couponId) throws SQLException {
        String query = "DELETE FROM Coupons WHERE couponID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, couponId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}

