package com.digitallocker.dao;

import com.digitallocker.model.User;
import com.digitallocker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for User operations.
 */
public class UserDAO {

    /**
     * Registers a new user in the database.
     * @param user The User object containing username and password.
     * @return The ID of the newly registered user, or -1 if registration fails.
     * @throws SQLException If a database access error occurs.
     */
    public int registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int userId = -1;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // WARNING: In production, store hashed passwords!

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getInt(1);
                }
            }
        } finally {
            DBConnection.close(conn, pstmt, rs);
        }
        return userId;
    }

    /**
     * Authenticates a user by checking their username and password.
     * @param username The username to check.
     * @param password The password to check.
     * @return The User object if authentication is successful, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public User getUserByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password FROM users WHERE username = ? AND password = ?"; // WARNING: For hashed passwords, you'd hash the input password and compare.
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } finally {
            DBConnection.close(conn, pstmt, rs);
        }
        return user;
    }

    /**
     * Checks if a username already exists in the database.
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean doesUsernameExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
        } finally {
            DBConnection.close(conn, pstmt, rs);
        }
        return exists;
    }
}
