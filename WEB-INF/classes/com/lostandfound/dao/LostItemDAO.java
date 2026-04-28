package com.lostandfound.dao;

import com.lostandfound.model.LostItem;
import com.lostandfound.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Lost Items.
 * Handles all database operations related to lost_items table.
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class LostItemDAO {

    /**
     * Insert a new lost item into the database
     */
    public boolean insert(LostItem item) {
        String sql = "INSERT INTO lost_items (user_id, item_name, category, description, date_lost, location, reporter_name, contact) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getUserId());
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getDescription());
            ps.setString(5, item.getDateLost());
            ps.setString(6, item.getLocation());
            ps.setString(7, item.getReporterName());
            ps.setString(8, item.getContact());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all lost items from the database
     */
    public List<LostItem> getAll() {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT * FROM lost_items WHERE verification_status = 'APPROVED' ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<LostItem> getByUser(int userId) {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT * FROM lost_items WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Get a lost item by its ID
     */
    public LostItem getById(int id) {
        String sql = "SELECT * FROM lost_items WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Search lost items by keyword (searches name, description, location)
     */
    public List<LostItem> search(String keyword) {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT * FROM lost_items WHERE (item_name LIKE ? OR description LIKE ? OR location LIKE ?) AND verification_status = 'APPROVED' ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Search lost items by category
     */
    public List<LostItem> searchByCategory(String category) {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT * FROM lost_items WHERE category = ? AND verification_status = 'APPROVED' ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean requestClaim(int id) {
        String sql = "UPDATE lost_items SET claim_requested = TRUE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE lost_items SET status = ?, claim_requested = FALSE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update the verification status of a lost item
     */
    public boolean verifyItem(int id, String status) {
        String sql = "UPDATE lost_items SET verification_status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all pending lost items for admin dashboard
     */
    public List<LostItem> getAllPending() {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT * FROM lost_items WHERE verification_status = 'PENDING' ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<LostItem> getPendingClaimRequests() {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT * FROM lost_items WHERE claim_requested = TRUE ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Get the total count of lost items
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM lost_items WHERE verification_status = 'APPROVED'";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get the count of claimed items
     */
    public int getClaimedCount() {
        String sql = "SELECT COUNT(*) FROM lost_items WHERE status = 'Claimed'";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get recent lost items (last 5)
     */
    public List<LostItem> getRecent(int limit) {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT * FROM lost_items WHERE verification_status = 'APPROVED' ORDER BY created_at DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Map a ResultSet row to a LostItem object
     */
    private LostItem mapResultSetToItem(ResultSet rs) throws SQLException {
        LostItem item = new LostItem();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setDescription(rs.getString("description"));
        item.setDateLost(rs.getString("date_lost"));
        item.setLocation(rs.getString("location"));
        item.setReporterName(rs.getString("reporter_name"));
        item.setContact(rs.getString("contact"));
        item.setStatus(rs.getString("status"));
        item.setVerificationStatus(rs.getString("verification_status"));
        item.setClaimRequested(rs.getBoolean("claim_requested"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        return item;
    }
}
