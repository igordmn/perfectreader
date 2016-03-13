package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.paragraph.HangingConfig
import com.dmi.perfectreader.layout.paragraph.Run
import com.dmi.perfectreader.style.TextAlign
import java.util.*

class LayoutParagraph(
        val fitAreaWidth: Boolean,
        val locale: Locale,
        val runs: List<Run>,
        val firstLineIndent: Float,
        val textAlign: TextAlign,
        val hangingConfig: HangingConfig
) : LayoutObject()
