package com.stockapp.dao;

import com.stockapp.DatabaseConnection;
import com.stockapp.models.Portfolio;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Portfolio entity.
 * Handles CRUD operations for user portfolios.
 */
@Repository
public class PortfolioDAO {

    private static final String INSERT_PORTFOLIO_SQL = "INSERT INTO portfolios (user_id, stock_id, quantity, average_price) VALUES (?, ?, ?, ?)";

    private static final String SELECT_PORTFOLIO_BY_USER_SQL = "SELECT id, user_id, stock_id, quantity, average_price FROM portfolios WHERE user_id = ?";

    private static final String SELECT_PORTFOLIO_BY_USER_AND_STOCK_SQL = "SELECT id, user_id, stock_id, quantity, average_price FROM portfolios WHERE user_id = ? AND stock_id = ?";

    private static final String UPDATE_PORTFOLIO_SQL = "UPDATE portfolios SET quantity = ?, average_price = ? WHERE id = ?";

    private static final String DELETE_PORTFOLIO_SQL = "DELETE FROM portfolios WHERE id = ?";

    /**
     * Maps a ResultSet row to a Portfolio object.
     */
    private Portfolio mapRowToPortfolio(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userID = rs.getInt("user_id");
        int stockID = rs.getInt("stock_id");
        int quantity = rs.getInt("quantity");
        double avgPrice = rs.getDouble("average_price");

        return new Portfolio(userID, stockID, quantity, id, avgPrice);
    }

    /**
     * Adds a new stock to user's portfolio.
     */
    public boolean addToPortfolio(int userID, int stockID, int quantity, double avgPrice) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_PORTFOLIO_SQL)) {

            stmt.setInt(1, userID);
            stmt.setInt(2, stockID);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, avgPrice);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to add portfolio entry: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all portfolio entries for a user.
     */
    public List<Portfolio> getPortfolioByUser(int userID) {
        List<Portfolio> portfolios = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_PORTFOLIO_BY_USER_SQL)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                portfolios.add(mapRowToPortfolio(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve portfolio: " + e.getMessage());
            e.printStackTrace();
        }
        return portfolios;
    }

    /**
     * Retrieves a specific portfolio entry for a user and stock.
     */
    public Portfolio getPortfolioByUserAndStock(int userID, int stockID) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_PORTFOLIO_BY_USER_AND_STOCK_SQL)) {

            stmt.setInt(1, userID);
            stmt.setInt(2, stockID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Portfolio portfolio = mapRowToPortfolio(rs);
                rs.close();
                return portfolio;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to retrieve portfolio entry: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates a portfolio entry with new quantity and average price.
     */
    public boolean updatePortfolio(int portfolioID, int quantity, double avgPrice) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_PORTFOLIO_SQL)) {

            stmt.setInt(1, quantity);
            stmt.setDouble(2, avgPrice);
            stmt.setInt(3, portfolioID);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to update portfolio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a portfolio entry.
     */
    public boolean deletePortfolio(int portfolioID) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_PORTFOLIO_SQL)) {

            stmt.setInt(1, portfolioID);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to delete portfolio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Alias for getPortfolioByUser - used by controllers
     */
    public List<Portfolio> getUserPortfolio(int userID) {
        return getPortfolioByUser(userID);
    }

    /**
     * Alias for getPortfolioByUserAndStock - used by controllers
     */
    public Portfolio getPortfolioItem(int userID, int stockID) {
        return getPortfolioByUserAndStock(userID, stockID);
    }

    /**
     * Create a new portfolio item
     */
    public boolean createPortfolioItem(Portfolio portfolio) {
        return addToPortfolio(portfolio.getUserID(), portfolio.getStockID(),
                portfolio.getQuantity(), portfolio.getAvgPrice());
    }

    /**
     * Update an existing portfolio item
     */
    public boolean updatePortfolioItem(Portfolio portfolio) {
        return updatePortfolio(portfolio.getId(), portfolio.getQuantity(),
                portfolio.getAvgPrice());
    }

    /**
     * Delete a portfolio item by ID
     */
    public boolean deletePortfolioItem(int portfolioID) {
        return deletePortfolio(portfolioID);
    }
}