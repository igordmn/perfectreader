package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.config.LayoutChars
import com.dmi.perfectreader.layout.liner.Liner.Config
import com.dmi.perfectreader.layout.liner.Liner.Line
import com.dmi.perfectreader.layout.liner.Liner.MeasuredText
import com.dmi.perfectreader.layout.liner.Liner.Token
import kotlin.Unit
import kotlin.jvm.functions.Function1
import spock.lang.Specification

class BreakLinerSpec extends Specification {
    final SYMBOL_WIDTH = 10
    final SPACE_WIDTH = 5
    final HYPHEN_WIDTH = 5

    def "break text into single line"() {
        given:
        def liner = new BreakLiner(spaceBreakFinder())
        def text = "single   line"

        when:
        def lines = makeLines(liner, text, config(300))

        then:
        leftsOf(lines) == [0]
        widthsOf(lines) == [
                6 * SYMBOL_WIDTH + 3 * SPACE_WIDTH + 4 * SYMBOL_WIDTH
        ]
        hasHyphensAfterOf(lines) == [false]

        tokenTextsOf(lines[0], text) == ["single", "   ", "line"]

        checkSpaces(lines, text)
    }

    def "break text into three lines"() {
        given:
        def liner = new BreakLiner(spaceBreakFinder())
        def text =
                "break   simple  " +
                "text into " +
                "three lines  "

        when:
        def lines = makeLines(liner, text, config(125))

        then:
        leftsOf(lines) == [0, 0, 0]
        widthsOf(lines) == [
                5 * SYMBOL_WIDTH + 3 * SPACE_WIDTH + 6 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH,
        ]
        hasHyphensAfterOf(lines) == [false, false, false]

        tokenTextsOf(lines[0], text) == ["break", "   ", "simple", "  "]
        tokenTextsOf(lines[1], text) == ["text", " ", "into", " "]
        tokenTextsOf(lines[2], text) == ["three", " ", "lines", "  "]

        checkSpaces(lines, text)
    }

    def "break between words"() {
        given:
        final BREAK_BEFORE_WORD_INDEX = 2
        def liner = new BreakLiner(spaceAndWordBreakFinder(BREAK_BEFORE_WORD_INDEX))
        def text = "break be" +
                   "tween  " +
                   "words, " +
                   "in wo" +
                   "rdswords"

        when:
        def lines = makeLines(liner, text, config(80))

        then:
        widthsOf(lines) == [
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                5 * SYMBOL_WIDTH,
                6 * SYMBOL_WIDTH,
                2 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                8 * SYMBOL_WIDTH,
        ]
        hasHyphensAfterOf(lines) == [
                true,
                false,
                false,
                true,
                false,
        ]

        tokenTextsOf(lines[0], text) == ["break", " ", "be"]
        tokenTextsOf(lines[1], text) == ["tween", "  "]
        tokenTextsOf(lines[2], text) == ["words,", " "]
        tokenTextsOf(lines[3], text) == ["in", " ", "wo"]
        tokenTextsOf(lines[4], text) == ["rdswords"]

        checkSpaces(lines, text)
    }

    def "break first long non-breaking word or word part"() {
        given:
        final BREAK_BEFORE_WORD_INDEX = 8
        def liner = new BreakLiner(spaceAndWordBreakFinder(BREAK_BEFORE_WORD_INDEX))
        def text = "longw" +
                   "ord" +
                   "longw" +
                   "ordso" +
                   "long " +
                   "longw" +
                   "ord"

        when:
        def lines = makeLines(liner, text, config(50))

        then:
        widthsOf(lines) == [
                5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH,
        ]
        hasHyphensAfterOf(lines) == [
                false,
                true,
                false,
                false,
                false,
                false,
                false,
        ]

        tokenTextsOf(lines[0], text) == ["longw"]
        tokenTextsOf(lines[1], text) == ["ord"]
        tokenTextsOf(lines[2], text) == ["longw"]
        tokenTextsOf(lines[3], text) == ["ordso"]
        tokenTextsOf(lines[4], text) == ["long", " "]
        tokenTextsOf(lines[5], text) == ["longw"]
        tokenTextsOf(lines[6], text) == ["ord"]

        checkSpaces(lines, text)
    }

