package com.javavim.display;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import com.javavim.buffer.Buffer;
import com.javavim.buffer.ScreenBuffer;
import com.javavim.terminal.Cursor;

class StatusLineTest {
    
    private StatusLine statusLine;
    private ScreenBuffer screenBuffer;
    private Buffer buffer;
    private Cursor cursor;
    
    @BeforeEach
    void setUp() {
        statusLine = new StatusLine();
        screenBuffer = new ScreenBuffer(80, 24);
        buffer = new Buffer("test.txt");
        cursor = new Cursor(80, 24);
    }
    
    @Test
    @DisplayName("Should create status line with default settings")
    void shouldCreateStatusLineWithDefaultSettings() {
        assertEquals("NORMAL", statusLine.getMode());
        assertEquals("", statusLine.getMessage());
        assertTrue(statusLine.isShowPosition());
        assertTrue(statusLine.isShowFileInfo());
    }
    
    @Test
    @DisplayName("Should render basic status line")
    void shouldRenderBasicStatusLine() {
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23); // Bottom line
        assertTrue(statusText.contains("NORMAL"));
        assertTrue(statusText.contains("test.txt"));
        assertTrue(statusText.contains("Ln 1, Col 1"));
    }
    
    @Test
    @DisplayName("Should set and get mode")
    void shouldSetAndGetMode() {
        statusLine.setMode("INSERT");
        
        assertEquals("INSERT", statusLine.getMode());
    }
    
    @Test
    @DisplayName("Should ignore null mode")
    void shouldIgnoreNullMode() {
        String originalMode = statusLine.getMode();
        statusLine.setMode(null);
        
        assertEquals(originalMode, statusLine.getMode());
    }
    
    @Test
    @DisplayName("Should set and get message")
    void shouldSetAndGetMessage() {
        statusLine.setMessage("Test message");
        
        assertEquals("Test message", statusLine.getMessage());
    }
    
    @Test
    @DisplayName("Should handle null message")
    void shouldHandleNullMessage() {
        statusLine.setMessage(null);
        
        assertEquals("", statusLine.getMessage());
    }
    
    @Test
    @DisplayName("Should clear message")
    void shouldClearMessage() {
        statusLine.setMessage("Test message");
        statusLine.clearMessage();
        
        assertEquals("", statusLine.getMessage());
    }
    
    @Test
    @DisplayName("Should show message in status line")
    void shouldShowMessageInStatusLine() {
        statusLine.setMessage("Error: File not found");
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertTrue(statusText.contains("Error: File not found"));
    }
    
    @Test
    @DisplayName("Should show modified indicator")
    void shouldShowModifiedIndicator() {
        buffer.setLine(0, "Modified content");
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertTrue(statusText.contains("[+]"));
    }
    
    @Test
    @DisplayName("Should not show modified indicator for unmodified buffer")
    void shouldNotShowModifiedIndicatorForUnmodifiedBuffer() {
        buffer.setModified(false);
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertFalse(statusText.contains("[+]"));
    }
    
    @Test
    @DisplayName("Should show cursor position")
    void shouldShowCursorPosition() {
        cursor.moveTo(10, 5);
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertTrue(statusText.contains("Ln 6, Col 11")); // 1-based indexing
    }
    
    @Test
    @DisplayName("Should hide position when disabled")
    void shouldHidePositionWhenDisabled() {
        statusLine.setShowPosition(false);
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertFalse(statusText.contains("Ln"));
        assertFalse(statusText.contains("Col"));
    }
    
    @Test
    @DisplayName("Should hide file info when disabled")
    void shouldHideFileInfoWhenDisabled() {
        statusLine.setShowFileInfo(false);
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertFalse(statusText.contains("test.txt"));
    }
    
    @Test
    @DisplayName("Should show no name for buffer without filename")
    void shouldShowNoNameForBufferWithoutFilename() {
        Buffer unnamedBuffer = new Buffer();
        statusLine.render(screenBuffer, unnamedBuffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertTrue(statusText.contains("[No Name]"));
    }
    
    @Test
    @DisplayName("Should show line count")
    void shouldShowLineCount() {
        buffer.insertLine(1, "Second line");
        buffer.insertLine(2, "Third line");
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertTrue(statusText.contains("(3 lines)"));
    }
    
    @Test
    @DisplayName("Should truncate long filename")
    void shouldTruncateLongFilename() {
        Buffer longNameBuffer = new Buffer("/very/long/path/to/file/with/very/long/name/that/exceeds/limit.txt");
        statusLine.render(screenBuffer, longNameBuffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertTrue(statusText.contains("..."));
    }
    
    @Test
    @DisplayName("Should handle null screen buffer gracefully")
    void shouldHandleNullScreenBufferGracefully() {
        assertDoesNotThrow(() -> statusLine.render(null, buffer, cursor));
    }
    
    @Test
    @DisplayName("Should handle null buffer gracefully")
    void shouldHandleNullBufferGracefully() {
        assertDoesNotThrow(() -> statusLine.render(screenBuffer, null, cursor));
        
        String statusText = screenBuffer.getLine(23);
        assertTrue(statusText.contains("NORMAL"));
    }
    
    @Test
    @DisplayName("Should handle null cursor gracefully")
    void shouldHandleNullCursorGracefully() {
        assertDoesNotThrow(() -> statusLine.render(screenBuffer, buffer, null));
        
        String statusText = screenBuffer.getLine(23);
        assertFalse(statusText.contains("Ln"));
    }
    
    @Test
    @DisplayName("Should fit status line to screen width")
    void shouldFitStatusLineToScreenWidth() {
        ScreenBuffer narrowScreen = new ScreenBuffer(40, 10);
        statusLine.setMessage("Very long message that might not fit");
        statusLine.render(narrowScreen, buffer, cursor);
        
        String statusText = narrowScreen.getLine(9);
        assertEquals(40, statusText.length());
    }
    
    @Test
    @DisplayName("Should toggle position display")
    void shouldTogglePositionDisplay() {
        assertTrue(statusLine.isShowPosition());
        
        statusLine.setShowPosition(false);
        assertFalse(statusLine.isShowPosition());
        
        statusLine.setShowPosition(true);
        assertTrue(statusLine.isShowPosition());
    }
    
    @Test
    @DisplayName("Should toggle file info display")
    void shouldToggleFileInfoDisplay() {
        assertTrue(statusLine.isShowFileInfo());
        
        statusLine.setShowFileInfo(false);
        assertFalse(statusLine.isShowFileInfo());
        
        statusLine.setShowFileInfo(true);
        assertTrue(statusLine.isShowFileInfo());
    }
    
    @Test
    @DisplayName("Should format status line with proper spacing")
    void shouldFormatStatusLineWithProperSpacing() {
        statusLine.render(screenBuffer, buffer, cursor);
        
        String statusText = screenBuffer.getLine(23);
        assertEquals(80, statusText.length());
        
        // Should have mode on left and position info on right
        assertTrue(statusText.startsWith(" NORMAL"));
        assertTrue(statusText.contains("lines)"));
    }
}