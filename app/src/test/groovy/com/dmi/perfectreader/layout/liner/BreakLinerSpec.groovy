package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.config.LayoutChars
import com.dmi.perfectreader.layout.liner.Liner.Config
import com.dmi.perfectreader.layout.liner.Liner.Line
import com.dmi.perfectreader.layout.liner.Liner.MeasuredText
import com.dmi.perfectreader.layout.liner.Liner.Token
import com.dmi.perfectreader.layout.liner.breaker.Breaker
import com.dmi.perfectreader.layout.liner.breaker.Breaks
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

import static java.lang.Character.isUpperCase

class BreakLinerSpec extends Specification {
    final SYMBOL_WIDTH = 10
    final SPACE_WIDTH = 5
    final HYPHEN_WIDTH = 5

    def "break text into single line"() {
        given:
        def liner = new BreakLiner(breaker())
        def text = "single   line"

        when:
        def lines = makeLines(liner, text, config(300))

        then:
        tokenTextsOf(lines[0], text) == ["single", "   ", "line"]

        leftsOf(lines) == [0]
        widthsOf(lines) == [
                6 * SYMBOL_WIDTH + 3 * SPACE_WIDTH + 4 * SYMBOL_WIDTH
        ]
        hasHyphensAfterOf(lines) == [false]

        checkSpaces(lines, text)
    }

    def "break text into three lines"() {
        given:
        def liner = new BreakLiner(breaker())
        def text =
                "break   simple  " +
                "text into " +
                "three lines  "

        when:
        def lines = makeLines(liner, text, config(125))

        then:
        tokenTextsOf(lines[0], text) == ["break", "   ", "simple", "  "]
        tokenTextsOf(lines[1], text) == ["text", " ", "into", " "]
        tokenTextsOf(lines[2], text) == ["three", " ", "lines", "  "]

        leftsOf(lines) == [0, 0, 0]
        widthsOf(lines) == [
                5 * SYMBOL_WIDTH + 3 * SPACE_WIDTH + 6 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH,
        ]
        hasHyphensAfterOf(lines) == [false, false, false]

        checkSpaces(lines, text)
    }

    def "break between words"() {
        given:
        def liner = new BreakLiner(breaker())
        def text = "brEak be" +
                   "Tween  " +
                   "woRds, " +
                   "in wo" +
                   "Rdswords"

        when:
        def lines = makeLines(liner, text, config(80))

        then:
        tokenTextsOf(lines[0], text) == ["brEak", " ", "be"]
        tokenTextsOf(lines[1], text) == ["Tween", "  "]
        tokenTextsOf(lines[2], text) == ["woRds,", " "]
        tokenTextsOf(lines[3], text) == ["in", " ", "wo"]
        tokenTextsOf(lines[4], text) == ["Rdswords"]

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

        checkSpaces(lines, text)
    }

    def "break first long non-breaking word or word part"() {
        given:
        def liner = new BreakLiner(breaker())
        def text = "longw" +
                   "ord" +
                   "Longw" +
                   "ordso" +
                   "long " +
                   "longw" +
                   "ord"

        when:
        def lines = makeLines(liner, text, config(50))

        then:
        tokenTextsOf(lines[0], text) == ["longw"]
        tokenTextsOf(lines[1], text) == ["ord"]
        tokenTextsOf(lines[2], text) == ["Longw"]
        tokenTextsOf(lines[3], text) == ["ordso"]
        tokenTextsOf(lines[4], text) == ["long", " "]
        tokenTextsOf(lines[5], text) == ["longw"]
        tokenTextsOf(lines[6], text) == ["ord"]

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

        checkSpaces(lines, text)
    }

    def "break fully non-breaking text"() {
        given:
        def liner = new BreakLiner(breaker())
        def text = "longw" +
                   "ordlo" +
                   "ngwor" +
                   "dsolo" +
                   "nglon" +
                   "gword"

        when:
        def lines = makeLines(liner, text, config(50))

        then:
        tokenTextsOf(lines[0], text) == ["longw"]
        tokenTextsOf(lines[1], text) == ["ordlo"]
        tokenTextsOf(lines[2], text) == ["ngwor"]
        tokenTextsOf(lines[3], text) == ["dsolo"]
        tokenTextsOf(lines[4], text) == ["nglon"]
        tokenTextsOf(lines[5], text) == ["gword"]

        widthsOf(lines) == [
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
        ]
        hasHyphensAfterOf(lines) == [
                false,
                false,
                false,
                false,
                false,
                false,
        ]

        checkSpaces(lines, text)
    }

