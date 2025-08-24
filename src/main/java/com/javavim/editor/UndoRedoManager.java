package com.javavim.editor;

import java.util.Stack;

/**
 * Manages undo/redo operations for text editing.
 * Follows single responsibility principle - manages edit history only.
 */
public class UndoRedoManager {
    
    private final Stack<EditOperation> undoStack;
    private final Stack<EditOperation> redoStack;
    private final int maxHistorySize;
    
    public UndoRedoManager() {
        this(100); // Default max history size
    }
    
    public UndoRedoManager(int maxHistorySize) {
        if (maxHistorySize <= 0) {
            throw new IllegalArgumentException("Max history size must be positive");
        }
        
        this.maxHistorySize = maxHistorySize;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    public void addOperation(EditOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }
        
        undoStack.push(operation);
        redoStack.clear(); // Clear redo stack when new operation is added
        
        enforceHistoryLimit();
    }
    
    public EditOperation undo() {
        if (canUndo()) {
            EditOperation operation = undoStack.pop();
            redoStack.push(operation);
            return operation;
        }
        return null;
    }
    
    public EditOperation redo() {
        if (canRedo()) {
            EditOperation operation = redoStack.pop();
            undoStack.push(operation);
            return operation;
        }
        return null;
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
    
    public int getUndoStackSize() {
        return undoStack.size();
    }
    
    public int getRedoStackSize() {
        return redoStack.size();
    }
    
    private void enforceHistoryLimit() {
        while (undoStack.size() > maxHistorySize) {
            undoStack.remove(0); // Remove oldest operation
        }
    }
    
    /**
     * Represents a text editing operation that can be undone/redone.
     */
    public static class EditOperation {
        private final OperationType type;
        private final int lineNumber;
        private final int position;
        private final String oldText;
        private final String newText;
        
        public EditOperation(OperationType type, int lineNumber, int position, String oldText, String newText) {
            this.type = type;
            this.lineNumber = lineNumber;
            this.position = position;
            this.oldText = oldText;
            this.newText = newText;
        }
        
        public OperationType getType() {
            return type;
        }
        
        public int getLineNumber() {
            return lineNumber;
        }
        
        public int getPosition() {
            return position;
        }
        
        public String getOldText() {
            return oldText;
        }
        
        public String getNewText() {
            return newText;
        }
    }
    
    public enum OperationType {
        INSERT_TEXT,
        DELETE_TEXT,
        REPLACE_TEXT,
        INSERT_LINE,
        DELETE_LINE
    }
}