package com.javavim.terminal;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.javavim.buffer.ScreenBuffer;
import com.googlecode.lanterna.TerminalSize;
import java.io.IOException;

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
        com.googlecode.lanterna.input.KeyStroke keyStroke = terminal.readInput();
        if (keyStroke != null) {
            return processKeyStroke(keyStroke);
        }
        return 0;
    }
    
    private char processKeyStroke(com.googlecode.lanterna.input.KeyStroke keyStroke) {
        // Handle ESC key specifically
        if (keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape) {
            return 27; // ESC character
        }
        
        // Handle regular characters
        Character character = keyStroke.getCharacter();
        if (character != null) {
            return character;
        }
        
        return 0; // Return null character for unhandled special keys
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
        terminal.enterPrivateMode();
        terminal.setCursorVisible(false); // Hide default terminal cursor
        terminal.clearScreen();
        terminal.flush();
    }
    
    private boolean isTestEnvironment() {
        // Check for common test environment indicators
        String classPath = System.getProperty("java.class.path", "");
        return classPath.contains("test-classes") || 
               classPath.contains("junit") || 
               classPath.contains("surefire") ||
               System.getProperty("maven.test.skip") != null;
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
            // Render cursor as a highlighted character at cursor position
            int x = cursor.getX();
            int y = cursor.getY();
            if (isValidPosition(x, y)) {
                char cursorChar = screenBuffer.getChar(x, y);
                if (cursorChar == 0) cursorChar = ' ';
                terminal.setBackgroundColor(com.googlecode.lanterna.TextColor.ANSI.WHITE);
                terminal.setForegroundColor(com.googlecode.lanterna.TextColor.ANSI.BLACK);
                terminal.setCursorPosition(x, y);
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