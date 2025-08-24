package com.javavim.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommandLineParser Tests")
class CommandLineParserTest {

    private CommandLineParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandLineParser();
    }

    @Test
    @DisplayName("Should handle null arguments")
    void shouldHandleNullArguments() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(null);
        
        assertTrue(result.isEmpty());
        assertFalse(result.hasFile());
        assertFalse(result.isHelp());
        assertNull(result.getFilename());
        assertNull(result.getHelpMessage());
    }

    @Test
    @DisplayName("Should handle empty arguments array")
    void shouldHandleEmptyArgumentsArray() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(new String[0]);
        
        assertTrue(result.isEmpty());
        assertFalse(result.hasFile());
        assertFalse(result.isHelp());
        assertNull(result.getFilename());
        assertNull(result.getHelpMessage());
    }

    @Test
    @DisplayName("Should parse filename argument")
    void shouldParseFilenameArgument() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(new String[]{"test.java"});
        
        assertTrue(result.hasFile());
        assertFalse(result.isEmpty());
        assertFalse(result.isHelp());
        assertEquals("test.java", result.getFilename());
        assertNull(result.getHelpMessage());
    }

    @Test
    @DisplayName("Should handle help flag --help")
    void shouldHandleHelpFlagLongForm() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(new String[]{"--help"});
        
        assertTrue(result.isHelp());
        assertFalse(result.isEmpty());
        assertFalse(result.hasFile());
        assertNull(result.getFilename());
        assertNotNull(result.getHelpMessage());
        assertTrue(result.getHelpMessage().contains("JavaVim"));
        assertTrue(result.getHelpMessage().contains("USAGE:"));
    }

    @Test
    @DisplayName("Should handle help flag -h")
    void shouldHandleHelpFlagShortForm() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(new String[]{"-h"});
        
        assertTrue(result.isHelp());
        assertFalse(result.isEmpty());
        assertFalse(result.hasFile());
        assertNotNull(result.getHelpMessage());
    }

    @Test
    @DisplayName("Should handle help command")
    void shouldHandleHelpCommand() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(new String[]{"help"});
        
        assertTrue(result.isHelp());
        assertNotNull(result.getHelpMessage());
        assertTrue(result.getHelpMessage().contains("VIM COMMANDS:"));
    }

    @Test
    @DisplayName("Should handle file with path")
    void shouldHandleFileWithPath() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(new String[]{"/path/to/file.java"});
        
        assertTrue(result.hasFile());
        assertEquals("/path/to/file.java", result.getFilename());
    }

    @Test
    @DisplayName("Should handle multiple arguments but use only first")
    void shouldHandleMultipleArgumentsButUseOnlyFirst() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(new String[]{"file1.java", "file2.java", "file3.java"});
        
        assertTrue(result.hasFile());
        assertEquals("file1.java", result.getFilename());
    }

    @Test
    @DisplayName("Should provide comprehensive help message")
    void shouldProvideComprehensiveHelpMessage() {
        CommandLineParser.CommandLineResult result = parser.parseArguments(new String[]{"--help"});
        
        String help = result.getHelpMessage();
        assertNotNull(help);
        assertTrue(help.contains("JavaVim"));
        assertTrue(help.contains("USAGE:"));
        assertTrue(help.contains("EXAMPLES:"));
        assertTrue(help.contains("VIM COMMANDS:"));
        assertTrue(help.contains(":w"));
        assertTrue(help.contains(":q"));
        assertTrue(help.contains("insert mode"));
    }
}