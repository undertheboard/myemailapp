package com.myemailapp.startup;

import java.io.*;
import java.nio.file.*;

/**
 * Manages desktop icon creation for different operating systems
 */
public class DesktopIconManager {
    
    /**
     * Create desktop icon on first run
     */
    public static void createDesktopIcon() {
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.contains("win")) {
                createWindowsDesktopIcon();
            } else if (os.contains("mac")) {
                createMacDesktopIcon();
            } else if (os.contains("nix") || os.contains("nux")) {
                createLinuxDesktopIcon();
            }
        } catch (Exception e) {
            System.err.println("Could not create desktop icon: " + e.getMessage());
        }
    }
    
    /**
     * Create desktop shortcut on Windows
     */
    private static void createWindowsDesktopIcon() throws IOException {
        String desktopPath = System.getProperty("user.home") + "\\Desktop";
        String jarPath = getJarPath();
        String shortcutPath = desktopPath + "\\AI Email App.lnk";
        
        // Create VBS script to generate shortcut
        String vbsScript = String.format(
            "Set oWS = WScript.CreateObject(\"WScript.Shell\")\n" +
            "sLinkFile = \"%s\"\n" +
            "Set oLink = oWS.CreateShortcut(sLinkFile)\n" +
            "oLink.TargetPath = \"javaw.exe\"\n" +
            "oLink.Arguments = \"-jar \"\"%s\"\"\"\n" +
            "oLink.WindowStyle = 1\n" +
            "oLink.Description = \"AI Email App - Voice-controlled email for seniors\"\n" +
            "oLink.WorkingDirectory = \"%s\"\n" +
            "oLink.Save\n",
            shortcutPath,
            jarPath,
            new File(jarPath).getParent()
        );
        
        File vbsFile = new File(System.getProperty("java.io.tmpdir"), "create_shortcut.vbs");
        Files.write(vbsFile.toPath(), vbsScript.getBytes());
        
        // Execute VBS script
        try {
            Process process = Runtime.getRuntime().exec("cscript //NoLogo " + vbsFile.getAbsolutePath());
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Clean up
        vbsFile.delete();
    }
    
    /**
     * Create desktop icon on macOS
     */
    private static void createMacDesktopIcon() throws IOException {
        String homeDir = System.getProperty("user.home");
        String desktopPath = homeDir + "/Desktop";
        String appPath = desktopPath + "/AI Email App.command";
        String jarPath = getJarPath();
        
        // Create command script
        String scriptContent = String.format(
            "#!/bin/bash\n" +
            "cd \"%s\"\n" +
            "java -jar \"%s\"\n",
            new File(jarPath).getParent(),
            jarPath
        );
        
        File scriptFile = new File(appPath);
        Files.write(scriptFile.toPath(), scriptContent.getBytes());
        
        // Make executable
        scriptFile.setExecutable(true, false);
        
        // Set icon (optional, requires additional setup)
        setMacIcon(scriptFile);
    }
    
    /**
     * Set icon for Mac script
     */
    private static void setMacIcon(File scriptFile) {
        try {
            // This would use AppleScript to set a custom icon
            // For simplicity, we'll skip custom icon setup
            // Users can manually set icon by dragging an .icns file in Finder
        } catch (Exception e) {
            // Ignore icon setting errors
        }
    }
    
    /**
     * Create desktop icon on Linux
     */
    private static void createLinuxDesktopIcon() throws IOException {
        String homeDir = System.getProperty("user.home");
        String desktopPath = homeDir + "/Desktop";
        String desktopFilePath = desktopPath + "/ai-email-app.desktop";
        String jarPath = getJarPath();
        
        // Ensure Desktop directory exists
        new File(desktopPath).mkdirs();
        
        // Create .desktop file
        String desktopContent = String.format(
            "[Desktop Entry]\n" +
            "Version=1.0\n" +
            "Type=Application\n" +
            "Name=AI Email App\n" +
            "Comment=Voice-controlled email application for seniors\n" +
            "Exec=java -jar \"%s\"\n" +
            "Path=%s\n" +
            "Icon=mail-client\n" +
            "Terminal=false\n" +
            "StartupNotify=true\n" +
            "Categories=Network;Email;\n",
            jarPath,
            new File(jarPath).getParent()
        );
        
        File desktopFile = new File(desktopFilePath);
        Files.write(desktopFile.toPath(), desktopContent.getBytes());
        
        // Make executable
        desktopFile.setExecutable(true, false);
        
        // Try to trust the desktop file (for some Linux distributions)
        try {
            Runtime.getRuntime().exec(new String[]{"gio", "set", desktopFilePath, "metadata::trusted", "true"});
        } catch (Exception e) {
            // Ignore if gio command not available
        }
    }
    
    /**
     * Get the path to the current JAR file
     */
    private static String getJarPath() {
        try {
            return new File(DesktopIconManager.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getPath();
        } catch (Exception e) {
            return System.getProperty("java.class.path");
        }
    }
    
    /**
     * Check if desktop icon already exists
     */
    public static boolean desktopIconExists() {
        String os = System.getProperty("os.name").toLowerCase();
        String desktopPath = System.getProperty("user.home");
        
        if (os.contains("win")) {
            desktopPath += "\\Desktop\\AI Email App.lnk";
        } else if (os.contains("mac")) {
            desktopPath += "/Desktop/AI Email App.command";
        } else {
            desktopPath += "/Desktop/ai-email-app.desktop";
        }
        
        return new File(desktopPath).exists();
    }
}
