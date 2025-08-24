package com.javavim.buffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages multiple text buffers in the editor.
 * Follows single responsibility principle - manages buffer collection only.
 */
public class BufferManager {
    
    private final List<Buffer> buffers;
    private int currentBufferIndex;
    
    public BufferManager() {
        this.buffers = new ArrayList<>();
        this.currentBufferIndex = -1;
    }
    
    public Buffer createNewBuffer() {
        Buffer buffer = new Buffer();
        addBuffer(buffer);
        return buffer;
    }
    
    public Buffer createNewBuffer(String filename) {
        Buffer buffer = new Buffer(filename);
        addBuffer(buffer);
        return buffer;
    }
    
    public void addBuffer(Buffer buffer) {
        if (buffer != null) {
            buffers.add(buffer);
            this.currentBufferIndex = buffers.size() - 1;
        }
    }
    
    public Buffer getCurrentBuffer() {
        if (hasCurrentBuffer()) {
            return buffers.get(currentBufferIndex);
        }
        return null;
    }
    
    public boolean switchToBuffer(int index) {
        if (isValidBufferIndex(index)) {
            this.currentBufferIndex = index;
            return true;
        }
        return false;
    }
    
    public boolean switchToNextBuffer() {
        if (hasBuffers()) {
            this.currentBufferIndex = (currentBufferIndex + 1) % buffers.size();
            return true;
        }
        return false;
    }
    
    public boolean switchToPreviousBuffer() {
        if (hasBuffers()) {
            this.currentBufferIndex = (currentBufferIndex - 1 + buffers.size()) % buffers.size();
            return true;
        }
        return false;
    }
    
    public boolean closeCurrentBuffer() {
        if (hasCurrentBuffer()) {
            buffers.remove(currentBufferIndex);
            adjustCurrentIndex();
            return true;
        }
        return false;
    }
    
    public int getBufferCount() {
        return buffers.size();
    }
    
    public int getCurrentBufferIndex() {
        return currentBufferIndex;
    }
    
    public List<Buffer> getAllBuffers() {
        return new ArrayList<>(buffers);
    }
    
    public boolean hasModifiedBuffers() {
        return buffers.stream().anyMatch(Buffer::isModified);
    }
    
    private boolean hasCurrentBuffer() {
        return isValidBufferIndex(currentBufferIndex);
    }
    
    private boolean hasBuffers() {
        return !buffers.isEmpty();
    }
    
    private boolean isValidBufferIndex(int index) {
        return index >= 0 && index < buffers.size();
    }
    
    private void adjustCurrentIndex() {
        if (buffers.isEmpty()) {
            currentBufferIndex = -1;
        } else if (currentBufferIndex >= buffers.size()) {
            currentBufferIndex = buffers.size() - 1;
        }
    }
}