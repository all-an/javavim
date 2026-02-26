package org.javavim;

/**
 * Produces editor line-number text for the left gutter.
 */
public final class LineNumberFormatter {

    private LineNumberFormatter() {
    }

    public static int countLines(String text) {
        if (text == null || text.isEmpty()) {
            return 1;
        }
        return text.split("\n", -1).length;
    }

    public static String format(String text) {
        int lines = countLines(text);
        StringBuilder sb = new StringBuilder();
        int width = String.valueOf(lines).length();
        for (int i = 1; i <= lines; i++) {
            sb.append(String.format("%" + (width + 1) + "d ", i));
            if (i < lines) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
