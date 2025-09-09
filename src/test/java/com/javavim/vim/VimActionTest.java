package com.javavim.vim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VimAction enum.
 * Tests each enum value and basic functionality following clean code principles.
 */
class VimActionTest {

    @Test
    void enumValues_ShouldContainAllExpectedActions() {
        VimAction[] actions = VimAction.values();
        
        assertTrue(actions.length >= 13);
        
        // Movement actions
        assertNotNull(VimAction.valueOf("MOVE_LEFT"));
        assertNotNull(VimAction.valueOf("MOVE_RIGHT"));
        assertNotNull(VimAction.valueOf("MOVE_UP"));
        assertNotNull(VimAction.valueOf("MOVE_DOWN"));
        
        // Selection actions
        assertNotNull(VimAction.valueOf("EXTEND_SELECTION_LEFT"));
        assertNotNull(VimAction.valueOf("EXTEND_SELECTION_RIGHT"));
        assertNotNull(VimAction.valueOf("EXTEND_SELECTION_UP"));
        assertNotNull(VimAction.valueOf("EXTEND_SELECTION_DOWN"));
        
        // Command actions
        assertNotNull(VimAction.valueOf("EXECUTE_COMMAND"));
        
        // Edit actions
        assertNotNull(VimAction.valueOf("DELETE_CHAR"));
        assertNotNull(VimAction.valueOf("DELETE_LINE"));
        assertNotNull(VimAction.valueOf("COPY_LINE"));
        assertNotNull(VimAction.valueOf("PASTE"));
        assertNotNull(VimAction.valueOf("UNDO"));
        assertNotNull(VimAction.valueOf("REDO"));
    }

    @Test
    void valueOf_WithValidActionName_ShouldReturnCorrectAction() {
        assertEquals(VimAction.MOVE_LEFT, VimAction.valueOf("MOVE_LEFT"));
        assertEquals(VimAction.EXECUTE_COMMAND, VimAction.valueOf("EXECUTE_COMMAND"));
        assertEquals(VimAction.UNDO, VimAction.valueOf("UNDO"));
    }

    @Test
    void valueOf_WithInvalidActionName_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            VimAction.valueOf("INVALID_ACTION");
        });
    }

    @Test
    void name_ShouldReturnCorrectName() {
        assertEquals("MOVE_LEFT", VimAction.MOVE_LEFT.name());
        assertEquals("EXECUTE_COMMAND", VimAction.EXECUTE_COMMAND.name());
        assertEquals("UNDO", VimAction.UNDO.name());
    }

    @Test
    void ordinal_ShouldReturnCorrectOrder() {
        VimAction[] actions = VimAction.values();
        
        for (int i = 0; i < actions.length; i++) {
            assertEquals(i, actions[i].ordinal());
        }
    }

    @Test
    void toString_ShouldReturnActionName() {
        assertEquals("MOVE_LEFT", VimAction.MOVE_LEFT.toString());
        assertEquals("EXTEND_SELECTION_UP", VimAction.EXTEND_SELECTION_UP.toString());
        assertEquals("DELETE_CHAR", VimAction.DELETE_CHAR.toString());
    }
}