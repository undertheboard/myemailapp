package com.myemailapp.ai;

import com.google.gson.JsonObject;

/**
 * Represents a parsed AI command from voice input
 */
public class AICommand {
    private String action;
    private JsonObject parameters;
    
    public AICommand(String action, JsonObject parameters) {
        this.action = action;
        this.parameters = parameters;
    }
    
    public String getAction() {
        return action;
    }
    
    public JsonObject getParameters() {
        return parameters;
    }
    
    public String getParameter(String key) {
        if (parameters.has(key)) {
            return parameters.get(key).getAsString();
        }
        return null;
    }
}