    def "don't break at non-breaking space but consider it as space"() {
        given:
        def liner = new BreakLiner(breaker())
        def text = "text\u00A0with\u00A0 non\u00A0break\u00A0ing spaces\u00A0\u00A0"

        when:
        def lines = makeLines(liner, text, config(85))

        then:
        tokenTextsOf(lines[0], text) == ["text", "\u00A0", "with", "\u00A0 "]
        tokenTextsOf(lines[1], text) == ["non", "\u00A0", "break", "\u00A0"]
        tokenTextsOf(lines[2], text) == ["ing", " "]  // break because long word
        tokenTextsOf(lines[3], text) == ["spaces", "\u00A0\u00A0"]

        widthsOf(lines) == [
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH,
                6 * SYMBOL_WIDTH,
        ]

        checkSpaces(lines, text)
    }

    def "indent first line"() {
        given:
        def liner = new BreakLiner(breaker())
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
        def liner = new BreakLiner(breaker())
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
        def liner = new BreakLiner(breaker())
        def text = "brEak wo" +
                   "Rd"

        when:
        def lines = makeLines(liner, text,
                config(75, 0, [:], [
                        (LayoutChars.HYPHEN): 1.0F,
                ])
        )

        then:
        tokenTextsOf(lines[0], text) == ["brEak", " ", "wo"]
        tokenTextsOf(lines[1], text) == ["Rd"]

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
        def liner = new BreakLiner(breaker())
        def text = "soMe  teXt,   \"teXt\"  q  "

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
        tokenTextsOf(lines[2], text) == ["M"]
        tokenTextsOf(lines[3], text) == ["e", "  "]
        tokenTextsOf(lines[4], text) == ["t"]
        tokenTextsOf(lines[5], text) == ["e"]
        tokenTextsOf(lines[6], text) == ["X"]
        tokenTextsOf(lines[7], text) == ["t"]
        tokenTextsOf(lines[8], text) == [",", "   "]
        tokenTextsOf(lines[9], text) == ["\""]
        tokenTextsOf(lines[10], text) == ["t"]
        tokenTextsOf(lines[11], text) == ["e"]
        tokenTextsOf(lines[12], text) == ["X"]
        tokenTextsOf(lines[13], text) == ["t"]
        tokenTextsOf(lines[14], text) == ["\"", "  "]
        tokenTextsOf(lines[15], text) == ["q", "  "]

        widthsOf(lines) == [
                SYMBOL_WIDTH,                        /* s */
                SYMBOL_WIDTH,                        /* o */
                SYMBOL_WIDTH,                        /* M */
                SYMBOL_WIDTH,                        /* e   */
                SYMBOL_WIDTH,                        /* t */
                SYMBOL_WIDTH,                        /* e */
                SYMBOL_WIDTH,                        /* X */
                SYMBOL_WIDTH,                        /* t */
                SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,  /* ,    */
                SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,  /* " */
                SYMBOL_WIDTH,                        /* t */
                SYMBOL_WIDTH,                        /* e */
                SYMBOL_WIDTH,                        /* X */
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
                false,  /* o */
                false,  /* m */
                false,  /* e   */
                false,  /* t */
                false,  /* e */
                false,  /* x */
                false,  /* t */
                false,  /* ,    */
                false,  /* " */
                false,  /* t */
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
        def liner = new BreakLiner(breaker())

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
            String getPlainText() {
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

    /**
     * Обычные переносы после пробелов и мягкие переносы перед большими буквами
     */
    def breaker() {
        new Breaker() {
            @Override
            Breaks breakText(@NotNull String text, @NotNull Locale locale) {
                return new Breaks() {
                    @Override
                    boolean hasBreakBefore(int index) {
                        return hasHyphenBefore(index) || isWordBegin(index)
                    }

                    private boolean isWordBegin(int index) {
                        return text.charAt(index) != ' ' as char && text.charAt(index - 1) == ' ' as char
                    }

                    @Override
                    boolean hasHyphenBefore(int index) {
                        return isUpperCase(text.charAt(index))
                    }
                }
            }
        }
    }

    def isSpaceChar(char ch) {
        return ch == ' ' as char || ch == '\n' as char || ch == '\u00A0' as char
    }
}
