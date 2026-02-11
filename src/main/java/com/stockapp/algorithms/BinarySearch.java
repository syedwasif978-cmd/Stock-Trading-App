package com.stockapp.algorithms;

import com.stockapp.models.Stock;
import com.stockapp.models.User;
import java.util.List;

/**
 * Binary Search Algorithm Implementation
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 * 
 * Best for: Searching in sorted lists
 * Prerequisite: List must be sorted by the search key
 * 
 * Binary search works by repeatedly dividing the search interval in half.
 * It compares the target value to the middle element of the array.
 * If they are equal, the search is successful.
 * If the target is less than the middle element, search the left half.
 * If the target is greater, search the right half.
 */
public class BinarySearch {

    /**
     * Searches for a stock by its ID using binary search algorithm.
     * 
     * @param sortedStocks List of stocks sorted by ID in ascending order
     * @param targetId     The ID of the stock to find
     * @return The Stock object if found, null otherwise
     * 
     *         Example: If looking for stock with ID=5 in a list of 100 stocks,
     *         binary search will find it in ~7 comparisons vs 50 on average for
     *         linear search
     */
    public static Stock searchStockById(List<Stock> sortedStocks, int targetId) {
        if (sortedStocks == null || sortedStocks.isEmpty()) {
            return null;
        }

        int left = 0;
        int right = sortedStocks.size() - 1;

        System.out.println("[ALGORITHM] Binary Search: Searching for Stock ID=" + targetId);
        int iterations = 0;

        while (left <= right) {
            iterations++;
            int mid = left + (right - left) / 2; // Prevents integer overflow
            Stock midStock = sortedStocks.get(mid);

            System.out.println("  Iteration " + iterations + ": Checking position " + mid +
                    " (ID=" + midStock.getId() + ")");

            if (midStock.getId() == targetId) {
                System.out.println("[ALGORITHM] Binary Search: Found after " + iterations + " iterations!");
                return midStock;
            } else if (midStock.getId() < targetId) {
                left = mid + 1; // Search right half
            } else {
                right = mid - 1; // Search left half
            }
        }

        System.out.println("[ALGORITHM] Binary Search: Not found after " + iterations + " iterations");
        return null;
    }

    /**
     * Searches for a user by their ID using binary search algorithm.
     * 
     * @param sortedUsers List of users sorted by ID in ascending order
     * @param targetId    The ID of the user to find
     * @return The User object if found, null otherwise
     */
    public static User searchUserById(List<User> sortedUsers, int targetId) {
        if (sortedUsers == null || sortedUsers.isEmpty()) {
            return null;
        }

        int left = 0;
        int right = sortedUsers.size() - 1;

        System.out.println("[ALGORITHM] Binary Search: Searching for User ID=" + targetId);
        int iterations = 0;

        while (left <= right) {
            iterations++;
            int mid = left + (right - left) / 2;
            User midUser = sortedUsers.get(mid);

            if (midUser.getId() == targetId) {
                System.out.println("[ALGORITHM] Binary Search: Found after " + iterations + " iterations!");
                return midUser;
            } else if (midUser.getId() < targetId) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        System.out.println("[ALGORITHM] Binary Search: Not found after " + iterations + " iterations");
        return null;
    }

    /**
     * Helper method to sort stocks by ID (ascending order) for binary search.
     * Uses simple bubble sort since we need a sorted list.
     * 
     * @param stocks List of stocks to sort
     */
    public static void sortStocksById(List<Stock> stocks) {
        if (stocks == null || stocks.size() <= 1) {
            return;
        }

        // Simple bubble sort for sorting by ID
        for (int i = 0; i < stocks.size() - 1; i++) {
            for (int j = 0; j < stocks.size() - i - 1; j++) {
                if (stocks.get(j).getId() > stocks.get(j + 1).getId()) {
                    // Swap
                    Stock temp = stocks.get(j);
                    stocks.set(j, stocks.get(j + 1));
                    stocks.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Helper method to sort users by ID (ascending order) for binary search.
     * 
     * @param users List of users to sort
     */
    public static void sortUsersById(List<User> users) {
        if (users == null || users.size() <= 1) {
            return;
        }

        for (int i = 0; i < users.size() - 1; i++) {
            for (int j = 0; j < users.size() - i - 1; j++) {
                if (users.get(j).getId() > users.get(j + 1).getId()) {
                    // Swap
                    User temp = users.get(j);
                    users.set(j, users.get(j + 1));
                    users.set(j + 1, temp);
                }
            }
        }
    }
}
