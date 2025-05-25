package com.digitallocker.dao;

import com.digitallocker.model.FileMetadata;
import com.digitallocker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for FileMetadata operations.
 */
public class FileDAO {

    /**
     * Adds new file metadata to the database.
     * @param fileMetadata The FileMetadata object to add.
     * @return The ID of the newly added file metadata, or -1 if insertion fails.
     * @throws SQLException If a database access error occurs.
     */
    public int addFileMetadata(FileMetadata fileMetadata) throws SQLException {
        String sql = "INSERT INTO files (user_id, original_filename, stored_filename) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int fileId = -1;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, fileMetadata.getUserId());
            pstmt.setString(2, fileMetadata.getOriginalFilename());
            pstmt.setString(3, fileMetadata.getStoredFilename());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    fileId = rs.getInt(1);
                }
            }
        } finally {
            DBConnection.close(conn, pstmt, rs);
        }
        return fileId;
    }

    /**
     * Retrieves all file metadata for a specific user.
     * @param userId The ID of the user.
     * @return A list of FileMetadata objects belonging to the user.
     * @throws SQLException If a database access error occurs.
     */
    public List<FileMetadata> getFilesByUserId(int userId) throws SQLException {
        List<FileMetadata> files = new ArrayList<>();
        String sql = "SELECT id, user_id, original_filename, stored_filename, upload_date FROM files WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                files.add(new FileMetadata(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("original_filename"),
                    rs.getString("stored_filename"),
                    rs.getTimestamp("upload_date")
                ));
            }
        } finally {
            DBConnection.close(conn, pstmt, rs);
        }
        return files;
    }

    /**
     * Retrieves file metadata by its ID and user ID.
     * This is crucial for access control.
     * @param fileId The ID of the file.
     * @param userId The ID of the user who owns the file.
     * @return The FileMetadata object if found and owned by the user, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public FileMetadata getFileByIdAndUserId(int fileId, int userId) throws SQLException {
        String sql = "SELECT id, user_id, original_filename, stored_filename, upload_date FROM files WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FileMetadata fileMetadata = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fileId);
            pstmt.setInt(2, userId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                fileMetadata = new FileMetadata(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("original_filename"),
                    rs.getString("stored_filename"),
                    rs.getTimestamp("upload_date")
                );
            }
        } finally {
            DBConnection.close(conn, pstmt, rs);
        }
        return fileMetadata;
    }

    /**
     * Deletes file metadata from the database.
     * @param fileId The ID of the file metadata to delete.
     * @param userId The ID of the user who owns the file (for access control).
     * @return true if the file metadata was deleted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteFileMetadata(int fileId, int userId) throws SQLException {
        String sql = "DELETE FROM files WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean deleted = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fileId);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                deleted = true;
            }
        } finally {
            DBConnection.close(conn, pstmt, null);
        }
        return deleted;
    }
}

