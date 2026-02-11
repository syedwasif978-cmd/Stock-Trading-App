package com.stockapp.dao;

import com.stockapp.models.StockSuspension;
import com.stockapp.DatabaseConnection;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for stock suspensions (admin operations).
 */
@Repository
public class StockSuspensionDAO {

    public StockSuspensionDAO() {
    }

    /**
     * Suspend a stock.
     */
    public void suspendStock(int stockId, int adminId, String reason) {
        String suspendQuery = "UPDATE stocks SET is_suspended = TRUE, suspension_reason = ?, suspended_at = NOW() WHERE id = ?";
        String logQuery = "INSERT INTO stock_suspensions (stock_id, admin_id, reason, suspended_at, is_active) VALUES (?, ?, ?, NOW(), TRUE)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt1 = conn.prepareStatement(suspendQuery)) {
                stmt1.setString(1, reason);
                stmt1.setInt(2, stockId);
                stmt1.executeUpdate();
            }
            try (PreparedStatement stmt2 = conn.prepareStatement(logQuery)) {
                stmt2.setInt(1, stockId);
                stmt2.setInt(2, adminId);
                stmt2.setString(3, reason);
                stmt2.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error suspending stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Resume a stock.
     */
    public void resumeStock(int stockId) {
        String query = "UPDATE stocks SET is_suspended = FALSE, suspension_reason = NULL WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, stockId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error resuming stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get all suspensions.
     */
    public List<StockSuspension> getAllSuspensions() {
        List<StockSuspension> suspensions = new ArrayList<>();
        String query = "SELECT * FROM stock_suspensions ORDER BY suspended_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                StockSuspension suspension = new StockSuspension();
                suspension.setId(rs.getInt("id"));
                suspension.setStockId(rs.getInt("stock_id"));
                suspension.setAdminId(rs.getInt("admin_id"));
                suspension.setReason(rs.getString("reason"));
                suspension.setSuspendedAt(rs.getTimestamp("suspended_at").toLocalDateTime());
                Timestamp resumedAt = rs.getTimestamp("resumed_at");
                if (resumedAt != null) {
                    suspension.setResumedAt(resumedAt.toLocalDateTime());
                }
                suspension.setActive(rs.getBoolean("is_active"));
                suspensions.add(suspension);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving suspensions: " + e.getMessage());
            e.printStackTrace();
        }
        return suspensions;
    }

    /**
     * Check if a stock is suspended.
     */
    public boolean isStockSuspended(int stockId) {
        String query = "SELECT is_suspended FROM stocks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, stockId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_suspended");
            }
        } catch (SQLException e) {
            System.err.println("Error checking stock suspension: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
