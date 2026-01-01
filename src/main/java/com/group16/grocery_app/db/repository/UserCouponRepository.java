package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Coupon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCouponRepository {
    private final Connection connection;

    public UserCouponRepository() {
        this.connection = Database.getInstance().getConnection();
    }

    public ObservableList<Coupon> getUserCoupons(int userId) throws SQLException {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT c.* FROM Coupons c " +
                "INNER JOIN UserCoupons uc ON c.couponID = uc.couponID " +
                "WHERE uc.userID = ? AND uc.is_used = FALSE " +
                "AND c.is_active = TRUE AND c.expired_date >= CURDATE()";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date expiryDate = rs.getDate("expired_date");
                java.time.LocalDate expiry = expiryDate != null ? expiryDate.toLocalDate() : null;

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

    public Coupon getCouponByCode(String code) throws SQLException {
        String query = "SELECT * FROM Coupons WHERE code = ? AND is_active = TRUE AND expired_date >= CURDATE()";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date expiryDate = rs.getDate("expired_date");
                java.time.LocalDate expiry = expiryDate != null ? expiryDate.toLocalDate() : null;

                return new Coupon(
                        rs.getInt("couponID"),
                        rs.getString("code"),
                        rs.getDouble("discount_amount"),
                        expiry,
                        true
                );
            }
        }

        return null;
    }

    public boolean useCoupon(int userId, int couponId) throws SQLException {
        String query = "UPDATE UserCoupons SET is_used = TRUE WHERE userID = ? AND couponID = ? AND is_used = FALSE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, couponId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean hasUnusedCoupon(int userId, int couponId) throws SQLException {
        String query = "SELECT COUNT(*) FROM UserCoupons WHERE userID = ? AND couponID = ? AND is_used = FALSE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, couponId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
