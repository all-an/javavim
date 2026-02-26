package org.javavim;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

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
    private static final String CTRL_E_LOG_FILE = "ctrl-e-run.log";

    // Configuration
    private static final Config config = new Config().load();

    // Components
    private JTextPane editorPane;
    private JLabel statusBar;
    private JLabel focusIndicator;
    private JPanel statusPanel;
    private VimMode currentMode = VimMode.NORMAL;
    private String commandBuffer = "";
    private String currentFilePath = null;
    private int visualStartPos = -1;

    // NerdTree components
    private JTree fileTree;
    private JScrollPane treeScrollPane;
    private JSplitPane splitPane;
    private JSplitPane mainSplitPane;  // Vertical split for editor/terminal
    private boolean nerdTreeVisible = false;
    private File currentRootDir;  // Current directory shown in explorer

    // Terminal components
    private JTextArea terminalArea;
    private JTextField terminalInput;
    private JPanel terminalPanel;
    private boolean terminalVisible = false;
    private Process currentProcess;
    private BufferedWriter processWriter;

    // Line numbers
    private JTextArea lineNumbers;
    private JScrollPane editorScrollPane;

    public Main(String filename) {
        // Set up the frame - windowed terminal style
        setTitle("VIM");
        setUndecorated(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Open in a regular window instead of fullscreen
        setSize(1280, 800);
        setLocationRelativeTo(null);

        // Create editor pane
        editorPane = new JTextPane();
        editorPane.setBackground(BG_COLOR);
        editorPane.setForeground(FG_COLOR);
        editorPane.setFont(new Font("Consolas", Font.PLAIN, config.getFontSize()));
        editorPane.setCaretColor(FG_COLOR);
        editorPane.setSelectionColor(VISUAL_SELECT);
        editorPane.setSelectedTextColor(FG_COLOR);
        editorPane.setMargin(new Insets(10, 10, 10, 50));

        // Set up tab size
        setTabSize(editorPane, config.getTabSize());

        // Make caret thicker (block cursor style)
        editorPane.setCaret(new BlockCaret());

        // Add key listener for vim bindings
        editorPane.addKeyListener(new VimKeyListener());

        // Add focus listener to update indicator
        editorPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateFocusIndicator("FILE");
            }
        });

        // Create line numbers panel
        lineNumbers = new JTextArea("  1 ");
        lineNumbers.setBackground(new Color(20, 20, 20));
        lineNumbers.setForeground(new Color(100, 100, 100));
        lineNumbers.setFont(new Font("Consolas", Font.PLAIN, config.getFontSize()));
        lineNumbers.setEditable(false);
        lineNumbers.setFocusable(false);
        lineNumbers.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        lineNumbers.setVisible(config.isLineNumbers());

        // Update line numbers when document changes
        editorPane.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
        });

        // Add scroll pane with line numbers
        editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setBorder(null);
        editorScrollPane.getViewport().setBackground(BG_COLOR);
        editorScrollPane.setRowHeaderView(lineNumbers);

        // Create terminal panel
        createTerminalPanel();

        // Create status bar panel
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(STATUS_BG);

        statusBar = new JLabel(" NORMAL ", JLabel.LEFT);
        statusBar.setBackground(STATUS_BG);
        statusBar.setForeground(FG_COLOR);
        statusBar.setFont(new Font("Consolas", Font.BOLD, 14));
        statusBar.setOpaque(true);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        focusIndicator = new JLabel("FILE ", JLabel.RIGHT);
        focusIndicator.setBackground(STATUS_BG);
        focusIndicator.setForeground(new Color(100, 200, 100));
        focusIndicator.setFont(new Font("Consolas", Font.BOLD, 14));
        focusIndicator.setOpaque(true);
        focusIndicator.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statusPanel.add(statusBar, BorderLayout.CENTER);
        statusPanel.add(focusIndicator, BorderLayout.EAST);

        // Create NerdTree file explorer
        createNerdTree();

        // Create split pane with tree on left, editor on right
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, editorScrollPane);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(2);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_COLOR);

        // Hide tree initially
        treeScrollPane.setVisible(false);
        splitPane.setDividerLocation(0);

        // Create main split pane with editor on top, terminal on bottom
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane, terminalPanel);
        mainSplitPane.setDividerSize(2);
        mainSplitPane.setBorder(null);
        mainSplitPane.setBackground(BG_COLOR);
        mainSplitPane.setResizeWeight(1.0);  // Editor gets all extra space

        // Hide terminal initially
        terminalPanel.setVisible(false);
        mainSplitPane.setDividerLocation(getHeight());

        // Layout
        setLayout(new BorderLayout());
        add(mainSplitPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        // Open file if provided, otherwise show welcome message
        if (filename != null && !filename.isEmpty()) {
            openFile(filename);
        } else {
            // Initial text
            editorPane.setText("~ VIM - Terminal Edition ~\n~ Press 'i' to enter INSERT mode ~\n~ Press ':' to enter COMMAND mode ~\n~ Press 'v' to enter VISUAL mode ~\n~ Press Ctrl+N to toggle NerdTree ~\n~ Press Ctrl+E to compile+run current Java folder ~\n~ Ctrl+E saves output to ctrl-e-run.log ~\n~ Press Tab to switch focus (tree/editor) ~\n~ Type ':help' for help ~\n");
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
        currentRootDir = new File(System.getProperty("user.dir"));

        fileTree = new JTree(buildTreeModel());
        fileTree.setBackground(BG_COLOR);
        fileTree.setForeground(FG_COLOR);
        fileTree.setFont(new Font("Consolas", Font.PLAIN, 14));
        fileTree.setRootVisible(true);

        // Custom renderer for terminal colors
        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
            {
                // Remove default icons
                setLeafIcon(null);
                setOpenIcon(null);
                setClosedIcon(null);
            }

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

                String nodeText = value.toString();
                // Special styling for ".." parent directory
                if (nodeText.equals("..")) {
                    setText("<- ..");
                } else if (leaf) {
                    setText("   " + nodeText);
                } else {
                    setText(expanded ? "[-] " + nodeText : "[+] " + nodeText);
                }

                return this;
            }
        });

        // Add double-click listener to open files or navigate directories
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleTreeSelection();
                }
            }
        });

        // Add Enter key listener to open files
        fileTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleTreeSelection();
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
                // Ctrl+' to toggle terminal
                else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_QUOTE) {
                    e.consume();
                    toggleTerminal();
                }
                // Ctrl+1 to focus editor
                else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1) {
                    e.consume();
                    editorPane.requestFocusInWindow();
                }
                // Backspace to go to parent directory
                else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    navigateToParent();
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
                // l to expand or enter directory, h to collapse or go to parent
                else if (c == 'l') {
                    e.consume();
                    TreePath path = fileTree.getSelectionPath();
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        File file = getFileFromNode(node);
                        if (file != null && file.isDirectory()) {
                            if (fileTree.isExpanded(path)) {
                                // Already expanded, enter into the directory
                                navigateToDirectory(file);
                            } else {
                                fileTree.expandPath(path);
                            }
                        }
                    }
                } else if (c == 'h') {
                    e.consume();
                    TreePath path = fileTree.getSelectionPath();
                    if (path != null) {
                        if (fileTree.isExpanded(path)) {
                            fileTree.collapsePath(path);
                        } else {
                            // Go to parent directory
                            navigateToParent();
                        }
                    }
                }
                // '-' key to go to parent directory (like vim's netrw)
                else if (c == '-') {
                    e.consume();
                    navigateToParent();
                }
            }
        });

        // Add focus listener to update indicator
        fileTree.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateFocusIndicator("TREE");
            }
        });

        treeScrollPane = new JScrollPane(fileTree);
        treeScrollPane.setBorder(null);
        treeScrollPane.getViewport().setBackground(BG_COLOR);
    }

    // Create integrated terminal panel
    private void createTerminalPanel() {
        terminalPanel = new JPanel(new BorderLayout());
        terminalPanel.setBackground(BG_COLOR);

        // Terminal output area
        terminalArea = new JTextArea();
        terminalArea.setBackground(BG_COLOR);
        terminalArea.setForeground(FG_COLOR);
        terminalArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        terminalArea.setEditable(false);
        terminalArea.setFocusable(false);
        terminalArea.setLineWrap(true);
        terminalArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JScrollPane terminalScroll = new JScrollPane(terminalArea);
        terminalScroll.setBorder(null);
        terminalScroll.getViewport().setBackground(BG_COLOR);

        // Terminal input field
        terminalInput = new JTextField();
        terminalInput.setBackground(new Color(20, 20, 20));
        terminalInput.setForeground(FG_COLOR);
        terminalInput.setCaretColor(FG_COLOR);
        terminalInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        terminalInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Handle terminal input
        terminalInput.addActionListener(e -> executeTerminalCommand());

        // Handle keyboard shortcuts in terminal
        terminalInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    editorPane.requestFocusInWindow();
                } else if (e.getKeyCode() == KeyEvent.VK_1 && e.isControlDown()) {
                    e.consume();
                    editorPane.requestFocusInWindow();
                } else if (e.getKeyCode() == KeyEvent.VK_QUOTE && e.isControlDown()) {
                    e.consume();
                    toggleTerminal();
                } else if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
                    // Ctrl+C to interrupt process
                    if (currentProcess != null && currentProcess.isAlive()) {
                        currentProcess.destroyForcibly();
                        appendToTerminal("\n^C\n");
                    }
                }
            }
        });

        // Add focus listener to update indicator
        terminalInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateFocusIndicator("TERMINAL");
            }
        });

        // Terminal header
        JLabel terminalHeader = new JLabel(" TERMINAL ");
        terminalHeader.setBackground(STATUS_BG);
        terminalHeader.setForeground(FG_COLOR);
        terminalHeader.setFont(new Font("Consolas", Font.BOLD, 12));
        terminalHeader.setOpaque(true);
        terminalHeader.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        terminalPanel.add(terminalHeader, BorderLayout.NORTH);
        terminalPanel.add(terminalScroll, BorderLayout.CENTER);
        terminalPanel.add(terminalInput, BorderLayout.SOUTH);

        // Initialize terminal with welcome message
        String shell = System.getProperty("os.name").toLowerCase().contains("win") ? "PowerShell" : "Bash";
        terminalArea.setText(" " + shell + " Terminal - Press Ctrl+' to toggle, Ctrl+1 for editor\n\n");
    }

    // Execute command in terminal
    private void executeTerminalCommand() {
        String command = terminalInput.getText().trim();
        if (command.isEmpty()) return;

        terminalInput.setText("");
        appendToTerminal("> " + command + "\n");

        // Run command in background thread
        new Thread(() -> {
            try {
                ProcessBuilder pb;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    pb = new ProcessBuilder("powershell", "-Command", command);
                } else {
                    pb = new ProcessBuilder("bash", "-c", command);
                }
                pb.directory(currentRootDir);
                pb.redirectErrorStream(true);

                currentProcess = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String output = line;
                    SwingUtilities.invokeLater(() -> appendToTerminal(output + "\n"));
                }

                currentProcess.waitFor();
                SwingUtilities.invokeLater(() -> appendToTerminal("\n"));

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> appendToTerminal("Error: " + e.getMessage() + "\n"));
            }
        }).start();
    }

    // Ctrl+E action: compile all Java files in current file folder and run current class
    private void runJavaFolderFromCurrentFile() {
        String selectedTreeFilePath = getSelectedTreeFilePath();
        String filePathToRun = CtrlEFileSelector.selectJavaFilePath(currentFilePath, selectedTreeFilePath);

        JavaRunPlanBuildResult buildResult = JavaRunPlanBuilder.build(filePathToRun);
        if (!buildResult.isSuccess()) {
            statusBar.setText(buildResult.errorMessage());
            return;
        }

        currentFilePath = filePathToRun;
        saveCurrentFileIfSelected();
        showTerminalForExecution();
        appendToTerminal(" Ctrl+E Java run\n");
        runJavaPlanAsync(buildResult.plan());
    }

    private String getSelectedTreeFilePath() {
        if (fileTree == null) {
            return null;
        }

        TreePath path = fileTree.getSelectionPath();
        if (path == null) {
            return null;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        File file = getFileFromNode(node);
        return (file != null && file.isFile()) ? file.getAbsolutePath() : null;
    }

    private void saveCurrentFileIfSelected() {
        if (currentFilePath != null && !currentFilePath.isBlank()) {
            saveFile();
        }
    }

    private void showTerminalForExecution() {
        if (!terminalVisible) {
            toggleTerminal();
        }
        terminalInput.requestFocusInWindow();
    }

    private void runJavaPlanAsync(JavaRunPlan plan) {
        Thread thread = new Thread(() -> executeJavaPlan(plan), "javavim-ctrl-e-runner");
        thread.setDaemon(true);
        thread.start();
    }

    private void executeJavaPlan(JavaRunPlan plan) {
        Path logFile = resolveCtrlELogPath(plan);
        try (BufferedWriter logWriter = openCtrlELogWriter(logFile)) {
            Files.createDirectories(plan.outputDirectory());
            appendToTerminalOnEdt(" Log file: " + logFile + "\n");
            writeCtrlELogHeader(logWriter, plan, logFile);

            appendCommandPreview(plan.buildCompileCommand(), logWriter);
            int compileExit = executeSystemCommand(plan.buildCompileCommand(), plan.workingDirectory(), logWriter);
            writeCtrlELogLine(logWriter, "compile-exit=" + compileExit);
            if (compileExit != 0) {
                updateStatusText(" Compilation failed (exit " + compileExit + ")");
                return;
            }

            appendCommandPreview(plan.buildRunCommand(), logWriter);
            int runExit = executeSystemCommand(plan.buildRunCommand(), plan.workingDirectory(), logWriter);
            writeCtrlELogLine(logWriter, "run-exit=" + runExit);
            updateStatusText(runExit == 0
                    ? " Ctrl+E run finished successfully"
                    : " Run failed (exit " + runExit + ")");
        } catch (Exception e) {
            appendToTerminalOnEdt("Error: " + e.getMessage() + "\n");
            updateStatusText(" Ctrl+E error: " + e.getMessage());
        }
    }

    private Path resolveCtrlELogPath(JavaRunPlan plan) {
        return plan.workingDirectory().resolve(CTRL_E_LOG_FILE);
    }

    private BufferedWriter openCtrlELogWriter(Path logFile) throws IOException {
        return Files.newBufferedWriter(logFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void writeCtrlELogHeader(BufferedWriter logWriter, JavaRunPlan plan, Path logFile) throws IOException {
        writeCtrlELogLine(logWriter, "-----");
        writeCtrlELogLine(logWriter, "timestamp=" + LocalDateTime.now());
        writeCtrlELogLine(logWriter, "file=" + currentFilePath);
        writeCtrlELogLine(logWriter, "main-class=" + plan.mainClassName());
        writeCtrlELogLine(logWriter, "working-dir=" + plan.workingDirectory());
        writeCtrlELogLine(logWriter, "log-file=" + logFile);
    }

    private void appendCommandPreview(List<String> command, BufferedWriter logWriter) throws IOException {
        String commandLine = String.join(" ", command);
        appendToTerminalOnEdt("> " + commandLine + "\n");
        writeCtrlELogLine(logWriter, "> " + commandLine);
    }

    private int executeSystemCommand(List<String> command, Path workingDirectory, BufferedWriter logWriter)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDirectory.toFile());
        pb.redirectErrorStream(true);

        currentProcess = pb.start();
        streamProcessOutput(currentProcess, logWriter);
        int exitCode = currentProcess.waitFor();
        currentProcess = null;
        appendToTerminalOnEdt("\n");
        return exitCode;
    }

    private void streamProcessOutput(Process process, BufferedWriter logWriter) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                appendToTerminalOnEdt(line + "\n");
                writeCtrlELogLine(logWriter, line);
            }
        }
    }

    private void writeCtrlELogLine(BufferedWriter logWriter, String line) throws IOException {
        logWriter.write(line);
        logWriter.newLine();
        logWriter.flush();
    }

    private void appendToTerminalOnEdt(String text) {
        SwingUtilities.invokeLater(() -> appendToTerminal(text));
    }

    private void updateStatusText(String text) {
        SwingUtilities.invokeLater(() -> statusBar.setText(text));
    }

    // Append text to terminal
    private void appendToTerminal(String text) {
        terminalArea.append(text);
        terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
    }

    // Toggle terminal visibility
    private void toggleTerminal() {
        terminalVisible = !terminalVisible;
        terminalPanel.setVisible(terminalVisible);

        if (terminalVisible) {
            mainSplitPane.setDividerLocation(getHeight() - 250);
            terminalInput.requestFocusInWindow();
        } else {
            mainSplitPane.setDividerLocation(getHeight());
            editorPane.requestFocusInWindow();
        }
    }

    // Update line numbers
    private void updateLineNumbers() {
        lineNumbers.setText(LineNumberFormatter.format(editorPane.getText()));
    }

    // Set tab size for editor
    private void setTabSize(JTextPane editor, int tabSize) {
        FontMetrics fm = editor.getFontMetrics(editor.getFont());
        int charWidth = fm.charWidth(' ');
        int tabWidth = charWidth * tabSize;

        TabStop[] tabs = new TabStop[50];
        for (int i = 0; i < tabs.length; i++) {
            tabs[i] = new TabStop((i + 1) * tabWidth);
        }
        TabSet tabSet = new TabSet(tabs);

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setTabSet(attrs, tabSet);

        StyledDocument doc = editor.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
    }

    // Build tree model for current root directory
    private DefaultTreeModel buildTreeModel() {
        String rootName = currentRootDir.getAbsolutePath();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootName);

        // Add ".." entry to navigate to parent (if parent exists)
        if (currentRootDir.getParentFile() != null) {
            DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("..");
            rootNode.add(parentNode);
        }

        buildFileTree(currentRootDir, rootNode);
        return new DefaultTreeModel(rootNode);
    }

    // Refresh the tree with new root directory
    private void refreshTree() {
        fileTree.setModel(buildTreeModel());
        fileTree.expandRow(0);  // Expand root
        fileTree.setSelectionRow(0);
    }

    // Navigate to parent directory
    private void navigateToParent() {
        File parent = currentRootDir.getParentFile();
        if (parent != null && parent.exists()) {
            navigateToDirectory(parent);
        }
    }

    // Navigate to a specific directory
    private void navigateToDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            currentRootDir = dir;
            refreshTree();
            statusBar.setText(" " + currentRootDir.getAbsolutePath());
        }
    }

    // Handle tree selection (double-click or Enter)
    private void handleTreeSelection() {
        TreePath path = fileTree.getSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            String nodeName = node.getUserObject().toString();

            // Handle ".." - go to parent directory
            if (nodeName.equals("..")) {
                navigateToParent();
                return;
            }

            File file = getFileFromNode(node);
            if (file != null) {
                if (file.isDirectory()) {
                    navigateToDirectory(file);
                } else if (file.isFile()) {
                    openFile(file.getAbsolutePath());
                    editorPane.requestFocusInWindow();
                }
            }
        }
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
    private File getFileFromNode(DefaultMutableTreeNode node) {
        Object[] pathParts = node.getUserObjectPath();
        if (pathParts.length == 0) return null;

        // Skip the root node (which contains the full path) and ".." entries
        String nodeName = node.getUserObject().toString();
        if (nodeName.equals("..")) {
            return currentRootDir.getParentFile();
        }

        // First element is the root path, rest are relative path components
        if (pathParts.length == 1) {
            // Root node selected
            return currentRootDir;
        }

        // Build path from root directory + relative path parts (skip first which is root path)
        StringBuilder pathBuilder = new StringBuilder(currentRootDir.getAbsolutePath());
        for (int i = 1; i < pathParts.length; i++) {
            String part = pathParts[i].toString();
            if (!part.equals("..")) {
                pathBuilder.append(File.separator).append(part);
            }
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

    private boolean handleSharedEditorShortcut(KeyEvent e, int keyCode) {
        boolean ctrlDown = e.isControlDown();

        if (EditorShortcut.TOGGLE_NERDTREE.matches(ctrlDown, keyCode)) {
            e.consume();
            toggleNerdTree();
            return true;
        }

        if (EditorShortcut.TOGGLE_TERMINAL.matches(ctrlDown, keyCode)) {
            e.consume();
            toggleTerminal();
            return true;
        }

        if (EditorShortcut.FOCUS_EDITOR.matches(ctrlDown, keyCode)) {
            e.consume();
            editorPane.requestFocusInWindow();
            return true;
        }

        if (EditorShortcut.RUN_JAVA_FOLDER.matches(ctrlDown, keyCode)) {
            e.consume();
            runJavaFolderFromCurrentFile();
            return true;
        }

        return false;
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
        private boolean consumeNextTyped = false;

        @Override
        public void keyTyped(KeyEvent e) {
            // Consume the typed character if we just switched modes
            if (consumeNextTyped) {
                e.consume();
                consumeNextTyped = false;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            char keyChar = e.getKeyChar();

            // Track if we're about to switch to INSERT mode
            if (currentMode == VimMode.NORMAL &&
                (keyChar == 'i' || keyChar == 'a' || keyChar == 'A' || keyChar == 'o')) {
                consumeNextTyped = true;
            }

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
            if (handleSharedEditorShortcut(e, keyCode)) return;

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
            if (handleSharedEditorShortcut(e, keyCode)) return;

            if (keyCode == KeyEvent.VK_ESCAPE) {
                e.consume();
                switchMode(VimMode.NORMAL);
            }
            // Let default typing behavior work
        }

        private void handleVisualMode(KeyEvent e, int keyCode, char keyChar) {
            if (handleSharedEditorShortcut(e, keyCode)) return;
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
            if (handleSharedEditorShortcut(e, keyCode)) return;
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

    private void updateFocusIndicator(String focus) {
        focusIndicator.setText(focus + " ");
    }

    private void executeCommand(String cmd) {
        EditorCommandParser.ParsedCommand command = EditorCommandParser.parse(cmd);
        switch (command.type()) {
            case QUIT:
                System.exit(0);
                break;
            case SAVE:
                saveFile();
                break;
            case SAVE_AND_QUIT:
                saveFile();
                System.exit(0);
                break;
            case SAVE_AS:
                currentFilePath = command.argument();
                saveFile();
                break;
            case OPEN:
                openFile(command.argument());
                break;
            case HELP:
                showHelp();
                break;
            case UNKNOWN:
                statusBar.setText(" Unknown command: " + command.argument());
                break;
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
                      "TERMINAL:\n" +
                      "  Ctrl+'      - Toggle integrated terminal\n" +
                      "  Ctrl+1      - Focus editor (from terminal)\n" +
                      "  Ctrl+C      - Interrupt running command\n" +
                      "  Ctrl+E      - Compile+run current Java file folder\n" +
                      "               (logs to ctrl-e-run.log in that folder)\n\n" +
                      "FILE EXPLORER:\n" +
                      "  Ctrl+N      - Toggle NerdTree file explorer\n" +
                      "  Tab         - Switch focus between tree and editor\n" +
                      "  Enter       - Open file or enter directory\n" +
                      "  ..          - Navigate to parent folder\n" +
                      "  Backspace   - Go to parent directory\n" +
                      "  -           - Go to parent directory\n" +
                      "  h           - Collapse folder or go to parent\n" +
                      "  l           - Expand folder or enter directory\n" +
                      "  j/k         - Navigate up/down in tree\n\n" +
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
                      "  Ctrl+E      - Compile+run current Java file folder\n" +
                      "  Ctrl+'      - Toggle terminal\n" +
                      "  Tab         - Focus file tree (if open)\n\n" +
                      "INSERT MODE:\n" +
                      "  ESC         - Return to NORMAL mode\n" +
                      "  Ctrl+N      - Toggle NerdTree\n" +
                      "  Ctrl+E      - Compile+run current Java file folder\n" +
                      "  Ctrl+'      - Toggle terminal\n" +
                      "  (Type freely)\n\n" +
                      "VISUAL MODE:\n" +
                      "  h, j, k, l  - Extend selection\n" +
                      "  d           - Delete selection\n" +
                      "  Ctrl+E      - Compile+run current Java file folder\n" +
                      "  ESC         - Return to NORMAL mode\n\n" +
                      "COMMAND MODE:\n" +
                      "  :w          - Save file\n" +
                      "  :w filename - Save as filename\n" +
                      "  :e filename - Open file\n" +
                      "  :q          - Quit application\n" +
                      "  :wq or :x   - Save and quit\n" +
                      "  :help       - Show this help\n" +
                      "  Ctrl+E      - Compile+run current Java file folder\n" +
                      "  ESC         - Return to NORMAL mode\n\n" +
                      "Press 'i' to edit, Ctrl+N for explorer, Ctrl+E to run Java folder...\n" +
                      "Ctrl+E also logs output to ctrl-e-run.log.\n";

        editorPane.setText(help);
        editorPane.setCaretPosition(0);
    }

    public static void main(String[] args) {
        final String fileToOpen = StartupArgsParser.extractFilename(args);

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
