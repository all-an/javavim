package com.javavim.terminal;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.javavim.buffer.ScreenBuffer;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import static com.googlecode.lanterna.input.KeyType.*;

/**
 * Manages terminal UI operations using Lanterna library.
 * Follows single responsibility principle - handles terminal UI only.
 */
public class TerminalUI {
    
    private Terminal terminal;
    private ScreenBuffer screenBuffer;
    private Cursor cursor;
    private boolean initialized;
    
    public void initialize() throws IOException {
        if (initialized) {
            return;
        }
        
        try {
            createTerminal();
            setupScreenBuffer();
            setupCursor();
            this.initialized = true;
        } catch (Exception e) {
            // Terminal initialization failed (e.g., in CI/headless environment)
            System.err.println("Terminal initialization failed: " + e.getMessage());
            this.initialized = false;
            // Don't throw exception, just mark as not initialized
        }
    }
    
    public void shutdown() throws IOException {
        if (terminal != null) {
            terminal.close();
            this.initialized = false;
        }
    }
    
    public void refresh() throws IOException {
        if (isInitialized() && screenBuffer.isDirty()) {
            renderScreenBuffer();
            updateCursorPosition();
            terminal.flush();
            screenBuffer.setDirty(false);
        }
    }
    
    public void forceRefresh() throws IOException {
        if (isInitialized()) {
            screenBuffer.setDirty(true);
            refresh();
        }
    }
    
    public void clear() throws IOException {
        if (isInitialized()) {
            terminal.clearScreen();
            screenBuffer.clear();
        }
    }
    
    public char readInput() throws IOException {
        if (isInitialized()) {
            return readInputFromTerminal();
        }
        return 0;
    }
    
    private char readInputFromTerminal() throws IOException {
        KeyStroke keyStroke = terminal.readInput();
        if (keyStroke != null) {
            return processKeyStroke(keyStroke);
        }
        return 0;
    }
    
    private char processKeyStroke(KeyStroke keyStroke) {
        char specialKeyResult = handleSpecialKeys(keyStroke);
        if (specialKeyResult != 0) {
            return specialKeyResult;
        }
        
        return handleRegularKeys(keyStroke);
    }
    
    private char handleSpecialKeys(KeyStroke keyStroke) {
        KeyType keyType = keyStroke.getKeyType();
        
        if (keyType == Escape) {
            return 27;
        }
        
        return handleArrowKeys(keyType);
    }
    
    private char handleArrowKeys(KeyType keyType) {
        switch (keyType) {
            case ArrowLeft:
                return 'h';
            case ArrowDown:
                return 'j';
            case ArrowUp:
                return 'k';
            case ArrowRight:
                return 'l';
            default:
                return 0;
        }
    }
    
    private char handleRegularKeys(KeyStroke keyStroke) {
        Character character = keyStroke.getCharacter();
        if (character != null) {
            return character;
        }
        return 0;
    }
    
    public ScreenBuffer getScreenBuffer() {
        return screenBuffer;
    }
    
    public Cursor getCursor() {
        return cursor;
    }
    
    public boolean isInitialized() {
        return initialized && terminal != null;
    }
    
    private void createTerminal() throws IOException {
        if (shouldDisableTerminal()) {
            throw new IOException("Terminal creation disabled in test environment");
        }
        
        initializeTerminal();
        configureTerminalDisplay();
    }
    
    private boolean shouldDisableTerminal() {
        return isTestEnvironment();
    }
    
    private void initializeTerminal() throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setForceTextTerminal(true);
        this.terminal = factory.createTerminal();
    }
    
    private void configureTerminalDisplay() throws IOException {
        // Minimal terminal setup for immediate display
        terminal.enterPrivateMode();
        terminal.setCursorVisible(false);
        terminal.clearScreen();
        terminal.flush();
    }
    
    private boolean isTestEnvironment() {
        // For now, only disable when explicitly running tests, not from main execution
        return System.getProperty("maven.test.skip") != null ||
               System.getProperty("test") != null ||
               isRunningFromTestMethod();
    }
    
    private boolean isRunningFromTestMethod() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (className.endsWith("Test") || className.contains(".test.")) {
                return true;
            }
        }
        return false;
    }
    
    private void setupScreenBuffer() throws IOException {
        TerminalSize size = terminal.getTerminalSize();
        this.screenBuffer = new ScreenBuffer(size.getColumns(), size.getRows());
    }
    
    private void setupCursor() throws IOException {
        TerminalSize size = terminal.getTerminalSize();
        this.cursor = new Cursor(size.getColumns(), size.getRows());
    }
    
    private void renderScreenBuffer() throws IOException {
        for (int y = 0; y < screenBuffer.getHeight(); y++) {
            renderLine(y);
        }
    }
    
    private void renderLine(int y) throws IOException {
        String line = screenBuffer.getLine(y);
        try {
            terminal.setCursorPosition(0, y);
            for (char c : line.toCharArray()) {
                terminal.putCharacter(c);
            }
        } catch (IOException e) {
            throw new IOException("Failed to render line: " + e.getMessage(), e);
        }
    }
    
    private void updateCursorPosition() throws IOException {
        if (shouldShowCursor()) {
            renderCursor();
        }
    }
    
    private boolean shouldShowCursor() {
        return cursor.isVisible();
    }
    
    private void renderCursor() throws IOException {
        try {
            // Calculate display position (logical position + line number offset)
            int logicalX = cursor.getX();
            int logicalY = cursor.getY();
            
            // The display position needs to account for line numbers
            // Line numbers take 4 characters + 1 separator = 5 character offset
            int displayX = logicalX + 5; // TODO: Get this offset from DisplayRenderer
            int displayY = logicalY;
            
            if (isValidPosition(displayX, displayY)) {
                char cursorChar = screenBuffer.getChar(displayX, displayY);
                if (cursorChar == 0) cursorChar = ' ';
                terminal.setBackgroundColor(com.googlecode.lanterna.TextColor.ANSI.WHITE);
                terminal.setForegroundColor(com.googlecode.lanterna.TextColor.ANSI.BLACK);
                terminal.setCursorPosition(displayX, displayY);
                terminal.putCharacter(cursorChar);
                terminal.resetColorAndSGR();
            }
        } catch (IOException e) {
            throw new IOException("Failed to render cursor: " + e.getMessage(), e);
        }
    }
    
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && 
               screenBuffer != null && 
               x < screenBuffer.getWidth() && 
               y < screenBuffer.getHeight();
    }
}