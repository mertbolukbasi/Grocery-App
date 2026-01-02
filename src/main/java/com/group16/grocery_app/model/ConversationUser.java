package com.group16.grocery_app.model;

public class ConversationUser {
    private String username;
    private int userId;

    /**
     * Creates a new ConversationUser instance.
     *
     * @param username the username
     * @param userId the user ID
     * @author Ege Usug
     */
    public ConversationUser(String username, int userId) {
        this.username = username;
        this.userId = userId;
    }

    /**
     * Gets the username.
     *
     * @return the username
     * @author Ege Usug
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     * @author Ege Usug
     */
    public int getUserId() {
        return userId;
    }
}
