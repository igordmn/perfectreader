package com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator

interface Hyphenator {
    fun hyphenateWord(text: CharSequence, beginIndex: Int, endIndex: Int): Hyphens
    fun alphabetContains(ch: Char): Boolean
}