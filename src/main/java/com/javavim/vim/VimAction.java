package com.javavim.vim;

/**
 * Represents vim actions that can be performed.
 * Follows single responsibility principle - defines vim actions only.
 */
public enum VimAction {
    // Movement actions
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_DOWN,
    
    // Selection actions
    EXTEND_SELECTION_LEFT,
    EXTEND_SELECTION_RIGHT,
    EXTEND_SELECTION_UP,
    EXTEND_SELECTION_DOWN,
    
    // Command actions
    EXECUTE_COMMAND,
    
    // Edit actions
    DELETE_CHAR,
    DELETE_LINE,
    COPY_LINE,
    PASTE,
    UNDO,
    REDO
}