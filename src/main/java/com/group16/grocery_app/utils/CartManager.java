package com.group16.grocery_app.utils;

import com.group16.grocery_app.model.Cart;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages cart instances per user session to ensure cart state persists across controller recreations.
 * This prevents cart loss when navigating between views.
 */
public class CartManager {
    private static CartManager instance;
    private final ConcurrentHashMap<Integer, Cart> userCarts = new ConcurrentHashMap<>();

    private CartManager() {
        // Private constructor for singleton pattern
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

    /**
     * Gets the cart for a specific user. Creates a new cart if one doesn't exist.
     * @param userId The user ID
     * @return The cart for the user
     */
    public Cart getCart(int userId) {
        return userCarts.computeIfAbsent(userId, k -> new Cart());
    }

    /**
     * Removes the cart for a user (e.g., on logout or checkout).
     * @param userId The user ID
     */
    public void clearCart(int userId) {
        Cart cart = userCarts.remove(userId);
        if (cart != null) {
            cart.clear();
        }
    }

    /**
     * Removes the cart for a user without clearing it first (e.g., on checkout after order is placed).
     * @param userId The user ID
     */
    public void removeCart(int userId) {
        userCarts.remove(userId);
    }

    /**
     * Checks if a user has an active cart.
     * @param userId The user ID
     * @return true if the user has a cart, false otherwise
     */
    public boolean hasCart(int userId) {
        return userCarts.containsKey(userId);
    }
}
