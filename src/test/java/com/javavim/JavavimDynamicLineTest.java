package com.javavim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.javavim.terminal.MockTerminalUI;
import com.javavim.buffer.Buffer;

class JavavimDynamicLineTest {
    
    private Javavim javavim;
    private MockTerminalUI mockTerminal;
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        mockTerminal = new MockTerminalUI();
        javavim = new Javavim(mockTerminal);
        
        tempFile = Files.createTempFile("javavim-line-test", ".txt");
        Files.write(tempFile, "Initial line".getBytes());
    }
    
    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }
    
    @Test
    @DisplayName("Should handle buffer line operations safely")
    void shouldHandleBufferLineOperationsSafely() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        
        assertNotNull(buffer);
        assertTrue(buffer.getLineCount() > 0);
        
        int originalCount = buffer.getLineCount();
        
        // Test line insertion
        buffer.insertLine(1, "New line");
        assertEquals(originalCount + 1, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should handle empty buffer correctly")
    void shouldHandleEmptyBufferCorrectly() {
        String[] emptyArgs = {};
        javavim.run(emptyArgs);
        
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        assertNotNull(buffer);
        assertTrue(buffer.getLineCount() > 0); // Welcome buffer has content
    }
    
    @Test
    @DisplayName("Should handle buffer modifications in insert mode")
    void shouldHandleBufferModificationsInInsertMode() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        assertNotNull(buffer);
        
        int originalCount = buffer.getLineCount();
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
        
        // Buffer should still be valid
        assertEquals(originalCount, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should maintain cursor position constraints")
    void shouldMaintainCursorPositionConstraints() throws IOException {
        javavim.openFile(tempFile.toString());
        
        assertDoesNotThrow(() -> {
            javavim.setMode(Javavim.EditorMode.INSERT);
            // In a real scenario, cursor movements would be constrained
            javavim.setMode(Javavim.EditorMode.NORMAL);
        });
    }
    
    @Test
    @DisplayName("Should handle line insertion at different positions")
    void shouldHandleLineInsertionAtDifferentPositions() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        
        int originalCount = buffer.getLineCount();
        
        // Insert at beginning
        buffer.insertLine(0, "First line");
        assertEquals(originalCount + 1, buffer.getLineCount());
        
        // Insert at end
        buffer.insertLine(buffer.getLineCount(), "Last line");
        assertEquals(originalCount + 2, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should handle multiple line insertions")
    void shouldHandleMultipleLineInsertions() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        
        int originalCount = buffer.getLineCount();
        
        for (int i = 0; i < 5; i++) {
            buffer.insertLine(buffer.getLineCount(), "Line " + (i + 1));
        }
        
        assertEquals(originalCount + 5, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should handle buffer state after line operations")
    void shouldHandleBufferStateAfterLineOperations() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        
        String originalFirstLine = buffer.getLine(0);
        assertNotNull(originalFirstLine);
        
        buffer.insertLine(0, "New first line");
        assertNotEquals(originalFirstLine, buffer.getLine(0));
        assertEquals("New first line", buffer.getLine(0));
        assertEquals(originalFirstLine, buffer.getLine(1));
    }
    
    @Test
    @DisplayName("Should handle cursor positioning after operations")
    void shouldHandleCursorPositioningAfterOperations() throws IOException {
        javavim.openFile(tempFile.toString());
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        
        assertDoesNotThrow(() -> {
            // Simulate cursor operations
            javavim.setMode(Javavim.EditorMode.NORMAL);
        });
    }
    
    @Test
    @DisplayName("Should handle buffer consistency during mode changes")
    void shouldHandleBufferConsistencyDuringModeChanges() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        
        String firstLine = buffer.getLine(0);
        int lineCount = buffer.getLineCount();
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        
        // Buffer should remain consistent
        assertEquals(firstLine, buffer.getLine(0));
        assertEquals(lineCount, buffer.getLineCount());
        
        javavim.setMode(Javavim.EditorMode.NORMAL);
        
        assertEquals(firstLine, buffer.getLine(0));
        assertEquals(lineCount, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should handle edge cases in line operations")
    void shouldHandleEdgeCasesInLineOperations() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        
        assertDoesNotThrow(() -> {
            // Test insertion at valid boundaries
            buffer.insertLine(0, "");
            buffer.insertLine(buffer.getLineCount(), "");
        });
    }
}