package com.dmi.perfectreader.fragment.book.layout.paragraph.breaker

import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.HyphenatorResolver
import com.dmi.util.lang.ReusableByteArray
import java.lang.Character.*
import java.util.*

class WordBreaker(private val hyphenatorResolver: HyphenatorResolver) : Breaker {
    override fun breakText(text: String, locale: Locale, config: Breaker.Config): Breaks {
        return object {
            val hyphenator = hyphenatorResolver.hyphenatorFor(locale)
            val hasBreakBefore = Reusables.hasBreakBefore(text.length).apply { fill(-1) }

            fun breakText() = if (config.hyphenation) hyphenationBreaks() else NoneBreaks

            private fun hyphenationBreaks(): Breaks {
                return object : Breaks {
                    override fun hasBreakBefore(index: Int): Boolean {
                        checkHyphensAt(index)
                        return hasBreakBefore[index] == 1.toByte()
                    }

                    override fun hasHyphenBefore(index: Int): Boolean {
                        val charMinus2 = if (index - 2 >= 0) text[index - 2] else 0.toChar()
                        val charMinus1 = if (index - 1 >= 0) text[index - 1] else 0.toChar()
                        val char = if (index < text.length) text[index] else 0.toChar()
                        val charPlus1 = if (index + 1 < text.length) text[index + 1] else 0.toChar()
                        val previousIsLetter =
                                if (isSurrogatePair(charMinus2, charMinus1))
                                    isLetter(toCodePoint(charMinus2, charMinus1))
                                else
                                    isLetter(charMinus1)

                        val isLetter =
                                if (isSurrogatePair(char, charPlus1))
                                    isLetter(toCodePoint(char, charPlus1))
                                else
                                    isLetter(char)

                        return hasBreakBefore(index) && previousIsLetter && isLetter
                    }
                }
            }

            fun checkHyphensAt(index: Int) {
                if (hasBreakBefore[index] == (-1).toByte()) {
                    val isWord = alphabetContains(text[index])
                    if (isWord) {
                        val begin = findBegin(index) { alphabetContains(it) }
                        val end = findEnd(index) { alphabetContains(it) }
                        check(end > begin)
                        val hyphens = hyphenator.hyphenateWord(text, begin, end)
                        for (i in begin..end - 1) {
                            hasBreakBefore[i] = if (hyphens.hasHyphenBefore(i)) 1 else 0
                        }
                    } else {
                        val begin = findBegin(index) { !alphabetContains(it) }
                        val end = findEnd(index) { !alphabetContains(it) }
                        check(end > begin)
                        for (i in begin..end - 1) {
                            hasBreakBefore[i] = 0
                        }
                    }
                }
            }

            fun alphabetContains(ch: Char) = hyphenator.alphabetContains(ch)

            inline fun findBegin(pos: Int, criteria: (Char) -> Boolean): Int {
                var i = pos
                while (i >= 0) {
                    if (!criteria(text[i]))
                        return i + 1
                    i--
                }
                return 0
            }

            inline fun findEnd(pos: Int, criteria: (Char) -> Boolean): Int {
                var i = pos
                while (i < text.length) {
                    if (!criteria(text[i]))
                        return i
                    i++
                }
                return text.length
            }
        }.breakText()
    }

    private object Reusables {
        private val INITIAL_CHARS_CAPACITY = 4000

        val hasBreakBefore = ReusableByteArray(INITIAL_CHARS_CAPACITY)
    }
}