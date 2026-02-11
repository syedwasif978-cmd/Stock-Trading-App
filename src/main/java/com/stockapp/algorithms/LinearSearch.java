package com.stockapp.algorithms;

import com.stockapp.models.Stock;
import com.stockapp.models.Transaction;
import java.util.List;

/**
 * Linear Search Algorithm Implementation
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Best for: Searching in unsorted lists, searching by non-indexed fields (like
 * strings)
 * No prerequisite: Works on any list (sorted or unsorted)
 * 
 * Linear search checks each element in the list sequentially until it finds the
 * target
 * or reaches the end of the list. Simple but less efficient than binary search
 * for large datasets.
 */
public class LinearSearch {

    /**
     * Searches for a stock by its symbol (ticker) using linear search algorithm.
     * 
     * Since symbols are strings and the list might not be sorted by symbol,
     * linear search is the appropriate choice here.
     * 
     * @param stocks       List of stocks (can be unsorted)
     * @param targetSymbol The symbol of the stock to find (e.g., "AAPL", "GOOG")
     * @return The Stock object if found, null otherwise
     * 
     *         Example: Searching for "AAPL" in a list of 50 stocks
     *         Best case: O(1) - found at first position
     *         Average case: O(n/2) - found in middle
     *         Worst case: O(n) - found at end or not found
     */
    public static Stock searchStockBySymbol(List<Stock> stocks, String targetSymbol) {
        if (stocks == null || stocks.isEmpty() || targetSymbol == null) {
            return null;
        }

        System.out.println("[ALGORITHM] Linear Search: Searching for Stock Symbol=" + targetSymbol);
        int iterations = 0;

        // Check each stock one by one
        for (int i = 0; i < stocks.size(); i++) {
            iterations++;
            Stock currentStock = stocks.get(i);

            System.out.println("  Iteration " + iterations + ": Checking " + currentStock.getSymbol());

            // String comparison - case insensitive
            if (currentStock.getSymbol().equalsIgnoreCase(targetSymbol)) {
                System.out.println("[ALGORITHM] Linear Search: Found '" + targetSymbol +
                        "' after checking " + iterations + " stocks");
                return currentStock;
            }
        }

        System.out.println("[ALGORITHM] Linear Search: Symbol '" + targetSymbol +
                "' not found after checking all " + iterations + " stocks");
        return null;
    }

    /**
     * Searches for a transaction by its ID using linear search algorithm.
     * 
     * @param transactions List of transactions (can be unsorted)
     * @param targetId     The ID of the transaction to find
     * @return The Transaction object if found, null otherwise
     * 
     *         Real-world use case: Finding a specific transaction in user's history
     */
    public static Transaction searchTransactionById(List<Transaction> transactions, int targetId) {
        if (transactions == null || transactions.isEmpty()) {
            return null;
        }

        System.out.println("[ALGORITHM] Linear Search: Searching for Transaction ID=" + targetId);
        int iterations = 0;

        for (int i = 0; i < transactions.size(); i++) {
            iterations++;
            Transaction currentTransaction = transactions.get(i);

            if (currentTransaction.getId() == targetId) {
                System.out.println("[ALGORITHM] Linear Search: Found transaction after " +
                        iterations + " checks");
                return currentTransaction;
            }
        }

        System.out.println("[ALGORITHM] Linear Search: Transaction not found after " +
                iterations + " checks");
        return null;
    }

    /**
     * Searches for a stock by name (company name) using linear search.
     * Performs partial matching (case-insensitive).
     * 
     * @param stocks     List of stocks
     * @param searchTerm The name or partial name to search for (e.g., "Apple")
     * @return The first Stock object that matches, null otherwise
     * 
     *         Example: Searching for "Apple" will find "Apple Inc."
     */
    public static Stock searchStockByName(List<Stock> stocks, String searchTerm) {
        if (stocks == null || stocks.isEmpty() || searchTerm == null) {
            return null;
        }

        System.out.println("[ALGORITHM] Linear Search: Searching for Stock Name containing '" +
                searchTerm + "'");
        int iterations = 0;

        for (int i = 0; i < stocks.size(); i++) {
            iterations++;
            Stock currentStock = stocks.get(i);

            // Check if stock name contains the search term (case-insensitive)
            if (currentStock.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                System.out.println("[ALGORITHM] Linear Search: Found '" + currentStock.getName() +
                        "' after " + iterations + " iterations");
                return currentStock;
            }
        }

        System.out.println("[ALGORITHM] Linear Search: No stock name matching '" + searchTerm +
                "' found after " + iterations + " checks");
        return null;
    }

    /**
     * Counts how many stocks have a price above a certain threshold.
     * Demonstrates linear search for counting/filtering operations.
     * 
     * @param stocks   List of stocks
     * @param minPrice Minimum price threshold
     * @return Count of stocks with price >= minPrice
     */
    public static int countStocksAbovePrice(List<Stock> stocks, double minPrice) {
        if (stocks == null || stocks.isEmpty()) {
            return 0;
        }

        System.out.println("[ALGORITHM] Linear Search: Counting stocks with price >= $" + minPrice);
        int count = 0;

        for (Stock stock : stocks) {
            if (stock.getPrice().doubleValue() >= minPrice) {
                count++;
            }
        }

        System.out.println("[ALGORITHM] Linear Search: Found " + count + " stocks above $" + minPrice);
        return count;
    }
}
