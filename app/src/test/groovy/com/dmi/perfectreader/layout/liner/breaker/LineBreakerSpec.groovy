package com.dmi.perfectreader.layout.liner.breaker

import spock.lang.Specification

import static com.dmi.perfectreader.layout.liner.breaker.BreakUtils.breakIndicesOf
import static com.dmi.perfectreader.layout.liner.breaker.BreakUtils.hyphenIndicesOf

class LineBreakerSpec extends Specification {
    def "break spaces"() {
        given:
        def text = "simple    text  sim\u00A0ple tex\nt    \n    text text  \u00A0\u00A0\n\n  "
        def breaker = new LineBreaker()

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)

        then:
        breakIndicesOf(breaks, text) == [10, 16, 24, 28, 34, 38, 43, 52, 53]
        hyphenIndicesOf(breaks, text) == []
    }

    def "break empty line"() {
        given:
        def text = ""
        def breaker = new LineBreaker()

        when:
        Breaks breaks = breaker.breakText(text, Locale.US)

        then:
        breakIndicesOf(breaks, text) == []
        hyphenIndicesOf(breaks, text) == []
    }
}
