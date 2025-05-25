import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

// Data Models
class User {
    private String username;
    private String passwordHash;
    private String email;
    private LocalDateTime createdAt;
    
    public User(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

class SecureFile {
    private String fileName;
    private String originalFileName;
    private byte[] content;
    private String fileType;
    private long fileSize;
    private String owner;
    private LocalDateTime uploadedAt;
    private String checksum;
    
    public SecureFile(String fileName, byte[] content, String owner) {
        this.fileName = fileName;
        this.originalFileName = fileName;
        this.content = content;
        this.owner = owner;
        this.fileSize = content.length;
        this.uploadedAt = LocalDateTime.now();
        this.checksum = calculateChecksum(content);
    }
    
    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "unknown";
        }
    }
    
    // Getters and setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public byte[] getContent() { return content; }
    public void setContent(byte[] content) { this.content = content; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public String getChecksum() { return checksum; }
}

// Services
class UserService {
    private Map<String, User> users = new ConcurrentHashMap<>();
    private final String USERS_FILE = "users.dat";
    
    public UserService() {
        loadUsers();
    }
    
    public boolean registerUser(String username, String password, String email) {
        if (users.containsKey(username)) {
            return false;
        }
        
        String passwordHash = hashPassword(password);
        User user = new User(username, passwordHash, email);
        users.put(username, user);
        saveUsers();
        return true;
    }
    
    public User authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
            return user;
        }
        return null;
    }
    
    public int getTotalUsers() {
        return users.size();
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback (not recommended for production)
        }
    }
    
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            // File doesn't exist or is corrupted, start with empty users
            users = new ConcurrentHashMap<>();
        }
    }
    
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
}

class FileService {
    private Map<String, java.util.List<SecureFile>> userFiles = new ConcurrentHashMap<>();
    private final String SECURE_DIR = "./secure_files/";
    
    public FileService() {
        createSecureDirectory();
        loadFiles();
    }
    
    private void createSecureDirectory() {
        try {
            Files.createDirectories(Paths.get(SECURE_DIR));
        } catch (IOException e) {
            System.err.println("Error creating secure directory: " + e.getMessage());
        }
    }
    
    public boolean storeSecureFile(SecureFile fileData) {
        try {
            String filePath = SECURE_DIR + fileData.getOwner() + "_" + fileData.getFileName();
            Files.write(Paths.get(filePath), fileData.getContent());
            
            userFiles.computeIfAbsent(fileData.getOwner(), k -> new ArrayList<>()).add(fileData);
            saveFileIndex();
            return true;
        } catch (IOException e) {
            System.err.println("Error storing file: " + e.getMessage());
            return false;
        }
    }
    
    public SecureFile retrieveSecureFile(String fileName, String username) {
        java.util.List<SecureFile> files = userFiles.get(username);
        if (files != null) {
            for (SecureFile file : files) {
                if (file.getFileName().equals(fileName)) {
                    return file;
                }
            }
        }
        return null;
    }
    
    public boolean downloadFile(String fileName, String username, String downloadPath) {
        SecureFile file = retrieveSecureFile(fileName, username);
        if (file != null) {
            try {
                String fullPath = downloadPath + File.separator + file.getOriginalFileName();
                Files.write(Paths.get(fullPath), file.getContent());
                return true;
            } catch (IOException e) {
                System.err.println("Error downloading file: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public java.util.List<SecureFile> getUserFiles(String username) {
        return userFiles.getOrDefault(username, new ArrayList<>());
    }
    
    public boolean deleteSecureFile(String fileName, String username) {
        java.util.List<SecureFile> files = userFiles.get(username);
        if (files != null) {
            SecureFile toRemove = null;
            for (SecureFile file : files) {
                if (file.getFileName().equals(fileName)) {
                    toRemove = file;
                    break;
                }
            }
            if (toRemove != null) {
                files.remove(toRemove);
                try {
                    String filePath = SECURE_DIR + username + "_" + fileName;
                    Files.deleteIfExists(Paths.get(filePath));
                    saveFileIndex();
                    return true;
                } catch (IOException e) {
                    System.err.println("Error deleting file: " + e.getMessage());
                }
            }
        }
        return false;
    }
    
    private void loadFiles() {
        // Implementation for loading file index from disk
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("file_index.dat"))) {
            userFiles = (Map<String, java.util.List<SecureFile>>) ois.readObject();
        } catch (Exception e) {
            userFiles = new ConcurrentHashMap<>();
        }
    }
    
    private void saveFileIndex() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("file_index.dat"))) {
            oos.writeObject(userFiles);
        } catch (IOException e) {
            System.err.println("Error saving file index: " + e.getMessage());
        }
    }
}

