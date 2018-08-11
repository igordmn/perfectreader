package com.dmi.perfectreader.book.layout.paragraph.breaker

import com.dmi.perfectreader.book.layout.paragraph.hyphenator.Hyphenator
import com.dmi.perfectreader.book.layout.paragraph.hyphenator.HyphenatorResolver
import com.dmi.perfectreader.book.layout.paragraph.hyphenator.Hyphens
import com.dmi.test.shouldBe
import org.junit.Test
import java.util.*

@Suppress("IllegalIdentifier")
class WordBreakerTest {
    @Test
    fun `break words`() {
        // given
        val text = "simple   \n  te\nxt word bigbigword, bigg-bigword"
        val breaker = WordBreaker(hyphenatorResolver(
                mapOf(
                        "simple" to listOf(3),
                        "text" to listOf(1), // should not break at 1 because of \n char in test text
                        "te\nxt" to listOf(1), // should not break at 1 because of \n char in test text
                        "word" to listOf(2),
                        "bigword" to listOf(3),
                        "bigbigword" to listOf(3, 6),
                        "bigg" to listOf(2)
                )
        ))

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldBe listOf(3, 20, 26, 29, 37, 43)
        hyphenIndicesOf(breaks, text) shouldBe listOf(3, 20, 26, 29, 37, 43)
    }

    @Test
    fun `not break words if hyphenation disabled`() {
        // given
        val text = "simple text"
        val breaker = WordBreaker(hyphenatorResolver(
                mapOf(
                        "simple" to listOf(3),
                        "text" to listOf(1)
                )
        ))

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(false))

        // then
        breakIndicesOf(breaks, text) shouldBe emptyList()
        hyphenIndicesOf(breaks, text) shouldBe emptyList()
    }

    @Test
    fun `break empty line`() {
        // given
        val text = ""
        val breaker = WordBreaker(hyphenatorResolver())

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldBe emptyList<Int>()
        hyphenIndicesOf(breaks, text) shouldBe emptyList<Int>()
    }

    @Test
    fun `don't add hyphen between non-letters`() {
        // given
//        val b = byteArrayOf(
//            0, 0x41, // A
//            0xD8.toByte(), 1, // High surrogate
//            0xDC.toByte(), 2, // Low surrogate
//            0, 0x42 // B
//        )
//        val s = String(b, Charsets.UTF_16)

        // \uD801\uDC02 это суррогатная пара для 'DESERET CAPITAL LETTER LONG A'
        // см. http://www.fileformat.info/info/unicode/char/10402/index.htm
        val text = "text t1xt te1t t11t t\uD801\uDC02xt te\uD801\uDC02t t\uD801\uDC02\uD801\uDC02t t\uD801\uDC021t t1\uD801\uDC02t"
        val breaker = WordBreaker(hyphenatorResolver(
                mapOf(
                        "text" to listOf(2),
                        "t1xt" to listOf(2),
                        "te1t" to listOf(2),
                        "t11t" to listOf(2),
                        "t\uD801\uDC02xt" to listOf(3),
                        "te\uD801\uDC02t" to listOf(2),
                        "t\uD801\uDC02\uD801\uDC02t" to listOf(3),
                        "t\uD801\uDC021t" to listOf(3),
                        "t1\uD801\uDC02t" to listOf(2)
                )
        ))

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldBe listOf(2, 2 + 5, 7 + 5, 12 + 5, 17 + 6, 23 + 5, 28 + 7, 35 + 7, 42 + 5)
        hyphenIndicesOf(breaks, text) shouldBe listOf(2, 23, 28, 35)
    }

    fun hyphenatorResolver(wordToBreakIndices: Map<String, List<Int>> = emptyMap()) = object : HyphenatorResolver {
        override fun hyphenatorFor(locale: Locale) = object : Hyphenator {
            override fun hyphenateWord(text: CharSequence, beginIndex: Int, endIndex: Int): Hyphens {
                val word = text.subSequence(beginIndex, endIndex)

                for ((key, value) in wordToBreakIndices) {
                    if (word == key)
                        return hyphens(beginIndex, value)
                }

                return hyphens(beginIndex, listOf())
            }

            override fun alphabetContains(ch: Char) = ch >= ('a') && ch <= ('z') || ch == '1' || ch == '\uD801' || ch == '\uDC02'

            fun hyphens(beginIndex: Int, indices: List<Int>) = object : Hyphens {
                override fun hasHyphenBefore(index: Int) = indices.contains(index - beginIndex)
            }
        }
    }
}