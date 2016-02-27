package com.dmi.perfectreader.layout.run

import com.dmi.perfectreader.style.FontStyle

class TextRun(text: String, private val style: FontStyle) : Run() {
    private val text: CharSequence

    init {
        this.text = text
    }

    fun text(): CharSequence {
        return text
    }

    fun style(): FontStyle {
        return style
    }
}
