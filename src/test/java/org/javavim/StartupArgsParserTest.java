package org.javavim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StartupArgsParserTest {

    @Test
    @DisplayName("Returns null when args are null")
    void returnsNullWhenArgsAreNull() {
        assertNull(StartupArgsParser.extractFilename(null));
    }

    @Test
    @DisplayName("Returns null when args are empty")
    void returnsNullWhenArgsAreEmpty() {
        assertNull(StartupArgsParser.extractFilename(new String[0]));
    }

    @Test
    @DisplayName("Returns first argument as filename")
    void returnsFirstArgumentAsFilename() {
        String[] args = {"file1.txt", "file2.txt"};
        assertEquals("file1.txt", StartupArgsParser.extractFilename(args));
    }

    @Test
    @DisplayName("Preserves blank first argument")
    void preservesBlankFirstArgument() {
        String[] args = {"", "fallback.txt"};
        assertEquals("", StartupArgsParser.extractFilename(args));
    }
}
