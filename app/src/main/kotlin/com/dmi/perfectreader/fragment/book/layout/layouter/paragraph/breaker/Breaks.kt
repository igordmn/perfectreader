package com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker

interface Breaks {
    fun hasBreakBefore(index: Int): Boolean
    fun hasHyphenBefore(index: Int): Boolean
}

object NoneBreaks : Breaks {
    override fun hasBreakBefore(index: Int) = false
    override fun hasHyphenBefore(index: Int) = false
}