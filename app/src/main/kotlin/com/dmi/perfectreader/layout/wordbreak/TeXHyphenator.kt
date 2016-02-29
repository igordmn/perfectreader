package com.dmi.perfectreader.layout.wordbreak

import com.carrotsearch.hppc.ByteArrayList
import com.carrotsearch.hppc.CharArrayList
import com.dmi.util.annotation.Reusable
import com.dmi.util.cache.ReuseCache.reuser
import com.google.common.base.Charsets
import com.google.common.io.CharStreams.readLines
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Math.min
import java.util.*
import java.util.Arrays.copyOf

class TeXHyphenator private constructor(private val patterns: TeXHyphenator.Patterns, private val exceptions: TeXHyphenator.CharsLevels) {
    fun breakWord(text: CharSequence, beginIndex: Int, endIndex: Int): WordBreaker.WordBreaks {
        val length = endIndex - beginIndex
        val wordLevels = Reusables.wordLevels(length)

        if (length >= 3 && (patterns.count > 0 || exceptions.size > 0)) {
            val chars = Reusables.wordChars()
            if (!applyException(text, beginIndex, endIndex, wordLevels, chars)) {
                applyPatterns(text, beginIndex, endIndex, wordLevels, chars)
            }
        }

        return Reusables.wordBreaks(wordLevels, beginIndex)
    }

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
            for (end in begin + 1..min(endIndex, begin + patterns.maxLength())) {
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
        val levels = patterns.levelsFor(chars)
        levels?.applyTo(wordLevels, begin - beginIndex)
    }

    class Builder {
        private val patterns = Patterns()
        private val exceptions = CharsLevels()

        @Throws(IOException::class)
        fun addPatternsFrom(stream: InputStream): Builder {
            val patterns = readLines(InputStreamReader(stream, Charsets.UTF_8))
            for (pattern in patterns) {
                addPattern(pattern)
            }
            return this
        }

        @Throws(IOException::class)
        fun addExceptionsFrom(stream: InputStream): Builder {
            val patterns = readLines(InputStreamReader(stream, Charsets.UTF_8))
            for (pattern in patterns) {
                addException(pattern)
            }
            return this
        }

        fun addPattern(pattern: String): Builder {
            val letters = CharArrayList()
            val levels = PatternLevels()
            levels.resize(pattern.length + 1)

            var atWordBegin = false
            var atWordEnd = false

            for (i in 0..pattern.length - 1) {
                val ch = pattern[i]
                if (ch == EDGE_OF_WORD) {
                    if (i == 0) {
                        atWordBegin = true
                    } else if (i == pattern.length - 1) {
                        atWordEnd = true
                    }
                } else if ('0' <= ch && ch <= '9') {
                    levels.set(letters.size(), (ch - '0').toByte())
                } else {
                    letters.add(ch)
                }
            }

            val patternStr = String(letters.buffer, 0, letters.elementsCount)
            val patternChars = PatternChars(patternStr, 0, patternStr.length, atWordBegin, atWordEnd)

            levels.resize(letters.size() + 1)
            levels.trimToSize()

            patterns.put(patternChars, levels)

            return this
        }

        fun addException(exception: String): Builder {
            val letters = CharArrayList()
            val levels = PatternLevels()
            levels.resize(exception.length + 1)

            for (i in 0..exception.length - 1) {
                val ch = exception[i]
                if (ch == '-') {
                    levels.set(letters.size(), 9.toByte())
                } else {
                    letters.add(ch)
                }
            }

            val patternStr = String(letters.buffer, 0, letters.elementsCount)
            val patternChars = PatternChars(patternStr, 0, patternStr.length, true, true)

            levels.resize(letters.size() + 1)
            levels.trimToSize()

            exceptions.put(patternChars, levels)

            return this
        }

        fun build(): TeXHyphenator {
            return TeXHyphenator(patterns, exceptions)
        }
    }

    private class Patterns {
        private var lengthToCharsLevels = arrayOfNulls<CharsLevels>(16)
        private var maxLength = 0
        var count = 0

        fun put(chars: PatternChars, levels: PatternLevels) {
            val length = chars.length
            if (length > lengthToCharsLevels.size) {
                lengthToCharsLevels = copyOf<CharsLevels>(lengthToCharsLevels, length shr 1)
            }
            if (length > maxLength) {
                maxLength = length
            }

            var charsLevels: CharsLevels? = lengthToCharsLevels[length]
            if (charsLevels == null) {
                charsLevels = CharsLevels()
                lengthToCharsLevels[length] = charsLevels
            }

            charsLevels.put(chars, levels)
            count++
        }

        fun maxLength(): Int {
            return maxLength
        }

        fun levelsFor(chars: PatternChars): PatternLevels? {
            val charsLevels = lengthToCharsLevels[chars.length]
            return if (charsLevels != null) charsLevels[chars] else null
        }
    }

    private class CharsLevels : HashMap<PatternChars, PatternLevels>()

    @Reusable
    private class PatternChars {
        private var str: CharSequence? = null
        private var begin: Int = 0
        private var end: Int = 0
        private var atWordBegin: Boolean = false
        private var atWordEnd: Boolean = false

        var length: Int = 0
        private var hashCode: Int = 0

        constructor() {
        }

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
                if (str!![i] != other.str!![j]) {
                    return false
                }
                i++
                j++
            }

            return true
        }

        override fun hashCode(): Int {
            return hashCode
        }

        private fun computeHashCode() {
            hashCode = 0
            for (i in begin..end - 1) {
                hashCode = 31 * hashCode + str!![i].toInt()
            }
            hashCode = 31 * hashCode + if (atWordBegin) 1 else 0
            hashCode = 31 * hashCode + if (atWordEnd) 1 else 0
        }
    }

    @Reusable
    private class PatternLevels : ByteArrayList() {
        internal fun applyTo(levels: PatternLevels, beginIndex: Int) {
            var i = 0
            var j = beginIndex
            while (i < elementsCount && j < levels.elementsCount) {
                val level = get(i)
                if (level > levels.get(j)) {
                    levels.set(j, level)
                }
                i++
                j++
            }
        }
    }

    @Reusable
    private class WordBreaksImpl : WordBreaker.WordBreaks {
        private var levels: PatternLevels? = null
        private var beginIndex: Int = 0

        fun reset(levels: PatternLevels, beginIndex: Int) {
            this.levels = levels
            this.beginIndex = beginIndex
        }

        override fun canBreakBefore(index: Int): Boolean {
            val wordIndex = index - beginIndex
            // переносить одну букву нельзя
            val isMiddleBreak = wordIndex >= 2 && wordIndex < levels!!.elementsCount - 2
            return isMiddleBreak && levels!!.get(wordIndex) % 2 != 0
        }
    }

    private object Reusables {
        private val wordChars = reuser<PatternChars>({ PatternChars() })
        private val wordLevels = reuser<PatternLevels>({ PatternLevels() })
        private val wordBreaks = reuser<WordBreaksImpl>({ WordBreaksImpl() })

        fun wordChars(): PatternChars {
            return wordChars.reuse()
        }

        fun wordLevels(wordLength: Int): PatternLevels {
            val value = wordLevels.reuse()
            value.clear()
            value.resize(wordLength + 1)
            return value
        }

        fun wordBreaks(levels: PatternLevels, beginIndex: Int): WordBreaker.WordBreaks {
            val value = wordBreaks.reuse()
            value.reset(levels, beginIndex)
            return value
        }
    }

    companion object {
        /**
         * алгоритм: https://habrahabr.ru/post/138088/
         */

        private val EDGE_OF_WORD = '.'
    }
}
