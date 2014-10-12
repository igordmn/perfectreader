package com.dmi.perfectreader.book.format.line;

public class BreakConfig {
    private final char[] blankChars;
    private final char hyphenChar;

    public BreakConfig(char[] blankChars, char hyphenChar) {
        this.blankChars = blankChars;
        this.hyphenChar = hyphenChar;
    }

    public char[] blankChars() {
        return blankChars;
    }

    public char hyphenChar() {
        return hyphenChar;
    }
}
