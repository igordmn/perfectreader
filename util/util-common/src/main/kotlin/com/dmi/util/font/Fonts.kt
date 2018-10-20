package com.dmi.util.font

import com.dmi.util.cache.cache
import com.dmi.util.log.Log
import java.io.File

class Fonts(
        private val getFileFonts: () -> FileFonts,
        private val loadFont: (File) -> Font,

        // todo replace by font file in assets folder
        private val loadDefaultFont: (isBold: Boolean, isItalic: Boolean) -> StyledFont,

        // todo replace by font file in assets folder
        private val loadMonospaceFont: (isBold: Boolean, isItalic: Boolean) -> StyledFont
) {
    private val fileFonts: FileFonts get() = getFileFonts()
    private val fileToFontCache = cache<File, Font>(softValues = true) {
        loadFont(it)
    }
    val familyNames: List<String> = listOf("") + fileFonts.familyNames

    fun loadFont(familyName: String, isBold: Boolean, isItalic: Boolean): StyledFont {
        if (familyName == "")
            return loadDefaultFont(isBold, isItalic)
        if (familyName.toLowerCase() == "Monospace")
            return loadMonospaceFont(isBold, isItalic)

        val style = fileFonts[familyName]?.style(isBold, isItalic)
        return if (style != null) {
            try {
                StyledFont(fileToFontCache[style.file], style.isFakeBold, style.isFakeItalic)
            } catch (e: Exception) {
                loadDefaultFont(isBold, isItalic)
            }
        } else {
            loadDefaultFont(isBold, isItalic)
        }
    }
}

class FontsCache(
        private val log: Log,
        loadSystemFiles: () -> Sequence<File>,
        private val createCollection: (getFileFonts: () -> FileFonts) -> Fonts
) {
    private val systemFiles by lazy { loadSystemFiles() }
    private val userFilesToFonts = cache<List<File>, FileFonts>(maximumSize = 1) { userFiles ->
        fileFonts(log, systemFiles + userFiles)
    }

    operator fun get(userDirectory: File) = createCollection {
        val userFiles = userDirectory.listFiles()?.toList() ?: emptyList()
        userFilesToFonts[userFiles]
    }
}