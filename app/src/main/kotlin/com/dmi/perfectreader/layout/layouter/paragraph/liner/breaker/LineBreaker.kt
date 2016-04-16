package com.dmi.perfectreader.layout.layouter.paragraph.liner.breaker

import com.dmi.util.cache.ReusableBooleanArray
import java.text.BreakIterator
import java.text.BreakIterator.DONE
import java.util.*

class LineBreaker: Breaker {
    override fun breakText(text: String, locale: Locale): Breaks {
        val hasBreakBefore = Reusables.hasBreakBefore(text.length).apply { fill(false) }

        with (BreakIterator.getLineInstance(locale)) {
            setText(text)
            first()
            var i = next()
            while (i != DONE && i < text.length) {
                hasBreakBefore[i] = true
                i = next()
            }
        }

        return object : Breaks {
            override fun hasBreakBefore(index: Int) = hasBreakBefore[index]
            override fun hasHyphenBefore(index: Int) = false
        }
    }

    private object Reusables {
        private val INITIAL_CHARS_CAPACITY = 4000

        val hasBreakBefore = ReusableBooleanArray(INITIAL_CHARS_CAPACITY)
    }
}