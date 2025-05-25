package com.digitallocker.service;

import com.digitallocker.dao.FileDAO;
import com.digitallocker.model.FileMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for file storage and retrieval operations.
 */
public class FileLockerService {
    private static final String LOCKER_DIR = "locker_files"; // Directory to store actual files
    private FileDAO fileDAO;

    public FileLockerService() {
        this.fileDAO = new FileDAO();
        // Ensure the locker directory exists
        Path path = Paths.get(LOCKER_DIR);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Created locker directory: " + LOCKER_DIR);
            } catch (IOException e) {
                System.err.println("Failed to create locker directory: " + e.getMessage());
            }
        }
    }

    /**
     * Uploads a file to the locker system.
     * Stores the file on disk and its metadata in the database.
     * @param userId The ID of the user uploading the file.
     * @param sourceFile The File object representing the file to upload.
     * @return true if the file was uploaded successfully, false otherwise.
     */
    public boolean uploadFile(int userId, File sourceFile) {
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.err.println("Source file does not exist or is not a file.");
            return false;
        }

        String originalFilename = sourceFile.getName();
        String storedFilename = UUID.randomUUID().toString(); // Generate a unique filename for storage
        Path destinationPath = Paths.get(LOCKER_DIR, storedFilename);

        try {
            // Copy file to the locker directory
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Save file metadata to the database
            FileMetadata metadata = new FileMetadata(userId, originalFilename, storedFilename);
            int fileId = fileDAO.addFileMetadata(metadata);
            if (fileId != -1) {
                System.out.println("File uploaded and metadata saved: " + originalFilename);
                return true;
            } else {
                // If metadata saving fails, delete the copied file to prevent orphaned files
                Files.deleteIfExists(destinationPath);
                System.err.println("Failed to save file metadata for: " + originalFilename);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error copying file: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Database error during file upload: " + e.getMessage());
            try {
                Files.deleteIfExists(destinationPath); // Clean up file if DB fails
            } catch (IOException ioException) {
                System.err.println("Error cleaning up file after DB error: " + ioException.getMessage());
            }
            return false;
        }
    }

    /**
     * Retrieves a file from the locker system.
     * Copies the stored file to a specified destination.
     * @param fileId The ID of the file to retrieve.
     * @param userId The ID of the user requesting the file (for access control).
     * @param destinationPath The Path where the file should be saved on the user's system.
     * @return true if the file was retrieved successfully, false otherwise.
     */
    public boolean retrieveFile(int fileId, int userId, Path destinationPath) {
        try {
            FileMetadata metadata = fileDAO.getFileByIdAndUserId(fileId, userId);
            if (metadata == null) {
                System.err.println("File not found or not owned by user.");
                return false;
            }

            Path sourcePath = Paths.get(LOCKER_DIR, metadata.getStoredFilename());
            if (!Files.exists(sourcePath)) {
                System.err.println("Stored file not found on disk: " + metadata.getStoredFilename());
                return false;
            }

            // Ensure the destination directory exists
            if (destinationPath.getParent() != null) {
                Files.createDirectories(destinationPath.getParent());
            }

            // Copy file to the destination
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File retrieved successfully: " + metadata.getOriginalFilename());
            return true;
        } catch (SQLException e) {
            System.err.println("Database error during file retrieval: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error copying file during retrieval: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a file from the locker system.
     * Deletes the file from disk and its metadata from the database.
     * @param fileId The ID of the file to delete.
     * @param userId The ID of the user requesting the deletion (for access control).
     * @return true if the file was deleted successfully, false otherwise.
     */
    public boolean deleteFile(int fileId, int userId) {
        try {
            FileMetadata metadata = fileDAO.getFileByIdAndUserId(fileId, userId);
            if (metadata == null) {
                System.err.println("File not found or not owned by user for deletion.");
                return false;
            }

            Path filePath = Paths.get(LOCKER_DIR, metadata.getStoredFilename());
            boolean fileDeleted = Files.deleteIfExists(filePath);

            boolean metadataDeleted = fileDAO.deleteFileMetadata(fileId, userId);

            if (fileDeleted && metadataDeleted) {
                System.out.println("File and metadata deleted successfully: " + metadata.getOriginalFilename());
                return true;
            } else {
                System.err.println("Failed to delete file or metadata fully for: " + metadata.getOriginalFilename());
                // Consider rollback if one operation succeeds and the other fails
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database error during file deletion: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error deleting file from disk: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets a list of all files for a given user.
     * @param userId The ID of the user.
     * @return A list of FileMetadata objects.
     */
    public List<FileMetadata> getUserFiles(int userId) {
        try {
            return fileDAO.getFilesByUserId(userId);
        } catch (SQLException e) {
            System.err.println("Database error getting user files: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
}
