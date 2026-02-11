package com.stockapp.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.stockapp.DatabaseConnection;
import com.stockapp.models.Transaction;
import com.stockapp.models.TransactionStatus;

/**
 * Data Access Object for Transaction entity.
 * Handles CRUD operations for buy/sell transactions with status tracking.
 */
@Repository
public class TransactionDAO {

    private static final String INSERT_TRANSACTION_SQL = "INSERT INTO transactions (user_id, stock_id, quantity, price, transaction_type, status, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";

    private static final String SELECT_TRANSACTIONS_BY_USER_SQL = "SELECT id, user_id, stock_id, quantity, price, created_at, transaction_type, status FROM transactions WHERE user_id = ? ORDER BY created_at DESC";

    private static final String SELECT_ALL_TRANSACTIONS_SQL = "SELECT id, user_id, stock_id, quantity, price, created_at, transaction_type, status FROM transactions ORDER BY created_at DESC";

    private static final String UPDATE_TRANSACTION_STATUS_SQL = "UPDATE transactions SET status = ?, completed_at = NOW() WHERE id = ?";

    /**
     * Maps a ResultSet row to a Transaction object.
     */
    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userID = rs.getInt("user_id");
        int stockID = rs.getInt("stock_id");
        int quantity = rs.getInt("quantity");
        BigDecimal price = rs.getBigDecimal("price");
        Timestamp timestamp = rs.getTimestamp("created_at");
        String type = rs.getString("transaction_type");
        String status = rs.getString("status");

        return new Transaction(id, userID, stockID, quantity, price, timestamp, type, status);
    }

    /**
     * Records a new transaction (buy or sell).
     */
    public int recordTransaction(int userID, int stockID, int quantity, double price, String type) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_TRANSACTION_SQL,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userID);
            stmt.setInt(2, stockID);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.setString(5, type.toUpperCase()); // "BUY" or "SELL"
            stmt.setString(6, "PENDING");

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to record transaction: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Create a new transaction from a Transaction object
     */
    public int createTransaction(Transaction transaction) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_TRANSACTION_SQL,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transaction.getUserID());
            stmt.setInt(2, transaction.getStockID());
            stmt.setInt(3, transaction.getQuantity());
            stmt.setBigDecimal(4, transaction.getPrice());
            stmt.setString(5, transaction.getType().toUpperCase());
            stmt.setString(6, transaction.getStatus().name());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to create transaction: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Update transaction status.
     */
    public boolean updateTransactionStatus(int transactionId, TransactionStatus status) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_TRANSACTION_STATUS_SQL)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, transactionId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to update transaction status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all transactions for a specific user.
     */
    public List<Transaction> getTransactionsByUser(int userID) {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_TRANSACTIONS_BY_USER_SQL)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Retrieves all transactions in the system.
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_TRANSACTIONS_SQL)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve all transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Alias for getTransactionsByUser - used by controllers
     */
    public List<Transaction> getUserTransactions(int userID) {
        return getTransactionsByUser(userID);
    }

    /**
     * Get transaction count for a user
     */
    public int getUserTransactionCount(int userID) {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to get transaction count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get transaction by ID
     */
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT id, user_id, stock_id, quantity, price, created_at, transaction_type, status FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToTransaction(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to get transaction by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update transaction
     */
    public boolean updateTransaction(Transaction transaction) {
        String sql = "UPDATE transactions SET user_id = ?, stock_id = ?, quantity = ?, price = ?, transaction_type = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transaction.getUserID());
            stmt.setInt(2, transaction.getStockID());
            stmt.setInt(3, transaction.getQuantity());
            stmt.setBigDecimal(4, transaction.getPrice());
            stmt.setString(5, transaction.getType());
            stmt.setString(6, transaction.getStatus().name());
            stmt.setInt(7, transaction.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to update transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}