package com.javavim.terminal;

/**
 * Manages cursor position and movement within the terminal.
 * Follows single responsibility principle - handles cursor operations only.
 */
public class Cursor {
    
    private int x;
    private int y;
    private final int maxX;
    private final int maxY;
    private boolean visible;
    
    public Cursor(int maxX, int maxY) {
        if (maxX <= 0 || maxY <= 0) {
            throw new IllegalArgumentException("Maximum coordinates must be positive");
        }
        
        this.maxX = maxX;
        this.maxY = maxY;
        this.x = 0;
        this.y = 0;
        this.visible = true;
    }
    
    public void moveTo(int newX, int newY) {
        if (isValidPosition(newX, newY)) {
            this.x = newX;
            this.y = newY;
        }
    }
    
    public void moveLeft() {
        if (canMoveLeft()) {
            this.x--;
        }
    }
    
    public void moveRight() {
        if (canMoveRight()) {
            this.x++;
        }
    }
    
    public void moveUp() {
        if (canMoveUp()) {
            this.y--;
        }
    }
    
    public void moveDown() {
        if (canMoveDown()) {
            this.y++;
        }
    }
    
    public void moveToStartOfLine() {
        this.x = 0;
    }
    
    public void moveToEndOfLine() {
        this.x = maxX - 1;
    }
    
    public void moveToTop() {
        this.y = 0;
    }
    
    public void moveToBottom() {
        this.y = maxY - 1;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    private boolean isValidPosition(int newX, int newY) {
        return newX >= 0 && newX < maxX && newY >= 0 && newY < maxY;
    }
    
    private boolean canMoveLeft() {
        return x > 0;
    }
    
    private boolean canMoveRight() {
        return x < maxX - 1;
    }
    
    private boolean canMoveUp() {
        return y > 0;
    }
    
    private boolean canMoveDown() {
        return y < maxY - 1;
    }
}