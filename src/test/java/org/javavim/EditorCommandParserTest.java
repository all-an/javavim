package org.javavim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditorCommandParserTest {

    @Test
    @DisplayName("Parses quit command")
    void parsesQuitCommand() {
        EditorCommandParser.ParsedCommand command = EditorCommandParser.parse("q");
        assertEquals(EditorCommandParser.CommandType.QUIT, command.type());
        assertNull(command.argument());
    }

    @Test
    @DisplayName("Parses save command")
    void parsesSaveCommand() {
        EditorCommandParser.ParsedCommand command = EditorCommandParser.parse("w");
        assertEquals(EditorCommandParser.CommandType.SAVE, command.type());
        assertNull(command.argument());
    }

    @Test
    @DisplayName("Parses save and quit aliases")
    void parsesSaveAndQuitAliases() {
        EditorCommandParser.ParsedCommand wq = EditorCommandParser.parse("wq");
        EditorCommandParser.ParsedCommand x = EditorCommandParser.parse("x");

        assertEquals(EditorCommandParser.CommandType.SAVE_AND_QUIT, wq.type());
        assertEquals(wq.type(), x.type());
    }

    @Test
    @DisplayName("Parses help command with surrounding spaces")
    void parsesHelpWithSpaces() {
        EditorCommandParser.ParsedCommand command = EditorCommandParser.parse("   help   ");
        assertEquals(EditorCommandParser.CommandType.HELP, command.type());
    }

    @Test
    @DisplayName("Parses save-as command and sanitizes filename")
    void parsesSaveAsAndSanitizesFilename() {
        String dirtyFilename = "\uFEFFmy\u200Bfile\u200C.txt";
        String expectedFilename = EditorCommandParser.sanitizeFilename(dirtyFilename);

        EditorCommandParser.ParsedCommand command = EditorCommandParser.parse("w " + dirtyFilename);

        assertEquals(EditorCommandParser.CommandType.SAVE_AS, command.type());
        assertEquals(expectedFilename, command.argument());
    }

    @Test
    @DisplayName("Parses open command with trimmed filename")
    void parsesOpenCommandWithTrimmedFilename() {
        String rawFilename = "   readme.md   ";
        String expectedFilename = EditorCommandParser.sanitizeFilename(rawFilename.trim());

        EditorCommandParser.ParsedCommand command = EditorCommandParser.parse("e " + rawFilename);

        assertEquals(EditorCommandParser.CommandType.OPEN, command.type());
        assertEquals(expectedFilename, command.argument());
    }

    @Test
    @DisplayName("Save command with trailing spaces is still save")
    void saveCommandWithTrailingSpaces() {
        EditorCommandParser.ParsedCommand command = EditorCommandParser.parse("w     ");
        assertEquals(EditorCommandParser.CommandType.SAVE, command.type());
        assertNull(command.argument());
    }

    @Test
    @DisplayName("Unknown command preserves trimmed text")
    void unknownCommandPreservesTrimmedText() {
        EditorCommandParser.ParsedCommand command = EditorCommandParser.parse("   nope   ");
        assertEquals(EditorCommandParser.CommandType.UNKNOWN, command.type());
        assertEquals("nope", command.argument());
    }

    @Test
    @DisplayName("Null and blank commands are unknown empty commands")
    void nullAndBlankCommands() {
        EditorCommandParser.ParsedCommand nullCommand = EditorCommandParser.parse(null);
        EditorCommandParser.ParsedCommand blankCommand = EditorCommandParser.parse("   ");

        assertEquals(EditorCommandParser.CommandType.UNKNOWN, nullCommand.type());
        assertEquals(nullCommand.type(), blankCommand.type());
        assertEquals("", nullCommand.argument());
        assertEquals(nullCommand.argument(), blankCommand.argument());
    }

    @Test
    @DisplayName("Sanitize filename removes invisible characters")
    void sanitizeFilenameRemovesInvisibleChars() {
        String sanitized = EditorCommandParser.sanitizeFilename("a\u200Bb\u200Cc\u200Dd\uFEFFe\uFFFEf\uFFFF");
        assertEquals("abcdef", sanitized);
    }
}
