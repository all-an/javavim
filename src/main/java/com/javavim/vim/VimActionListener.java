package com.javavim.vim;

/**
 * Interface for listening to vim actions and mode changes.
 * Follows single responsibility principle - defines vim event callbacks only.
 */
public interface VimActionListener {
    
    void onModeChanged(VimMode newMode);
    
    void onVimAction(VimAction action);
}