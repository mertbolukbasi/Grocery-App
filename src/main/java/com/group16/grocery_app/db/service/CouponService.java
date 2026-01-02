package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.CouponRepository;
import com.group16.grocery_app.model.Coupon;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Service class for coupon-related operations.
 * Provides a higher-level API over {@link CouponRepository} and handles exceptions gracefully.
 *
 * @author Yiğit Emre Ünlüçerçi
 */
public class CouponService {
    private final CouponRepository couponRepository;

    /**
     * Creates a new CouponService instance and initializes its repository dependency.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    public CouponService() {
        this.couponRepository = new CouponRepository();
    }

    /**
     * Retrieves all coupons from the database.
     *
     * @return observable list of all coupons; returns an empty list if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public ObservableList<Coupon> getAllCoupons() {
        try {
            return couponRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    /**
     * Adds a new coupon to the database.
     *
     * @param code coupon code
     * @param discountAmount discount amount of the coupon
     * @param expiryDate expiry date of the coupon
     * @return true if the coupon is added successfully; false if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public boolean addCoupon(String code, double discountAmount, LocalDate expiryDate) {
        try {
            return couponRepository.addCoupon(code, discountAmount, expiryDate);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
     * @return true if the coupon is updated successfully; false if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public boolean updateCoupon(int couponId, String code, double discountAmount, LocalDate expiryDate, boolean isActive) {
        try {
            return couponRepository.updateCoupon(couponId, code, discountAmount, expiryDate, isActive);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves active and non-expired coupons from the database.
     *
     * @return observable list of active and valid coupons; returns an empty list if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public ObservableList<Coupon> getActiveCoupons() {
        try {
            return couponRepository.getActiveCoupons();
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    /**
     * Deletes a coupon from the database by its ID.
     *
     * @param couponId the coupon ID to delete
     * @return true if the coupon is deleted successfully; false if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public boolean deleteCoupon(int couponId) {
        try {
            return couponRepository.deleteCoupon(couponId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
