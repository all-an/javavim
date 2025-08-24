package com.javavim.display;

import com.javavim.buffer.Buffer;
import com.javavim.buffer.ScreenBuffer;
import com.javavim.terminal.Cursor;

/**
 * Manages the status line display at the bottom of the editor.
 * Follows single responsibility principle - manages status line only.
 */
public class StatusLine {
    
    private String mode;
    private String message;
    private boolean showPosition;
    private boolean showFileInfo;
    
    public StatusLine() {
        this.mode = "NORMAL";
        this.message = "";
        this.showPosition = true;
        this.showFileInfo = true;
    }
    
    public void render(ScreenBuffer screenBuffer, Buffer buffer, Cursor cursor) {
        if (screenBuffer == null) {
            return;
        }
        
        int statusLineY = screenBuffer.getHeight() - 1;
        String statusText = buildStatusText(buffer, cursor, screenBuffer.getWidth());
        
        screenBuffer.setString(0, statusLineY, statusText);
    }
    
    public void setMode(String mode) {
        if (mode != null) {
            this.mode = mode;
        }
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMessage(String message) {
        this.message = message != null ? message : "";
    }
    
    public String getMessage() {
        return message;
    }
    
    public void clearMessage() {
        this.message = "";
    }
    
    public void setShowPosition(boolean showPosition) {
        this.showPosition = showPosition;
    }
    
    public boolean isShowPosition() {
        return showPosition;
    }
    
    public void setShowFileInfo(boolean showFileInfo) {
        this.showFileInfo = showFileInfo;
    }
    
    public boolean isShowFileInfo() {
        return showFileInfo;
    }
    
    private String buildStatusText(Buffer buffer, Cursor cursor, int screenWidth) {
        StringBuilder status = new StringBuilder();
        
        addModeInfo(status);
        addFileInfo(status, buffer);
        addMessage(status);
        
        String leftPart = status.toString();
        String rightPart = buildRightPart(buffer, cursor);
        
        return formatStatusLine(leftPart, rightPart, screenWidth);
    }
    
    private void addModeInfo(StringBuilder status) {
        status.append(" ").append(mode).append(" ");
    }
    
    private void addFileInfo(StringBuilder status, Buffer buffer) {
        if (!showFileInfo || buffer == null) {
            return;
        }
        
        String filename = getDisplayFilename(buffer);
        status.append(filename);
        
        if (buffer.isModified()) {
            status.append(" [+]");
        }
        
        status.append(" ");
    }
    
    private void addMessage(StringBuilder status) {
        if (!message.isEmpty()) {
            status.append("| ").append(message).append(" ");
        }
    }
    
    private String buildRightPart(Buffer buffer, Cursor cursor) {
        StringBuilder rightPart = new StringBuilder();
        
        if (showPosition && cursor != null) {
            addPositionInfo(rightPart, cursor);
        }
        
        if (buffer != null) {
            addBufferInfo(rightPart, buffer);
        }
        
        return rightPart.toString().trim();
    }
    
    private void addPositionInfo(StringBuilder rightPart, Cursor cursor) {
        rightPart.append(String.format("Ln %d, Col %d ", 
            cursor.getY() + 1, cursor.getX() + 1));
    }
    
    private void addBufferInfo(StringBuilder rightPart, Buffer buffer) {
        int lineCount = buffer.getLineCount();
        rightPart.append(String.format("(%d lines) ", lineCount));
    }
    
    private String getDisplayFilename(Buffer buffer) {
        String filename = buffer.getFilename();
        if (filename == null) {
            return "[No Name]";
        }
        
        return truncateFilename(filename);
    }
    
    private String truncateFilename(String filename) {
        int maxLength = 30;
        if (filename.length() <= maxLength) {
            return filename;
        }
        
        return "..." + filename.substring(filename.length() - maxLength + 3);
    }
    
    private String formatStatusLine(String leftPart, String rightPart, int screenWidth) {
        if (leftPart.length() + rightPart.length() >= screenWidth) {
            return truncateToFit(leftPart, rightPart, screenWidth);
        }
        
        int padding = screenWidth - leftPart.length() - rightPart.length();
        return leftPart + " ".repeat(Math.max(0, padding)) + rightPart;
    }
    
    private String truncateToFit(String leftPart, String rightPart, int screenWidth) {
        int totalLength = leftPart.length() + rightPart.length();
        int excess = totalLength - screenWidth;
        
        if (leftPart.length() > excess) {
            return leftPart.substring(0, leftPart.length() - excess) + rightPart;
        } else {
            return leftPart.substring(0, Math.max(0, screenWidth - rightPart.length())) + rightPart;
        }
    }
}