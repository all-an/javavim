package org.javavim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

class EditorShortcutTest {

    @Test
    @DisplayName("Shortcut constants map to expected Swing key constants")
    void shortcutConstantsMatchSwingKeyConstants() {
        assertEquals(KeyEvent.VK_N, EditorShortcut.TOGGLE_NERDTREE.keyCode());
        assertEquals(KeyEvent.VK_QUOTE, EditorShortcut.TOGGLE_TERMINAL.keyCode());
        assertEquals(KeyEvent.VK_1, EditorShortcut.FOCUS_EDITOR.keyCode());
        assertEquals(KeyEvent.VK_E, EditorShortcut.RUN_JAVA_FOLDER.keyCode());
    }

    @Test
    @DisplayName("All shortcuts match only when CTRL is pressed and key matches")
    void shortcutMatchingBehavior() {
        for (EditorShortcut shortcut : EditorShortcut.values()) {
            assertTrue(shortcut.matches(true, shortcut.keyCode()));
            assertFalse(shortcut.matches(false, shortcut.keyCode()));
            assertFalse(shortcut.matches(true, KeyEvent.VK_Z));
        }
    }
}
