package com.myemailapp.ai;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import okhttp3.*;
import java.io.IOException;

/**
 * Service for interacting with Google Gemini AI API
 * Handles email composition, summarization, and voice commands
 */
public class GeminiAIService {
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;
    
    public GeminiAIService(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }
    
    /**
     * Generate AI response for general queries
     */
    public String generateResponse(String prompt) throws IOException {
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        
        part.addProperty("text", prompt);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        requestBody.add("contents", contents);
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
            .url(GEMINI_API_URL + "?key=" + apiKey)
            .post(body)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Gemini API error: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            return extractTextFromResponse(jsonResponse);
        }
    }
    
    /**
     * Compose email with AI assistance
     */
    public String composeEmail(String instructions) throws IOException {
        String prompt = "You are helping an elderly person compose an email. " +
                       "Based on these instructions, write a clear, polite email:\n\n" +
                       instructions + "\n\nEmail:";
        return generateResponse(prompt);
    }
    
    /**
     * Summarize an email for easy understanding
     */
    public String summarizeEmail(String emailContent) throws IOException {
        String prompt = "Summarize this email in simple terms for an elderly person:\n\n" +
                       emailContent + "\n\nSimple summary:";
        return generateResponse(prompt);
    }
    
    /**
     * Process voice command
     */
    public AICommand processVoiceCommand(String voiceText) throws IOException {
        String prompt = "You are helping an elderly person control their email app with voice commands. " +
                       "Analyze this voice command and respond with a JSON object containing:\n" +
                       "- action: one of [READ_EMAIL, COMPOSE_EMAIL, SEND_EMAIL, CHECK_NEW, HELP]\n" +
                       "- parameters: any relevant details (recipient, subject, message, etc.)\n\n" +
                       "Voice command: \"" + voiceText + "\"\n\n" +
                       "Respond ONLY with valid JSON, no other text.";
        
        String response = generateResponse(prompt);
        return parseCommand(response);
    }
    
    /**
     * Extract text from Gemini API response
     */
    private String extractTextFromResponse(JsonObject response) {
        try {
            return response.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
        } catch (Exception e) {
            return "Error parsing AI response";
        }
    }
    
    /**
     * Parse AI command response
     */
    private AICommand parseCommand(String jsonResponse) {
        try {
            // Extract JSON from response if wrapped in text
            int jsonStart = jsonResponse.indexOf("{");
            int jsonEnd = jsonResponse.lastIndexOf("}") + 1;
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                jsonResponse = jsonResponse.substring(jsonStart, jsonEnd);
            }
            
            JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
            String action = json.get("action").getAsString();
            JsonObject params = json.has("parameters") ? 
                              json.getAsJsonObject("parameters") : new JsonObject();
            
            return new AICommand(action, params);
        } catch (Exception e) {
            // Default to help if parsing fails
            return new AICommand("HELP", new JsonObject());
        }
    }
}
