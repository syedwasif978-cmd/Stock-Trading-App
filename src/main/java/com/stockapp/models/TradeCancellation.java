package com.stockapp.models;

import java.time.LocalDateTime;

/**
 * Model class for trade cancellations.
 */
public class TradeCancellation {
    private int id;
    private int originalTransactionId;
    private int userId;
    private String reason;
    private boolean rollbackExecuted;
    private LocalDateTime cancelledAt;

    public TradeCancellation() {
    }

    public TradeCancellation(int originalTransactionId, int userId, String reason) {
        this.originalTransactionId = originalTransactionId;
        this.userId = userId;
        this.reason = reason;
        this.rollbackExecuted = false;
        this.cancelledAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOriginalTransactionId() {
        return originalTransactionId;
    }

    public void setOriginalTransactionId(int originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isRollbackExecuted() {
        return rollbackExecuted;
    }

    public void setRollbackExecuted(boolean rollbackExecuted) {
        this.rollbackExecuted = rollbackExecuted;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    @Override
    public String toString() {
        return "TradeCancellation{" +
                "id=" + id +
                ", originalTransactionId=" + originalTransactionId +
                ", userId=" + userId +
                ", reason='" + reason + '\'' +
                ", rollbackExecuted=" + rollbackExecuted +
                ", cancelledAt=" + cancelledAt +
                '}';
    }
}
