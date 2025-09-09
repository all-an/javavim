package com.javavim.vim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VimModeManager class.
 * Tests each method individually following clean code principles.
 */
class VimModeManagerTest {

    private VimModeManager modeManager;

    @BeforeEach
    void setUp() {
        modeManager = new VimModeManager();
    }

    @Test
    void constructor_ShouldInitializeWithNormalMode() {
        assertEquals(VimMode.NORMAL, modeManager.getCurrentMode());
    }

    @Test
    void getCurrentMode_ShouldReturnCurrentMode() {
        VimMode mode = modeManager.getCurrentMode();
        
        assertEquals(VimMode.NORMAL, mode);
    }

    @Test
    void setMode_WithValidMode_ShouldUpdateCurrentMode() {
        modeManager.setMode(VimMode.INSERT);
        
        assertEquals(VimMode.INSERT, modeManager.getCurrentMode());
    }

    @Test
    void setMode_WithNullMode_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            modeManager.setMode(null);
        });
    }

    @Test
    void isInMode_WithCurrentMode_ShouldReturnTrue() {
        modeManager.setMode(VimMode.VISUAL);
        
        assertTrue(modeManager.isInMode(VimMode.VISUAL));
    }

    @Test
    void isInMode_WithDifferentMode_ShouldReturnFalse() {
        modeManager.setMode(VimMode.NORMAL);
        
        assertFalse(modeManager.isInMode(VimMode.INSERT));
    }

    @Test
    void enterNormalMode_ShouldSetModeToNormal() {
        modeManager.setMode(VimMode.INSERT);
        
        modeManager.enterNormalMode();
        
        assertEquals(VimMode.NORMAL, modeManager.getCurrentMode());
    }

    @Test
    void enterInsertMode_ShouldSetModeToInsert() {
        modeManager.enterInsertMode();
        
        assertEquals(VimMode.INSERT, modeManager.getCurrentMode());
    }

    @Test
    void enterVisualMode_ShouldSetModeToVisual() {
        modeManager.enterVisualMode();
        
        assertEquals(VimMode.VISUAL, modeManager.getCurrentMode());
    }

    @Test
    void enterCommandMode_ShouldSetModeToCommand() {
        modeManager.enterCommandMode();
        
        assertEquals(VimMode.COMMAND, modeManager.getCurrentMode());
    }

    @Test
    void canTransitionTo_WithNullTarget_ShouldReturnFalse() {
        boolean canTransition = modeManager.canTransitionTo(null);
        
        assertFalse(canTransition);
    }

    @Test
    void canTransitionTo_FromNormalToAnyMode_ShouldReturnTrue() {
        modeManager.setMode(VimMode.NORMAL);
        
        assertTrue(modeManager.canTransitionTo(VimMode.INSERT));
        assertTrue(modeManager.canTransitionTo(VimMode.VISUAL));
        assertTrue(modeManager.canTransitionTo(VimMode.COMMAND));
        assertTrue(modeManager.canTransitionTo(VimMode.NORMAL));
    }

    @Test
    void canTransitionTo_FromInsertToNormal_ShouldReturnTrue() {
        modeManager.setMode(VimMode.INSERT);
        
        assertTrue(modeManager.canTransitionTo(VimMode.NORMAL));
    }

    @Test
    void canTransitionTo_FromInsertToNonNormal_ShouldReturnFalse() {
        modeManager.setMode(VimMode.INSERT);
        
        assertFalse(modeManager.canTransitionTo(VimMode.VISUAL));
        assertFalse(modeManager.canTransitionTo(VimMode.COMMAND));
        assertFalse(modeManager.canTransitionTo(VimMode.INSERT));
    }

    @Test
    void canTransitionTo_FromVisualToNormal_ShouldReturnTrue() {
        modeManager.setMode(VimMode.VISUAL);
        
        assertTrue(modeManager.canTransitionTo(VimMode.NORMAL));
    }

    @Test
    void canTransitionTo_FromVisualToNonNormal_ShouldReturnFalse() {
        modeManager.setMode(VimMode.VISUAL);
        
        assertFalse(modeManager.canTransitionTo(VimMode.INSERT));
        assertFalse(modeManager.canTransitionTo(VimMode.COMMAND));
        assertFalse(modeManager.canTransitionTo(VimMode.VISUAL));
    }

    @Test
    void canTransitionTo_FromCommandToNormal_ShouldReturnTrue() {
        modeManager.setMode(VimMode.COMMAND);
        
        assertTrue(modeManager.canTransitionTo(VimMode.NORMAL));
    }

    @Test
    void canTransitionTo_FromCommandToNonNormal_ShouldReturnFalse() {
        modeManager.setMode(VimMode.COMMAND);
        
        assertFalse(modeManager.canTransitionTo(VimMode.INSERT));
        assertFalse(modeManager.canTransitionTo(VimMode.VISUAL));
        assertFalse(modeManager.canTransitionTo(VimMode.COMMAND));
    }
}