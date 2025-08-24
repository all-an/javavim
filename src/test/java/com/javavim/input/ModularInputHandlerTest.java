package com.javavim.input;

import com.javavim.Javavim;
import com.javavim.buffer.Buffer;
import com.javavim.buffer.BufferManager;
import com.javavim.command.CommandProcessor;
import com.javavim.display.StatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ModularInputHandler Tests")
class ModularInputHandlerTest {

    @Mock
    private Javavim mockEditor;
    
    @Mock
    private BufferManager mockBufferManager;
    
    @Mock
    private StatusLine mockStatusLine;
    
    @Mock
    private CommandProcessor mockCommandProcessor;
    
    @Mock
    private Buffer mockBuffer;
    
    private ModularInputHandler inputHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inputHandler = new ModularInputHandler(mockEditor, mockBufferManager, mockStatusLine, mockCommandProcessor);
    }

    @Test
    @DisplayName("Should return input processing result for normal mode quit")
    void shouldReturnInputProcessingResultForNormalModeQuit() {
        when(mockBufferManager.hasModifiedBuffers()).thenReturn(false);
        
        InputProcessingResult result = inputHandler.processInput('q', Javavim.EditorMode.NORMAL);
        
        assertTrue(result.wasProcessed());
        assertTrue(result.shouldQuitEditor());
        assertFalse(result.hasError());
        verify(mockEditor).setRunning(false);
    }

    @Test
    @DisplayName("Should return warning result for quit with unsaved changes")
    void shouldReturnWarningResultForQuitWithUnsavedChanges() {
        when(mockBufferManager.hasModifiedBuffers()).thenReturn(true);
        
        InputProcessingResult result = inputHandler.processInput('q', Javavim.EditorMode.NORMAL);
        
        assertTrue(result.wasProcessed());
        assertFalse(result.shouldQuitEditor());
        assertTrue(result.hasWarning());
        assertEquals("Warning: Unsaved changes! Use :q! to force quit", result.getMessage());
    }

    @Test
    @DisplayName("Should return mode change result for insert mode")
    void shouldReturnModeChangeResultForInsertMode() {
        InputProcessingResult result = inputHandler.processInput('i', Javavim.EditorMode.NORMAL);
        
        assertTrue(result.wasProcessed());
        assertTrue(result.hasModeChange());
        assertEquals(Javavim.EditorMode.INSERT, result.getNewMode());
        assertEquals("-- INSERT --", result.getMessage());
    }

    @Test
    @DisplayName("Should return command execution result")
    void shouldReturnCommandExecutionResult() throws IOException {
        when(mockStatusLine.getMessage()).thenReturn(":w");
        
        InputProcessingResult result = inputHandler.processInput('\r', Javavim.EditorMode.COMMAND);
        
        assertTrue(result.wasProcessed());
        assertTrue(result.hasCommandExecution());
        assertEquals(":w", result.getExecutedCommand());
        verify(mockCommandProcessor).executeCommand(":w");
    }

    @Test
    @DisplayName("Should return error result for processing exception")
    void shouldReturnErrorResultForProcessingException() {
        doThrow(new RuntimeException("Test error")).when(mockEditor).setMode(any());
        
        InputProcessingResult result = inputHandler.processInput('i', Javavim.EditorMode.NORMAL);
        
        assertTrue(result.hasError());
        assertEquals("Error: Test error", result.getMessage());
        assertFalse(result.wasProcessed());
    }

    @Test
    @DisplayName("Should return ignored result for null input")
    void shouldReturnIgnoredResultForNullInput() {
        InputProcessingResult result = inputHandler.processInput((char) 0, Javavim.EditorMode.NORMAL);
        
        assertFalse(result.wasProcessed());
        assertFalse(result.hasError());
        assertEquals("Input ignored", result.getMessage());
    }

    @Test
    @DisplayName("Should handle cursor movement commands with return values")
    void shouldHandleCursorMovementCommandsWithReturnValues() {
        InputProcessingResult result = inputHandler.processInput('h', Javavim.EditorMode.NORMAL);
        
        assertTrue(result.wasProcessed());
        assertTrue(result.hasCursorMovement());
        assertEquals("left", result.getMovementDirection());
        verify(mockEditor).moveCursorLeft();
    }

    @Test
    @DisplayName("Should handle insert mode with escape key")
    void shouldHandleInsertModeWithEscapeKey() {
        InputProcessingResult result = inputHandler.processInput((char) 27, Javavim.EditorMode.INSERT);
        
        assertTrue(result.wasProcessed());
        assertTrue(result.hasModeChange());
        assertEquals(Javavim.EditorMode.NORMAL, result.getNewMode());
        assertEquals("Exited insert mode", result.getMessage());
    }

    @Test
    @DisplayName("Should handle text input in insert mode")
    void shouldHandleTextInputInInsertMode() {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
        
        InputProcessingResult result = inputHandler.processInput('a', Javavim.EditorMode.INSERT);
        
        assertTrue(result.wasProcessed());
        assertTrue(result.hasTextInsertion());
        assertEquals('a', result.getInsertedCharacter());
    }

    @Test
    @DisplayName("Should handle visual mode escape")
    void shouldHandleVisualModeEscape() {
        InputProcessingResult result = inputHandler.processInput((char) 27, Javavim.EditorMode.VISUAL);
        
        assertTrue(result.wasProcessed());
        assertTrue(result.hasModeChange());
        assertEquals(Javavim.EditorMode.NORMAL, result.getNewMode());
    }

    @Test
    @DisplayName("Should handle all cursor movement directions")
    void shouldHandleAllCursorMovementDirections() {
        InputProcessingResult resultH = inputHandler.processInput('h', Javavim.EditorMode.NORMAL);
        InputProcessingResult resultJ = inputHandler.processInput('j', Javavim.EditorMode.NORMAL);
        InputProcessingResult resultK = inputHandler.processInput('k', Javavim.EditorMode.NORMAL);
        InputProcessingResult resultL = inputHandler.processInput('l', Javavim.EditorMode.NORMAL);
        
        assertEquals("left", resultH.getMovementDirection());
        assertEquals("down", resultJ.getMovementDirection());
        assertEquals("up", resultK.getMovementDirection());
        assertEquals("right", resultL.getMovementDirection());
        
        verify(mockEditor).moveCursorLeft();
        verify(mockEditor).moveCursorDown();
        verify(mockEditor).moveCursorUp();
        verify(mockEditor).moveCursorRight();
    }

    @Test
    @DisplayName("Should handle command mode switch with colon and slash")
    void shouldHandleCommandModeSwitchWithColonAndSlash() {
        InputProcessingResult resultColon = inputHandler.processInput(':', Javavim.EditorMode.NORMAL);
        InputProcessingResult resultSlash = inputHandler.processInput('/', Javavim.EditorMode.NORMAL);
        
        assertTrue(resultColon.hasModeChange());
        assertTrue(resultSlash.hasModeChange());
        assertEquals(Javavim.EditorMode.COMMAND, resultColon.getNewMode());
        assertEquals(Javavim.EditorMode.COMMAND, resultSlash.getNewMode());
        assertEquals(":", resultColon.getMessage());
        assertEquals("/", resultSlash.getMessage());
    }

    @Test
    @DisplayName("Should handle enter key in insert mode")
    void shouldHandleEnterKeyInInsertMode() {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
        when(mockEditor.getCursorY()).thenReturn(2);
        when(mockBuffer.getLineCount()).thenReturn(5);
        
        InputProcessingResult result = inputHandler.processInput('\r', Javavim.EditorMode.INSERT);
        
        assertTrue(result.wasProcessed());
        assertTrue(result.hasTextInsertion());
        assertEquals('\n', result.getInsertedCharacter());
        verify(mockBuffer).insertLine(3, "");
        verify(mockEditor).moveCursorToNewLine();
    }

    @Test
    @DisplayName("Should handle unknown commands gracefully")
    void shouldHandleUnknownCommandsGracefully() {
        InputProcessingResult result = inputHandler.processInput('z', Javavim.EditorMode.NORMAL);
        
        assertTrue(result.wasProcessed());
        assertEquals("Unknown command: z", result.getMessage());
        verify(mockStatusLine).setMessage("Unknown command: z");
    }
}