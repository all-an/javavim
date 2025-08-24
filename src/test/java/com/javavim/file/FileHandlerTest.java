package com.javavim.file;

import com.javavim.buffer.Buffer;
import com.javavim.io.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileHandlerTest {
    
    @Mock
    private FileManager mockFileManager;
    
    private FileHandler fileHandler;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileHandler = new FileHandler(mockFileManager);
        fileHandler.setCurrentWorkingDirectory(tempDir.toString());
    }
    
    @Test
    @DisplayName("Should create FileHandler with empty recent files")
    void shouldCreateFileHandlerWithEmptyRecentFiles() {
        assertTrue(fileHandler.getRecentFiles().isEmpty());
        assertEquals(tempDir.toString(), fileHandler.getCurrentWorkingDirectory());
    }
    
    @Test
    @DisplayName("Should open existing file")
    void shouldOpenExistingFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.createFile(testFile);
        
        Buffer mockBuffer = new Buffer("test.txt");
        when(mockFileManager.loadFile(testFile.toString())).thenReturn(mockBuffer);
        
        FileHandler.FileOperationResult result = fileHandler.openFile("test.txt");
        
        assertTrue(result.isSuccess());
        assertEquals(mockBuffer, result.getBuffer());
        assertTrue(result.getMessage().contains("Opened:"));
        assertEquals(1, fileHandler.getRecentFiles().size());
        assertEquals(testFile.toString(), fileHandler.getRecentFiles().get(0));
    }
    
    @Test
    @DisplayName("Should create new file when opening non-existent file")
    void shouldCreateNewFileWhenOpeningNonExistentFile() throws IOException {
        when(mockFileManager.loadFile(anyString())).thenThrow(new IOException("File not found"));
        
        FileHandler.FileOperationResult result = fileHandler.openFile("newfile.txt");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getBuffer());
        assertTrue(result.getBuffer().getFilename().endsWith("newfile.txt"));
        assertFalse(result.getBuffer().isModified());
    }
    
    @Test
    @DisplayName("Should reject invalid filename")
    void shouldRejectInvalidFilename() {
        FileHandler.FileOperationResult result = fileHandler.openFile("");
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid filename"));
    }
    
    @Test
    @DisplayName("Should reject null filename")
    void shouldRejectNullFilename() {
        FileHandler.FileOperationResult result = fileHandler.openFile(null);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid filename"));
    }
    
    @Test
    @DisplayName("Should reject filename with null character")
    void shouldRejectFilenameWithNullCharacter() {
        FileHandler.FileOperationResult result = fileHandler.openFile("test\0file.txt");
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid filename"));
    }
    
    @Test
    @DisplayName("Should reject dot and dotdot filenames")
    void shouldRejectDotAndDotdotFilenames() {
        FileHandler.FileOperationResult result1 = fileHandler.openFile(".");
        FileHandler.FileOperationResult result2 = fileHandler.openFile("..");
        
        assertFalse(result1.isSuccess());
        assertFalse(result2.isSuccess());
    }
    
    @Test
    @DisplayName("Should save buffer to specific file")
    void shouldSaveBufferToSpecificFile() throws IOException {
        Buffer buffer = new Buffer();
        buffer.setLine(0, "Test content");
        
        FileHandler.FileOperationResult result = fileHandler.saveFile(buffer, "output.txt");
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Saved to:"));
        verify(mockFileManager).saveBufferAs(eq(buffer), anyString());
        assertTrue(fileHandler.getRecentFiles().contains(tempDir.resolve("output.txt").toString()));
    }
    
    @Test
    @DisplayName("Should save current buffer")
    void shouldSaveCurrentBuffer() throws IOException {
        Buffer buffer = new Buffer("existing.txt");
        
        FileHandler.FileOperationResult result = fileHandler.saveFile(buffer, null);
        
        assertTrue(result.isSuccess());
        assertEquals("File saved", result.getMessage());
        verify(mockFileManager).saveBuffer(buffer);
    }
    
    @Test
    @DisplayName("Should save current buffer with empty filename")
    void shouldSaveCurrentBufferWithEmptyFilename() throws IOException {
        Buffer buffer = new Buffer("existing.txt");
        
        FileHandler.FileOperationResult result = fileHandler.saveFile(buffer, "");
        
        assertTrue(result.isSuccess());
        verify(mockFileManager).saveBuffer(buffer);
    }
    
    @Test
    @DisplayName("Should handle save error")
    void shouldHandleSaveError() throws IOException {
        Buffer buffer = new Buffer("test.txt");
        doThrow(new IOException("Permission denied")).when(mockFileManager).saveBuffer(buffer);
        
        FileHandler.FileOperationResult result = fileHandler.saveFile(buffer, null);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Cannot save file"));
    }
    
    @Test
    @DisplayName("Should handle null buffer for save")
    void shouldHandleNullBufferForSave() {
        FileHandler.FileOperationResult result = fileHandler.saveFile(null, "test.txt");
        
        assertFalse(result.isSuccess());
        assertEquals("No buffer to save", result.getMessage());
    }
    
    @Test
    @DisplayName("Should list files in directory")
    void shouldListFilesInDirectory() throws IOException {
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.java"));
        Files.createDirectory(tempDir.resolve("subdir"));
        
        List<String> files = fileHandler.listFilesInDirectory(tempDir.toString());
        
        assertEquals(2, files.size());
        assertTrue(files.contains("file1.txt"));
        assertTrue(files.contains("file2.java"));
        assertFalse(files.contains("subdir")); // Should not include directories
    }
    
    @Test
    @DisplayName("Should return empty list for non-existent directory")
    void shouldReturnEmptyListForNonExistentDirectory() {
        List<String> files = fileHandler.listFilesInDirectory("/non/existent/directory");
        
        assertTrue(files.isEmpty());
    }
    
    @Test
    @DisplayName("Should check if file exists")
    void shouldCheckIfFileExists() throws IOException {
        Path existingFile = tempDir.resolve("existing.txt");
        Files.createFile(existingFile);
        
        assertTrue(fileHandler.fileExists("existing.txt"));
        assertFalse(fileHandler.fileExists("nonexistent.txt"));
    }
    
    @Test
    @DisplayName("Should return false for invalid filename existence check")
    void shouldReturnFalseForInvalidFilenameExistenceCheck() {
        assertFalse(fileHandler.fileExists(null));
        assertFalse(fileHandler.fileExists(""));
        assertFalse(fileHandler.fileExists("test\0file.txt"));
    }
    
    @Test
    @DisplayName("Should manage recent files list")
    void shouldManageRecentFilesList() {
        fileHandler.openFile("file1.txt");
        fileHandler.openFile("file2.txt");
        fileHandler.openFile("file1.txt"); // Duplicate should move to front
        
        List<String> recentFiles = fileHandler.getRecentFiles();
        assertEquals(2, recentFiles.size());
        assertTrue(recentFiles.get(0).endsWith("file1.txt"));
        assertTrue(recentFiles.get(1).endsWith("file2.txt"));
    }
    
    @Test
    @DisplayName("Should limit recent files to 10")
    void shouldLimitRecentFilesToTen() {
        for (int i = 1; i <= 15; i++) {
            fileHandler.openFile("file" + i + ".txt");
        }
        
        List<String> recentFiles = fileHandler.getRecentFiles();
        assertEquals(10, recentFiles.size());
        assertTrue(recentFiles.get(0).endsWith("file15.txt"));
        assertTrue(recentFiles.get(9).endsWith("file6.txt"));
    }
    
    @Test
    @DisplayName("Should set and get current working directory")
    void shouldSetAndGetCurrentWorkingDirectory() throws IOException {
        Path newDir = tempDir.resolve("newdir");
        Files.createDirectory(newDir);
        
        fileHandler.setCurrentWorkingDirectory(newDir.toString());
        assertEquals(newDir.toString(), fileHandler.getCurrentWorkingDirectory());
    }
    
    @Test
    @DisplayName("Should not change directory to invalid path")
    void shouldNotChangeDirectoryToInvalidPath() {
        String originalDir = fileHandler.getCurrentWorkingDirectory();
        
        fileHandler.setCurrentWorkingDirectory("/invalid/path");
        assertEquals(originalDir, fileHandler.getCurrentWorkingDirectory());
        
        fileHandler.setCurrentWorkingDirectory(null);
        assertEquals(originalDir, fileHandler.getCurrentWorkingDirectory());
        
        fileHandler.setCurrentWorkingDirectory("");
        assertEquals(originalDir, fileHandler.getCurrentWorkingDirectory());
    }
    
    @Test
    @DisplayName("Should resolve absolute file paths")
    void shouldResolveAbsoluteFilePaths() throws IOException {
        Path absoluteFile = tempDir.resolve("absolute.txt");
        Files.createFile(absoluteFile);
        
        Buffer mockBuffer = new Buffer("absolute.txt");
        when(mockFileManager.loadFile(absoluteFile.toString())).thenReturn(mockBuffer);
        
        FileHandler.FileOperationResult result = fileHandler.openFile(absoluteFile.toString());
        
        assertTrue(result.isSuccess());
        assertTrue(fileHandler.getRecentFiles().get(0).equals(absoluteFile.toString()));
    }
    
    @Test
    @DisplayName("Should handle IOException when opening file")
    void shouldHandleIOExceptionWhenOpeningFile() throws IOException {
        Path testFile = tempDir.resolve("error.txt");
        Files.createFile(testFile);
        
        when(mockFileManager.loadFile(testFile.toString()))
            .thenThrow(new IOException("Read permission denied"));
        
        FileHandler.FileOperationResult result = fileHandler.openFile("error.txt");
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Cannot open file"));
    }
    
    @Test
    @DisplayName("FileOperationResult should create success and error results")
    void fileOperationResultShouldCreateSuccessAndErrorResults() {
        Buffer buffer = new Buffer();
        
        FileHandler.FileOperationResult success = 
            FileHandler.FileOperationResult.success("Success message", buffer);
        assertTrue(success.isSuccess());
        assertEquals("Success message", success.getMessage());
        assertEquals(buffer, success.getBuffer());
        
        FileHandler.FileOperationResult error = 
            FileHandler.FileOperationResult.error("Error message");
        assertFalse(error.isSuccess());
        assertEquals("Error message", error.getMessage());
        assertNull(error.getBuffer());
    }
}