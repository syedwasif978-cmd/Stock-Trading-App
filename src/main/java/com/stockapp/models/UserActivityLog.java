package com.stockapp.models;

import java.time.LocalDateTime;

/**
 * Model class for user activity logs.
 */
public class UserActivityLog {
    private int id;
    private int userId;
    private String actionType;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;

    public UserActivityLog() {
    }

    public UserActivityLog(int userId, String actionType, String details, String ipAddress) {
        this.userId = userId;
        this.actionType = actionType;
        this.details = details;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Legacy getters for backward compatibility
    public String getActivityType() {
        return actionType;
    }

    public void setActivityType(String activityType) {
        this.actionType = activityType;
    }

    public String getDescription() {
        return details;
    }

    public void setDescription(String description) {
        this.details = description;
    }

    public LocalDateTime getLoggedAt() {
        return timestamp;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.timestamp = loggedAt;
    }

    @Override
    public String toString() {
        return "UserActivityLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", actionType='" + actionType + '\'' +
                ", details='" + details + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
