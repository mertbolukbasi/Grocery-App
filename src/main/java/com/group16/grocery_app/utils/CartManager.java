package com.group16.grocery_app.utils;

import com.group16.grocery_app.model.Cart;
import java.util.concurrent.ConcurrentHashMap;

public class CartManager {
    private static CartManager instance;
    private final ConcurrentHashMap<Integer, Cart> userCarts = new ConcurrentHashMap<>();

    private CartManager() {

    }

    public static CartManager getInstance() {
        if (instance == null) {
            synchronized (CartManager.class) {
                if (instance == null) {
                    instance = new CartManager();
                }
            }
        }
        return instance;
    }

    public Cart getCart(int userId) {
        return userCarts.computeIfAbsent(userId, k -> new Cart());
    }

    public void clearCart(int userId) {
        Cart cart = userCarts.remove(userId);
        if (cart != null) {
            cart.clear();
        }
    }

    public void removeCart(int userId) {
        userCarts.remove(userId);
    }

    public boolean hasCart(int userId) {
        return userCarts.containsKey(userId);
    }
}
