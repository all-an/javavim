package com.javavim.input;

import com.javavim.Javavim;
import com.javavim.buffer.Buffer;
import com.javavim.buffer.BufferManager;
import com.javavim.command.CommandProcessor;
import com.javavim.display.StatusLine;

import java.io.IOException;

/**
 * Modular input handler following code guidance principles.
 * Each method does one thing and returns a meaningful value.
 */
public class ModularInputHandler {
    
    private final Javavim editor;
    private final BufferManager bufferManager;
    private final StatusLine statusLine;
    private final CommandProcessor commandProcessor;
    
    public ModularInputHandler(Javavim editor, BufferManager bufferManager, 
                             StatusLine statusLine, CommandProcessor commandProcessor) {
        this.editor = editor;
        this.bufferManager = bufferManager;
        this.statusLine = statusLine;
        this.commandProcessor = commandProcessor;
    }
    
    public InputProcessingResult processInput(char input, Javavim.EditorMode currentMode) {
        if (input == 0) {
            return InputProcessingResult.ignored("Input ignored").build();
        }
        
        try {
            return processInputByMode(input, currentMode);
        } catch (Exception e) {
            statusLine.setMessage("Error: " + e.getMessage());
            return InputProcessingResult.error("Error: " + e.getMessage()).build();
        }
    }
    
    private InputProcessingResult processInputByMode(char input, Javavim.EditorMode currentMode) throws IOException {
        if (isNormalMode(currentMode)) {
            return handleNormalModeInput(input);
        }
        
        return handleOtherModeInput(input, currentMode);
    }
    
    private boolean isNormalMode(Javavim.EditorMode mode) {
        return mode == Javavim.EditorMode.NORMAL;
    }
    
    private InputProcessingResult handleOtherModeInput(char input, Javavim.EditorMode currentMode) throws IOException {
        if (currentMode == Javavim.EditorMode.INSERT) {
            return handleInsertModeInput(input);
        }
        
        return handleCommandOrVisualMode(input, currentMode);
    }
    
    private InputProcessingResult handleCommandOrVisualMode(char input, Javavim.EditorMode currentMode) throws IOException {
        if (currentMode == Javavim.EditorMode.COMMAND) {
            return handleCommandModeInput(input);
        }
        
        return handleVisualModeInput(input);
    }
    
    private InputProcessingResult handleNormalModeInput(char input) {
        if (isQuitCommand(input)) {
            return handleQuitCommand();
        }
        
        return handleOtherNormalCommands(input);
    }
    
    private boolean isQuitCommand(char input) {
        return input == 'q';
    }
    
    private InputProcessingResult handleQuitCommand() {
        if (bufferManager.hasModifiedBuffers()) {
            String message = "Warning: Unsaved changes! Use :q! to force quit";
            statusLine.setMessage(message);
            return InputProcessingResult.success(message).warning(true).build();
        }
        
        editor.setRunning(false);
        return InputProcessingResult.success("Editor quit").shouldQuit(true).build();
    }
    
    private InputProcessingResult handleOtherNormalCommands(char input) {
        if (isModeSwitch(input)) {
            return handleModeSwitch(input);
        }
        
        return handleMovementOrOther(input);
    }
    
    private boolean isModeSwitch(char input) {
        return input == 'i' || input == ':' || input == '/';
    }
    
    private InputProcessingResult handleModeSwitch(char input) {
        if (input == 'i') {
            return handleInsertModeSwitch();
        }
        
        return handleCommandModeSwitch(input);
    }
    
    private InputProcessingResult handleInsertModeSwitch() {
        editor.setMode(Javavim.EditorMode.INSERT);
        String message = "-- INSERT --";
        statusLine.setMessage(message);
        return InputProcessingResult.success(message)
                .modeChange(Javavim.EditorMode.INSERT).build();
    }
    
    private InputProcessingResult handleCommandModeSwitch(char input) {
        editor.setMode(Javavim.EditorMode.COMMAND);
        String message = String.valueOf(input);
        statusLine.setMessage(message);
        return InputProcessingResult.success(message)
                .modeChange(Javavim.EditorMode.COMMAND).build();
    }
    
    private InputProcessingResult handleMovementOrOther(char input) {
        if (isCursorMovement(input)) {
            return handleCursorMovement(input);
        }
        
        return handleUnknownCommand(input);
    }
    
    private boolean isCursorMovement(char input) {
        return input == 'h' || input == 'j' || input == 'k' || input == 'l';
    }
    
