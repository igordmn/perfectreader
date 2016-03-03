package com.dmi.perfectreader.layout.liner.breaker

import com.dmi.perfectreader.layout.config.LayoutChars.OBJECT_REPLACEMENT_CHARACTER
import java.util.*

class ObjectBreaker: Breaker {
    override fun breakText(text: String, locale: Locale): Breaks {
        return object : Breaks {
            override fun hasBreakBefore(index: Int) = isObject(index) || previousIsObject(index)
            override fun hasHyphenBefore(index: Int) = false

            private fun isObject(i: Int) = text[i] == OBJECT_REPLACEMENT_CHARACTER
            private fun previousIsObject(i: Int) = (i > 0 && text[i - 1] == OBJECT_REPLACEMENT_CHARACTER)
        }
    }
}
