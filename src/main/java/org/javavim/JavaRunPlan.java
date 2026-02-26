package org.javavim;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable plan describing how to compile and run Java files.
 */
public record JavaRunPlan(
        Path workingDirectory,
        Path outputDirectory,
        String mainClassName,
        List<Path> sourceFiles
) {

    public JavaRunPlan {
        sourceFiles = List.copyOf(sourceFiles);
    }

    /**
     * Builds a javac command that compiles every source file.
     */
    public List<String> buildCompileCommand() {
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-d");
        command.add(outputDirectory.toString());
        for (Path sourceFile : sourceFiles) {
            command.add(sourceFile.toString());
        }
        return command;
    }

    /**
     * Builds a java command that runs the current file's class.
     */
    public List<String> buildRunCommand() {
        return List.of(
                "java",
                "-cp",
                outputDirectory.toString(),
                mainClassName
        );
    }
}
