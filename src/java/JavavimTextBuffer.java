public class JavavimTextBuffer {
    private StringBuilder text;
    private int cursorPosition;

    public JavavimTextBuffer() {
        this.text = new StringBuilder();
        this.cursorPosition = 0;
    }

    public void appendText(String newText) {
        this.text.append(newText);
    }

    // Method to insert text at the current cursor position
    public void insertText(String newText) {
        this.text.insert(cursorPosition, newText);
        this.cursorPosition += newText.length();
    }

    // Method to move the cursor to a specific position
    public void moveCursorTo(int position) {
        if (position >= 0 && position <= text.length()) {
            this.cursorPosition = position;
        }
    }

    // Method to delete the character before the cursor
    public void deleteBeforeCursor() {
        if (cursorPosition > 0) {
            text.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
        }
    }

    // Method to delete the character after the cursor
    public void deleteAfterCursor() {
        if (cursorPosition < text.length()) {
            text.deleteCharAt(cursorPosition);
        }
    }

    // Method to get the current text in the buffer
    public String getText() {
        return this.text.toString();
    }

    // Method to get the current cursor position
    public int getCursorPosition() {
        return this.cursorPosition;
    }
}