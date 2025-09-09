package com.javavim;

import com.javavim.buffer.Buffer;
import com.javavim.gui.VimTextEditor;
import com.javavim.gui.LineNumberTextArea;
import com.javavim.vim.VimMode;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Main JavaFX application for JavaVim - A vim-style editor with dark theme.
 * Follows single responsibility principle - manages main application window only.
 */
public class JavavimApplication extends Application {
    
    private static final String DARK_THEME_CSS = 
        "-fx-background-color: #1e1e1e;" +
        "-fx-text-fill: #d4d4d4;" +
        "-fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;" +
        "-fx-font-size: 14px;";
    
    private VimTextEditor textEditor;
    private LineNumberTextArea editorWithLineNumbers;
    private Label statusLabel;
    private Label modeLabel;
    
    @Override
    public void start(Stage primaryStage) {
        createComponents();
        layoutComponents();
        setupEventHandlers();
        
        Scene scene = createScene();
        configureStage(primaryStage, scene);
        
        editorWithLineNumbers.requestFocus();
    }
    
    private void createComponents() {
        Buffer buffer = createWelcomeBuffer();
        textEditor = new VimTextEditor(buffer);
        
        // Create welcome text for line number text area
        StringBuilder welcomeText = new StringBuilder();
        for (int i = 0; i < buffer.getLineCount(); i++) {
            if (i > 0) welcomeText.append("\n");
            welcomeText.append(buffer.getLine(i));
        }
        
        editorWithLineNumbers = new LineNumberTextArea(welcomeText.toString());
        statusLabel = createStatusLabel();
        modeLabel = createModeLabel();
    }
    
    private Buffer createWelcomeBuffer() {
        Buffer buffer = new Buffer("[Welcome]");
        buffer.setLine(0, "JavaVim - Vim-Style Editor");
        buffer.insertLine(1, "");
        buffer.insertLine(2, "Current Mode: NORMAL");
        buffer.insertLine(3, "");
        buffer.insertLine(4, "Vim Commands:");
        buffer.insertLine(5, "  i          - Enter Insert mode");
        buffer.insertLine(6, "  ESC        - Return to Normal mode");
        buffer.insertLine(7, "  h,j,k,l    - Move cursor (Normal mode)");
        buffer.insertLine(8, "  v          - Enter Visual mode");
        buffer.insertLine(9, "  :          - Enter Command mode");
        buffer.insertLine(10, "");
        buffer.insertLine(11, "Try pressing 'i' to start editing!");
        buffer.setModified(false);
        return buffer;
    }
    
    private Label createStatusLabel() {
        Label label = new Label("Ready");
        label.setStyle(DARK_THEME_CSS + "-fx-padding: 5px;");
        return label;
    }
    
    private Label createModeLabel() {
        Label label = new Label("NORMAL");
        label.setStyle(DARK_THEME_CSS + 
                      "-fx-padding: 5px;" +
                      "-fx-background-color: #264f78;" +
                      "-fx-text-fill: white;");
        return label;
    }
    
    private void layoutComponents() {
        BorderPane root = new BorderPane();
        root.setStyle(DARK_THEME_CSS);
        
        // Add text editor with line numbers to center
        root.setCenter(editorWithLineNumbers);
        
        // Create status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
        
        this.root = root;
    }
    
    private BorderPane root;
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setStyle(DARK_THEME_CSS + "-fx-padding: 5px;");
        statusBar.setPadding(new Insets(5));
        
        statusBar.getChildren().addAll(modeLabel, statusLabel);
        return statusBar;
    }
    
    private void setupEventHandlers() {
        textEditor.setStatusListener(new VimTextEditor.VimStatusListener() {
            @Override
            public void onModeChanged(VimMode mode) {
                updateModeDisplay(mode);
            }
            
            @Override
            public void onCommandRequested() {
                statusLabel.setText("Command mode - type command and press Enter");
            }
        });
    }
    
    private void updateModeDisplay(VimMode mode) {
        modeLabel.setText(mode.toString());
        
        String modeColor = switch (mode) {
            case NORMAL -> "#264f78";
            case INSERT -> "#4d7c0f";
            case VISUAL -> "#7c2d12";
            case COMMAND -> "#581c87";
        };
        
        modeLabel.setStyle(DARK_THEME_CSS + 
                          "-fx-padding: 5px;" +
                          "-fx-background-color: " + modeColor + ";" +
                          "-fx-text-fill: white;");
        
        String statusMessage = switch (mode) {
            case NORMAL -> "Ready";
            case INSERT -> "-- INSERT --";
            case VISUAL -> "-- VISUAL --";
            case COMMAND -> "Enter command:";
        };
        
        statusLabel.setText(statusMessage);
    }
    
    private Scene createScene() {
        Scene scene = new Scene(root, 1024, 768);
        loadCssTheme(scene);
        return scene;
    }
    
    private void loadCssTheme(Scene scene) {
        try {
            String css = getClass().getResource("/dark-theme.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("Could not load dark-theme.css, using inline styles only");
        }
    }
    
    private void configureStage(Stage primaryStage, Scene scene) {
        primaryStage.setTitle("JavaVim - Vim Editor");
        primaryStage.setScene(scene);
        
        // Make window maximized (full screen available but maximized is more practical)
        primaryStage.setMaximized(true);
        
        // Optional: Allow user to toggle full screen with F11
        scene.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("F11")) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
        
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}