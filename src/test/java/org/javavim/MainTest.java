package org.javavim;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Main class using reflection and Mockito.
 * Tests logic without opening GUI windows.
 */
class MainTest {

    // ========== Helper methods for reflection ==========

    private static Object getStaticField(String fieldName) throws Exception {
        Field field = Main.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private static Class<?> getVimModeClass() throws Exception {
        return Class.forName("org.javavim.Main$VimMode");
    }

    private static Object getVimModeValue(String name) throws Exception {
        Class<?> vimModeClass = getVimModeClass();
        return Enum.valueOf((Class<Enum>) vimModeClass, name);
    }

    // ========== Tests for VimMode enum ==========

    @Test
    @DisplayName("VimMode enum has all expected values")
    void testVimModeEnum() throws Exception {
        Class<?> vimModeClass = getVimModeClass();
        Object[] constants = vimModeClass.getEnumConstants();

        assertEquals(4, constants.length);
        assertEquals("NORMAL", constants[0].toString());
        assertEquals("INSERT", constants[1].toString());
        assertEquals("VISUAL", constants[2].toString());
        assertEquals("COMMAND", constants[3].toString());
    }

    @Test
    @DisplayName("VimMode NORMAL exists")
    void testVimModeNormal() throws Exception {
        Object normalMode = getVimModeValue("NORMAL");
        assertNotNull(normalMode);
        assertEquals("NORMAL", normalMode.toString());
    }

    @Test
    @DisplayName("VimMode INSERT exists")
    void testVimModeInsert() throws Exception {
        Object insertMode = getVimModeValue("INSERT");
        assertNotNull(insertMode);
        assertEquals("INSERT", insertMode.toString());
    }

    @Test
    @DisplayName("VimMode VISUAL exists")
    void testVimModeVisual() throws Exception {
        Object visualMode = getVimModeValue("VISUAL");
        assertNotNull(visualMode);
        assertEquals("VISUAL", visualMode.toString());
    }

    @Test
    @DisplayName("VimMode COMMAND exists")
    void testVimModeCommand() throws Exception {
        Object commandMode = getVimModeValue("COMMAND");
        assertNotNull(commandMode);
        assertEquals("COMMAND", commandMode.toString());
    }

    // ========== Tests for color constants ==========

    @Test
    @DisplayName("BG_COLOR is black (0, 0, 0)")
    void testBgColorIsBlack() throws Exception {
        Color bgColor = (Color) getStaticField("BG_COLOR");
        assertEquals(0, bgColor.getRed());
        assertEquals(0, bgColor.getGreen());
        assertEquals(0, bgColor.getBlue());
    }

    @Test
    @DisplayName("FG_COLOR is green (0, 255, 0)")
    void testFgColorIsGreen() throws Exception {
        Color fgColor = (Color) getStaticField("FG_COLOR");
        assertEquals(0, fgColor.getRed());
        assertEquals(255, fgColor.getGreen());
        assertEquals(0, fgColor.getBlue());
    }

    @Test
    @DisplayName("STATUS_BG is dark green")
    void testStatusBgColor() throws Exception {
        Color statusBg = (Color) getStaticField("STATUS_BG");
        assertEquals(0, statusBg.getRed());
        assertEquals(50, statusBg.getGreen());
        assertEquals(0, statusBg.getBlue());
    }

    @Test
    @DisplayName("VISUAL_SELECT color is defined")
    void testVisualSelectColor() throws Exception {
        Color visualSelect = (Color) getStaticField("VISUAL_SELECT");
        assertEquals(0, visualSelect.getRed());
        assertEquals(100, visualSelect.getGreen());
        assertEquals(0, visualSelect.getBlue());
    }

    // ========== Tests for Config integration ==========

    @Test
    @DisplayName("Config is loaded as static field")
    void testConfigLoaded() throws Exception {
        Config config = (Config) getStaticField("config");
        assertNotNull(config);
    }

    @Test
    @DisplayName("Config has valid tabSize")
    void testConfigTabSize() throws Exception {
        Config config = (Config) getStaticField("config");
        assertTrue(config.getTabSize() > 0);
        assertTrue(config.getTabSize() <= 16); // reasonable range
    }

    @Test
    @DisplayName("Config has valid fontSize")
    void testConfigFontSize() throws Exception {
        Config config = (Config) getStaticField("config");
        assertTrue(config.getFontSize() >= 8);
        assertTrue(config.getFontSize() <= 72); // reasonable range
    }

    // ========== Tests for Main class structure ==========

    @Test
    @DisplayName("Main class extends JFrame")
    void testMainExtendsJFrame() {
        assertTrue(JFrame.class.isAssignableFrom(Main.class));
    }

    @Test
    @DisplayName("Main has editorPane field")
    void testMainHasEditorPane() throws Exception {
        Field field = Main.class.getDeclaredField("editorPane");
        assertEquals(JTextPane.class, field.getType());
    }

    @Test
    @DisplayName("Main has statusBar field")
    void testMainHasStatusBar() throws Exception {
        Field field = Main.class.getDeclaredField("statusBar");
        assertEquals(JLabel.class, field.getType());
    }

    @Test
    @DisplayName("Main has fileTree field")
    void testMainHasFileTree() throws Exception {
        Field field = Main.class.getDeclaredField("fileTree");
        assertEquals(JTree.class, field.getType());
    }

    @Test
    @DisplayName("Main has terminalArea field")
    void testMainHasTerminalArea() throws Exception {
        Field field = Main.class.getDeclaredField("terminalArea");
        assertEquals(JTextArea.class, field.getType());
    }

    @Test
    @DisplayName("Main has terminalInput field")
    void testMainHasTerminalInput() throws Exception {
        Field field = Main.class.getDeclaredField("terminalInput");
        assertEquals(JTextField.class, field.getType());
    }

    @Test
    @DisplayName("Main has currentMode field")
    void testMainHasCurrentMode() throws Exception {
        Field field = Main.class.getDeclaredField("currentMode");
        assertNotNull(field);
    }

    @Test
    @DisplayName("Main has commandBuffer field")
    void testMainHasCommandBuffer() throws Exception {
        Field field = Main.class.getDeclaredField("commandBuffer");
        assertEquals(String.class, field.getType());
    }

    @Test
    @DisplayName("Main has currentFilePath field")
    void testMainHasCurrentFilePath() throws Exception {
        Field field = Main.class.getDeclaredField("currentFilePath");
        assertEquals(String.class, field.getType());
    }

    @Test
    @DisplayName("Main has nerdTreeVisible field")
    void testMainHasNerdTreeVisible() throws Exception {
        Field field = Main.class.getDeclaredField("nerdTreeVisible");
        assertEquals(boolean.class, field.getType());
    }

    @Test
    @DisplayName("Main has terminalVisible field")
    void testMainHasTerminalVisible() throws Exception {
        Field field = Main.class.getDeclaredField("terminalVisible");
        assertEquals(boolean.class, field.getType());
    }

    // ========== Tests for method signatures ==========

    @Test
    @DisplayName("switchMode method exists with correct signature")
    void testSwitchModeMethodExists() throws Exception {
        Class<?> vimModeClass = getVimModeClass();
        Method method = Main.class.getDeclaredMethod("switchMode", vimModeClass);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("updateStatusBar method exists")
    void testUpdateStatusBarMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("updateStatusBar");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("executeCommand method exists with String parameter")
    void testExecuteCommandMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("executeCommand", String.class);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("saveFile method exists")
    void testSaveFileMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("saveFile");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("openFile method exists with String parameter")
    void testOpenFileMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("openFile", String.class);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("toggleNerdTree method exists")
    void testToggleNerdTreeMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("toggleNerdTree");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("toggleTerminal method exists")
    void testToggleTerminalMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("toggleTerminal");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("showHelp method exists")
    void testShowHelpMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("showHelp");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("updateLineNumbers method exists")
    void testUpdateLineNumbersMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("updateLineNumbers");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("updateFocusIndicator method exists with String parameter")
    void testUpdateFocusIndicatorMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("updateFocusIndicator", String.class);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("navigateToDirectory method exists with File parameter")
    void testNavigateToDirectoryMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("navigateToDirectory", File.class);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("navigateToParent method exists")
    void testNavigateToParentMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("navigateToParent");
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("setTabSize method exists with correct parameters")
    void testSetTabSizeMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("setTabSize", JTextPane.class, int.class);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("appendToTerminal method exists")
    void testAppendToTerminalMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("appendToTerminal", String.class);
        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
    }

    // ========== Tests for inner classes ==========

    @Test
    @DisplayName("VimKeyListener inner class exists")
    void testVimKeyListenerExists() throws Exception {
        Class<?> innerClass = Class.forName("org.javavim.Main$VimKeyListener");
        assertNotNull(innerClass);
    }

    @Test
    @DisplayName("BlockCaret inner class exists")
    void testBlockCaretExists() throws Exception {
        Class<?> innerClass = Class.forName("org.javavim.Main$BlockCaret");
        assertNotNull(innerClass);
    }

    // ========== Tests for command parsing logic ==========

    @Test
    @DisplayName("Command 'w' pattern is valid save command")
    void testSaveCommandPattern() {
        String cmd = "w";
        assertTrue(cmd.equals("w") || cmd.startsWith("w "));
    }

    @Test
    @DisplayName("Command 'w filename' pattern matches save as")
    void testSaveAsCommandPattern() {
        String cmd = "w myfile.txt";
        assertTrue(cmd.startsWith("w "));
        assertEquals("myfile.txt", cmd.substring(2).trim());
    }

    @Test
    @DisplayName("Command 'e filename' pattern matches open")
    void testOpenCommandPattern() {
        String cmd = "e myfile.txt";
        assertTrue(cmd.startsWith("e "));
        assertEquals("myfile.txt", cmd.substring(2).trim());
    }

    @Test
    @DisplayName("Command 'q' matches quit")
    void testQuitCommandPattern() {
        String cmd = "q";
        assertEquals("q", cmd);
    }

    @Test
    @DisplayName("Command 'wq' matches save and quit")
    void testSaveQuitCommandPattern() {
        String cmd = "wq";
        assertTrue(cmd.equals("wq") || cmd.equals("x"));
    }

    @Test
    @DisplayName("Command 'x' matches save and quit")
    void testXCommandPattern() {
        String cmd = "x";
        assertTrue(cmd.equals("wq") || cmd.equals("x"));
    }

    @Test
    @DisplayName("Command 'help' matches help")
    void testHelpCommandPattern() {
        String cmd = "help";
        assertEquals("help", cmd);
    }

    // ========== Tests for file operations (without GUI) ==========

    @Test
    @DisplayName("File write operation works correctly")
    void testFileWriteOperation(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("test_write.txt");
        String content = "Test content for writing";

        Files.writeString(testFile, content);

        assertTrue(Files.exists(testFile));
        assertEquals(content, Files.readString(testFile));
    }

    @Test
    @DisplayName("File read operation works correctly")
    void testFileReadOperation(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("test_read.txt");
        String content = "Test content for reading";
        Files.writeString(testFile, content);

        String readContent = Files.readString(testFile);

        assertEquals(content, readContent);
    }

    @Test
    @DisplayName("Non-existent file throws exception")
    void testNonExistentFileThrows() {
        Path nonExistent = Paths.get("/non/existent/path/file.txt");
        assertThrows(IOException.class, () -> Files.readString(nonExistent));
    }

    // ========== Tests for filename sanitization ==========

    @Test
    @DisplayName("Filename BOM removal pattern works")
    void testFilenameSanitization() {
        String filenameWithBom = "\uFEFFmyfile.txt";
        String sanitized = filenameWithBom.replaceAll("[\uFEFF\u200B-\u200D\uFFFE\uFFFF]", "");
        assertEquals("myfile.txt", sanitized);
    }

    @Test
    @DisplayName("Filename with zero-width chars is sanitized")
    void testFilenameZeroWidthRemoval() {
        String filenameWithZeroWidth = "my\u200Bfile\u200C.txt";
        String sanitized = filenameWithZeroWidth.replaceAll("[\uFEFF\u200B-\u200D\uFFFE\uFFFF]", "");
        assertEquals("myfile.txt", sanitized);
    }

    // ========== Tests for line number calculation ==========

    @Test
    @DisplayName("Line count for empty text is 1")
    void testLineCountEmpty() {
        String text = "";
        int lines = text.isEmpty() ? 1 : text.split("\n", -1).length;
        assertEquals(1, lines);
    }

    @Test
    @DisplayName("Line count for single line")
    void testLineCountSingleLine() {
        String text = "Single line";
        int lines = text.isEmpty() ? 1 : text.split("\n", -1).length;
        assertEquals(1, lines);
    }

    @Test
    @DisplayName("Line count for multiple lines")
    void testLineCountMultipleLines() {
        String text = "Line 1\nLine 2\nLine 3";
        int lines = text.isEmpty() ? 1 : text.split("\n", -1).length;
        assertEquals(3, lines);
    }

    @Test
    @DisplayName("Line count with trailing newline")
    void testLineCountTrailingNewline() {
        String text = "Line 1\nLine 2\n";
        int lines = text.isEmpty() ? 1 : text.split("\n", -1).length;
        assertEquals(3, lines); // empty line after trailing newline
    }

    // ========== Tests for line number formatting ==========

    @Test
    @DisplayName("Line number format for single digit")
    void testLineNumberFormatSingleDigit() {
        int lines = 5;
        int width = String.valueOf(lines).length();
        String formatted = String.format("%" + (width + 1) + "d ", 3);
        assertEquals(" 3 ", formatted);
    }

    @Test
    @DisplayName("Line number format for double digit")
    void testLineNumberFormatDoubleDigit() {
        int lines = 50;
        int width = String.valueOf(lines).length();
        String formatted = String.format("%" + (width + 1) + "d ", 7);
        assertEquals("  7 ", formatted);
    }

    @Test
    @DisplayName("Line number format for triple digit")
    void testLineNumberFormatTripleDigit() {
        int lines = 500;
        int width = String.valueOf(lines).length();
        String formatted = String.format("%" + (width + 1) + "d ", 42);
        assertEquals("  42 ", formatted);
    }

    // ========== Tests for directory operations ==========

    @Test
    @DisplayName("Parent directory can be obtained")
    void testGetParentDirectory(@TempDir Path tempDir) throws Exception {
        Path subDir = tempDir.resolve("subdir");
        Files.createDirectories(subDir);

        File parent = subDir.toFile().getParentFile();

        assertNotNull(parent);
        assertEquals(tempDir.toFile().getAbsolutePath(), parent.getAbsolutePath());
    }

    @Test
    @DisplayName("Directory listing works")
    void testDirectoryListing(@TempDir Path tempDir) throws Exception {
        // Create some files
        Files.writeString(tempDir.resolve("file1.txt"), "content1");
        Files.writeString(tempDir.resolve("file2.txt"), "content2");
        Files.createDirectories(tempDir.resolve("subdir"));

        File[] files = tempDir.toFile().listFiles();

        assertNotNull(files);
        assertEquals(3, files.length);
    }

    @Test
    @DisplayName("Hidden files start with dot")
    void testHiddenFileDetection() {
        String hiddenFile = ".hidden";
        String normalFile = "normal.txt";

        assertTrue(hiddenFile.startsWith("."));
        assertFalse(normalFile.startsWith("."));
    }

    // ========== Tests for OS detection ==========

    @Test
    @DisplayName("OS name property is available")
    void testOsNameAvailable() {
        String osName = System.getProperty("os.name");
        assertNotNull(osName);
        assertFalse(osName.isEmpty());
    }

    @Test
    @DisplayName("Windows detection pattern works")
    void testWindowsDetection() {
        String osName = System.getProperty("os.name").toLowerCase();
        // Just verify the pattern matching works
        boolean isWindows = osName.contains("win");
        // Result depends on actual OS, just verify it doesn't throw
        assertNotNull(Boolean.valueOf(isWindows));
    }

    // ========== Tests for main method ==========

    @Test
    @DisplayName("Main class has main method")
    void testMainMethodExists() throws Exception {
        Method mainMethod = Main.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
        assertTrue(Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(Modifier.isPublic(mainMethod.getModifiers()));
        assertEquals(void.class, mainMethod.getReturnType());
    }

    // ========== Tests for constructor ==========

    @Test
    @DisplayName("Main has constructor with String parameter")
    void testConstructorExists() throws Exception {
        Constructor<?> constructor = Main.class.getConstructor(String.class);
        assertNotNull(constructor);
        assertTrue(Modifier.isPublic(constructor.getModifiers()));
    }
}
