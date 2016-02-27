package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.config.LayoutChars
import com.dmi.perfectreader.layout.wordbreak.WordBreaker
import com.dmi.perfectreader.layout.wordbreak.WordBreaker.WordBreaks
import com.dmi.util.annotation.Reusable
import com.dmi.util.cache.ReuseCache.reuseBooleanArray
import com.dmi.util.cache.ReuseCache.reuser
import com.dmi.util.text.CharSequenceCharacterIterator
import com.google.common.base.Preconditions.checkArgument
import java.text.BreakIterator
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class RuleBreakFinder(private val wordBreaker: WordBreaker) : BreakFinder {

    override fun findBreaks(text: CharSequence, locale: Locale, accept: (BreakFinder.Break) -> Unit) {
        val breaks = Breaks(text.length)
        addLineBreaks(text, locale, breaks)
        addWordBreaks(text, locale, breaks)
        addObjectBreaks(text, breaks)
        breaks.forEach(accept)
    }

    private fun addLineBreaks(text: CharSequence, locale: Locale, breaks: Breaks) {
        val it = BreakIterator.getLineInstance(locale)
        it.text = CharSequenceCharacterIterator(text)
        it.first()
        var i = it.next()
        while (i != text.length) {
            breaks[i, false] = isLineBreakingChar(text[i - 1])
            i = it.next()
        }
    }

    private fun addWordBreaks(text: CharSequence, locale: Locale, breaks: Breaks) {
        val it = BreakIterator.getWordInstance(locale)
        it.text = CharSequenceCharacterIterator(text)
        var begin = it.first()
        var i = it.next()
        while (i != BreakIterator.DONE) {
            for (end in begin + 1..i) {
                if (end == i || breaks.isBreak(end)) {
                    val wordBreaks = wordBreaker.breakWord(text, locale, begin, end)
                    addWordBreaks(text, wordBreaks, begin, end, breaks)
                    begin = end
                }
            }
            i = it.next()
        }
    }

    private fun addWordBreaks(text: CharSequence, wordBreaks: WordBreaks, begin: Int, end: Int, breaks: Breaks) {
        for (i in begin + 1..end - 1) {
            if (wordBreaks.canBreakBefore(i)) {
                breaks[i, true] = isLineBreakingChar(text[i - 1])
            }
        }
    }

    private fun addObjectBreaks(text: CharSequence, breaks: Breaks) {
        for (i in 0..text.length - 1) {
            if (text[i] == LayoutChars.OBJECT_REPLACEMENT_CHARACTER) {
                breaks[i, false] = isLineBreakingChar(text[i - 1])
                if (i + 1 < text.length) {
                    breaks[i + 1, false] = isLineBreakingChar(text[i])
                }
            }
        }
    }

    private fun isLineBreakingChar(ch: Char): Boolean {
        return ch == '\n' || ch == '\r' || ch == '\u000B' || ch == '\u000C' || ch == '\u0085' || ch == '\u2028' || ch == '\u2029'
    }

    private class Breaks(private val length: Int) {
        private val isBreak: BooleanArray
        private val hasHyphen: BooleanArray
        private val isForce: BooleanArray

        private val br = BreakImpl()

        init {
            isBreak = Reusables.isBreak(length)
            hasHyphen = Reusables.hasHyphen(length)
            isForce = Reusables.isForce(length)

            Arrays.fill(isBreak, false)
            Arrays.fill(hasHyphen, false)
            Arrays.fill(isForce, false)
        }

        operator fun set(index: Int, hasHyphen: Boolean, isForce: Boolean) {
            checkArgument(index < length)
            this.isBreak[index] = true
            this.hasHyphen[index] = hasHyphen
            this.isForce[index] = isForce
        }

        fun isBreak(index: Int): Boolean {
            checkArgument(index < length)
            return isBreak[index]
        }

        fun forEach(accept: (BreakFinder.Break) -> Unit) {
            for (i in 0..length - 1) {
                if (isBreak[i]) {
                    br.index = i
                    br.hasHyphen = hasHyphen[i]
                    br.force = isForce[i]
                    accept(br)
                }
            }
            br.index = length
            br.hasHyphen = false
            br.force = false
            accept(br)
        }
    }

    @Reusable
    private class BreakImpl : BreakFinder.Break {
        var index: Int = 0
        var hasHyphen: Boolean = false
        var force: Boolean = false

        override fun index(): Int {
            return index
        }

        override fun hasHyphen(): Boolean {
            return hasHyphen
        }

        override fun isForce(): Boolean {
            return force
        }
    }

    private object Reusables {
        private val INITIAL_CHARS_CAPACITY = 4000

        private val isBreak = reuser { AtomicReference(BooleanArray(INITIAL_CHARS_CAPACITY)) }
        private val hasHyphen = reuser { AtomicReference(BooleanArray(INITIAL_CHARS_CAPACITY)) }
        private val isForce = reuser { AtomicReference(BooleanArray(INITIAL_CHARS_CAPACITY)) }

        fun isBreak(capacity: Int): BooleanArray {
            return reuseBooleanArray(isBreak, capacity)
        }

        fun hasHyphen(capacity: Int): BooleanArray {
            return reuseBooleanArray(hasHyphen, capacity)
        }

        fun isForce(capacity: Int): BooleanArray {
            return reuseBooleanArray(isForce, capacity)
        }
    }
}
