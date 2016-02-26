package com.dmi.perfectreader.layout.wordbreak;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.CharArrayList;
import com.dmi.util.annotation.Reusable;
import com.dmi.util.cache.ReuseCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import static com.dmi.util.cache.ReuseCache.reuser;
import static com.google.common.io.CharStreams.readLines;
import static java.lang.Math.min;
import static java.util.Arrays.copyOf;

public class TeXHyphenator {
    /**
     * алгоритм: https://habrahabr.ru/post/138088/
     */

    private static final char EDGE_OF_WORD = '.';

    private final Patterns patterns;
    private final CharsLevels exceptions;

    private TeXHyphenator(Patterns patterns, CharsLevels exceptions) {
        this.patterns = patterns;
        this.exceptions = exceptions;
    }

    public WordBreaker.WordBreaks breakWord(CharSequence text, int beginIndex, int endIndex) {
        int length = endIndex - beginIndex;
        PatternLevels wordLevels = Reusables.wordLevels(length);

        if (length >= 3 && (patterns.count > 0 || exceptions.size() > 0)) {
            PatternChars chars = Reusables.wordChars();
            if (!applyException(text, beginIndex, endIndex, wordLevels, chars)) {
                applyPatterns(text, beginIndex, endIndex, wordLevels, chars);
            }
        }

        return Reusables.wordBreaks(wordLevels, beginIndex);
    }

    private boolean applyException(CharSequence text, int beginIndex, int endIndex, PatternLevels wordLevels, PatternChars chars) {
        chars.reset(text, beginIndex, endIndex, true, true);
        PatternLevels levels = exceptions.get(chars);
        if (levels != null) {
            levels.applyTo(wordLevels, 0);
            return true;
        } else {
            return false;
        }
    }

    private void applyPatterns(CharSequence text, int beginIndex, int endIndex, PatternLevels wordLevels, PatternChars chars) {
        for (int begin = beginIndex; begin < endIndex; begin++) {
            for (int end = min(endIndex, begin + patterns.maxLength()); end > begin; end--) {
                if (begin == beginIndex) {
                    applyLevels(text, beginIndex, wordLevels, chars, begin, end, true, false);
                }
                if (end == endIndex) {
                    applyLevels(text, beginIndex, wordLevels, chars, begin, end, false, true);
                }
                if (begin == beginIndex && end == endIndex) {
                    applyLevels(text, beginIndex, wordLevels, chars, begin, end, true, true);
                }
                applyLevels(text, beginIndex, wordLevels, chars, begin, end, false, false);
            }
        }
    }

    private void applyLevels(CharSequence text, int beginIndex, PatternLevels wordLevels, PatternChars chars, int begin, int end,
                             boolean atWordBegin, boolean atWordEnd
    ) {
        chars.reset(text, begin, end, atWordBegin, atWordEnd);
        PatternLevels levels = patterns.levelsFor(chars);
        if (levels != null) {
            levels.applyTo(wordLevels, begin - beginIndex);
        }
    }

    public static class Builder {
        private Patterns patterns = new Patterns();
        private CharsLevels exceptions = new CharsLevels();

        public Builder addPatternsFrom(InputStream is) throws IOException {
            List<String> patterns = readLines(new InputStreamReader(is));
            for (String pattern : patterns) {
                addPattern(pattern);
            }
            return this;
        }

        public Builder addExceptionsFrom(InputStream is) throws IOException {
            List<String> patterns = readLines(new InputStreamReader(is));
            for (String pattern : patterns) {
                addException(pattern);
            }
            return this;
        }

        public Builder addPattern(String pattern) {
            CharArrayList letters = new CharArrayList();
            PatternLevels levels = new PatternLevels();
            levels.resize(pattern.length() + 1);

            boolean atWordBegin = false;
            boolean atWordEnd = false;

            for (int i = 0; i < pattern.length(); i++) {
                char ch = pattern.charAt(i);
                if (ch == EDGE_OF_WORD) {
                    if (i == 0) {
                        atWordBegin = true;
                    } else if (i == pattern.length() - 1) {
                        atWordEnd = true;
                    }
                } else if ('0' <= ch && ch <= '9') {
                    levels.set(letters.size(), (byte) (ch - '0'));
                } else {
                    letters.add(ch);
                }
            }

            String patternStr = new String(letters.buffer, 0, letters.elementsCount);
            PatternChars patternChars = new PatternChars(patternStr, 0, patternStr.length(), atWordBegin, atWordEnd);

            levels.resize(letters.size() + 1);
            levels.trimToSize();

            patterns.put(patternChars, levels);

            return this;
        }

