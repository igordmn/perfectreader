package com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker

import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.util.*

@Suppress("IllegalIdentifier")
class ObjectBreakerTest {
    @Test
    fun `break objects`() {
        // given
        val text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldEqual listOf(9, 10, 11, 15, 16, 18)
        hyphenIndicesOf(breaks, text) shouldEqual emptyList<Int>()
    }

    @Test
    fun `reverse access`() {
        // given
        val text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))
        for (i in text.length - 1..0) {
            breaks.hasBreakBefore(i) shouldEqual true
        }

        // then
        breakIndicesOf(breaks, text) shouldEqual listOf(9, 10, 11, 15, 16, 18)
        hyphenIndicesOf(breaks, text) shouldEqual emptyList<Int>()
    }

    @Test
    fun `random access`() {
        // given
        val text = "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC"
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))
        breaks.hasBreakBefore(11)
        breaks.hasBreakBefore(9)
        breaks.hasBreakBefore(17)
        breaks.hasBreakBefore(3)

        // then
        breakIndicesOf(breaks, text) shouldEqual listOf(9, 10, 11, 15, 16, 18)
        hyphenIndicesOf(breaks, text) shouldEqual emptyList<Int>()
    }

    @Test
    fun `break empty line`() {
        // given
        val text = ""
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldEqual emptyList<Int>()
        hyphenIndicesOf(breaks, text) shouldEqual emptyList<Int>()
    }
}