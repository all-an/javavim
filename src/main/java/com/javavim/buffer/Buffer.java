package com.javavim.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a text buffer in the editor.
 * Follows single responsibility principle - manages text content only.
 */
public class Buffer {
    
    private final List<String> lines;
    private boolean modified;
    private String filename;
    
    public Buffer() {
        this.lines = new ArrayList<>();
        this.modified = false;
        this.filename = null;
        lines.add(""); // Start with one empty line
    }
    
    public Buffer(String filename) {
        this();
        this.filename = filename;
    }
    
    public String getLine(int lineNumber) {
        if (isValidLineNumber(lineNumber)) {
            return lines.get(lineNumber);
        }
        return "";
    }
    
    public void setLine(int lineNumber, String content) {
        if (isValidLineNumber(lineNumber)) {
            lines.set(lineNumber, Objects.requireNonNull(content));
            setModified(true);
        }
    }
    
    public void insertLine(int lineNumber, String content) {
        if (isValidInsertPosition(lineNumber)) {
            lines.add(lineNumber, Objects.requireNonNull(content));
            setModified(true);
        }
    }
    
    public void deleteLine(int lineNumber) {
        if (canDeleteLine(lineNumber)) {
            lines.remove(lineNumber);
            setModified(true);
        }
    }
    
    public int getLineCount() {
        return lines.size();
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public List<String> getLines() {
        return new ArrayList<>(lines);
    }
    
    private boolean isValidLineNumber(int lineNumber) {
        return lineNumber >= 0 && lineNumber < lines.size();
    }
    
    private boolean isValidInsertPosition(int lineNumber) {
        return lineNumber >= 0 && lineNumber <= lines.size();
    }
    
    private boolean canDeleteLine(int lineNumber) {
        if (!isValidLineNumber(lineNumber)) {
            return false;
        }
        return lines.size() > 1;
    }
}