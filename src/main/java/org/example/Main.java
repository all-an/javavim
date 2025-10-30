package org.example;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class Main extends JFrame {

    // Vim modes
    enum VimMode {
        NORMAL, INSERT, VISUAL, COMMAND
    }

    // Terminal colors
    private static final Color BG_COLOR = new Color(0, 0, 0);           // Black
    private static final Color FG_COLOR = new Color(0, 255, 0);         // Green
    private static final Color STATUS_BG = new Color(0, 50, 0);         // Dark green
    private static final Color VISUAL_SELECT = new Color(0, 100, 0);    // Selection color

    // Components
    private JTextPane editorPane;
    private JLabel statusBar;
    private VimMode currentMode = VimMode.NORMAL;
    private String commandBuffer = "";
    private String currentFilePath = null;
    private int visualStartPos = -1;

    // NerdTree components
    private JTree fileTree;
    private JScrollPane treeScrollPane;
    private JSplitPane splitPane;
    private boolean nerdTreeVisible = false;

    public Main(String filename) {
        // Set up the frame - fullscreen terminal style
        setTitle("VIM");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make it fullscreen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        // Create editor pane
        editorPane = new JTextPane();
        editorPane.setBackground(BG_COLOR);
        editorPane.setForeground(FG_COLOR);
        editorPane.setFont(new Font("Consolas", Font.PLAIN, 16));
        editorPane.setCaretColor(FG_COLOR);
        editorPane.setSelectionColor(VISUAL_SELECT);
        editorPane.setSelectedTextColor(FG_COLOR);
        editorPane.setMargin(new Insets(10, 10, 10, 50));

        // Make caret thicker (block cursor style)
        editorPane.setCaret(new BlockCaret());

        // Add key listener for vim bindings
        editorPane.addKeyListener(new VimKeyListener());

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_COLOR);

        // Create status bar
        statusBar = new JLabel(" NORMAL ", JLabel.LEFT);
        statusBar.setBackground(STATUS_BG);
        statusBar.setForeground(FG_COLOR);
        statusBar.setFont(new Font("Consolas", Font.BOLD, 14));
        statusBar.setOpaque(true);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Create NerdTree file explorer
        createNerdTree();

        // Create split pane with tree on left, editor on right
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, scrollPane);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(2);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_COLOR);

        // Hide tree initially
        treeScrollPane.setVisible(false);
        splitPane.setDividerLocation(0);

        // Layout
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Open file if provided, otherwise show welcome message
        if (filename != null && !filename.isEmpty()) {
            openFile(filename);
        } else {
            // Initial text
            editorPane.setText("~ VIM - Terminal Edition ~\n~ Press 'i' to enter INSERT mode ~\n~ Press ':' to enter COMMAND mode ~\n~ Press 'v' to enter VISUAL mode ~\n~ Type ':help' for help ~\n");
            editorPane.setCaretPosition(0);
        }

        // Disable editing by default (NORMAL mode)
        editorPane.setEditable(false);

        // Focus
        editorPane.requestFocusInWindow();
    }

    // Create NerdTree file explorer
    private void createNerdTree() {
        // Get current directory
        File rootDir = new File(System.getProperty("user.dir"));
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootDir.getName());
        buildFileTree(rootDir, rootNode);

        fileTree = new JTree(rootNode);
        fileTree.setBackground(BG_COLOR);
        fileTree.setForeground(FG_COLOR);
        fileTree.setFont(new Font("Consolas", Font.PLAIN, 14));

        // Custom renderer for terminal colors
        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                setBackground(sel ? VISUAL_SELECT : BG_COLOR);
                setForeground(FG_COLOR);
                setBorderSelectionColor(FG_COLOR);
                setBackgroundNonSelectionColor(BG_COLOR);
                setBackgroundSelectionColor(VISUAL_SELECT);
                setTextSelectionColor(FG_COLOR);
                setTextNonSelectionColor(FG_COLOR);
                setOpaque(true);

                // Different icons for files vs folders
                if (leaf) {
                    setText("  " + value.toString());
                } else {
                    setText(expanded ? "▼ " + value.toString() : "▶ " + value.toString());
                }

                return this;
            }
        });

        // Add double-click listener to open files
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = fileTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        File file = getFileFromNode(node, rootDir);
                        if (file != null && file.isFile()) {
                            openFile(file.getAbsolutePath());
                            editorPane.requestFocusInWindow();
                        }
                    }
                }
            }
        });

        // Add Enter key listener to open files
        fileTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    TreePath path = fileTree.getSelectionPath();
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        File file = getFileFromNode(node, rootDir);
                        if (file != null && file.isFile()) {
                            openFile(file.getAbsolutePath());
                            editorPane.requestFocusInWindow();
                        }
                    }
                }
                // Tab to switch focus to editor
                else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    editorPane.requestFocusInWindow();
                }
                // Ctrl+N to toggle tree
                else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                    e.consume();
                    toggleNerdTree();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                int currentRow = fileTree.getLeadSelectionRow();

                // Vim-style navigation with j/k
                if (c == 'j' && currentRow < fileTree.getRowCount() - 1) {
                    e.consume();
                    fileTree.setSelectionRow(currentRow + 1);
                    fileTree.scrollRowToVisible(currentRow + 1);
                } else if (c == 'k' && currentRow > 0) {
                    e.consume();
                    fileTree.setSelectionRow(currentRow - 1);
                    fileTree.scrollRowToVisible(currentRow - 1);
                }
                // l to expand, h to collapse
                else if (c == 'l') {
                    e.consume();
                    TreePath path = fileTree.getSelectionPath();
                    if (path != null && !fileTree.isExpanded(path)) {
                        fileTree.expandPath(path);
                    }
                } else if (c == 'h') {
                    e.consume();
                    TreePath path = fileTree.getSelectionPath();
                    if (path != null && fileTree.isExpanded(path)) {
                        fileTree.collapsePath(path);
                    }
                }
            }
        });

        treeScrollPane = new JScrollPane(fileTree);
        treeScrollPane.setBorder(null);
        treeScrollPane.getViewport().setBackground(BG_COLOR);
    }

    // Build the file tree recursively
    private void buildFileTree(File directory, DefaultMutableTreeNode node) {
        File[] files = directory.listFiles();
        if (files != null) {
            // Sort: directories first, then files
            java.util.Arrays.sort(files, (f1, f2) -> {
                if (f1.isDirectory() && !f2.isDirectory()) return -1;
                if (!f1.isDirectory() && f2.isDirectory()) return 1;
                return f1.getName().compareToIgnoreCase(f2.getName());
            });

            for (File file : files) {
                // Skip hidden files
                if (file.getName().startsWith(".")) continue;

                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
                node.add(childNode);

                // Recursively add subdirectories (limit depth to avoid too much recursion)
                if (file.isDirectory() && node.getLevel() < 3) {
                    buildFileTree(file, childNode);
                }
            }
        }
    }

    // Get the actual File object from a tree node
    private File getFileFromNode(DefaultMutableTreeNode node, File rootDir) {
        Object[] path = node.getUserObjectPath();
        StringBuilder pathBuilder = new StringBuilder(rootDir.getParent());
        for (Object p : path) {
            pathBuilder.append(File.separator).append(p.toString());
        }
        return new File(pathBuilder.toString());
    }

    // Toggle NerdTree visibility
    private void toggleNerdTree() {
        nerdTreeVisible = !nerdTreeVisible;
        treeScrollPane.setVisible(nerdTreeVisible);

        if (nerdTreeVisible) {
            splitPane.setDividerLocation(250);
        } else {
            splitPane.setDividerLocation(0);
        }

        editorPane.requestFocusInWindow();
    }

    // Block cursor for terminal feel
    class BlockCaret extends DefaultCaret {
        public BlockCaret() {
            setBlinkRate(500);
        }

        @Override
        protected synchronized void damage(Rectangle r) {
            if (r != null) {
                x = r.x;
                y = r.y;
                width = 10; // Block width
                height = r.height;
                repaint();
            }
        }

        @Override
        public void paint(Graphics g) {
            JTextComponent comp = getComponent();
            if (comp == null) return;

            int pos = getDot();
            Rectangle r;
            try {
                r = comp.modelToView(pos);
                if (r == null) return;
            } catch (BadLocationException e) {
                return;
            }

            if (isVisible()) {
                g.setColor(FG_COLOR);
                g.fillRect(r.x, r.y, 10, r.height);

                // Draw the character in inverted color
                try {
                    String ch = comp.getDocument().getText(pos, 1);
                    g.setColor(BG_COLOR);
                    g.setFont(comp.getFont());
                    g.drawString(ch, r.x, r.y + r.height - 4);
                } catch (BadLocationException e) {
                    // Ignore
                }
            }
        }
    }

    // Vim key listener
    class VimKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            char keyChar = e.getKeyChar();

            switch (currentMode) {
                case NORMAL:
                    handleNormalMode(e, keyCode, keyChar);
                    break;
                case INSERT:
                    handleInsertMode(e, keyCode, keyChar);
                    break;
                case VISUAL:
                    handleVisualMode(e, keyCode, keyChar);
                    break;
                case COMMAND:
                    handleCommandMode(e, keyCode, keyChar);
                    break;
            }
        }

        private void handleNormalMode(KeyEvent e, int keyCode, char keyChar) {
            // Check for Ctrl+N to toggle NerdTree
            if (e.isControlDown() && keyCode == KeyEvent.VK_N) {
                e.consume();
                toggleNerdTree();
                return;
            }

            // Tab to switch focus to tree if visible
            if (keyCode == KeyEvent.VK_TAB && nerdTreeVisible) {
                e.consume();
                fileTree.requestFocusInWindow();
                return;
            }

            e.consume();

            int pos = editorPane.getCaretPosition();
            String text = editorPane.getText();

            switch (keyChar) {
                case 'h': // Move left
                    if (pos > 0) editorPane.setCaretPosition(pos - 1);
                    break;
                case 'l': // Move right
                    if (pos < text.length() - 1) editorPane.setCaretPosition(pos + 1);
                    break;
                case 'j': // Move down
                    try {
                        int line = editorPane.getDocument().getDefaultRootElement().getElementIndex(pos);
                        Element elem = editorPane.getDocument().getDefaultRootElement().getElement(line + 1);
                        if (elem != null) {
                            editorPane.setCaretPosition(elem.getStartOffset());
                        }
                    } catch (Exception ex) {}
                    break;
                case 'k': // Move up
                    try {
                        int line = editorPane.getDocument().getDefaultRootElement().getElementIndex(pos);
                        if (line > 0) {
                            Element elem = editorPane.getDocument().getDefaultRootElement().getElement(line - 1);
                            editorPane.setCaretPosition(elem.getStartOffset());
                        }
                    } catch (Exception ex) {}
                    break;
                case 'i': // Enter INSERT mode
                    switchMode(VimMode.INSERT);
                    break;
                case 'v': // Enter VISUAL mode
                    visualStartPos = pos;
                    switchMode(VimMode.VISUAL);
                    break;
                case ':': // Enter COMMAND mode
                    commandBuffer = "";
                    switchMode(VimMode.COMMAND);
                    break;
                case 'x': // Delete character
                    try {
                        editorPane.getDocument().remove(pos, 1);
                    } catch (BadLocationException ex) {}
                    break;
                case 'o': // Open line below and enter INSERT
                    try {
                        int line = editorPane.getDocument().getDefaultRootElement().getElementIndex(pos);
                        Element elem = editorPane.getDocument().getDefaultRootElement().getElement(line);
                        int endOffset = elem.getEndOffset();
                        editorPane.getDocument().insertString(endOffset - 1, "\n", null);
                        editorPane.setCaretPosition(endOffset);
                        switchMode(VimMode.INSERT);
                    } catch (BadLocationException ex) {}
                    break;
                case 'A': // Append at end of line
                    try {
                        int line = editorPane.getDocument().getDefaultRootElement().getElementIndex(pos);
                        Element elem = editorPane.getDocument().getDefaultRootElement().getElement(line);
                        editorPane.setCaretPosition(elem.getEndOffset() - 1);
                        switchMode(VimMode.INSERT);
                    } catch (Exception ex) {}
                    break;
                case 'a': // Append after cursor
                    if (pos < text.length() - 1) editorPane.setCaretPosition(pos + 1);
                    switchMode(VimMode.INSERT);
                    break;
            }

            // ESC in NORMAL mode - already in normal, do nothing
            // Use :q to quit
        }

        private void handleInsertMode(KeyEvent e, int keyCode, char keyChar) {
            // Check for Ctrl+N to toggle NerdTree
            if (e.isControlDown() && keyCode == KeyEvent.VK_N) {
                e.consume();
                toggleNerdTree();
                return;
            }

            if (keyCode == KeyEvent.VK_ESCAPE) {
                e.consume();
                switchMode(VimMode.NORMAL);
            }
            // Let default typing behavior work
        }

        private void handleVisualMode(KeyEvent e, int keyCode, char keyChar) {
            e.consume();

            int pos = editorPane.getCaretPosition();
            String text = editorPane.getText();

            switch (keyChar) {
                case 'h': // Extend selection left
                    if (pos > 0) {
                        editorPane.setCaretPosition(pos - 1);
                        updateVisualSelection();
                    }
                    break;
                case 'l': // Extend selection right
                    if (pos < text.length() - 1) {
                        editorPane.setCaretPosition(pos + 1);
                        updateVisualSelection();
                    }
                    break;
                case 'j': // Extend selection down
                    try {
                        int line = editorPane.getDocument().getDefaultRootElement().getElementIndex(pos);
                        Element elem = editorPane.getDocument().getDefaultRootElement().getElement(line + 1);
                        if (elem != null) {
                            editorPane.setCaretPosition(elem.getStartOffset());
                            updateVisualSelection();
                        }
                    } catch (Exception ex) {}
                    break;
                case 'k': // Extend selection up
                    try {
                        int line = editorPane.getDocument().getDefaultRootElement().getElementIndex(pos);
                        if (line > 0) {
                            Element elem = editorPane.getDocument().getDefaultRootElement().getElement(line - 1);
                            editorPane.setCaretPosition(elem.getStartOffset());
                            updateVisualSelection();
                        }
                    } catch (Exception ex) {}
                    break;
                case 'd': // Delete selection
                    try {
                        int start = Math.min(visualStartPos, editorPane.getCaretPosition());
                        int end = Math.max(visualStartPos, editorPane.getCaretPosition());
                        editorPane.getDocument().remove(start, end - start);
                        editorPane.setCaretPosition(start);
                    } catch (BadLocationException ex) {}
                    switchMode(VimMode.NORMAL);
                    break;
            }

            if (keyCode == KeyEvent.VK_ESCAPE) {
                editorPane.select(0, 0);
                switchMode(VimMode.NORMAL);
            }
        }

        private void handleCommandMode(KeyEvent e, int keyCode, char keyChar) {
            e.consume();

            if (keyCode == KeyEvent.VK_ESCAPE) {
                switchMode(VimMode.NORMAL);
                return;
            }

            if (keyCode == KeyEvent.VK_ENTER) {
                executeCommand(commandBuffer);
                switchMode(VimMode.NORMAL);
                return;
            }

            if (keyCode == KeyEvent.VK_BACK_SPACE) {
                if (commandBuffer.length() > 0) {
                    commandBuffer = commandBuffer.substring(0, commandBuffer.length() - 1);
                    updateStatusBar();
                }
                return;
            }

            if (!Character.isISOControl(keyChar)) {
                commandBuffer += keyChar;
                updateStatusBar();
            }
        }

        private void updateVisualSelection() {
            int start = Math.min(visualStartPos, editorPane.getCaretPosition());
            int end = Math.max(visualStartPos, editorPane.getCaretPosition());
            editorPane.select(start, end);
        }
    }

    private void switchMode(VimMode newMode) {
        currentMode = newMode;

        switch (newMode) {
            case NORMAL:
                editorPane.setEditable(false);
                editorPane.getCaret().setVisible(true);
                break;
            case INSERT:
                editorPane.setEditable(true);
                editorPane.getCaret().setVisible(true);
                break;
            case VISUAL:
                editorPane.setEditable(false);
                editorPane.getCaret().setVisible(true);
                break;
            case COMMAND:
                editorPane.setEditable(false);
                commandBuffer = "";
                break;
        }

        updateStatusBar();
    }

    private void updateStatusBar() {
        String status = "";
        switch (currentMode) {
            case NORMAL:
                status = " NORMAL ";
                break;
            case INSERT:
                status = " -- INSERT -- ";
                break;
            case VISUAL:
                status = " -- VISUAL -- ";
                break;
            case COMMAND:
                status = " :" + commandBuffer;
                break;
        }

        if (currentFilePath != null) {
            status += "  [" + currentFilePath + "]";
        }

        statusBar.setText(status);
    }

    private void executeCommand(String cmd) {
        cmd = cmd.trim();

        if (cmd.equals("q")) {
            System.exit(0);
        } else if (cmd.equals("w")) {
            saveFile();
        } else if (cmd.equals("wq") || cmd.equals("x")) {
            saveFile();
            System.exit(0);
        } else if (cmd.startsWith("w ")) {
            String filename = cmd.substring(2).trim();
            // Remove BOM and other invisible Unicode characters
            filename = filename.replaceAll("[\uFEFF\u200B-\u200D\uFFFE\uFFFF]", "");
            currentFilePath = filename;
            saveFile();
        } else if (cmd.startsWith("e ")) {
            String filename = cmd.substring(2).trim();
            // Remove BOM and other invisible Unicode characters
            filename = filename.replaceAll("[\uFEFF\u200B-\u200D\uFFFE\uFFFF]", "");
            openFile(filename);
        } else if (cmd.equals("help")) {
            showHelp();
        } else {
            statusBar.setText(" Unknown command: " + cmd);
        }
    }

    private void saveFile() {
        if (currentFilePath == null) {
            currentFilePath = "untitled.txt";
        }

        try {
            Files.writeString(Paths.get(currentFilePath), editorPane.getText());
            statusBar.setText(" Saved: " + currentFilePath);
        } catch (IOException e) {
            statusBar.setText(" Error saving file: " + e.getMessage());
        }
    }

    private void openFile(String filename) {
        try {
            String content = Files.readString(Paths.get(filename));
            editorPane.setText(content);
            currentFilePath = filename;
            editorPane.setCaretPosition(0);
            statusBar.setText(" Opened: " + filename);
        } catch (IOException e) {
            statusBar.setText(" Error opening file: " + e.getMessage());
        }
    }

    private void showHelp() {
        String help = "=== VIM TERMINAL EDITION - HELP ===\n\n" +
                      "FILE EXPLORER:\n" +
                      "  Ctrl+N      - Toggle NerdTree file explorer\n" +
                      "  Tab         - Switch focus between tree and editor\n" +
                      "  (In tree: Arrow keys to navigate, Enter to open)\n\n" +
                      "NORMAL MODE (default):\n" +
                      "  h, j, k, l  - Move cursor (left, down, up, right)\n" +
                      "  i           - Enter INSERT mode\n" +
                      "  a           - Append after cursor\n" +
                      "  A           - Append at end of line\n" +
                      "  o           - Open new line below\n" +
                      "  v           - Enter VISUAL mode\n" +
                      "  x           - Delete character\n" +
                      "  :           - Enter COMMAND mode\n" +
                      "  Ctrl+N      - Toggle NerdTree\n" +
                      "  Tab         - Focus file tree (if open)\n\n" +
                      "INSERT MODE:\n" +
                      "  ESC         - Return to NORMAL mode\n" +
                      "  Ctrl+N      - Toggle NerdTree\n" +
                      "  (Type freely)\n\n" +
                      "VISUAL MODE:\n" +
                      "  h, j, k, l  - Extend selection\n" +
                      "  d           - Delete selection\n" +
                      "  ESC         - Return to NORMAL mode\n\n" +
                      "COMMAND MODE:\n" +
                      "  :w          - Save file\n" +
                      "  :w filename - Save as filename\n" +
                      "  :e filename - Open file\n" +
                      "  :q          - Quit application\n" +
                      "  :wq or :x   - Save and quit\n" +
                      "  :help       - Show this help\n" +
                      "  ESC         - Return to NORMAL mode\n\n" +
                      "USAGE:\n" +
                      "  Run with: java -jar app.jar [filename.java]\n" +
                      "  Example: java -jar app.jar MyClass.java\n\n" +
                      "Press 'i' to start editing, Ctrl+N for file explorer...\n";

        editorPane.setText(help);
        editorPane.setCaretPosition(0);
    }

    public static void main(String[] args) {
        // Get filename from command-line arguments
        String filename = null;
        if (args.length > 0) {
            filename = args[0];
        }

        final String fileToOpen = filename;

        SwingUtilities.invokeLater(() -> {
            Main frame = new Main(fileToOpen);
            frame.setVisible(true);

            Timer timer = new Timer(100, e -> {
                frame.editorPane.requestFocusInWindow();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}
