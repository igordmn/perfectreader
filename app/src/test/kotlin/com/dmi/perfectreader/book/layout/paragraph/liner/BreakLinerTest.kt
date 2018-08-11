package com.dmi.perfectreader.book.layout.paragraph.liner

import com.dmi.util.text.Chars
import com.dmi.perfectreader.book.layout.paragraph.breaker.Breaker
import com.dmi.perfectreader.book.layout.paragraph.breaker.Breaks
import com.dmi.perfectreader.book.layout.paragraph.breaker.NoneBreaks
import com.dmi.perfectreader.book.layout.paragraph.liner.Liner.*
import com.dmi.test.shouldBe
import org.junit.Test
import java.lang.Character.isUpperCase
import java.util.*

@Suppress("IllegalIdentifier")
class BreakLinerTest {
    val SYMBOL_WIDTH = 10F
    val SPACE_WIDTH = 5F
    val HYPHEN_WIDTH = 5F

    @Test
    fun `break text into single line`() {
        // given
        val liner = BreakLiner(breaker())
        val text = "single   line"

        // when
        val lines = liner.makeLines(measuredText(text), config(300F))

        // then
        tokenTextsOf(lines[0], text) shouldBe listOf("single", "   ", "line")

        leftsOf(lines) shouldBe listOf(0F)
        widthsOf(lines) shouldBe listOf(
                6 * SYMBOL_WIDTH + 3 * SPACE_WIDTH + 4 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldBe listOf(false)

        checkSpaces(lines, text)
    }

    @Test
    fun `break text into three lines`() {
        // given
        val liner = BreakLiner(breaker())
        val text =
                "break   simple  " +
                "text into " +
                "three lines  "

        // when
        val lines = liner.makeLines(measuredText(text), config(125F))

        // then
        tokenTextsOf(lines[0], text) shouldBe listOf("break", "   ", "simple", "  ")
        tokenTextsOf(lines[1], text) shouldBe listOf("text", " ", "into", " ")
        tokenTextsOf(lines[2], text) shouldBe listOf("three", " ", "lines", "  ")

        leftsOf(lines) shouldBe listOf(0F, 0F, 0F)
        widthsOf(lines) shouldBe listOf(
                5 * SYMBOL_WIDTH + 3 * SPACE_WIDTH + 6 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldBe listOf(false, false, false)

        checkSpaces(lines, text)
    }

    @Test
    fun `break between words`() {
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
        tokenTextsOf(lines[0], text) shouldBe listOf("brEak", " ", "be")
        tokenTextsOf(lines[1], text) shouldBe listOf("Tween", "  ")
        tokenTextsOf(lines[2], text) shouldBe listOf("woRds,", " ")
        tokenTextsOf(lines[3], text) shouldBe listOf("in", " ", "wo")
        tokenTextsOf(lines[4], text) shouldBe listOf("Rdswords")

        widthsOf(lines) shouldBe listOf(
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                5 * SYMBOL_WIDTH,
                6 * SYMBOL_WIDTH,
                2 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                8 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldBe listOf(
                true,
                false,
                false,
                true,
                false
        )

        checkSpaces(lines, text)
    }

    @Test
    fun `break first long non-breaking word or word part`() {
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
        tokenTextsOf(lines[0], text) shouldBe listOf("longw")
        tokenTextsOf(lines[1], text) shouldBe listOf("ord")
        tokenTextsOf(lines[2], text) shouldBe listOf("Longw")
        tokenTextsOf(lines[3], text) shouldBe listOf("ordso")
        tokenTextsOf(lines[4], text) shouldBe listOf("long", " ")
        tokenTextsOf(lines[5], text) shouldBe listOf("longw")
        tokenTextsOf(lines[6], text) shouldBe listOf("ord")

        widthsOf(lines) shouldBe listOf(
                5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH + HYPHEN_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldBe listOf(
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
    fun `break fully non-breaking text`() {
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
        tokenTextsOf(lines[0], text) shouldBe listOf("longw")
        tokenTextsOf(lines[1], text) shouldBe listOf("ordlo")
        tokenTextsOf(lines[2], text) shouldBe listOf("ngwor")
        tokenTextsOf(lines[3], text) shouldBe listOf("dsolo")
        tokenTextsOf(lines[4], text) shouldBe listOf("nglon")
        tokenTextsOf(lines[5], text) shouldBe listOf("gword")

        widthsOf(lines) shouldBe listOf(
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldBe listOf(
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
    fun `don't break at non-breaking space but consider it as space`() {
        // given
        val liner = BreakLiner(breaker())
        val text = "text\u00A0with\u00A0 non\u00A0break\u00A0ing spaces\u00A0\u00A0"

        // when
        val lines = liner.makeLines(measuredText(text), config(85F))

        // then
        tokenTextsOf(lines[0], text) shouldBe listOf("text", "\u00A0", "with", "\u00A0 ")
        tokenTextsOf(lines[1], text) shouldBe listOf("non", "\u00A0", "break", "\u00A0")
        tokenTextsOf(lines[2], text) shouldBe listOf("ing", " ")  // break because long word
        tokenTextsOf(lines[3], text) shouldBe listOf("spaces", "\u00A0\u00A0")

        widthsOf(lines) shouldBe listOf(
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH,
                3 * SYMBOL_WIDTH,
                6 * SYMBOL_WIDTH
        )

        checkSpaces(lines, text)
    }

    @Test
    fun `indent first line`() {
        // given
        val liner = BreakLiner(breaker())
        val text =
                "some text " +
                "a text text " +
                "text"

        // when
        val lines = liner.makeLines(measuredText(text), config(100F, 8F))

        // then
        tokenTextsOf(lines[0], text) shouldBe listOf("some", " ", "text", " ")
        tokenTextsOf(lines[1], text) shouldBe listOf("a", " ", "text", " ", "text", " ")
        tokenTextsOf(lines[2], text) shouldBe listOf("text")

        widthsOf(lines) shouldBe listOf(
                4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                1 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 4 * SYMBOL_WIDTH,
                4 * SYMBOL_WIDTH
        )
        leftsOf(lines) shouldBe listOf(
                8F,
                0F,
                0F
        )

        checkSpaces(lines, text)
    }

    @Test
    fun `hang symbols`() {
        // given
        val liner = BreakLiner(breaker())
        val text =
                " (some     (text)      text) " +
                "(with hyphenated symbols), " +
                "\"hyphenated, symbols\""

        // when
        val lines = liner.makeLines(measuredText(text),
                config(252.5F, 8F, true, mapOf(
                        '(' to 1.0F,
                        '"' to 0.5F
                ), mapOf(
                        ')' to 1.0F,
                        '"' to 0.5F,
                        ',' to 0.5F
                ))
        )

        // then
        tokenTextsOf(lines[0], text) shouldBe listOf(" ", "(some", "     ", "(text)", "      ", "text)", " ")
        tokenTextsOf(lines[1], text) shouldBe listOf("(with", " ", "hyphenated", " ", "symbols),", " ")
        tokenTextsOf(lines[2], text) shouldBe listOf("\"hyphenated,", " ", "symbols\"")

        widthsOf(lines) shouldBe listOf(
                1 * SPACE_WIDTH + 5 * SYMBOL_WIDTH + 5 * SPACE_WIDTH + 6 * SYMBOL_WIDTH + 6 * SPACE_WIDTH + 5 * SYMBOL_WIDTH - 1.0F * SYMBOL_WIDTH,
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 10 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 9 * SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH,
                12 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 8 * SYMBOL_WIDTH - 0.5F * SYMBOL_WIDTH
        )
        leftsOf(lines) shouldBe listOf(
                8F,
                -1.0F * SYMBOL_WIDTH,
                0F - 0.5F * SYMBOL_WIDTH
        )

        checkSpaces(lines, text)
    }

    @Test
    fun `hang hyphen symbol`() {
        // given
        val liner = BreakLiner(breaker())
        val text = "brEak wo" +
                   "Rd"

        // when
        val lines = liner.makeLines(measuredText(text),
                config(75F, 0F, true, emptyMap(), mapOf(
                        Chars.HYPHEN to 1.0F
                ))
        )

        // then
        tokenTextsOf(lines[0], text) shouldBe listOf("brEak", " ", "wo")
        tokenTextsOf(lines[1], text) shouldBe listOf("Rd")

        widthsOf(lines) shouldBe listOf(
                5 * SYMBOL_WIDTH + 1 * SPACE_WIDTH + 2 * SYMBOL_WIDTH + HYPHEN_WIDTH - 1.0F * HYPHEN_WIDTH,
                2 * SYMBOL_WIDTH
        )
        hasHyphensAfterOf(lines) shouldBe listOf(
                true,
                false
        )

        checkSpaces(lines, text)
    }

    @Test
    fun `break with zero width`() {
        // given
        val liner = BreakLiner(breaker())
        val text = "soMe  teXt,   \"teXt\"  q  "

        // when
        val lines = liner.makeLines(measuredText(text),
                config(0F, 8F, true, mapOf(
                        '"' to 0.5F
                ), mapOf(
                        '"' to 0.5F,
                        ',' to 0.5F
                ))
        )

        // then
        tokenTextsOf(lines[0], text) shouldBe listOf("s")
        tokenTextsOf(lines[1], text) shouldBe listOf("o")
        tokenTextsOf(lines[2], text) shouldBe listOf("M")
        tokenTextsOf(lines[3], text) shouldBe listOf("e", "  ")
        tokenTextsOf(lines[4], text) shouldBe listOf("t")
        tokenTextsOf(lines[5], text) shouldBe listOf("e")
        tokenTextsOf(lines[6], text) shouldBe listOf("X")
        tokenTextsOf(lines[7], text) shouldBe listOf("t")
        tokenTextsOf(lines[8], text) shouldBe listOf(",", "   ")
        tokenTextsOf(lines[9], text) shouldBe listOf("\"")
        tokenTextsOf(lines[10], text) shouldBe listOf("t")
        tokenTextsOf(lines[11], text) shouldBe listOf("e")
        tokenTextsOf(lines[12], text) shouldBe listOf("X")
        tokenTextsOf(lines[13], text) shouldBe listOf("t")
        tokenTextsOf(lines[14], text) shouldBe listOf("\"", "  ")
        tokenTextsOf(lines[15], text) shouldBe listOf("q", "  ")

        widthsOf(lines) shouldBe listOf(
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
        leftsOf(lines) shouldBe listOf(
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
        hasHyphensAfterOf(lines) shouldBe listOf(
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
    fun `make for empty text`() {
        // given
        val liner = BreakLiner(breaker())

        // when
        val lines = liner.makeLines(measuredText(""), config(300F))

        // then
        lines.size shouldBe 0
    }

    @Test
    fun `apply hyphenation to breaker`() {
        // given
        val breaker = object : Breaker {
            lateinit var config: Breaker.Config

            override fun breakText(text: String, locale: Locale, config: Breaker.Config): Breaks {
                this.config = config
                return NoneBreaks
            }
        }
        val liner = BreakLiner(breaker)

        // when
        liner.makeLines(measuredText("text"), config(300F, hyphenation = false))

        // then
        breaker.config.hyphenation shouldBe false

        // when
        liner.makeLines(measuredText("text"), config(300F, hyphenation = true))

        // then
        breaker.config.hyphenation shouldBe true
    }

    fun checkSpaces(lines: List<Line>, text: String) {
        for (line in lines) {
            for (token in line.tokens) {
                val isSpace = isSpaceChar(text[token.beginIndex])
                for (i in token.beginIndex..token.endIndex - 1) {
                    isSpaceChar(text[token.beginIndex]) shouldBe isSpace
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

                override fun advanceOf(index: Int) = if (isSpaceChar(text[index])) SPACE_WIDTH else SYMBOL_WIDTH

                override fun advanceOf(beginIndex: Int, endIndex: Int): Float {
                    var advance = 0F
                    for (i in beginIndex..endIndex - 1) {
                        advance += advanceOf(i)
                    }
                    return advance
                }

                override fun hyphenWidthAfter(index: Int) = HYPHEN_WIDTH
            }


    fun config(
            maxWidth: Float,
            firstLineIndent: Float = 0F,
            hyphenation: Boolean = false,
            leftHangFactors: Map<Char, Float> = emptyMap(),
            rightHangFactors: Map<Char, Float> = emptyMap()
    ) = object : Config {
        override val firstLineIndent = firstLineIndent
        override val maxWidth = maxWidth
        override val hyphenation = hyphenation

        override fun leftHangFactor(ch: Char) = leftHangFactors[ch] ?: 0F
        override fun rightHangFactor(ch: Char) = rightHangFactors[ch] ?: 0F
    }

    /**
     * Обычные переносы после пробелов и мягкие переносы перед большими буквами
     */
    fun breaker() = object : Breaker {
        override fun breakText(text: String, locale: Locale, config: Breaker.Config) = object : Breaks {
            override fun hasBreakBefore(index: Int) = hasHyphenBefore(index) || isWordBegin(index)
            override fun hasHyphenBefore(index: Int) = isUpperCase(text[index])

            fun isWordBegin(index: Int) = text[index] != ' ' && text[index - 1] == ' '
        }
    }

    fun isSpaceChar(ch: Char) = ch == ' ' || ch == '\n' || ch == '\u00A0'
}