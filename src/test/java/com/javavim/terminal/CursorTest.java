package com.javavim.terminal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CursorTest {
    
    private Cursor cursor;
    
    @BeforeEach
    void setUp() {
        cursor = new Cursor(80, 24);
    }
    
    @Test
    @DisplayName("Should create cursor at origin position")
    void shouldCreateCursorAtOriginPosition() {
        assertEquals(0, cursor.getX());
        assertEquals(0, cursor.getY());
        assertTrue(cursor.isVisible());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid maximum coordinates")
    void shouldThrowExceptionForInvalidMaximumCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new Cursor(0, 24));
        assertThrows(IllegalArgumentException.class, () -> new Cursor(80, 0));
        assertThrows(IllegalArgumentException.class, () -> new Cursor(-1, 24));
        assertThrows(IllegalArgumentException.class, () -> new Cursor(80, -1));
    }
    
    @Test
    @DisplayName("Should move to valid position")
    void shouldMoveToValidPosition() {
        cursor.moveTo(10, 5);
        
        assertEquals(10, cursor.getX());
        assertEquals(5, cursor.getY());
    }
    
    @Test
    @DisplayName("Should ignore move to invalid position")
    void shouldIgnoreMoveToInvalidPosition() {
        cursor.moveTo(5, 5);
        
        cursor.moveTo(-1, 5);
        assertEquals(5, cursor.getX());
        assertEquals(5, cursor.getY());
        
        cursor.moveTo(5, -1);
        assertEquals(5, cursor.getX());
        assertEquals(5, cursor.getY());
        
        cursor.moveTo(80, 5);
        assertEquals(5, cursor.getX());
        assertEquals(5, cursor.getY());
        
        cursor.moveTo(5, 24);
        assertEquals(5, cursor.getX());
        assertEquals(5, cursor.getY());
    }
    
    @Test
    @DisplayName("Should move left when possible")
    void shouldMoveLeftWhenPossible() {
        cursor.moveTo(5, 5);
        cursor.moveLeft();
        
        assertEquals(4, cursor.getX());
        assertEquals(5, cursor.getY());
    }
    
    @Test
    @DisplayName("Should not move left from leftmost position")
    void shouldNotMoveLeftFromLeftmostPosition() {
        cursor.moveTo(0, 5);
        cursor.moveLeft();
        
        assertEquals(0, cursor.getX());
        assertEquals(5, cursor.getY());
    }
    
    @Test
    @DisplayName("Should move right when possible")
    void shouldMoveRightWhenPossible() {
        cursor.moveTo(5, 5);
        cursor.moveRight();
        
        assertEquals(6, cursor.getX());
        assertEquals(5, cursor.getY());
    }
    
    @Test
    @DisplayName("Should not move right from rightmost position")
    void shouldNotMoveRightFromRightmostPosition() {
        cursor.moveTo(79, 5);
        cursor.moveRight();
        
        assertEquals(79, cursor.getX());
        assertEquals(5, cursor.getY());
    }
    
    @Test
    @DisplayName("Should move up when possible")
    void shouldMoveUpWhenPossible() {
        cursor.moveTo(5, 5);
        cursor.moveUp();
        
        assertEquals(5, cursor.getX());
        assertEquals(4, cursor.getY());
    }
    
    @Test
    @DisplayName("Should not move up from topmost position")
    void shouldNotMoveUpFromTopmostPosition() {
        cursor.moveTo(5, 0);
        cursor.moveUp();
        
        assertEquals(5, cursor.getX());
        assertEquals(0, cursor.getY());
    }
    
    @Test
    @DisplayName("Should move down when possible")
    void shouldMoveDownWhenPossible() {
        cursor.moveTo(5, 5);
        cursor.moveDown();
        
        assertEquals(5, cursor.getX());
        assertEquals(6, cursor.getY());
    }
    
    @Test
    @DisplayName("Should not move down from bottommost position")
    void shouldNotMoveDownFromBottommostPosition() {
        cursor.moveTo(5, 23);
        cursor.moveDown();
        
        assertEquals(5, cursor.getX());
        assertEquals(23, cursor.getY());
    }
    
    @Test
    @DisplayName("Should move to start of line")
    void shouldMoveToStartOfLine() {
        cursor.moveTo(40, 10);
        cursor.moveToStartOfLine();
        
        assertEquals(0, cursor.getX());
        assertEquals(10, cursor.getY());
    }
    
    @Test
    @DisplayName("Should move to end of line")
    void shouldMoveToEndOfLine() {
        cursor.moveTo(10, 5);
        cursor.moveToEndOfLine();
        
        assertEquals(79, cursor.getX());
        assertEquals(5, cursor.getY());
    }
    
    @Test
    @DisplayName("Should move to top of screen")
    void shouldMoveToTopOfScreen() {
        cursor.moveTo(40, 10);
        cursor.moveToTop();
        
        assertEquals(40, cursor.getX());
        assertEquals(0, cursor.getY());
    }
    
    @Test
    @DisplayName("Should move to bottom of screen")
    void shouldMoveToBottomOfScreen() {
        cursor.moveTo(40, 5);
        cursor.moveToBottom();
        
        assertEquals(40, cursor.getX());
        assertEquals(23, cursor.getY());
    }
    
    @Test
    @DisplayName("Should set and get visibility")
    void shouldSetAndGetVisibility() {
        assertTrue(cursor.isVisible());
        
        cursor.setVisible(false);
        assertFalse(cursor.isVisible());
        
        cursor.setVisible(true);
        assertTrue(cursor.isVisible());
    }
    
    @Test
    @DisplayName("Should handle boundary movements correctly")
    void shouldHandleBoundaryMovementsCorrectly() {
        // Test all corners
        cursor.moveTo(0, 0);
        cursor.moveLeft();
        cursor.moveUp();
        assertEquals(0, cursor.getX());
        assertEquals(0, cursor.getY());
        
        cursor.moveTo(79, 23);
        cursor.moveRight();
        cursor.moveDown();
        assertEquals(79, cursor.getX());
        assertEquals(23, cursor.getY());
    }
}