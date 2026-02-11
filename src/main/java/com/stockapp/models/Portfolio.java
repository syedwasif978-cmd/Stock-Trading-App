package com.stockapp.models;

public class Portfolio {
    private int userID;
    private int stockID;
    private int quantity; // Total quantity of the stock in the portfolio
    private int id;
    private double avgPrice; // Average price of the stock in the portfolio

    // Constructor
    public Portfolio(int userID, int stockID, int quantity, int id, double avgPrice) {
        this.userID = userID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.id = id;
        this.avgPrice = avgPrice;
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getStockID() {
        return stockID;
    }

    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }
}
