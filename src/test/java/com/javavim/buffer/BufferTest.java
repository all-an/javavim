package com.javavim.buffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class BufferTest {
    
    private Buffer buffer;
    
    @BeforeEach
    void setUp() {
        buffer = new Buffer();
    }
    
    @Test
    @DisplayName("Should create buffer with one empty line")
    void shouldCreateBufferWithOneEmptyLine() {
        assertEquals(1, buffer.getLineCount());
        assertEquals("", buffer.getLine(0));
    }
    
    @Test
    @DisplayName("Should create buffer with filename")
    void shouldCreateBufferWithFilename() {
        Buffer namedBuffer = new Buffer("test.txt");
        
        assertEquals("test.txt", namedBuffer.getFilename());
        assertEquals(1, namedBuffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should get line content correctly")
    void shouldGetLineContentCorrectly() {
        buffer.setLine(0, "Hello World");
        
        assertEquals("Hello World", buffer.getLine(0));
    }
    
    @Test
    @DisplayName("Should return empty string for invalid line number")
    void shouldReturnEmptyStringForInvalidLineNumber() {
        assertEquals("", buffer.getLine(-1));
        assertEquals("", buffer.getLine(999));
    }
    
    @Test
    @DisplayName("Should set line content and mark as modified")
    void shouldSetLineContentAndMarkAsModified() {
        buffer.setLine(0, "New content");
        
        assertEquals("New content", buffer.getLine(0));
        assertTrue(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should ignore setting invalid line numbers")
    void shouldIgnoreSettingInvalidLineNumbers() {
        boolean originalModified = buffer.isModified();
        
        buffer.setLine(-1, "Invalid");
        buffer.setLine(999, "Invalid");
        
        assertEquals(originalModified, buffer.isModified());
    }
    
    @Test
    @DisplayName("Should insert line at valid position")
    void shouldInsertLineAtValidPosition() {
        buffer.setLine(0, "First line");
        buffer.insertLine(1, "Second line");
        
        assertEquals(2, buffer.getLineCount());
        assertEquals("First line", buffer.getLine(0));
        assertEquals("Second line", buffer.getLine(1));
        assertTrue(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should insert line at beginning")
    void shouldInsertLineAtBeginning() {
        buffer.setLine(0, "Original line");
        buffer.insertLine(0, "New first line");
        
        assertEquals(2, buffer.getLineCount());
        assertEquals("New first line", buffer.getLine(0));
        assertEquals("Original line", buffer.getLine(1));
    }
    
    @Test
    @DisplayName("Should ignore inserting at invalid positions")
    void shouldIgnoreInsertingAtInvalidPositions() {
        int originalLineCount = buffer.getLineCount();
        
        buffer.insertLine(-1, "Invalid");
        buffer.insertLine(999, "Invalid");
        
        assertEquals(originalLineCount, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should delete line when multiple lines exist")
    void shouldDeleteLineWhenMultipleLinesExist() {
        buffer.insertLine(1, "Second line");
        buffer.deleteLine(0);
        
        assertEquals(1, buffer.getLineCount());
        assertEquals("Second line", buffer.getLine(0));
        assertTrue(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should not delete last remaining line")
    void shouldNotDeleteLastRemainingLine() {
        buffer.deleteLine(0);
        
        assertEquals(1, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should ignore deleting invalid line numbers")
    void shouldIgnoreDeleteingInvalidLineNumbers() {
        int originalLineCount = buffer.getLineCount();
        
        buffer.deleteLine(-1);
        buffer.deleteLine(999);
        
        assertEquals(originalLineCount, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should track modified state correctly")
    void shouldTrackModifiedStateCorrectly() {
        assertFalse(buffer.isModified());
        
        buffer.setLine(0, "Modified content");
        assertTrue(buffer.isModified());
        
        buffer.setModified(false);
        assertFalse(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should set and get filename")
    void shouldSetAndGetFilename() {
        assertNull(buffer.getFilename());
        
        buffer.setFilename("test.java");
        assertEquals("test.java", buffer.getFilename());
    }
    
    @Test
    @DisplayName("Should return copy of lines list")
    void shouldReturnCopyOfLinesList() {
        buffer.setLine(0, "Original");
        var lines = buffer.getLines();
        
        lines.set(0, "Modified");
        
        assertEquals("Original", buffer.getLine(0));
    }
    
    @Test
    @DisplayName("Should handle null content gracefully")
    void shouldHandleNullContentGracefully() {
        assertThrows(NullPointerException.class, () -> buffer.setLine(0, null));
        assertThrows(NullPointerException.class, () -> buffer.insertLine(0, null));
    }
}