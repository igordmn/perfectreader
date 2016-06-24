package com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator

import com.carrotsearch.hppc.ByteArrayList
import com.carrotsearch.hppc.CharArrayList
import com.carrotsearch.hppc.CharScatterSet
import com.carrotsearch.hppc.CharSet
import com.dmi.util.lang.Reusable
import com.dmi.util.lang.ReusableValue
import com.google.common.io.CharStreams.readLines
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Character.toLowerCase
import java.lang.Math.min
import java.util.*
import java.util.Arrays.copyOf

class TeXHyphenator private constructor(
        private val alphabet: CharSet,
        private val patterns: Patterns,
        private val exceptions: CharsLevels
) : Hyphenator {
    /**
     * алгоритм: https://habrahabr.ru/post/138088/
     */

    companion object {
        private val EDGE_OF_WORD = '.'
    }

    override fun hyphenateWord(text: CharSequence, beginIndex: Int, endIndex: Int): Hyphens {
        val length = endIndex - beginIndex
        val wordLevels = Reusables.wordLevels(length)

        if (length >= 3 && (patterns.size > 0 || exceptions.size > 0)) {
            val chars = Reusables.wordChars()
            if (!applyException(text, beginIndex, endIndex, wordLevels, chars)) {
                applyPatterns(text, beginIndex, endIndex, wordLevels, chars)
            }
        }

        return Reusables.hyphens(wordLevels, beginIndex)
    }

    override fun alphabetContains(ch: Char) = alphabet.contains(toLowerCase(ch))

    private fun applyException(text: CharSequence, beginIndex: Int, endIndex: Int, wordLevels: PatternLevels, chars: PatternChars): Boolean {
        chars.reset(text, beginIndex, endIndex, true, true)
        val levels = exceptions[chars]
        if (levels != null) {
            levels.applyTo(wordLevels, 0)
            return true
        } else {
            return false
        }
    }

    private fun applyPatterns(text: CharSequence, beginIndex: Int, endIndex: Int, wordLevels: PatternLevels, chars: PatternChars) {
        for (begin in beginIndex..endIndex - 1) {
            for (end in begin + 1..min(endIndex, begin + patterns.maxLength)) {
                if (begin == beginIndex) {
                    applyLevels(text, beginIndex, wordLevels, chars, begin, end, true, false)
                }
                if (end == endIndex) {
                    applyLevels(text, beginIndex, wordLevels, chars, begin, end, false, true)
                }
                if (begin == beginIndex && end == endIndex) {
                    applyLevels(text, beginIndex, wordLevels, chars, begin, end, true, true)
                }
                applyLevels(text, beginIndex, wordLevels, chars, begin, end, false, false)
            }
        }
    }

    private fun applyLevels(text: CharSequence, beginIndex: Int, wordLevels: PatternLevels, chars: PatternChars, begin: Int, end: Int,
                            atWordBegin: Boolean, atWordEnd: Boolean) {
        chars.reset(text, begin, end, atWordBegin, atWordEnd)
        val levels = patterns[chars]
        levels?.applyTo(wordLevels, begin - beginIndex)
    }

    class Builder {
        private val patterns = Patterns()
        private val exceptions = CharsLevels()
        private val alphabet = CharScatterSet()

        fun addPatternsFrom(stream: InputStream): Builder {
            readStreamLines(stream) { addPattern(it) }
            return this
        }

        fun addExceptionsFrom(stream: InputStream): Builder {
            readStreamLines(stream) { addException(it) }
            return this
        }

        private inline fun readStreamLines(stream: InputStream, forEach: (String) -> Unit) {
            readLines(InputStreamReader(stream, com.google.common.base.Charsets.UTF_8)).forEach(forEach)
        }

        fun addPattern(pattern: String): Builder {
            val letters = CharArrayList()
            val levels = PatternLevels().apply { resize(pattern.length + 1) }
            var atWordBegin = false
            var atWordEnd = false

            for (i in 0..pattern.length - 1) {
                val ch = pattern[i]
                when (ch) {
                    EDGE_OF_WORD -> {
                        if (i == 0) atWordBegin = true
                        if (i == pattern.length - 1) atWordEnd = true
                    }
                    in '0'..'9' -> levels.set(letters.size(), (ch - '0').toByte())
                    else -> letters.add(ch)
                }
            }

            alphabet.addAll(letters)
            patterns.put(
                    patternChars(letters, atWordBegin, atWordEnd),
                    trimLevels(letters.size(), levels)
            )

            return this
        }

        fun addException(exception: String): Builder {
            val letters = CharArrayList()
            val levels = PatternLevels().apply { resize(exception.length + 1) }

            for (i in 0..exception.length - 1) {
                val ch = exception[i]
                when (ch) {
                    '-' -> levels.set(letters.size(), 9)
                    else -> letters.add(ch)
                }
            }

            alphabet.addAll(letters)
            exceptions.put(
                    patternChars(letters, true, true),
                    trimLevels(letters.size(), levels)
            )

            return this
        }

        private fun patternChars(array: CharArrayList, atWordBegin: Boolean, atWordEnd: Boolean): PatternChars {
            val patternStr = String(array.buffer, 0, array.elementsCount)
            return PatternChars(patternStr, 0, patternStr.length, atWordBegin, atWordEnd)
        }

        private fun trimLevels(lettersSize: Int, levels: PatternLevels) = levels.apply {
            resize(lettersSize + 1)
            trimToSize()
        }

        fun build(): TeXHyphenator {
            return TeXHyphenator(alphabet, patterns, exceptions)
        }
    }

    private class Patterns {
        private var lengthToCharsLevels = arrayOfNulls<CharsLevels>(16)
        var maxLength = 0
            private set
        var size = 0
            private set

        fun put(chars: PatternChars, levels: PatternLevels) {
            val len = chars.length
            if (len > lengthToCharsLevels.size) {
                lengthToCharsLevels = copyOf<CharsLevels>(lengthToCharsLevels, len shr 1)
            }
            if (len > maxLength) maxLength = len

            var charsLevels: CharsLevels? = lengthToCharsLevels[len]
            if (charsLevels == null) {
                charsLevels = CharsLevels()
                lengthToCharsLevels[len] = charsLevels
            }

            charsLevels.put(chars, levels)
            size++
        }

        operator fun get(chars: PatternChars): PatternLevels? {
            return lengthToCharsLevels[chars.length]?.get(chars)
        }
    }

    private class CharsLevels : HashMap<PatternChars, PatternLevels>()

    @Reusable
    private class PatternChars {
        private lateinit var str: CharSequence
        private var begin: Int = 0
        private var end: Int = 0
        private var atWordBegin: Boolean = false
        private var atWordEnd: Boolean = false

        private var hashCode: Int = 0

        var length: Int = 0
            private set

        constructor()

        constructor(str: CharSequence, begin: Int, end: Int, atWordBegin: Boolean, atWordEnd: Boolean) {
            reset(str, begin, end, atWordBegin, atWordEnd)
        }

        fun reset(str: CharSequence, begin: Int, end: Int, atWordBegin: Boolean, atWordEnd: Boolean) {
            this.str = str
            this.begin = begin
            this.end = end
            this.atWordBegin = atWordBegin
            this.atWordEnd = atWordEnd
            this.length = end - begin

            computeHashCode()
        }

        override fun equals(other: Any?): Boolean {
            other as PatternChars

            if (other.length != length || other.atWordBegin != atWordBegin || other.atWordEnd != atWordEnd) {
                return false
            }

            var i = begin
            var j = other.begin
            while (i < end) {
                if (toLowerCase(str[i]) != toLowerCase(other.str[j])) {
                    return false
                }
                i++
                j++
            }

            return true
        }

        override fun hashCode() = hashCode

        private fun computeHashCode() {
            hashCode = 0
            for (i in begin..end - 1) {
                hashCode = 31 * hashCode + toLowerCase(str[i]).toInt()
            }
            hashCode = 31 * hashCode + atWordBegin.hashCode()
            hashCode = 31 * hashCode + atWordEnd.hashCode()
        }
    }

    @Reusable
    private class PatternLevels : ByteArrayList() {
        fun applyTo(levels: PatternLevels, beginIndex: Int) {
            var i = 0
            var j = beginIndex
            while (i < elementsCount && j < levels.elementsCount) {
                val level = this[i]
                if (level > levels[j]) {
                    levels[j] = level
                }
                i++
                j++
            }
        }
    }

    @Reusable
    private class TeXHyphens : Hyphens {
        private lateinit var levels: PatternLevels

        private var beginIndex: Int = 0

        fun reset(levels: PatternLevels, beginIndex: Int) {
            this.levels = levels
            this.beginIndex = beginIndex
        }

        override fun hasHyphenBefore(index: Int): Boolean {
            val wordIndex = index - beginIndex
            // переносить одну букву нельзя
            val isMiddleBreak = wordIndex >= 2 && wordIndex < levels.elementsCount - 2
            return isMiddleBreak && levels[wordIndex] % 2 != 0
        }
    }

    private object Reusables {
        val wordChars = ReusableValue({ PatternChars() })
        val wordLevels = ReusableWordLevels()
        val hyphens = ReusableHyphens()

        class ReusableHyphens {
            private val value = ReusableValue({ TeXHyphens() })
            operator fun invoke(levels: PatternLevels, beginIndex: Int) = value().apply { reset(levels, beginIndex) }
        }

        class ReusableWordLevels {
            private val value = ReusableValue({ PatternLevels() })
            operator fun invoke(wordLength: Int) = value().apply {
                clear()
                resize(wordLength + 1)
            }
        }
    }
}