package com.stockapp.dao;

import com.stockapp.models.TradeCancellation;
import com.stockapp.DatabaseConnection;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for trade cancellations and rollback records.
 */
@Repository
public class TradeCancellationDAO {

    public TradeCancellationDAO() {
    }

    /**
     * Record a trade cancellation.
     */
    public void recordCancellation(int transactionId, int userId, String reason) {
        String query = "INSERT INTO trade_cancellations (original_transaction_id, user_id, reason, rollback_executed, cancelled_at) VALUES (?, ?, ?, FALSE, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transactionId);
            stmt.setInt(2, userId);
            stmt.setString(3, reason);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording cancellation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mark cancellation as rollback executed.
     */
    public void markRollbackExecuted(int cancellationId) {
        String query = "UPDATE trade_cancellations SET rollback_executed = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cancellationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error marking rollback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get all cancellations for a user.
     */
    public List<TradeCancellation> getCancellationsByUser(int userId) {
        List<TradeCancellation> cancellations = new ArrayList<>();
        String query = "SELECT * FROM trade_cancellations WHERE user_id = ? ORDER BY cancelled_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TradeCancellation cancellation = new TradeCancellation();
                cancellation.setId(rs.getInt("id"));
                cancellation.setOriginalTransactionId(rs.getInt("original_transaction_id"));
                cancellation.setUserId(rs.getInt("user_id"));
                cancellation.setReason(rs.getString("reason"));
                cancellation.setRollbackExecuted(rs.getBoolean("rollback_executed"));
                cancellation.setCancelledAt(rs.getTimestamp("cancelled_at").toLocalDateTime());
                cancellations.add(cancellation);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving cancellations: " + e.getMessage());
            e.printStackTrace();
        }
        return cancellations;
    }

    /**
     * Get all cancellations (admin view).
     */
    public List<TradeCancellation> getAllCancellations() {
        List<TradeCancellation> cancellations = new ArrayList<>();
        String query = "SELECT * FROM trade_cancellations ORDER BY cancelled_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                TradeCancellation cancellation = new TradeCancellation();
                cancellation.setId(rs.getInt("id"));
                cancellation.setOriginalTransactionId(rs.getInt("original_transaction_id"));
                cancellation.setUserId(rs.getInt("user_id"));
                cancellation.setReason(rs.getString("reason"));
                cancellation.setRollbackExecuted(rs.getBoolean("rollback_executed"));
                cancellation.setCancelledAt(rs.getTimestamp("cancelled_at").toLocalDateTime());
                cancellations.add(cancellation);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all cancellations: " + e.getMessage());
            e.printStackTrace();
        }
        return cancellations;
    }
}
