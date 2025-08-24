package com.javavim.input;

import com.javavim.Javavim;
import com.javavim.buffer.Buffer;
import com.javavim.buffer.BufferManager;
import com.javavim.display.StatusLine;
import com.javavim.command.CommandProcessor;
import java.io.IOException;

/**
 * Handles all input processing for different editor modes.
 * Follows single responsibility principle - processes input only.
 */
public class InputHandler {
    
    private final Javavim editor;
    private final BufferManager bufferManager;
    private final StatusLine statusLine;
    private final CommandProcessor commandProcessor;
    
    public InputHandler(Javavim editor, BufferManager bufferManager, 
                       StatusLine statusLine, CommandProcessor commandProcessor) {
        this.editor = editor;
        this.bufferManager = bufferManager;
        this.statusLine = statusLine;
        this.commandProcessor = commandProcessor;
    }
    
    public void handleInput(char input, Javavim.EditorMode currentMode) throws IOException {
        if (input == 0) {
            return;
        }
        
        try {
            processInputByMode(input, currentMode);
        } catch (Exception e) {
            statusLine.setMessage("Error: " + e.getMessage());
        }
    }
    
    private void processInputByMode(char input, Javavim.EditorMode currentMode) throws IOException {
        switch (currentMode) {
            case NORMAL:
                handleNormalModeInput(input);
                break;
            case INSERT:
                handleInsertModeInput(input);
                break;
            case COMMAND:
                handleCommandModeInput(input);
                break;
            case VISUAL:
                handleVisualModeInput(input);
                break;
        }
    }
    
    private void handleNormalModeInput(char input) throws IOException {
        if (tryHandleQuitCommand(input)) {
            return;
        }
        
        if (tryHandleModeSwitch(input)) {
            return;
        }
        
        handleOtherNormalCommands(input);
    }
    
    private boolean tryHandleQuitCommand(char input) {
        if (input == 'q') {
            if (bufferManager.hasModifiedBuffers()) {
                statusLine.setMessage("Warning: Unsaved changes! Use :q! to force quit");
            } else {
                editor.setRunning(false);
            }
            return true;
        }
        return false;
    }
    
    private boolean tryHandleModeSwitch(char input) {
        if (input == 'i') {
            editor.setMode(Javavim.EditorMode.INSERT);
            statusLine.setMessage("-- INSERT --");
            return true;
        }
        
        if (input == ':' || input == '/') {
            editor.setMode(Javavim.EditorMode.COMMAND);
            statusLine.setMessage(String.valueOf(input));
            return true;
        }
        
        return false;
    }
    
    private void handleOtherNormalCommands(char input) {
        switch (input) {
            case 'n':
                searchNext();
                break;
            case 'u':
                undo();
                break;
            case 18: // Ctrl+R
                redo();
                break;
            case 'h':
                editor.moveCursorLeft();
                break;
            case 'j':
                editor.moveCursorDown();
                break;
            case 'k':
                editor.moveCursorUp();
                break;
            case 'l':
                editor.moveCursorRight();
                break;
            default:
                statusLine.setMessage("Unknown command: " + input);
                break;
        }
    }
    
    private void handleInsertModeInput(char input) {
        if (isEscapeKey(input)) {
            exitInsertMode();
            return;
        }
        
        processInsertModeInput(input);
    }
    
    private boolean isEscapeKey(char input) {
        return input == 27;
    }
    
    private void exitInsertMode() {
        editor.setMode(Javavim.EditorMode.NORMAL);
        statusLine.setMessage("Exited insert mode");
    }
    
    private void processInsertModeInput(char input) {
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer != null) {
            handleInsertInput(currentBuffer, input);
        }
    }
    
    private void handleInsertInput(Buffer buffer, char input) {
        if (isEnterKey(input)) {
            handleEnterKey(buffer);
        } else {
            handleRegularInput(buffer, input);
        }
    }
    
    private boolean isEnterKey(char input) {
        return input == '\r' || input == '\n';
    }
    
    private void handleEnterKey(Buffer buffer) {
        int currentRow = editor.getCursorY();
        insertNewLineAt(buffer, currentRow + 1);
        editor.moveCursorToNewLine();
        statusLine.setMessage("INSERT - New line added");
    }
    
    private void insertNewLineAt(Buffer buffer, int lineNumber) {
        if (lineNumber <= buffer.getLineCount()) {
            buffer.insertLine(lineNumber, "");
        }
    }
    
    private void handleRegularInput(Buffer buffer, char input) {
        statusLine.setMessage("INSERT - Key: " + (int)input + " (" + input + ") - Press ESC to exit");
    }
    
    private void handleCommandModeInput(char input) throws IOException {
        if (input == 27) {
            exitCommandMode();
            return;
        }
        
        if (input == '\r' || input == '\n') {
            executeCurrentCommand();
        }
    }
    
    private void exitCommandMode() {
        editor.setMode(Javavim.EditorMode.NORMAL);
        statusLine.clearMessage();
    }
    
    private void executeCurrentCommand() throws IOException {
        commandProcessor.executeCommand(statusLine.getMessage());
        editor.setMode(Javavim.EditorMode.NORMAL);
        statusLine.clearMessage();
    }
    
    private void handleVisualModeInput(char input) {
        if (input == 27) {
            editor.setMode(Javavim.EditorMode.NORMAL);
            statusLine.clearMessage();
        }
    }
    
    private void searchNext() {
        statusLine.setMessage("Next search functionality");
    }
    
    private void undo() {
        statusLine.setMessage("Undo functionality");
    }
    
    private void redo() {
        statusLine.setMessage("Redo functionality");
    }
}