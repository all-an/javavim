package com.javavim.display;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import com.javavim.buffer.Buffer;
import com.javavim.buffer.ScreenBuffer;
import com.javavim.terminal.Cursor;

class DisplayRendererTest {
    
    private DisplayRenderer renderer;
    private Buffer buffer;
    private ScreenBuffer screenBuffer;
    private Cursor cursor;
    
    @BeforeEach
    void setUp() {
        renderer = new DisplayRenderer();
        buffer = new Buffer();
        screenBuffer = new ScreenBuffer(80, 24);
        cursor = new Cursor(80, 24);
        setupTestBuffer();
    }
    
    private void setupTestBuffer() {
        buffer.setLine(0, "First line");
        buffer.insertLine(1, "Second line");
        buffer.insertLine(2, "Third line");
        buffer.insertLine(3, "Fourth line");
    }
    
    @Test
    @DisplayName("Should create renderer with default settings")
    void shouldCreateRendererWithDefaultSettings() {
        assertTrue(renderer.isShowLineNumbers());
        assertEquals(0, renderer.getScrollOffsetY());
        assertEquals(0, renderer.getScrollOffsetX());
    }
    
    @Test
    @DisplayName("Should render buffer with line numbers")
    void shouldRenderBufferWithLineNumbers() {
        renderer.render(buffer, screenBuffer, cursor);
        
        String firstLine = screenBuffer.getLine(0);
        assertTrue(firstLine.contains("1"));
        assertTrue(firstLine.contains("│"));
        assertTrue(firstLine.contains("First line"));
        
        String secondLine = screenBuffer.getLine(1);
        assertTrue(secondLine.contains("2"));
        assertTrue(secondLine.contains("Second line"));
    }
    
    @Test
    @DisplayName("Should render buffer without line numbers")
    void shouldRenderBufferWithoutLineNumbers() {
        renderer.setShowLineNumbers(false);
        renderer.render(buffer, screenBuffer, cursor);
        
        String firstLine = screenBuffer.getLine(0);
        assertFalse(firstLine.contains("1"));
        assertFalse(firstLine.contains("│"));
        assertTrue(firstLine.contains("First line"));
    }
    
    @Test
    @DisplayName("Should handle null parameters gracefully")
    void shouldHandleNullParametersGracefully() {
        assertDoesNotThrow(() -> renderer.render(null, screenBuffer, cursor));
        assertDoesNotThrow(() -> renderer.render(buffer, null, cursor));
        assertDoesNotThrow(() -> renderer.render(buffer, screenBuffer, null));
    }
    
    @Test
    @DisplayName("Should scroll up")
    void shouldScrollUp() {
        renderer.setScrollOffset(5, 0);
        renderer.scrollUp();
        
        assertEquals(4, renderer.getScrollOffsetY());
        assertEquals(0, renderer.getScrollOffsetX());
    }
    
    @Test
    @DisplayName("Should not scroll up beyond zero")
    void shouldNotScrollUpBeyondZero() {
        renderer.setScrollOffset(0, 0);
        renderer.scrollUp();
        
        assertEquals(0, renderer.getScrollOffsetY());
    }
    
    @Test
    @DisplayName("Should scroll down")
    void shouldScrollDown() {
        renderer.scrollDown(buffer);
        
        assertEquals(1, renderer.getScrollOffsetY());
    }
    
    @Test
    @DisplayName("Should not scroll down beyond buffer end")
    void shouldNotScrollDownBeyondBufferEnd() {
        // Set scroll to near end of buffer
        renderer.setScrollOffset(buffer.getLineCount() - 1, 0);
        int originalOffset = renderer.getScrollOffsetY();
        
        renderer.scrollDown(buffer);
        
        assertEquals(originalOffset, renderer.getScrollOffsetY());
    }
    
    @Test
    @DisplayName("Should scroll left")
    void shouldScrollLeft() {
        renderer.setScrollOffset(0, 5);
        renderer.scrollLeft();
        
        assertEquals(4, renderer.getScrollOffsetX());
    }
    
    @Test
    @DisplayName("Should not scroll left beyond zero")
    void shouldNotScrollLeftBeyondZero() {
        renderer.setScrollOffset(0, 0);
        renderer.scrollLeft();
        
        assertEquals(0, renderer.getScrollOffsetX());
    }
    
