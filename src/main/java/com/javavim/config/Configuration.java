package com.javavim.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages editor configuration settings.
 * Follows single responsibility principle - manages configuration only.
 */
public class Configuration {
    
    private final Map<String, String> settings;
    
    public Configuration() {
        this.settings = new HashMap<>();
        loadDefaults();
    }
    
    public String get(String key) {
        if (key == null) {
            return null;
        }
        return settings.get(key);
    }
    
    public void set(String key, String value) {
        if (isValidKey(key)) {
            settings.put(key, value);
        }
    }
    
    public boolean getBoolean(String key) {
        String value = get(key);
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
    
    public void setBoolean(String key, boolean value) {
        set(key, String.valueOf(value));
    }
    
    public int getInteger(String key) {
        String value = get(key);
        if (value == null) {
            return 0;
        }
        
        return parseInteger(value);
    }
    
    public void setInteger(String key, int value) {
        set(key, String.valueOf(value));
    }
    
    public boolean hasKey(String key) {
        if (key == null) {
            return false;
        }
        return settings.containsKey(key);
    }
    
    public void remove(String key) {
        if (key != null) {
            settings.remove(key);
        }
    }
    
    public void clear() {
        settings.clear();
        loadDefaults();
    }
    
    private void loadDefaults() {
        settings.put("line_numbers", "true");
        settings.put("tab_size", "4");
        settings.put("auto_save", "false");
        settings.put("theme", "default");
    }
    
    private boolean isValidKey(String key) {
        return key != null && !key.trim().isEmpty();
    }
    
    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}