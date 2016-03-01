package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.config.LayoutChars.OBJECT_REPLACEMENT_CHARACTER
import com.dmi.perfectreader.layout.liner.BreakFinder.Break
import com.dmi.perfectreader.layout.wordbreak.WordBreaker
import com.dmi.util.cache.ReusableBooleanArray
import com.dmi.util.text.CharSequenceCharacterIterator
import com.google.common.base.Preconditions.checkArgument
import java.text.BreakIterator
import java.util.*

class RuleBreakFinder(private val wordBreaker: WordBreaker) : BreakFinder {
    override fun findBreaks(text: CharSequence, locale: Locale, accept: (BreakFinder.Break) -> Unit) {
        object {
            val breaks = Breaks(text.length)

            fun findBreaks() {
                addLineBreaks()
                addWordBreaks()
                addObjectBreaks()
                breaks.forEach(accept)
            }

            fun addLineBreaks() {
                with (BreakIterator.getLineInstance(locale)) {
                    this.text = CharSequenceCharacterIterator(text)
                    first()
                    var i = next()
                    while (i != text.length) {
                        addBreak(i)
                        i = next()
                    }
                }
            }

            fun addWordBreaks() {
                with (BreakIterator.getWordInstance(locale)) {
                    this.text = CharSequenceCharacterIterator(text)
                    var begin = first()
                    var i = next()
                    while (i != BreakIterator.DONE) {
                        for (end in begin + 1..i) {
                            if (end == i || breaks.isBreak(end)) {
                                addWordBreaks(begin, end)
                                begin = end
                            }
                        }
                        i = next()
                    }
                }
            }

            fun addWordBreaks(begin: Int, end: Int) {
                val wordBreaks = wordBreaker.breakWord(text, locale, begin, end)
                for (i in begin + 1..end - 1) {
                    if (wordBreaks.canBreakBefore(i))
                        addBreak(i, true)
                }
            }

            fun addObjectBreaks() {
                for (i in 0..text.length - 1) {
                    if (text[i] == OBJECT_REPLACEMENT_CHARACTER) {
                        breaks.add(i, false, isLineSeparator(text[i - 1]))
                        if (i + 1 < text.length)
                            addBreak(i + 1)
                    }
                }
            }

            fun addBreak(index: Int, hasHyphen: Boolean = false) {
                breaks.add(index, hasHyphen, isLineSeparator(text[index - 1]))
            }

            fun isLineSeparator(ch: Char): Boolean {
                return ch == '\n' || ch == '\r' || ch == '\u000B' || ch == '\u000C' || ch == '\u0085' || ch == '\u2028' || ch == '\u2029'
            }
        }.findBreaks()
    }

    class Breaks(private val length: Int) {
        private val isBreak = Reusables.isBreak(length).apply { fill(false) }
        private val hasHyphen = Reusables.hasHyphen(length).apply { fill(false) }
        private val isForce = Reusables.isForce(length).apply { fill(false) }

        private val br = Break()

        fun add(index: Int, hasHyphen: Boolean, isForce: Boolean) {
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
                    br.isForce = isForce[i]
                    accept(br)
                }
            }
            br.index = length
            br.hasHyphen = false
            br.isForce = false
            accept(br)
        }
    }

    private object Reusables {
        private val INITIAL_CHARS_CAPACITY = 4000

        val isBreak = ReusableBooleanArray(INITIAL_CHARS_CAPACITY)
        val hasHyphen = ReusableBooleanArray(INITIAL_CHARS_CAPACITY)
        val isForce = ReusableBooleanArray(INITIAL_CHARS_CAPACITY)
    }
}
