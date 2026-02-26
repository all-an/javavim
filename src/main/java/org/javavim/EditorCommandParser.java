package org.javavim;

import java.util.regex.Pattern;

/**
 * Parses VIM-style command mode inputs (without the leading ':').
 */
public final class EditorCommandParser {

    private static final Pattern INVISIBLE_FILENAME_CHARS =
            Pattern.compile("[\\uFEFF\\u200B-\\u200D\\uFFFE\\uFFFF]");

    private EditorCommandParser() {
    }

    public enum CommandType {
        QUIT,
        SAVE,
        SAVE_AND_QUIT,
        SAVE_AS,
        OPEN,
        HELP,
        UNKNOWN
    }

    public record ParsedCommand(CommandType type, String argument) {
    }

    public static ParsedCommand parse(String rawCommand) {
        String cmd = rawCommand == null ? "" : rawCommand.trim();

        if (cmd.equals("q")) {
            return new ParsedCommand(CommandType.QUIT, null);
        }
        if (cmd.equals("w")) {
            return new ParsedCommand(CommandType.SAVE, null);
        }
        if (cmd.equals("wq") || cmd.equals("x")) {
            return new ParsedCommand(CommandType.SAVE_AND_QUIT, null);
        }
        if (cmd.startsWith("w ")) {
            String filename = sanitizeFilename(cmd.substring(2).trim());
            return new ParsedCommand(CommandType.SAVE_AS, filename);
        }
        if (cmd.startsWith("e ")) {
            String filename = sanitizeFilename(cmd.substring(2).trim());
            return new ParsedCommand(CommandType.OPEN, filename);
        }
        if (cmd.equals("help")) {
            return new ParsedCommand(CommandType.HELP, null);
        }

        return new ParsedCommand(CommandType.UNKNOWN, cmd);
    }

    static String sanitizeFilename(String filename) {
        if (filename == null) {
            return "";
        }
        return INVISIBLE_FILENAME_CHARS.matcher(filename).replaceAll("");
    }
}
