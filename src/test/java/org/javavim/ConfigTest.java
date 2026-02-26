package org.javavim;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    @DisplayName("Default values are set correctly")
    void testDefaultValues() {
        Config config = new Config();

        assertEquals(4, config.getTabSize());
        assertEquals(16, config.getFontSize());
        assertTrue(config.isLineNumbers());
    }

    @Test
    @DisplayName("Parse valid JSON with all fields")
    void testParseValidJson() {
        String json = """
            {
                "tabSize": 2,
                "fontSize": 14,
                "lineNumbers": false
            }
            """;

        Config config = new Config().loadFromString(json);

        assertEquals(2, config.getTabSize());
        assertEquals(14, config.getFontSize());
        assertFalse(config.isLineNumbers());
    }

    @Test
    @DisplayName("Parse JSON with only tabSize")
    void testParsePartialJsonTabSize() {
        String json = """
            {
                "tabSize": 8
            }
            """;

        Config config = new Config().loadFromString(json);

        assertEquals(8, config.getTabSize());
        assertEquals(16, config.getFontSize()); // default
        assertTrue(config.isLineNumbers()); // default
    }

    @Test
    @DisplayName("Parse JSON with only fontSize")
    void testParsePartialJsonFontSize() {
        String json = """
            {
                "fontSize": 20
            }
            """;

        Config config = new Config().loadFromString(json);

        assertEquals(4, config.getTabSize()); // default
        assertEquals(20, config.getFontSize());
        assertTrue(config.isLineNumbers()); // default
    }

    @Test
    @DisplayName("Parse JSON with extra whitespace")
    void testParseJsonWithWhitespace() {
        String json = """
            {
                "tabSize"  :   6  ,
                "fontSize" :  18
            }
            """;

        Config config = new Config().loadFromString(json);

        assertEquals(6, config.getTabSize());
        assertEquals(18, config.getFontSize());
    }

    @Test
    @DisplayName("Handle empty JSON")
    void testEmptyJson() {
        Config config = new Config().loadFromString("{}");

        assertEquals(4, config.getTabSize());
        assertEquals(16, config.getFontSize());
        assertTrue(config.isLineNumbers());
    }

    @Test
    @DisplayName("Ignore string-typed values and keep defaults")
    void testIgnoreStringTypedValues() {
        String json = """
            {
                "tabSize": "8",
                "fontSize": "20",
                "lineNumbers": "false"
            }
            """;

        Config config = new Config().loadFromString(json);

        assertEquals(4, config.getTabSize());
        assertEquals(16, config.getFontSize());
        assertTrue(config.isLineNumbers());
    }

    @Test
    @DisplayName("Numeric parser keeps current regex behavior on decimal-like input")
    void testRegexNumericParsingBehavior() {
        String json = """
            {
                "tabSize": -2,
                "fontSize": 12.5
            }
            """;

        Config config = new Config().loadFromString(json);

        assertEquals(4, config.getTabSize());
        assertEquals(12, config.getFontSize());
    }

    @Test
    @DisplayName("Apply valid values and ignore invalid ones in same payload")
    void testMixedValidAndInvalidValues() {
        String json = """
            {
                "tabSize": 2,
                "fontSize": "bad",
                "lineNumbers": false
            }
            """;

        Config config = new Config().loadFromString(json);

        assertEquals(2, config.getTabSize());
        assertEquals(16, config.getFontSize());
        assertFalse(config.isLineNumbers());
    }

    @Test
    @DisplayName("Handle null input")
    void testNullInput() {
        Config config = new Config().loadFromString(null);

        assertEquals(4, config.getTabSize());
        assertEquals(16, config.getFontSize());
        assertTrue(config.isLineNumbers());
    }

    @Test
    @DisplayName("Handle blank input")
    void testBlankInput() {
        Config config = new Config().loadFromString("   ");

        assertEquals(4, config.getTabSize());
        assertEquals(16, config.getFontSize());
        assertTrue(config.isLineNumbers());
    }

    @Test
    @DisplayName("Load from file")
    void testLoadFromFile(@TempDir Path tempDir) throws IOException {
        String json = """
            {
                "tabSize": 3,
                "fontSize": 12,
                "lineNumbers": true
            }
            """;

        Path configFile = tempDir.resolve("config.json");
        Files.writeString(configFile, json);

        Config config = new Config().load(configFile);

        assertEquals(3, config.getTabSize());
        assertEquals(12, config.getFontSize());
        assertTrue(config.isLineNumbers());
    }

    @Test
    @DisplayName("Handle non-existent file gracefully")
    void testNonExistentFile() {
        Path nonExistent = Paths.get("/non/existent/config.json");

        Config config = new Config().load(nonExistent);

        // Should use defaults
        assertEquals(4, config.getTabSize());
        assertEquals(16, config.getFontSize());
        assertTrue(config.isLineNumbers());
    }

    @Test
    @DisplayName("Setters work correctly")
    void testSetters() {
        Config config = new Config()
                .setTabSize(2)
                .setFontSize(20)
                .setLineNumbers(false);

        assertEquals(2, config.getTabSize());
        assertEquals(20, config.getFontSize());
        assertFalse(config.isLineNumbers());
    }

    @Test
    @DisplayName("toString returns expected format")
    void testToString() {
        Config config = new Config();
        String str = config.toString();

        assertTrue(str.contains("tabSize=4"));
        assertTrue(str.contains("fontSize=16"));
        assertTrue(str.contains("lineNumbers=true"));
    }
}
