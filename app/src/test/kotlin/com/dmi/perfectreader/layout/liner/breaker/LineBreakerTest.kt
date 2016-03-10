package com.dmi.perfectreader.layout.liner.breaker

import com.dmi.util.shouldEquals
import org.junit.Test
import java.util.*

class LineBreakerTest {
    @Test
    fun break_spaces() {
        // given
        val text = "simple    text  sim\u00A0ple tex\nt    \n    text text  \u00A0\u00A0\n\n  "
        val breaker = LineBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US)

        // then
        breakIndicesOf(breaks, text) shouldEquals listOf(10, 16, 24, 28, 34, 38, 43, 52, 53)
        hyphenIndicesOf(breaks, text) shouldEquals emptyList<Int>()
    }


    @Test
    fun break_empty_line() {
        // given
        val text = ""
        val breaker = LineBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US)

        // then
        breakIndicesOf(breaks, text) shouldEquals emptyList<Float>()
        hyphenIndicesOf(breaks, text) shouldEquals emptyList<Float>()
    }
}
