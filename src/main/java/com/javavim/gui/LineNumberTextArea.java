package com.javavim.gui;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;

/**
 * A TextArea with line numbers displayed on the left side.
 * Combines a line number pane with the text area for vim-like editing experience.
 */
public class LineNumberTextArea extends HBox {
    
    private final TextArea textArea;
    private final VBox lineNumberPane;
    
    private static final String LINE_NUMBER_STYLE = 
        "-fx-background-color: #2d2d2d;" +
        "-fx-text-fill: #858585;" +
        "-fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;" +
        "-fx-font-size: 14px;" +
        "-fx-padding: 2px 8px 0px 8px;" +
        "-fx-alignment: center-right;" +
        "-fx-line-spacing: 0px;";
        
    private static final String TEXT_AREA_STYLE = 
        "-fx-background-color: #1e1e1e;" +
        "-fx-text-fill: #d4d4d4;" +
        "-fx-control-inner-background: #1e1e1e;" +
        "-fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;" +
        "-fx-font-size: 14px;" +
        "-fx-border-color: transparent;" +
        "-fx-focus-color: transparent;" +
        "-fx-faint-focus-color: transparent;" +
        "-fx-padding: 2px;";
    
    public LineNumberTextArea() {
        this.textArea = new TextArea();
        this.lineNumberPane = new VBox();
        
        setupStyles();
        setupLayout();
        setupLineNumbers();
    }
    
    public LineNumberTextArea(String initialText) {
        this();
        textArea.setText(initialText);
        updateLineNumbers();
    }
    
    private void setupStyles() {
        textArea.setStyle(TEXT_AREA_STYLE);
        lineNumberPane.setStyle(LINE_NUMBER_STYLE);
        setStyle("-fx-background-color: #1e1e1e;");
    }
    
    private void setupLayout() {
        lineNumberPane.setPrefWidth(50);
        lineNumberPane.setMinWidth(50);
        lineNumberPane.setMaxWidth(50);
        lineNumberPane.setSpacing(0);
        lineNumberPane.setPadding(new Insets(2, 0, 2, 0)); // Match TextArea padding
        
        // Try to match TextArea's content area padding
        textArea.setPadding(new Insets(0, 0, 0, 0));
        
        HBox.setHgrow(textArea, Priority.ALWAYS);
        
        getChildren().addAll(lineNumberPane, textArea);
        setSpacing(0);
    }
    
    private void setupLineNumbers() {
        // Update line numbers when text changes
        textArea.textProperty().addListener((obs, oldText, newText) -> updateLineNumbers());
        
        // Update line numbers when scroll position changes
        textArea.scrollTopProperty().addListener((obs, oldVal, newVal) -> updateLineNumbers());
        
        updateLineNumbers();
    }
    
    private void updateLineNumbers() {
        lineNumberPane.getChildren().clear();
        
        String text = textArea.getText();
        int lineCount = text.isEmpty() ? 1 : text.split("\n", -1).length;
        
        for (int i = 1; i <= lineCount; i++) {
            Label lineNumber = new Label(String.valueOf(i));
            lineNumber.setStyle(LINE_NUMBER_STYLE + "-fx-line-spacing: 0px;");
            lineNumber.setPrefWidth(42);
            lineNumber.setMaxWidth(42);
            lineNumberPane.getChildren().add(lineNumber);
        }
    }
    
    // Delegate methods to access TextArea functionality
    public TextArea getTextArea() {
        return textArea;
    }
    
    public String getText() {
        return textArea.getText();
    }
    
    public void setText(String text) {
        textArea.setText(text);
        updateLineNumbers();
    }
    
    public void appendText(String text) {
        textArea.appendText(text);
        updateLineNumbers();
    }
    
    public int getCaretPosition() {
        return textArea.getCaretPosition();
    }
    
    public void positionCaret(int pos) {
        textArea.positionCaret(pos);
    }
    
    public void selectRange(int anchor, int caretPosition) {
        textArea.selectRange(anchor, caretPosition);
    }
    
    public int getAnchor() {
        return textArea.getAnchor();
    }
    
    public void requestFocus() {
        textArea.requestFocus();
    }
}