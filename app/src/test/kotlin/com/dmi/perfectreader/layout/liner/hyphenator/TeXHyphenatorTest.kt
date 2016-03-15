package com.dmi.perfectreader.layout.liner.hyphenator

import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.io.FileInputStream

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
        formatBreaks(text, breaks) shouldEqual "ал-го-ритм"
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
        formatBreaks(text, breaks) shouldEqual "  ал-го-ритм   "
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
        formatBreaks(text, breaks) shouldEqual "алгори-тм"
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
        formatBreaks(text, breaks) shouldEqual "абв-а-бвабв"
    }

    @Test
    fun `break without rules`() {
        // given
        val text = "абвабвабв"
        val hyphenator = TeXHyphenator.Builder().build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 0, text.length)

        // then
        formatBreaks(text, breaks) shouldEqual "абвабвабв"
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
        formatBreaks(text, breaks) shouldEqual "аб-в-а-бв"
    }

    @Test
    fun `break with real patterns`() {
        // given
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPatternsFrom(FileInputStream("src/main/assets/hyphenation/hyph-ru.pat.txt"))
                        .addExceptionsFrom(FileInputStream("src/main/assets/hyphenation/hyph-ru.hyp.txt"))
                        .build()

        fun breakWord(text: String) = formatBreaks(
                text = text,
                hyphens = hyphenator.hyphenateWord(text, 0, text.length)
        )

        // then
        breakWord("программист") shouldEqual "про-грам-мист"
        breakWord("кибернетика") shouldEqual "ки-бер-не-ти-ка"
        breakWord("вопль") shouldEqual "вопль"
        breakWord("интуиция") shouldEqual "ин-ту-и-ция"
        breakWord("достопримечательность") shouldEqual "до-сто-при-ме-ча-тель-ность"
        breakWord("ривет") shouldEqual "ри-вет"
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
        formatBreaks(text, breaks) shouldEqual "Ал-гО-ритМ"
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
        hyphenator.alphabetContains('а') shouldEqual true
        hyphenator.alphabetContains('б') shouldEqual true
        hyphenator.alphabetContains('в') shouldEqual true
        hyphenator.alphabetContains('у') shouldEqual true
        hyphenator.alphabetContains('ф') shouldEqual true
        hyphenator.alphabetContains('х') shouldEqual true
        hyphenator.alphabetContains('А') shouldEqual true
        hyphenator.alphabetContains('Б') shouldEqual true
        hyphenator.alphabetContains('В') shouldEqual true
        hyphenator.alphabetContains('У') shouldEqual true
        hyphenator.alphabetContains('Ф') shouldEqual true
        hyphenator.alphabetContains('Х') shouldEqual true
        hyphenator.alphabetContains('г') shouldEqual false
        hyphenator.alphabetContains('3') shouldEqual false
        hyphenator.alphabetContains('-') shouldEqual false
        hyphenator.alphabetContains('.') shouldEqual false
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
