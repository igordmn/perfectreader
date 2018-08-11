package com.dmi.perfectreader.book.layout.paragraph.breaker

import com.dmi.test.shouldBe
import org.junit.Test
import java.util.*

@Suppress("IllegalIdentifier")
class LineBreakerTest {
    @Test
    fun `break spaces`() {
        // given
        val text = "simple    text  sim\u00A0ple tex\nt    \n    text text  \u00A0\u00A0\n\n  "
        val breaker = LineBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldBe listOf(10, 16, 24, 28, 34, 38, 43, 52, 53)
        hyphenIndicesOf(breaks, text) shouldBe emptyList<Int>()
    }


    @Test
    fun `break empty line`() {
        // given
        val text = ""
        val breaker = LineBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldBe emptyList<Float>()
        hyphenIndicesOf(breaks, text) shouldBe emptyList<Float>()
    }
}