    @Test
    @DisplayName("Should scroll right")
    void shouldScrollRight() {
        renderer.scrollRight();
        
        assertEquals(1, renderer.getScrollOffsetX());
    }
    
    @Test
    @DisplayName("Should set scroll offset")
    void shouldSetScrollOffset() {
        renderer.setScrollOffset(10, 5);
        
        assertEquals(10, renderer.getScrollOffsetY());
        assertEquals(5, renderer.getScrollOffsetX());
    }
    
    @Test
    @DisplayName("Should not allow negative scroll offsets")
    void shouldNotAllowNegativeScrollOffsets() {
        renderer.setScrollOffset(-5, -3);
        
        assertEquals(0, renderer.getScrollOffsetY());
        assertEquals(0, renderer.getScrollOffsetX());
    }
    
    @Test
    @DisplayName("Should render empty lines with tilde")
    void shouldRenderEmptyLinesWithTilde() {
        Buffer smallBuffer = new Buffer();
        smallBuffer.setLine(0, "Only line");
        
        renderer.render(smallBuffer, screenBuffer, cursor);
        
        String secondLine = screenBuffer.getLine(1);
        assertTrue(secondLine.contains("~"));
        assertTrue(secondLine.contains("│"));
    }
    
    @Test
    @DisplayName("Should handle long lines with horizontal scrolling")
    void shouldHandleLongLinesWithHorizontalScrolling() {
        buffer.setLine(0, "This is a very long line that should be horizontally scrolled when it exceeds the screen width");
        renderer.setScrollOffset(0, 10);
        
        renderer.render(buffer, screenBuffer, cursor);
        
        String firstLine = screenBuffer.getLine(0);
        // Should start from position 10 in the original line
        assertTrue(firstLine.contains("very long line"));
        assertFalse(firstLine.contains("This is a"));
    }
    
    @Test
    @DisplayName("Should handle empty buffer")
    void shouldHandleEmptyBuffer() {
        Buffer emptyBuffer = new Buffer();
        emptyBuffer.setLine(0, "");
        
        assertDoesNotThrow(() -> renderer.render(emptyBuffer, screenBuffer, cursor));
    }
    
    @Test
    @DisplayName("Should toggle line numbers correctly")
    void shouldToggleLineNumbersCorrectly() {
        assertTrue(renderer.isShowLineNumbers());
        
        renderer.setShowLineNumbers(false);
        assertFalse(renderer.isShowLineNumbers());
        
        renderer.setShowLineNumbers(true);
        assertTrue(renderer.isShowLineNumbers());
    }
    
    @Test
    @DisplayName("Should render with vertical scrolling")
    void shouldRenderWithVerticalScrolling() {
        // Add more lines to buffer
        for (int i = 5; i <= 30; i++) {
            buffer.insertLine(i - 1, "Line " + i);
        }
        
        renderer.setScrollOffset(10, 0);
        renderer.render(buffer, screenBuffer, cursor);
        
        String firstVisibleLine = screenBuffer.getLine(0);
        assertTrue(firstVisibleLine.contains("11")); // Line 11 should be first visible (10 + 1)
    }
    
    @Test
    @DisplayName("Should handle line number formatting")
    void shouldHandleLineNumberFormatting() {
        renderer.render(buffer, screenBuffer, cursor);
        
        String firstLine = screenBuffer.getLine(0);
        String secondLine = screenBuffer.getLine(1);
        
        // Line numbers should be right-aligned within their width
        assertTrue(firstLine.substring(0, 4).contains("1"));
        assertTrue(secondLine.substring(0, 4).contains("2"));
    }
    
    @Test
    @DisplayName("Should clear screen buffer before rendering")
    void shouldClearScreenBufferBeforeRendering() {
        // Put some content in screen buffer
        screenBuffer.setString(0, 0, "Old content");
        
        renderer.render(buffer, screenBuffer, cursor);
        
        // Old content should be cleared
        String line = screenBuffer.getLine(0);
        assertFalse(line.contains("Old content"));
        assertTrue(line.contains("First line"));
    }
}