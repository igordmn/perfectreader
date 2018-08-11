package com.dmi.perfectreader.book.layout.paragraph.hyphenator

interface Hyphens {
    fun hasHyphenBefore(index: Int): Boolean
}