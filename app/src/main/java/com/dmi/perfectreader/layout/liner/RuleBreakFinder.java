package com.dmi.perfectreader.layout.liner;

import com.dmi.perfectreader.layout.config.LayoutChars;
import com.dmi.perfectreader.layout.wordbreak.WordBreaker;
import com.dmi.perfectreader.layout.wordbreak.WordBreaker.WordBreaks;
import com.dmi.util.annotation.Reusable;
import com.dmi.util.cache.ReuseCache.Reuser;
import com.dmi.util.text.CharSequenceCharacterIterator;

import java.text.BreakIterator;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import java8.util.function.Consumer;

import static com.dmi.util.cache.ReuseCache.reuseBooleanArray;
import static com.dmi.util.cache.ReuseCache.reuser;
import static com.google.common.base.Preconditions.checkArgument;

public class RuleBreakFinder implements BreakFinder {
    private final WordBreaker wordBreaker;

    public RuleBreakFinder(WordBreaker wordBreaker) {this.wordBreaker = wordBreaker;}

    @Override
    public void findBreaks(CharSequence text, Locale locale, Consumer<Break> consumer) {
        Breaks breaks = new Breaks(text.length());
        addLineBreaks(text, locale, breaks);
        addWordBreaks(text, locale, breaks);
        addObjectBreaks(text, breaks);
        breaks.forEach(consumer);
    }

    private void addLineBreaks(CharSequence text, Locale locale, Breaks breaks) {
        BreakIterator it = BreakIterator.getLineInstance(locale);
        it.setText(new CharSequenceCharacterIterator(text));
        it.first();
        for (int i = it.next(); i != text.length(); i = it.next()) {
            breaks.set(
                    i,
                    false,
                    isLineBreakingChar(text.charAt(i - 1))
            );
        }
    }

    private void addWordBreaks(CharSequence text, Locale locale, Breaks breaks) {
        BreakIterator it = BreakIterator.getWordInstance(locale);
        it.setText(new CharSequenceCharacterIterator(text));
        int begin = it.first();
        for (int i = it.next(); i != BreakIterator.DONE; i = it.next()) {
            for (int end = begin + 1; end <= i; end++) {
                if (end == i || breaks.isBreak(end)) {
                    WordBreaks wordBreaks = wordBreaker.breakWord(text, locale, begin, end);
                    addWordBreaks(text, wordBreaks, begin, end, breaks);
                    begin = end;
                }
            }
        }
    }

    private void addWordBreaks(CharSequence text, WordBreaks wordBreaks, int begin, int end, Breaks breaks) {
        for (int i = begin + 1; i <= end - 1; i++) {
            if (wordBreaks.canBreakBefore(i)) {
                breaks.set(
                        i,
                        true,
                        isLineBreakingChar(text.charAt(i - 1))
                );
            }
        }
    }

    private void addObjectBreaks(CharSequence text, Breaks breaks) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == LayoutChars.OBJECT_REPLACEMENT_CHARACTER) {
                breaks.set(
                        i,
                        false,
                        isLineBreakingChar(text.charAt(i - 1))
                );
                if (i + 1 < text.length()) {
                    breaks.set(
                            i + 1,
                            false,
                            isLineBreakingChar(text.charAt(i))
                    );
                }
            }
        }
    }

    private boolean isLineBreakingChar(char ch) {
        return ch == '\n' || ch == '\r' || ch == '\u000B' || ch == '\u000C' || ch == '\u0085' || ch == '\u2028' || ch == '\u2029';
    }

    private static class Breaks {
        private int length;
        private boolean[] isBreak;
        private boolean[] hasHyphen;
        private boolean[] isForce;

        private final BreakImpl br = new BreakImpl();

        public Breaks(int length) {
            this.length = length;
            isBreak = Reusables.isBreak(length);
            hasHyphen = Reusables.hasHyphen(length);
            isForce = Reusables.isForce(length);

            Arrays.fill(isBreak, false);
            Arrays.fill(hasHyphen, false);
            Arrays.fill(isForce, false);
        }

        public void set(int index, boolean hasHyphen, boolean isForce) {
            checkArgument(index < length);
            this.isBreak[index] = true;
            this.hasHyphen[index] = hasHyphen;
            this.isForce[index] = isForce;
        }

        public boolean isBreak(int index) {
            checkArgument(index < length);
            return isBreak[index];
        }

        public void forEach(Consumer<Break> consumer) {
            for (int i = 0; i < length; i++) {
                if (isBreak[i]) {
                    br.index = i;
                    br.hasHyphen = hasHyphen[i];
                    br.isForce = isForce[i];
                    consumer.accept(br);
                }
            }
            br.index = length;
            br.hasHyphen = false;
            br.isForce = false;
            consumer.accept(br);
        }
    }

    @Reusable
    private static class BreakImpl implements Break {
        public int index;
        public boolean hasHyphen;
        public boolean isForce;

        @Override
        public int index() {
            return index;
        }

        @Override
        public boolean hasHyphen() {
            return hasHyphen;
        }

        @Override
        public boolean isForce() {
            return isForce;
        }
    }

    private static class Reusables {
        private static final int INITIAL_CHARS_CAPACITY = 4000;

        private static Reuser<AtomicReference<boolean[]>> isBreak = reuser(() -> new AtomicReference<>(new boolean[INITIAL_CHARS_CAPACITY]));
        private static Reuser<AtomicReference<boolean[]>> hasHyphen = reuser(() -> new AtomicReference<>(new boolean[INITIAL_CHARS_CAPACITY]));
        private static Reuser<AtomicReference<boolean[]>> isForce = reuser(() -> new AtomicReference<>(new boolean[INITIAL_CHARS_CAPACITY]));

        public static boolean[] isBreak(int capacity) {
            return reuseBooleanArray(isBreak, capacity);
        }

        public static boolean[] hasHyphen(int capacity) {
            return reuseBooleanArray(hasHyphen, capacity);
        }

        public static boolean[] isForce(int capacity) {
            return reuseBooleanArray(isForce, capacity);
        }
    }
}
