# Digital-Locker-System
A secure digital locker application developed in Java Swing with MySQL database integration for user authentication and file metadata management, and file system handling for actual file storage. This system allows users to securely register, log in, upload, retrieve, and delete their personal files.

Table of Contents
Features

Technologies Used

Prerequisites

Database Setup

Project Setup (Maven)

How to Run the Application

Important Security Notes

Future Enhancements

Features
User Registration (Sign Up): New users can create an account with a unique username and password.

User Login: Registered users can log in to access their personal locker.

File Upload: Users can upload files from their local system to their secure digital locker.

File Retrieval: Users can download their stored files back to their local system.

File Deletion: Users can remove files from their locker.

Access Control: Files are accessible only by the user who uploaded them.

Simple GUI: An intuitive graphical user interface built with Java Swing.

Technologies Used
Backend: Java (JDK 8+)

Database: MySQL

Database Connectivity: JDBC (Java Database Connectivity)

Build Tool: Maven (configured with pom.xml)

UI: Java Swing

File Handling: Java's java.io and java.nio.file packages

Prerequisites
Before you begin, ensure you have the following installed:

Java Development Kit (JDK): Version 8 or higher.

Download JDK

MySQL Server: A running instance of MySQL.

Download MySQL Community Server

MySQL JDBC Driver (Connector/J): This will be managed by Maven, but ensure compatibility.

Maven: If you plan to build the project using Maven.

Download Maven

An IDE: (Recommended) IntelliJ IDEA, Eclipse, or VS Code with Java extensions.

Database Setup
Create the Database:
Open your MySQL client (e.g., MySQL Workbench, command line, DBeaver) and execute the following SQL commands to create the database and tables:

CREATE DATABASE IF NOT EXISTS digitallocker;

USE digitallocker;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- In a real app, store hashed passwords (e.g., using BCrypt)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL UNIQUE, -- UUID for security and uniqueness
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

Update Database Credentials:
Open the src/main/java/com/digitallocker/util/DBConnection.java file and update the USER and PASSWORD constants with your MySQL database credentials:

public class DBConnection {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/digitallocker?useSSL=false&serverTimezone=UTC";
    private static final String USER = "your_mysql_username"; // <-- UPDATE THIS
    private static final String PASSWORD = "your_mysql_password"; // <-- UPDATE THIS
    // ... rest of the code
}

Project Setup (Maven)
Clone the Repository:

git clone <your-repository-url>
cd digital-locker-system

Import into IDE:

IntelliJ IDEA: Open -> Navigate to the digital-locker-system directory -> Select pom.xml -> Open as Project.

Eclipse: File -> Import -> Maven -> Existing Maven Projects -> Browse to digital-locker-system -> Finish.

Build the Project:
Once imported, your IDE should automatically download the necessary Maven dependencies (including mysql-connector-java). If not, you can manually build using Maven:

mvn clean install

This command will compile the code, run tests, and package the application into a JAR file (including a "fat JAR" with all dependencies).

Create locker_files Directory:
A directory named locker_files will be automatically created in your project's root directory (where pom.xml is located) when the application runs for the first time. This directory will store the actual uploaded files.

How to Run the Application
There are two primary ways to run the application:

From your IDE:

Navigate to src/main/java/com/digitallocker/MainApp.java.

Right-click on MainApp.java and select "Run 'MainApp.main()'".

From the command line (using the fat JAR):
After building the project with mvn clean install, a "fat JAR" (containing all dependencies) will be created in the target/ directory. Its name will be similar to digital-locker-system-1.0-SNAPSHOT-all.jar.

cd target
java -jar digital-locker-system-1.0-SNAPSHOT-all.jar

Upon running, the "Digital Locker - Login/Register" GUI window will appear.

Important Security Notes
❗ WARNING: This project is for educational purposes and demonstrates core functionalities. It includes significant security vulnerabilities that MUST be addressed for any production-ready application.

Plain Text Passwords: Passwords are currently stored as plain text in the database. This is highly insecure. For a production application, you must implement strong password hashing using libraries like BCrypt or PBKDF2.

File Encryption: The actual files stored in the locker_files directory are not encrypted. For true data security, implement robust file encryption (e.g., AES encryption) before storing files on disk and decryption upon retrieval.

Error Handling: Error handling is basic. A production system requires comprehensive logging and user-friendly error messages.

Input Validation: While some basic checks are present, thorough input validation on all user inputs is crucial to prevent various attacks (e.g., XSS, SQL injection beyond PreparedStatement).

Future Enhancements
Implement strong password hashing (e.g., BCrypt).

Add file encryption/decryption for stored files.

Implement more robust input validation for all user inputs.

Add file type restrictions for uploads.

Implement file size limits.

Improve UI/UX with more modern Swing libraries or switch to a web-based interface.

Add logging using a framework like Log4j or SLF4J.

Implement "Forgot Password" functionality.

Allow users to rename or move files within the locker.

Add search functionality for files.
