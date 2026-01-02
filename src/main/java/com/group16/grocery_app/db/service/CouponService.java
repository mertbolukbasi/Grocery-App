package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.CouponRepository;
import com.group16.grocery_app.model.Coupon;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;

public class CouponService {
    private final CouponRepository couponRepository;

    public CouponService() {
        this.couponRepository = new CouponRepository();
    }

    public ObservableList<Coupon> getAllCoupons() {
        try {
            return couponRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    public boolean addCoupon(String code, double discountAmount, LocalDate expiryDate) {
        try {
            return couponRepository.addCoupon(code, discountAmount, expiryDate);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCoupon(int couponId, String code, double discountAmount, LocalDate expiryDate, boolean isActive) {
        try {
            return couponRepository.updateCoupon(couponId, code, discountAmount, expiryDate, isActive);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<Coupon> getActiveCoupons() {
        try {
            return couponRepository.getActiveCoupons();
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    public boolean deleteCoupon(int couponId) {
        try {
            return couponRepository.deleteCoupon(couponId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
