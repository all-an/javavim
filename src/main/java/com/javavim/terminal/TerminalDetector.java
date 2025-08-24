package com.javavim.terminal;

/**
 * Detects terminal capabilities and environment information.
 * Follows single responsibility principle - handles terminal detection only.
 */
public class TerminalDetector {
    
    public boolean isTerminalAvailable() {
        return hasConsole();
    }
    
    public int getTerminalWidth() {
        String columns = System.getenv("COLUMNS");
        if (columns != null) {
            return parseInteger(columns, 80);
        }
        return 80; // Default width
    }
    
    public int getTerminalHeight() {
        String lines = System.getenv("LINES");
        if (lines != null) {
            return parseInteger(lines, 24);
        }
        return 24; // Default height
    }
    
    public boolean supportsColor() {
        String term = System.getenv("TERM");
        if (term == null) {
            return false;
        }
        return isColorCapableTerm(term);
    }
    
    public String getTerminalType() {
        String term = System.getenv("TERM");
        if (term != null) {
            return term;
        }
        return "unknown";
    }
    
    public boolean isInteractive() {
        return hasConsole() && !isRedirected();
    }
    
    private boolean hasConsole() {
        return System.console() != null;
    }
    
    private boolean isRedirected() {
        return System.getProperty("java.class.path").contains("maven");
    }
    
    private int parseInteger(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private boolean isColorCapableTerm(String term) {
        return term.contains("color") || term.contains("256") || term.equals("xterm");
    }
}