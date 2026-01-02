package com.group16.grocery_app.utils;

import com.group16.grocery_app.model.Cart;
import java.util.concurrent.ConcurrentHashMap;

public class CartManager {
    private static CartManager instance;
    private final ConcurrentHashMap<Integer, Cart> userCarts = new ConcurrentHashMap<>();

    private CartManager() {

    }

    /**
     * Gets the singleton instance of CartManager.
     *
     * @return the CartManager instance
     * @author Ege Usug
     */
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
     * Gets the cart for a specific user, creating one if it doesn't exist.
     *
     * @param userId the user ID
     * @return the cart for the user
     * @author Ege Usug
     */
    public Cart getCart(int userId) {
        return userCarts.computeIfAbsent(userId, k -> new Cart());
    }

    /**
     * Clears and removes the cart for a specific user.
     *
     * @param userId the user ID
     * @author Ege Usug
     */
    public void clearCart(int userId) {
        Cart cart = userCarts.remove(userId);
        if (cart != null) {
            cart.clear();
        }
    }

    /**
     * Removes the cart for a specific user without clearing it.
     *
     * @param userId the user ID
     * @author Ege Usug
     */
    public void removeCart(int userId) {
        userCarts.remove(userId);
    }

    /**
     * Checks if a user has a cart.
     *
     * @param userId the user ID
     * @return true if the user has a cart, false otherwise
     * @author Ege Usug
     */
    public boolean hasCart(int userId) {
        return userCarts.containsKey(userId);
    }
}
