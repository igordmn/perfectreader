package com.dmi.util.font

import com.dmi.util.log.Log
import java.io.File
import java.util.*

fun fileFonts(log: Log, files: Sequence<File>): FileFonts {
    data class TempStyle(val name: String, val file: File, val isFakeBold: Boolean = false, val isFakeItalic: Boolean = false) {
        fun finish() = FileFonts.Style(file, isFakeBold, isFakeItalic)
    }

    val families = ArrayList<String>()
    val familyToStyles = HashMap<String, ArrayList<TempStyle>>()

    for (file in files) {
        if (file.exists() && supportsFontFile(file)) {
            val info: FontInfo? = safeParseInfo(log, file)
            if (info != null) {
                val normalizedFamily = info.familyName.trim()
                val lowercaseFamily = normalizedFamily.toLowerCase()
                if (!familyToStyles.containsKey(lowercaseFamily))
                    families.add(normalizedFamily)
                val styles = familyToStyles.getOrPut(lowercaseFamily) { ArrayList() }
                styles.add(TempStyle(info.styleName, file))
            }
        }
    }

    fun font(tempStyles: ArrayList<TempStyle>): FileFonts.Font {
        val first = tempStyles.first().finish()
        var regular: FileFonts.Style? = null
        var bold: FileFonts.Style? = null
        var italic: FileFonts.Style? = null
        var boldItalic: FileFonts.Style? = null

        for (style in tempStyles) {
            when (normalizeStyleName(style.name)) {
                "Regular" -> regular = style.finish()
                "Bold" -> bold = style.finish()
                "Italic" -> italic = style.finish()
                "Bold Italic" -> boldItalic = style.finish()
            }
        }

        return FileFonts.Font(
                regular = regular ?: when {
                    bold != null -> bold
                    italic != null -> italic
                    boldItalic != null -> boldItalic
                    else -> first
                },
                bold = bold ?: when {
                    regular != null -> regular.copy(isFakeBold = true)
                    boldItalic != null -> boldItalic
                    italic != null -> italic.copy(isFakeBold = true)
                    else -> first.copy(isFakeBold = true)
                },
                italic = italic ?: when {
                    regular != null -> regular.copy(isFakeItalic = true)
                    boldItalic != null -> boldItalic
                    bold != null -> bold.copy(isFakeItalic = true)
                    else -> first.copy(isFakeItalic = true)
                },
                boldItalic = boldItalic ?: when {
                    italic != null -> italic.copy(isFakeBold = true)
                    bold != null -> bold.copy(isFakeItalic = true)
                    regular != null -> regular.copy(isFakeBold = true, isFakeItalic = true)
                    else -> first.copy(isFakeBold = true, isFakeItalic = true)
                }
        )
    }

    return FileFonts(families, familyToStyles.mapValues { font(it.value) })
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

class FileFonts(
        val familyNames: List<String>,
        private val lowercaseFamilyToFont: Map<String, Font>
) {
    operator fun get(familyName: String): Font? = lowercaseFamilyToFont[familyName.toLowerCase()]

    data class Font(
            private val regular: Style,
            private val bold: Style,
            private val italic: Style,
            private val boldItalic: Style
    ) {
        fun style(isBold: Boolean, isItalic: Boolean): Style = when {
            isBold && isItalic -> boldItalic
            isBold -> bold
            isItalic -> italic
            else -> regular
        }
    }

    data class Style(val file: File, val isFakeBold: Boolean, val isFakeItalic: Boolean)
}