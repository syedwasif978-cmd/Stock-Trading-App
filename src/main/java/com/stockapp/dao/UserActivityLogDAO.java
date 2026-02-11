package com.stockapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.stockapp.DatabaseConnection;
import com.stockapp.models.UserActivityLog;

/**
 * DAO for user activity logs.
 */
@Repository
public class UserActivityLogDAO {

    public UserActivityLogDAO() {
    }

    /**
     * Log user activity.
     */
    public void logActivity(int userId, String activityType, String description) {
        String query = "INSERT INTO user_activity_logs (user_id, action_type, details, logged_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, activityType);
            stmt.setString(3, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging activity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get all activities for a user.
     */
    public List<UserActivityLog> getActivitiesByUser(int userId) {
        List<UserActivityLog> activities = new ArrayList<>();
        String query = "SELECT * FROM user_activity_logs WHERE user_id = ? ORDER BY logged_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserActivityLog log = new UserActivityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setActionType(rs.getString("action_type"));
                log.setDetails(rs.getString("details"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setTimestamp(rs.getTimestamp("logged_at").toLocalDateTime());
                activities.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving activities: " + e.getMessage());
            e.printStackTrace();
        }
        return activities;
    }

    /**
     * Get all activities (admin view).
     */
    public List<UserActivityLog> getAllActivities() {
        List<UserActivityLog> activities = new ArrayList<>();
        String query = "SELECT * FROM user_activity_logs ORDER BY logged_at DESC LIMIT 1000";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                UserActivityLog log = new UserActivityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setActionType(rs.getString("action_type"));
                log.setDetails(rs.getString("details"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setTimestamp(rs.getTimestamp("logged_at").toLocalDateTime());
                activities.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all activities: " + e.getMessage());
            e.printStackTrace();
        }
        return activities;
    }

    /**
     * Delete old activity logs (older than N days).
     */
    public void deleteOldLogs(int daysOld) {
        String query = "DELETE FROM user_activity_logs WHERE logged_at < DATE_SUB(NOW(), INTERVAL ? DAY)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, daysOld);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting old logs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create a new activity log entry
     */
    public void createActivityLog(UserActivityLog log) {
        String query = "INSERT INTO user_activity_logs (user_id, activity_type, description, logged_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, log.getUserId());
            stmt.setString(2, log.getActionType());
            stmt.setString(3, log.getDetails());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating activity log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Search activities by username and/or action type
     */
    public List<UserActivityLog> searchActivities(String username, String actionType) {
        List<UserActivityLog> activities = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT a.* FROM user_activity_logs a");
        if (username != null && !username.isEmpty()) {
            query.append(" JOIN users u ON a.user_id = u.id");
        }
        query.append(" WHERE 1=1");
        if (username != null && !username.isEmpty()) {
            query.append(" AND u.username LIKE ?");
        }
        if (actionType != null && !actionType.isEmpty()) {
            query.append(" AND a.action_type = ?");
        }
        query.append(" ORDER BY a.logged_at DESC LIMIT 1000");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (username != null && !username.isEmpty()) {
                stmt.setString(paramIndex++, "%" + username + "%");
            }
            if (actionType != null && !actionType.isEmpty()) {
                stmt.setString(paramIndex++, actionType);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserActivityLog log = new UserActivityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setActionType(rs.getString("action_type"));
                log.setDetails(rs.getString("details"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setTimestamp(rs.getTimestamp("logged_at").toLocalDateTime());
                activities.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error searching activities: " + e.getMessage());
            e.printStackTrace();
        }
        return activities;
    }
}
