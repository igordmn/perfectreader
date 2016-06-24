package com.dmi.perfectreader.fragment.book.layout.paragraph.breaker

import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.Hyphenator
import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.HyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.Hyphens
import org.amshove.kluent.shouldEqual
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
        breakIndicesOf(breaks, text) shouldEqual listOf(3, 20, 26, 29, 37, 43)
        hyphenIndicesOf(breaks, text) shouldEqual listOf(3, 20, 26, 29, 37, 43)
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
        breakIndicesOf(breaks, text) shouldEqual emptyList()
        hyphenIndicesOf(breaks, text) shouldEqual emptyList()
    }

    @Test
    fun `break empty line`() {
        // given
        val text = ""
        val breaker = WordBreaker(hyphenatorResolver())

        // when
        val breaks = breaker.breakText(text, Locale.US, Breaker.Config(true))

        // then
        breakIndicesOf(breaks, text) shouldEqual emptyList<Int>()
        hyphenIndicesOf(breaks, text) shouldEqual emptyList<Int>()
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

            override fun alphabetContains(ch: Char) = ch >= ('a') && ch <= ('z')

            fun hyphens(beginIndex: Int, indices: List<Int>) = object : Hyphens {
                override fun hasHyphenBefore(index: Int) = indices.contains(index - beginIndex)
            }
        }
    }
}