// Main Application Class
public class DigitalLockerSystem {
    private UserService userService;
    private FileService fileService;
    private Scanner scanner;
    private User currentUser;
    
    public DigitalLockerSystem() {
        userService = new UserService();
        fileService = new FileService();
        scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        DigitalLockerSystem system = new DigitalLockerSystem();
        
        if (args.length > 0 && args[0].equals("gui")) {
            system.launchGUI();
        } else {
            system.runConsoleInterface();
        }
    }
    
    public void runConsoleInterface() {
        displayWelcome();
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
    
    private void showLoginMenu() {
        System.out.println("\n=== LOGIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        
        int choice = getValidChoice(1, 3);
        
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegistration();
                break;
            case 3:
                System.out.println("Thank you for using Digital Locker System!");
                System.exit(0);
                break;
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        System.out.println("1. Store File");
        System.out.println("2. Retrieve File");
        System.out.println("3. Download File");
        System.out.println("4. List Files");
        System.out.println("5. Delete File");
        System.out.println("6. Logout");
        
        int choice = getValidChoice(1, 6);
        
        switch (choice) {
            case 1:
                handleStoreFile();
                break;
            case 2:
                handleRetrieveFile();
                break;
            case 3:
                handleDownloadFile();
                break;
            case 4:
                handleListFiles();
                break;
            case 5:
                handleDeleteFile();
                break;
            case 6:
                currentUser = null;
                System.out.println("Logged out successfully!");
                break;
        }
    }
    
    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        User user = userService.authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials!");
        }
    }
    
    private void handleRegistration() {
        System.out.print("Choose username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        
        if (userService.registerUser(username, password, email)) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Username already exists!");
        }
    }
    
    private void handleStoreFile() {
        System.out.print("Enter file name (with extension): ");
        String fileName = scanner.nextLine().trim();
        
        if (fileName.isEmpty()) {
            System.out.println("File name cannot be empty!");
            return;
        }
        
        System.out.println("Enter content (type 'END' on a new line to finish):");
        StringBuilder content = new StringBuilder();
        String line;
        
        while (!(line = scanner.nextLine()).equals("END")) {
            content.append(line).append("\n");
        }
        
        SecureFile fileData = new SecureFile(fileName, content.toString().getBytes(), currentUser.getUsername());
        fileData.setFileSize(content.length());
        fileData.setFileType(getFileExtension(fileName));
        
        if (fileService.storeSecureFile(fileData)) {
            System.out.println("File stored successfully!");
        } else {
            System.out.println("Error storing file!");
        }
    }
    
