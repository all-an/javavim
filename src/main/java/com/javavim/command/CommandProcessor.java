package com.javavim.command;

import com.javavim.Javavim;
import com.javavim.buffer.Buffer;
import com.javavim.buffer.BufferManager;
import com.javavim.display.StatusLine;
import com.javavim.io.FileManager;
import com.javavim.search.SearchEngine;
import java.io.IOException;

/**
 * Handles command processing for vim-like commands.
 * Follows single responsibility principle - processes commands only.
 */
public class CommandProcessor {
    
    private final BufferManager bufferManager;
    private final StatusLine statusLine;
    private final FileManager fileManager;
    private final SearchEngine searchEngine;
    private final Javavim editor;
    
    public CommandProcessor(Javavim editor, BufferManager bufferManager, StatusLine statusLine, 
                           FileManager fileManager, SearchEngine searchEngine) {
        this.editor = editor;
        this.bufferManager = bufferManager;
        this.statusLine = statusLine;
        this.fileManager = fileManager;
        this.searchEngine = searchEngine;
    }
    
    public void executeCommand(String command) throws IOException {
        if (tryExecuteFileCommand(command)) {
            return;
        }
        
        if (tryExecuteOtherCommand(command)) {
            return;
        }
        
        statusLine.setMessage("Unknown command: " + command);
    }
    
    private boolean tryExecuteFileCommand(String command) throws IOException {
        if (isWriteCommand(command)) {
            handleWriteCommand(command);
            return true;
        }
        
        if (isEditCommand(command)) {
            handleEditCommand(command);
            return true;
        }
        
        return false;
    }
    
    private boolean tryExecuteOtherCommand(String command) throws IOException {
        if (isQuitCommand(command)) {
            handleQuitCommand(command);
            return true;
        }
        
        if (isSearchOrHelpCommand(command)) {
            handleSearchOrHelpCommand(command);
            return true;
        }
        
        return false;
    }
    
    private boolean isWriteCommand(String command) {
        return command.startsWith(":w");
    }
    
    private boolean isQuitCommand(String command) {
        return command.startsWith(":q");
    }
    
    private boolean isEditCommand(String command) {
        return command.startsWith(":e");
    }
    
    private boolean isSearchCommand(String command) {
        return command.startsWith("/");
    }
    
    private boolean isHelpCommand(String command) {
        return command.startsWith(":help");
    }
    
    private boolean isSearchOrHelpCommand(String command) {
        return isSearchCommand(command) || isHelpCommand(command);
    }
    
    private void handleWriteCommand(String command) throws IOException {
        if (hasFilename(command)) {
            String filename = extractFilename(command, 2);
            saveBufferToFile(filename);
        } else {
            saveCurrentBuffer();
        }
    }
    
    private void handleQuitCommand(String command) {
        if (command.startsWith(":q!")) {
            editor.setRunning(false);
        } else {
            quitWithCheck();
        }
    }
    
    private void quitWithCheck() {
        if (bufferManager.hasModifiedBuffers()) {
            statusLine.setMessage("Warning: Unsaved changes! Use :q! to force quit");
        } else {
            editor.setRunning(false);
        }
    }
    
    private void handleEditCommand(String command) throws IOException {
        if (hasFilename(command)) {
            String filename = extractFilename(command, 2);
            openFileWithValidation(filename);
        } else {
            statusLine.setMessage("Usage: :e <filename>");
        }
    }
    
    private void handleSearchOrHelpCommand(String command) {
        if (isSearchCommand(command)) {
            handleSearchCommand(command);
        } else {
            showHelp();
        }
    }
    
    private void handleSearchCommand(String command) {
        String searchText = command.substring(1);
        performSearch(searchText);
    }
    
    private boolean hasFilename(String command) {
        return command.length() > 3 && command.charAt(2) == ' ';
    }
    
    private String extractFilename(String command, int colonIndex) {
        return command.substring(colonIndex + 1).trim();
    }
    
    private void openFileWithValidation(String filename) throws IOException {
        if (isValidFilename(filename)) {
            editor.openFile(filename);
            statusLine.setMessage("Opened: " + filename);
        } else {
            statusLine.setMessage("Invalid filename: " + filename);
        }
    }
    
    private boolean isValidFilename(String filename) {
        return filename != null && !filename.isEmpty() && !filename.contains("\0");
    }
    
    private void saveCurrentBuffer() throws IOException {
        editor.saveCurrentBuffer();
    }
    
    private void saveBufferToFile(String filename) throws IOException {
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer != null) {
            handleBufferSave(currentBuffer, filename);
        } else {
            statusLine.setMessage("No buffer to save");
        }
    }
    
    private void handleBufferSave(Buffer buffer, String filename) throws IOException {
        try {
            fileManager.saveBuffer(buffer);
            statusLine.setMessage("Saved to: " + filename);
        } catch (IOException e) {
            statusLine.setMessage("Cannot save to: " + filename + " (" + e.getMessage() + ")");
        }
    }
    
    private void performSearch(String searchText) {
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer != null && !searchText.isEmpty()) {
            executeSearch(currentBuffer, searchText);
        }
    }
    
    private void executeSearch(Buffer buffer, String searchText) {
        var results = searchEngine.findAll(buffer, searchText, true);
        statusLine.setMessage("Found " + results.size() + " matches for: " + searchText);
    }
    
    private void showHelp() {
        // Create help content in current buffer
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer != null) {
            setupHelpContent(currentBuffer);
            statusLine.setMessage("Help displayed");
        }
    }
    
    private void setupHelpContent(Buffer buffer) {
        buffer.setLine(0, "JavaVim - Terminal Vim Editor v1.0.0");
        buffer.insertLine(1, "");
        buffer.insertLine(2, "Normal Mode Commands:");
        buffer.insertLine(3, "  q          - Quit");
        buffer.insertLine(4, "  i          - Enter Insert mode");
        buffer.insertLine(5, "  :          - Enter Command mode");
        buffer.insertLine(6, "  /          - Search forward");
        buffer.insertLine(7, "  n          - Next search result");
        buffer.insertLine(8, "  u          - Undo");
        buffer.insertLine(9, "  Ctrl+r     - Redo");
        buffer.insertLine(10, "  h,j,k,l    - Move cursor");
        buffer.insertLine(11, "  :w         - Save file");
        buffer.insertLine(12, "  :e <file>  - Open file");
        buffer.insertLine(13, "  :help      - Show this help");
        buffer.insertLine(14, "");
        buffer.insertLine(15, "Press 'i' to start editing, ':' for commands, or 'q' to quit");
        buffer.setModified(false);
    }
}