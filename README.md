🔐 Digital Locker System 🔒
===========================

Secure Your Digital Assets with Ease!
-------------------------------------

Welcome to the Digital Locker System, a robust and intuitive desktop application designed to provide users with a secure and personal space for storing and managing their valuable digital files. Built with Java Swing and backed by a MySQL database, this system ensures that your files are organized, protected, and accessible only by you.

✨ Features at a Glance
----------------------

Our Digital Locker System empowers you with essential file management capabilities:

*   **User Authentication System:**
    
    *   **🔑 Secure Registration:** Create your unique digital locker account with a custom username and password.
        
    *   **✅ Seamless Login:** Access your personalized file vault with ease using your credentials.
        
*   **Comprehensive File Management:**
    
    *   **⬆️ Effortless File Uploads:** Directly upload files from your local machine into your secure locker.
        
    *   **⬇️ Quick File Retrieval:** Download your stored files back to your local system whenever needed.
        
    *   **🗑️ Intuitive File Deletion:** Remove files from your locker with a simple click, maintaining control over your storage.
        
*   **🔒 Robust Access Control:** Each user's files are meticulously segregated and accessible only by the respective account holder, ensuring privacy and data integrity.
    
*   **🖥️ User-Friendly GUI:** Navigate and interact with the system through a clean, responsive, and aesthetically pleasing graphical interface built with Java Swing.
    

🚀 Technologies Used
--------------------

This project leverages a powerful combination of technologies to deliver a reliable and interactive experience:

*   **Core Logic:** Java (JDK 8+)
    
*   **Database Management:** MySQL
    
*   **Database Connectivity:** JDBC (Java Database Connectivity)
    
*   **Build Automation:** Maven (managed via pom.xml)
    
*   **User Interface:** Java Swing
    
*   **File Operations:** Java's java.io and java.nio.file packages for secure file handling.
    

🛠️ Getting Started
-------------------

Follow these steps to set up and run the Digital Locker System on your local machine.

### Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK):** Version 8 or higher.
    
*   **MySQL Server:** A running instance of MySQL.
    
*   **MySQL JDBC Driver (Connector/J):** This will be managed by Maven, but ensure compatibility.
    
*   **Maven:** If you plan to build the project using Maven.
    
*   **An IDE:** (Recommended) IntelliJ IDEA, Eclipse, or VS Code with Java extensions.
    
    *   **For VS Code:** Install the Extension Pack for Java which includes Maven for Java.
        

### Database Setup

1.  **Connect to MySQL:**Open your preferred MySQL client (e.g., MySQL Workbench, command line, DBeaver) and connect to your MySQL server.
    
2.  CREATE DATABASE IF NOT EXISTS digitallocker;USE digitallocker;CREATE TABLE IF NOT EXISTS users ( id INT AUTO\_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, -- IMPORTANT: In a real app, hash passwords (e.g., using BCrypt) created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP);CREATE TABLE IF NOT EXISTS files ( id INT AUTO\_INCREMENT PRIMARY KEY, user\_id INT NOT NULL, original\_filename VARCHAR(255) NOT NULL, stored\_filename VARCHAR(255) NOT NULL UNIQUE, -- UUID for security and uniqueness upload\_date TIMESTAMP DEFAULT CURRENT\_TIMESTAMP, FOREIGN KEY (user\_id) REFERENCES users(id) ON DELETE CASCADE);
    
