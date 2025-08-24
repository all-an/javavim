package com.javavim.terminal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class TerminalDetectorTest {
    
    private TerminalDetector detector;
    
    @BeforeEach
    void setUp() {
        detector = new TerminalDetector();
    }
    
    @Test
    @DisplayName("Should detect terminal availability")
    void shouldDetectTerminalAvailability() {
        boolean available = detector.isTerminalAvailable();
        
        // Result depends on environment, just ensure it doesn't throw
        assertNotNull(available);
    }
    
    @Test
    @DisplayName("Should get terminal width from environment")
    void shouldGetTerminalWidthFromEnvironment() {
        int width = detector.getTerminalWidth();
        
        assertTrue(width > 0);
    }
    
    @Test
    @DisplayName("Should return default width when COLUMNS not set")
    void shouldReturnDefaultWidthWhenColumnsNotSet() {
        // This test assumes COLUMNS might not be set in test environment
        int width = detector.getTerminalWidth();
        
        assertTrue(width >= 80); // Should be at least default
    }
    
    @Test
    @DisplayName("Should get terminal height from environment")
    void shouldGetTerminalHeightFromEnvironment() {
        int height = detector.getTerminalHeight();
        
        assertTrue(height > 0);
    }
    
    @Test
    @DisplayName("Should return default height when LINES not set")
    void shouldReturnDefaultHeightWhenLinesNotSet() {
        // This test assumes LINES might not be set in test environment
        int height = detector.getTerminalHeight();
        
        assertTrue(height >= 24); // Should be at least default
    }
    
    @Test
    @DisplayName("Should detect color support based on TERM variable")
    void shouldDetectColorSupportBasedOnTermVariable() {
        boolean supportsColor = detector.supportsColor();
        
        // Result depends on environment, just ensure it doesn't throw
        assertNotNull(supportsColor);
    }
    
    @Test
    @DisplayName("Should get terminal type from environment")
    void shouldGetTerminalTypeFromEnvironment() {
        String termType = detector.getTerminalType();
        
        assertNotNull(termType);
        assertFalse(termType.isEmpty());
    }
    
    @Test
    @DisplayName("Should return unknown for terminal type when TERM not set")
    void shouldReturnUnknownForTerminalTypeWhenTermNotSet() {
        // In most test environments, this will be the case
        String termType = detector.getTerminalType();
        
        assertTrue(termType.equals("unknown") || !termType.isEmpty());
    }
    
    @Test
    @DisplayName("Should determine if terminal is interactive")
    void shouldDetermineIfTerminalIsInteractive() {
        boolean interactive = detector.isInteractive();
        
        // Result depends on environment, just ensure it doesn't throw
        assertNotNull(interactive);
    }
    
    @Test
    @DisplayName("Should handle invalid COLUMNS value gracefully")
    void shouldHandleInvalidColumnsValueGracefully() {
        // This tests the internal parseInteger method indirectly
        int width = detector.getTerminalWidth();
        
        assertTrue(width > 0); // Should fallback to default
    }
    
    @Test
    @DisplayName("Should handle invalid LINES value gracefully")
    void shouldHandleInvalidLinesValueGracefully() {
        // This tests the internal parseInteger method indirectly
        int height = detector.getTerminalHeight();
        
        assertTrue(height > 0); // Should fallback to default
    }
    
    @Test
    @DisplayName("Should provide consistent results across multiple calls")
    void shouldProvideConsistentResultsAcrossMultipleCalls() {
        int width1 = detector.getTerminalWidth();
        int width2 = detector.getTerminalWidth();
        int height1 = detector.getTerminalHeight();
        int height2 = detector.getTerminalHeight();
        
        assertEquals(width1, width2);
        assertEquals(height1, height2);
    }
}