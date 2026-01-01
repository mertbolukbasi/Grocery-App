package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.UserCouponRepository;
import com.group16.grocery_app.model.Coupon;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class UserCouponService {
    private final UserCouponRepository userCouponRepository;

    public UserCouponService() {
        this.userCouponRepository = new UserCouponRepository();
    }

    public ObservableList<Coupon> getUserCoupons(int userId) {
        try {
            return userCouponRepository.getUserCoupons(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    public Coupon getCouponByCode(String code) {
        try {
            return userCouponRepository.getCouponByCode(code);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean useCoupon(int userId, int couponId) {
        try {
            return userCouponRepository.useCoupon(userId, couponId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasUnusedCoupon(int userId, int couponId) {
        try {
            return userCouponRepository.hasUnusedCoupon(userId, couponId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
