package com.dmi.perfectreader.fragment.book.layout.paragraph.breaker

import com.dmi.util.text.Chars.OBJECT_REPLACEMENT
import java.util.*

class ObjectBreaker : Breaker {
    override fun breakText(text: String, locale: Locale, config: Breaker.Config): Breaks {
        return object : Breaks {
            override fun hasBreakBefore(index: Int) = isObject(index) || previousIsObject(index)
            override fun hasHyphenBefore(index: Int) = false

            private fun isObject(i: Int) = text[i] == OBJECT_REPLACEMENT
            private fun previousIsObject(i: Int) = (i > 0 && text[i - 1] == OBJECT_REPLACEMENT)
        }
    }
}