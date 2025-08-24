package com.javavim.terminal;

import com.javavim.buffer.ScreenBuffer;
import java.io.IOException;

/**
 * Mock terminal UI for testing that doesn't open real terminals.
 */
public class MockTerminalUI extends TerminalUI {
    
    private ScreenBuffer mockScreenBuffer;
    private Cursor mockCursor;
    private boolean mockInitialized;
    
    public MockTerminalUI() {
        super();
        this.mockScreenBuffer = new ScreenBuffer(80, 24);
        this.mockCursor = new Cursor(80, 24);
        this.mockInitialized = false;
    }
    
    @Override
    public void initialize() throws IOException {
        // Don't create real terminal, just set up mock objects
        this.mockInitialized = true;
    }
    
    @Override
    public void shutdown() throws IOException {
        this.mockInitialized = false;
    }
    
    @Override
    public boolean isInitialized() {
        return mockInitialized;
    }
    
    @Override
    public ScreenBuffer getScreenBuffer() {
        return mockInitialized ? mockScreenBuffer : null;
    }
    
    @Override
    public Cursor getCursor() {
        return mockInitialized ? mockCursor : null;
    }
    
    @Override
    public void refresh() throws IOException {
        // Mock refresh - do nothing
        if (mockInitialized && mockScreenBuffer.isDirty()) {
            mockScreenBuffer.setDirty(false);
        }
    }
    
    @Override
    public void clear() throws IOException {
        // Mock clear - just clear screen buffer
        if (mockInitialized) {
            mockScreenBuffer.clear();
        }
    }
    
    @Override
    public char readInput() throws IOException {
        // Mock input - return 'q' if initialized, 0 if not
        return mockInitialized ? 'q' : (char) 0;
    }
}