package com.javavim.terminal;

import com.javavim.buffer.ScreenBuffer;
import java.io.IOException;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Test terminal UI that allows us to simulate specific input sequences
 * to test different input handling methods in run().
 */
public class TestInputTerminalUI extends TerminalUI {
    
    private ScreenBuffer mockScreenBuffer;
    private Cursor mockCursor;
    private boolean mockInitialized;
    private Queue<Character> inputSequence;
    
    public TestInputTerminalUI() {
        super();
        this.mockScreenBuffer = new ScreenBuffer(80, 24);
        this.mockCursor = new Cursor(80, 24);
        this.mockInitialized = false;
        this.inputSequence = new LinkedList<>();
    }
    
    /**
     * Set a sequence of characters to be returned by readInput()
     */
    public void setInputSequence(char... inputs) {
        inputSequence.clear();
        for (char input : inputs) {
            inputSequence.offer(input);
        }
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
        if (!mockInitialized) {
            return (char) 0;
        }
        
        // Return next character from sequence, or 'q' to quit if sequence is empty
        Character next = inputSequence.poll();
        return next != null ? next : 'q';
    }
}