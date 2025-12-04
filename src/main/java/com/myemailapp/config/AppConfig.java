package com.myemailapp.config;

import java.io.*;
import java.util.Properties;

/**
 * Configuration manager for the email application
 * Stores user settings, API keys, and email credentials
 */
public class AppConfig {
    private static final String CONFIG_FILE = System.getProperty("user.home") + 
                                              File.separator + ".myemailapp" + 
                                              File.separator + "config.properties";
    private Properties properties;
    
    public AppConfig() {
        properties = new Properties();
        loadConfig();
    }
    
    private void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading config: " + e.getMessage());
            }
        } else {
            // Create default config directory
            configFile.getParentFile().mkdirs();
        }
    }
    
    public void saveConfig() {
        File configFile = new File(CONFIG_FILE);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "AI Email App Configuration");
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }
    
    // Email settings
    public String getEmailAddress() {
        return properties.getProperty("email.address", "");
    }
    
    public void setEmailAddress(String email) {
        properties.setProperty("email.address", email);
    }
    
    public String getEmailPassword() {
        return properties.getProperty("email.password", "");
    }
    
    public void setEmailPassword(String password) {
        properties.setProperty("email.password", password);
    }
    
    // Optimum email server settings
    public String getImapServer() {
        return properties.getProperty("imap.server", "mail.optimum.net");
    }
    
    public int getImapPort() {
        return Integer.parseInt(properties.getProperty("imap.port", "993"));
    }
    
    public String getSmtpServer() {
        return properties.getProperty("smtp.server", "mail.optimum.net");
    }
    
    public int getSmtpPort() {
        return Integer.parseInt(properties.getProperty("smtp.port", "465"));
    }
    
    // AI settings
    public String getGeminiApiKey() {
        return properties.getProperty("gemini.api.key", "");
    }
    
    public void setGeminiApiKey(String apiKey) {
        properties.setProperty("gemini.api.key", apiKey);
    }
    
    // API Authorization settings
    public String getApiAuthSecret() {
        return properties.getProperty("api.auth.secret", "");
    }
    
    public void setApiAuthSecret(String secret) {
        properties.setProperty("api.auth.secret", secret);
    }
    
    public String getApiBaseUrl() {
        return properties.getProperty("api.base.url", "");
    }
    
    public void setApiBaseUrl(String url) {
        properties.setProperty("api.base.url", url);
    }
    
    // Session management
    public String getSessionToken() {
        return properties.getProperty("session.token", "");
    }
    
    public void setSessionToken(String token) {
        properties.setProperty("session.token", token);
    }
    
    public boolean isRememberLogin() {
        return Boolean.parseBoolean(properties.getProperty("remember.login", "true"));
    }
    
    public void setRememberLogin(boolean remember) {
        properties.setProperty("remember.login", String.valueOf(remember));
    }
    
    public boolean isVoiceEnabled() {
        return Boolean.parseBoolean(properties.getProperty("voice.enabled", "true"));
    }
    
    public void setVoiceEnabled(boolean enabled) {
        properties.setProperty("voice.enabled", String.valueOf(enabled));
    }
    
    public int getFontSize() {
        return Integer.parseInt(properties.getProperty("font.size", "18"));
    }
    
    public void setFontSize(int size) {
        properties.setProperty("font.size", String.valueOf(size));
    }
}
