package org.javavim;

import java.io.IOException;
import java.nio.file.*;
import java.util.regex.*;

/**
 * Configuration manager for the VIM editor.
 * Reads settings from a JSON config file.
 */
public class Config {

    private int tabSize = 4;
    private int fontSize = 16;
    private boolean lineNumbers = true;

    // Default config file name
    private static final String CONFIG_FILE = "config.json";

    /**
     * Creates a Config with default values.
     */
    public Config() {
    }

    /**
     * Loads configuration from the default config.json file.
     * Looks in current directory first, then in the application directory.
     *
     * @return this Config instance for chaining
     */
    public Config load() {
        return load(findConfigFile());
    }

    /**
     * Loads configuration from a specific path.
     *
     * @param configPath path to the config file
     * @return this Config instance for chaining
     */
    public Config load(Path configPath) {
        if (configPath != null && Files.exists(configPath)) {
            try {
                String content = Files.readString(configPath);
                parseJson(content);
            } catch (IOException e) {
                System.err.println("Could not read config file: " + e.getMessage());
            }
        }
        return this;
    }

    /**
     * Loads configuration from a JSON string.
     *
     * @param jsonContent the JSON content to parse
     * @return this Config instance for chaining
     */
    public Config loadFromString(String jsonContent) {
        if (jsonContent != null && !jsonContent.isBlank()) {
            parseJson(jsonContent);
        }
        return this;
    }

    /**
     * Parses JSON content and extracts configuration values.
     * Uses simple regex parsing to avoid external dependencies.
     */
    private void parseJson(String content) {
        tabSize = parseIntValue(content, "tabSize", tabSize);
        fontSize = parseIntValue(content, "fontSize", fontSize);
        lineNumbers = parseBooleanValue(content, "lineNumbers", lineNumbers);
    }

    private int parseIntValue(String content, String key, int fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)").matcher(content);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return fallback;
    }

    private boolean parseBooleanValue(String content, String key, boolean fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)").matcher(content);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return fallback;
    }

    /**
     * Finds the config file in standard locations.
     */
    private Path findConfigFile() {
        // Check current directory
        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            return configPath;
        }

        // Try application directory
        try {
            String jarDir = Config.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath();
            configPath = Paths.get(jarDir).getParent().resolve(CONFIG_FILE);
            if (Files.exists(configPath)) {
                return configPath;
            }
        } catch (Exception e) {
            // Ignore, will use defaults
        }

        return null;
    }

    // Getters

    public int getTabSize() {
        return tabSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean isLineNumbers() {
        return lineNumbers;
    }

    // Setters (for testing)

    public Config setTabSize(int tabSize) {
        this.tabSize = tabSize;
        return this;
    }

    public Config setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Config setLineNumbers(boolean lineNumbers) {
        this.lineNumbers = lineNumbers;
        return this;
    }

    @Override
    public String toString() {
        return "Config{tabSize=" + tabSize + ", fontSize=" + fontSize + ", lineNumbers=" + lineNumbers + "}";
    }
}