    private InputProcessingResult handleCursorMovement(char input) {
        String direction = getCursorDirection(input);
        executeCursorMovement(input);
        return InputProcessingResult.success("Cursor moved " + direction)
                .cursorMovement(direction).build();
    }
    
    private String getCursorDirection(char input) {
        if (input == 'h') return "left";
        if (input == 'j') return "down";
        if (input == 'k') return "up";
        return "right";
    }
    
    private InputProcessingResult executeCursorMovement(char input) {
        if (input == 'h') {
            editor.moveCursorLeft();
        } else if (input == 'j') {
            editor.moveCursorDown();
        } else if (input == 'k') {
            editor.moveCursorUp();
        } else {
            editor.moveCursorRight();
        }
        return InputProcessingResult.success("Movement executed").build();
    }
    
    private InputProcessingResult handleUnknownCommand(char input) {
        String message = "Unknown command: " + input;
        statusLine.setMessage(message);
        return InputProcessingResult.success(message).build();
    }
    
    private InputProcessingResult handleInsertModeInput(char input) {
        if (isEscapeKey(input)) {
            return exitInsertMode();
        }
        
        return processInsertModeInput(input);
    }
    
    private boolean isEscapeKey(char input) {
        return input == 27;
    }
    
    private InputProcessingResult exitInsertMode() {
        editor.setMode(Javavim.EditorMode.NORMAL);
        String message = "Exited insert mode";
        statusLine.setMessage(message);
        return InputProcessingResult.success(message)
                .modeChange(Javavim.EditorMode.NORMAL).build();
    }
    
    private InputProcessingResult processInsertModeInput(char input) {
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer == null) {
            return InputProcessingResult.success("No buffer available").build();
        }
        
        return handleInsertInput(currentBuffer, input);
    }
    
    private InputProcessingResult handleInsertInput(Buffer buffer, char input) {
        if (isEnterKey(input)) {
            return handleEnterKey(buffer);
        }
        
        return handleRegularInput(input);
    }
    
    private boolean isEnterKey(char input) {
        return input == '\r' || input == '\n';
    }
    
    private InputProcessingResult handleEnterKey(Buffer buffer) {
        int currentRow = editor.getCursorY();
        insertNewLineAt(buffer, currentRow + 1);
        editor.moveCursorToNewLine();
        String message = "INSERT - New line added";
        statusLine.setMessage(message);
        return InputProcessingResult.success(message).textInsertion('\n').build();
    }
    
    private boolean insertNewLineAt(Buffer buffer, int lineNumber) {
        if (lineNumber <= buffer.getLineCount()) {
            buffer.insertLine(lineNumber, "");
            return true;
        }
        return false;
    }
    
    private InputProcessingResult handleRegularInput(char input) {
        String message = "INSERT - Key: " + (int)input + " (" + input + ") - Press ESC to exit";
        statusLine.setMessage(message);
        return InputProcessingResult.success(message).textInsertion(input).build();
    }
    
    private InputProcessingResult handleCommandModeInput(char input) throws IOException {
        if (isEscapeKey(input)) {
            return exitCommandMode();
        }
        
        return processCommandInput(input);
    }
    
    private InputProcessingResult exitCommandMode() {
        editor.setMode(Javavim.EditorMode.NORMAL);
        statusLine.clearMessage();
        return InputProcessingResult.success("Exited command mode")
                .modeChange(Javavim.EditorMode.NORMAL).build();
    }
    
    private InputProcessingResult processCommandInput(char input) throws IOException {
        if (isEnterKey(input)) {
            return executeCurrentCommand();
        }
        
        return InputProcessingResult.success("Command input received").build();
    }
    
    private InputProcessingResult executeCurrentCommand() throws IOException {
        String command = statusLine.getMessage();
        commandProcessor.executeCommand(command);
        editor.setMode(Javavim.EditorMode.NORMAL);
        statusLine.clearMessage();
        return InputProcessingResult.success("Command executed")
                .commandExecution(command)
                .modeChange(Javavim.EditorMode.NORMAL).build();
    }
    
    private InputProcessingResult handleVisualModeInput(char input) {
        if (isEscapeKey(input)) {
            return exitVisualMode();
        }
        
        return InputProcessingResult.success("Visual mode input").build();
    }
    
    private InputProcessingResult exitVisualMode() {
        editor.setMode(Javavim.EditorMode.NORMAL);
        statusLine.clearMessage();
        return InputProcessingResult.success("Exited visual mode")
                .modeChange(Javavim.EditorMode.NORMAL).build();
    }
}