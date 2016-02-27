package com.dmi.perfectreader.layout.liner

import com.dmi.util.annotation.Reusable
import java.util.*

interface BreakFinder {
    fun findBreaks(text: CharSequence, locale: Locale, accept: (Break) -> Unit)

    @Reusable
    interface Break {
        fun index(): Int
        fun hasHyphen(): Boolean
        fun isForce(): Boolean
    }
}
