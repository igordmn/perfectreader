package com.dmi.perfectreader.layout.layouter.paragraph.liner.breaker

import java.util.*

class CompositeBreaker(
        private val breaker1: Breaker,
        private val breaker2: Breaker,
        private val breaker3: Breaker
): Breaker {
    override fun breakText(text: String, locale: Locale): Breaks {
        return object: Breaks {
            private val breaks1 = breaker1.breakText(text, locale)
            private val breaks2 = breaker2.breakText(text, locale)
            private val breaks3 = breaker3.breakText(text, locale)

            override fun hasBreakBefore(index: Int): Boolean {
                require(index >= 0 && index < text.length)
                return breaks3.hasBreakBefore(index) || breaks2.hasBreakBefore(index) || breaks1.hasBreakBefore(index)
            }

            override fun hasHyphenBefore(index: Int): Boolean {
                require(index >= 0 && index < text.length)
                return breaks3.hasHyphenBefore(index) || breaks2.hasHyphenBefore(index) || breaks1.hasHyphenBefore(index)
            }
        }
    }
}