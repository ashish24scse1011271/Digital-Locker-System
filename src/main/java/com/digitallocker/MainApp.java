package com.digitallocker;

import com.digitallocker.gui.LoginRegisterFrame;

import javax.swing.*;

/*
  Main application class for the Digital Locker System.
  This class serves as the entry point for the GUI application.
 */
public class MainApp {
    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginRegisterFrame();
            }
        });
    }
}

