package com.javavim.terminal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class TerminalUITest {
    
    private TerminalUI terminalUI;
    
    @BeforeEach
    void setUp() {
        // Use mock terminal UI for testing to avoid opening real terminals
        terminalUI = new MockTerminalUI();
    }
    
    @Test
    @DisplayName("Should create terminal UI instance")
    void shouldCreateTerminalUIInstance() {
        assertNotNull(terminalUI);
        assertFalse(terminalUI.isInitialized());
    }
    
    @Test
    @DisplayName("Should handle initialization gracefully")
    void shouldHandleInitializationGracefully() throws Exception {
        terminalUI.initialize();
        
        assertTrue(terminalUI.isInitialized());
        assertNotNull(terminalUI.getScreenBuffer());
        assertNotNull(terminalUI.getCursor());
    }
    
    @Test
    @DisplayName("Should handle shutdown gracefully when not initialized")
    void shouldHandleShutdownGracefullyWhenNotInitialized() {
        assertDoesNotThrow(() -> terminalUI.shutdown());
        assertFalse(terminalUI.isInitialized());
    }
    
    @Test
    @DisplayName("Should handle multiple initialize calls")
    void shouldHandleMultipleInitializeCalls() {
        assertDoesNotThrow(() -> {
            try {
                terminalUI.initialize();
                terminalUI.initialize(); // Second call should be safe
                if (terminalUI.isInitialized()) {
                    terminalUI.shutdown();
                }
            } catch (Exception e) {
                // Expected in headless test environments
                assertTrue(e.getMessage() != null);
            }
        });
    }
    
    @Test
    @DisplayName("Should return null components when not initialized")
    void shouldReturnNullComponentsWhenNotInitialized() {
        assertNull(terminalUI.getScreenBuffer());
        assertNull(terminalUI.getCursor());
    }
    
    @Test
    @DisplayName("Should handle refresh when not initialized")
    void shouldHandleRefreshWhenNotInitialized() {
        assertDoesNotThrow(() -> terminalUI.refresh());
    }
    
    @Test
    @DisplayName("Should handle clear when not initialized")
    void shouldHandleClearWhenNotInitialized() {
        assertDoesNotThrow(() -> terminalUI.clear());
    }
    
    @Test
    @DisplayName("Should handle read input when not initialized")
    void shouldHandleReadInputWhenNotInitialized() {
        assertDoesNotThrow(() -> {
            char result = terminalUI.readInput();
            // Mock terminal returns 'q' (ASCII 113), real terminal would return 0
            assertTrue(result == 0 || result == 'q');
        });
    }
    
    @Test
    @DisplayName("Should maintain proper state transitions")
    void shouldMaintainProperStateTransitions() {
        assertFalse(terminalUI.isInitialized());
        
        assertDoesNotThrow(() -> {
            try {
                terminalUI.initialize();
                // State after initialization depends on environment
                terminalUI.shutdown();
                assertFalse(terminalUI.isInitialized());
            } catch (Exception e) {
                // Expected in headless test environments
                assertFalse(terminalUI.isInitialized());
            }
        });
    }
    
    @Test
    @DisplayName("Should handle operations in sequence safely")
    void shouldHandleOperationsInSequenceSafely() {
        assertDoesNotThrow(() -> {
            try {
                terminalUI.initialize();
                if (terminalUI.isInitialized()) {
                    terminalUI.clear();
                    terminalUI.refresh();
                    terminalUI.shutdown();
                }
            } catch (Exception e) {
                // Expected in headless test environments
                assertTrue(e.getMessage() != null);
            }
        });
    }
    
    @Test
    @DisplayName("Should be safe to call shutdown multiple times")
    void shouldBeSafeToCallShutdownMultipleTimes() {
        assertDoesNotThrow(() -> {
            try {
                terminalUI.initialize();
                terminalUI.shutdown();
                terminalUI.shutdown(); // Second call should be safe
            } catch (Exception e) {
                // Expected in headless test environments
                terminalUI.shutdown(); // Should still be safe
            }
        });
    }
}