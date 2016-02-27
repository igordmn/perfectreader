package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.config.HangingConfig
import com.dmi.perfectreader.layout.run.Run
import com.dmi.perfectreader.style.TextAlign
import java.util.*

class LayoutParagraph(
        private val fillAreaWidth: Boolean,
        private val locale: Locale,
        private val runs: List<Run>,
        private val firstLineIndent: Float,
        private val textAlign: TextAlign,
        private val hangingConfig: HangingConfig) : LayoutObject() {

    fun fillAreaWidth(): Boolean {
        return fillAreaWidth
    }

    fun locale(): Locale {
        return locale
    }

    fun runs(): List<Run> {
        return runs
    }

    fun firstLineIndent(): Float {
        return firstLineIndent
    }

    fun textAlign(): TextAlign {
        return textAlign
    }

    fun hangingConfig(): HangingConfig {
        return hangingConfig
    }
}
