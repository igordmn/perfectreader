package com.dmi.perfectreader.settings

import com.dmi.perfectreader.book.content.common.TextAlign
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class FormatSettings(store: ValueStore) {
    var textAlign by store.value(TextAlign.JUSTIFY)
    var letterSpacingEm by store.value(0F)
    var wordSpacingMultiplier by store.value(1F)
    var lineHeightMultiplier by store.value(1F)
    var paragraphFirstLineIndentEm by store.value(1F)
    var paragraphVerticalMarginEm by store.value(0.5F)
    var hangingPunctuation by store.value(true)
    var hyphenation by store.value(true)

    var paddingLeftDip by store.value(20F)
    var paddingRightDip by store.value(20F)
    var paddingTopDip by store.value(20F)
    var paddingBottomDip by store.value(20F)
}

var FormatSettings.pagePadding: Float
    get() = paddingLeftDip
    set(value) {
        paddingLeftDip = value
        paddingRightDip = value
        paddingTopDip = value
        paddingBottomDip = value
    }

var FormatSettings.textJustify: Boolean
    get() = textAlign == TextAlign.JUSTIFY
    set(value) {
        textAlign = if (value) TextAlign.JUSTIFY else TextAlign.LEFT
    }