package com.dmi.util.android.font

import android.graphics.Typeface
import com.dmi.util.font.FontCollection
import com.dmi.util.font.FontCollectionCache
import com.dmi.util.font.FontFileCollection
import com.dmi.util.font.StyledFont
import com.dmi.util.log.Log
import java.io.File

fun androidFontCollectionCache(log: Log) = FontCollectionCache(
        log,
        loadSystemFiles = { androidSystemFontFiles(log) },
        createCollection = { getFileCollection: () -> FontFileCollection ->
            FontCollection(
                    getFileCollection,
                    ::loadAndroidFont,
                    ::loadDefaultAndroidFont
            )
        }
)

fun loadAndroidFont(file: File) = AndroidFont(Typeface.createFromFile(file.absolutePath))

fun loadDefaultAndroidFont(styleName: String): StyledFont {
    val style = when (styleName) {
        "Regular" -> Typeface.NORMAL
        "Bold" -> Typeface.NORMAL
        "Italic" -> Typeface.NORMAL
        "Bold Italic" -> Typeface.NORMAL
        else -> Typeface.NORMAL
    }
    val font = AndroidFont(Typeface.create(Typeface.SERIF, style))
    return StyledFont(font, isFakeBold = false, isFakeItalic = false)
}