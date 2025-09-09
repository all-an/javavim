package com.javavim.vim;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for VimKeyHandler trigger mechanism.
 * Tests the insert mode trigger functionality specifically.
 */
class VimKeyHandlerTriggerTest {

    private VimKeyHandler keyHandler;
    private VimModeManager modeManager;

    @BeforeEach
    void setUp() {
        modeManager = new VimModeManager();
        keyHandler = new VimKeyHandler(modeManager);
    }

    @Test
    void enterInsertMode_ShouldSetTrigger() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.I);
        
        keyHandler.handleKeyEvent(keyEvent);
        
        assertEquals(VimMode.INSERT, modeManager.getCurrentMode());
        assertEquals("i", keyHandler.getLastModeChangeTrigger());
    }

    @Test
    void enterVisualMode_ShouldNotSetTrigger() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.V);
        
        keyHandler.handleKeyEvent(keyEvent);
        
        assertEquals(VimMode.VISUAL, modeManager.getCurrentMode());
        assertNull(keyHandler.getLastModeChangeTrigger());
    }

    @Test
    void clearTrigger_ShouldResetToNull() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.I);
        keyHandler.handleKeyEvent(keyEvent);
        
        keyHandler.clearModeChangeTrigger();
        
        assertNull(keyHandler.getLastModeChangeTrigger());
    }

    @Test
    void multipleKeyPresses_ShouldOnlySetTriggerForModeChange() {
        modeManager.setMode(VimMode.NORMAL);
        
        // Press 'i' to enter insert mode
        KeyEvent insertKey = createKeyEvent(KeyCode.I);
        keyHandler.handleKeyEvent(insertKey);
        assertEquals("i", keyHandler.getLastModeChangeTrigger());
        
        // Press escape to return to normal mode (should not change trigger)
        modeManager.setMode(VimMode.INSERT);
        KeyEvent escapeKey = createKeyEvent(KeyCode.ESCAPE);
        keyHandler.handleKeyEvent(escapeKey);
        assertEquals("i", keyHandler.getLastModeChangeTrigger()); // Should still be "i"
    }

    private KeyEvent createKeyEvent(KeyCode keyCode) {
        return new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            keyCode,
            false,
            false,
            false,
            false
        );
    }
}