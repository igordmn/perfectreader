package com.dmi.util.font

import com.dmi.util.collection.putIntoBegin
import com.dmi.util.log.Log
import java.io.File
import java.util.*

fun buildFontFileCollection(log: Log, files: Sequence<File>): FontFileCollection {
    val families = ArrayList<String>()
    val familyToStyles = HashMap<String, ArrayList<FontFileCollection.Style>>()

    for (file in files) {
        if (file.exists() && supportsFontFile(file)) {
            val info: FontInfo? = safeParseInfo(log, file)
            if (info != null) {
                val normalizedFamily = info.familyName.trim()
                val lowercaseFamily = normalizedFamily.toLowerCase()
                if (!familyToStyles.containsKey(lowercaseFamily))
                    families.add(normalizedFamily)
                val styles = familyToStyles.getOrPut(lowercaseFamily) { ArrayList() }
                val styleName = normalizeStyleName(info.styleName)
                styles.add(FontFileCollection.Style(styleName, file, isFakeBold = false, isFakeItalic = false))
            }
        }
    }

    for ((_, styles) in familyToStyles) {
        var regularStyle: FontFileCollection.Style? = null
        var boldStyle: FontFileCollection.Style? = null
        var italicStyle: FontFileCollection.Style? = null
        var boldItalicStyle: FontFileCollection.Style? = null

        for (style in styles) {
            when (style.name) {
                "Regular" -> regularStyle = style
                "Bold" -> boldStyle = style
                "Italic" -> italicStyle = style
                "Bold Italic" -> boldItalicStyle = style
            }
        }

        if (regularStyle != null && boldStyle == null) {
            styles.add(FontFileCollection.Style("Bold", regularStyle.file, isFakeBold = true, isFakeItalic = false))
        }
        if (regularStyle != null && italicStyle == null) {
            styles.add(FontFileCollection.Style("Italic", regularStyle.file, isFakeBold = false, isFakeItalic = true))
        }
        if (boldItalicStyle == null) {
            when {
                regularStyle != null -> {
                    styles.add(FontFileCollection.Style("Bold Italic", regularStyle.file, isFakeBold = true, isFakeItalic = true))
                }
                boldStyle != null -> {
                    styles.add(FontFileCollection.Style("Bold Italic", boldStyle.file, isFakeBold = false, isFakeItalic = true))
                }
                italicStyle != null -> {
                    styles.add(FontFileCollection.Style("Bold Italic", italicStyle.file, isFakeBold = true, isFakeItalic = false))
                }
            }
        }
        val distinctStyles = styles.distinct()
        styles.clear()
        styles.addAll(distinctStyles)
        styles.sortBy { it.name }
        styles.putIntoBegin { it.name == "Bold Italic" }
        styles.putIntoBegin { it.name == "Italic" }
        styles.putIntoBegin { it.name == "Bold" }
        styles.putIntoBegin { it.name == "Regular" }
    }

    families.sort()

    return FontFileCollection(families, familyToStyles)
}

private fun supportsFontFile(file: File): Boolean {
    val ext = file.extension.toLowerCase()
    return ext == "ttf" || ext == "otf"
}

private fun safeParseInfo(log: Log, file: File): FontInfo? = try {
    parseFontInfo(file)
} catch (e: Exception) {
    log.w("font file get info error. file: ${file.absolutePath}; error: ${e.message}")
    null
}

private fun normalizeStyleName(styleName: String): String {
    val trimmed = styleName.trim()
    val lowercase = trimmed.toLowerCase()
    return when (lowercase) {
        "regular", "normal", "standard" -> "Regular"
        "bold" -> "Bold"

        "italic", "oblique",
        "regular italic", "regularitalic", "regular-italic",
        "normal italic", "normalitalic", "normal-italic" -> "Italic"

        "bold italic", "italic bold",
        "bolditalic", "italicbold",
        "bold oblique", "oblique bold",
        "boldoblique", "obliquebold",
        "bold-italic", "italic-bold",
        "bold-oblique", "oblique-bold" -> "Bold Italic"

        else -> trimmed
    }
}

class FontFileCollection(
        val familyNames: List<String>,
        private val lowercaseFamilyToStyles: Map<String, List<Style>>
) {
    fun stylesFor(familyName: String): List<Style> = lowercaseFamilyToStyles[familyName.toLowerCase()]!!

    fun styleFor(familyName: String, styleName: String): Style? {
        return stylesFor(familyName).find {
            it.name.equals(styleName, ignoreCase = true)
        }
    }

    class Style(val name: String, val file: File, val isFakeBold: Boolean, val isFakeItalic: Boolean)
}