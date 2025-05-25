package com.digitallocker.gui;

import com.digitallocker.model.User;
import com.digitallocker.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI Frame for user login and registration.
 * Provides a responsive and aesthetically pleasing interface.
 */
public class LoginRegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;

    private AuthService authService;

    public LoginRegisterFrame() {
        super("Digital Locker - Login/Register");
        authService = new AuthService();
        initComponents();
        setupLayout();
        addListeners();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300); // Initial size
        setMinimumSize(new Dimension(350, 250)); // Minimum size for responsiveness
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        messageLabel = new JLabel("Welcome! Please Login or Register.", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLUE); // Initial message color

        // Enhance button aesthetics
        loginButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Inter", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 139, 87), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Inter", Font.BOLD, 14));
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(65, 105, 225), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Rounded corners for buttons (requires custom UI delegate or drawing)
        // For simplicity, using standard JButton appearance with color/border
        // For true rounded corners, you'd extend JButton and override paintComponent.
    }

    private void setupLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255)); // AliceBlue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Digital Locker System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112)); // MidnightBlue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Message Label
        gbc.gridy = 1;
        messageLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        panel.add(messageLabel, gbc);

        // Username Label
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        panel.add(userLabel, gbc);

        // Username Field
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        panel.add(passLabel, gbc);

        // Password Field
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Do not stretch buttons
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);
    }

    private void addListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                User user = authService.login(username, password);
                if (user != null) {
                    messageLabel.setText("Login successful!");
                    messageLabel.setForeground(new Color(34, 139, 34)); // ForestGreen
                    JOptionPane.showMessageDialog(LoginRegisterFrame.this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Open Locker Dashboard
                    SwingUtilities.invokeLater(() -> {
                        new LockerDashboardFrame(user);
                        dispose(); // Close login frame
                    });
                } else {
                    messageLabel.setText("Login failed: Invalid credentials.");
                    messageLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(LoginRegisterFrame.this, "Login Failed: Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Username and Password cannot be empty.");
                    messageLabel.setForeground(Color.ORANGE);
                    JOptionPane.showMessageDialog(LoginRegisterFrame.this, "Username and Password cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                User newUser = authService.register(username, password);
                if (newUser != null) {
                    messageLabel.setText("Registration successful! Please login.");
                    messageLabel.setForeground(new Color(34, 139, 34)); // ForestGreen
                    JOptionPane.showMessageDialog(LoginRegisterFrame.this, "Registration Successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    usernameField.setText("");
                    passwordField.setText("");
                } else {
                    messageLabel.setText("Registration failed: Username might exist.");
                    messageLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(LoginRegisterFrame.this, "Registration Failed: Username might exist or an error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Basic responsiveness: Components will resize with the frame due to GridBagLayout's fill property.
    // Aesthetics: Use of colors, fonts, borders for a cleaner look.
    // Accessibility: Standard Swing components are generally accessible. Labels are associated with fields.
}