package com.stockapp.models;

import java.math.BigDecimal;

public class Stock {

    private int id;
    private String symbol;
    private String name;
    private BigDecimal price;
    private double changePercent;
    private boolean suspended;

    public Stock(int id, String symbol, String name, double price, double changePercent) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.price = BigDecimal.valueOf(price);
        this.changePercent = changePercent;
        this.suspended = false;
    }

    // Constructor with suspended
    public Stock(int id, String symbol, String name, BigDecimal price, double changePercent, boolean suspended) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.changePercent = changePercent;
        this.suspended = suspended;
    }

    // Default constructor
    public Stock() {
        this.id = 0;
        this.symbol = "";
        this.name = "";
        this.price = BigDecimal.ZERO;
        this.changePercent = 0.0;
        this.suspended = false;
    }

    // --- Standard Getters/Setters --

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }
}
