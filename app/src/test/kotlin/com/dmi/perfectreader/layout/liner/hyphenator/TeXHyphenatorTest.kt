package com.dmi.perfectreader.layout.liner.hyphenator

import com.dmi.util.shouldEquals
import org.junit.Test
import java.io.FileInputStream

class TeXHyphenatorTest {
    @Test
    fun break_word_by_TeX_rules() {
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
        formatBreaks(text, breaks) shouldEquals "ал-го-ритм"
    }

    @Test
    fun break_word_in_the_middle_of_text() {
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
        formatBreaks(text, breaks) shouldEquals "  ал-го-ритм   "
    }

    @Test
    fun break_word_with_edge_TeX_rule() {
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
        formatBreaks(text, breaks) shouldEquals "алгори-тм"
    }

    @Test
    fun break_word_with_exception_TeX_rule() {
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
        formatBreaks(text, breaks) shouldEquals "абв-а-бвабв"
    }

    @Test
    fun break_without_rules() {
        // given
        val text = "абвабвабв"
        val hyphenator = TeXHyphenator.Builder().build()

        // when
        val breaks = hyphenator.hyphenateWord(text, 0, text.length)

        // then
        formatBreaks(text, breaks) shouldEquals "абвабвабв"
    }

    @Test
    fun not_break_one_letter_at_edge() {
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
        formatBreaks(text, breaks) shouldEquals "аб-в-а-бв"
    }

    @Test
    fun break_with_real_patterns() {
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
        breakWord("программист") shouldEquals "про-грам-мист"
        breakWord("кибернетика") shouldEquals "ки-бер-не-ти-ка"
        breakWord("вопль") shouldEquals "вопль"
        breakWord("интуиция") shouldEquals "ин-ту-и-ция"
        breakWord("достопримечательность") shouldEquals "до-сто-при-ме-ча-тель-ность"
        breakWord("ривет") shouldEquals "ри-вет"
    }

    @Test
    fun case_insensitive() {
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
        formatBreaks(text, breaks) shouldEquals "Ал-гО-ритМ"
    }

    @Test
    fun update_alphabet() {
        // given
        val hyphenator =
                TeXHyphenator.Builder()
                        .addPattern(".абв3")
                        .addPattern("бв2а")
                        .addPattern("7бв1абв.")
                        .addException("уф-х")
                        .build()

        // then
        hyphenator.alphabetContains('а') shouldEquals true
        hyphenator.alphabetContains('б') shouldEquals true
        hyphenator.alphabetContains('в') shouldEquals true
        hyphenator.alphabetContains('у') shouldEquals true
        hyphenator.alphabetContains('ф') shouldEquals true
        hyphenator.alphabetContains('х') shouldEquals true
        hyphenator.alphabetContains('А') shouldEquals true
        hyphenator.alphabetContains('Б') shouldEquals true
        hyphenator.alphabetContains('В') shouldEquals true
        hyphenator.alphabetContains('У') shouldEquals true
        hyphenator.alphabetContains('Ф') shouldEquals true
        hyphenator.alphabetContains('Х') shouldEquals true
        hyphenator.alphabetContains('г') shouldEquals false
        hyphenator.alphabetContains('3') shouldEquals false
        hyphenator.alphabetContains('-') shouldEquals false
        hyphenator.alphabetContains('.') shouldEquals false
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
