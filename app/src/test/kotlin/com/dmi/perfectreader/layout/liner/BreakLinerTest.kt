package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.config.LayoutChars
import com.dmi.perfectreader.layout.liner.Liner.*
import com.dmi.perfectreader.layout.liner.breaker.Breaker
import com.dmi.perfectreader.layout.liner.breaker.Breaks
import com.dmi.util.shouldEquals
import org.junit.Test
import java.lang.Character.isUpperCase
import java.util.*

class BreakLinerTest {
    val SYMBOL_WIDTH = 10F
    val SPACE_WIDTH = 5F
    val HYPHEN_WIDTH = 5F

    @Test
    fun break_text_into_single_line() {
        // given
        val liner = BreakLiner(breaker())
        val text = "single   line"

        // when
        val lines = liner.makeLines(measuredText(text), config(300F))

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("single", "   ", "line")

        leftsOf(lines) shouldEquals listOf(0F)
        widthsOf(lines) shouldEquals listOf(
                6 * SYMBOL_WIDTH + 3 * SPACE_WIDTH + 4 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldEquals listOf(false)

        checkSpaces(lines, text)
    }

    @Test
    fun break_text_into_three_lines() {
        // given
        val liner = BreakLiner(breaker())
        val text =
                "break   simple  " +
                "text into " +
                "three lines  "

        // when
        val lines = liner.makeLines(measuredText(text), config(125F))

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("break", "   ", "simple", "  ")
        tokenTextsOf(lines[1], text) shouldEquals listOf("text", " ", "into", " ")
        tokenTextsOf(lines[2], text) shouldEquals listOf("three", " ", "lines", "  ")

        leftsOf(lines) shouldEquals listOf(0F, 0F, 0F)
        widthsOf(lines) shouldEquals listOf(
                5 * SYMBOL_WIDTH + 3 * SPACE_WIDTH + 6 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldEquals listOf(false, false, false)

        checkSpaces(lines, text)
    }

    @Test
    fun break_between_words() {
        // given
        val liner = BreakLiner(breaker())
        val text = "brEak be" +
                   "Tween  " +
                   "woRds, " +
                   "in wo" +
                   "Rdswords"

        // when
        val lines = liner.makeLines(measuredText(text), config(80F))

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("brEak", " ", "be")
        tokenTextsOf(lines[1], text) shouldEquals listOf("Tween", "  ")
        tokenTextsOf(lines[2], text) shouldEquals listOf("woRds,", " ")
        tokenTextsOf(lines[3], text) shouldEquals listOf("in", " ", "wo")
        tokenTextsOf(lines[4], text) shouldEquals listOf("Rdswords")

        widthsOf(lines) shouldEquals listOf(
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                5 * SYMBOL_WIDTH,
                6 * SYMBOL_WIDTH,
                2 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                8 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldEquals listOf(
                true,
                false,
                false,
                true,
                false
        )

        checkSpaces(lines, text)
    }

    @Test
    fun break_first_long_non_breaking_word_or_word_part() {
        // given
        val liner = BreakLiner(breaker())
        val text = "longw" +
                   "ord" +
                   "Longw" +
                   "ordso" +
                   "long " +
                   "longw" +
                   "ord"

        // when
        val lines = liner.makeLines(measuredText(text), config(50F))

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("longw")
        tokenTextsOf(lines[1], text) shouldEquals listOf("ord")
        tokenTextsOf(lines[2], text) shouldEquals listOf("Longw")
        tokenTextsOf(lines[3], text) shouldEquals listOf("ordso")
        tokenTextsOf(lines[4], text) shouldEquals listOf("long", " ")
        tokenTextsOf(lines[5], text) shouldEquals listOf("longw")
        tokenTextsOf(lines[6], text) shouldEquals listOf("ord")

        widthsOf(lines) shouldEquals listOf(
                5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldEquals listOf(
                false,
                true,
                false,
                false,
                false,
                false,
                false
        )

        checkSpaces(lines, text)
    }

    @Test
    fun break_fully_non_breaking_text() {
        // given
        val liner = BreakLiner(breaker())
        val text = "longw" +
                   "ordlo" +
                   "ngwor" +
                   "dsolo" +
                   "nglon" +
                   "gword"

        // when
        val lines = liner.makeLines(measuredText(text), config(50F))

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("longw")
        tokenTextsOf(lines[1], text) shouldEquals listOf("ordlo")
        tokenTextsOf(lines[2], text) shouldEquals listOf("ngwor")
        tokenTextsOf(lines[3], text) shouldEquals listOf("dsolo")
        tokenTextsOf(lines[4], text) shouldEquals listOf("nglon")
        tokenTextsOf(lines[5], text) shouldEquals listOf("gword")

        widthsOf(lines) shouldEquals listOf(
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldEquals listOf(
                false,
                false,
                false,
                false,
                false,
                false
        )

        checkSpaces(lines, text)
    }

    @Test
    fun don_not_break_at_non_breaking_space_but_consider_it_as_space() {
        // given
        val liner = BreakLiner(breaker())
        val text = "text\u00A0with\u00A0 non\u00A0break\u00A0ing spaces\u00A0\u00A0"

        // when
        val lines = liner.makeLines(measuredText(text), config(85F))

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("text", "\u00A0", "with", "\u00A0 ")
        tokenTextsOf(lines[1], text) shouldEquals listOf("non", "\u00A0", "break", "\u00A0")
        tokenTextsOf(lines[2], text) shouldEquals listOf("ing", " ")  // break because long word
        tokenTextsOf(lines[3], text) shouldEquals listOf("spaces", "\u00A0\u00A0")

        widthsOf(lines) shouldEquals listOf(
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH,
                6 * SYMBOL_WIDTH
        )

        checkSpaces(lines, text)
    }

    @Test
    fun indent_first_line() {
        // given
        val liner = BreakLiner(breaker())
        val text =
                "some text " +
                "a text text " +
                "text"

        // when
        val lines = liner.makeLines(measuredText(text), config(100F, 8F))

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("some", " ", "text", " ")
        tokenTextsOf(lines[1], text) shouldEquals listOf("a", " ", "text", " ", "text", " ")
        tokenTextsOf(lines[2], text) shouldEquals listOf("text")

        widthsOf(lines) shouldEquals listOf(
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                1 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH
        )
        leftsOf(lines) shouldEquals listOf(
                8F,
                0F,
                0F
        )

        checkSpaces(lines, text)
    }

    @Test
    fun hang_symbols() {
        // given
        val liner = BreakLiner(breaker())
        val text =
                " (some     (text)      text) " +
                "(with hyphenated symbols), " +
                "\"hyphenated, symbols\""

        // when
        val lines = liner.makeLines(measuredText(text),
                config(252.5F, 8F, mapOf(
                        '(' to 1.0F,
                        '"' to 0.5F
                ), mapOf(
                        ')' to 1.0F,
                        '"' to 0.5F,
                        ',' to 0.5F
                ))
        )

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf(" ", "(some", "     ", "(text)", "      ", "text)", " ")
        tokenTextsOf(lines[1], text) shouldEquals listOf("(with", " ", "hyphenated", " ", "symbols),", " ")
        tokenTextsOf(lines[2], text) shouldEquals listOf("\"hyphenated,", " ", "symbols\"")

        widthsOf(lines) shouldEquals listOf(
                1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH + 5 * SPACE_WIDTH + 6 * SYMBOL_WIDTH + 6 * SPACE_WIDTH + 5 * SYMBOL_WIDTH - 1.0F * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 10 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 9 * SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,
                12 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 8 * SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH
        )
        leftsOf(lines) shouldEquals listOf(
                8F,
                -1.0F * SYMBOL_WIDTH,
                0F - 0.5F * SYMBOL_WIDTH
        )

        checkSpaces(lines, text)
    }

    @Test
    fun hang_hyphen_symbol() {
        // given
        val liner = BreakLiner(breaker())
        val text = "brEak wo" +
                   "Rd"

        // when
        val lines = liner.makeLines(measuredText(text),
                config(75F, 0F, emptyMap(), mapOf(
                        LayoutChars.HYPHEN to 1.0F
                ))
        )

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("brEak", " ", "wo")
        tokenTextsOf(lines[1], text) shouldEquals listOf("Rd")

        widthsOf(lines) shouldEquals listOf(
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH - 1.0F * HYPHEN_WIDTH,
                2 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldEquals listOf(
                true,
                false
        )

        checkSpaces(lines, text)
    }

    @Test
    fun break_with_zero_width() {
        // given
        val liner = BreakLiner(breaker())
        val text = "soMe  teXt,   \"teXt\"  q  "

        // when
        val lines = liner.makeLines(measuredText(text),
                config(0F, 8F, mapOf(
                        '"' to 0.5F
                ), mapOf(
                        '"' to 0.5F,
                        ',' to 0.5F
                ))
        )

        // then
        tokenTextsOf(lines[0], text) shouldEquals listOf("s")
        tokenTextsOf(lines[1], text) shouldEquals listOf("o")
        tokenTextsOf(lines[2], text) shouldEquals listOf("M")
        tokenTextsOf(lines[3], text) shouldEquals listOf("e", "  ")
        tokenTextsOf(lines[4], text) shouldEquals listOf("t")
        tokenTextsOf(lines[5], text) shouldEquals listOf("e")
        tokenTextsOf(lines[6], text) shouldEquals listOf("X")
        tokenTextsOf(lines[7], text) shouldEquals listOf("t")
        tokenTextsOf(lines[8], text) shouldEquals listOf(",", "   ")
        tokenTextsOf(lines[9], text) shouldEquals listOf("\"")
        tokenTextsOf(lines[10], text) shouldEquals listOf("t")
        tokenTextsOf(lines[11], text) shouldEquals listOf("e")
        tokenTextsOf(lines[12], text) shouldEquals listOf("X")
        tokenTextsOf(lines[13], text) shouldEquals listOf("t")
        tokenTextsOf(lines[14], text) shouldEquals listOf("\"", "  ")
        tokenTextsOf(lines[15], text) shouldEquals listOf("q", "  ")

        widthsOf(lines) shouldEquals listOf(
                SYMBOL_WIDTH, /* s */
                SYMBOL_WIDTH, /* o */
                SYMBOL_WIDTH, /* M */
                SYMBOL_WIDTH, /* e   */
                SYMBOL_WIDTH, /* t */
                SYMBOL_WIDTH, /* e */
                SYMBOL_WIDTH, /* X */
                SYMBOL_WIDTH, /* t */
                SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH, /* ,    */
                SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH, /* " */
                SYMBOL_WIDTH, /* t */
                SYMBOL_WIDTH, /* e */
                SYMBOL_WIDTH, /* X */
                SYMBOL_WIDTH, /* t */
                SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH, /* "   */
                SYMBOL_WIDTH  /* q   */
        )
        leftsOf(lines) shouldEquals listOf(
                8F, /* s */
                0F, /* o */
                0F, /* m */
                0F, /* e   */
                0F, /* t */
                0F, /* e */
                0F, /* x */
                0F, /* t */
                0F, /* ,    */
                -0.5F * SYMBOL_WIDTH, /* " */
                0F, /* t */
                0F, /* e */
                0F, /* x */
                0F, /* t */
                -0.5F * SYMBOL_WIDTH, /* "   */
                0F  /* q   */
        )
        hasHyphensAfterOf(lines) shouldEquals listOf(
                false, /* s */
                false, /* o */
                false, /* m */
                false, /* e   */
                false, /* t */
                false, /* e */
                false, /* x */
                false, /* t */
                false, /* ,    */
                false, /* " */
                false, /* t */
                false, /* e */
                false, /* x */
                false, /* t */
                false, /* "   */
                false  /* q   */
        )

        checkSpaces(lines, text)
    }

    @Test
    fun make_for_empty_text() {
        // given
        val liner = BreakLiner(breaker())

        // when
        val lines = liner.makeLines(measuredText(""), config(300F))

        // then
        lines.size shouldEquals 0
    }

    fun checkSpaces(lines: List<Line>, text: String) {
        for (line in lines) {
            for (token in line.tokens) {
                var isSpace = isSpaceChar(text[token.beginIndex])
                for (i in token.beginIndex..token.endIndex - 1) {
                    isSpaceChar(text[token.beginIndex]) shouldEquals isSpace
                }
            }
        }
    }

    fun leftsOf(lines: List<Line>) = lines.map { it.left }
    fun widthsOf(lines: List<Line>) = lines.map { it.width }
    fun hasHyphensAfterOf(lines: List<Line>) = lines.map { it.hasHyphenAfter }
    fun tokenTextsOf(line: Line, text: String) = line.tokens.map { text.substring(it.beginIndex, it.endIndex) }

    fun measuredText(text: String) =
            object : MeasuredText {
                override val plainText = text
                override val locale = Locale.US

                override fun widthOf(index: Int) = if (isSpaceChar(text[index])) SPACE_WIDTH else SYMBOL_WIDTH

                override fun widthOf(beginIndex: Int, endIndex: Int): Float {
                    var width = 0F
                    for (i in beginIndex..endIndex - 1) {
                        width += widthOf(i)
                    }
                    return width
                }

                override fun hyphenWidthAfter(index: Int) = HYPHEN_WIDTH
            }


    fun config(
            maxWidth: Float, indent: Float = 0F,
            leftHangFactors: Map<Char, Float> = emptyMap(),
            rightHangFactors: Map<Char, Float> = emptyMap()
    ) = object : Config {
        override val firstLineIndent = indent
        override val maxWidth = maxWidth

        override fun leftHangFactor(ch: Char) = leftHangFactors[ch] ?: 0F
        override fun rightHangFactor(ch: Char) = rightHangFactors[ch] ?: 0F
    }

    /**
     * Обычные переносы после пробелов и мягкие переносы перед большими буквами
     */
    fun breaker() = object : Breaker {
        override fun breakText(text: String, locale: Locale) = object : Breaks {
            override fun hasBreakBefore(index: Int) = hasHyphenBefore(index) || isWordBegin(index)
            override fun hasHyphenBefore(index: Int) = isUpperCase(text[index])

            fun isWordBegin(index: Int) = text[index] != ' ' && text[index - 1] == ' '
        }
    }

    fun isSpaceChar(ch: Char) = ch == ' ' || ch == '\n' || ch == '\u00A0'
}
