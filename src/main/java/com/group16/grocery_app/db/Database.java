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
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
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
        return connection;
    }
}
