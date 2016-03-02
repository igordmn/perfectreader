package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.config.LayoutChars.OBJECT_REPLACEMENT_CHARACTER
import com.dmi.perfectreader.layout.liner.BreakFinder.Break
import com.dmi.perfectreader.layout.wordbreak.WordBreaker
import com.dmi.util.cache.ReusableBooleanArray
import com.google.common.base.Preconditions.checkArgument
import java.text.BreakIterator
import java.text.BreakIterator.DONE
import java.util.*

class RuleBreakFinder(private val wordBreaker: WordBreaker) : BreakFinder {
    override fun findBreaks(text: String, locale: Locale, accept: (BreakFinder.Break) -> Unit) {
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
                    this.setText(text)
                    first()
                    var i = next()
                    while (i != text.length && i != DONE) {
                        breaks.add(i, false)
                        i = next()
                    }
                }
            }

            fun addWordBreaks() {
                with (BreakIterator.getWordInstance(locale)) {
                    this.setText(text)
                    var begin = first()
                    var i = next()
                    while (i != DONE) {
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
                        breaks.add(i, true)
                }
            }

            fun addObjectBreaks() {
                for (i in 0..text.length - 1) {
                    if (text[i] == OBJECT_REPLACEMENT_CHARACTER) {
                        breaks.add(i, false)
                        if (i + 1 < text.length)
                            breaks.add(i + 1, false)
                    }
                }
            }
        }.findBreaks()
    }

    class Breaks(private val length: Int) {
        private val isBreak = Reusables.isBreak(length).apply { fill(false) }
        private val hasHyphen = Reusables.hasHyphen(length).apply { fill(false) }

        private val br = Break()

        fun add(index: Int, hasHyphen: Boolean) {
            checkArgument(index < length)
            this.isBreak[index] = true
            this.hasHyphen[index] = hasHyphen
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
                    accept(br)
                }
            }
            if (length > 0) {
                br.index = length
                br.hasHyphen = false
                accept(br)
            }
        }
    }

    private object Reusables {
        private val INITIAL_CHARS_CAPACITY = 4000

        val isBreak = ReusableBooleanArray(INITIAL_CHARS_CAPACITY)
        val hasHyphen = ReusableBooleanArray(INITIAL_CHARS_CAPACITY)
    }
}
