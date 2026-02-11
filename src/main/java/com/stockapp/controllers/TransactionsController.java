package com.stockapp.controllers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockapp.services.AuthService;

import com.stockapp.dao.PortfolioDAO;
import com.stockapp.dao.StockDAO;
import com.stockapp.dao.TransactionDAO;
import com.stockapp.dao.UserActivityLogDAO;
import com.stockapp.dao.UserDAO;
import com.stockapp.models.Portfolio;
import com.stockapp.models.Stock;
import com.stockapp.models.Transaction;
import com.stockapp.models.TransactionStatus;
import com.stockapp.models.User;
import com.stockapp.models.UserActivityLog;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionsController {

    private TransactionDAO transactionDAO = new TransactionDAO();

    private UserDAO userDAO = new UserDAO();

    private StockDAO stockDAO = new StockDAO();

    private PortfolioDAO portfolioDAO = new PortfolioDAO();

    private UserActivityLogDAO activityLogDAO = new UserActivityLogDAO();

    @Autowired
    private AuthService authService;

    /**
     * Get Recent Transactions for User
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentTransactions(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.getUserFromToken(authHeader.substring(7));

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            List<Transaction> transactions = transactionDAO.getUserTransactions(user.getId());

            List<Map<String, Object>> result = new ArrayList<>();
            for (Transaction t : transactions.stream().limit(10).toList()) {
                Stock stock = stockDAO.getStockById(t.getStockId());
                Map<String, Object> item = new HashMap<>();
                item.put("id", t.getId());
                item.put("stockSymbol", stock.getSymbol());
                item.put("type", t.getType());
                item.put("quantity", t.getQuantity());
                item.put("price", t.getPrice().doubleValue());
                item.put("status", t.getStatus());
                item.put("createdAt", t.getCreatedAt());
                result.add(item);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Get All Transactions for User
     */
    @GetMapping("/list")
    public ResponseEntity<?> getUserTransactions(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.getUserFromToken(authHeader.substring(7));

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            List<Transaction> transactions = transactionDAO.getUserTransactions(user.getId());

            List<Map<String, Object>> result = new ArrayList<>();
            for (Transaction t : transactions) {
                Stock stock = stockDAO.getStockById(t.getStockId());
                Map<String, Object> item = new HashMap<>();
                item.put("id", t.getId());
                item.put("stockSymbol", stock.getSymbol());
                item.put("type", t.getType());
                item.put("quantity", t.getQuantity());
                item.put("price", t.getPrice().doubleValue());
                item.put("total", t.getPrice().multiply(BigDecimal.valueOf(t.getQuantity())).doubleValue());
                item.put("status", t.getStatus().toString());
                item.put("timestamp", t.getCreatedAt());
                result.add(item);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Buy Stock
     */
    @PostMapping("/buy")
    public ResponseEntity<?> buyStock(@RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.getUserFromToken(authHeader.substring(7));

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            int stockId = ((Number) request.get("stockId")).intValue();
            int quantity = ((Number) request.get("quantity")).intValue();

            Stock stock = stockDAO.getStockById(stockId);

            if (stock.isSuspended()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Stock is suspended"));
            }

            BigDecimal totalCost = stock.getPrice().multiply(new BigDecimal(quantity));

            if (user.getBalance().compareTo(totalCost) < 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Insufficient balance"));
            }

            // Deduct from balance
            user.setBalance(user.getBalance().subtract(totalCost));
            userDAO.updateUser(user);

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setUserId(user.getId());
            transaction.setStockId(stockId);
            transaction.setQuantity(quantity);
            transaction.setPrice(stock.getPrice());
            transaction.setType("BUY");
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            int transactionId = transactionDAO.createTransaction(transaction);

            // Update portfolio
            Portfolio portfolio = portfolioDAO.getPortfolioItem(user.getId(), stockId);
            if (portfolio == null) {
                // Create new portfolio item with current stock price as average price
                portfolio = new Portfolio(user.getId(), stockId, quantity, 0, stock.getPrice().doubleValue());
                portfolioDAO.createPortfolioItem(portfolio);
            } else {
                // Recalculate weighted average price
                double currentTotalValue = portfolio.getAvgPrice() * portfolio.getQuantity();
                double newTotalValue = stock.getPrice().doubleValue() * quantity;
                double totalQuantity = portfolio.getQuantity() + quantity;
                double newAvgPrice = (currentTotalValue + newTotalValue) / totalQuantity;
                portfolio.setAvgPrice(newAvgPrice);
                portfolio.setQuantity(portfolio.getQuantity() + quantity);
                portfolioDAO.updatePortfolioItem(portfolio);
            }

            // Adjust stock price based on supply/demand (buy increases price)
            BigDecimal priceChange = stock.getPrice().multiply(new BigDecimal("0.0001"))
                    .multiply(new BigDecimal(quantity));
            stock.setPrice(stock.getPrice().add(priceChange));
            stockDAO.updateStock(stock);

            // Log activity
            logActivity(user.getId(), "BUY_STOCK", "Bought " + quantity + " shares of " + stock.getSymbol());

            return ResponseEntity.ok(Map.of("success", true, "transactionId", transactionId));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Sell Stock
     */
    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(@RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.getUserFromToken(authHeader.substring(7));

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            int stockId = ((Number) request.get("stockId")).intValue();
            int quantity = ((Number) request.get("quantity")).intValue();

            Stock stock = stockDAO.getStockById(stockId);

            if (stock.isSuspended()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Stock is suspended"));
            }

            Portfolio portfolio = portfolioDAO.getPortfolioItem(user.getId(), stockId);

            if (portfolio == null || portfolio.getQuantity() < quantity) {
                return ResponseEntity.badRequest().body(Map.of("message", "Insufficient shares"));
            }

            BigDecimal totalRevenue = stock.getPrice().multiply(new BigDecimal(quantity));

            // Add to balance
            user.setBalance(user.getBalance().add(totalRevenue));
            userDAO.updateUser(user);

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setUserId(user.getId());
            transaction.setStockId(stockId);
            transaction.setQuantity(quantity);
            transaction.setPrice(stock.getPrice());
            transaction.setType("SELL");
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            int transactionId = transactionDAO.createTransaction(transaction);

            // Update portfolio
            portfolio.setQuantity(portfolio.getQuantity() - quantity);
            if (portfolio.getQuantity() <= 0) {
                portfolioDAO.deletePortfolioItem(portfolio.getId());
            } else {
                portfolioDAO.updatePortfolioItem(portfolio);
            }

            // Adjust stock price based on supply/demand (sell decreases price)
            BigDecimal priceChange = stock.getPrice().multiply(new BigDecimal("0.0001"))
                    .multiply(new BigDecimal(quantity));
            stock.setPrice(stock.getPrice().subtract(priceChange));
            // Ensure price doesn't go below 0.01
            if (stock.getPrice().compareTo(new BigDecimal("0.01")) < 0) {
                stock.setPrice(new BigDecimal("0.01"));
            }
            stockDAO.updateStock(stock);

            // Log activity
            logActivity(user.getId(), "SELL_STOCK", "Sold " + quantity + " shares of " + stock.getSymbol());

            return ResponseEntity.ok(Map.of("success", true, "transactionId", transactionId));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ================== UTILITY METHODS ==================

    private void logActivity(int userId, String actionType, String details) {
        try {
            UserActivityLog log = new UserActivityLog();
            log.setUserId(userId);
            log.setActionType(actionType);
            log.setDetails(details);
            log.setLoggedAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
            activityLogDAO.createActivityLog(log);
        } catch (Exception e) {
            // Silently fail logging
        }
    }
}
