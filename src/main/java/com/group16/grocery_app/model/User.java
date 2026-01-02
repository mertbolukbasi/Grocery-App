package com.group16.grocery_app.model;

/**
 * Represents a user in the grocery application.
 * Stores user information including credentials, role, and loyalty points.
 *
 * @author Mert Bölükbaşı
 */
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

    /**
     * Creates a new user with all required information.
     *
     * @param id unique user identifier
     * @param username login username
     * @param password user password (hashed)
     * @param role user role (CUSTOMER, CARRIER, or OWNER)
     * @param firstName user's first name
     * @param lastName user's last name
     * @param address user's address
     * @param phoneNumber user's phone number
     * @param loyaltyPoints current loyalty points balance
     * @author Mert Bölükbaşı
     */
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

    /**
     * Gets the user's unique identifier.
     *
     * @return user ID
     * @author Mert Bölükbaşı
     */
    public int getId() { return id; }

    /**
     * Gets the username.
     *
     * @return username
     * @author Mert Bölükbaşı
     */
    public String getUsername() { return username; }

    /**
     * Gets the user's role.
     *
     * @return user role
     * @author Mert Bölükbaşı
     */
    public Role getRole() { return role; }

    /**
     * Gets the user's first name.
     *
     * @return first name
     * @author Mert Bölükbaşı
     */
    public String getFirstName() { return firstName; }

    /**
     * Gets the user's last name.
     *
     * @return last name
     * @author Mert Bölükbaşı
     */
    public String getLastName() { return lastName; }

    /**
     * Gets the user's address.
     *
     * @return address
     * @author Mert Bölükbaşı
     */
    public String getAddress() { return address; }

    /**
     * Gets the user's phone number.
     *
     * @return phone number
     * @author Mert Bölükbaşı
     */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * Gets the user's current loyalty points.
     *
     * @return loyalty points balance
     * @author Mert Bölükbaşı
     */
    public int getLoyaltyPoints() { return loyaltyPoints; }

    /**
     * Sets the user's address.
     *
     * @param address new address
     * @author Mert Bölükbaşı
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * Sets the user's phone number.
     *
     * @param phoneNumber new phone number
     * @author Mert Bölükbaşı
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
