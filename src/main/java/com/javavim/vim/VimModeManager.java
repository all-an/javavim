package com.javavim.vim;

/**
 * Manages vim mode transitions.
 * Follows single responsibility principle - handles mode state only.
 */
public class VimModeManager {
    
    private VimMode currentMode;
    
    public VimModeManager() {
        this.currentMode = VimMode.NORMAL;
    }
    
    public VimMode getCurrentMode() {
        return currentMode;
    }
    
    public void setMode(VimMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }
        this.currentMode = mode;
    }
    
    public boolean isInMode(VimMode mode) {
        return currentMode == mode;
    }
    
    public void enterNormalMode() {
        setMode(VimMode.NORMAL);
    }
    
    public void enterInsertMode() {
        setMode(VimMode.INSERT);
    }
    
    public void enterVisualMode() {
        setMode(VimMode.VISUAL);
    }
    
    public void enterCommandMode() {
        setMode(VimMode.COMMAND);
    }
    
    public boolean canTransitionTo(VimMode targetMode) {
        if (targetMode == null) {
            return false;
        }
        
        return switch (currentMode) {
            case NORMAL -> true; // Can transition from normal to any mode
            case INSERT -> targetMode == VimMode.NORMAL;
            case VISUAL -> targetMode == VimMode.NORMAL;
            case COMMAND -> targetMode == VimMode.NORMAL;
        };
    }
}