    def "don't break at non-breaking space but consider it as space"() {
        given:
        def liner = new BreakLiner(spaceBreakFinder())
        def text = "text\u00A0with\u00A0 non\u00A0break\u00A0ing spaces\u00A0\u00A0"

        when:
        def lines = makeLines(liner, text, config(85))

        then:
        widthsOf(lines) == [
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH,
                6 * SYMBOL_WIDTH,
        ]

        tokenTextsOf(lines[0], text) == ["text", "\u00A0", "with", "\u00A0 "]
        tokenTextsOf(lines[1], text) == ["non", "\u00A0", "break", "\u00A0"]
        tokenTextsOf(lines[2], text) == ["ing", " "]  // break because long word
        tokenTextsOf(lines[3], text) == ["spaces", "\u00A0\u00A0"]
        
        checkSpaces(lines, text)
    }

    def "indent first line"() {
        given:
        def liner = new BreakLiner(spaceBreakFinder())
        def text =
                "some text " +
                "a text text " +
                "text"

        when:
        def lines = makeLines(liner, text, config(100, 8))

        then:
        tokenTextsOf(lines[0], text) == ["some", " ", "text", " "]
        tokenTextsOf(lines[1], text) == ["a", " ", "text", " ", "text", " "]
        tokenTextsOf(lines[2], text) == ["text"]

        widthsOf(lines) == [
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                1 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH,
        ]
        leftsOf(lines) == [
                8,
                0,
                0
        ]

        checkSpaces(lines, text)
    }

    def "hang symbols"() {
        given:
        def liner = new BreakLiner(spaceBreakFinder())
        def text =
                " (some     (text)      text) " +
                "(with hyphenated symbols), " +
                "\"hyphenated, symbols\""

        when:
        def lines = makeLines(liner, text,
                config(252.5, 8, [
                        ('(' as char): 1.0F,
                        ('"' as char): 0.5F,
                ], [
                        (')' as char)               : 1.0F,
                        ('"' as char)               : 0.5F,
                        (',' as char)               : 0.5F,
                ])
        )

        then:
        tokenTextsOf(lines[0], text) == [" ", "(some", "     ", "(text)", "      ", "text)", " "]
        tokenTextsOf(lines[1], text) == ["(with", " ", "hyphenated", " ", "symbols),", " "]
        tokenTextsOf(lines[2], text) == ["\"hyphenated,", " ", "symbols\""]

        widthsOf(lines) == [
                1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH + 5 * SPACE_WIDTH + 6 * SYMBOL_WIDTH + 6 * SPACE_WIDTH + 5 * SYMBOL_WIDTH - 1.0F * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 10 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 9 * SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,
                12 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 8 * SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,
        ]
        leftsOf(lines) == [
                8,
                -1.0F * SYMBOL_WIDTH,
                0 - 0.5F * SYMBOL_WIDTH
        ]

        checkSpaces(lines, text)
    }

    def "hang hyphen symbol"() {
        given:
        final BREAK_BEFORE_WORD_INDEX = 2
        def liner = new BreakLiner(spaceAndWordBreakFinder(BREAK_BEFORE_WORD_INDEX))
        def text = "break wo" + 
                   "rd"

        when:
        def lines = makeLines(liner, text,
                config(75, 0, [:], [
                        (LayoutChars.HYPHEN): 1.0F,
                ])
        )

        then:
        tokenTextsOf(lines[0], text) == ["break", " ", "wo"]
        tokenTextsOf(lines[1], text) == ["rd"]

        widthsOf(lines) == [
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH - 1.0F * HYPHEN_WIDTH,
                2 * SYMBOL_WIDTH,
        ]
        hasHyphensAfterOf(lines) == [
                true,
                false
        ]

        checkSpaces(lines, text)
    }

