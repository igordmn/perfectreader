package com.dmi.perfectreader.layout.liner

import com.dmi.util.annotation.Reusable
import java.util.*

interface BreakFinder {
    fun findBreaks(text: CharSequence, locale: Locale, accept: (Break) -> Unit)

    @Reusable
    class Break {
        var index = 0
        var hasHyphen = false
    }
}
