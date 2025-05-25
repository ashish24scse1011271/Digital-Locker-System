package com.digitallocker.gui;

import com.digitallocker.model.FileMetadata;
import com.digitallocker.model.User;
import com.digitallocker.service.FileLockerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * GUI Frame for the Digital Locker Dashboard.
 * Allows users to upload, retrieve, and view their files.
 */
public class LockerDashboardFrame extends JFrame {
    private User currentUser;
    private FileLockerService fileLockerService;

    private JLabel welcomeLabel;
    private JTable fileTable;
    private DefaultTableModel tableModel;
    private JButton uploadButton;
    private JButton retrieveButton;
    private JButton deleteButton;
    private JButton logoutButton;

    public LockerDashboardFrame(User user) {
        super("Digital Locker - Dashboard");
        this.currentUser = user;
        this.fileLockerService = new FileLockerService();
        initComponents();
        setupLayout();
        addListeners();
        loadUserFiles(); // Load files when dashboard opens

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Initial size
        setMinimumSize(new Dimension(700, 500)); // Minimum size for responsiveness
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    private void initComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(0, 100, 0)); // DarkGreen

        // Table for displaying files
        String[] columnNames = {"ID", "Original Filename", "Upload Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        fileTable = new JTable(tableModel);
        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        fileTable.setFont(new Font("Inter", Font.PLAIN, 12));
        fileTable.setRowHeight(25);
        fileTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        fileTable.getTableHeader().setBackground(new Color(200, 230, 200)); // Light green header

        // Buttons
        uploadButton = new JButton("Upload File");
        retrieveButton = new JButton("Retrieve File");
        deleteButton = new JButton("Delete File");
        logoutButton = new JButton("Logout");

        // Enhance button aesthetics
        customizeButton(uploadButton, new Color(30, 144, 255)); // DodgerBlue
        customizeButton(retrieveButton, new Color(255, 140, 0)); // DarkOrange
        customizeButton(deleteButton, new Color(220, 20, 60)); // Crimson
        customizeButton(logoutButton, new Color(105, 105, 105)); // DimGray
    }

    private void customizeButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 255, 240)); // Honeydew

        // Top Panel: Welcome message and Logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(mainPanel.getBackground());
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(mainPanel.getBackground());
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel: File Table
        JScrollPane scrollPane = new JScrollPane(fileTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(150, 200, 150), 1)); // Light green border
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(mainPanel.getBackground());
        buttonPanel.add(uploadButton);
        buttonPanel.add(retrieveButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addListeners() {
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select File to Upload");
                int returnValue = fileChooser.showOpenDialog(LockerDashboardFrame.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (fileLockerService.uploadFile(currentUser.getId(), selectedFile)) {
                        showMessage("File uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUserFiles(); // Refresh file list
                    } else {
                        showMessage("File upload failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = fileTable.getSelectedRow();
                if (selectedRow == -1) {
                    showMessage("Please select a file to retrieve.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int fileId = (int) tableModel.getValueAt(selectedRow, 0); // Get file ID from table

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save File As...");
                fileChooser.setSelectedFile(new File((String) tableModel.getValueAt(selectedRow, 1))); // Suggest original filename
                int returnValue = fileChooser.showSaveDialog(LockerDashboardFrame.this);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File saveLocation = fileChooser.getSelectedFile();
                    if (fileLockerService.retrieveFile(fileId, currentUser.getId(), Paths.get(saveLocation.getAbsolutePath()))) {
                        showMessage("File retrieved successfully to: " + saveLocation.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showMessage("File retrieval failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = fileTable.getSelectedRow();
                if (selectedRow == -1) {
                    showMessage("Please select a file to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(LockerDashboardFrame.this,
                        "Are you sure you want to delete this file?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    int fileId = (int) tableModel.getValueAt(selectedRow, 0);
                    if (fileLockerService.deleteFile(fileId, currentUser.getId())) {
                        showMessage("File deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUserFiles(); // Refresh file list
                    } else {
                        showMessage("File deletion failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(LockerDashboardFrame.this,
                        "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(() -> {
                        new LoginRegisterFrame(); // Go back to login screen
                        dispose(); // Close dashboard
                    });
                }
            }
        });
    }

    /**
     * Loads and displays the current user's files in the table.
     */
    private void loadUserFiles() {
        tableModel.setRowCount(0); // Clear existing rows
        List<FileMetadata> files = fileLockerService.getUserFiles(currentUser.getId());
        for (FileMetadata file : files) {
            tableModel.addRow(new Object[]{file.getId(), file.getOriginalFilename(), file.getUploadDate()});
        }
        if (files.isEmpty()) {
            showMessage("No files found in your locker. Upload one!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Custom message box replacement for JOptionPane.showMessageDialog.
     * This method is a wrapper to ensure consistency and could be extended for custom UI.
     * @param message The message to display.
     * @param title The title of the message box.
     * @param messageType The type of message (e.g., JOptionPane.INFORMATION_MESSAGE).
     */
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Responsiveness: BorderLayout and JScrollPane handle resizing well.
    // Aesthetics: Colors, fonts, and padding for a clean look.
    // Component Placement: Logical grouping of elements.
}
