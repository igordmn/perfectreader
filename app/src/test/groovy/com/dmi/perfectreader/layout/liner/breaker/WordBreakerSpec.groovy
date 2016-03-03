package com.dmi.perfectreader.layout.liner.breaker

import com.dmi.perfectreader.layout.liner.hyphenator.Hyphenator
import com.dmi.perfectreader.layout.liner.hyphenator.HyphenatorResolver
import com.dmi.perfectreader.layout.liner.hyphenator.Hyphens
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

import static com.dmi.perfectreader.layout.liner.breaker.BreakUtils.breakIndicesOf
import static com.dmi.perfectreader.layout.liner.breaker.BreakUtils.hyphenIndicesOf

class WordBreakerSpec extends Specification {
    def "break words"() {
        given:
        def text = "simple   \n  te\nxt word bigbigword, bigg-bigword"
        def breaker = new WordBreaker(hyphenatorResolver(
                [
                        "simple": [3],
                        "text": [1],           // should not break at 1 because of \n char in test text
                        ("te\nxt"): [1],       // should not break at 1 because of \n char in test text
                        "word": [2],
                        "bigword": [3],
                        "bigbigword": [3, 6],
                        "bigg": [2],
                ]
        ))

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)

        then:
        breakIndicesOf(breaks, text) == [3, 20, 26, 29, 37, 43]
        hyphenIndicesOf(breaks, text) == [3, 20, 26, 29, 37, 43]
    }

    def "break empty line"() {
        given:
        def text = ""
        def breaker = new WordBreaker(hyphenatorResolver())

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)

        then:
        breakIndicesOf(breaks, text) == []
        hyphenIndicesOf(breaks, text) == []
    }

    HyphenatorResolver hyphenatorResolver(Map<String, List<Integer>> wordToBreakIndices) {
        return new HyphenatorResolver() {
            @Override
            Hyphenator hyphenatorFor(@NotNull Locale locale) {
                return hyphenator(wordToBreakIndices)
            }
        }
    }

    Hyphenator hyphenator(Map<String, List<Integer>> wordToBreakIndices = [:]) {
        return new Hyphenator() {
            @Override
            Hyphens hyphenateWord(@NotNull CharSequence text, int beginIndex, int endIndex) {
                def word = text.subSequence(beginIndex, endIndex)
                for (entry in wordToBreakIndices) {
                    if (word == entry.key) {
                        return hyphens(beginIndex, entry.value)
                    }
                }
                return hyphens(beginIndex, [])
            }

            @Override
            boolean alphabetContains(char ch) {
                return ch >= ('a' as char) && ch <= ('z' as char)
            }
        }
    }

    Hyphens hyphens(int beginIndex, List<Integer> indices) {
        return new Hyphens() {
            @Override
            boolean hasHyphenBefore(int index) {
                return indices.contains(index - beginIndex)
            }
        }
    }
}
