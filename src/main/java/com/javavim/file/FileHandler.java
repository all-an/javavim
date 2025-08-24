package com.javavim.file;

import com.javavim.buffer.Buffer;
import com.javavim.io.FileManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles dynamic file operations and validation.
 * Follows single responsibility principle - manages file operations only.
 */
public class FileHandler {
    
    private final FileManager fileManager;
    private final List<String> recentFiles;
    private String currentWorkingDirectory;
    
    public FileHandler(FileManager fileManager) {
        this.fileManager = fileManager;
        this.recentFiles = new ArrayList<>();
        this.currentWorkingDirectory = System.getProperty("user.dir");
    }
    
    public FileOperationResult openFile(String filename) {
        if (!isValidFilename(filename)) {
            return FileOperationResult.error("Invalid filename: " + filename);
        }
        
        try {
            Path filePath = resolveFilePath(filename);
            Buffer buffer = attemptFileLoad(filePath);
            addToRecentFiles(filePath.toString());
            return FileOperationResult.success("Opened: " + filePath.toString(), buffer);
        } catch (IOException e) {
            return FileOperationResult.error("Cannot open file: " + filename + " - " + e.getMessage());
        }
    }
    
    public FileOperationResult saveFile(Buffer buffer, String filename) {
        if (buffer == null) {
            return FileOperationResult.error("No buffer to save");
        }
        
        if (filename != null && !filename.isEmpty()) {
            return saveBufferToSpecificFile(buffer, filename);
        } else {
            return saveCurrentBuffer(buffer);
        }
    }
    
    public List<String> listFilesInDirectory(String directory) {
        List<String> files = new ArrayList<>();
        
        try {
            Path dirPath = resolveDirectoryPath(directory);
            if (Files.isDirectory(dirPath)) {
                Files.list(dirPath)
                    .filter(path -> Files.isRegularFile(path))
                    .forEach(path -> files.add(path.getFileName().toString()));
            }
        } catch (IOException e) {
            // Return empty list if directory can't be read
        }
        
        return files;
    }
    
    public List<String> getRecentFiles() {
        return new ArrayList<>(recentFiles);
    }
    
    public boolean fileExists(String filename) {
        if (!isValidFilename(filename)) {
            return false;
        }
        
        try {
            Path filePath = resolveFilePath(filename);
            return Files.exists(filePath) && Files.isRegularFile(filePath);
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getCurrentWorkingDirectory() {
        return currentWorkingDirectory;
    }
    
    public void setCurrentWorkingDirectory(String directory) {
        if (isValidDirectory(directory)) {
            this.currentWorkingDirectory = directory;
        }
    }
    
    private boolean isValidFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        // Check for invalid characters
        return !filename.contains("\0") && 
               !filename.trim().equals(".") && 
               !filename.trim().equals("..");
    }
    
    private boolean isValidDirectory(String directory) {
        if (directory == null || directory.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path dirPath = Paths.get(directory);
            return Files.exists(dirPath) && Files.isDirectory(dirPath);
        } catch (Exception e) {
            return false;
        }
    }
    
    private Path resolveFilePath(String filename) {
        Path path = Paths.get(filename);
        
        if (path.isAbsolute()) {
            return path;
        } else {
            return Paths.get(currentWorkingDirectory, filename);
        }
    }
    
    private Path resolveDirectoryPath(String directory) {
        if (directory == null || directory.trim().isEmpty()) {
            return Paths.get(currentWorkingDirectory);
        }
        
        Path path = Paths.get(directory);
        return path.isAbsolute() ? path : Paths.get(currentWorkingDirectory, directory);
    }
    
    private Buffer attemptFileLoad(Path filePath) throws IOException {
        if (Files.exists(filePath)) {
            return fileManager.loadFile(filePath.toString());
        } else {
            return createNewFileBuffer(filePath.toString());
        }
    }
    
    private Buffer createNewFileBuffer(String filename) {
        Buffer buffer = new Buffer();
        buffer.setFilename(filename);
        buffer.setLine(0, ""); // Start with empty line
        buffer.setModified(false);
        return buffer;
    }
    
    private void addToRecentFiles(String filename) {
        recentFiles.remove(filename); // Remove if already exists
        recentFiles.add(0, filename); // Add to beginning
        
        // Keep only last 10 recent files
        if (recentFiles.size() > 10) {
            recentFiles.remove(recentFiles.size() - 1);
        }
    }
    
    private FileOperationResult saveBufferToSpecificFile(Buffer buffer, String filename) {
        try {
            Path filePath = resolveFilePath(filename);
            fileManager.saveBufferAs(buffer, filePath.toString());
            buffer.setFilename(filePath.toString());
            addToRecentFiles(filePath.toString());
            return FileOperationResult.success("Saved to: " + filePath.toString(), buffer);
        } catch (IOException e) {
            return FileOperationResult.error("Cannot save to: " + filename + " - " + e.getMessage());
        }
    }
    
    private FileOperationResult saveCurrentBuffer(Buffer buffer) {
        try {
            fileManager.saveBuffer(buffer);
            String filename = buffer.getFilename();
            if (filename != null) {
                addToRecentFiles(filename);
            }
            return FileOperationResult.success("File saved", buffer);
        } catch (IOException e) {
            return FileOperationResult.error("Cannot save file: " + e.getMessage());
        }
    }
    
    /**
     * Result class for file operations
     */
    public static class FileOperationResult {
        private final boolean success;
        private final String message;
        private final Buffer buffer;
        
        private FileOperationResult(boolean success, String message, Buffer buffer) {
            this.success = success;
            this.message = message;
            this.buffer = buffer;
        }
        
        public static FileOperationResult success(String message, Buffer buffer) {
            return new FileOperationResult(true, message, buffer);
        }
        
        public static FileOperationResult error(String message) {
            return new FileOperationResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Buffer getBuffer() {
            return buffer;
        }
    }
}