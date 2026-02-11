package com.stockapp.algorithms;

import com.stockapp.models.Stock;
import com.stockapp.models.Portfolio;
import java.util.List;

/**
 * Insertion Sort Algorithm Implementation
 * 
 * Time Complexity:
 * - Best case: O(n) - when list is already sorted
 * - Average case: O(n²)
 * - Worst case: O(n²)
 * Space Complexity: O(1) - sorts in place
 * 
 * Best for: Small datasets (< 100 items), nearly sorted data
 * Characteristics: Stable sort, adaptive (efficient for partially sorted data)
 * 
 * Insertion sort builds the final sorted array one item at a time.
 * It takes each element and inserts it into its correct position among the
 * already sorted elements.
 * Similar to how you would sort playing cards in your hand.
 */
public class InsertionSort {

    /**
     * Sorts stocks by price using insertion sort algorithm.
     * 
     * @param stocks    List of stocks to sort
     * @param ascending true for ascending order, false for descending
     * 
     *                  Use case: Display stocks from cheapest to most expensive (or
     *                  vice versa)
     *                  Perfect for small stock portfolios (10-50 stocks)
     * 
     *                  Example: Sorting 20 stocks by price takes ~200 comparisons
     *                  in worst case
     */
    public static void sortStocksByPrice(List<Stock> stocks, boolean ascending) {
        if (stocks == null || stocks.size() <= 1) {
            return;
        }

        System.out.println("[ALGORITHM] Insertion Sort: Sorting " + stocks.size() +
                " stocks by price (" + (ascending ? "ascending" : "descending") + ")");

        int comparisons = 0;
        int swaps = 0;

        // Start from second element (index 1)
        for (int i = 1; i < stocks.size(); i++) {
            Stock key = stocks.get(i);
            int j = i - 1;

            // Move elements that are greater/lesser than key one position ahead
            if (ascending) {
                // Ascending order: smaller prices first
                while (j >= 0 && stocks.get(j).getPrice().compareTo(key.getPrice()) > 0) {
                    comparisons++;
                    stocks.set(j + 1, stocks.get(j));
                    swaps++;
                    j = j - 1;
                }
            } else {
                // Descending order: larger prices first
                while (j >= 0 && stocks.get(j).getPrice().compareTo(key.getPrice()) < 0) {
                    comparisons++;
                    stocks.set(j + 1, stocks.get(j));
                    swaps++;
                    j = j - 1;
                }
            }

            stocks.set(j + 1, key);
            if (j + 1 != i) {
                swaps++;
            }
        }

        System.out.println("[ALGORITHM] Insertion Sort: Complete! " +
                comparisons + " comparisons, " + swaps + " swaps");
    }

    /**
     * Sorts stocks alphabetically by their symbol (ticker).
     * 
     * @param stocks List of stocks to sort
     * 
     *               Use case: Display stocks in alphabetical order (AAPL, AMZN,
     *               GOOG, MSFT, TSLA)
     *               Makes it easier to find stocks visually
     */
    public static void sortStocksBySymbol(List<Stock> stocks) {
        if (stocks == null || stocks.size() <= 1) {
            return;
        }

        System.out.println("[ALGORITHM] Insertion Sort: Sorting " + stocks.size() +
                " stocks alphabetically by symbol");

        int comparisons = 0;

        for (int i = 1; i < stocks.size(); i++) {
            Stock key = stocks.get(i);
            int j = i - 1;

            // Sort alphabetically (A to Z)
            while (j >= 0 && stocks.get(j).getSymbol().compareTo(key.getSymbol()) > 0) {
                comparisons++;
                stocks.set(j + 1, stocks.get(j));
                j = j - 1;
            }

            stocks.set(j + 1, key);
        }

        System.out.println("[ALGORITHM] Insertion Sort: Complete! " + comparisons + " comparisons");
    }

    /**
     * Sorts stocks by their change percentage.
     * 
     * @param stocks    List of stocks to sort
     * @param ascending true to show biggest losers first, false to show biggest
     *                  gainers first
     * 
     *                  Use case: Display "Top Gainers" or "Top Losers" in the
     *                  dashboard
     */
    public static void sortStocksByChangePercent(List<Stock> stocks, boolean ascending) {
        if (stocks == null || stocks.size() <= 1) {
            return;
        }

        System.out.println("[ALGORITHM] Insertion Sort: Sorting stocks by change % (" +
                (ascending ? "losers first" : "gainers first") + ")");

        for (int i = 1; i < stocks.size(); i++) {
            Stock key = stocks.get(i);
            int j = i - 1;

            if (ascending) {
                while (j >= 0 && stocks.get(j).getChangePercent() > key.getChangePercent()) {
                    stocks.set(j + 1, stocks.get(j));
                    j = j - 1;
                }
            } else {
                while (j >= 0 && stocks.get(j).getChangePercent() < key.getChangePercent()) {
                    stocks.set(j + 1, stocks.get(j));
                    j = j - 1;
                }
            }

            stocks.set(j + 1, key);
        }

        System.out.println("[ALGORITHM] Insertion Sort: Complete!");
    }

    /**
     * Sorts user's portfolio by quantity (number of shares owned).
     * 
     * @param portfolio List of portfolio items to sort
     * @param ascending true for ascending order, false for descending
     * 
     *                  Use case: Show user's biggest holdings first (most shares
     *                  owned)
     */
    public static void sortPortfolioByQuantity(List<Portfolio> portfolio, boolean ascending) {
        if (portfolio == null || portfolio.size() <= 1) {
            return;
        }

        System.out.println("[ALGORITHM] Insertion Sort: Sorting " + portfolio.size() +
                " portfolio items by quantity");

        for (int i = 1; i < portfolio.size(); i++) {
            Portfolio key = portfolio.get(i);
            int j = i - 1;

            if (ascending) {
                while (j >= 0 && portfolio.get(j).getQuantity() > key.getQuantity()) {
                    portfolio.set(j + 1, portfolio.get(j));
                    j = j - 1;
                }
            } else {
                while (j >= 0 && portfolio.get(j).getQuantity() < key.getQuantity()) {
                    portfolio.set(j + 1, portfolio.get(j));
                    j = j - 1;
                }
            }

            portfolio.set(j + 1, key);
        }

        System.out.println("[ALGORITHM] Insertion Sort: Complete!");
    }

    /**
     * Sorts portfolio by average price (cost basis).
     * 
     * @param portfolio List of portfolio items to sort
     * @param ascending true for ascending order, false for descending
     * 
     *                  Use case: Show which stocks user bought at highest/lowest
     *                  prices
     */
    public static void sortPortfolioByAveragePrice(List<Portfolio> portfolio, boolean ascending) {
        if (portfolio == null || portfolio.size() <= 1) {
            return;
        }

        System.out.println("[ALGORITHM] Insertion Sort: Sorting portfolio by average price");

        for (int i = 1; i < portfolio.size(); i++) {
            Portfolio key = portfolio.get(i);
            int j = i - 1;

            if (ascending) {
                while (j >= 0 && portfolio.get(j).getAvgPrice() > key.getAvgPrice()) {
                    portfolio.set(j + 1, portfolio.get(j));
                    j = j - 1;
                }
            } else {
                while (j >= 0 && portfolio.get(j).getAvgPrice() < key.getAvgPrice()) {
                    portfolio.set(j + 1, portfolio.get(j));
                    j = j - 1;
                }
            }

            portfolio.set(j + 1, key);
        }

        System.out.println("[ALGORITHM] Insertion Sort: Complete!");
    }
}
