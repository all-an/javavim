package com.javavim.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import com.javavim.buffer.Buffer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class FileManagerTest {
    
    private FileManager fileManager;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
    }
    
    @Test
    @DisplayName("Should load existing file correctly")
    void shouldLoadExistingFileCorrectly() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, Arrays.asList("Line 1", "Line 2", "Line 3"));
        
        Buffer buffer = fileManager.loadFile(testFile.toString());
        
        assertEquals(testFile.toString(), buffer.getFilename());
        assertEquals(3, buffer.getLineCount());
        assertEquals("Line 1", buffer.getLine(0));
        assertEquals("Line 2", buffer.getLine(1));
        assertEquals("Line 3", buffer.getLine(2));
        assertFalse(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should create new buffer for non-existent file")
    void shouldCreateNewBufferForNonExistentFile() throws IOException {
        String nonExistentFile = tempDir.resolve("new.txt").toString();
        
        Buffer buffer = fileManager.loadFile(nonExistentFile);
        
        assertEquals(nonExistentFile, buffer.getFilename());
        assertEquals(1, buffer.getLineCount());
        assertEquals("", buffer.getLine(0));
        assertFalse(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should load empty file correctly")
    void shouldLoadEmptyFileCorrectly() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.createFile(emptyFile);
        
        Buffer buffer = fileManager.loadFile(emptyFile.toString());
        
        assertEquals(1, buffer.getLineCount());
        assertEquals("", buffer.getLine(0));
        assertFalse(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should throw exception for null filename in load")
    void shouldThrowExceptionForNullFilenameInLoad() {
        assertThrows(IllegalArgumentException.class, () -> fileManager.loadFile(null));
    }
    
    @Test
    @DisplayName("Should save buffer to existing filename")
    void shouldSaveBufferToExistingFilename() throws IOException {
        Path testFile = tempDir.resolve("save_test.txt");
        Buffer buffer = new Buffer(testFile.toString());
        buffer.setLine(0, "Saved content");
        buffer.insertLine(1, "Second line");
        
        fileManager.saveBuffer(buffer);
        
        assertTrue(Files.exists(testFile));
        var savedLines = Files.readAllLines(testFile);
        assertEquals(2, savedLines.size());
        assertEquals("Saved content", savedLines.get(0));
        assertEquals("Second line", savedLines.get(1));
        assertFalse(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should not save buffer without filename")
    void shouldNotSaveBufferWithoutFilename() throws IOException {
        Buffer buffer = new Buffer();
        buffer.setLine(0, "Content");
        
        assertDoesNotThrow(() -> fileManager.saveBuffer(buffer));
        assertTrue(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should throw exception for null buffer in save")
    void shouldThrowExceptionForNullBufferInSave() {
        assertThrows(IllegalArgumentException.class, () -> fileManager.saveBuffer(null));
    }
    
    @Test
    @DisplayName("Should save buffer as new filename")
    void shouldSaveBufferAsNewFilename() throws IOException {
        Path newFile = tempDir.resolve("save_as_test.txt");
        Buffer buffer = new Buffer();
        buffer.setLine(0, "Save as content");
        
        fileManager.saveBufferAs(buffer, newFile.toString());
        
        assertEquals(newFile.toString(), buffer.getFilename());
        assertTrue(Files.exists(newFile));
        var savedLines = Files.readAllLines(newFile);
        assertEquals(1, savedLines.size());
        assertEquals("Save as content", savedLines.get(0));
        assertFalse(buffer.isModified());
    }
    
    @Test
    @DisplayName("Should throw exception for null parameters in save as")
    void shouldThrowExceptionForNullParametersInSaveAs() {
        Buffer buffer = new Buffer();
        
        assertThrows(IllegalArgumentException.class, () -> fileManager.saveBufferAs(null, "test.txt"));
        assertThrows(IllegalArgumentException.class, () -> fileManager.saveBufferAs(buffer, null));
    }
    
    @Test
    @DisplayName("Should check file existence correctly")
    void shouldCheckFileExistenceCorrectly() throws IOException {
        Path existingFile = tempDir.resolve("existing.txt");
        Files.createFile(existingFile);
        String nonExistentFile = tempDir.resolve("non_existent.txt").toString();
        
        assertTrue(fileManager.fileExists(existingFile.toString()));
        assertFalse(fileManager.fileExists(nonExistentFile));
        assertFalse(fileManager.fileExists(null));
    }
    
    @Test
    @DisplayName("Should check file readability correctly")
    void shouldCheckFileReadabilityCorrectly() throws IOException {
        Path readableFile = tempDir.resolve("readable.txt");
        Files.createFile(readableFile);
        String nonExistentFile = tempDir.resolve("non_existent.txt").toString();
        
        assertTrue(fileManager.isReadable(readableFile.toString()));
        assertFalse(fileManager.isReadable(nonExistentFile));
        assertFalse(fileManager.isReadable(null));
    }
    
    @Test
    @DisplayName("Should check file writability correctly")
    void shouldCheckFileWritabilityCorrectly() throws IOException {
        Path writableFile = tempDir.resolve("writable.txt");
        Files.createFile(writableFile);
        String newFile = tempDir.resolve("new_writable.txt").toString();
        
        assertTrue(fileManager.isWritable(writableFile.toString()));
        assertTrue(fileManager.isWritable(newFile));
        assertFalse(fileManager.isWritable(null));
    }
}