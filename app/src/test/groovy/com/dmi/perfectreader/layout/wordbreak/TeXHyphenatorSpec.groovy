package com.dmi.perfectreader.layout.wordbreak

import com.dmi.perfectreader.layout.wordbreak.WordBreaker.WordBreaks
import spock.lang.Specification

class TeXHyphenatorSpec extends Specification {
    def "break word by TeX rules"() {
        given:
        def text = "алгоритм"
        def hyphenator =
                new TeXHyphenator.Builder().
                        addPattern("лго1").
                        addPattern("1г").
                        addPattern("о1ри").
                        addPattern("и1т").
                        addPattern("и2тм").
                        addPattern("тм2").
                        build()

        when:
        def breaks = hyphenator.breakWord(text, 0, text.length())

        then:
        formatBreaks(text, breaks) == "ал-го-ритм"
    }

    def "break word in the middle of text"() {
        given:
        def text = "  алгоритм   "
        def hyphenator =
                new TeXHyphenator.Builder().
                        addPattern("лго1").
                        addPattern("1г").
                        addPattern("о1ри").
                        addPattern("и1т").
                        addPattern("и2тм").
                        addPattern("тм2").
                        build()

        when:
        def breaks = hyphenator.breakWord(text, 2, 2 + "алгоритм".length())

        then:
        formatBreaks(text, breaks) == "  ал-го-ритм   "
    }

    def "break word with edge TeX rule"() {
        given:
        def text = "алгоритм"
        def hyphenator =
                new TeXHyphenator.Builder().
                        addPattern("лго1").
                        addPattern("1г").
                        addPattern("о1ри").
                        addPattern("и1т").
                        addPattern("и2тм").
                        addPattern("тм2").
                        addException("алгори-тм").
                        build()

        when:
        def breaks = hyphenator.breakWord(text, 0, text.length())

        then:
        formatBreaks(text, breaks) == "алгори-тм"
    }

    def "break word with exception TeX rule"() {
        given:
        def text = "абвабвабв"
        def hyphenator =
                new TeXHyphenator.Builder().
                        addPattern(".абв3").
                        addPattern("бв2а").
                        addPattern("7бв1абв.").
                        build()

        when:
        def breaks = hyphenator.breakWord(text, 0, text.length())

        then:
        formatBreaks(text, breaks) == "абв-а-бвабв"
    }

    def "break without rules"() {
        given:
        def text = "абвабвабв"
        def hyphenator = new TeXHyphenator.Builder().build()

        when:
        def breaks = hyphenator.breakWord(text, 0, text.length())

        then:
        formatBreaks(text, breaks) == "абвабвабв"
    }

    def "not break one letter at edge"() {
        given:
        def text = "абвабв"
        def hyphenator =
                new TeXHyphenator.Builder().
                        addPattern("1а1").
                        addPattern("1б1").
                        addPattern("1в1").
                        build()

        when:
        def breaks = hyphenator.breakWord(text, 0, text.length())

        then:
        formatBreaks(text, breaks) == "аб-в-а-бв"
    }

    def "break with real patterns"() {
        given:
        def hyphenator =
                new TeXHyphenator.Builder().
                        addPatternsFrom(new FileInputStream("src/main/assets/hyphenation/hyph-ru.pat.txt")).
                        addExceptionsFrom(new FileInputStream("src/main/assets/hyphenation/hyph-ru.hyp.txt")).
                        build()
        def breakWord = { text ->
            def breaks = hyphenator.breakWord(text, 0, text.length())
            return formatBreaks(text, breaks)
        }

        expect:
        breakWord("программист") == "про-грам-мист"
        breakWord("кибернетика") == "ки-бер-не-ти-ка"
        breakWord("вопль") == "вопль"
        breakWord("интуиция") == "ин-ту-и-ция"
        breakWord("достопримечательность") == "до-сто-при-ме-ча-тель-ность"
        breakWord("ривет") == "ри-вет"
    }

    def formatBreaks(String text, WordBreaks breaks) {
        StringBuilder s = new StringBuilder()
        for (int i = 0; i <= text.length(); i++) {
            if (breaks.canBreakBefore(i)) {
                s.append("-")
            }
            if (i < text.length()) {
                s.append(text.charAt(i))
            }
        }
        return s.toString()
    }
}
