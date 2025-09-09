package com.javavim.vim;

import javafx.scene.input.KeyEvent;

/**
 * Handles vim key bindings based on current mode.
 * Follows single responsibility principle - processes vim keys only.
 */
public class VimKeyHandler {
    
    private final VimModeManager modeManager;
    private VimActionListener actionListener;
    private String lastModeChangeTrigger;
    
    public VimKeyHandler(VimModeManager modeManager) {
        this.modeManager = modeManager;
    }
    
    public void setActionListener(VimActionListener listener) {
        this.actionListener = listener;
    }
    
    public String getLastModeChangeTrigger() {
        return lastModeChangeTrigger;
    }
    
    public void clearModeChangeTrigger() {
        lastModeChangeTrigger = null;
    }
    
    public boolean handleKeyEvent(KeyEvent event) {
        if (event == null) {
            return false;
        }
        
        return switch (modeManager.getCurrentMode()) {
            case NORMAL -> handleNormalModeKey(event);
            case INSERT -> handleInsertModeKey(event);
            case VISUAL -> handleVisualModeKey(event);
            case COMMAND -> handleCommandModeKey(event);
        };
    }
    
    private boolean handleNormalModeKey(KeyEvent event) {
        return switch (event.getCode()) {
            case I -> {
                lastModeChangeTrigger = "i";
                modeManager.enterInsertMode();
                notifyModeChange();
                yield true;
            }
            case H -> {
                notifyAction(VimAction.MOVE_LEFT);
                yield true;
            }
            case J -> {
                notifyAction(VimAction.MOVE_DOWN);
                yield true;
            }
            case K -> {
                notifyAction(VimAction.MOVE_UP);
                yield true;
            }
            case L -> {
                notifyAction(VimAction.MOVE_RIGHT);
                yield true;
            }
            case SEMICOLON -> {
                if (event.isShiftDown()) { // ':'
                    modeManager.enterCommandMode();
                    notifyModeChange();
                    yield true;
                }
                yield false;
            }
            case V -> {
                lastModeChangeTrigger = "v";
                modeManager.enterVisualMode();
                notifyModeChange();
                yield true;
            }
            default -> false;
        };
    }
    
    private boolean handleInsertModeKey(KeyEvent event) {
        return switch (event.getCode()) {
            case ESCAPE -> {
                lastModeChangeTrigger = null; // Clear trigger when exiting insert mode
                modeManager.enterNormalMode();
                notifyModeChange();
                yield true;
            }
            default -> false; // Let normal text input pass through
        };
    }
    
    private boolean handleVisualModeKey(KeyEvent event) {
        return switch (event.getCode()) {
            case ESCAPE -> {
                modeManager.enterNormalMode();
                notifyModeChange();
                yield true;
            }
            case H -> {
                notifyAction(VimAction.EXTEND_SELECTION_LEFT);
                yield true;
            }
            case J -> {
                notifyAction(VimAction.EXTEND_SELECTION_DOWN);
                yield true;
            }
            case K -> {
                notifyAction(VimAction.EXTEND_SELECTION_UP);
                yield true;
            }
            case L -> {
                notifyAction(VimAction.EXTEND_SELECTION_RIGHT);
                yield true;
            }
            default -> false;
        };
    }
    
    private boolean handleCommandModeKey(KeyEvent event) {
        return switch (event.getCode()) {
            case ESCAPE -> {
                modeManager.enterNormalMode();
                notifyModeChange();
                yield true;
            }
            case ENTER -> {
                notifyAction(VimAction.EXECUTE_COMMAND);
                modeManager.enterNormalMode();
                notifyModeChange();
                yield true;
            }
            default -> false; // Let command input pass through
        };
    }
    
    private void notifyModeChange() {
        if (actionListener != null) {
            actionListener.onModeChanged(modeManager.getCurrentMode());
        }
    }
    
    private void notifyAction(VimAction action) {
        if (actionListener != null) {
            actionListener.onVimAction(action);
        }
    }
}