package com.dmi.util.android.font

import android.graphics.Typeface
import com.dmi.util.font.FileFonts
import com.dmi.util.font.Fonts
import com.dmi.util.font.FontsCache
import com.dmi.util.font.StyledFont
import com.dmi.util.log.Log
import java.io.File

fun androidFontsCache(log: Log) = FontsCache(
        log,
        loadSystemFiles = { androidSystemFontFiles(log) },
        createCollection = { getFileFonts: () -> FileFonts ->
            Fonts(
                    getFileFonts,
                    ::loadAndroidFont,
                    ::loadDefaultAndroidFont
            )
        }
)

fun loadAndroidFont(file: File) = AndroidFont(Typeface.createFromFile(file.absolutePath))

fun loadDefaultAndroidFont(isBold: Boolean, isItalic: Boolean): StyledFont {
    val style = when {
        isBold && isItalic -> Typeface.BOLD_ITALIC
        isBold -> Typeface.BOLD
        isItalic -> Typeface.ITALIC
        else -> Typeface.NORMAL
    }
    val font = AndroidFont(Typeface.create(Typeface.SANS_SERIF, style))
    return StyledFont(font, isFakeBold = false, isFakeItalic = false)
}