        public Builder addException(String exception) {
            CharArrayList letters = new CharArrayList();
            PatternLevels levels = new PatternLevels();
            levels.resize(exception.length() + 1);

            for (int i = 0; i < exception.length(); i++) {
                char ch = exception.charAt(i);
                if (ch == '-') {
                    levels.set(letters.size(), (byte) 9);
                } else {
                    letters.add(ch);
                }
            }

            String patternStr = new String(letters.buffer, 0, letters.elementsCount);
            PatternChars patternChars = new PatternChars(patternStr, 0, patternStr.length(), true, true);

            levels.resize(letters.size() + 1);
            levels.trimToSize();

            exceptions.put(patternChars, levels);

            return this;
        }

        public TeXHyphenator build() {
            return new TeXHyphenator(patterns, exceptions);
        }
    }

    private static class Patterns {
        private CharsLevels[] lengthToCharsLevels = new CharsLevels[16];
        private int maxLength = 0;
        public int count = 0;

        public void put(PatternChars chars, PatternLevels levels) {
            int length = chars.length;
            if (length > lengthToCharsLevels.length) {
                lengthToCharsLevels = copyOf(lengthToCharsLevels, length >> 1);
            }
            if (length > maxLength) {
                maxLength = length;
            }

            CharsLevels charsLevels = lengthToCharsLevels[length];
            if (charsLevels == null) {
                charsLevels = new CharsLevels();
                lengthToCharsLevels[length] = charsLevels;
            }

            charsLevels.put(chars, levels);
            count++;
        }

        public int maxLength() {
            return maxLength;
        }

        public PatternLevels levelsFor(PatternChars chars) {
            CharsLevels charsLevels = lengthToCharsLevels[chars.length];
            return charsLevels != null ? charsLevels.get(chars) : null;
        }
    }

    private static class CharsLevels extends HashMap<PatternChars, PatternLevels> {}

    @Reusable
    private static class PatternChars {
        private CharSequence str;
        private int begin;
        private int end;
        private boolean atWordBegin;
        private boolean atWordEnd;

        public int length;
        private int hashCode;

        public PatternChars() {
        }

        public PatternChars(CharSequence str, int begin, int end, boolean atWordBegin, boolean atWordEnd) {
            reset(str, begin, end, atWordBegin, atWordEnd);
        }

        public void reset(CharSequence str, int begin, int end, boolean atWordBegin, boolean atWordEnd) {
            this.str = str;
            this.begin = begin;
            this.end = end;
            this.atWordBegin = atWordBegin;
            this.atWordEnd = atWordEnd;
            this.length = end - begin;

            computeHashCode();
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object o) {
            PatternChars other = (PatternChars) o;

            if (other.length != length || other.atWordBegin != atWordBegin || other.atWordEnd != atWordEnd) {
                return false;
            }

            for (int i = begin, j = other.begin; i < end; i++, j++) {
                if (str.charAt(i) != other.str.charAt(j)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        private void computeHashCode() {
            hashCode = 0;
            for (int i = begin; i < end; ++i) {
                hashCode = 31 * hashCode + str.charAt(i);
            }
            hashCode = 31 * hashCode + (atWordBegin ? 1 : 0);
            hashCode = 31 * hashCode + (atWordEnd ? 1 : 0);
        }
    }

    @Reusable
    private static class PatternLevels extends ByteArrayList {
        void applyTo(PatternLevels levels, int beginIndex) {
            for (int i = 0, j = beginIndex; i < elementsCount && j < levels.elementsCount; i++, j++) {
                byte level = get(i);
                if (level > levels.get(j)) {
                    levels.set(j, level);
                }
            }
        }
    }

    @Reusable
    private static class WordBreaksImpl implements WordBreaker.WordBreaks {
        private PatternLevels levels;
        private int beginIndex;

        public void reset(PatternLevels levels, int beginIndex) {
            this.levels = levels;
            this.beginIndex = beginIndex;
        }

        @Override
        public boolean canBreakBefore(int index) {
            int wordIndex = index - beginIndex;
            // переносить одну букву нельзя
            boolean isMiddleBreak = wordIndex >= 2 && wordIndex < levels.elementsCount - 2;
            return isMiddleBreak && levels.get(wordIndex) % 2 != 0;
        }
    }

    private static class Reusables {
        private static final ReuseCache.Reuser<PatternChars> wordChars = reuser(PatternChars::new);
        private static final ReuseCache.Reuser<PatternLevels> wordLevels = reuser(PatternLevels::new);
        private static final ReuseCache.Reuser<WordBreaksImpl> wordBreaks = reuser(WordBreaksImpl::new);

        public static PatternChars wordChars() {
            return wordChars.reuse();
        }

        public static PatternLevels wordLevels(int wordLength) {
            PatternLevels value = wordLevels.reuse();
            value.clear();
            value.resize(wordLength + 1);
            return value;
        }

        public static WordBreaker.WordBreaks wordBreaks(PatternLevels levels, int beginIndex) {
            WordBreaksImpl value = wordBreaks.reuse();
            value.reset(levels, beginIndex);
            return value;
        }
    }
}
