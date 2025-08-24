package com.javavim;

import com.javavim.terminal.TerminalUI;
import com.javavim.buffer.Buffer;
import com.javavim.buffer.BufferManager;
import com.javavim.config.Configuration;
import com.javavim.display.DisplayRenderer;
import com.javavim.display.StatusLine;
import com.javavim.io.FileManager;
import com.javavim.search.SearchEngine;
import com.javavim.editor.UndoRedoManager;
import java.io.IOException;

/**
 * Main JavaVim editor application with full interactive features.
 * Integrates all editor components for a complete vim-like experience.
 */
public class Javavim {
    
    private TerminalUI terminalUI;
    private BufferManager bufferManager;
    private DisplayRenderer displayRenderer;
    private StatusLine statusLine;
    private FileManager fileManager;
    private SearchEngine searchEngine;
    private UndoRedoManager undoRedoManager;
    private Configuration config;
    private boolean running;
    private EditorMode currentMode;
    private String lastSearch;
    
    public enum EditorMode {
        NORMAL, INSERT, VISUAL, COMMAND
    }
    
    public Javavim() {
        this.terminalUI = new TerminalUI();
        this.bufferManager = new BufferManager();
        this.displayRenderer = new DisplayRenderer();
        this.statusLine = new StatusLine();
        this.fileManager = new FileManager();
        this.searchEngine = new SearchEngine();
        this.undoRedoManager = new UndoRedoManager();
        this.config = new Configuration();
        this.running = false;
        this.currentMode = EditorMode.NORMAL;
        this.lastSearch = "";
    }
    
    public Javavim(TerminalUI terminalUI) {
        this();
        this.terminalUI = terminalUI;
    }
    
    public static void main(String[] args) {
        com.javavim.cli.CommandLineParser parser = new com.javavim.cli.CommandLineParser();
        com.javavim.cli.CommandLineParser.CommandLineResult result = parser.parseArguments(args);
        
        if (result.isHelp()) {
            System.out.println(result.getHelpMessage());
            return;
        }
        
        Javavim editor = new Javavim();
        if (result.hasFile()) {
            editor.run(new String[]{result.getFilename()});
        } else {
            editor.run(new String[0]);
        }
    }
    
    public void run(String[] args) {
        try {
            initialize(args);
            if (terminalUI.isInitialized()) {
                showWelcomeScreen();
                mainLoop();
            } else {
                handleNonTerminalMode();
            }
        } catch (IOException e) {
            handleError("Failed to start Javavim: " + e.getMessage());
        } finally {
            shutdown();
        }
    }
    
    public void openFile(String filename) throws IOException {
        if (filename != null && !filename.isEmpty()) {
            Buffer buffer = fileManager.loadFile(filename);
            bufferManager.addBuffer(buffer);
            statusLine.setMessage("Opened: " + filename);
        }
    }
    
