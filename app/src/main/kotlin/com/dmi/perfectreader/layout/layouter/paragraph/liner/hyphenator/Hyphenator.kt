package com.dmi.perfectreader.layout.layouter.paragraph.liner.hyphenator

interface Hyphenator {
    fun hyphenateWord(text: CharSequence, beginIndex: Int, endIndex: Int): Hyphens
    fun alphabetContains(ch: Char): Boolean
}