package org.javavim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JavaRunPlanBuilderTest {

    @Test
    @DisplayName("Build fails for null and blank current file")
    void buildFailsForNullAndBlankCurrentFile() {
        JavaRunPlanBuildResult nullResult = JavaRunPlanBuilder.build(null);
        JavaRunPlanBuildResult blankResult = JavaRunPlanBuilder.build("   ");

        assertFalse(nullResult.isSuccess());
        assertFalse(blankResult.isSuccess());
        assertNotNull(nullResult.errorMessage());
        assertNotNull(blankResult.errorMessage());
    }

    @Test
    @DisplayName("Build fails for missing file")
    void buildFailsForMissingFile(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("Missing.java");
        JavaRunPlanBuildResult result = JavaRunPlanBuilder.build(missing.toString());

        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().contains("does not exist"));
    }

    @Test
    @DisplayName("Build fails for non-Java file")
    void buildFailsForNonJavaFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("notes.txt");
        Files.writeString(file, "hello");

        JavaRunPlanBuildResult result = JavaRunPlanBuilder.build(file.toString());
        assertFalse(result.isSuccess());
        assertTrue(result.errorMessage().contains(".java"));
    }

    @Test
    @DisplayName("Build creates compile and run plan for Java folder recursively")
    void buildCreatesPlanForJavaFolder(@TempDir Path tempDir) throws IOException {
        Path mainFile = tempDir.resolve("Main.java");
        Path helperDir = tempDir.resolve("util");
        Files.createDirectories(helperDir);
        Path helperFile = helperDir.resolve("Helper.java");

        Files.writeString(mainFile, """
                package demo.app;
                import demo.app.util.Helper;
                public class Main {
                    public static void main(String[] args) { System.out.println(Helper.message()); }
                }
                """);
        Files.writeString(helperFile, """
                package demo.app.util;
                public class Helper {
                    public static String message() { return "ok"; }
                }
                """);

        JavaRunPlanBuildResult result = JavaRunPlanBuilder.build(mainFile.toString());

        assertTrue(result.isSuccess());
        JavaRunPlan plan = result.plan();
        assertEquals(tempDir, plan.workingDirectory());
        assertEquals(tempDir.resolve(JavaRunPlanBuilder.OUTPUT_FOLDER_NAME), plan.outputDirectory());
        assertEquals("demo.app.Main", plan.mainClassName());

        List<Path> sourceFiles = plan.sourceFiles();
        assertTrue(sourceFiles.contains(mainFile));
        assertTrue(sourceFiles.contains(helperFile));

        List<String> compileCommand = plan.buildCompileCommand();
        assertEquals("javac", compileCommand.get(0));
        assertEquals("-d", compileCommand.get(1));
        assertEquals(plan.outputDirectory().toString(), compileCommand.get(2));
        assertTrue(compileCommand.contains(mainFile.toString()));
        assertTrue(compileCommand.contains(helperFile.toString()));

        List<String> runCommand = plan.buildRunCommand();
        assertEquals(List.of("java", "-cp", plan.outputDirectory().toString(), "demo.app.Main"), runCommand);
    }
}