    public void saveCurrentBuffer() throws IOException {
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer != null) {
            fileManager.saveBuffer(currentBuffer);
            statusLine.setMessage("Saved: " + (currentBuffer.getFilename() != null ? currentBuffer.getFilename() : "[No Name]"));
        }
    }
    
    public EditorMode getCurrentMode() {
        return currentMode;
    }
    
    public void setMode(EditorMode mode) {
        this.currentMode = mode;
        statusLine.setMode(mode.toString());
    }
    
    public BufferManager getBufferManager() {
        return bufferManager;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
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
    
    private void initialize(String[] args) throws IOException {
        terminalUI.initialize();
        this.running = true;
        
        if (terminalUI.isInitialized()) {
            setupEditor(args);
        }
    }
    
    private void setupEditor(String[] args) throws IOException {
        statusLine.setMode(currentMode.toString());
        
        if (args.length > 0) {
            openFile(args[0]);
        } else {
            createWelcomeBuffer();
        }
        
        initializeCursor();
    }
    
    private void initializeCursor() {
        if (hasCursorAndBuffer()) {
            terminalUI.getCursor().moveTo(0, 0);
        }
    }
    
    private boolean hasCursorAndBuffer() {
        return terminalUI.getCursor() != null && bufferManager.getCurrentBuffer() != null;
    }
    
    private void createWelcomeBuffer() {
        Buffer welcomeBuffer = bufferManager.createNewBuffer("[Welcome]");
        setupWelcomeContent(welcomeBuffer);
    }
    
    private void setupWelcomeContent(Buffer buffer) {
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
    
    private void showWelcomeScreen() throws IOException {
        if (!terminalUI.isInitialized()) {
            return;
        }
        
        renderEditor();
    }
    
    private void mainLoop() throws IOException {
        while (running) {
            handleInput();
            if (running) {
                renderEditor();
            }
        }
    }
    
    private void renderEditor() throws IOException {
        if (!terminalUI.isInitialized()) {
            return;
        }
        
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer != null) {
            constrainCursorToBuffer(currentBuffer);
            displayRenderer.render(currentBuffer, terminalUI.getScreenBuffer(), terminalUI.getCursor());
            statusLine.render(terminalUI.getScreenBuffer(), currentBuffer, terminalUI.getCursor());
        }
        
        terminalUI.refresh();
    }
    
    private void constrainCursorToBuffer(Buffer buffer) {
        if (terminalUI.getCursor() == null || buffer == null) {
            return;
        }
        
        int cursorY = constrainCursorY(buffer);
        int cursorX = constrainCursorX(buffer, cursorY);
        updateCursorIfChanged(cursorX, cursorY);
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
    
    private void handleInput() throws IOException {
        char input = terminalUI.readInput();
        
        if (input == 0) {
            return; // Ignore special keys
        }
        
        try {
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
        } catch (Exception e) {
            statusLine.setMessage("Error: " + e.getMessage());
        }
    }
    
    private void handleNormalModeInput(char input) throws IOException {
        switch (input) {
            case 'q':
                if (bufferManager.hasModifiedBuffers()) {
                    statusLine.setMessage("Warning: Unsaved changes! Use :q! to force quit");
                } else {
                    this.running = false;
                }
                break;
            case 'i':
                setMode(EditorMode.INSERT);
                statusLine.setMessage("-- INSERT --");
                break;
            case ':':
                setMode(EditorMode.COMMAND);
                statusLine.setMessage(":");
                break;
            case '/':
                setMode(EditorMode.COMMAND);
                statusLine.setMessage("/");
                break;
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
                moveCursorLeft();
                break;
            case 'j':
                moveCursorDown();
                break;
            case 'k':
                moveCursorUp();
                break;
            case 'l':
                moveCursorRight();
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
        setMode(EditorMode.NORMAL);
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
        int currentRow = terminalUI.getCursor().getY();
        insertNewLineAt(buffer, currentRow + 1);
        moveCursorToNewLine();
        statusLine.setMessage("INSERT - New line added");
    }
    
    private void insertNewLineAt(Buffer buffer, int lineNumber) {
        if (lineNumber <= buffer.getLineCount()) {
            buffer.insertLine(lineNumber, "");
        }
    }
    
    public void moveCursorToNewLine() {
        int newY = terminalUI.getCursor().getY() + 1;
        terminalUI.getCursor().moveTo(0, newY);
    }
    
    private void handleRegularInput(Buffer buffer, char input) {
        statusLine.setMessage("INSERT - Key: " + (int)input + " (" + input + ") - Press ESC to exit");
    }
    
    private void handleCommandModeInput(char input) throws IOException {
        if (input == 27) { // ESC key
            setMode(EditorMode.NORMAL);
            statusLine.clearMessage();
            return;
        }
        
        // Simple command handling - in real implementation this would build command string
        if (input == '\r' || input == '\n') {
            executeCommand(statusLine.getMessage());
            setMode(EditorMode.NORMAL);
            statusLine.clearMessage();
        }
    }
    
    private void handleVisualModeInput(char input) {
        if (input == 27) { // ESC key
            setMode(EditorMode.NORMAL);
            statusLine.clearMessage();
        }
    }
    
    private void executeCommand(String command) throws IOException {
        if (command.startsWith(":w")) {
            saveCurrentBuffer();
        } else if (command.startsWith(":q!")) {
            this.running = false;
        } else if (command.startsWith(":q")) {
            handleQuitCommand();
        } else if (command.startsWith(":e")) {
            handleEditCommand(command);
        } else if (command.startsWith(":help")) {
            showHelp();
        } else if (command.startsWith("/")) {
            handleSearchCommand(command);
        } else {
            statusLine.setMessage("Unknown command: " + command);
        }
    }
    
    private void handleQuitCommand() {
        if (bufferManager.hasModifiedBuffers()) {
            statusLine.setMessage("Warning: Unsaved changes! Use :q! to force quit");
        } else {
            this.running = false;
        }
    }
    
    private void handleEditCommand(String command) throws IOException {
        if (command.length() > 3 && command.charAt(2) == ' ') {
            String filename = command.substring(3).trim();
            if (isValidFilename(filename)) {
                openFileWithValidation(filename);
            } else {
                statusLine.setMessage("Invalid filename: " + filename);
            }
        } else {
            statusLine.setMessage("Usage: :e <filename>");
        }
    }
    
    private void handleSearchCommand(String command) {
        String searchText = command.substring(1);
        search(searchText);
    }
    
    private boolean isValidFilename(String filename) {
        return filename != null && !filename.isEmpty() && !filename.contains("\0");
    }
    
    private void openFileWithValidation(String filename) throws IOException {
        try {
            openFile(filename);
            statusLine.setMessage("Opened: " + filename);
        } catch (IOException e) {
            statusLine.setMessage("Cannot open file: " + filename + " (" + e.getMessage() + ")");
        }
    }
    
    private void search(String searchText) {
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer != null && !searchText.isEmpty()) {
            this.lastSearch = searchText;
            var results = searchEngine.findAll(currentBuffer, searchText, true);
            statusLine.setMessage("Found " + results.size() + " matches for: " + searchText);
        }
    }
    
    private void searchNext() {
        if (!lastSearch.isEmpty()) {
            statusLine.setMessage("Next match: " + lastSearch);
        } else {
            statusLine.setMessage("No previous search");
        }
    }
    
    private void undo() {
        if (undoRedoManager.canUndo()) {
            undoRedoManager.undo();
            statusLine.setMessage("Undo");
        } else {
            statusLine.setMessage("Nothing to undo");
        }
    }
    
    private void redo() {
        if (undoRedoManager.canRedo()) {
            undoRedoManager.redo();
            statusLine.setMessage("Redo");
        } else {
            statusLine.setMessage("Nothing to redo");
        }
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
    
    private void showHelp() {
        Buffer currentBuffer = bufferManager.getCurrentBuffer();
        if (currentBuffer != null) {
            setupWelcomeContent(currentBuffer);
            statusLine.setMessage("Help displayed");
        }
    }
    
    private void handleNonTerminalMode() {
        System.out.println("JavaVim - Terminal Vim Editor v1.0.0");
        System.out.println("Running in non-terminal mode (no interactive features)");
        System.out.println("Terminal features require console access");
        System.out.println("Try running from a real terminal for full functionality");
    }
    
    private void handleError(String message) {
        System.err.println("Error: " + message);
        System.err.println("Falling back to basic mode...");
        handleNonTerminalMode();
    }
    
    public void shutdown() {
        try {
            if (terminalUI != null) {
                terminalUI.shutdown();
            }
        } catch (IOException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    public int getCursorY() {
        if (terminalUI.getCursor() != null) {
            return terminalUI.getCursor().getY();
        }
        return 0;
    }
}