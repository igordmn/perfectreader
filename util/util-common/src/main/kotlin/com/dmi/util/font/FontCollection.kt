package com.dmi.util.font

import com.dmi.util.ext.cache
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
    val familyNames: Collection<String> get() = fileCollection.familyNames

    fun loadFont(familyName: String, styleName: String): StyledFont {
        if (familyName == "")
            return loadDefaultFont(familyName)

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
        loadSystemFiles: () -> Sequence<File>,
        private val createCollection: (getFileCollection: () -> FontFileCollection) -> FontCollection
) {
    private val systemFiles by lazy { loadSystemFiles() }
    private val userFilesToFileCollection = cache<List<File>, FontFileCollection>(maximumSize = 1) { userFiles ->
        buildFontFileCollection(systemFiles + userFiles)
    }

    fun collectionFor(userDirectory: File) = createCollection {
        val userFiles = userDirectory.listFiles()?.toList() ?: emptyList()
        userFilesToFileCollection[userFiles]
    }
}