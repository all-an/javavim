package com.javavim.editor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import com.javavim.editor.UndoRedoManager.EditOperation;
import com.javavim.editor.UndoRedoManager.OperationType;

class UndoRedoManagerTest {
    
    private UndoRedoManager undoRedoManager;
    
    @BeforeEach
    void setUp() {
        undoRedoManager = new UndoRedoManager();
    }
    
    @Test
    @DisplayName("Should create empty undo/redo manager")
    void shouldCreateEmptyUndoRedoManager() {
        assertFalse(undoRedoManager.canUndo());
        assertFalse(undoRedoManager.canRedo());
        assertEquals(0, undoRedoManager.getUndoStackSize());
        assertEquals(0, undoRedoManager.getRedoStackSize());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid max history size")
    void shouldThrowExceptionForInvalidMaxHistorySize() {
        assertThrows(IllegalArgumentException.class, () -> new UndoRedoManager(0));
        assertThrows(IllegalArgumentException.class, () -> new UndoRedoManager(-1));
    }
    
    @Test
    @DisplayName("Should add operation")
    void shouldAddOperation() {
        EditOperation operation = new EditOperation(OperationType.INSERT_TEXT, 0, 0, "", "Hello");
        
        undoRedoManager.addOperation(operation);
        
        assertTrue(undoRedoManager.canUndo());
        assertFalse(undoRedoManager.canRedo());
        assertEquals(1, undoRedoManager.getUndoStackSize());
    }
    
    @Test
    @DisplayName("Should throw exception for null operation")
    void shouldThrowExceptionForNullOperation() {
        assertThrows(IllegalArgumentException.class, () -> undoRedoManager.addOperation(null));
    }
    
    @Test
    @DisplayName("Should undo operation")
    void shouldUndoOperation() {
        EditOperation operation = new EditOperation(OperationType.INSERT_TEXT, 0, 0, "", "Hello");
        undoRedoManager.addOperation(operation);
        
        EditOperation undoneOperation = undoRedoManager.undo();
        
        assertEquals(operation, undoneOperation);
        assertFalse(undoRedoManager.canUndo());
        assertTrue(undoRedoManager.canRedo());
        assertEquals(0, undoRedoManager.getUndoStackSize());
        assertEquals(1, undoRedoManager.getRedoStackSize());
    }
    
    @Test
    @DisplayName("Should return null when cannot undo")
    void shouldReturnNullWhenCannotUndo() {
        assertNull(undoRedoManager.undo());
    }
    
    @Test
    @DisplayName("Should redo operation")
    void shouldRedoOperation() {
        EditOperation operation = new EditOperation(OperationType.INSERT_TEXT, 0, 0, "", "Hello");
        undoRedoManager.addOperation(operation);
        undoRedoManager.undo();
        
        EditOperation redoneOperation = undoRedoManager.redo();
        
        assertEquals(operation, redoneOperation);
        assertTrue(undoRedoManager.canUndo());
        assertFalse(undoRedoManager.canRedo());
        assertEquals(1, undoRedoManager.getUndoStackSize());
        assertEquals(0, undoRedoManager.getRedoStackSize());
    }
    
    @Test
    @DisplayName("Should return null when cannot redo")
    void shouldReturnNullWhenCannotRedo() {
        assertNull(undoRedoManager.redo());
    }
    
    @Test
    @DisplayName("Should clear redo stack when adding new operation")
    void shouldClearRedoStackWhenAddingNewOperation() {
        EditOperation operation1 = new EditOperation(OperationType.INSERT_TEXT, 0, 0, "", "Hello");
        EditOperation operation2 = new EditOperation(OperationType.INSERT_TEXT, 0, 5, "", " World");
        
        undoRedoManager.addOperation(operation1);
        undoRedoManager.undo();
        assertTrue(undoRedoManager.canRedo());
        
        undoRedoManager.addOperation(operation2);
        assertFalse(undoRedoManager.canRedo());
        assertEquals(0, undoRedoManager.getRedoStackSize());
    }
    
    @Test
    @DisplayName("Should clear both stacks")
    void shouldClearBothStacks() {
        EditOperation operation = new EditOperation(OperationType.INSERT_TEXT, 0, 0, "", "Hello");
        undoRedoManager.addOperation(operation);
        undoRedoManager.undo();
        
        undoRedoManager.clear();
        
        assertFalse(undoRedoManager.canUndo());
        assertFalse(undoRedoManager.canRedo());
        assertEquals(0, undoRedoManager.getUndoStackSize());
        assertEquals(0, undoRedoManager.getRedoStackSize());
    }
    
    @Test
    @DisplayName("Should enforce history limit")
    void shouldEnforceHistoryLimit() {
        UndoRedoManager limitedManager = new UndoRedoManager(2);
        
        EditOperation op1 = new EditOperation(OperationType.INSERT_TEXT, 0, 0, "", "1");
        EditOperation op2 = new EditOperation(OperationType.INSERT_TEXT, 0, 1, "", "2");
        EditOperation op3 = new EditOperation(OperationType.INSERT_TEXT, 0, 2, "", "3");
        
        limitedManager.addOperation(op1);
        limitedManager.addOperation(op2);
        limitedManager.addOperation(op3);
        
        assertEquals(2, limitedManager.getUndoStackSize());
        
        EditOperation undone1 = limitedManager.undo();
        assertEquals(op3, undone1);
        
        EditOperation undone2 = limitedManager.undo();
        assertEquals(op2, undone2);
        
        assertFalse(limitedManager.canUndo());
    }
    
    @Test
    @DisplayName("Should handle multiple undo/redo operations")
    void shouldHandleMultipleUndoRedoOperations() {
        EditOperation op1 = new EditOperation(OperationType.INSERT_TEXT, 0, 0, "", "Hello");
        EditOperation op2 = new EditOperation(OperationType.INSERT_TEXT, 0, 5, "", " World");
        EditOperation op3 = new EditOperation(OperationType.DELETE_TEXT, 0, 4, "o", "");
        
        undoRedoManager.addOperation(op1);
        undoRedoManager.addOperation(op2);
        undoRedoManager.addOperation(op3);
        
        assertEquals(op3, undoRedoManager.undo());
        assertEquals(op2, undoRedoManager.undo());
        assertEquals(op1, undoRedoManager.undo());
        assertFalse(undoRedoManager.canUndo());
        
        assertEquals(op1, undoRedoManager.redo());
        assertEquals(op2, undoRedoManager.redo());
        assertEquals(op3, undoRedoManager.redo());
        assertFalse(undoRedoManager.canRedo());
    }
    
    @Test
    @DisplayName("Should create edit operation with all fields")
    void shouldCreateEditOperationWithAllFields() {
        EditOperation operation = new EditOperation(
            OperationType.REPLACE_TEXT, 5, 10, "old text", "new text"
        );
        
        assertEquals(OperationType.REPLACE_TEXT, operation.getType());
        assertEquals(5, operation.getLineNumber());
        assertEquals(10, operation.getPosition());
        assertEquals("old text", operation.getOldText());
        assertEquals("new text", operation.getNewText());
    }
    
    @Test
    @DisplayName("Should handle different operation types")
    void shouldHandleDifferentOperationTypes() {
        EditOperation insert = new EditOperation(OperationType.INSERT_TEXT, 0, 0, "", "text");
        EditOperation delete = new EditOperation(OperationType.DELETE_TEXT, 1, 5, "text", "");
        EditOperation replace = new EditOperation(OperationType.REPLACE_TEXT, 2, 10, "old", "new");
        EditOperation insertLine = new EditOperation(OperationType.INSERT_LINE, 3, 0, "", "new line");
        EditOperation deleteLine = new EditOperation(OperationType.DELETE_LINE, 4, 0, "line", "");
        
        undoRedoManager.addOperation(insert);
        undoRedoManager.addOperation(delete);
        undoRedoManager.addOperation(replace);
        undoRedoManager.addOperation(insertLine);
        undoRedoManager.addOperation(deleteLine);
        
        assertEquals(5, undoRedoManager.getUndoStackSize());
        
        assertEquals(deleteLine, undoRedoManager.undo());
        assertEquals(insertLine, undoRedoManager.undo());
        assertEquals(replace, undoRedoManager.undo());
        assertEquals(delete, undoRedoManager.undo());
        assertEquals(insert, undoRedoManager.undo());
    }
}