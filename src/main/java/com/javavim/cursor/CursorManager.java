package com.javavim.cursor;

import com.javavim.buffer.Buffer;
import com.javavim.buffer.BufferManager;
import com.javavim.terminal.TerminalUI;

/**
 * Manages cursor operations and positioning.
 * Follows single responsibility principle - handles cursor operations only.
 */
public class CursorManager {
    
    private final TerminalUI terminalUI;
    private final BufferManager bufferManager;
    
    public CursorManager(TerminalUI terminalUI, BufferManager bufferManager) {
        this.terminalUI = terminalUI;
        this.bufferManager = bufferManager;
    }
    
    public void moveCursorLeft() {
        if (canMoveCursor()) {
            if (terminalUI.getCursor().getX() > 0) {
                terminalUI.getCursor().moveLeft();
            }
        }
    }
    
    public void moveCursorRight() {
        if (canMoveCursor()) {
            Buffer currentBuffer = bufferManager.getCurrentBuffer();
            String currentLine = getCurrentLine(currentBuffer);
            if (canMoveRightInLine(currentLine)) {
                terminalUI.getCursor().moveRight();
            }
        }
    }
    
    public void moveCursorUp() {
        if (canMoveCursor()) {
            if (terminalUI.getCursor().getY() > 0) {
                terminalUI.getCursor().moveUp();
                adjustCursorToLineLength();
            }
        }
    }
    
    public void moveCursorDown() {
        if (canMoveCursor()) {
            Buffer currentBuffer = bufferManager.getCurrentBuffer();
            if (canMoveDownInBuffer(currentBuffer)) {
                terminalUI.getCursor().moveDown();
                adjustCursorToLineLength();
            }
        }
    }
    
    public void moveCursorToNewLine() {
        if (terminalUI.getCursor() != null) {
            int newY = terminalUI.getCursor().getY() + 1;
            terminalUI.getCursor().moveTo(0, newY);
        }
    }
    
    public void initializeCursor() {
        if (hasCursorAndBuffer()) {
            terminalUI.getCursor().moveTo(0, 0);
        }
    }
    
    public void constrainCursorToBuffer(Buffer buffer) {
        if (terminalUI.getCursor() == null || buffer == null) {
            return;
        }
        
        int cursorY = constrainCursorY(buffer);
        int cursorX = constrainCursorX(buffer, cursorY);
        updateCursorIfChanged(cursorX, cursorY);
    }
    
    public int getCursorY() {
        if (terminalUI.getCursor() != null) {
            return terminalUI.getCursor().getY();
        }
        return 0;
    }
    
    private boolean canMoveCursor() {
        return terminalUI.getCursor() != null && bufferManager.getCurrentBuffer() != null;
    }
    
    private String getCurrentLine(Buffer buffer) {
        if (buffer != null) {
            int currentRow = terminalUI.getCursor().getY();
            return buffer.getLine(currentRow);
        }
        return null;
    }
    
    private boolean canMoveRightInLine(String line) {
        if (line == null) return false;
        int currentCol = terminalUI.getCursor().getX();
        return currentCol < line.length();
    }
    
    private boolean canMoveDownInBuffer(Buffer buffer) {
        if (buffer == null) return false;
        int currentRow = terminalUI.getCursor().getY();
        return currentRow < buffer.getLineCount() - 1;
    }
    
    private void adjustCursorToLineLength() {
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        String currentLine = getCurrentLine(currentBuffer);
        if (currentLine != null) {
            int currentCol = terminalUI.getCursor().getX();
            if (currentCol > currentLine.length()) {
                terminalUI.getCursor().moveTo(currentLine.length(), terminalUI.getCursor().getY());
            }
        }
    }
    
    private boolean hasCursorAndBuffer() {
        return terminalUI.getCursor() != null && bufferManager.getCurrentBuffer() != null;
    }
    
    private int constrainCursorY(Buffer buffer) {
        int cursorY = terminalUI.getCursor().getY();
        int maxY = Math.max(0, buffer.getLineCount() - 1);
        
        if (cursorY > maxY) {
            return maxY;
        }
        if (cursorY < 0) {
            return 0;
        }
        return cursorY;
    }
    
    private int constrainCursorX(Buffer buffer, int cursorY) {
        int cursorX = terminalUI.getCursor().getX();
        String currentLine = buffer.getLine(cursorY);
        int maxX = currentLine != null ? currentLine.length() : 0;
        
        if (cursorX > maxX) {
            return maxX;
        }
        if (cursorX < 0) {
            return 0;
        }
        return cursorX;
    }
    
    private void updateCursorIfChanged(int newX, int newY) {
        boolean yChanged = newY != terminalUI.getCursor().getY();
        boolean xChanged = newX != terminalUI.getCursor().getX();
        
        if (yChanged || xChanged) {
            terminalUI.getCursor().moveTo(newX, newY);
        }
    }
}