package com.dmi.perfectreader.book.layout.paragraph.hyphenator

import com.dmi.test.shouldBe
import org.junit.Test
import java.io.FileInputStream

@Suppress("IllegalIdentifier")
class TeXHyphenatorTest {
    @Test
    fun `break word by TeX rules`() {
        // given
        val text = "алгоритм"
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPattern("лго1")
                        .addPattern("1г")
                        .addPattern("о1ри")
                        .addPattern("и1т")
                        .addPattern("и2тм")
                        .addPattern("тм2")
                        .build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 0, text.length)

        // then
        formatBreaks(text, breaks) shouldBe "ал-го-ритм"
    }

    @Test
    fun `break word in the middle of text`() {
        // given
        val text = "  алгоритм   "
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPattern("лго1")
                        .addPattern("1г")
                        .addPattern("о1ри")
                        .addPattern("и1т")
                        .addPattern("и2тм")
                        .addPattern("тм2")
                        .build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 2, 2 + "алгоритм".length)

        // then
        formatBreaks(text, breaks) shouldBe "  ал-го-ритм   "
    }

    @Test
    fun `break word with edge TeX rule`() {
        // given
        val text = "алгоритм"
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPattern("лго1")
                        .addPattern("1г")
                        .addPattern("о1ри")
                        .addPattern("и1т")
                        .addPattern("и2тм")
                        .addPattern("тм2")
                        .addException("алгори-тм")
                        .build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 0, text.length)

        // then
        formatBreaks(text, breaks) shouldBe "алгори-тм"
    }

    @Test
    fun `break word with exception TeX rule`() {
        // given
        val text = "абвабвабв"
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPattern(".абв3")
                        .addPattern("бв2а")
                        .addPattern("7бв1абв.")
                        .build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 0, text.length)

        // then
        formatBreaks(text, breaks) shouldBe "абв-а-бвабв"
    }

    @Test
    fun `break without rules`() {
        // given
        val text = "абвабвабв"
        val hyphenator = TeXHyphenator.Builder().build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 0, text.length)

        // then
        formatBreaks(text, breaks) shouldBe "абвабвабв"
    }

    @Test
    fun `not break one letter at edge`() {
        // given
        val text = "абвабв"
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPattern("1а1")
                        .addPattern("1б1")
                        .addPattern("1в1")
                        .build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 0, text.length)

        // then
        formatBreaks(text, breaks) shouldBe "аб-в-а-бв"
    }

    @Test
    fun `break with real patterns`() {
        // given
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPatternsFrom(FileInputStream("src/main/assets/resources/hyphenations/hyph-ru.pat.txt"))
                        .addExceptionsFrom(FileInputStream("src/main/assets/resources/hyphenations/hyph-ru.hyp.txt"))
                        .build()

        fun breakWord(text: String) = formatBreaks(
                text = text,
                hyphens = hyphenator.hyphenateWord(text, 0, text.length)
        )

        // then
        breakWord("программист") shouldBe "про-грам-мист"
        breakWord("кибернетика") shouldBe "ки-бер-не-ти-ка"
        breakWord("вопль") shouldBe "вопль"
        breakWord("интуиция") shouldBe "ин-ту-и-ция"
        breakWord("достопримечательность") shouldBe "до-сто-при-ме-ча-тель-ность"
        breakWord("ривет") shouldBe "ри-вет"
    }

    @Test
    fun `case insensitive`() {
        // given
        val text = "АлгОритМ"
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPattern("лго1")
                        .addPattern("1г")
                        .addPattern("о1ри")
                        .addPattern("и1т")
                        .addPattern("и2тм")
                        .addPattern("тм2")
                        .build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 0, text.length)

        // then
        formatBreaks(text, breaks) shouldBe "Ал-гО-ритМ"
    }

    @Test
    fun `update alphabet`() {
        // given
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPattern(".абв3")
                        .addPattern("бв2а")
                        .addPattern("7бв1абв.")
                        .addException("уф-х")
                        .build()

        // then
        hyphenator.alphabetContains('а') shouldBe true
        hyphenator.alphabetContains('б') shouldBe true
        hyphenator.alphabetContains('в') shouldBe true
        hyphenator.alphabetContains('у') shouldBe true
        hyphenator.alphabetContains('ф') shouldBe true
        hyphenator.alphabetContains('х') shouldBe true
        hyphenator.alphabetContains('А') shouldBe true
        hyphenator.alphabetContains('Б') shouldBe true
        hyphenator.alphabetContains('В') shouldBe true
        hyphenator.alphabetContains('У') shouldBe true
        hyphenator.alphabetContains('Ф') shouldBe true
        hyphenator.alphabetContains('Х') shouldBe true
        hyphenator.alphabetContains('г') shouldBe false
        hyphenator.alphabetContains('3') shouldBe false
        hyphenator.alphabetContains('-') shouldBe false
        hyphenator.alphabetContains('.') shouldBe false
    }

    fun formatBreaks(text: String, hyphens: Hyphens): String =
            with (StringBuilder()) {
                for (i in 0..text.length) {
                    if (hyphens.hasHyphenBefore(i))
                        append("-")
                    if (i < text.length)
                        append(text[i])
                }
                return toString()
            }
}