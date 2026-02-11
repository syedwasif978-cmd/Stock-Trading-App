package com.stockapp.models;

import java.time.LocalDateTime;

/**
 * Model class for stock suspensions.
 */
public class StockSuspension {
    private int id;
    private int stockId;
    private int adminId;
    private String reason;
    private LocalDateTime suspendedAt;
    private LocalDateTime resumedAt;
    private boolean isActive;

    public StockSuspension() {
    }

    public StockSuspension(int stockId, int adminId, String reason) {
        this.stockId = stockId;
        this.adminId = adminId;
        this.reason = reason;
        this.suspendedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getSuspendedAt() {
        return suspendedAt;
    }

    public void setSuspendedAt(LocalDateTime suspendedAt) {
        this.suspendedAt = suspendedAt;
    }

    public LocalDateTime getResumedAt() {
        return resumedAt;
    }

    public void setResumedAt(LocalDateTime resumedAt) {
        this.resumedAt = resumedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "StockSuspension{" +
                "id=" + id +
                ", stockId=" + stockId +
                ", adminId=" + adminId +
                ", reason='" + reason + '\'' +
                ", suspendedAt=" + suspendedAt +
                ", resumedAt=" + resumedAt +
                ", isActive=" + isActive +
                '}';
    }
}
