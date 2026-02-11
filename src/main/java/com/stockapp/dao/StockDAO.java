package com.stockapp.dao;

import com.stockapp.models.Stock;
import com.stockapp.DatabaseConnection;
import com.stockapp.algorithms.LinearSearch;
import com.stockapp.algorithms.InsertionSort;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Data Access Object for managing Stock market data.
 * Handles database operations related to the 'stocks' table.
 */
@Repository
public class StockDAO {

    /**
     * @return A list of Stock objects with fetched or simulated change percent.
     */
    public List<Stock> getAllStocks() {
        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT id, symbol, name, price, change_percent, is_suspended FROM stocks";

        // Generate a random seed based on the current day for stable simulation
        long seed = System.currentTimeMillis() / (1000 * 60 * 60 * 24);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            Random random = new Random(seed);

            while (rs.next()) {
                int id = rs.getInt("id");
                String symbol = rs.getString("symbol");
                String name = rs.getString("name");
                BigDecimal price = rs.getBigDecimal("price");

                // Fetch the change_percent from the database.
                double changePercent = rs.getDouble("change_percent");

                // Simulate change if the database returns the default (0.0)
                if (changePercent == 0.0) {
                    // Simulate a random value between -5% and +5%
                    changePercent = (random.nextDouble() * 0.10) - 0.05;
                }

                boolean suspended = rs.getBoolean("is_suspended");

                stocks.add(new Stock(id, symbol, name, price, changePercent, suspended));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all stocks from database: " + e.getMessage());
            e.printStackTrace();
        }
        return stocks;
    }

    /**
     * Get a stock by ID
     */
    public Stock getStockById(int stockId) {
        String sql = "SELECT id, symbol, name, price, change_percent, is_suspended FROM stocks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String symbol = rs.getString("symbol");
                    String name = rs.getString("name");
                    BigDecimal price = rs.getBigDecimal("price");
                    double changePercent = rs.getDouble("change_percent");
                    boolean suspended = rs.getBoolean("is_suspended");

                    return new Stock(id, symbol, name, price, changePercent, suspended);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stock by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update stock price and other details
     */
    public boolean updateStock(Stock stock) {
        String sql = "UPDATE stocks SET symbol = ?, name = ?, price = ?, change_percent = ?, is_suspended = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stock.getSymbol());
            stmt.setString(2, stock.getName());
            stmt.setBigDecimal(3, stock.getPrice());
            stmt.setDouble(4, stock.getChangePercent());
            stmt.setBoolean(5, stock.isSuspended());
            stmt.setInt(6, stock.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if a stock is suspended
     */
    public boolean isStockSuspended(int stockId) {
        String sql = "SELECT is_suspended FROM stocks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_suspended");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking stock suspension: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get a stock by symbol (using SQL query)
     * Note: For demonstration, see getStockBySymbolUsingLinearSearch() which uses
     * the Linear Search algorithm
     */
    public Stock getStockBySymbol(String symbol) {
        String sql = "SELECT id, symbol, name, price, change_percent, is_suspended FROM stocks WHERE symbol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, symbol);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String sym = rs.getString("symbol");
                    String name = rs.getString("name");
                    BigDecimal price = rs.getBigDecimal("price");
                    double changePercent = rs.getDouble("change_percent");
                    boolean suspended = rs.getBoolean("is_suspended");

                    return new Stock(id, sym, name, price, changePercent, suspended);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stock by symbol: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ==================== ALGORITHM DEMONSTRATION METHODS ====================
    // These methods demonstrate the use of custom search and sort algorithms
    // for educational purposes (viva demonstration)

    /**
     * ALGORITHM DEMO: Uses LINEAR SEARCH to find a stock by symbol.
     * 
     * Algorithm: Linear Search
     * Time Complexity: O(n) - checks each stock sequentially
     * 
     * This method fetches all stocks and uses LinearSearch.searchStockBySymbol()
     * instead of a SQL WHERE clause. Great for demonstrating linear search
     * algorithm.
     * 
     * @param symbol The stock symbol to search for (e.g., "AAPL")
     * @return The Stock object if found, null otherwise
     * 
     *         API Route: Can be accessed via /api/stocks/symbol/{symbol}
     *         Example: GET http://localhost:8080/api/stocks/symbol/AAPL
     */
    public Stock getStockBySymbolUsingLinearSearch(String symbol) {
        System.out.println("\n========== USING LINEAR SEARCH ALGORITHM ==========");
        List<Stock> allStocks = getAllStocks();
        return LinearSearch.searchStockBySymbol(allStocks, symbol);
    }

    /**
     * ALGORITHM DEMO: Gets all stocks sorted by price using INSERTION SORT.
     * 
     * Algorithm: Insertion Sort
     * Time Complexity: O(n²) in worst case, O(n) if already sorted
     * 
     * Perfect for small datasets (typically 10-50 stocks). Insertion sort is:
     * - Simple to implement and understand
     * - Efficient for small datasets
     * - Stable (maintains relative order of equal elements)
     * - Adaptive (efficient for partially sorted data)
     * 
     * @param ascending true for cheapest first, false for most expensive first
     * @return List of stocks sorted by price
     * 
     *         API Route: GET /api/stocks/all?sort=price&order=asc
     *         Example: GET
     *         http://localhost:8080/api/stocks/all?sort=price&order=desc
     */
    public List<Stock> getAllStocksSortedByPrice(boolean ascending) {
        System.out.println("\n========== USING INSERTION SORT ALGORITHM ==========");
        List<Stock> stocks = getAllStocks();
        InsertionSort.sortStocksByPrice(stocks, ascending);
        return stocks;
    }

    /**
     * ALGORITHM DEMO: Gets all stocks sorted alphabetically by symbol using
     * INSERTION SORT.
     * 
     * Algorithm: Insertion Sort
     * Time Complexity: O(n²)
     * 
     * Sorts stocks alphabetically: AAPL, AMZN, GOOG, MSFT, TSLA
     * Makes it easy to visually locate stocks in the UI.
     * 
     * @return List of stocks sorted alphabetically by symbol
     * 
     *         API Route: GET /api/stocks/all?sort=symbol
     *         Example: GET http://localhost:8080/api/stocks/all?sort=symbol
     */
    public List<Stock> getAllStocksSortedBySymbol() {
        System.out.println("\n========== USING INSERTION SORT ALGORITHM ==========");
        List<Stock> stocks = getAllStocks();
        InsertionSort.sortStocksBySymbol(stocks);
        return stocks;
    }

    /**
     * ALGORITHM DEMO: Gets top gainers or losers using INSERTION SORT.
     * 
     * Algorithm: Insertion Sort by change percentage
     * 
     * @param showGainers true for top gainers (highest % first), false for top
     *                    losers
     * @return List of stocks sorted by change percentage
     * 
     *         API Route: GET /api/stocks/all?sort=change&order=desc (for gainers)
     *         Example: GET
     *         http://localhost:8080/api/stocks/all?sort=change&order=asc (for
     *         losers)
     */
    public List<Stock> getStocksSortedByChange(boolean showGainers) {
        System.out.println("\n========== USING INSERTION SORT ALGORITHM ==========");
        List<Stock> stocks = getAllStocks();
        // false = descending (gainers first), true = ascending (losers first)
        InsertionSort.sortStocksByChangePercent(stocks, !showGainers);
        return stocks;
    }
}