package com.myemailapp.startup;

import java.io.*;
import java.nio.file.*;

/**
 * Manages auto-startup configuration for different operating systems
 */
public class StartupManager {
    
    /**
     * Enable auto-startup based on the operating system
     */
    public static void enableAutoStartup() {
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.contains("win")) {
                enableWindowsStartup();
            } else if (os.contains("mac")) {
                enableMacStartup();
            } else if (os.contains("nix") || os.contains("nux")) {
                enableLinuxStartup();
            }
        } catch (Exception e) {
            System.err.println("Could not enable auto-startup: " + e.getMessage());
        }
    }
    
    /**
     * Enable startup on Windows by creating a registry entry
     */
    private static void enableWindowsStartup() throws IOException {
        String appPath = getJarPath();
        String registryKey = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
        String command = String.format(
            "reg add \"%s\" /v \"AIEmailApp\" /t REG_SZ /d \"\\\"%s\\\"\" /f",
            registryKey, appPath
        );
        
        Runtime.getRuntime().exec(command);
    }
    
    /**
     * Enable startup on macOS by creating a launch agent plist
     */
    private static void enableMacStartup() throws IOException {
        String homeDir = System.getProperty("user.home");
        String launchAgentsDir = homeDir + "/Library/LaunchAgents";
        String plistPath = launchAgentsDir + "/com.myemailapp.plist";
        
        Files.createDirectories(Paths.get(launchAgentsDir));
        
        String plistContent = String.format(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" " +
            "\"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<dict>\n" +
            "    <key>Label</key>\n" +
            "    <string>com.myemailapp</string>\n" +
            "    <key>ProgramArguments</key>\n" +
            "    <array>\n" +
            "        <string>java</string>\n" +
            "        <string>-jar</string>\n" +
            "        <string>%s</string>\n" +
            "    </array>\n" +
            "    <key>RunAtLoad</key>\n" +
            "    <true/>\n" +
            "</dict>\n" +
            "</plist>",
            getJarPath()
        );
        
        Files.write(Paths.get(plistPath), plistContent.getBytes());
    }
    
    /**
     * Enable startup on Linux by creating a .desktop file
     */
    private static void enableLinuxStartup() throws IOException {
        String homeDir = System.getProperty("user.home");
        String autostartDir = homeDir + "/.config/autostart";
        String desktopPath = autostartDir + "/ai-email-app.desktop";
        
        Files.createDirectories(Paths.get(autostartDir));
        
        String desktopContent = String.format(
            "[Desktop Entry]\n" +
            "Type=Application\n" +
            "Name=AI Email App\n" +
            "Exec=java -jar %s\n" +
            "Hidden=false\n" +
            "NoDisplay=false\n" +
            "X-GNOME-Autostart-enabled=true\n",
            getJarPath()
        );
        
        Files.write(Paths.get(desktopPath), desktopContent.getBytes());
    }
    
    /**
     * Get the path to the current JAR file
     */
    private static String getJarPath() {
        try {
            return new File(StartupManager.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getPath();
        } catch (Exception e) {
            return System.getProperty("java.class.path");
        }
    }
}
