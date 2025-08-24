package com.javavim.io;

import com.javavim.buffer.Buffer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles file I/O operations for the editor.
 * Follows single responsibility principle - manages file operations only.
 */
public class FileManager {
    
    public Buffer loadFile(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        
        Path path = Paths.get(filename);
        if (Files.exists(path)) {
            return loadExistingFile(path, filename);
        }
        return createNewBuffer(filename);
    }
    
    public void saveBuffer(Buffer buffer) throws IOException {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer cannot be null");
        }
        
        if (hasFilename(buffer)) {
            saveToFile(buffer);
        }
    }
    
    public void saveBufferAs(Buffer buffer, String filename) throws IOException {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer cannot be null");
        }
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        
        buffer.setFilename(filename);
        saveToFile(buffer);
    }
    
    public boolean fileExists(String filename) {
        if (filename == null) {
            return false;
        }
        return Files.exists(Paths.get(filename));
    }
    
    public boolean isReadable(String filename) {
        if (filename == null) {
            return false;
        }
        Path path = Paths.get(filename);
        return Files.exists(path) && Files.isReadable(path);
    }
    
    public boolean isWritable(String filename) {
        if (filename == null) {
            return false;
        }
        Path path = Paths.get(filename);
        if (Files.exists(path)) {
            return Files.isWritable(path);
        }
        return isParentDirectoryWritable(path);
    }
    
    private Buffer loadExistingFile(Path path, String filename) throws IOException {
        List<String> lines = Files.readAllLines(path);
        Buffer buffer = new Buffer(filename);
        
        if (lines.isEmpty()) {
            return buffer;
        }
        
        buffer.setLine(0, lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            buffer.insertLine(i, lines.get(i));
        }
        
        buffer.setModified(false);
        return buffer;
    }
    
    private Buffer createNewBuffer(String filename) {
        return new Buffer(filename);
    }
    
    private boolean hasFilename(Buffer buffer) {
        return buffer.getFilename() != null;
    }
    
    private void saveToFile(Buffer buffer) throws IOException {
        Path path = Paths.get(buffer.getFilename());
        Files.write(path, buffer.getLines());
        buffer.setModified(false);
    }
    
    private boolean isParentDirectoryWritable(Path path) {
        Path parent = path.getParent();
        if (parent == null) {
            return true;
        }
        return Files.exists(parent) && Files.isWritable(parent);
    }
}