    private void handleRetrieveFile() {
        System.out.print("Enter file name: ");
        String fileName = scanner.nextLine().trim();
        
        SecureFile fileData = fileService.retrieveSecureFile(fileName, currentUser.getUsername());
        
        if (fileData != null) {
            System.out.println("\n=== FILE DETAILS ===");
            System.out.println("File Name: " + fileData.getFileName());
            System.out.println("File Type: " + fileData.getFileType());
            System.out.println("File Size: " + formatFileSize(fileData.getFileSize()));
            System.out.println("Upload Date: " + fileData.getUploadedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("Checksum: " + fileData.getChecksum());
            System.out.println("\n=== FILE CONTENT ===");
            System.out.println(new String(fileData.getContent()));
        } else {
            System.out.println("File not found!");
        }
    }
    
    private void handleDownloadFile() {
        System.out.print("Enter file name to download: ");
        String fileName = scanner.nextLine().trim();
        
        System.out.print("Enter download path (or press Enter for current directory): ");
        String downloadPath = scanner.nextLine().trim();
        
        if (downloadPath.isEmpty()) {
            downloadPath = ".";
        }
        
        SecureFile fileData = fileService.retrieveSecureFile(fileName, currentUser.getUsername());
        if (fileData != null) {
            if (fileService.downloadFile(fileName, currentUser.getUsername(), downloadPath)) {
                System.out.println("File downloaded successfully to: " + downloadPath);
            } else {
                System.out.println("Error downloading file!");
            }
        } else {
            System.out.println("File not found!");
        }
    }
    
    private void handleListFiles() {
        java.util.List<SecureFile> files = fileService.getUserFiles(currentUser.getUsername());
        
        if (files.isEmpty()) {
            System.out.println("No files found!");
            return;
        }
        
        System.out.println("\n=== YOUR FILES ===");
        System.out.printf("%-20s %-10s %-15s %-20s%n", "File Name", "Type", "Size", "Upload Date");
        System.out.println("-".repeat(65));
        
        for (SecureFile file : files) {
            System.out.printf("%-20s %-10s %-15s %-20s%n",
                file.getFileName(),
                file.getFileType(),
                formatFileSize(file.getFileSize()),
                file.getUploadedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        }
    }
    
    private void handleDeleteFile() {
        System.out.print("Enter file name to delete: ");
        String fileName = scanner.nextLine().trim();
        
        if (fileService.deleteSecureFile(fileName, currentUser.getUsername())) {
            System.out.println("File deleted successfully!");
        } else {
            System.out.println("Error deleting file or file not found!");
        }
    }
    
    private void displayWelcome() {
        System.out.println("Digital Locker System v2.0");
        System.out.println("Secure file storage supporting all file types");
        System.out.println("Features: Store, Retrieve, Download, Delete files");
        System.out.println("Interfaces: Console & GUI");
        System.out.println("Total registered users: " + userService.getTotalUsers());
        System.out.println("Storage location: ./secure_files/");
    }
    
    private int getValidChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("Enter your choice (" + min + "-" + max + "): ");
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "unknown";
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    // GUI Implementation
    public void launchGUI() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            new DigitalLockerGUI(this);
        });
    }
    
    // Getters for GUI access
    public UserService getUserService() { return userService; }
    public FileService getFileService() { return fileService; }
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
}

// Simple GUI Implementation
class DigitalLockerGUI extends JFrame {
    private DigitalLockerSystem system;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    public DigitalLockerGUI(DigitalLockerSystem system) {
        this.system = system;
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Digital Locker System v2.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Add panels
        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createMainPanel(), "MAIN");
        
        add(cardPanel);
        setVisible(true);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel titleLabel = new JLabel("Digital Locker System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        
        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(loginButton, gbc);
        gbc.gridx = 1;
        panel.add(registerButton, gbc);
        
        // Event handlers
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            User user = system.getUserService().authenticateUser(username, password);
            if (user != null) {
                system.setCurrentUser(user);
                cardLayout.show(cardPanel, "MAIN");
                usernameField.setText("");
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome to Digital Locker System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton storeButton = new JButton("Store File");
        JButton retrieveButton = new JButton("Retrieve File");
        JButton downloadButton = new JButton("Download File");
        JButton listButton = new JButton("List Files");
        JButton deleteButton = new JButton("Delete File");
        JButton logoutButton = new JButton("Logout");
        
        buttonPanel.add(storeButton);
        buttonPanel.add(retrieveButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(listButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton);
        
        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        // Event handlers
        logoutButton.addActionListener(e -> {
            system.setCurrentUser(null);
            cardLayout.show(cardPanel, "LOGIN");
        });
        
        listButton.addActionListener(e -> {
            if (system.getCurrentUser() != null) {
                java.util.List<SecureFile> files = system.getFileService().getUserFiles(system.getCurrentUser().getUsername());
                if (files.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No files found!", "Information", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder("Your Files:\n\n");
                    for (SecureFile file : files) {
                        sb.append(String.format("Name: %s | Type: %s | Size: %s\n", 
                            file.getFileName(), file.getFileType(), formatFileSize(file.getFileSize())));
                    }
                    JOptionPane.showMessageDialog(this, sb.toString(), "File List", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}