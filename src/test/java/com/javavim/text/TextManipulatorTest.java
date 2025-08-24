package com.javavim.text;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class TextManipulatorTest {
    
    private TextManipulator textManipulator;
    
    @BeforeEach
    void setUp() {
        textManipulator = new TextManipulator();
    }
    
    @Test
    @DisplayName("Should insert text at valid position")
    void shouldInsertTextAtValidPosition() {
        String result = textManipulator.insertTextAtPosition("Hello World", 5, " Java");
        
        assertEquals("Hello Java World", result);
    }
    
    @Test
    @DisplayName("Should insert text at beginning")
    void shouldInsertTextAtBeginning() {
        String result = textManipulator.insertTextAtPosition("World", 0, "Hello ");
        
        assertEquals("Hello World", result);
    }
    
    @Test
    @DisplayName("Should insert text at end")
    void shouldInsertTextAtEnd() {
        String result = textManipulator.insertTextAtPosition("Hello", 5, " World");
        
        assertEquals("Hello World", result);
    }
    
    @Test
    @DisplayName("Should return original line for invalid insert position")
    void shouldReturnOriginalLineForInvalidInsertPosition() {
        String original = "Hello World";
        
        String result1 = textManipulator.insertTextAtPosition(original, -1, " Java");
        String result2 = textManipulator.insertTextAtPosition(original, 999, " Java");
        
        assertEquals(original, result1);
        assertEquals(original, result2);
    }
    
    @Test
    @DisplayName("Should throw exception for null parameters in insert")
    void shouldThrowExceptionForNullParametersInInsert() {
        assertThrows(IllegalArgumentException.class, () -> 
            textManipulator.insertTextAtPosition(null, 0, "text"));
        assertThrows(IllegalArgumentException.class, () -> 
            textManipulator.insertTextAtPosition("line", 0, null));
    }
    
    @Test
    @DisplayName("Should delete character at valid position")
    void shouldDeleteCharacterAtValidPosition() {
        String result = textManipulator.deleteCharAtPosition("Hello World", 5);
        
        assertEquals("HelloWorld", result);
    }
    
    @Test
    @DisplayName("Should delete first character")
    void shouldDeleteFirstCharacter() {
        String result = textManipulator.deleteCharAtPosition("Hello", 0);
        
        assertEquals("ello", result);
    }
    
    @Test
    @DisplayName("Should delete last character")
    void shouldDeleteLastCharacter() {
        String result = textManipulator.deleteCharAtPosition("Hello", 4);
        
        assertEquals("Hell", result);
    }
    
    @Test
    @DisplayName("Should return original line for invalid delete position")
    void shouldReturnOriginalLineForInvalidDeletePosition() {
        String original = "Hello";
        
        String result1 = textManipulator.deleteCharAtPosition(original, -1);
        String result2 = textManipulator.deleteCharAtPosition(original, 5);
        
        assertEquals(original, result1);
        assertEquals(original, result2);
    }
    
    @Test
    @DisplayName("Should throw exception for null line in delete char")
    void shouldThrowExceptionForNullLineInDeleteChar() {
        assertThrows(IllegalArgumentException.class, () -> 
            textManipulator.deleteCharAtPosition(null, 0));
    }
    
    @Test
    @DisplayName("Should delete range correctly")
    void shouldDeleteRangeCorrectly() {
        String result = textManipulator.deleteRange("Hello World", 2, 8);
        
        assertEquals("Herld", result);
    }
    
    @Test
    @DisplayName("Should delete from beginning to middle")
    void shouldDeleteFromBeginningToMiddle() {
        String result = textManipulator.deleteRange("Hello World", 0, 6);
        
        assertEquals("World", result);
    }
    
    @Test
    @DisplayName("Should delete from middle to end")
    void shouldDeleteFromMiddleToEnd() {
        String result = textManipulator.deleteRange("Hello World", 5, 11);
        
        assertEquals("Hello", result);
    }
    
    @Test
    @DisplayName("Should return original line for invalid range")
    void shouldReturnOriginalLineForInvalidRange() {
        String original = "Hello World";
        
        String result1 = textManipulator.deleteRange(original, -1, 5);
        String result2 = textManipulator.deleteRange(original, 5, 2);
        String result3 = textManipulator.deleteRange(original, 2, 99);
        
        assertEquals(original, result1);
        assertEquals(original, result2);
        assertEquals(original, result3);
    }
    
    @Test
    @DisplayName("Should throw exception for null line in delete range")
    void shouldThrowExceptionForNullLineInDeleteRange() {
        assertThrows(IllegalArgumentException.class, () -> 
            textManipulator.deleteRange(null, 0, 5));
    }
    
    @Test
    @DisplayName("Should get substring correctly")
    void shouldGetSubstringCorrectly() {
        String result = textManipulator.getSubstring("Hello World", 2, 7);
        
        assertEquals("llo W", result);
    }
    
    @Test
    @DisplayName("Should return empty string for invalid substring range")
    void shouldReturnEmptyStringForInvalidSubstringRange() {
        String result1 = textManipulator.getSubstring("Hello", -1, 3);
        String result2 = textManipulator.getSubstring("Hello", 3, 2);
        String result3 = textManipulator.getSubstring("Hello", 2, 99);
        
        assertEquals("", result1);
        assertEquals("", result2);
        assertEquals("", result3);
    }
    
    @Test
    @DisplayName("Should throw exception for null line in substring")
    void shouldThrowExceptionForNullLineInSubstring() {
        assertThrows(IllegalArgumentException.class, () -> 
            textManipulator.getSubstring(null, 0, 5));
    }
    
    @Test
    @DisplayName("Should find next word correctly")
    void shouldFindNextWordCorrectly() {
        int result = textManipulator.findNextWord("Hello World Java", 0);
        
        assertEquals(6, result);
    }
    
    @Test
    @DisplayName("Should find next word from middle of word")
    void shouldFindNextWordFromMiddleOfWord() {
        int result = textManipulator.findNextWord("Hello World Java", 2);
        
        assertEquals(6, result);
    }
    
    @Test
    @DisplayName("Should return same position when at end of line")
    void shouldReturnSamePositionWhenAtEndOfLine() {
        int result = textManipulator.findNextWord("Hello", 5);
        
        assertEquals(5, result);
    }
    
    @Test
    @DisplayName("Should throw exception for null line in find next word")
    void shouldThrowExceptionForNullLineInFindNextWord() {
        assertThrows(IllegalArgumentException.class, () -> 
            textManipulator.findNextWord(null, 0));
    }
    
    @Test
    @DisplayName("Should find previous word correctly")
    void shouldFindPreviousWordCorrectly() {
        int result = textManipulator.findPreviousWord("Hello World Java", 12);
        
        assertEquals(6, result);
    }
    
    @Test
    @DisplayName("Should find previous word from beginning of word")
    void shouldFindPreviousWordFromBeginningOfWord() {
        int result = textManipulator.findPreviousWord("Hello World Java", 6);
        
        assertEquals(0, result);
    }
    
    @Test
    @DisplayName("Should return zero when at start of line")
    void shouldReturnZeroWhenAtStartOfLine() {
        int result = textManipulator.findPreviousWord("Hello World", 0);
        
        assertEquals(0, result);
    }
    
    @Test
    @DisplayName("Should throw exception for null line in find previous word")
    void shouldThrowExceptionForNullLineInFindPreviousWord() {
        assertThrows(IllegalArgumentException.class, () -> 
            textManipulator.findPreviousWord(null, 5));
    }
    
    @Test
    @DisplayName("Should identify whitespace characters correctly")
    void shouldIdentifyWhitespaceCharactersCorrectly() {
        assertTrue(textManipulator.isWhitespace(' '));
        assertTrue(textManipulator.isWhitespace('\t'));
        assertTrue(textManipulator.isWhitespace('\n'));
        assertFalse(textManipulator.isWhitespace('a'));
        assertFalse(textManipulator.isWhitespace('1'));
    }
    
    @Test
    @DisplayName("Should identify empty lines correctly")
    void shouldIdentifyEmptyLinesCorrectly() {
        assertTrue(textManipulator.isEmpty(null));
        assertTrue(textManipulator.isEmpty(""));
        assertTrue(textManipulator.isEmpty("   "));
        assertTrue(textManipulator.isEmpty("\t\n"));
        assertFalse(textManipulator.isEmpty("Hello"));
        assertFalse(textManipulator.isEmpty("  a  "));
    }
}