    def "break with zero width"() {
        given:
        final BREAK_BEFORE_WORD_INDEX = 2
        def liner = new BreakLiner(spaceAndWordBreakFinder(BREAK_BEFORE_WORD_INDEX))
        def text = "some  text,   \"text\"  q  "

        when:
        def lines = makeLines(liner, text,
                config(0, 8, [
                        ('"' as char): 0.5F,
                ], [
                        ('"' as char)               : 0.5F,
                        (',' as char)               : 0.5F,
                ])
        )

        then:
        tokenTextsOf(lines[0], text) == ["s"]
        tokenTextsOf(lines[1], text) == ["o"]
        tokenTextsOf(lines[2], text) == ["m"]
        tokenTextsOf(lines[3], text) == ["e", "  "]
        tokenTextsOf(lines[4], text) == ["t"]
        tokenTextsOf(lines[5], text) == ["e"]
        tokenTextsOf(lines[6], text) == ["x"]
        tokenTextsOf(lines[7], text) == ["t"]
        tokenTextsOf(lines[8], text) == [",", "   "]
        tokenTextsOf(lines[9], text) == ["\""]
        tokenTextsOf(lines[10], text) == ["t"]
        tokenTextsOf(lines[11], text) == ["e"]
        tokenTextsOf(lines[12], text) == ["x"]
        tokenTextsOf(lines[13], text) == ["t"]
        tokenTextsOf(lines[14], text) == ["\"", "  "]
        tokenTextsOf(lines[15], text) == ["q", "  "]

        widthsOf(lines) == [
                SYMBOL_WIDTH,                        /* s */
                SYMBOL_WIDTH + HYPHEN_WIDTH,         /* o */
                SYMBOL_WIDTH,                        /* m */
                SYMBOL_WIDTH,                        /* e   */
                SYMBOL_WIDTH,                        /* t */
                SYMBOL_WIDTH + HYPHEN_WIDTH,         /* e */
                SYMBOL_WIDTH,                        /* x */
                SYMBOL_WIDTH,                        /* t */
                SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,  /* ,    */
                SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,  /* " */
                SYMBOL_WIDTH + HYPHEN_WIDTH,         /* t */
                SYMBOL_WIDTH,                        /* e */
                SYMBOL_WIDTH,                        /* x */
                SYMBOL_WIDTH,                        /* t */
                SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,  /* "   */
                SYMBOL_WIDTH,                        /* q   */
        ]
        leftsOf(lines) == [
                8,                        /* s */
                0,                        /* o */
                0,                        /* m */
                0,                        /* e   */
                0,                        /* t */
                0,                        /* e */
                0,                        /* x */
                0,                        /* t */
                0,                        /* ,    */
                -0.5F * SYMBOL_WIDTH,     /* " */
                0,                        /* t */
                0,                        /* e */
                0,                        /* x */
                0,                        /* t */
                -0.5F * SYMBOL_WIDTH,     /* "   */
                0,                        /* q   */
        ]
        hasHyphensAfterOf(lines) == [
                false,  /* s */
                true,   /* o */
                false,  /* m */
                false,  /* e   */
                false,  /* t */
                true,   /* e */
                false,  /* x */
                false,  /* t */
                false,  /* ,    */
                false,  /* " */
                true,   /* t */
                false,  /* e */
                false,  /* x */
                false,  /* t */
                false,  /* "   */
                false,  /* q   */
        ]

        checkSpaces(lines, text)
    }

    def "make for empty text"() {
        given:
        def liner = new BreakLiner(spaceBreakFinder())

        when:
        def lines = makeLines(liner, "", config(300))

        then:
        lines.size() == 0
    }

    void checkSpaces(lines, text) {
        for (line in lines) {
            for (token in line.tokens) {
                boolean isSpace = isSpaceChar(text.charAt(token.beginIndex))
                for (int i = token.beginIndex + 1; i < token.endIndex; i++) {
                    assert isSpaceChar(text.charAt(token.beginIndex)) == isSpace
                }
            }
        }
    }

    def makeLines(BreakLiner liner, String text, Config config) {
        def lines = []
        def resultLines = liner.makeLines(measuredText(text), config)
        for (Line resultLine : resultLines) {
            lines.add(wrapLine(resultLine))
        }
        return lines
    }

    def wrapLine(Line line) {
        return [
                left: line.left,
                width: line.width,
                hasHyphenAfter: line.hasHyphenAfter,
                tokens: wrapTokens(line.tokens)
        ]
    }

