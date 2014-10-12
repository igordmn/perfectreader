package com.dmi.perfectreader.book.format.line;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public interface TextBreaker {
    BreakResult breakText(char[] chars, int begin, int end, int leftLimit, int rightLimit);

    class BreakResult {
        private final int index;
        private final boolean wordBroken;

        public BreakResult(int index, boolean wordBroken) {
            this.index = index;
            this.wordBroken = wordBroken;
        }

        public int index() {
            return index;
        }

        public boolean wordBroken() {
            return wordBroken;
        }
    }
}
