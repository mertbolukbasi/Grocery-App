package com.group16.grocery_app.model;

public class ConversationUser {
    private String username;
    private int userId;

    public ConversationUser(String username, int userId) {
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }
}
