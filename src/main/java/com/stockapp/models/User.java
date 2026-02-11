package com.stockapp.models;

import java.math.BigDecimal;

public class User {

    // Attributes For the user
    private int id;
    private String username;
    private String password;
    private BigDecimal balance;
    private UserRole role;
    private boolean isActive;

    /**
     * Mandatory no-argument constructor.
     * This is required when the LoginController calls 'new User()'
     * before setting properties using setters.
     */
    public User() {
        this.role = UserRole.USER;
        this.isActive = true;
    }

    // Constructor for the user
    public User(int id, String username, String password, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.role = UserRole.USER;
        this.isActive = true;
    }

    // Full constructor
    public User(int id, String username, String password, BigDecimal balance, UserRole role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
