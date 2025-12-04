package com.myemailapp;

import com.myemailapp.gui.EmailAppGUI;
import com.myemailapp.startup.StartupManager;
import com.myemailapp.startup.DesktopIconManager;
import javax.swing.*;

/**
 * Main entry point for the AI-powered Email Application
 * Designed for elderly users with simple, voice-controlled interface
 */
public class Main {
    
    public static void main(String[] args) {
        // Set system properties for better GUI experience
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Enable auto-startup on first run if not already configured
        StartupManager.enableAutoStartup();
        
        // Create desktop icon on first run
        if (!DesktopIconManager.desktopIconExists()) {
            DesktopIconManager.createDesktopIcon();
        }
        
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Use system look and feel for familiarity
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                EmailAppGUI gui = new EmailAppGUI();
                gui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
