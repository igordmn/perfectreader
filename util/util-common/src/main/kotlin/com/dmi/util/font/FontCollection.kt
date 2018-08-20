package com.dmi.util.font

import com.dmi.util.ext.cache
import com.dmi.util.log.Log
import java.io.File

class FontCollection(
        private val getFileCollection: () -> FontFileCollection,
        private val loadFont: (File) -> Font,
        private val loadDefaultFont: (styleName: String) -> StyledFont
) {
    private val fileCollection: FontFileCollection get() = getFileCollection()
    private val fileToFontCache = cache<File, Font>(softValues = true) {
        loadFont(it)
    }
    val familyNames: List<String> = listOf("") + fileCollection.familyNames

    fun loadFont(familyName: String, styleName: String): StyledFont {
        if (familyName == "")
            return loadDefaultFont(styleName)

        val style = fileCollection.styleFor(familyName, styleName)
        return if (style != null) {
            try {
                StyledFont(fileToFontCache[style.file], style.isFakeBold, style.isFakeItalic)
            } catch (e: Exception) {
                loadDefaultFont(styleName)
            }
        } else {
            loadDefaultFont(styleName)
        }
    }
}

class FontCollectionCache(
        private val log: Log,
        loadSystemFiles: () -> Sequence<File>,
        private val createCollection: (getFileCollection: () -> FontFileCollection) -> FontCollection
) {
    private val systemFiles by lazy { loadSystemFiles() }
    private val userFilesToFileCollection = cache<List<File>, FontFileCollection>(maximumSize = 1) { userFiles ->
        buildFontFileCollection(log, systemFiles + userFiles)
    }

    fun collectionFor(userDirectory: File) = createCollection {
        val userFiles = userDirectory.listFiles()?.toList() ?: emptyList()
        userFilesToFileCollection[userFiles]
    }
}