package com.stockapp.services;

import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import com.stockapp.dao.UserDAO;
import com.stockapp.models.User;
import com.stockapp.models.UserRole;

@Service
public class AuthService {

    private UserDAO userDAO = new UserDAO();

    @PostConstruct
    public void init() {
        // Initialize database on startup
        try {
            com.stockapp.DatabaseConnection.getConnection();
            createDefaultAdminUser();
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Create default admin user if it doesn't exist
     */
    private void createDefaultAdminUser() {
        try {
            // Check if admin user exists
            User existingAdmin = userDAO.findUserByUsername("admin");
            if (existingAdmin == null) {
                // Create default admin user
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(hashPassword("admin123")); // Hash the password
                adminUser.setBalance(new java.math.BigDecimal("1000000.00"));
                adminUser.setRole(UserRole.ADMIN);
                adminUser.setActive(true);

                userDAO.registerUser(adminUser);
                System.out.println("Default admin user created: admin/admin123");
            }

            // Check if regular user exists
            User existingUser = userDAO.findUserByUsername("user");
            if (existingUser == null) {
                // Create default regular user
                User regularUser = new User();
                regularUser.setUsername("user");
                regularUser.setPassword(hashPassword("user123"));
                regularUser.setBalance(new java.math.BigDecimal("100000.00"));
                regularUser.setRole(UserRole.USER);
                regularUser.setActive(true);

                userDAO.registerUser(regularUser);
                System.out.println("Default user created: user/user123");
            }
        } catch (Exception e) {
            System.err.println("Failed to create default users: " + e.getMessage());
        }
    }

    /**
     * Hash a password using SHA-256
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verify a password against its hash or plaintext
     * Supports both plaintext (legacy) and hashed passwords
     */
    public boolean verifyPassword(String password, String storedPassword) {
        try {
            // First try direct plaintext comparison (current DB schema)
            if (password.equals(storedPassword)) {
                return true;
            }

            // Fallback to hash comparison for future compatibility
            String passwordHash = hashPassword(password);
            return passwordHash.equals(storedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate a simple JWT token (in production, use proper JWT library)
     */
    public String generateToken(User user) {
        try {
            // Create a simple token with user info
            String payload = user.getId() + ":" + user.getUsername() + ":" + user.getRole() + ":"
                    + System.currentTimeMillis();
            String encoded = Base64.getEncoder().encodeToString(payload.getBytes());
            return encoded;
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    /**
     * Verify and decode a token
     */
    public boolean verifyToken(String token) {
        try {
            Base64.getDecoder().decode(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get User from Token
     */
    public User getUserFromToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }

            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");

            if (parts.length < 2) {
                return null;
            }

            int userId = Integer.parseInt(parts[0]);
            return userDAO.getUserById(userId);

        } catch (Exception e) {
            return null;
        }
    }
}
