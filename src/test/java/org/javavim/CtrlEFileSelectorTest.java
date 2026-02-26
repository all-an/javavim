package org.javavim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CtrlEFileSelectorTest {

    @Test
    @DisplayName("Uses current file when current file is Java")
    void usesCurrentJavaFile() {
        String selected = CtrlEFileSelector.selectJavaFilePath("src/Main.java", "src/Other.java");
        assertEquals("src/Main.java", selected);
    }

    @Test
    @DisplayName("Falls back to selected tree file when current file is null")
    void fallsBackToTreeWhenCurrentIsNull() {
        String selected = CtrlEFileSelector.selectJavaFilePath(null, "src/TreeFile.java");
        assertEquals("src/TreeFile.java", selected);
    }

    @Test
    @DisplayName("Falls back to selected tree file when current file is not Java")
    void fallsBackToTreeWhenCurrentIsNotJava() {
        String selected = CtrlEFileSelector.selectJavaFilePath("README.md", "src/TreeFile.java");
        assertEquals("src/TreeFile.java", selected);
    }

    @Test
    @DisplayName("Keeps current value when neither path is Java")
    void keepsCurrentWhenNeitherIsJava() {
        String selected = CtrlEFileSelector.selectJavaFilePath("README.md", "notes.txt");
        assertEquals("README.md", selected);
    }

    @Test
    @DisplayName("Java path helper validates all input parameter types")
    void javaPathHelperValidatesInputs() {
        assertTrue(CtrlEFileSelector.isJavaFilePath("A.java"));
        assertFalse(CtrlEFileSelector.isJavaFilePath("A.jav"));
        assertFalse(CtrlEFileSelector.isJavaFilePath(""));
        assertFalse(CtrlEFileSelector.isJavaFilePath("   "));
        assertFalse(CtrlEFileSelector.isJavaFilePath(null));
    }
}
