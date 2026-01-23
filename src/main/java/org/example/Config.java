package org.example;

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
        // Parse tabSize
        Matcher m = Pattern.compile("\"tabSize\"\\s*:\\s*(\\d+)").matcher(content);
        if (m.find()) {
            tabSize = Integer.parseInt(m.group(1));
        }

        // Parse fontSize
        m = Pattern.compile("\"fontSize\"\\s*:\\s*(\\d+)").matcher(content);
        if (m.find()) {
            fontSize = Integer.parseInt(m.group(1));
        }

        // Parse lineNumbers
        m = Pattern.compile("\"lineNumbers\"\\s*:\\s*(true|false)").matcher(content);
        if (m.find()) {
            lineNumbers = Boolean.parseBoolean(m.group(1));
        }
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
