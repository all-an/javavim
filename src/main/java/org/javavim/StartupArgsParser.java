package org.javavim;

/**
 * Extracts startup options passed to main(String[] args).
 */
public final class StartupArgsParser {

    private StartupArgsParser() {
    }

    public static String extractFilename(String[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        return args[0];
    }
}
