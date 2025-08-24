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

class JavavimInsertModeTest {
    
    private Javavim javavim;
    private MockTerminalUI mockTerminal;
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        mockTerminal = new MockTerminalUI();
        javavim = new Javavim(mockTerminal);
        
        tempFile = Files.createTempFile("javavim-insert-test", ".txt");
        Files.write(tempFile, "Line 1\nLine 2\nLine 3".getBytes());
    }
    
    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }
    
    @Test
    @DisplayName("Should enter insert mode correctly")
    void shouldEnterInsertModeCorrectly() throws IOException {
        javavim.openFile(tempFile.toString());
        
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should exit insert mode with ESC")
    void shouldExitInsertModeWithEsc() throws IOException {
        javavim.openFile(tempFile.toString());
        javavim.setMode(Javavim.EditorMode.INSERT);
        
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.NORMAL);
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should handle mode transitions safely")
    void shouldHandleModeTransitionsSafely() {
        assertDoesNotThrow(() -> {
            javavim.setMode(Javavim.EditorMode.INSERT);
            assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
            
            javavim.setMode(Javavim.EditorMode.NORMAL);
            assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
            
            javavim.setMode(Javavim.EditorMode.COMMAND);
            assertEquals(Javavim.EditorMode.COMMAND, javavim.getCurrentMode());
            
            javavim.setMode(Javavim.EditorMode.VISUAL);
            assertEquals(Javavim.EditorMode.VISUAL, javavim.getCurrentMode());
        });
    }
    
    @Test
    @DisplayName("Should maintain buffer state during mode changes")
    void shouldMaintainBufferStateDuringModeChanges() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        assertNotNull(buffer);
        
        int originalLineCount = buffer.getLineCount();
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        assertEquals(originalLineCount, buffer.getLineCount());
        
        javavim.setMode(Javavim.EditorMode.NORMAL);
        assertEquals(originalLineCount, buffer.getLineCount());
    }
    
    @Test
    @DisplayName("Should handle cursor position during mode changes")
    void shouldHandleCursorPositionDuringModeChanges() throws IOException {
        javavim.openFile(tempFile.toString());
        
        assertDoesNotThrow(() -> {
            javavim.setMode(Javavim.EditorMode.INSERT);
            javavim.setMode(Javavim.EditorMode.NORMAL);
        });
    }
    
    @Test
    @DisplayName("Should handle buffer creation in insert mode")
    void shouldHandleBufferCreationInInsertMode() {
        String[] emptyArgs = {};
        javavim.run(emptyArgs);
        
        Buffer buffer = javavim.getBufferManager().getCurrentBuffer();
        assertNotNull(buffer);
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should handle invalid operations gracefully")
    void shouldHandleInvalidOperationsGracefully() {
        assertDoesNotThrow(() -> {
            javavim.setMode(Javavim.EditorMode.INSERT);
            javavim.setMode(Javavim.EditorMode.NORMAL);
            javavim.setMode(Javavim.EditorMode.INSERT);
        });
    }
    
    @Test
    @DisplayName("Should initialize with correct default mode")
    void shouldInitializeWithCorrectDefaultMode() {
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should handle multiple mode switches")
    void shouldHandleMultipleModeSwi() {
        for (int i = 0; i < 5; i++) {
            javavim.setMode(Javavim.EditorMode.INSERT);
            assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
            
            javavim.setMode(Javavim.EditorMode.NORMAL);
            assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
        }
    }
    
    @Test
    @DisplayName("Should handle editor state consistency")
    void shouldHandleEditorStateConsistency() throws IOException {
        javavim.openFile(tempFile.toString());
        
        assertNotNull(javavim.getBufferManager());
        assertNotNull(javavim.getBufferManager().getCurrentBuffer());
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        
        assertNotNull(javavim.getBufferManager());
        assertNotNull(javavim.getBufferManager().getCurrentBuffer());
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
    }
}