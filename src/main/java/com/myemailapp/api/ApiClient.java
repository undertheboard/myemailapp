package com.myemailapp.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myemailapp.config.AppConfig;
import okhttp3.*;

import java.io.IOException;

/**
 * Client for communicating with the PHP REST API
 * Handles authorization and session management
 */
public class ApiClient {
    private final String baseUrl;
    private final String authSecret;
    private final OkHttpClient client;
    private final Gson gson;
    private AppConfig config;
    
    public ApiClient(AppConfig config) {
        this.config = config;
        this.baseUrl = config.getApiBaseUrl();
        this.authSecret = config.getApiAuthSecret();
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }
    
    /**
     * Login to create a session
     */
    public String login(String email, String password) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("email", email);
        requestBody.addProperty("password", password);
        
        JsonObject response = makeRequest("login", requestBody);
        
        if (response.get("success").getAsBoolean()) {
            String sessionToken = response.getAsJsonObject("data")
                                         .get("session_token").getAsString();
            
            // Store session token if remember login is enabled
            if (config.isRememberLogin()) {
                config.setSessionToken(sessionToken);
                config.saveConfig();
            }
            
            return sessionToken;
        }
        
        throw new IOException("Login failed: " + response.get("message").getAsString());
    }
    
    /**
     * Logout and destroy session
     */
    public void logout(String sessionToken) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("session_token", sessionToken);
        
        makeRequest("logout", requestBody);
        
        // Clear stored session
        config.setSessionToken("");
        config.saveConfig();
    }
    
    /**
     * AI compose email
     */
    public String aiComposeEmail(String instructions) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("instructions", instructions);
        
        JsonObject response = makeRequest("ai-compose", requestBody);
        
        if (response.get("success").getAsBoolean()) {
            return response.getAsJsonObject("data")
                          .get("email_content").getAsString();
        }
        
        throw new IOException("AI compose failed: " + response.get("message").getAsString());
    }
    
    /**
     * AI summarize email
     */
    public String aiSummarizeEmail(String emailText) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("email_text", emailText);
        
        JsonObject response = makeRequest("ai-summarize", requestBody);
        
        if (response.get("success").getAsBoolean()) {
            return response.getAsJsonObject("data")
                          .get("summary").getAsString();
        }
        
        throw new IOException("AI summarize failed: " + response.get("message").getAsString());
    }
    
    /**
     * Send email via API with session
     */
    public void sendEmail(String sessionToken, String to, String subject, String body) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("session_token", sessionToken);
        requestBody.addProperty("to", to);
        requestBody.addProperty("subject", subject);
        requestBody.addProperty("body", body);
        
        JsonObject response = makeRequest("send-email", requestBody);
        
        if (!response.get("success").getAsBoolean()) {
            throw new IOException("Send email failed: " + response.get("message").getAsString());
        }
    }
    
    /**
     * Make authenticated request to API
     */
    private JsonObject makeRequest(String endpoint, JsonObject requestBody) throws IOException {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IOException("API base URL not configured");
        }
        
        if (authSecret == null || authSecret.isEmpty()) {
            throw new IOException("API auth secret not configured");
        }
        
        String url = baseUrl.endsWith("/") ? baseUrl + endpoint : baseUrl + "/" + endpoint;
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer " + authSecret)
            .addHeader("Content-Type", "application/json")
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API error: HTTP " + response.code());
            }
            
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, JsonObject.class);
        }
    }
    
    /**
     * Check if API is available
     */
    public boolean isAvailable() {
        if (baseUrl == null || baseUrl.isEmpty()) {
            return false;
        }
        
        try {
            String url = baseUrl.endsWith("/") ? baseUrl + "health" : baseUrl + "/health";
            
            Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
