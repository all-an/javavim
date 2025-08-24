package com.javavim.buffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class BufferManagerTest {
    
    private BufferManager bufferManager;
    
    @BeforeEach
    void setUp() {
        bufferManager = new BufferManager();
    }
    
    @Test
    @DisplayName("Should create empty buffer manager")
    void shouldCreateEmptyBufferManager() {
        assertEquals(0, bufferManager.getBufferCount());
        assertEquals(-1, bufferManager.getCurrentBufferIndex());
        assertNull(bufferManager.getCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should create new buffer")
    void shouldCreateNewBuffer() {
        Buffer buffer = bufferManager.createNewBuffer();
        
        assertNotNull(buffer);
        assertEquals(1, bufferManager.getBufferCount());
        assertEquals(0, bufferManager.getCurrentBufferIndex());
        assertEquals(buffer, bufferManager.getCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should create new buffer with filename")
    void shouldCreateNewBufferWithFilename() {
        Buffer buffer = bufferManager.createNewBuffer("test.txt");
        
        assertNotNull(buffer);
        assertEquals("test.txt", buffer.getFilename());
        assertEquals(buffer, bufferManager.getCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should add buffer")
    void shouldAddBuffer() {
        Buffer buffer = new Buffer("manual.txt");
        bufferManager.addBuffer(buffer);
        
        assertEquals(1, bufferManager.getBufferCount());
        assertEquals(buffer, bufferManager.getCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should ignore adding null buffer")
    void shouldIgnoreAddingNullBuffer() {
        bufferManager.addBuffer(null);
        
        assertEquals(0, bufferManager.getBufferCount());
    }
    
    @Test
    @DisplayName("Should switch to buffer by index")
    void shouldSwitchToBufferByIndex() {
        Buffer buffer1 = bufferManager.createNewBuffer("file1.txt");
        Buffer buffer2 = bufferManager.createNewBuffer("file2.txt");
        
        assertTrue(bufferManager.switchToBuffer(0));
        assertEquals(buffer1, bufferManager.getCurrentBuffer());
        assertEquals(0, bufferManager.getCurrentBufferIndex());
        
        assertTrue(bufferManager.switchToBuffer(1));
        assertEquals(buffer2, bufferManager.getCurrentBuffer());
        assertEquals(1, bufferManager.getCurrentBufferIndex());
    }
    
    @Test
    @DisplayName("Should not switch to invalid buffer index")
    void shouldNotSwitchToInvalidBufferIndex() {
        bufferManager.createNewBuffer();
        
        assertFalse(bufferManager.switchToBuffer(-1));
        assertFalse(bufferManager.switchToBuffer(1));
        assertEquals(0, bufferManager.getCurrentBufferIndex());
    }
    
    @Test
    @DisplayName("Should switch to next buffer")
    void shouldSwitchToNextBuffer() {
        Buffer buffer1 = bufferManager.createNewBuffer("file1.txt");
        Buffer buffer2 = bufferManager.createNewBuffer("file2.txt");
        
        bufferManager.switchToBuffer(0);
        assertTrue(bufferManager.switchToNextBuffer());
        assertEquals(buffer2, bufferManager.getCurrentBuffer());
        
        assertTrue(bufferManager.switchToNextBuffer());
        assertEquals(buffer1, bufferManager.getCurrentBuffer()); // Wrap around
    }
    
    @Test
    @DisplayName("Should not switch next buffer when no buffers")
    void shouldNotSwitchNextBufferWhenNoBuffers() {
        assertFalse(bufferManager.switchToNextBuffer());
    }
    
    @Test
    @DisplayName("Should switch to previous buffer")
    void shouldSwitchToPreviousBuffer() {
        Buffer buffer1 = bufferManager.createNewBuffer("file1.txt");
        Buffer buffer2 = bufferManager.createNewBuffer("file2.txt");
        
        bufferManager.switchToBuffer(1);
        assertTrue(bufferManager.switchToPreviousBuffer());
        assertEquals(buffer1, bufferManager.getCurrentBuffer());
        
        assertTrue(bufferManager.switchToPreviousBuffer());
        assertEquals(buffer2, bufferManager.getCurrentBuffer()); // Wrap around
    }
    
    @Test
    @DisplayName("Should not switch previous buffer when no buffers")
    void shouldNotSwitchPreviousBufferWhenNoBuffers() {
        assertFalse(bufferManager.switchToPreviousBuffer());
    }
    
    @Test
    @DisplayName("Should close current buffer")
    void shouldCloseCurrentBuffer() {
        Buffer buffer1 = bufferManager.createNewBuffer("file1.txt");
        bufferManager.createNewBuffer("file2.txt");
        
        assertTrue(bufferManager.closeCurrentBuffer());
        assertEquals(1, bufferManager.getBufferCount());
        assertEquals(buffer1, bufferManager.getCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should close last buffer")
    void shouldCloseLastBuffer() {
        bufferManager.createNewBuffer("file1.txt");
        
        assertTrue(bufferManager.closeCurrentBuffer());
        assertEquals(0, bufferManager.getBufferCount());
        assertNull(bufferManager.getCurrentBuffer());
        assertEquals(-1, bufferManager.getCurrentBufferIndex());
    }
    
    @Test
    @DisplayName("Should not close buffer when none exist")
    void shouldNotCloseBufferWhenNoneExist() {
        assertFalse(bufferManager.closeCurrentBuffer());
    }
    
    @Test
    @DisplayName("Should get all buffers")
    void shouldGetAllBuffers() {
        Buffer buffer1 = bufferManager.createNewBuffer("file1.txt");
        Buffer buffer2 = bufferManager.createNewBuffer("file2.txt");
        
        var allBuffers = bufferManager.getAllBuffers();
        assertEquals(2, allBuffers.size());
        assertTrue(allBuffers.contains(buffer1));
        assertTrue(allBuffers.contains(buffer2));
    }
    
    @Test
    @DisplayName("Should detect modified buffers")
    void shouldDetectModifiedBuffers() {
        Buffer buffer1 = bufferManager.createNewBuffer("file1.txt");
        Buffer buffer2 = bufferManager.createNewBuffer("file2.txt");
        
        assertFalse(bufferManager.hasModifiedBuffers());
        
        buffer1.setLine(0, "Modified content");
        assertTrue(bufferManager.hasModifiedBuffers());
        
        buffer1.setModified(false);
        assertFalse(bufferManager.hasModifiedBuffers());
        
        buffer2.setLine(0, "Another modification");
        assertTrue(bufferManager.hasModifiedBuffers());
    }
    
    @Test
    @DisplayName("Should handle buffer switching after closing middle buffer")
    void shouldHandleBufferSwitchingAfterClosingMiddleBuffer() {
        Buffer buffer1 = bufferManager.createNewBuffer("file1.txt");
        Buffer buffer2 = bufferManager.createNewBuffer("file2.txt");
        Buffer buffer3 = bufferManager.createNewBuffer("file3.txt");
        
        bufferManager.switchToBuffer(1); // Select middle buffer
        bufferManager.closeCurrentBuffer(); // Close buffer2
        
        assertEquals(2, bufferManager.getBufferCount());
        assertEquals(1, bufferManager.getCurrentBufferIndex());
        assertEquals(buffer3, bufferManager.getCurrentBuffer());
    }
}