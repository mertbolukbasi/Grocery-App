package com.group16.grocery_app.model;

public class User {
    private int id;
    private String username;
    private String password;
    private Role role;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private int loyaltyPoints;

    public User(int id, String username, String password, Role role, String firstName, String lastName, String address, String phoneNumber, int loyaltyPoints) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = loyaltyPoints;
    }


    public int getId() { return id; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
}
