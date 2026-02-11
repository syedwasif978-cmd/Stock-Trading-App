package com.stockapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Standard utility class for managing the connection to the MySQL database.
 * This class provides a static method to obtain a database Connection object,
 * which is required by all DAO classes (like UserDAO).
 */
public class DatabaseConnection {

    // --- 1. JDBC Driver Class ---
    // Try H2 first (for development), fallback to MySQL
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    // --- 2. Connection Details ---
    // H2 in-memory database for development
    private static final String H2_URL = "jdbc:h2:mem:stockapp;DB_CLOSE_DELAY=-1";
    private static final String H2_USER = "sa";
    private static final String H2_PASS = "";

    // MySQL for production
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/stockapp?serverTimezone=UTC";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "strongpassword";

    /**
     * Attempts to establish a connection to the database.
     * Tries MySQL first (production), then H2 as fallback.
     * 
     * @return A valid Connection object, or null if connection fails.
     */
    public static Connection getConnection() {
        Connection connection = null;

        // Try MySQL first (production)
        try {
            Class.forName(MYSQL_DRIVER);
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
            if (connection != null) {
                System.out.println("Connected to MySQL database");
                return connection;
            }
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL ERROR: MySQL JDBC Driver not found. Check your classpath!");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("ERROR: MySQL connection failed, trying H2...");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Make sure MySQL is running and the database exists.");
        }

        // Try H2 as fallback (development)
        try {
            Class.forName(H2_DRIVER);
            connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASS);
            if (connection != null) {
                System.out.println("Connected to H2 database (development mode)");
                initializeH2Database(connection);
                return connection;
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("H2 not available.");
        }

        return null;
    }

    /**
     * Initialize H2 database with schema and sample data
     */
    private static void initializeH2Database(Connection connection) {
        try {
            System.out.println("Initializing H2 database...");

            // Execute initialization SQL
            String initSql = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) UNIQUE NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        balance DECIMAL(15, 2) DEFAULT 100000.00 NOT NULL,
                        role VARCHAR(10) DEFAULT 'USER' NOT NULL,
                        is_active BOOLEAN DEFAULT TRUE NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );

                    CREATE TABLE IF NOT EXISTS stocks (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        symbol VARCHAR(10) UNIQUE NOT NULL,
                        name VARCHAR(100) NOT NULL,
                        price DECIMAL(15, 2) NOT NULL,
                        change_percent DOUBLE DEFAULT 0.0 NOT NULL,
                        is_suspended BOOLEAN DEFAULT FALSE NOT NULL,
                        suspension_reason VARCHAR(255),
                        suspended_at TIMESTAMP NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );

                    CREATE TABLE IF NOT EXISTS portfolios (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        stock_id INT NOT NULL,
                        quantity INT NOT NULL,
                        average_price DECIMAL(15, 4) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (stock_id) REFERENCES stocks(id),
                        UNIQUE (user_id, stock_id)
                    );

                    CREATE TABLE IF NOT EXISTS transactions (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        stock_id INT NOT NULL,
                        quantity INT NOT NULL,
                        price DECIMAL(15, 2) NOT NULL,
                        transaction_type VARCHAR(4) NOT NULL,
                        status VARCHAR(10) DEFAULT 'PENDING' NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        completed_at TIMESTAMP NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (stock_id) REFERENCES stocks(id)
                    );

                    CREATE TABLE IF NOT EXISTS user_activity_logs (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        activity_type VARCHAR(50) NOT NULL,
                        description VARCHAR(255),
                        ip_address VARCHAR(45),
                        logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    );

                    INSERT INTO users (username, password, role, is_active, balance) SELECT 'admin', 'admin123', 'ADMIN', TRUE, 1000000.00 WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
                    INSERT INTO users (username, password, role, is_active, balance) SELECT 'user', 'user123', 'USER', TRUE, 100000.00 WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'user');
                    INSERT INTO stocks (symbol, name, price, is_suspended) SELECT 'UITU', 'UIT UNI STOCK', 150.00, FALSE WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE symbol = 'UITU');
                    INSERT INTO stocks (symbol, name, price, is_suspended) SELECT 'AAPL', 'Apple Inc.', 180.50, FALSE WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE symbol = 'AAPL');
                    INSERT INTO stocks (symbol, name, price, is_suspended) SELECT 'GOOGL', 'Alphabet Inc.', 2800.00, FALSE WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE symbol = 'GOOGL');
                    INSERT INTO stocks (symbol, name, price, is_suspended) SELECT 'MSFT', 'Microsoft Corp.', 380.00, FALSE WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE symbol = 'MSFT');
                    """;

            try (var stmt = connection.createStatement()) {
                // Split SQL by semicolon and execute each statement
                String[] statements = initSql.split(";");
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                        System.out.println("Executed: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    }
                }
                System.out.println("H2 database initialized with sample data");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing H2 database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
