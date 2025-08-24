package com.javavim.text;

/**
 * Handles basic text manipulation operations.
 * Follows single responsibility principle - manages text operations only.
 */
public class TextManipulator {
    
    public String insertTextAtPosition(String line, int position, String text) {
        if (line == null || text == null) {
            throw new IllegalArgumentException("Line and text cannot be null");
        }
        
        if (isValidInsertPosition(line, position)) {
            return line.substring(0, position) + text + line.substring(position);
        }
        return line;
    }
    
    public String deleteCharAtPosition(String line, int position) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        
        if (canDeleteChar(line, position)) {
            return line.substring(0, position) + line.substring(position + 1);
        }
        return line;
    }
    
    public String deleteRange(String line, int start, int end) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        
        if (isValidRange(line, start, end)) {
            return line.substring(0, start) + line.substring(end);
        }
        return line;
    }
    
    public String getSubstring(String line, int start, int end) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        
        if (isValidRange(line, start, end)) {
            return line.substring(start, end);
        }
        return "";
    }
    
    public int findNextWord(String line, int currentPosition) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        
        if (isAtEndOfLine(line, currentPosition)) {
            return currentPosition;
        }
        
        return findWordBoundary(line, currentPosition);
    }
    
    public int findPreviousWord(String line, int currentPosition) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        
        if (isAtStartOfLine(currentPosition)) {
            return 0;
        }
        
        return findPreviousWordBoundary(line, currentPosition);
    }
    
    public boolean isWhitespace(char c) {
        return Character.isWhitespace(c);
    }
    
    public boolean isEmpty(String line) {
        return line == null || line.trim().isEmpty();
    }
    
    private boolean isValidInsertPosition(String line, int position) {
        return position >= 0 && position <= line.length();
    }
    
    private boolean canDeleteChar(String line, int position) {
        return position >= 0 && position < line.length();
    }
    
    private boolean isValidRange(String line, int start, int end) {
        return start >= 0 && end >= start && end <= line.length();
    }
    
    private boolean isAtEndOfLine(String line, int position) {
        return position >= line.length();
    }
    
    private boolean isAtStartOfLine(int position) {
        return position <= 0;
    }
    
    private int findWordBoundary(String line, int position) {
        int i = position;
        
        // Skip current word
        while (i < line.length() && !isWhitespace(line.charAt(i))) {
            i++;
        }
        
        // Skip whitespace to next word
        while (i < line.length() && isWhitespace(line.charAt(i))) {
            i++;
        }
        
        return i;
    }
    
    private int findPreviousWordBoundary(String line, int position) {
        int i = position - 1;
        
        // Skip whitespace
        while (i > 0 && isWhitespace(line.charAt(i))) {
            i--;
        }
        
        // Skip to beginning of current word
        while (i > 0 && !isWhitespace(line.charAt(i - 1))) {
            i--;
        }
        
        return i;
    }
}