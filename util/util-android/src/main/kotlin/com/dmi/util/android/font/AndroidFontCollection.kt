package com.dmi.util.android.font

import android.graphics.Typeface
import com.dmi.util.font.FontCollection
import com.dmi.util.font.FontCollectionCache
import com.dmi.util.font.FontFileCollection
import com.dmi.util.font.StyledFont
import java.io.File

fun androidFontCollectionCache() =
        FontCollectionCache(::androidSystemFontFiles) { getFileCollection: () -> FontFileCollection ->
            FontCollection(getFileCollection, loadAndroidFont, loadDefaultAndroidFont)
        }

val loadAndroidFont = { file: File ->
    AndroidFont(Typeface.createFromFile(file.absolutePath))
}

val loadDefaultAndroidFont = { styleName: String ->
    val style = when (styleName) {
        "Regular" -> Typeface.NORMAL
        "Bold" -> Typeface.NORMAL
        "Italic" -> Typeface.NORMAL
        "Bold Italic" -> Typeface.NORMAL
        else -> Typeface.NORMAL
    }
    val font = AndroidFont(Typeface.create(Typeface.SERIF, style))
    StyledFont(font, isFakeBold = false, isFakeItalic = false)
}