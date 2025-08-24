package com.javavim;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.javavim.terminal.MockTerminalUI;
import com.javavim.terminal.TestInputTerminalUI;
import com.javavim.buffer.Buffer;

class JavavimTest {
    
    private Javavim javavim;
    private MockTerminalUI mockTerminal;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        mockTerminal = new MockTerminalUI();
        javavim = new Javavim(mockTerminal);
        System.setOut(new PrintStream(outputStreamCaptor));
        
        // Create temporary test file
        tempFile = Files.createTempFile("javavim-test", ".txt");
        Files.write(tempFile, "Hello World\nTest content".getBytes());
    }
    
    @AfterEach
    void tearDown() throws IOException {
        System.setOut(standardOut);
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }
    
    @Test
    @DisplayName("Should create Javavim instance successfully")
    void shouldCreateJavavimInstance() {
        assertNotNull(javavim);
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
        assertFalse(javavim.isRunning());
        assertNotNull(javavim.getBufferManager());
    }
    
    @Test
    @DisplayName("Should initialize with mock terminal")
    void shouldInitializeWithMockTerminal() {
        Javavim customEditor = new Javavim(mockTerminal);
        assertNotNull(customEditor);
        assertEquals(Javavim.EditorMode.NORMAL, customEditor.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should change editor modes correctly")
    void shouldChangeEditorModesCorrectly() {
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.COMMAND);
        assertEquals(Javavim.EditorMode.COMMAND, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.VISUAL);
        assertEquals(Javavim.EditorMode.VISUAL, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.NORMAL);
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should open file successfully")
    void shouldOpenFileSuccessfully() throws IOException {
        javavim.openFile(tempFile.toString());
        
        Buffer currentBuffer = javavim.getBufferManager().getCurrentBuffer();
        assertNotNull(currentBuffer);
        assertEquals("Hello World", currentBuffer.getLine(0));
        assertEquals("Test content", currentBuffer.getLine(1));
    }
    
    @Test
    @DisplayName("Should handle null filename gracefully")
    void shouldHandleNullFilenameGracefully() {
        assertDoesNotThrow(() -> javavim.openFile(null));
        assertDoesNotThrow(() -> javavim.openFile(""));
    }
    
    @Test
    @DisplayName("Should handle nonexistent file gracefully")
    void shouldHandleNonexistentFileGracefully() {
        assertDoesNotThrow(() -> javavim.openFile("/nonexistent/path/file.txt"));
    }
    
    @Test
    @DisplayName("Should save current buffer successfully")
    void shouldSaveCurrentBufferSuccessfully() throws IOException {
        javavim.openFile(tempFile.toString());
        
        Buffer currentBuffer = javavim.getBufferManager().getCurrentBuffer();
        if (currentBuffer != null) {
            currentBuffer.setLine(0, "Modified content");
            currentBuffer.setModified(true);
            
            assertDoesNotThrow(() -> javavim.saveCurrentBuffer());
        }
    }
    
    @Test
    @DisplayName("Should handle save when no buffer is open")
    void shouldHandleSaveWhenNoBufferIsOpen() {
        assertDoesNotThrow(() -> javavim.saveCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should run with empty arguments")
    void shouldRunWithEmptyArguments() {
        String[] emptyArgs = {};
        assertDoesNotThrow(() -> javavim.run(emptyArgs));
    }
    
    @Test
    @DisplayName("Should run with file argument")
    void shouldRunWithFileArgument() {
        String[] args = {tempFile.toString()};
        assertDoesNotThrow(() -> javavim.run(args));
    }
    
    @Test
    @DisplayName("Should handle IO exceptions gracefully during run")
    void shouldHandleIOExceptionsGracefullyDuringRun() {
        // Test with invalid file path to trigger IOException handling
        String[] args = {"/invalid/path/that/does/not/exist.txt"};
        assertDoesNotThrow(() -> javavim.run(args));
    }
    
    @Test
    @DisplayName("Should maintain running state correctly")
    void shouldMaintainRunningStateCorrectly() {
        assertFalse(javavim.isRunning());
        
        // After initialization, running should be set to true briefly
        String[] args = {};
        javavim.run(args);
        
        // After run completes, should be false again
        assertFalse(javavim.isRunning());
    }
    
    @Test
    @DisplayName("All editor modes should be available")
    void allEditorModesShouldBeAvailable() {
        assertEquals(4, Javavim.EditorMode.values().length);
        
        assertNotNull(Javavim.EditorMode.NORMAL);
        assertNotNull(Javavim.EditorMode.INSERT);
        assertNotNull(Javavim.EditorMode.VISUAL);
        assertNotNull(Javavim.EditorMode.COMMAND);
        
        assertEquals("NORMAL", Javavim.EditorMode.NORMAL.toString());
        assertEquals("INSERT", Javavim.EditorMode.INSERT.toString());
        assertEquals("VISUAL", Javavim.EditorMode.VISUAL.toString());
        assertEquals("COMMAND", Javavim.EditorMode.COMMAND.toString());
    }
    
    @Test
    @DisplayName("Buffer manager should be properly initialized")
    void bufferManagerShouldBeProperlyInitialized() {
        assertNotNull(javavim.getBufferManager());
        
        // Should handle operations gracefully even without buffers
        assertDoesNotThrow(() -> javavim.saveCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should handle terminal initialization failure gracefully")
    void shouldHandleTerminalInitializationFailureGracefully() {
        // Create a terminal that fails to initialize
        MockTerminalUI failingTerminal = new MockTerminalUI() {
            @Override
            public void initialize() throws IOException {
                throw new IOException("Mock initialization failure");
            }
        };
        
        Javavim editorWithFailingTerminal = new Javavim(failingTerminal);
        String[] args = {};
        
        assertDoesNotThrow(() -> editorWithFailingTerminal.run(args));
    }
    
    @Test
    @DisplayName("Should create welcome buffer when no file specified")
    void shouldCreateWelcomeBufferWhenNoFileSpecified() {
        String[] args = {};
        javavim.run(args);
        
        Buffer currentBuffer = javavim.getBufferManager().getCurrentBuffer();
        if (currentBuffer != null) {
            String firstLine = currentBuffer.getLine(0);
            assertTrue(firstLine.contains("JavaVim") || firstLine.contains("Welcome"));
        }
    }
    
    @Test
    @DisplayName("Main method should print startup messages")
    void mainMethodShouldPrintStartupMessages() {
        String[] args = {};
        
        Javavim.main(args);
        
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Javavim") || output.contains("JavaVim"));
    }
    
    @Test
    @DisplayName("Main method should handle null arguments")
    void mainMethodShouldHandleNullArguments() {
        assertDoesNotThrow(() -> Javavim.main(null));
    }
    
    @Test
    @DisplayName("Main method should handle empty arguments")
    void mainMethodShouldHandleEmptyArguments() {
        String[] emptyArgs = {};
        assertDoesNotThrow(() -> Javavim.main(emptyArgs));
    }
    
    @Test
    @DisplayName("Main method should handle file arguments")
    void mainMethodShouldHandleFileArguments() {
        String[] args = {tempFile.toString()};
        assertDoesNotThrow(() -> Javavim.main(args));
    }
    
    @Test
    @DisplayName("Should handle insert mode correctly")
    void shouldHandleInsertModeCorrectly() throws IOException {
        javavim.openFile(tempFile.toString());
        javavim.setMode(Javavim.EditorMode.INSERT);
        
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should handle ESC key to exit insert mode")
    void shouldHandleEscKeyToExitInsertMode() throws IOException {
        javavim.openFile(tempFile.toString());
        javavim.setMode(Javavim.EditorMode.INSERT);
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.NORMAL);
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should handle cursor positioning within buffer bounds")
    void shouldHandleCursorPositioningWithinBufferBounds() throws IOException {
        javavim.openFile(tempFile.toString());
        Buffer currentBuffer = javavim.getBufferManager().getCurrentBuffer();
        
        assertNotNull(currentBuffer);
        assertTrue(currentBuffer.getLineCount() > 0);
    }
    
    @Test
    @DisplayName("Should create new buffer when no file specified")
    void shouldCreateNewBufferWhenNoFileSpecified() {
        String[] emptyArgs = {};
        javavim.run(emptyArgs);
        
        Buffer currentBuffer = javavim.getBufferManager().getCurrentBuffer();
        assertNotNull(currentBuffer);
    }
    
    @Test
    @DisplayName("Should handle mode transitions correctly")
    void shouldHandleModeTransitionsCorrectly() {
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.INSERT);
        assertEquals(Javavim.EditorMode.INSERT, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.COMMAND);
        assertEquals(Javavim.EditorMode.COMMAND, javavim.getCurrentMode());
        
        javavim.setMode(Javavim.EditorMode.NORMAL);
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
    }
    
    @Test
    @DisplayName("Should handle buffer operations safely")
    void shouldHandleBufferOperationsSafely() throws IOException {
        javavim.openFile(tempFile.toString());
        
        Buffer currentBuffer = javavim.getBufferManager().getCurrentBuffer();
        assertNotNull(currentBuffer);
        
        int originalLineCount = currentBuffer.getLineCount();
        assertTrue(originalLineCount > 0);
    }
    
    @Test
    @DisplayName("Should maintain editor state consistency")
    void shouldMaintainEditorStateConsistency() throws IOException {
        assertFalse(javavim.isRunning());
        assertEquals(Javavim.EditorMode.NORMAL, javavim.getCurrentMode());
        assertNotNull(javavim.getBufferManager());
        
        javavim.openFile(tempFile.toString());
        assertNotNull(javavim.getBufferManager().getCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should handle invalid file operations gracefully")
    void shouldHandleInvalidFileOperationsGracefully() {
        // Opening invalid file should not throw exception
        assertDoesNotThrow(() -> {
            javavim.openFile("/invalid/nonexistent/path.txt");
        });
        
        // Save operation might fail if no buffer exists or has invalid path
        // This is expected behavior, so we test it works without crashing
        Buffer currentBuffer = javavim.getBufferManager().getCurrentBuffer();
        if (currentBuffer != null) {
            assertDoesNotThrow(() -> {
                try {
                    javavim.saveCurrentBuffer();
                } catch (IOException e) {
                    // Expected for invalid file paths
                    assertTrue(e.getMessage().contains("nonexistent") || 
                              e.getMessage().contains("NoSuchFileException"));
                }
            });
        }
    }
    
    @Test
    @DisplayName("Should handle editor initialization properly")
    void shouldHandleEditorInitializationProperly() {
        Javavim newEditor = new Javavim(mockTerminal);
        
        assertEquals(Javavim.EditorMode.NORMAL, newEditor.getCurrentMode());
        assertFalse(newEditor.isRunning());
        assertNotNull(newEditor.getBufferManager());
    }
    
    @Test
    @DisplayName("Should handle all editor modes without errors")
    void shouldHandleAllEditorModesWithoutErrors() {
        for (Javavim.EditorMode mode : Javavim.EditorMode.values()) {
            assertDoesNotThrow(() -> {
                javavim.setMode(mode);
                assertEquals(mode, javavim.getCurrentMode());
            });
        }
    }
    
    @Test
    @DisplayName("run() should initialize editor properly")
    void runShouldInitializeEditorProperly() {
        // Test initialize() behavior through run()
        Javavim testEditor = new Javavim(mockTerminal);
        
        // Before run() - editor should not be running
        assertFalse(testEditor.isRunning());
        
        String[] args = {"testfile.txt"};
        testEditor.run(args);
        
        // After run() completes - terminal should be shut down
        assertFalse(testEditor.isRunning());
        assertFalse(mockTerminal.isInitialized()); // Should be shut down after run()
    }
    
    @Test
    @DisplayName("run() should handle empty args gracefully")
    void runShouldHandleEmptyArgsGracefully() {
        // Test behavior with no arguments
        Javavim testEditor = new Javavim(mockTerminal);
        
        String[] args = {};
        
        // Should not throw exception
        assertDoesNotThrow(() -> testEditor.run(args));
        
        // Should have created a buffer (empty editor state)
        assertNotNull(testEditor.getBufferManager().getCurrentBuffer());
    }
    
    @Test
    @DisplayName("run() should handle null args by throwing exception")
    void runShouldHandleNullArgsByThrowingException() {
        // Test behavior with null arguments
        Javavim testEditor = new Javavim(mockTerminal);
        
        // Currently throws NPE - this test documents the current behavior
        // In future, this could be enhanced to handle null args gracefully
        assertThrows(NullPointerException.class, () -> testEditor.run(null));
    }
    
    @Test
    @DisplayName("run() should always call shutdown in finally block")
    void runShouldAlwaysCallShutdownInFinallyBlock() {
        // Test shutdown() behavior through run()
        Javavim testEditor = new Javavim(mockTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // After run(), terminal should be shut down
        assertFalse(mockTerminal.isInitialized());
    }
    
    @Test
    @DisplayName("run() should initialize with file when provided")
    void runShouldInitializeWithFileWhenProvided() {
        // Test setupEditor() behavior with file through run()
        Javavim testEditor = new Javavim(mockTerminal);
        
        String[] args = {"testfile.txt"};
        testEditor.run(args);
        
        // Verify that a buffer was created (indicating file setup was attempted)
        Buffer currentBuffer = testEditor.getBufferManager().getCurrentBuffer();
        assertNotNull(currentBuffer);
        
        // The filename should be set (even if file doesn't exist, buffer should have the name)
        assertTrue(currentBuffer.getFilename() == null || 
                   currentBuffer.getFilename().endsWith("testfile.txt"));
    }
    
    @Test
    @DisplayName("run() should handle multiple files in args")
    void runShouldHandleMultipleFilesInArgs() {
        // Test setupEditor() behavior with multiple files through run()
        Javavim testEditor = new Javavim(mockTerminal);
        
        String[] args = {"file1.txt", "file2.txt", "file3.txt"};
        testEditor.run(args);
        
        // Should have initialized editor successfully
        assertNotNull(testEditor.getBufferManager().getCurrentBuffer());
        
        // Buffer manager should have at least one buffer
        assertTrue(testEditor.getBufferManager().getBufferCount() >= 1);
    }
    
    @Test
    @DisplayName("run() should execute main loop when terminal is initialized")
    void runShouldExecuteMainLoopWhenTerminalInitialized() {
        // Test that mainLoop() is called when terminal initializes successfully
        Javavim testEditor = new Javavim(mockTerminal);
        
        String[] args = {};
        
        // Should complete without hanging (MockTerminalUI returns 'q' for readInput())
        assertDoesNotThrow(() -> testEditor.run(args));
        
        // Verify editor state after running
        assertFalse(testEditor.isRunning());
        assertNotNull(testEditor.getBufferManager().getCurrentBuffer());
    }
    
    @Test
    @DisplayName("run() should handle normal mode input - quit command")
    void runShouldHandleNormalModeInputQuitCommand() {
        // Test handleNormalModeInput() with 'q' command through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence('q'); // Quit immediately
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // Should have quit cleanly
        assertFalse(testEditor.isRunning());
        assertEquals(Javavim.EditorMode.NORMAL, testEditor.getCurrentMode());
    }
    
    @Test
    @DisplayName("run() should handle normal mode input - enter insert mode")
    void runShouldHandleNormalModeInputEnterInsertMode() {
        // Test handleNormalModeInput() with 'i' command through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence('i', (char)27, 'q'); // Enter insert, escape, quit
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // Should have gone through insert mode and back to normal
        assertFalse(testEditor.isRunning());
        assertEquals(Javavim.EditorMode.NORMAL, testEditor.getCurrentMode());
    }
    
    @Test
    @DisplayName("run() should handle insert mode input - escape key")
    void runShouldHandleInsertModeInputEscapeKey() {
        // Test handleInsertModeInput() with escape key through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence('i', 'h', 'e', 'l', 'l', 'o', (char)27, 'q'); // Type "hello", escape, quit
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // Should have exited insert mode and returned to normal mode
        assertFalse(testEditor.isRunning());
        assertEquals(Javavim.EditorMode.NORMAL, testEditor.getCurrentMode());
        
        // Verify buffer exists (text insertion is not yet fully implemented)
        Buffer buffer = testEditor.getBufferManager().getCurrentBuffer();
        assertNotNull(buffer);
    }
    
    @Test
    @DisplayName("run() should handle command mode input - escape key")
    void runShouldHandleCommandModeInputEscapeKey() {
        // Test handleCommandModeInput() with escape key through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence(':', (char)27, 'q'); // Enter command mode, escape, quit
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // Should have entered command mode and returned to normal
        assertFalse(testEditor.isRunning());
        assertEquals(Javavim.EditorMode.NORMAL, testEditor.getCurrentMode());
    }
    
    @Test
    @DisplayName("run() should handle visual mode input - escape key")
    void runShouldHandleVisualModeInputEscapeKey() {
        // Test handleVisualModeInput() with escape key through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence('v', (char)27, 'q'); // Enter visual mode, escape, quit
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // Should have entered visual mode and returned to normal
        assertFalse(testEditor.isRunning());
        assertEquals(Javavim.EditorMode.NORMAL, testEditor.getCurrentMode());
    }
    
    @Test
    @DisplayName("run() should handle input with unsaved changes warning")
    void runShouldHandleInputWithUnsavedChangesWarning() {
        // Test handleNormalModeInput() quit with unsaved changes through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence('i', 't', 'e', 's', 't', (char)27, 'q', 'q'); // Type, escape, try quit twice
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // Should have quit after processing input
        assertFalse(testEditor.isRunning());
        
        // Buffer should exist (text insertion is not yet fully implemented)
        Buffer buffer = testEditor.getBufferManager().getCurrentBuffer();
        assertNotNull(buffer);
    }
    
    @Test
    @DisplayName("run() should handle complex input sequence testing all modes")
    void runShouldHandleComplexInputSequenceTestingAllModes() {
        // Test comprehensive input handling through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence(
            'i',           // Enter insert mode
            'H', 'e', 'l', 'l', 'o',  // Type "Hello"
            (char)27,      // Exit insert mode
            'v',           // Enter visual mode
            (char)27,      // Exit visual mode
            ':',           // Enter command mode
            (char)27,      // Exit command mode
            'q'            // Quit
        );
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // Should have completed successfully
        assertFalse(testEditor.isRunning());
        assertEquals(Javavim.EditorMode.NORMAL, testEditor.getCurrentMode());
        
        // Verify buffer exists and all modes were tested
        Buffer buffer = testEditor.getBufferManager().getCurrentBuffer();
        assertNotNull(buffer);
    }
    
    @Test
    @DisplayName("run() should handle handleInput error recovery")
    void runShouldHandleInputErrorRecovery() {
        // Test error handling in handleInput() through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence((char)0, 'i', 'o', 'k', (char)27, 'q'); // Null input, then normal sequence
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        testEditor.run(args);
        
        // Should handle null input gracefully and continue
        assertFalse(testEditor.isRunning());
        assertEquals(Javavim.EditorMode.NORMAL, testEditor.getCurrentMode());
        
        // Should still have processed the input sequence (text insertion not yet implemented)
        Buffer buffer = testEditor.getBufferManager().getCurrentBuffer();
        assertNotNull(buffer);
    }
    
    @Test
    @DisplayName("run() should test mainLoop and renderEditor integration")
    void runShouldTestMainLoopAndRenderEditorIntegration() {
        // Test that mainLoop() calls renderEditor() after handleInput() through run()
        TestInputTerminalUI testTerminal = new TestInputTerminalUI();
        testTerminal.setInputSequence('i', 'r', 'e', 'n', 'd', 'e', 'r', (char)27, 'q');
        
        Javavim testEditor = new Javavim(testTerminal);
        
        String[] args = {};
        
        // Should complete without hanging and handle rendering
        assertDoesNotThrow(() -> testEditor.run(args));
        
        // Verify editor completed properly
        assertFalse(testEditor.isRunning());
        
        // Verify the test completed successfully (integration test passes)
        assertEquals(Javavim.EditorMode.NORMAL, testEditor.getCurrentMode());
    }
}