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

@DisplayName("InputHandler Tests")
class InputHandlerTest {

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
    
    private InputHandler inputHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inputHandler = new InputHandler(mockEditor, mockBufferManager, mockStatusLine, mockCommandProcessor);
        
        // Default setup
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
    }

    @Test
    @DisplayName("Should ignore null input character")
    void shouldIgnoreNullInputCharacter() throws IOException {
        inputHandler.handleInput((char) 0, Javavim.EditorMode.NORMAL);
        
        verifyNoInteractions(mockEditor, mockStatusLine);
    }

    @Test
    @DisplayName("Should handle exception during input processing")
    void shouldHandleExceptionDuringInputProcessing() throws IOException {
        doThrow(new RuntimeException("Test error")).when(mockEditor).setMode(any());
        
        inputHandler.handleInput('i', Javavim.EditorMode.NORMAL);
        
        verify(mockStatusLine).setMessage("Error: Test error");
    }

    @Test
    @DisplayName("Should handle normal mode quit command without unsaved changes")
    void shouldHandleNormalModeQuitCommandWithoutUnsavedChanges() throws IOException {
        when(mockBufferManager.hasModifiedBuffers()).thenReturn(false);
        
        inputHandler.handleInput('q', Javavim.EditorMode.NORMAL);
        
        verify(mockEditor).setRunning(false);
        verify(mockBufferManager).hasModifiedBuffers();
    }

    @Test
    @DisplayName("Should handle normal mode quit command with unsaved changes")
    void shouldHandleNormalModeQuitCommandWithUnsavedChanges() throws IOException {
        when(mockBufferManager.hasModifiedBuffers()).thenReturn(true);
        
        inputHandler.handleInput('q', Javavim.EditorMode.NORMAL);
        
        verify(mockStatusLine).setMessage("Warning: Unsaved changes! Use :q! to force quit");
        verify(mockEditor, never()).setRunning(false);
    }

    @Test
    @DisplayName("Should handle normal mode insert command")
    void shouldHandleNormalModeInsertCommand() throws IOException {
        inputHandler.handleInput('i', Javavim.EditorMode.NORMAL);
        
        verify(mockEditor).setMode(Javavim.EditorMode.INSERT);
        verify(mockStatusLine).setMessage("-- INSERT --");
    }

    @Test
    @DisplayName("Should handle normal mode command mode switch with colon")
    void shouldHandleNormalModeCommandModeSwitchWithColon() throws IOException {
        inputHandler.handleInput(':', Javavim.EditorMode.NORMAL);
        
        verify(mockEditor).setMode(Javavim.EditorMode.COMMAND);
        verify(mockStatusLine).setMessage(":");
    }

    @Test
    @DisplayName("Should handle normal mode command mode switch with slash")
    void shouldHandleNormalModeCommandModeSwitchWithSlash() throws IOException {
        inputHandler.handleInput('/', Javavim.EditorMode.NORMAL);
        
        verify(mockEditor).setMode(Javavim.EditorMode.COMMAND);
        verify(mockStatusLine).setMessage("/");
    }

    @Test
    @DisplayName("Should handle normal mode search next command")
    void shouldHandleNormalModeSearchNextCommand() throws IOException {
        inputHandler.handleInput('n', Javavim.EditorMode.NORMAL);
        
        verify(mockStatusLine).setMessage("Next search functionality");
    }

    @Test
    @DisplayName("Should handle normal mode undo command")
    void shouldHandleNormalModeUndoCommand() throws IOException {
        inputHandler.handleInput('u', Javavim.EditorMode.NORMAL);
        
        verify(mockStatusLine).setMessage("Undo functionality");
    }

    @Test
    @DisplayName("Should handle normal mode redo command")
    void shouldHandleNormalModeRedoCommand() throws IOException {
        inputHandler.handleInput((char) 18, Javavim.EditorMode.NORMAL); // Ctrl+R
        
        verify(mockStatusLine).setMessage("Redo functionality");
    }

    @Test
    @DisplayName("Should handle normal mode cursor movement left")
    void shouldHandleNormalModeCursorMovementLeft() throws IOException {
        inputHandler.handleInput('h', Javavim.EditorMode.NORMAL);
        
        verify(mockEditor).moveCursorLeft();
    }

    @Test
    @DisplayName("Should handle normal mode cursor movement down")
    void shouldHandleNormalModeCursorMovementDown() throws IOException {
        inputHandler.handleInput('j', Javavim.EditorMode.NORMAL);
        
        verify(mockEditor).moveCursorDown();
    }

    @Test
    @DisplayName("Should handle normal mode cursor movement up")
    void shouldHandleNormalModeCursorMovementUp() throws IOException {
        inputHandler.handleInput('k', Javavim.EditorMode.NORMAL);
        
        verify(mockEditor).moveCursorUp();
    }

    @Test
    @DisplayName("Should handle normal mode cursor movement right")
    void shouldHandleNormalModeCursorMovementRight() throws IOException {
        inputHandler.handleInput('l', Javavim.EditorMode.NORMAL);
        
        verify(mockEditor).moveCursorRight();
    }

    @Test
    @DisplayName("Should handle normal mode unknown command")
    void shouldHandleNormalModeUnknownCommand() throws IOException {
        inputHandler.handleInput('z', Javavim.EditorMode.NORMAL);
        
        verify(mockStatusLine).setMessage("Unknown command: z");
    }

    @Test
    @DisplayName("Should handle insert mode escape key")
    void shouldHandleInsertModeEscapeKey() throws IOException {
        inputHandler.handleInput((char) 27, Javavim.EditorMode.INSERT);
        
        verify(mockEditor).setMode(Javavim.EditorMode.NORMAL);
        verify(mockStatusLine).setMessage("Exited insert mode");
    }

    @Test
    @DisplayName("Should handle insert mode regular input")
    void shouldHandleInsertModeRegularInput() throws IOException {
        inputHandler.handleInput('a', Javavim.EditorMode.INSERT);
        
        verify(mockStatusLine).setMessage("INSERT - Key: 97 (a) - Press ESC to exit");
    }

    @Test
    @DisplayName("Should handle insert mode regular input when buffer is null")
    void shouldHandleInsertModeRegularInputWhenBufferIsNull() throws IOException {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(null);
        
        inputHandler.handleInput('a', Javavim.EditorMode.INSERT);
        
        // Should not crash and should not call buffer operations
        verify(mockBuffer, never()).insertLine(anyInt(), anyString());
    }

    @Test
    @DisplayName("Should handle insert mode enter key")
    void shouldHandleInsertModeEnterKey() throws IOException {
        when(mockEditor.getCursorY()).thenReturn(2);
        when(mockBuffer.getLineCount()).thenReturn(5);
        
        inputHandler.handleInput('\r', Javavim.EditorMode.INSERT);
        
        verify(mockBuffer).insertLine(3, ""); // currentRow + 1
        verify(mockEditor).moveCursorToNewLine();
        verify(mockStatusLine).setMessage("INSERT - New line added");
    }

    @Test
    @DisplayName("Should handle insert mode newline character")
    void shouldHandleInsertModeNewlineCharacter() throws IOException {
        when(mockEditor.getCursorY()).thenReturn(1);
        when(mockBuffer.getLineCount()).thenReturn(3);
        
        inputHandler.handleInput('\n', Javavim.EditorMode.INSERT);
        
        verify(mockBuffer).insertLine(2, "");
        verify(mockEditor).moveCursorToNewLine();
        verify(mockStatusLine).setMessage("INSERT - New line added");
    }

    @Test
    @DisplayName("Should not insert line when line number exceeds buffer count")
    void shouldNotInsertLineWhenLineNumberExceedsBufferCount() throws IOException {
        when(mockEditor.getCursorY()).thenReturn(5);
        when(mockBuffer.getLineCount()).thenReturn(3);
        
        inputHandler.handleInput('\r', Javavim.EditorMode.INSERT);
        
        verify(mockBuffer, never()).insertLine(anyInt(), anyString());
        verify(mockEditor).moveCursorToNewLine();
        verify(mockStatusLine).setMessage("INSERT - New line added");
    }

    @Test
    @DisplayName("Should handle command mode escape key")
    void shouldHandleCommandModeEscapeKey() throws IOException {
        inputHandler.handleInput((char) 27, Javavim.EditorMode.COMMAND);
        
        verify(mockEditor).setMode(Javavim.EditorMode.NORMAL);
        verify(mockStatusLine).clearMessage();
    }

    @Test
    @DisplayName("Should handle command mode enter key")
    void shouldHandleCommandModeEnterKey() throws IOException {
        when(mockStatusLine.getMessage()).thenReturn(":w");
        
        inputHandler.handleInput('\r', Javavim.EditorMode.COMMAND);
        
        verify(mockCommandProcessor).executeCommand(":w");
        verify(mockEditor).setMode(Javavim.EditorMode.NORMAL);
        verify(mockStatusLine).clearMessage();
    }

    @Test
    @DisplayName("Should handle command mode newline character")
    void shouldHandleCommandModeNewlineCharacter() throws IOException {
        when(mockStatusLine.getMessage()).thenReturn(":q");
        
        inputHandler.handleInput('\n', Javavim.EditorMode.COMMAND);
        
        verify(mockCommandProcessor).executeCommand(":q");
        verify(mockEditor).setMode(Javavim.EditorMode.NORMAL);
        verify(mockStatusLine).clearMessage();
    }

    @Test
    @DisplayName("Should handle command mode regular input")
    void shouldHandleCommandModeRegularInput() throws IOException {
        inputHandler.handleInput('w', Javavim.EditorMode.COMMAND);
        
        // Regular input in command mode doesn't trigger any actions
        verify(mockCommandProcessor, never()).executeCommand(anyString());
        verify(mockEditor, never()).setMode(any());
    }

    @Test
    @DisplayName("Should handle visual mode escape key")
    void shouldHandleVisualModeEscapeKey() throws IOException {
        inputHandler.handleInput((char) 27, Javavim.EditorMode.VISUAL);
        
        verify(mockEditor).setMode(Javavim.EditorMode.NORMAL);
        verify(mockStatusLine).clearMessage();
    }

    @Test
    @DisplayName("Should handle visual mode regular input")
    void shouldHandleVisualModeRegularInput() throws IOException {
        inputHandler.handleInput('v', Javavim.EditorMode.VISUAL);
        
        // Regular input in visual mode doesn't trigger any actions currently
        verify(mockEditor, never()).setMode(any());
    }

    @Test
    @DisplayName("Should handle command processor exception gracefully")
    void shouldHandleCommandProcessorExceptionGracefully() throws IOException {
        when(mockStatusLine.getMessage()).thenReturn(":invalid");
        doThrow(new IOException("Command failed")).when(mockCommandProcessor).executeCommand(":invalid");
        
        inputHandler.handleInput('\r', Javavim.EditorMode.COMMAND);
        
        verify(mockStatusLine).setMessage("Error: Command failed");
    }

    @Test
    @DisplayName("Should handle all editor modes correctly")
    void shouldHandleAllEditorModesCorrectly() throws IOException {
        // Test that each mode gets processed correctly without exceptions
        assertDoesNotThrow(() -> inputHandler.handleInput('q', Javavim.EditorMode.NORMAL));
        assertDoesNotThrow(() -> inputHandler.handleInput('a', Javavim.EditorMode.INSERT));
        assertDoesNotThrow(() -> inputHandler.handleInput('w', Javavim.EditorMode.COMMAND));
        assertDoesNotThrow(() -> inputHandler.handleInput('v', Javavim.EditorMode.VISUAL));
    }
}