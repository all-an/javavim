package org.javavim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineNumberFormatterTest {

    @Test
    @DisplayName("Counts lines for null and empty content")
    void countsLinesForNullAndEmptyContent() {
        assertEquals(1, LineNumberFormatter.countLines(null));
        assertEquals(LineNumberFormatter.countLines(null), LineNumberFormatter.countLines(""));
    }

    @Test
    @DisplayName("Counts lines for multiline content with trailing newline")
    void countsLinesWithTrailingNewline() {
        String content = "line1\nline2\n";
        assertEquals(3, LineNumberFormatter.countLines(content));
    }

    @Test
    @DisplayName("Formats single line gutter")
    void formatsSingleLineGutter() {
        String content = "only one line";
        String lineNumbers = LineNumberFormatter.format(content);

        assertEquals(" 1 ", lineNumbers);
        assertEquals(1, lineNumbers.split("\n", -1).length);
    }

    @Test
    @DisplayName("Formats multiline gutter with consistent width")
    void formatsMultilineGutter() {
        String content = "a\nb\nc\nd\ne\nf\ng\nh\ni\nj";
        int lines = LineNumberFormatter.countLines(content);
        String lineNumbers = LineNumberFormatter.format(content);
        String[] rows = lineNumbers.split("\n", -1);

        assertEquals(lines, rows.length);
        assertEquals(String.format("%" + (String.valueOf(lines).length() + 1) + "d ", 1), rows[0]);
        assertEquals(String.format("%" + (String.valueOf(lines).length() + 1) + "d ", lines), rows[lines - 1]);
    }

    @Test
    @DisplayName("Formats empty content as line one")
    void formatsEmptyContentAsLineOne() {
        assertEquals(" 1 ", LineNumberFormatter.format(""));
    }
}
