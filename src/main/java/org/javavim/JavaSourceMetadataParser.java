package org.javavim;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses package/class metadata from Java source files.
 */
public final class JavaSourceMetadataParser {

    private static final Pattern PACKAGE_PATTERN =
            Pattern.compile("(?m)^\\s*package\\s+([a-zA-Z_][\\w.]*)\\s*;");

    private JavaSourceMetadataParser() {
    }

    /**
     * Resolves the fully-qualified main class name for a Java source file.
     */
    public static String resolveMainClassName(Path sourceFile) throws IOException {
        String packageName = extractPackageName(java.nio.file.Files.readString(sourceFile));
        String className = extractSimpleClassName(sourceFile);
        return packageName.isEmpty() ? className : packageName + "." + className;
    }

    /**
     * Extracts package name from source code, or empty string if absent.
     */
    public static String extractPackageName(String sourceCode) {
        if (sourceCode == null || sourceCode.isBlank()) {
            return "";
        }
        Matcher matcher = PACKAGE_PATTERN.matcher(sourceCode);
        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * Extracts simple class name from source file path.
     */
    public static String extractSimpleClassName(Path sourceFile) {
        String fileName = sourceFile.getFileName().toString();
        return stripJavaExtension(fileName);
    }

    private static String stripJavaExtension(String fileName) {
        return fileName.endsWith(".java")
                ? fileName.substring(0, fileName.length() - 5)
                : fileName;
    }

    /**
     * Resolves a path string into a normalized Path.
     */
    public static Path normalizePath(String rawPath) {
        return Paths.get(rawPath).normalize();
    }
}
