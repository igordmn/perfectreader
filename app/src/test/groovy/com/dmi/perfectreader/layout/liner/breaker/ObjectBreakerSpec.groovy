package com.dmi.perfectreader.layout.liner.breaker

import spock.lang.Specification

import static com.dmi.perfectreader.layout.liner.breaker.BreakUtils.breakIndicesOf
import static com.dmi.perfectreader.layout.liner.breaker.BreakUtils.hyphenIndicesOf

class ObjectBreakerSpec extends Specification {
    def "break objects"() {
        given:
        def text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        def breaker = new ObjectBreaker()

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)

        then:
        breakIndicesOf(breaks, text) == [9, 10, 11, 15, 16, 18]
        hyphenIndicesOf(breaks, text) == []
    }

    def "reverse access"() {
        given:
        def text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        def breaker = new ObjectBreaker()

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)
        for (int i = text.length() - 1; i >= 0; i--) {
            breaks.hasBreakBefore(i)
        }

        then:
        breakIndicesOf(breaks, text) == [9, 10, 11, 15, 16, 18]
        hyphenIndicesOf(breaks, text) == []
    }

    def "random access"() {
        given:
        def text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        def breaker = new ObjectBreaker()

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)
        breaks.hasBreakBefore(11)
        breaks.hasBreakBefore(9)
        breaks.hasBreakBefore(17)
        breaks.hasBreakBefore(3)

        then:
        breakIndicesOf(breaks, text) == [9, 10, 11, 15, 16, 18]
        hyphenIndicesOf(breaks, text) == []
    }

    def "break empty line"() {
        given:
        def text = ""
        def breaker = new ObjectBreaker()

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)

        then:
        breakIndicesOf(breaks, text) == []
        hyphenIndicesOf(breaks, text) == []
    }
}
