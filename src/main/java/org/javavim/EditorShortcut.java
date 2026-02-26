package org.javavim;

import java.awt.event.KeyEvent;

/**
 * Centralizes CTRL-based editor shortcuts.
 */
public enum EditorShortcut {
    TOGGLE_NERDTREE(KeyEvent.VK_N),
    TOGGLE_TERMINAL(KeyEvent.VK_QUOTE),
    FOCUS_EDITOR(KeyEvent.VK_1),
    RUN_JAVA_FOLDER(KeyEvent.VK_E);

    private final int keyCode;

    EditorShortcut(int keyCode) {
        this.keyCode = keyCode;
    }

    /**
     * Returns the Swing key code mapped to this shortcut.
     */
    public int keyCode() {
        return keyCode;
    }

    /**
     * Checks if the shortcut matches a CTRL + key combination.
     */
    public boolean matches(boolean ctrlDown, int pressedKeyCode) {
        return ctrlDown && keyCode == pressedKeyCode;
    }
}
