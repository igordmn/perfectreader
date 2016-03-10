package com.dmi.perfectreader.layout.liner.breaker

import com.dmi.util.shouldEquals
import org.junit.Test
import java.util.*

class ObjectBreakerTest {
    @Test
    fun break_objects() {
        // given
        val text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US)

        // then
        breakIndicesOf(breaks, text) shouldEquals listOf(9, 10, 11, 15, 16, 18)
        hyphenIndicesOf(breaks, text) shouldEquals emptyList<Int>()
    }

    @Test
    fun reverse_access() {
        // given
        val text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US)
        for (i in text.length - 1..0) {
            breaks.hasBreakBefore(i) shouldEquals true
        }

        // then
        breakIndicesOf(breaks, text) shouldEquals listOf(9, 10, 11, 15, 16, 18)
        hyphenIndicesOf(breaks, text) shouldEquals emptyList<Int>()
    }

    @Test
    fun random_access() {
        // given
        val text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US)
        breaks.hasBreakBefore(11)
        breaks.hasBreakBefore(9)
        breaks.hasBreakBefore(17)
        breaks.hasBreakBefore(3)

        // then
        breakIndicesOf(breaks, text) shouldEquals listOf(9, 10, 11, 15, 16, 18)
        hyphenIndicesOf(breaks, text) shouldEquals emptyList<Int>()
    }

    @Test
    fun break_empty_line() {
        // given
        val text = ""
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US)

        // then
        breakIndicesOf(breaks, text) shouldEquals emptyList<Int>()
        hyphenIndicesOf(breaks, text) shouldEquals emptyList<Int>()
    }
}
