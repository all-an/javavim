package org.javavim;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates a compile/run plan for the currently opened Java file.
 */
public final class JavaRunPlanBuilder {

    static final String JAVA_EXTENSION = ".java";
    static final String OUTPUT_FOLDER_NAME = ".javavim-build";

    private JavaRunPlanBuilder() {
    }

    /**
     * Builds a plan from the current editor file path.
     */
    public static JavaRunPlanBuildResult build(String currentFilePath) {
        if (currentFilePath == null || currentFilePath.isBlank()) {
            return JavaRunPlanBuildResult.error(" No current file selected for Ctrl+E.");
        }

        Path sourceFile = JavaSourceMetadataParser.normalizePath(currentFilePath);
        if (!Files.exists(sourceFile) || !Files.isRegularFile(sourceFile)) {
            return JavaRunPlanBuildResult.error(" Current file does not exist: " + currentFilePath);
        }

        if (!isJavaFile(sourceFile)) {
            return JavaRunPlanBuildResult.error(" Ctrl+E works only for .java files.");
        }

        Path workingDirectory = sourceFile.getParent();
        if (workingDirectory == null) {
            return JavaRunPlanBuildResult.error(" Could not resolve file folder.");
        }

        try {
            List<Path> javaFiles = collectJavaFiles(workingDirectory);
            if (javaFiles.isEmpty()) {
                return JavaRunPlanBuildResult.error(" No Java files found in folder.");
            }

            Path outputDirectory = workingDirectory.resolve(OUTPUT_FOLDER_NAME);
            String mainClassName = JavaSourceMetadataParser.resolveMainClassName(sourceFile);
            JavaRunPlan plan = new JavaRunPlan(workingDirectory, outputDirectory, mainClassName, javaFiles);
            return JavaRunPlanBuildResult.success(plan);
        } catch (IOException e) {
            return JavaRunPlanBuildResult.error(" Failed to prepare Ctrl+E run: " + e.getMessage());
        }
    }

    private static boolean isJavaFile(Path sourceFile) {
        return sourceFile.getFileName().toString().endsWith(JAVA_EXTENSION);
    }

    private static List<Path> collectJavaFiles(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(JAVA_EXTENSION))
                    .sorted(Comparator.comparing(Path::toString))
                    .collect(Collectors.toList());
        }
    }
}
