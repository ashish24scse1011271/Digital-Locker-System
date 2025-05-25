package com.digitallocker.service;

import com.digitallocker.dao.UserDAO;
import com.digitallocker.model.User;

import java.sql.SQLException;

/**
 * Service layer for user authentication and registration.
 */
public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Registers a new user.
     * @param username The desired username.
     * @param password The desired password.
     * @return The registered User object if successful, null if username already exists or an error occurs.
     */
    public User register(String username, String password) {
        try {
            if (userDAO.doesUsernameExist(username)) {
                System.out.println("Registration failed: Username already exists.");
                return null;
            }
            User newUser = new User(username, password);
            int userId = userDAO.registerUser(newUser);
            if (userId != -1) {
                newUser.setId(userId);
                return newUser;
            }
        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
        }
        return null;
    }

    /**
     * Authenticates a user.
     * @param username The username.
     * @param password The password.
     * @return The authenticated User object if successful, null otherwise.
     */
    public User login(String username, String password) {
        try {
            User user = userDAO.getUserByUsernameAndPassword(username, password);
            if (user != null) {
                System.out.println("Login successful for user: " + username);
                return user;
            } else {
                System.out.println("Login failed: Invalid username or password.");
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
        }
        return null;
    }
}
