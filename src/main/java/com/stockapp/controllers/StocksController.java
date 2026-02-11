package com.stockapp.controllers;

import com.stockapp.models.Stock;
import com.stockapp.dao.StockDAO;
import com.stockapp.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StocksController {

    private StockDAO stockDAO = new StockDAO();

    @Autowired
    private AuthService authService;

    /**
     * Get All Stocks
     * 
     * ALGORITHM DEMO: Supports query parameters to demonstrate sorting algorithms
     * 
     * Query Parameters:
     * - sort: "price", "symbol", "change" (uses Insertion Sort algorithm)
     * - order: "asc" or "desc" (for price and change sorting)
     * 
     * Examples:
     * - /api/stocks/all - Returns unsorted stocks
     * - /api/stocks/all?sort=price&order=desc - Sorted by price (expensive first)
     * - /api/stocks/all?sort=symbol - Sorted alphabetically by symbol
     * - /api/stocks/all?sort=change&order=desc - Top gainers first
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllStocks(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order) {
        try {
            // If sort parameter is provided, use INSERTION SORT algorithm
            if (sortBy != null) {
                boolean ascending = "asc".equalsIgnoreCase(order);

                switch (sortBy.toLowerCase()) {
                    case "price":
                        return ResponseEntity.ok(stockDAO.getAllStocksSortedByPrice(ascending));
                    case "symbol":
                        return ResponseEntity.ok(stockDAO.getAllStocksSortedBySymbol());
                    case "change":
                        return ResponseEntity.ok(stockDAO.getStocksSortedByChange(!ascending));
                    default:
                        return ResponseEntity.ok(stockDAO.getAllStocks());
                }
            }

            // Default: return unsorted stocks
            return ResponseEntity.ok(stockDAO.getAllStocks());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Get Stock by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStock(@PathVariable int id) {
        try {
            Stock stock = stockDAO.getStockById(id);

            if (stock == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Get Stock by Symbol
     * 
     * ALGORITHM DEMO: Add ?useAlgorithm=true to use LINEAR SEARCH instead of SQL
     * 
     * Examples:
     * - /api/stocks/symbol/AAPL - Uses direct SQL query
     * - /api/stocks/symbol/AAPL?useAlgorithm=true - Uses Linear Search algorithm
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<?> getStockBySymbol(
            @PathVariable String symbol,
            @RequestParam(value = "useAlgorithm", required = false, defaultValue = "false") boolean useAlgorithm) {
        try {
            Stock stock;

            if (useAlgorithm) {
                // ALGORITHM DEMO: Uses Linear Search
                stock = stockDAO.getStockBySymbolUsingLinearSearch(symbol);
            } else {
                // Standard SQL query
                stock = stockDAO.getStockBySymbol(symbol);
            }

            if (stock == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
