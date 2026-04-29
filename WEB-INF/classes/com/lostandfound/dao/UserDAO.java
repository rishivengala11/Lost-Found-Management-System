package com.lostandfound.dao;

import com.lostandfound.model.User;
import com.lostandfound.util.DBConnection;
import java.sql.*;

public class UserDAO {

    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        System.out.println("LOGIN EMAIL: [" + email + "]");
        System.out.println("LOGIN PASSWORD: [" + password + "]");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("LOGIN SUCCESS: user found");
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setVerified(rs.getBoolean("is_verified"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("LOGIN FAILED: no matching user");
        return null;
    }

    public boolean registerUser(User user, String token, Timestamp expiry) {
        String sql = "INSERT INTO users (email, password, full_name, role, verification_token, token_expiry) VALUES (?, ?, ?, 'USER', ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, token);
            ps.setTimestamp(5, expiry);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyUser(String token) {
        String verifySql = "UPDATE users SET is_verified = TRUE, verification_token = NULL, token_expiry = NULL WHERE verification_token = ? AND token_expiry > NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setString(1, token);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateVerificationToken(String email, String newToken, Timestamp expiry) {
        String sql = "UPDATE users SET verification_token = ?, token_expiry = ? WHERE email = ? AND is_verified = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newToken);
            ps.setTimestamp(2, expiry);
            ps.setString(3, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<String> getAllVerifiedUserEmails() {
        java.util.List<String> emails = new java.util.ArrayList<>();
        String sql = "SELECT email FROM users WHERE is_verified = TRUE";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }

    public int getPendingCount() {
        int count = 0;
        String sql = "SELECT (SELECT COUNT(*) FROM lost_items WHERE verification_status = 'PENDING') + " +
                "(SELECT COUNT(*) FROM found_items WHERE verification_status = 'PENDING') AS total";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
