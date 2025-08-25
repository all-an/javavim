package com.javavim.display;

import com.javavim.buffer.Buffer;
import com.javavim.buffer.ScreenBuffer;
import com.javavim.terminal.Cursor;

/**
 * Handles rendering text buffer content to the screen.
 * Follows single responsibility principle - manages display rendering only.
 */
public class DisplayRenderer {
    
    private boolean showLineNumbers;
    private int lineNumberWidth;
    private int scrollOffsetY;
    private int scrollOffsetX;
    
    public DisplayRenderer() {
        this.showLineNumbers = true;
        this.lineNumberWidth = 4;
        this.scrollOffsetY = 0;
        this.scrollOffsetX = 0;
    }
    
    public void render(Buffer buffer, ScreenBuffer screenBuffer, Cursor cursor) {
        if (buffer == null || screenBuffer == null) {
            return;
        }
        
        screenBuffer.clear();
        
        int contentWidth = calculateContentWidth(screenBuffer);
        int visibleLines = calculateVisibleLines(screenBuffer);
        
        renderLines(buffer, screenBuffer, contentWidth, visibleLines);
    }
    
    public void setShowLineNumbers(boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers;
        updateLineNumberWidth();
    }
    
    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }
    
    public void scrollUp() {
        if (scrollOffsetY > 0) {
            scrollOffsetY--;
        }
    }
    
    public void scrollDown(Buffer buffer) {
        if (buffer != null && canScrollDown(buffer)) {
            scrollOffsetY++;
        }
    }
    
    public void scrollLeft() {
        if (scrollOffsetX > 0) {
            scrollOffsetX--;
        }
    }
    
    public void scrollRight() {
        scrollOffsetX++;
    }
    
    public int getScrollOffsetY() {
        return scrollOffsetY;
    }
    
    public int getScrollOffsetX() {
        return scrollOffsetX;
    }
    
    public void setScrollOffset(int offsetY, int offsetX) {
        this.scrollOffsetY = Math.max(0, offsetY);
        this.scrollOffsetX = Math.max(0, offsetX);
    }
    
    private int calculateContentWidth(ScreenBuffer screenBuffer) {
        return showLineNumbers ? 
            screenBuffer.getWidth() - lineNumberWidth - 1 : // -1 for separator
            screenBuffer.getWidth();
    }
    
    private int calculateVisibleLines(ScreenBuffer screenBuffer) {
        return screenBuffer.getHeight() - 1; // -1 for status line
    }
    
    private void renderLines(Buffer buffer, ScreenBuffer screenBuffer, int contentWidth, int visibleLines) {
        for (int screenLine = 0; screenLine < visibleLines; screenLine++) {
            int bufferLine = screenLine + scrollOffsetY;
            
            if (isValidBufferLine(buffer, bufferLine)) {
                renderLine(buffer, screenBuffer, screenLine, bufferLine, contentWidth);
            } else {
                renderEmptyLine(screenBuffer, screenLine);
            }
        }
    }
    
    private void renderLine(Buffer buffer, ScreenBuffer screenBuffer, int screenLine, int bufferLine, int contentWidth) {
        int xPosition = 0;
        
        if (showLineNumbers) {
            xPosition = renderLineNumber(screenBuffer, screenLine, bufferLine + 1);
        }
        
        String line = buffer.getLine(bufferLine);
        renderLineContent(screenBuffer, screenLine, xPosition, line, contentWidth);
    }
    
    private int renderLineNumber(ScreenBuffer screenBuffer, int screenLine, int lineNumber) {
        String lineNumStr = formatLineNumber(lineNumber);
        screenBuffer.setString(0, screenLine, lineNumStr);
        screenBuffer.setChar(lineNumberWidth, screenLine, '│');
        return lineNumberWidth + 1;
    }
    
    private void renderLineContent(ScreenBuffer screenBuffer, int screenLine, int startX, String line, int contentWidth) {
        if (line.isEmpty()) {
            return;
        }
        
        String visiblePart = getVisiblePart(line, contentWidth);
        screenBuffer.setString(startX, screenLine, visiblePart);
    }
    
    private void renderEmptyLine(ScreenBuffer screenBuffer, int screenLine) {
        if (showLineNumbers) {
            String emptyLineNum = formatEmptyLineNumber();
            screenBuffer.setString(0, screenLine, emptyLineNum);
            screenBuffer.setChar(lineNumberWidth, screenLine, '│');
        }
    }
    
    private String formatLineNumber(int lineNumber) {
        return String.format("%" + lineNumberWidth + "d", lineNumber);
    }
    
    private String formatEmptyLineNumber() {
        return String.format("%" + lineNumberWidth + "s", "~");
    }
    
    private String getVisiblePart(String line, int contentWidth) {
        if (scrollOffsetX >= line.length()) {
            return "";
        }
        
        int endIndex = Math.min(line.length(), scrollOffsetX + contentWidth);
        return line.substring(scrollOffsetX, endIndex);
    }
    
    private boolean isValidBufferLine(Buffer buffer, int lineNumber) {
        return lineNumber >= 0 && lineNumber < buffer.getLineCount();
    }
    
    private boolean canScrollDown(Buffer buffer) {
        return scrollOffsetY < buffer.getLineCount() - 1;
    }
    
    
    private void updateLineNumberWidth() {
        this.lineNumberWidth = showLineNumbers ? 4 : 0;
    }
}