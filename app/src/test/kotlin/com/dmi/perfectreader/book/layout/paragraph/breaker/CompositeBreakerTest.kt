package com.dmi.perfectreader.book.layout.paragraph.breaker

import com.dmi.test.shouldEqual
import org.junit.Test
import java.util.*

@Suppress("IllegalIdentifier")
class CompositeBreakerTest {
    @Test
    fun `composite breaks`() {
        // given
        val text = "simple  text"
        val breaker1 = breaker(listOf(4, 7), emptyList<Int>())
        val breaker2 = breaker(listOf(2, 7), listOf(2))
        val breaker3 = breaker(listOf(1, 7, 9), listOf(7))
        val breaker = CompositeBreaker(breaker1, breaker2, breaker3)

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldEqual listOf(1, 2, 4, 7, 9)
        hyphenIndicesOf(breaks, text) shouldEqual listOf(2, 7)
    }

    fun breaker(breakIndices: List<Int>, hyphenIndices: List<Int>) = object : Breaker {
        override fun breakText(text: String, locale: Locale, config: Breaker.Config) = object : Breaks {
            override fun hasBreakBefore(index: Int) = breakIndices.contains(index)
            override fun hasHyphenBefore(index: Int) = hyphenIndices.contains(index)
        }
    }
}