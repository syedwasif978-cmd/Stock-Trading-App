package com.stockapp.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private int userID;
    private int stockID;
    private int quantity;
    private BigDecimal price; // The price at the time of transaction
    private Timestamp timestamp;
    private String type; // buy or sell
    private TransactionStatus status;
    private Timestamp completedAt;

    // Constructor for the transaction ( When reading from the database)
    public Transaction(int id, int userID, int stockID, int quantity, BigDecimal price, Timestamp timestamp,
            String type, String status) {
        this.id = id;
        this.userID = userID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
        this.type = type;
        this.status = TransactionStatus.fromString(status);
    }

    // Constructor for the transaction ( When creating a new transaction)
    public Transaction(int userID, int stockID, int quantity, BigDecimal price, String type) {
        this.userID = userID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.status = TransactionStatus.PENDING;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    // Default constructor
    public Transaction() {
        this.status = TransactionStatus.PENDING;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.price = BigDecimal.ZERO;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUserId(int userID) {
        this.userID = userID;
    }

    public int getStockId() {
        return stockID;
    }

    public int getStockID() {
        return stockID;
    }

    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    public void setStockId(int stockID) {
        this.stockID = stockID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTransactionDate() {
        return timestamp != null ? timestamp.toString() : "";
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    // Alias for created_at timestamp
    public Timestamp getCreatedAt() {
        return timestamp;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.timestamp = createdAt;
    }
}
