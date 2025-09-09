package com.javavim.vim;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VimKeyHandler class.
 * Tests each method individually following clean code principles.
 */
class VimKeyHandlerTest {

    private VimKeyHandler keyHandler;
    private VimModeManager modeManager;
    
    @Mock
    private VimActionListener actionListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modeManager = new VimModeManager();
        keyHandler = new VimKeyHandler(modeManager);
        keyHandler.setActionListener(actionListener);
    }

    @Test
    void constructor_ShouldInitializeWithModeManager() {
        VimModeManager testManager = new VimModeManager();
        VimKeyHandler handler = new VimKeyHandler(testManager);
        
        assertNotNull(handler);
    }

    @Test
    void setActionListener_ShouldSetListener() {
        VimActionListener listener = mock(VimActionListener.class);
        
        keyHandler.setActionListener(listener);
        
        // Test by triggering an event and verifying the listener is called
        KeyEvent keyEvent = createKeyEvent(KeyCode.I);
        keyHandler.handleKeyEvent(keyEvent);
        
        verify(listener).onModeChanged(VimMode.INSERT);
    }

    @Test
    void handleKeyEvent_WithNullEvent_ShouldReturnFalse() {
        boolean handled = keyHandler.handleKeyEvent(null);
        
        assertFalse(handled);
    }

    @Test
    void handleKeyEvent_InNormalModeWithI_ShouldEnterInsertMode() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.I);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        assertEquals(VimMode.INSERT, modeManager.getCurrentMode());
        verify(actionListener).onModeChanged(VimMode.INSERT);
    }

    @Test
    void handleKeyEvent_InNormalModeWithH_ShouldTriggerMoveLeft() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.H);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        verify(actionListener).onVimAction(VimAction.MOVE_LEFT);
    }

    @Test
    void handleKeyEvent_InNormalModeWithJ_ShouldTriggerMoveDown() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.J);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        verify(actionListener).onVimAction(VimAction.MOVE_DOWN);
    }

    @Test
    void handleKeyEvent_InNormalModeWithK_ShouldTriggerMoveUp() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.K);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        verify(actionListener).onVimAction(VimAction.MOVE_UP);
    }

    @Test
    void handleKeyEvent_InNormalModeWithL_ShouldTriggerMoveRight() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.L);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        verify(actionListener).onVimAction(VimAction.MOVE_RIGHT);
    }

    @Test
    void handleKeyEvent_InNormalModeWithV_ShouldEnterVisualMode() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.V);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        assertEquals(VimMode.VISUAL, modeManager.getCurrentMode());
        verify(actionListener).onModeChanged(VimMode.VISUAL);
    }

    @Test
    void handleKeyEvent_InNormalModeWithColonShift_ShouldEnterCommandMode() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEventWithShift(KeyCode.SEMICOLON);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        assertEquals(VimMode.COMMAND, modeManager.getCurrentMode());
        verify(actionListener).onModeChanged(VimMode.COMMAND);
    }

    @Test
    void handleKeyEvent_InNormalModeWithColonNoShift_ShouldReturnFalse() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.SEMICOLON);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertFalse(handled);
        assertEquals(VimMode.NORMAL, modeManager.getCurrentMode());
    }

    @Test
    void handleKeyEvent_InNormalModeWithUnknownKey_ShouldReturnFalse() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.A);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertFalse(handled);
        assertEquals(VimMode.NORMAL, modeManager.getCurrentMode());
    }

    @Test
    void handleKeyEvent_InInsertModeWithEscape_ShouldReturnToNormalMode() {
        modeManager.setMode(VimMode.INSERT);
        KeyEvent keyEvent = createKeyEvent(KeyCode.ESCAPE);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        assertEquals(VimMode.NORMAL, modeManager.getCurrentMode());
        verify(actionListener).onModeChanged(VimMode.NORMAL);
    }

    @Test
    void handleKeyEvent_InInsertModeWithRegularKey_ShouldReturnFalse() {
        modeManager.setMode(VimMode.INSERT);
        KeyEvent keyEvent = createKeyEvent(KeyCode.A);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertFalse(handled);
        assertEquals(VimMode.INSERT, modeManager.getCurrentMode());
    }

    @Test
    void handleKeyEvent_InVisualModeWithEscape_ShouldReturnToNormalMode() {
        modeManager.setMode(VimMode.VISUAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.ESCAPE);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        assertEquals(VimMode.NORMAL, modeManager.getCurrentMode());
        verify(actionListener).onModeChanged(VimMode.NORMAL);
    }

    @Test
    void handleKeyEvent_InVisualModeWithH_ShouldExtendSelectionLeft() {
        modeManager.setMode(VimMode.VISUAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.H);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        verify(actionListener).onVimAction(VimAction.EXTEND_SELECTION_LEFT);
    }

    @Test
    void handleKeyEvent_InVisualModeWithJ_ShouldExtendSelectionDown() {
        modeManager.setMode(VimMode.VISUAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.J);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        verify(actionListener).onVimAction(VimAction.EXTEND_SELECTION_DOWN);
    }

    @Test
    void handleKeyEvent_InVisualModeWithK_ShouldExtendSelectionUp() {
        modeManager.setMode(VimMode.VISUAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.K);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        verify(actionListener).onVimAction(VimAction.EXTEND_SELECTION_UP);
    }

    @Test
    void handleKeyEvent_InVisualModeWithL_ShouldExtendSelectionRight() {
        modeManager.setMode(VimMode.VISUAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.L);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        verify(actionListener).onVimAction(VimAction.EXTEND_SELECTION_RIGHT);
    }

    @Test
    void handleKeyEvent_InCommandModeWithEscape_ShouldReturnToNormalMode() {
        modeManager.setMode(VimMode.COMMAND);
        KeyEvent keyEvent = createKeyEvent(KeyCode.ESCAPE);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        assertEquals(VimMode.NORMAL, modeManager.getCurrentMode());
        verify(actionListener).onModeChanged(VimMode.NORMAL);
    }

    @Test
    void handleKeyEvent_InCommandModeWithEnter_ShouldExecuteCommandAndReturnToNormal() {
        modeManager.setMode(VimMode.COMMAND);
        KeyEvent keyEvent = createKeyEvent(KeyCode.ENTER);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertTrue(handled);
        assertEquals(VimMode.NORMAL, modeManager.getCurrentMode());
        verify(actionListener).onVimAction(VimAction.EXECUTE_COMMAND);
        verify(actionListener).onModeChanged(VimMode.NORMAL);
    }

    @Test
    void handleKeyEvent_InCommandModeWithRegularKey_ShouldReturnFalse() {
        modeManager.setMode(VimMode.COMMAND);
        KeyEvent keyEvent = createKeyEvent(KeyCode.A);
        
        boolean handled = keyHandler.handleKeyEvent(keyEvent);
        
        assertFalse(handled);
        assertEquals(VimMode.COMMAND, modeManager.getCurrentMode());
    }

    @Test
    void handleKeyEvent_WithNullActionListener_ShouldNotThrowException() {
        keyHandler.setActionListener(null);
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.I);
        
        assertDoesNotThrow(() -> {
            boolean handled = keyHandler.handleKeyEvent(keyEvent);
            assertTrue(handled);
        });
    }

    @Test
    void getLastModeChangeTrigger_AfterEnteringInsertMode_ShouldReturnI() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.I);
        
        keyHandler.handleKeyEvent(keyEvent);
        
        assertEquals("i", keyHandler.getLastModeChangeTrigger());
    }

    @Test
    void getLastModeChangeTrigger_InitialState_ShouldReturnNull() {
        assertNull(keyHandler.getLastModeChangeTrigger());
    }

    @Test
    void clearModeChangeTrigger_ShouldSetTriggerToNull() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent keyEvent = createKeyEvent(KeyCode.I);
        keyHandler.handleKeyEvent(keyEvent);
        
        keyHandler.clearModeChangeTrigger();
        
        assertNull(keyHandler.getLastModeChangeTrigger());
    }

    @Test
    void getLastModeChangeTrigger_AfterNonModeChangingKey_ShouldRetainPreviousValue() {
        modeManager.setMode(VimMode.NORMAL);
        KeyEvent insertKey = createKeyEvent(KeyCode.I);
        keyHandler.handleKeyEvent(insertKey);
        
        KeyEvent moveKey = createKeyEvent(KeyCode.H);
        keyHandler.handleKeyEvent(moveKey);
        
        assertEquals("i", keyHandler.getLastModeChangeTrigger());
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

    private KeyEvent createKeyEventWithShift(KeyCode keyCode) {
        return new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            keyCode,
            true,  // shift down
            false,
            false,
            false
        );
    }
}