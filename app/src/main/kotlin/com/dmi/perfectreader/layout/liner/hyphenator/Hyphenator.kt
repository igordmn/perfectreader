package com.dmi.perfectreader.layout.liner.hyphenator

interface Hyphenator {
    fun hyphenateWord(text: CharSequence, beginIndex: Int, endIndex: Int): Hyphens
    fun alphabetContains(ch: Char): Boolean
}