3.  public class DBConnection { private static final String JDBC\_URL = "jdbc:mysql://localhost:3306/digitallocker?useSSL=false&serverTimezone=UTC"; private static final String USER = "your\_mysql\_username"; // ✨ UPDATE THIS ✨ private static final String PASSWORD = "your\_mysql\_password"; // ✨ UPDATE THIS ✨ // ... rest of the code}
    

Project Setup (Maven)
---------------------

1.  git clone https://github.com/your-username/digital-locker-system.git # Replace with your actual repo URLcd digital-locker-system
    
2.  **Import into Your IDE:**
    
    *   **IntelliJ IDEA:** Open IntelliJ, select "File" -> "Open" -> navigate to the digital-locker-system directory -> select pom.xml -> click "Open as Project". IntelliJ will automatically detect it as a Maven project and download dependencies.
        
    *   **Eclipse:** Open Eclipse, select "File" -> "Import" -> "Maven" -> "Existing Maven Projects" -> Browse to your digital-locker-system directory -> Click "Finish".
        
    *   **VS Code:**
        
        1.  Open VS Code.
            
        2.  Go to "File" -> "Open Folder..." and select the digital-locker-system directory.
            
        3.  VS Code, with the Java extensions, should automatically detect the Maven project and configure it. You might see a prompt to import Maven projects; confirm it.
            
3.  mvn clean installThis command will compile the source code, run any tests (if available), and package the application into a runnable JAR file, including a "fat JAR" with all its dependencies in the target/ directory.
    
4.  **Verify locker\_files Directory:**A directory named locker\_files will be automatically created in your project's root directory (next to src/ and pom.xml) the first time the application runs. This directory is where the actual uploaded files will be stored.
    

How to Run the Application
--------------------------

There are two primary ways to run the application:

1.  **From your IDE:**
    
    *   **IntelliJ IDEA / Eclipse:**
        
        *   Navigate to src/main/java/com/digitallocker/MainApp.java.
            
        *   Right-click on MainApp.java and select "Run 'MainApp.main()'".
            
    *   **VS Code:**
        
        *   Open the MainApp.java file in the editor.
            
        *   You should see a "Run" button (green triangle) near the main method declaration or at the top right of the editor. Click this button.
            
        *   Alternatively, open the "Run and Debug" view (Ctrl+Shift+D) and click "Run Java".
            
2.  cd targetjava -jar digital-locker-system-1.0-SNAPSHOT-all.jar # The exact name might vary slightlyUpon successful execution, the "Digital Locker - Login/Register" GUI window will appear, ready for use!
    

📂 Project Structure
--------------------

The project is organized following a standard Maven layout and a layered architecture to ensure modularity and maintainability:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   digital-locker-system/  ├── src/  │   ├── main/  │   │   ├── java/  │   │   │   ├── com/  │   │   │   │   ├── digitallocker/  │   │   │   │   │   ├── model/         # Data structures (User, FileMetadata)  │   │   │   │   │   ├── dao/           # Data Access Objects (UserDAO, FileDAO) - Database interaction  │   │   │   │   │   ├── service/       # Business logic (AuthService, FileLockerService)  │   │   │   │   │   ├── util/          # Utility classes (DBConnection)  │   │   │   │   │   ├── gui/           # Graphical User Interface (LoginRegisterFrame, LockerDashboardFrame)  │   │   │   │   │   └── MainApp.java   # Application entry point  ├── target/                        # Compiled classes and JAR files (generated by Maven)  ├── lib/                           # (Optional) For manual JDBC driver placement  ├── locker_files/                  # Actual files are stored here (created on first run)  └── pom.xml                        # Maven Project Object Model file   `

### Code Architecture

The application adheres to a layered architecture for clear separation of concerns:

*   **Model Layer (model/):** Contains Plain Old Java Objects (POJOs) like User and FileMetadata that represent the data entities in the application.
    
*   **DAO Layer (dao/):** Data Access Objects (UserDAO, FileDAO) encapsulate all logic for interacting with the MySQL database. They abstract the complexities of JDBC from the business logic.
    
*   **Service Layer (service/):** Houses the core business logic (AuthService, FileLockerService). These services orchestrate calls to DAOs and handle complex operations, ensuring data integrity and business rules.
    
*   **Utility Layer (util/):** Provides common helper classes, such as DBConnection for managing database connections.
    
*   **GUI Layer (gui/):** Contains all the Java Swing components (JFrame, JPanel, JButton, etc.) that form the user interface, handling user input and displaying information.
    
*   **Main Application (MainApp.java):** The entry point that initializes the GUI and starts the application.
    

⚠️ Important Security Notes ⚠️
------------------------------

**Please be aware:** This project is developed primarily for **educational and demonstrative purposes**. While it provides core functionalities, it contains **critical security vulnerabilities** that **MUST be addressed** before considering it for any production or sensitive environment.

*   **Plain Text Passwords:** Currently, user passwords are stored as plain text directly in the database. **This is highly insecure and unacceptable for any real-world application.**
    
    *   **🚫 DO NOT USE IN PRODUCTION.**
        
    *   **✅ SOLUTION:** Implement strong, one-way password hashing using industry-standard algorithms like **BCrypt**, **PBKDF2**, or **Argon2**. Libraries like Spring Security's BCryptPasswordEncoder are excellent choices.
        
*   **Unencrypted Stored Files:** The actual files uploaded to the locker\_files directory are stored without encryption.
    
    *   **🚫 EXPOSES SENSITIVE DATA.**
        
    *   **✅ SOLUTION:** Implement robust **file encryption** (e.g., AES encryption) before storing files on disk. Decrypt files only when a legitimate user requests retrieval. Securely manage encryption keys.
        
*   **Basic Error Handling:** The current error handling is simplistic, primarily printing to the console or showing basic dialogs.
    
    *   **✅ SOLUTION:** Implement comprehensive error logging (e.g., using Log4j, SLF4J) and provide more user-friendly, informative error messages.
        
*   **Input Validation:** While PreparedStatement mitigates SQL injection, extensive input validation on all user inputs is crucial to prevent various attack vectors and ensure data quality.
    

💡 Future Enhancements
----------------------

We envision several exciting improvements for the Digital Locker System:

*   **Enhanced Security:**
    
    *   🚀 Implement robust password hashing (BCrypt, PBKDF2).
        
    *   🔒 Add file encryption/decryption for all stored files.
        
*   **Advanced Features:**
    
    *   📂 Introduce file type restrictions for uploads.
        
    *   📏 Implement file size limits to manage storage effectively.
        
    *   ✏️ Allow users to rename or move files within their locker.
        
    *   🔍 Implement search functionality for quick file discovery.
        
    *   🗑️ Add a "recycle bin" or soft-delete feature for files.
        
*   **User Experience:**
    
    *   🎨 Improve UI/UX with more modern Swing libraries or consider migrating to a web-based interface for broader accessibility.
        
    *   🌟 Introduce smooth transitions and animations for a more dynamic feel.
        
*   **System Robustness:**
    
    *   📊 Implement comprehensive logging using frameworks like Log4j or SLF4J.
        
    *   ⚙️ Add more sophisticated error handling and recovery mechanisms.
        
*   **Account Management:**
    
    *   🔑 Implement "Forgot Password" functionality.
        
    *   📝 Allow users to update their profile information.
        

👋 Contributing
---------------

Contributions are welcome! If you have suggestions for improvements or new features, please feel free to:

1.  Fork the repository.
    
2.  Create a new branch (git checkout -b feature/YourFeature).
    
3.  Make your changes.
    
4.  Commit your changes (git commit -m 'Add Your Feature').
    
5.  Push to the branch (git push origin feature/YourFeature).
    
6.  Open a Pull Request.
    

**Thank you for exploring the Digital Locker System!**
