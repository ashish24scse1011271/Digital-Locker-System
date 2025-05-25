package com.digitallocker.model;

import java.sql.Timestamp;

/**
 * Represents metadata for a file stored in the digital locker.
 */
public class FileMetadata {
    private int id;
    private int userId;
    private String originalFilename;
    private String storedFilename; // The actual filename on disk (e.g., a UUID)
    private Timestamp uploadDate;

    public FileMetadata(int id, int userId, String originalFilename, String storedFilename, Timestamp uploadDate) {
        this.id = id;
        this.userId = userId;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.uploadDate = uploadDate;
    }

    public FileMetadata(int userId, String originalFilename, String storedFilename) {
        this.userId = userId;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public Timestamp getUploadDate() {
        return uploadDate;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }
}

