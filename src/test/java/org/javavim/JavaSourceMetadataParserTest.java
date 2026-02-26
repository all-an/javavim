package org.javavim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JavaSourceMetadataParserTest {

    @Test
    @DisplayName("Package extraction handles null, blank, and missing package")
    void packageExtractionEdgeCases() {
        assertEquals("", JavaSourceMetadataParser.extractPackageName(null));
        assertEquals("", JavaSourceMetadataParser.extractPackageName("   "));
        assertEquals("", JavaSourceMetadataParser.extractPackageName("public class Main {}"));
    }

    @Test
    @DisplayName("Package extraction handles valid package declaration")
    void packageExtractionValidCase() {
        String source = "package org.demo.app;\npublic class Main {}";
        assertEquals("org.demo.app", JavaSourceMetadataParser.extractPackageName(source));
    }

    @Test
    @DisplayName("Simple class name extraction removes .java suffix")
    void simpleClassNameExtraction() {
        Path path = Path.of("MyMain.java");
        assertEquals("MyMain", JavaSourceMetadataParser.extractSimpleClassName(path));
    }

    @Test
    @DisplayName("Main class resolution includes package when present")
    void mainClassResolutionWithPackage(@TempDir Path tempDir) throws IOException {
        Path sourceFile = tempDir.resolve("Main.java");
        Files.writeString(sourceFile, "package sample.run;\npublic class Main { public static void main(String[] args) {} }");

        String className = JavaSourceMetadataParser.resolveMainClassName(sourceFile);
        assertEquals("sample.run.Main", className);
    }

    @Test
    @DisplayName("Main class resolution without package uses simple class name")
    void mainClassResolutionWithoutPackage(@TempDir Path tempDir) throws IOException {
        Path sourceFile = tempDir.resolve("Main.java");
        Files.writeString(sourceFile, "public class Main { public static void main(String[] args) {} }");

        String className = JavaSourceMetadataParser.resolveMainClassName(sourceFile);
        assertEquals("Main", className);
    }

    @Test
    @DisplayName("Normalize path removes relative segments")
    void normalizePathRemovesRelativeSegments() {
        Path normalized = JavaSourceMetadataParser.normalizePath("a/./b/../c/Main.java");
        assertEquals(Path.of("a/c/Main.java"), normalized);
    }
}