    def wrapTokens(List<Token> tokens) {
        def wrapped = []
        for (int i = 0; i < tokens.size(); i++) {
            wrapped.add(wrapToken(tokens.get(i)))
        }
        return wrapped
    }

    def wrapToken(Token token) {
        return [
                isSpace: token.space,
                beginIndex: token.beginIndex,
                endIndex: token.endIndex,
        ]
    }

    def leftsOf(lines) {
        return lines.collect({ it.left })
    }

    def widthsOf(lines) {
        return lines.collect({ it.width })
    }

    def hasHyphensAfterOf(lines) {
        return lines.collect({ it.hasHyphenAfter })
    }

    def isLastOf(lines) {
        return lines.collect({ it.isLast })
    }

    def tokenTextsOf(lines, text) {
        return lines.tokens.collect({ text.substring(it.beginIndex, it.endIndex) })
    }

    def isSpaceOf(lines) {
        return lines.tokens.collect({ it.isSpace })
    }

    def measuredText(String text) {
        return new MeasuredText() {
            @Override
            CharSequence getPlainText() {
                return text
            }

            @Override
            Locale getLocale() {
                return Locale.US
            }

            @Override
            float widthOf(int index) {
                return isSpaceChar(text.charAt(index)) ? SPACE_WIDTH : SYMBOL_WIDTH
            }

            @Override
            float widthOf(int beginIndex, int endIndex) {
                float width = 0
                for (int i = beginIndex; i < endIndex; i++) {
                    width += widthOf(i)
                }
                return width
            }

            @Override
            float hyphenWidthAfter(int index) {
                return HYPHEN_WIDTH
            }
        }
    }

    def config(float maxWidth, float indent = 0,
               Map<Character, Float> leftHangFactors = [:], Map<Character, Float> rightHangFactors = [:]) {
        return new Config() {
            @Override
            float getFirstLineIndent() {
                return indent
            }

            @Override
            float getMaxWidth() {
                return maxWidth
            }

            @Override
            float leftHangFactor(char ch) {
                return leftHangFactors.get(ch) ?: 0.0F
            }

            @Override
            float rightHangFactor(char ch) {
                return rightHangFactors.get(ch) ?: 0.0F
            }
        }
    }

    def spaceBreakFinder() {
        return new BreakFinder() {
            @Override
            void findBreaks(CharSequence text, Locale locale, Function1<? super BreakFinder.Break, Unit> accept) {
                for (int i = 1; i < text.length(); i++) {
                    if (text.charAt(i - 1) == '\n' as char) {
                        accept.invoke(br(i, false))
                    } else if (text.charAt(i - 1) == ' ' as char && text.charAt(i) != ' ' as char) {
                        accept.invoke(br(i, false))
                    }
                }
                if (text.length() > 0)
                    accept.invoke(br(text.length(), false))
            }
        }
    }

    def spaceAndWordBreakFinder(int breakBeforeWordIndex) {
        return new BreakFinder() {
            @Override
            void findBreaks(CharSequence text, Locale locale, Function1<? super BreakFinder.Break, Unit> accept) {
                int wordBeginIndex = 0
                for (int i = 1; i < text.length(); i++) {
                    int wordIndex = i - wordBeginIndex
                    if (text.charAt(i - 1) == '\n' as char) {
                        accept.invoke(br(i, false))
                    } else if (text.charAt(i - 1) == ' ' as char && text.charAt(i) != ' ' as char) {
                        accept.invoke(br(i, false))
                    } else if (wordIndex == breakBeforeWordIndex) {
                        accept.invoke(br(i, true))
                    }

                    if (isSpaceChar(text.charAt(i))) {
                        wordBeginIndex = i + 1
                    }
                }
                accept.invoke(br(text.length(), false))
            }
        }
    }

    def isSpaceChar(char ch) {
        return ch == ' ' as char || ch == '\n' as char || ch == '\u00A0' as char
    }

    def br(index, hasHyphen) {
        def br = new BreakFinder.Break()
        br.setIndex(index)
        br.setHasHyphen(hasHyphen)
        return br
    }
}
