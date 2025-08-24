package com.javavim.command;

import com.javavim.Javavim;
import com.javavim.buffer.Buffer;
import com.javavim.buffer.BufferManager;
import com.javavim.display.StatusLine;
import com.javavim.io.FileManager;
import com.javavim.search.SearchEngine;
import com.javavim.search.SearchEngine.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class CommandProcessorSimpleTest {
    
    @Mock private Javavim mockEditor;
    @Mock private BufferManager mockBufferManager;
    @Mock private StatusLine mockStatusLine;
    @Mock private FileManager mockFileManager;
    @Mock private SearchEngine mockSearchEngine;
    @Mock private Buffer mockBuffer;
    
    private CommandProcessor commandProcessor;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandProcessor = new CommandProcessor(
            mockEditor, mockBufferManager, mockStatusLine, mockFileManager, mockSearchEngine
        );
    }
    
    @Test
    @DisplayName("Should handle write command without filename")
    void shouldHandleWriteCommandWithoutFilename() throws IOException {
        commandProcessor.executeCommand(":w");
        verify(mockEditor).saveCurrentBuffer();
    }
    
    @Test
    @DisplayName("Should handle quit command")
    void shouldHandleQuitCommand() throws IOException {
        when(mockBufferManager.hasModifiedBuffers()).thenReturn(false);
        commandProcessor.executeCommand(":q");
        verify(mockEditor).setRunning(false);
    }
    
    @Test
    @DisplayName("Should handle force quit command")
    void shouldHandleForceQuitCommand() throws IOException {
        commandProcessor.executeCommand(":q!");
        verify(mockEditor).setRunning(false);
    }
    
    @Test
    @DisplayName("Should handle quit with unsaved changes")
    void shouldHandleQuitWithUnsavedChanges() throws IOException {
        when(mockBufferManager.hasModifiedBuffers()).thenReturn(true);
        commandProcessor.executeCommand(":q");
        verify(mockEditor, never()).setRunning(false);
        verify(mockStatusLine).setMessage("Warning: Unsaved changes! Use :q! to force quit");
    }
    
    @Test
    @DisplayName("Should handle edit command without filename")
    void shouldHandleEditCommandWithoutFilename() throws IOException {
        commandProcessor.executeCommand(":e");
        verify(mockStatusLine).setMessage("Usage: :e <filename>");
    }
    
    @Test
    @DisplayName("Should handle search command")
    void shouldHandleSearchCommand() throws IOException {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
        List<SearchResult> results = new ArrayList<>();
        results.add(new SearchResult(1, 5, 4, "test"));
        when(mockSearchEngine.findAll(mockBuffer, "test", true)).thenReturn(results);
        
        commandProcessor.executeCommand("/test");
        
        verify(mockSearchEngine).findAll(mockBuffer, "test", true);
        verify(mockStatusLine).setMessage("Found 1 matches for: test");
    }
    
    @Test
    @DisplayName("Should handle empty search")
    void shouldHandleEmptySearch() throws IOException {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
        commandProcessor.executeCommand("/");
        verify(mockSearchEngine, never()).findAll(any(), anyString(), anyBoolean());
    }
    
    @Test
    @DisplayName("Should handle help command")
    void shouldHandleHelpCommand() throws IOException {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
        
        commandProcessor.executeCommand(":help");
        
        verify(mockBuffer).setLine(0, "JavaVim - Terminal Vim Editor v1.0.0");
        verify(mockBuffer).setModified(false);
        verify(mockStatusLine).setMessage("Help displayed");
    }
    
    @Test
    @DisplayName("Should handle unknown command")
    void shouldHandleUnknownCommand() throws IOException {
        commandProcessor.executeCommand(":unknown");
        verify(mockStatusLine).setMessage("Unknown command: :unknown");
    }
    
    @Test
    @DisplayName("Should handle write command with filename")
    void shouldHandleWriteCommandWithFilename() throws IOException {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
        
        commandProcessor.executeCommand(":w filename.txt");
        
        verify(mockBufferManager).getCurrentBuffer();
        verify(mockFileManager).saveBuffer(mockBuffer);
        verify(mockStatusLine).setMessage("Saved to: filename.txt");
    }
    
    @Test
    @DisplayName("Should handle write with no buffer")
    void shouldHandleWriteWithNoBuffer() throws IOException {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(null);
        
        commandProcessor.executeCommand(":w filename.txt");
        
        verify(mockStatusLine).setMessage("No buffer to save");
    }
    
    @Test
    @DisplayName("Should handle write error")
    void shouldHandleWriteError() throws IOException {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
        doThrow(new IOException("Error")).when(mockFileManager).saveBuffer(mockBuffer);
        
        commandProcessor.executeCommand(":w filename.txt");
        
        verify(mockStatusLine).setMessage("Cannot save to: filename.txt (Error)");
    }
    
    @Test
    @DisplayName("Should handle edit command with filename")
    void shouldHandleEditCommandWithFilename() throws IOException {
        commandProcessor.executeCommand(":e filename.txt");
        
        verify(mockEditor).openFile("filename.txt");
        verify(mockStatusLine).setMessage("Opened: filename.txt");
    }
}