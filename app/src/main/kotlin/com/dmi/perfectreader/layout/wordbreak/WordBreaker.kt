package com.dmi.perfectreader.layout.wordbreak

import com.dmi.util.annotation.Reusable
import java.util.*

interface WordBreaker {
    fun breakWord(text: CharSequence, locale: Locale, beginIndex: Int, endIndex: Int): WordBreaks

    @Reusable
    interface WordBreaks {
        fun canBreakBefore(index: Int): Boolean
    }
}
