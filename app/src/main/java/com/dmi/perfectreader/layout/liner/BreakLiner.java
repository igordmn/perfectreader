package com.dmi.perfectreader.layout.liner;

import com.dmi.perfectreader.layout.config.LayoutChars;
import com.dmi.util.cache.ReuseCache.Reuser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.dmi.util.cache.ReuseCache.reuseCollection;
import static com.dmi.util.cache.ReuseCache.reuser;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.max;

public class BreakLiner implements Liner {
    private final BreakFinder breakFinder;

    public BreakLiner(BreakFinder breakFinder) {
        this.breakFinder = breakFinder;
    }

    @Override
    public List<Line> makeLines(MeasuredText measuredText, Config config) {
        List<Line> lines = Reusables.lines();

        CharSequence text = measuredText.plainText();
        Locale locale = measuredText.locale();

        new Runnable() {
            LinePart part = new LinePart();             // содержит символы, которые уже точно будет содержать строка
            LinePart newPart = new LinePart();          // содержит символы из part, а также новые добавочные. необходима для проверки, вмещается ли новая строка

            @Override
            public void run() {
                part.reset(0, 0, config.firstLineIndent(), false);

                breakFinder.findBreaks(text, locale, br -> {
                    checkState(part.endIndex < br.index(), "Wrong line end index");

                    pushChars(br.index(), br.hasHyphen());

                    if (br.isForce()) {
                        pushLine(true);
                    }

                    checkState(part.endIndex == br.index(), "Wrong line end index");
                });

                pushLine(true);
            }

            void pushChars(int endIndex, boolean hasHyphenAfter) {
                newPart.reset(part.beginIndex, endIndex, part.indent, hasHyphenAfter);

                if (canUsePart(newPart)) {
                    useNewPart();
                } else {
                    pushLine();
                    pushCharsAtBeginOfLine(endIndex, hasHyphenAfter);
                }
            }

            private void pushCharsAtBeginOfLine(int endIndex, boolean hasHyphenAfter) {
                newPart.reset(part.beginIndex, endIndex, part.indent, hasHyphenAfter);

                if (canUsePart(newPart)) {
                    useNewPart();
                } else {
                    pushCharByChar(endIndex, hasHyphenAfter);
                }
            }

            private void pushCharByChar(int endIndex, boolean hasHyphenAfter) {
                for (int end = part.endIndex + 1; end <= endIndex; end++) {
                    boolean charHasHyphenAfter = end == endIndex && hasHyphenAfter;
                    newPart.reset(part.beginIndex, end, part.indent, charHasHyphenAfter);

                    if (!canUsePart(newPart)) {
                        pushLine();
                        newPart.reset(part.beginIndex, end, part.indent, charHasHyphenAfter);
                    }

                    useNewPart();
                }
            }

            private boolean canUsePart(LinePart newPart) {
                return newPart.left + newPart.width <= config.maxWidth() ||
                       newPart.left + newPart.width == part.left + part.width;
            }

            private void useNewPart() {
                LinePart temp = part;
                part = newPart;
                newPart = temp;
            }

            void pushLine() {
                pushLine(false);
            }

            void pushLine(boolean isLast) {
                if (!part.isEmpty()) {
                    LineImpl line = new LineImpl();
                    line.left = part.left;
                    line.width = part.width;
                    line.hasHyphenAfter = part.hasHyphenAfter;
                    line.isLast = isLast;
                    addTokensInto(line.tokens, part.beginIndex, part.endIndex);
                    lines.add(line);

                    part.reset(part.endIndex, part.endIndex, 0, false);
                }
            }

            private void addTokensInto(List<Token> tokens, int beginIndex, int endIndex) {
                TokenImpl token = null;
                for (int begin = beginIndex; begin < endIndex; begin++) {
                    char ch = text.charAt(begin);
                    boolean isSpace = isSpace(ch);
                    int end = begin + 1;

                    if (token == null || token.isSpace() != isSpace) {
                        token = new TokenImpl();
                        token.isSpace = isSpace;
                        token.beginIndex = begin;
                        token.endIndex = end;
                        tokens.add(token);
                    } else {
                        token.endIndex = end;
                    }
                }
            }

            private boolean isSpace(char ch) {
                return Character.isSpaceChar(ch) || Character.isWhitespace(ch);
            }

            class LinePart {
                public int beginIndex;
                public int endIndex;
                public float indent;
                public boolean hasHyphenAfter;

                public float left;
                public float width;

                public void reset(int beginIndex, int endIndex, float indent, boolean hasHyphenAfter) {
                    this.beginIndex = beginIndex;
                    this.endIndex = endIndex;
                    this.indent = indent;
                    this.hasHyphenAfter = hasHyphenAfter;

                    if (endIndex > beginIndex) {
                        int trailingSpacesBeginIndex = findTrailingSpacesBeginIndex(beginIndex, endIndex);
                        float symbolsWidth = measuredText.widthOf(beginIndex, trailingSpacesBeginIndex);
                        float hyphenWidth = hasHyphenAfter ? measuredText.hyphenWidthAfter(endIndex - 1) : 0;

                        float leftHang = config.leftHangFactor(text.charAt(beginIndex)) * measuredText.widthOf(beginIndex);
                        float rightHang = rightHang(beginIndex, trailingSpacesBeginIndex, hyphenWidth, hasHyphenAfter);

                        left = indent - leftHang;
                        width = max(0, symbolsWidth + hyphenWidth - rightHang);
                    } else {
                        left = 0;
                        width = 0;
                    }
                }

                private float rightHang(int beginIndex, int trailingSpacesBeginIndex, float hyphenWidth, boolean hasHyphenAfter) {
                    int lastIndex = trailingSpacesBeginIndex - 1;
                    if (lastIndex >= beginIndex) {
                        char lastChar = text.charAt(lastIndex);
                        return hasHyphenAfter ?
                               config.rightHangFactor(LayoutChars.HYPHEN) * hyphenWidth :
                               config.rightHangFactor(lastChar) * measuredText.widthOf(lastIndex);
                    } else {
                        return 0;
                    }
                }

                private int findTrailingSpacesBeginIndex(int beginIndex, int endIndex) {
                    for (int i = endIndex - 1; i >= beginIndex; i--) {
                        char ch = text.charAt(i);
                        if (!isSpace(ch)) {
                            return i + 1;
                        }
                    }
                    return beginIndex;
                }

                public boolean isEmpty() {
                    return endIndex == beginIndex;
                }
            }
        }.run();

        return lines;
    }

    private static class Reusables {
        private static final Reuser<ArrayList<Line>> lines = reuser(ArrayList::new);

        public static ArrayList<Line> lines() {
            return reuseCollection(lines);
        }
    }

    private static class LineImpl implements Line {
        public float left;
        public float width;
        public boolean hasHyphenAfter;
        public boolean isLast;
        public List<Token> tokens = new ArrayList<>();

        @Override
        public float left() {
            return left;
        }

        @Override
        public float width() {
            return width;
        }

        @Override
        public boolean hasHyphenAfter() {
            return hasHyphenAfter;
        }

        @Override
        public boolean isLast() {
            return isLast;
        }

        @Override
        public List<Token> tokens() {
            return tokens;
        }
    }

    private static class TokenImpl implements Token {
        public boolean isSpace;
        public int beginIndex;
        public int endIndex;

        @Override
        public boolean isSpace() {
            return isSpace;
        }

        @Override
        public int beginIndex() {
            return beginIndex;
        }

        @Override
        public int endIndex() {
            return endIndex;
        }
    }
}
