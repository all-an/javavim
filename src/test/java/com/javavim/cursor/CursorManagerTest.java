package com.javavim.cursor;

import com.javavim.buffer.Buffer;
import com.javavim.buffer.BufferManager;
import com.javavim.terminal.Cursor;
import com.javavim.terminal.TerminalUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CursorManager Tests")
class CursorManagerTest {

    @Mock
    private TerminalUI mockTerminalUI;
    
    @Mock
    private BufferManager mockBufferManager;
    
    @Mock
    private Cursor mockCursor;
    
    @Mock
    private Buffer mockBuffer;
    
    private CursorManager cursorManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cursorManager = new CursorManager(mockTerminalUI, mockBufferManager);
        
        // Default setup - cursor exists and is at origin
        when(mockTerminalUI.getCursor()).thenReturn(mockCursor);
        when(mockCursor.getX()).thenReturn(0);
        when(mockCursor.getY()).thenReturn(0);
        when(mockBufferManager.getCurrentBuffer()).thenReturn(mockBuffer);
    }

    @Test
    @DisplayName("Should move cursor left when position allows")
    void shouldMoveCursorLeftWhenPositionAllows() {
        when(mockCursor.getX()).thenReturn(5);
        
        cursorManager.moveCursorLeft();
        
        verify(mockCursor).moveLeft();
    }

    @Test
    @DisplayName("Should not move cursor left when at leftmost position")
    void shouldNotMoveCursorLeftWhenAtLeftmostPosition() {
        when(mockCursor.getX()).thenReturn(0);
        
        cursorManager.moveCursorLeft();
        
        verify(mockCursor, never()).moveLeft();
    }

    @Test
    @DisplayName("Should not move cursor left when cursor is null")
    void shouldNotMoveCursorLeftWhenCursorIsNull() {
        when(mockTerminalUI.getCursor()).thenReturn(null);
        
        cursorManager.moveCursorLeft();
        
        verify(mockCursor, never()).moveLeft();
    }

    @Test
    @DisplayName("Should not move cursor left when buffer is null")
    void shouldNotMoveCursorLeftWhenBufferIsNull() {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(null);
        
        cursorManager.moveCursorLeft();
        
        verify(mockCursor, never()).moveLeft();
    }

    @Test
    @DisplayName("Should move cursor right when within line bounds")
    void shouldMoveCursorRightWhenWithinLineBounds() {
        when(mockCursor.getX()).thenReturn(2);
        when(mockCursor.getY()).thenReturn(0);
        when(mockBuffer.getLine(0)).thenReturn("Hello World");
        
        cursorManager.moveCursorRight();
        
        verify(mockCursor).moveRight();
    }

    @Test
    @DisplayName("Should not move cursor right when at end of line")
    void shouldNotMoveCursorRightWhenAtEndOfLine() {
        when(mockCursor.getX()).thenReturn(5);
        when(mockCursor.getY()).thenReturn(0);
        when(mockBuffer.getLine(0)).thenReturn("Hello");
        
        cursorManager.moveCursorRight();
        
        verify(mockCursor, never()).moveRight();
    }

    @Test
    @DisplayName("Should not move cursor right when line is null")
    void shouldNotMoveCursorRightWhenLineIsNull() {
        when(mockCursor.getX()).thenReturn(2);
        when(mockCursor.getY()).thenReturn(0);
        when(mockBuffer.getLine(0)).thenReturn(null);
        
        cursorManager.moveCursorRight();
        
        verify(mockCursor, never()).moveRight();
    }

    @Test
    @DisplayName("Should move cursor up when position allows")
    void shouldMoveCursorUpWhenPositionAllows() {
        when(mockCursor.getY()).thenReturn(3);
        when(mockCursor.getX()).thenReturn(2);
        when(mockBuffer.getLine(2)).thenReturn("Some line");
        
        cursorManager.moveCursorUp();
        
        verify(mockCursor).moveUp();
        verify(mockCursor, never()).moveTo(anyInt(), anyInt()); // No adjustment needed
    }

    @Test
    @DisplayName("Should not move cursor up when at top")
    void shouldNotMoveCursorUpWhenAtTop() {
        when(mockCursor.getY()).thenReturn(0);
        
        cursorManager.moveCursorUp();
        
        verify(mockCursor, never()).moveUp();
    }

    @Test
    @DisplayName("Should adjust cursor position when moving up to shorter line")
    void shouldAdjustCursorPositionWhenMovingUpToShorterLine() {
        when(mockCursor.getY()).thenReturn(2).thenReturn(1); // After moveUp()
        when(mockCursor.getX()).thenReturn(10);
        when(mockBuffer.getLine(1)).thenReturn("Short"); // Line after moving up
        
        cursorManager.moveCursorUp();
        
        verify(mockCursor).moveUp();
        verify(mockCursor).moveTo(5, 1); // Adjusted to end of shorter line (Y=1 after moveUp)
    }

    @Test
    @DisplayName("Should move cursor down when buffer allows")
    void shouldMoveCursorDownWhenBufferAllows() {
        when(mockCursor.getY()).thenReturn(1).thenReturn(2); // After moveDown()
        when(mockCursor.getX()).thenReturn(2);
        when(mockBuffer.getLineCount()).thenReturn(5);
        when(mockBuffer.getLine(2)).thenReturn("Line content");
        
        cursorManager.moveCursorDown();
        
        verify(mockCursor).moveDown();
        verify(mockCursor, never()).moveTo(anyInt(), anyInt()); // No adjustment needed since cursor X fits in line
    }

    @Test
    @DisplayName("Should not move cursor down when at bottom of buffer")
    void shouldNotMoveCursorDownWhenAtBottomOfBuffer() {
        when(mockCursor.getY()).thenReturn(4);
        when(mockBuffer.getLineCount()).thenReturn(5);
        
        cursorManager.moveCursorDown();
        
        verify(mockCursor, never()).moveDown();
    }

    @Test
    @DisplayName("Should not move cursor down when buffer is null")
    void shouldNotMoveCursorDownWhenBufferIsNull() {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(null);
        
        cursorManager.moveCursorDown();
        
        verify(mockCursor, never()).moveDown();
    }

    @Test
    @DisplayName("Should move cursor to new line")
    void shouldMoveCursorToNewLine() {
        when(mockCursor.getY()).thenReturn(2);
        
        cursorManager.moveCursorToNewLine();
        
        verify(mockCursor).moveTo(0, 3);
    }

    @Test
    @DisplayName("Should not move to new line when cursor is null")
    void shouldNotMoveToNewLineWhenCursorIsNull() {
        when(mockTerminalUI.getCursor()).thenReturn(null);
        
        cursorManager.moveCursorToNewLine();
        
        verify(mockCursor, never()).moveTo(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should initialize cursor to origin when cursor and buffer exist")
    void shouldInitializeCursorToOriginWhenCursorAndBufferExist() {
        cursorManager.initializeCursor();
        
        verify(mockCursor).moveTo(0, 0);
    }

    @Test
    @DisplayName("Should not initialize cursor when cursor is null")
    void shouldNotInitializeCursorWhenCursorIsNull() {
        when(mockTerminalUI.getCursor()).thenReturn(null);
        
        cursorManager.initializeCursor();
        
        verify(mockCursor, never()).moveTo(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should not initialize cursor when buffer is null")
    void shouldNotInitializeCursorWhenBufferIsNull() {
        when(mockBufferManager.getCurrentBuffer()).thenReturn(null);
        
        cursorManager.initializeCursor();
        
        verify(mockCursor, never()).moveTo(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should constrain cursor Y position within buffer bounds")
    void shouldConstrainCursorYPositionWithinBufferBounds() {
        when(mockCursor.getX()).thenReturn(2);
        when(mockCursor.getY()).thenReturn(10);
        when(mockBuffer.getLineCount()).thenReturn(5);
        when(mockBuffer.getLine(4)).thenReturn("Last line");
        
        cursorManager.constrainCursorToBuffer(mockBuffer);
        
        verify(mockCursor).moveTo(2, 4); // Constrained to last line
    }

    @Test
    @DisplayName("Should constrain cursor X position within line bounds")
    void shouldConstrainCursorXPositionWithinLineBounds() {
        when(mockCursor.getX()).thenReturn(20);
        when(mockCursor.getY()).thenReturn(2);
        when(mockBuffer.getLineCount()).thenReturn(5);
        when(mockBuffer.getLine(2)).thenReturn("Short");
        
        cursorManager.constrainCursorToBuffer(mockBuffer);
        
        verify(mockCursor).moveTo(5, 2); // Constrained to end of line
    }

    @Test
    @DisplayName("Should constrain negative cursor Y position to zero")
    void shouldConstrainNegativeCursorYPositionToZero() {
        when(mockCursor.getX()).thenReturn(2);
        when(mockCursor.getY()).thenReturn(-5);
        when(mockBuffer.getLineCount()).thenReturn(5);
        when(mockBuffer.getLine(0)).thenReturn("First line");
        
        cursorManager.constrainCursorToBuffer(mockBuffer);
        
        verify(mockCursor).moveTo(2, 0);
    }

    @Test
    @DisplayName("Should constrain negative cursor X position to zero")
    void shouldConstrainNegativeCursorXPositionToZero() {
        when(mockCursor.getX()).thenReturn(-3);
        when(mockCursor.getY()).thenReturn(2);
        when(mockBuffer.getLineCount()).thenReturn(5);
        when(mockBuffer.getLine(2)).thenReturn("Some line");
        
        cursorManager.constrainCursorToBuffer(mockBuffer);
        
        verify(mockCursor).moveTo(0, 2);
    }

    @Test
    @DisplayName("Should not constrain cursor when cursor is null")
    void shouldNotConstrainCursorWhenCursorIsNull() {
        when(mockTerminalUI.getCursor()).thenReturn(null);
        
        cursorManager.constrainCursorToBuffer(mockBuffer);
        
        verify(mockCursor, never()).moveTo(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should not constrain cursor when buffer is null")
    void shouldNotConstrainCursorWhenBufferIsNull() {
        cursorManager.constrainCursorToBuffer(null);
        
        verify(mockCursor, never()).moveTo(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should not update cursor position when already at correct position")
    void shouldNotUpdateCursorPositionWhenAlreadyAtCorrectPosition() {
        when(mockCursor.getX()).thenReturn(2);
        when(mockCursor.getY()).thenReturn(1);
        when(mockBuffer.getLineCount()).thenReturn(5);
        when(mockBuffer.getLine(1)).thenReturn("Valid line content");
        
        cursorManager.constrainCursorToBuffer(mockBuffer);
        
        verify(mockCursor, never()).moveTo(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should handle empty line when constraining cursor X")
    void shouldHandleEmptyLineWhenConstrainingCursorX() {
        when(mockCursor.getX()).thenReturn(5);
        when(mockCursor.getY()).thenReturn(1);
        when(mockBuffer.getLineCount()).thenReturn(3);
        when(mockBuffer.getLine(1)).thenReturn("");
        
        cursorManager.constrainCursorToBuffer(mockBuffer);
        
        verify(mockCursor).moveTo(0, 1);
    }

    @Test
    @DisplayName("Should handle null line when constraining cursor X")
    void shouldHandleNullLineWhenConstrainingCursorX() {
        when(mockCursor.getX()).thenReturn(5);
        when(mockCursor.getY()).thenReturn(1);
        when(mockBuffer.getLineCount()).thenReturn(3);
        when(mockBuffer.getLine(1)).thenReturn(null);
        
        cursorManager.constrainCursorToBuffer(mockBuffer);
        
        verify(mockCursor).moveTo(0, 1);
    }

    @Test
    @DisplayName("Should return cursor Y position when cursor exists")
    void shouldReturnCursorYPositionWhenCursorExists() {
        when(mockCursor.getY()).thenReturn(5);
        
        int result = cursorManager.getCursorY();
        
        assertEquals(5, result);
    }

    @Test
    @DisplayName("Should return zero when cursor is null")
    void shouldReturnZeroWhenCursorIsNull() {
        when(mockTerminalUI.getCursor()).thenReturn(null);
        
        int result = cursorManager.getCursorY();
        
        assertEquals(0, result);
    }
}