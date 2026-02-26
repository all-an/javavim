package org.javavim;

/**
 * Chooses which file path Ctrl+E should use.
 */
public final class CtrlEFileSelector {

    private CtrlEFileSelector() {
    }

    /**
     * Prefers the current editor file when it is Java, otherwise falls back to tree selection.
     */
    public static String selectJavaFilePath(String currentFilePath, String selectedTreeFilePath) {
        if (isJavaFilePath(currentFilePath)) {
            return currentFilePath;
        }
        if (isJavaFilePath(selectedTreeFilePath)) {
            return selectedTreeFilePath;
        }
        return currentFilePath;
    }

    static boolean isJavaFilePath(String path) {
        return path != null && !path.isBlank() && path.endsWith(".java");
    }
}
