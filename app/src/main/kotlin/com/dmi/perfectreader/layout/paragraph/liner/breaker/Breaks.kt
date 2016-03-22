package com.dmi.perfectreader.layout.paragraph.liner.breaker

interface Breaks {
    fun hasBreakBefore(index: Int): Boolean
    fun hasHyphenBefore(index: Int): Boolean
}
