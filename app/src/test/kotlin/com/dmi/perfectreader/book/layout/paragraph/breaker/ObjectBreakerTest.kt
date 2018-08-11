package com.dmi.perfectreader.book.layout.paragraph.breaker

import com.dmi.test.shouldBe
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
        breakIndicesOf(breaks, text) shouldBe listOf(9, 10, 11, 15, 16, 18)
        hyphenIndicesOf(breaks, text) shouldBe emptyList()
    }

    @Test
    fun `break empty line`() {
        // given
        val text = ""
        val breaker = ObjectBreaker()

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldBe emptyList()
        hyphenIndicesOf(breaks, text) shouldBe emptyList()
    }
}