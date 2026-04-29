package com.lostandfound.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility
 * Provides a centralized method to obtain MySQL database connections.
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class DBConnection {

    // Database configuration
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD"); // railway online db connection
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Get a connection to the MySQL database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Close a database connection safely
     * @param connection the connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
