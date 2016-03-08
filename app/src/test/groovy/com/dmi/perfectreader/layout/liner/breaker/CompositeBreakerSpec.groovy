package com.dmi.perfectreader.layout.liner.breaker

import org.jetbrains.annotations.NotNull
import spock.lang.Specification

import static com.dmi.perfectreader.layout.liner.breaker.BreakUtils.breakIndicesOf
import static com.dmi.perfectreader.layout.liner.breaker.BreakUtils.hyphenIndicesOf

class CompositeBreakerSpec extends Specification {
    def "composite breaks"() {
        given:
        def text = "simple  text"
        def breaker1 = breaker([4, 7], [])
        def breaker2 = breaker([2, 7], [2])
        def breaker3 = breaker([1, 7, 9], [7])
        def breaker = new CompositeBreaker(breaker1, breaker2, breaker3)

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)

        then:
        breakIndicesOf(breaks, text) == [1, 2, 4, 7, 9]
        hyphenIndicesOf(breaks, text) == [2, 7]
    }

    Breaker breaker(List<Integer> breakIndices, List<Integer> hyphenIndices) {
        return new Breaker() {
            @Override
            Breaks breakText(@NotNull String text, @NotNull Locale locale) {
                return new Breaks() {
                    @Override
                    boolean hasBreakBefore(int index) { return breakIndices.contains(index) }

                    @Override
                    boolean hasHyphenBefore(int index) { return hyphenIndices.contains(index) }
                }
            }
        }
    }
}
