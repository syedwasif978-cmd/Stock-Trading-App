package com.stockapp.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockapp.services.AuthService;

import com.stockapp.dao.PortfolioDAO;
import com.stockapp.dao.StockDAO;
import com.stockapp.dao.TransactionDAO;
import com.stockapp.dao.UserDAO;
import com.stockapp.models.Portfolio;
import com.stockapp.models.Stock;
import com.stockapp.models.Transaction;
import com.stockapp.models.TransactionStatus;
import com.stockapp.models.User;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private UserDAO userDAO = new UserDAO();

    private StockDAO stockDAO = new StockDAO();

    private TransactionDAO transactionDAO = new TransactionDAO();

    private PortfolioDAO portfolioDAO = new PortfolioDAO();

    @Autowired
    private AuthService authService;

    /**
     * Get Dashboard Summary
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.getUserFromToken(authHeader.substring(7));

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            Map<String, Object> summary = new HashMap<>();

            // Get balance
            summary.put("totalBalance", user.getBalance());

            // Get portfolio value
            List<Portfolio> portfolio = portfolioDAO.getUserPortfolio(user.getId());
            BigDecimal portfolioValue = calculatePortfolioValue(portfolio);
            summary.put("portfolioValue", portfolioValue);

            // Get profit/loss
            BigDecimal profitLoss = portfolioValue.add(calculateCompletedTransactionValue(user.getId()))
                    .subtract(user.getBalance());
            summary.put("profitLoss", profitLoss);

            // Get transaction count
            summary.put("totalTransactions", transactionDAO.getUserTransactionCount(user.getId()));

            // Get portfolio distribution
            List<Map<String, Object>> portfolioData = new ArrayList<>();
            for (Portfolio p : portfolio) {
                Stock stock = stockDAO.getStockById(p.getStockID());
                BigDecimal currentPrice = stock.getPrice();
                BigDecimal currentValue = currentPrice.multiply(new BigDecimal(p.getQuantity()));
                BigDecimal costBasis = new BigDecimal(p.getAvgPrice()).multiply(new BigDecimal(p.getQuantity()));
                BigDecimal itemProfitLoss = currentValue.subtract(costBasis);

                Map<String, Object> item = new HashMap<>();
                item.put("symbol", stock.getSymbol());
                item.put("quantity", p.getQuantity());
                item.put("averagePrice", p.getAvgPrice());
                item.put("currentPrice", currentPrice.doubleValue());
                item.put("totalValue", currentValue.doubleValue());
                item.put("profitLoss", itemProfitLoss.doubleValue());
                item.put("stockId", p.getStockID());
                portfolioData.add(item);
            }
            summary.put("portfolio", portfolioData);

            // Get stock prices (for chart)
            List<Stock> allStocks = stockDAO.getAllStocks();
            List<Map<String, Object>> stockPrices = new ArrayList<>();
            for (Stock stock : allStocks.stream().limit(5).toList()) {
                Map<String, Object> item = new HashMap<>();
                item.put("symbol", stock.getSymbol());
                item.put("price", stock.getPrice().doubleValue());
                stockPrices.add(item);
            }
            summary.put("stockPrices", stockPrices);

            // Get trading activity
            Map<String, Object> tradingActivity = new HashMap<>();
            List<String> dates = new ArrayList<>();
            List<Integer> buyOrders = new ArrayList<>();
            List<Integer> sellOrders = new ArrayList<>();

            for (int i = 6; i >= 0; i--) {
                dates.add("Day -" + i);
                buyOrders.add((int) (Math.random() * 10));
                sellOrders.add((int) (Math.random() * 10));
            }

            tradingActivity.put("dates", dates);
            tradingActivity.put("buyOrders", buyOrders);
            tradingActivity.put("sellOrders", sellOrders);
            summary.put("tradingActivity", tradingActivity);

            // Get balance history
            List<Map<String, Object>> balanceHistory = new ArrayList<>();
            BigDecimal currentBalance = user.getBalance();
            for (int i = 6; i >= 0; i--) {
                Map<String, Object> item = new HashMap<>();
                item.put("date", "Day -" + i);
                item.put("balance", currentBalance.subtract(new BigDecimal(i * 100)));
                balanceHistory.add(item);
            }
            summary.put("balanceHistory", balanceHistory);

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Get Recent Transactions
     */
    @GetMapping("/transactions/recent")
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

    // ================== UTILITY METHODS ==================

    private BigDecimal calculatePortfolioValue(List<Portfolio> portfolio) {
        BigDecimal total = BigDecimal.ZERO;
        for (Portfolio p : portfolio) {
            try {
                Stock stock = stockDAO.getStockById(p.getStockID());
                total = total.add(stock.getPrice().multiply(new BigDecimal(p.getQuantity())));
            } catch (Exception e) {
                // Skip on error
            }
        }
        return total;
    }

    private BigDecimal calculateCompletedTransactionValue(int userId) {
        try {
            List<Transaction> transactions = transactionDAO.getUserTransactions(userId);
            BigDecimal total = BigDecimal.ZERO;
            for (Transaction t : transactions) {
                if (t.getStatus() == TransactionStatus.COMPLETED) {
                    total = total.add(t.getPrice().multiply(new BigDecimal(t.getQuantity())));
                }
            }
            return total;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
