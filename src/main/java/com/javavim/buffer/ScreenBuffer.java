package com.javavim.buffer;

/**
 * Manages the screen buffer for terminal display.
 * Follows single responsibility principle - manages screen content only.
 */
public class ScreenBuffer {
    
    private final char[][] buffer;
    private final int width;
    private final int height;
    private boolean dirty;
    
    public ScreenBuffer(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        
        this.width = width;
        this.height = height;
        this.buffer = new char[height][width];
        this.dirty = true;
        clear();
    }
    
    public void setChar(int x, int y, char c) {
        if (isValidPosition(x, y)) {
            buffer[y][x] = c;
            setDirty(true);
        }
    }
    
    public char getChar(int x, int y) {
        if (isValidPosition(x, y)) {
            return buffer[y][x];
        }
        return ' ';
    }
    
    public void setString(int x, int y, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        if (isValidPosition(x, y)) {
            writeString(x, y, text);
        }
    }
    
    public String getLine(int y) {
        if (isValidRow(y)) {
            return new String(buffer[y]);
        }
        return "";
    }
    
    public void clear() {
        fillWithSpaces();
        setDirty(true);
    }
    
    public void clearLine(int y) {
        if (isValidRow(y)) {
            fillLineWithSpaces(y);
            setDirty(true);
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    private boolean isValidRow(int y) {
        return y >= 0 && y < height;
    }
    
    private void writeString(int x, int y, String text) {
        int pos = x;
        for (char c : text.toCharArray()) {
            if (pos >= width) {
                break;
            }
            buffer[y][pos] = c;
            pos++;
        }
        setDirty(true);
    }
    
    private void fillWithSpaces() {
        for (int y = 0; y < height; y++) {
            fillLineWithSpaces(y);
        }
    }
    
    private void fillLineWithSpaces(int y) {
        for (int x = 0; x < width; x++) {
            buffer[y][x] = ' ';
        }
    }
}