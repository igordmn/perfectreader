package com.dmi.perfectreader.book.format.line;

public class SpaceTextBreaker implements TextBreaker {
    @Override
    public BreakResult breakText(char[] chars, int begin, int end, int leftLimit, int rightLimit) {
        if (end == rightLimit) {
            return new BreakResult(end, false);
        }
        for (int i = begin; i < end; i++) {
            if (chars[i] == '\n') {
                return new BreakResult(i + 1, false);
            }
        }
        for (int i = end - 1; i >= (end + begin) / 2; i--) {
            if (chars[i] == ' ') {
                return new BreakResult(i + 1, false);
            }
        }
        return new BreakResult(end, true);
    }
}
