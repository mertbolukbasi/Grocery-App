package com.group16.grocery_app.db;

import java.sql.*;

public class Database {

    private final String DB_URL = "jdbc:mysql://localhost:3306/Group16";

    private final String DB_USER = "myuser";

    private final String DB_PASSWORD = "1234";

    private static Database instance;

    private Connection connection;

    private Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection is successful!");
            
            // Verify connection is valid
            if (this.connection != null && !this.connection.isClosed()) {
                System.out.println("Database connection verified and active.");
            } else {
                System.err.println("Warning: Database connection is null or closed!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            System.err.println("Please ensure MySQL JDBC driver is in the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println("URL: " + DB_URL);
            System.err.println("User: " + DB_USER);
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please ensure:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Database 'Group16' exists");
            System.err.println("3. User credentials are correct");
            System.err.println("4. Run the Group16.sql script to set up the database");
            e.printStackTrace();
            this.connection = null;
        }
    }

    /**
     * Returns database singleton object.
     *
     * @return Database object instance.
     * @author Mert Bölükbaşı
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        if (connection == null) {
            System.err.println("Warning: getConnection() called but connection is null!");
            System.err.println("Database may not have been initialized properly.");
        }
        return connection;
    }
}
