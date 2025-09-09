package com.javavim.gui;

import com.javavim.buffer.Buffer;
import com.javavim.vim.*;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

/**
 * JavaFX text editor component with vim functionality.
 * Follows single responsibility principle - manages text editing with vim behavior only.
 */
public class VimTextEditor extends TextArea implements VimActionListener {
    
    private final VimModeManager modeManager;
    private final VimKeyHandler keyHandler;
    private final Buffer buffer;
    private VimStatusListener statusListener;
    
    public VimTextEditor(Buffer buffer) {
        super();
        this.buffer = buffer;
        this.modeManager = new VimModeManager();
        this.keyHandler = new VimKeyHandler(modeManager);
        
        initialize();
    }
    
    private void initialize() {
        setupEventHandlers();
        setupStyling();
        keyHandler.setActionListener(this);
        loadBufferContent();
        updateModeDisplay();
    }
    
    private void setupEventHandlers() {
        // Add key event filter to catch events before default processing
        addEventFilter(KeyEvent.KEY_TYPED, this::handleKeyTypedFilter);
        setOnKeyPressed(this::handleKeyPressed);
        setOnKeyTyped(this::handleKeyTyped);
        textProperty().addListener((obs, oldText, newText) -> updateBuffer());
    }
    
    private void setupStyling() {
        setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-text-fill: #d4d4d4;" +
            "-fx-control-inner-background: #1e1e1e;" +
            "-fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: #3e3e3e;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );
    }
    
    private void handleKeyTypedFilter(KeyEvent event) {
        // Filter KEY_TYPED events before they reach the TextArea
        // This prevents mode-change characters from appearing in the text
        
        // If we're not in insert mode, block all character input
        if (!modeManager.isInMode(VimMode.INSERT)) {
            event.consume();
            return;
        }
        
        // If this character triggered the mode change to insert mode, block it
        String trigger = keyHandler.getLastModeChangeTrigger();
        if (trigger != null && trigger.equals(event.getCharacter())) {
            event.consume();
            keyHandler.clearModeChangeTrigger();
        }
        
        // All other characters in insert mode pass through normally
    }
    
    private void handleKeyPressed(KeyEvent event) {
        boolean handled = keyHandler.handleKeyEvent(event);
        
        if (handled) {
            event.consume();
            return;
        }
        
        // In non-insert modes, consume all key events to prevent typing
        if (!modeManager.isInMode(VimMode.INSERT)) {
            event.consume();
            return;
        }
        
        // In insert mode, let normal typing pass through
        // (JavaFX will handle it naturally)
    }
    
    private void handleKeyTyped(KeyEvent event) {
        // Block all character input in non-insert modes
        if (!modeManager.isInMode(VimMode.INSERT)) {
            event.consume();
            return;
        }
        
        // Normal text input in insert mode - let it pass through naturally
    }
    
    private void updateBuffer() {
        if (buffer == null) {
            return;
        }
        
        String[] lines = getText().split("\n", -1);
        
        // Update buffer with current text content
        int lineCount = buffer.getLineCount();
        for (int i = 0; i < Math.max(lineCount, lines.length); i++) {
            if (i < lines.length && i < lineCount) {
                buffer.setLine(i, lines[i]);
            } else if (i < lines.length) {
                buffer.insertLine(i, lines[i]);
            } else {
                buffer.deleteLine(lineCount - 1);
                lineCount--;
            }
        }
    }
    
    private void loadBufferContent() {
        if (buffer == null) {
            return;
        }
        
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < buffer.getLineCount(); i++) {
            if (i > 0) {
                content.append("\n");
            }
            content.append(buffer.getLine(i));
        }
        
        setText(content.toString());
    }
    
    public void setStatusListener(VimStatusListener listener) {
        this.statusListener = listener;
    }
    
    public VimMode getCurrentMode() {
        return modeManager.getCurrentMode();
    }
    
    public Buffer getBuffer() {
        return buffer;
    }
    
    @Override
    public void onModeChanged(VimMode newMode) {
        updateModeDisplay();
        notifyStatusChange();
    }
    
    @Override
    public void onVimAction(VimAction action) {
        executeVimAction(action);
    }
    
    private void executeVimAction(VimAction action) {
        switch (action) {
            case MOVE_LEFT -> moveCursorLeft();
            case MOVE_RIGHT -> moveCursorRight();
            case MOVE_UP -> moveCursorUp();
            case MOVE_DOWN -> moveCursorDown();
            case EXTEND_SELECTION_LEFT -> extendSelectionLeft();
            case EXTEND_SELECTION_RIGHT -> extendSelectionRight();
            case EXTEND_SELECTION_UP -> extendSelectionUp();
            case EXTEND_SELECTION_DOWN -> extendSelectionDown();
            case EXECUTE_COMMAND -> executeCommand();
            default -> { /* Other actions */ }
        }
    }
    
    private void moveCursorLeft() {
        int pos = getCaretPosition();
        if (pos > 0) {
            positionCaret(pos - 1);
        }
    }
    
    private void moveCursorRight() {
        int pos = getCaretPosition();
        if (pos < getText().length()) {
            positionCaret(pos + 1);
        }
    }
    
    private void moveCursorUp() {
        // Simple implementation - move to previous line
        int pos = getCaretPosition();
        String text = getText();
        int lineStart = text.lastIndexOf('\n', pos - 1);
        if (lineStart > 0) {
            int prevLineStart = text.lastIndexOf('\n', lineStart - 1);
            int columnOffset = pos - lineStart - 1;
            int newPos = Math.min(prevLineStart + 1 + columnOffset, lineStart - 1);
            positionCaret(newPos);
        }
    }
    
    private void moveCursorDown() {
        // Simple implementation - move to next line
        int pos = getCaretPosition();
        String text = getText();
        int lineEnd = text.indexOf('\n', pos);
        if (lineEnd != -1 && lineEnd < text.length() - 1) {
            int nextLineEnd = text.indexOf('\n', lineEnd + 1);
            if (nextLineEnd == -1) nextLineEnd = text.length();
            
            int currentLineStart = text.lastIndexOf('\n', pos - 1) + 1;
            int columnOffset = pos - currentLineStart;
            int newPos = Math.min(lineEnd + 1 + columnOffset, nextLineEnd);
            positionCaret(newPos);
        }
    }
    
    private void extendSelectionLeft() {
        selectRange(getAnchor(), Math.max(0, getCaretPosition() - 1));
    }
    
    private void extendSelectionRight() {
        selectRange(getAnchor(), Math.min(getText().length(), getCaretPosition() + 1));
    }
    
    private void extendSelectionUp() {
        // Implement selection extension upward
        moveCursorUp();
    }
    
    private void extendSelectionDown() {
        // Implement selection extension downward  
        moveCursorDown();
    }
    
    private void executeCommand() {
        if (statusListener != null) {
            statusListener.onCommandRequested();
        }
    }
    
    private void updateModeDisplay() {
        // Update cursor style based on mode
        String cursor = switch (modeManager.getCurrentMode()) {
            case NORMAL -> "-fx-text-cursor: block;";
            case INSERT -> "-fx-text-cursor: text;";
            case VISUAL -> "-fx-text-cursor: crosshair;";
            case COMMAND -> "-fx-text-cursor: text;";
        };
        
        // Apply cursor styling (would need custom CSS for full effect)
        setStyle(getStyle() + cursor);
    }
    
    private void notifyStatusChange() {
        if (statusListener != null) {
            statusListener.onModeChanged(modeManager.getCurrentMode());
        }
    }
    
    public interface VimStatusListener {
        void onModeChanged(VimMode mode);
        void onCommandRequested();
    }
}