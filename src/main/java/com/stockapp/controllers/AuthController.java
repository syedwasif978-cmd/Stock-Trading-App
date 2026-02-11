package com.stockapp.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockapp.services.AuthService;

import com.stockapp.dao.UserDAO;
import com.stockapp.models.User;
import com.stockapp.models.UserRole;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private UserDAO userDAO = new UserDAO();

    @Autowired
    private AuthService authService;

    /**
     * User Login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            System.out.println("Login attempt for username: " + username);

            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                System.out.println("User not found: " + username);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "User not found");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (!authService.verifyPassword(password, user.getPassword())) {
                System.out.println("Invalid password for: " + username);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid password");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (!user.isActive()) {
                System.out.println("Account inactive for: " + username);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Account is inactive");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Generate token (simple JWT simulation)
            String token = authService.generateToken(user);

            System.out.println("Login successful for: " + username);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Admin Login
     */
    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            User user = userDAO.getUserByUsername(username);

            if (user == null || user.getRole() != UserRole.ADMIN) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Admin user not found");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (!authService.verifyPassword(password, user.getPassword())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid password");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Generate token
            String token = authService.generateToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Admin login failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Initialize Admin User (for development)
     */
    @GetMapping("/init-admin")
    public ResponseEntity<?> initAdmin() {
        try {
            // Create default admin user if it doesn't exist
            User existingAdmin = userDAO.findUserByUsername("admin");
            if (existingAdmin == null) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(authService.hashPassword("admin123"));
                adminUser.setBalance(new java.math.BigDecimal("1000000.00"));
                adminUser.setRole(UserRole.ADMIN);
                adminUser.setActive(true);

                userDAO.registerUser(adminUser);
                return ResponseEntity.ok(Map.of("message", "Admin user created: admin/admin123"));
            } else {
                return ResponseEntity.ok(Map.of("message", "Admin user already exists"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * User Registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required"));
            }

            // Check if user exists
            User existing = userDAO.findUserByUsername(username);
            if (existing != null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
            }

            User newUser = new User();
            newUser.setUsername(username);
            // For now store plaintext (legacy) - recommend hashing for production
            newUser.setPassword(password);
            newUser.setRole(UserRole.USER);
            newUser.setBalance(new java.math.BigDecimal("100000.00"));
            newUser.setActive(true);

            boolean created = userDAO.registerUser(newUser);
            if (!created) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Failed to create user"));
            }

            // Remove password from response for safety
            newUser.setPassword(null);

            return ResponseEntity.ok(Map.of("message", "User created", "user", newUser));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
}
