package com.javavim.buffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ScreenBufferTest {
    
    private ScreenBuffer screenBuffer;
    
    @BeforeEach
    void setUp() {
        screenBuffer = new ScreenBuffer(80, 24);
    }
    
    @Test
    @DisplayName("Should create screen buffer with correct dimensions")
    void shouldCreateScreenBufferWithCorrectDimensions() {
        assertEquals(80, screenBuffer.getWidth());
        assertEquals(24, screenBuffer.getHeight());
        assertTrue(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid dimensions")
    void shouldThrowExceptionForInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new ScreenBuffer(0, 24));
        assertThrows(IllegalArgumentException.class, () -> new ScreenBuffer(80, 0));
        assertThrows(IllegalArgumentException.class, () -> new ScreenBuffer(-1, 24));
        assertThrows(IllegalArgumentException.class, () -> new ScreenBuffer(80, -1));
    }
    
    @Test
    @DisplayName("Should set character at valid position")
    void shouldSetCharacterAtValidPosition() {
        screenBuffer.setChar(5, 10, 'A');
        
        assertEquals('A', screenBuffer.getChar(5, 10));
        assertTrue(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should ignore setting character at invalid position")
    void shouldIgnoreSettingCharacterAtInvalidPosition() {
        screenBuffer.setDirty(false);
        
        screenBuffer.setChar(-1, 10, 'A');
        screenBuffer.setChar(5, -1, 'A');
        screenBuffer.setChar(80, 10, 'A');
        screenBuffer.setChar(5, 24, 'A');
        
        assertFalse(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should return space for invalid position when getting character")
    void shouldReturnSpaceForInvalidPositionWhenGettingCharacter() {
        assertEquals(' ', screenBuffer.getChar(-1, 10));
        assertEquals(' ', screenBuffer.getChar(5, -1));
        assertEquals(' ', screenBuffer.getChar(80, 10));
        assertEquals(' ', screenBuffer.getChar(5, 24));
    }
    
    @Test
    @DisplayName("Should set string at valid position")
    void shouldSetStringAtValidPosition() {
        screenBuffer.setString(5, 10, "Hello");
        
        assertEquals('H', screenBuffer.getChar(5, 10));
        assertEquals('e', screenBuffer.getChar(6, 10));
        assertEquals('l', screenBuffer.getChar(7, 10));
        assertEquals('l', screenBuffer.getChar(8, 10));
        assertEquals('o', screenBuffer.getChar(9, 10));
        assertTrue(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should truncate string that exceeds line width")
    void shouldTruncateStringThatExceedsLineWidth() {
        screenBuffer.setString(78, 10, "HelloWorld");
        
        assertEquals('H', screenBuffer.getChar(78, 10));
        assertEquals('e', screenBuffer.getChar(79, 10));
        assertEquals(' ', screenBuffer.getChar(79, 11)); // Should not wrap
    }
    
    @Test
    @DisplayName("Should throw exception for null string")
    void shouldThrowExceptionForNullString() {
        assertThrows(IllegalArgumentException.class, () -> 
            screenBuffer.setString(5, 10, null));
    }
    
    @Test
    @DisplayName("Should ignore setting string at invalid position")
    void shouldIgnoreSettingStringAtInvalidPosition() {
        screenBuffer.setDirty(false);
        
        screenBuffer.setString(-1, 10, "Hello");
        screenBuffer.setString(5, -1, "Hello");
        screenBuffer.setString(80, 10, "Hello");
        screenBuffer.setString(5, 24, "Hello");
        
        assertFalse(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should get line content correctly")
    void shouldGetLineContentCorrectly() {
        screenBuffer.setString(0, 5, "Hello World");
        
        String line = screenBuffer.getLine(5);
        assertTrue(line.startsWith("Hello World"));
        assertEquals(80, line.length()); // Full width
    }
    
    @Test
    @DisplayName("Should return empty string for invalid line number")
    void shouldReturnEmptyStringForInvalidLineNumber() {
        assertEquals("", screenBuffer.getLine(-1));
        assertEquals("", screenBuffer.getLine(24));
    }
    
    @Test
    @DisplayName("Should clear entire buffer")
    void shouldClearEntireBuffer() {
        screenBuffer.setString(10, 10, "Test");
        screenBuffer.setDirty(false);
        
        screenBuffer.clear();
        
        assertEquals(' ', screenBuffer.getChar(10, 10));
        assertTrue(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should clear specific line")
    void shouldClearSpecificLine() {
        screenBuffer.setString(10, 5, "Test");
        screenBuffer.setString(10, 6, "Keep");
        screenBuffer.setDirty(false);
        
        screenBuffer.clearLine(5);
        
        assertEquals(' ', screenBuffer.getChar(10, 5));
        assertEquals('K', screenBuffer.getChar(10, 6));
        assertTrue(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should ignore clearing invalid line number")
    void shouldIgnoreClearingInvalidLineNumber() {
        screenBuffer.setDirty(false);
        
        screenBuffer.clearLine(-1);
        screenBuffer.clearLine(24);
        
        assertFalse(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should track dirty state correctly")
    void shouldTrackDirtyStateCorrectly() {
        screenBuffer.setDirty(false);
        assertFalse(screenBuffer.isDirty());
        
        screenBuffer.setChar(5, 5, 'X');
        assertTrue(screenBuffer.isDirty());
        
        screenBuffer.setDirty(false);
        assertFalse(screenBuffer.isDirty());
    }
    
    @Test
    @DisplayName("Should initialize with all spaces")
    void shouldInitializeWithAllSpaces() {
        ScreenBuffer newBuffer = new ScreenBuffer(10, 5);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 10; x++) {
                assertEquals(' ', newBuffer.getChar(x, y));
            }
        }
    }
}