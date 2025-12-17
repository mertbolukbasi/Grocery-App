package com.group16.grocery_app.db;

import java.sql.*;

public class Database {

    /**
     * Database URL.
     */
    private final String DB_URL = "jdbc:mysql://localhost:3306/Group16";

    /**
     * Database Username.
     */
    private final String DB_USER = "myuser";

    /**
     * Database Password.
     */
    private final String DB_PASSWORD = "1234";

    /**
     * Database object instance to use singleton pattern.
     */
    private static Database instance;

    /**
     * Database connection object.
     */
    private Connection connection;

    /**
     * Database object constructor. It creates database connection.
     */
    private Database() {
        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection is successful!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    /**
     * Returns database singleton object.
     * @return Database object instance.
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
