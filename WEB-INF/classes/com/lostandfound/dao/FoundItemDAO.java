package com.lostandfound.dao;

import com.lostandfound.model.FoundItem;
import com.lostandfound.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Found Items.
 * Handles all database operations related to found_items table.
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class FoundItemDAO {

    /**
     * Insert a new found item into the database
     */
    public boolean insert(FoundItem item) {
        String sql = "INSERT INTO found_items (user_id, item_name, category, description, date_found, location, finder_name, contact) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getUserId());
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getDescription());
            ps.setString(5, item.getDateFound());
            ps.setString(6, item.getLocation());
            ps.setString(7, item.getFinderName());
            ps.setString(8, item.getContact());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all found items from the database
     */
    public List<FoundItem> getAll() {
        List<FoundItem> items = new ArrayList<>();
        String sql = "SELECT * FROM found_items WHERE verification_status = 'APPROVED' ORDER BY created_at DESC";

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

    public List<FoundItem> getByUser(int userId) {
        List<FoundItem> items = new ArrayList<>();
        String sql = "SELECT * FROM found_items WHERE user_id = ? ORDER BY created_at DESC";

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
     * Get a found item by its ID
     */
    public FoundItem getById(int id) {
        String sql = "SELECT * FROM found_items WHERE id = ?";

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
     * Search found items by keyword (searches name, description, location)
     */
    public List<FoundItem> search(String keyword) {
        List<FoundItem> items = new ArrayList<>();
        String sql = "SELECT * FROM found_items WHERE (item_name LIKE ? OR description LIKE ? OR location LIKE ?) AND verification_status = 'APPROVED' ORDER BY created_at DESC";

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
     * Search found items by category
     */
    public List<FoundItem> searchByCategory(String category) {
        List<FoundItem> items = new ArrayList<>();
        String sql = "SELECT * FROM found_items WHERE category = ? AND verification_status = 'APPROVED' ORDER BY created_at DESC";

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

    public boolean requestReturn(int id) {
        String sql = "UPDATE found_items SET return_requested = TRUE WHERE id = ?";

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
        String sql = "UPDATE found_items SET status = ?, return_requested = FALSE WHERE id = ?";

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
     * Update the verification status of a found item
     */
    public boolean verifyItem(int id, String status) {
        String sql = "UPDATE found_items SET verification_status = ? WHERE id = ?";

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
     * Get all pending found items for admin dashboard
     */
    public List<FoundItem> getAllPending() {
        List<FoundItem> items = new ArrayList<>();
        String sql = "SELECT * FROM found_items WHERE verification_status = 'PENDING' ORDER BY created_at DESC";

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

    public List<FoundItem> getPendingReturnRequests() {
        List<FoundItem> items = new ArrayList<>();
        String sql = "SELECT * FROM found_items WHERE return_requested = TRUE ORDER BY created_at DESC";

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
     * Get the total count of found items
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM found_items WHERE verification_status = 'APPROVED'";

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
     * Get the count of returned items
     */
    public int getReturnedCount() {
        String sql = "SELECT COUNT(*) FROM found_items WHERE status = 'Returned'";

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
     * Get recent found items
     */
    public List<FoundItem> getRecent(int limit) {
        List<FoundItem> items = new ArrayList<>();
        String sql = "SELECT * FROM found_items WHERE verification_status = 'APPROVED' ORDER BY created_at DESC LIMIT ?";

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
     * Map a ResultSet row to a FoundItem object
     */
    private FoundItem mapResultSetToItem(ResultSet rs) throws SQLException {
        FoundItem item = new FoundItem();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setDescription(rs.getString("description"));
        item.setDateFound(rs.getString("date_found"));
        item.setLocation(rs.getString("location"));
        item.setFinderName(rs.getString("finder_name"));
        item.setContact(rs.getString("contact"));
        item.setStatus(rs.getString("status"));
        item.setVerificationStatus(rs.getString("verification_status"));
        item.setReturnRequested(rs.getBoolean("return_requested"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        return item;
    }
}
