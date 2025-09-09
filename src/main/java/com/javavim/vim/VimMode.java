package com.javavim.vim;

/**
 * Represents the different modes in vim.
 * Follows single responsibility principle - defines vim modes only.
 */
public enum VimMode {
    NORMAL,
    INSERT, 
    VISUAL,
    COMMAND
}