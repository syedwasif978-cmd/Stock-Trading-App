package com.stockapp.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockapp.services.AuthService;

import com.stockapp.dao.StockDAO;
import com.stockapp.dao.TransactionDAO;
import com.stockapp.dao.UserActivityLogDAO;
import com.stockapp.dao.UserDAO;
import com.stockapp.models.Stock;
import com.stockapp.models.Transaction;
import com.stockapp.models.TransactionStatus;
import com.stockapp.models.User;
import com.stockapp.models.UserActivityLog;
import com.stockapp.models.UserRole;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private UserDAO userDAO = new UserDAO();

    private StockDAO stockDAO = new StockDAO();

    private TransactionDAO transactionDAO = new TransactionDAO();

    private UserActivityLogDAO activityLogDAO = new UserActivityLogDAO();

    @Autowired
    private AuthService authService;

    // ================== USER MANAGEMENT ==================

    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            String username = (String) request.get("username");
            String password = (String) request.get("password");
            Double initialBalance = ((Number) request.get("initialBalance")).doubleValue();
            String roleStr = (String) request.get("role");

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(authService.hashPassword(password));
            newUser.setBalance(new BigDecimal(initialBalance));
            newUser.setRole(UserRole.valueOf(roleStr));
            newUser.setActive(true);

            int userId = userDAO.createUser(newUser);

            // Log activity
            logActivity("ADMIN_ACTION", "Created new user: " + username);

            return ResponseEntity.ok(Map.of("success", true, "userId", userId));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/users/list")
    public ResponseEntity<?> listAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            return ResponseEntity.ok(userDAO.getAllUsers());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId, @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            User user = userDAO.getUserById(userId);
            userDAO.deleteUser(userId);

            logActivity("ADMIN_ACTION", "Deleted user: " + user.getUsername());

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/users/edit/{userId}")
    public ResponseEntity<?> editUser(@PathVariable int userId, @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            User user = userDAO.getUserById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
            }

            String username = (String) request.get("username");
            String password = (String) request.get("password");
            Double balance = ((Number) request.get("balance")).doubleValue();
            String roleStr = (String) request.get("role");
            Boolean isActive = (Boolean) request.get("isActive");

            if (username != null)
                user.setUsername(username);
            if (password != null && !password.isEmpty())
                user.setPassword(authService.hashPassword(password));
            if (balance != null)
                user.setBalance(new BigDecimal(balance));
            if (roleStr != null)
                user.setRole(UserRole.valueOf(roleStr));
            if (isActive != null)
                user.setActive(isActive);

            userDAO.updateUser(user);

            logActivity("ADMIN_ACTION", "Edited user: " + user.getUsername());

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * ALGORITHM DEMO: Search User by ID using BINARY SEARCH
     * 
     * This endpoint is specifically for demonstrating the Binary Search algorithm.
     * It fetches all users, sorts them by ID, and then applies binary search.
     */
    @GetMapping("/users/search/{userId}")
    public ResponseEntity<?> searchUserById(@PathVariable int userId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            // ALGORITHM DEMO: Uses Binary Search in UserDAO
            User user = userDAO.getUserByIdUsingBinarySearch(userId);

            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("message", "User not found using Binary Search"));
            }

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ================== STOCK MANAGEMENT ==================

    @PostMapping("/stocks/fluctuate")
    public ResponseEntity<?> fluctuateStock(@RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            int stockId = ((Number) request.get("stockId")).intValue();
            Double newPrice = ((Number) request.get("newPrice")).doubleValue();
            String reason = (String) request.get("reason");

            Stock stock = stockDAO.getStockById(stockId);
            BigDecimal oldPrice = stock.getPrice();
            BigDecimal newPriceBD = new BigDecimal(newPrice);

            // Calculate change percent
            double changePercent = 0.0;
            if (oldPrice.compareTo(BigDecimal.ZERO) > 0) {
                changePercent = newPriceBD.subtract(oldPrice).divide(oldPrice, 4, BigDecimal.ROUND_HALF_UP)
                        .doubleValue() * 100.0;
            }

            stock.setPrice(newPriceBD);
            stock.setChangePercent(changePercent);
            stockDAO.updateStock(stock);

            logActivity("ADMIN_ACTION", "Updated stock price for " + stock.getSymbol() +
                    " from " + oldPrice + " to " + newPrice + ". Reason: " + reason);

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/stocks/suspend")
    public ResponseEntity<?> toggleStockSuspension(@RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            int stockId = ((Number) request.get("stockId")).intValue();
            boolean suspend = (boolean) request.get("suspend");

            Stock stock = stockDAO.getStockById(stockId);
            stock.setSuspended(suspend);
            stockDAO.updateStock(stock);

            logActivity("ADMIN_ACTION", (suspend ? "Suspended" : "Resumed") + " stock: " + stock.getSymbol());

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ================== TRANSACTION MANAGEMENT ==================

    @GetMapping("/transactions/all")
    public ResponseEntity<?> getAllTransactions(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            List<Transaction> transactions = transactionDAO.getAllTransactions();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Transaction t : transactions) {
                User user = userDAO.getUserById(t.getUserId());
                Stock stock = stockDAO.getStockById(t.getStockId());

                Map<String, Object> item = new HashMap<>();
                item.put("id", t.getId());
                item.put("userId", t.getUserId());
                item.put("username", user.getUsername());
                item.put("stockId", t.getStockId());
                item.put("stockSymbol", stock.getSymbol());
                item.put("quantity", t.getQuantity());
                item.put("price", t.getPrice().doubleValue());
                item.put("createdAt", t.getCreatedAt());
                item.put("type", t.getType());
                item.put("status", t.getStatus());
                result.add(item);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/transactions/rollback/{transactionId}")
    public ResponseEntity<?> rollbackTransaction(@PathVariable int transactionId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            Transaction transaction = transactionDAO.getTransactionById(transactionId);

            if (transaction == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Transaction not found"));
            }

            // Update transaction status
            transaction.setStatus(TransactionStatus.CANCELLED);
            transactionDAO.updateTransaction(transaction);

            // Return funds to user
            User user = userDAO.getUserById(transaction.getUserId());
            BigDecimal compensationAmount = transaction.getPrice().multiply(new BigDecimal(transaction.getQuantity()));
            user.setBalance(user.getBalance().add(compensationAmount));
            userDAO.updateUser(user);

            logActivity("ADMIN_ACTION",
                    "Rolled back transaction #" + transactionId + " for user " + user.getUsername() +
                            ". Compensation: " + compensationAmount);

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ================== ACTIVITY LOGS ==================

    @GetMapping("/activities/all")
    public ResponseEntity<?> getAllActivities(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            List<UserActivityLog> logs = activityLogDAO.getAllActivities();
            List<Map<String, Object>> result = new ArrayList<>();

            for (UserActivityLog log : logs) {
                User user = userDAO.getUserById(log.getUserId());
                Map<String, Object> item = new HashMap<>();
                item.put("id", log.getId());
                item.put("userId", log.getUserId());
                item.put("username", user != null ? user.getUsername() : "Unknown");
                item.put("actionType", log.getActionType());
                item.put("details", log.getDetails());
                item.put("loggedAt", log.getTimestamp());
                result.add(item);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/activities/search")
    public ResponseEntity<?> searchActivities(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String actionType,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }

            List<UserActivityLog> logs = activityLogDAO.searchActivities(username, actionType);
            List<Map<String, Object>> result = new ArrayList<>();

            for (UserActivityLog log : logs) {
                User user = userDAO.getUserById(log.getUserId());
                Map<String, Object> item = new HashMap<>();
                item.put("id", log.getId());
                item.put("userId", log.getUserId());
                item.put("username", user != null ? user.getUsername() : "Unknown");
                item.put("actionType", log.getActionType());
                item.put("details", log.getDetails());
                item.put("loggedAt", log.getTimestamp());
                result.add(item);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ================== UTILITY METHODS ==================

    private boolean isAdmin(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return false;
            }
            String token = authHeader.substring(7);
            User user = authService.getUserFromToken(token);
            return user != null && user.getRole() == UserRole.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }

    private void logActivity(String actionType, String details) {
        try {
            UserActivityLog log = new UserActivityLog();
            log.setActionType(actionType);
            log.setDetails(details);
            log.setLoggedAt(java.time.LocalDateTime.now());
            activityLogDAO.createActivityLog(log);
        } catch (Exception e) {
            // Silently fail logging
        }
    }
}
