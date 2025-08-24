package com.javavim.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {
    
    private Configuration config;
    
    @BeforeEach
    void setUp() {
        config = new Configuration();
    }
    
    @Test
    @DisplayName("Should load default settings on creation")
    void shouldLoadDefaultSettingsOnCreation() {
        assertEquals("true", config.get("line_numbers"));
        assertEquals("4", config.get("tab_size"));
        assertEquals("false", config.get("auto_save"));
        assertEquals("default", config.get("theme"));
    }
    
    @Test
    @DisplayName("Should get string value correctly")
    void shouldGetStringValueCorrectly() {
        config.set("test_key", "test_value");
        
        assertEquals("test_value", config.get("test_key"));
    }
    
    @Test
    @DisplayName("Should return null for non-existent key")
    void shouldReturnNullForNonExistentKey() {
        assertNull(config.get("non_existent_key"));
    }
    
    @Test
    @DisplayName("Should return null for null key in get")
    void shouldReturnNullForNullKeyInGet() {
        assertNull(config.get(null));
    }
    
    @Test
    @DisplayName("Should set string value correctly")
    void shouldSetStringValueCorrectly() {
        config.set("new_key", "new_value");
        
        assertEquals("new_value", config.get("new_key"));
    }
    
    @Test
    @DisplayName("Should ignore setting null or empty keys")
    void shouldIgnoreSettingNullOrEmptyKeys() {
        config.set(null, "value");
        config.set("", "value");
        config.set("   ", "value");
        
        assertNull(config.get(null));
        assertNull(config.get(""));
        assertNull(config.get("   "));
    }
    
    @Test
    @DisplayName("Should get boolean value correctly")
    void shouldGetBooleanValueCorrectly() {
        assertTrue(config.getBoolean("line_numbers"));
        assertFalse(config.getBoolean("auto_save"));
    }
    
    @Test
    @DisplayName("Should return false for non-existent boolean key")
    void shouldReturnFalseForNonExistentBooleanKey() {
        assertFalse(config.getBoolean("non_existent"));
    }
    
    @Test
    @DisplayName("Should set boolean value correctly")
    void shouldSetBooleanValueCorrectly() {
        config.setBoolean("test_bool", true);
        
        assertTrue(config.getBoolean("test_bool"));
        assertEquals("true", config.get("test_bool"));
    }
    
    @Test
    @DisplayName("Should get integer value correctly")
    void shouldGetIntegerValueCorrectly() {
        assertEquals(4, config.getInteger("tab_size"));
    }
    
    @Test
    @DisplayName("Should return zero for non-existent integer key")
    void shouldReturnZeroForNonExistentIntegerKey() {
        assertEquals(0, config.getInteger("non_existent"));
    }
    
    @Test
    @DisplayName("Should return zero for invalid integer value")
    void shouldReturnZeroForInvalidIntegerValue() {
        config.set("invalid_int", "not_a_number");
        
        assertEquals(0, config.getInteger("invalid_int"));
    }
    
    @Test
    @DisplayName("Should set integer value correctly")
    void shouldSetIntegerValueCorrectly() {
        config.setInteger("test_int", 42);
        
        assertEquals(42, config.getInteger("test_int"));
        assertEquals("42", config.get("test_int"));
    }
    
    @Test
    @DisplayName("Should check key existence correctly")
    void shouldCheckKeyExistenceCorrectly() {
        assertTrue(config.hasKey("line_numbers"));
        assertFalse(config.hasKey("non_existent"));
        assertFalse(config.hasKey(null));
    }
    
    @Test
    @DisplayName("Should remove key correctly")
    void shouldRemoveKeyCorrectly() {
        config.set("to_remove", "value");
        assertTrue(config.hasKey("to_remove"));
        
        config.remove("to_remove");
        assertFalse(config.hasKey("to_remove"));
    }
    
    @Test
    @DisplayName("Should ignore removing null key")
    void shouldIgnoreRemovingNullKey() {
        int originalSize = config.hasKey("line_numbers") ? 1 : 0;
        config.remove(null);
        
        assertEquals(originalSize > 0, config.hasKey("line_numbers"));
    }
    
    @Test
    @DisplayName("Should clear all settings and reload defaults")
    void shouldClearAllSettingsAndReloadDefaults() {
        config.set("custom_setting", "value");
        assertTrue(config.hasKey("custom_setting"));
        
        config.clear();
        
        assertFalse(config.hasKey("custom_setting"));
        assertEquals("true", config.get("line_numbers"));
        assertEquals("4", config.get("tab_size"));
    }
    
    @Test
    @DisplayName("Should handle overriding default values")
    void shouldHandleOverridingDefaultValues() {
        config.set("line_numbers", "false");
        config.setInteger("tab_size", 8);
        
        assertFalse(config.getBoolean("line_numbers"));
        assertEquals(8, config.getInteger("tab_size"));
    }
}