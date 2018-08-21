package com.dmi.perfectreader.settingschange

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingschange.SettingValues.TEXT_SIZE
import com.dmi.perfectreader.settingschange.SettingValues.TEXT_SKALEX
import com.dmi.perfectreader.settingschange.SettingValues.TEXT_SKEWX
import com.dmi.perfectreader.settingschange.SettingValues.TEXT_STROKE_WIDTH
import com.dmi.perfectreader.settingschange.common.SettingScrollView
import com.dmi.perfectreader.settingschange.common.settingFloatView
import com.dmi.util.android.font.AndroidFont
import com.dmi.util.android.view.color
import com.dmi.util.font.FontCollection
import com.dmi.util.lang.splitIntoTwoLines
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor

class FontView(
        context: Context,
        private val fontCollection: FontCollection
) : TextView(context) {
    init {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        gravity = Gravity.CENTER
        height = dip(48)
        setPadding(dip(16), 0, dip(16), 0)
        textColor = if (isActivated) color(R.color.onSecondary) else color(R.color.onBackground)
    }

    fun bind(familyName: String) {
        val font = fontCollection.loadFont(familyName, "Regular").font as AndroidFont
        val text = if (familyName == "") "Default" else familyName
        val (firstLine, secondLine) = text.splitIntoTwoLines()
        setText(if (secondLine != null) "$firstLine\n$secondLine" else firstLine)
        typeface = font.typeface
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        textColor = if (isActivated) color(R.color.onSecondary) else color(R.color.onBackground)
    }
}

fun Context.settingsChangeFontView(): RecyclerView {
    fun view() = FontView(this, main.fontCollection)
    return SettingScrollView(
            this,
            main.settings.format::textFontFamily,
            main.fontCollection.familyNames,
            ::view,
            FontView::bind
    )
}

fun Context.settingsChangeFontSize() = settingFloatView(
        main.settings.format::textSizeDip,
        TEXT_SIZE,
        R.drawable.settings_font_size_dec,
        R.drawable.settings_font_size_inc
)

fun Context.settingsChangeFontSkewX() = settingFloatView(
        main.settings.format::textSkewX,
        TEXT_SKEWX,
        R.drawable.settings_font_skewx_dec,
        R.drawable.settings_font_skewx_inc
)

fun Context.settingsChangeFontScaleX() = settingFloatView(
        main.settings.format::textScaleX,
        TEXT_SKALEX,
        R.drawable.settings_font_scalex_dec,
        R.drawable.settings_font_scalex_inc
)

fun Context.settingsChangeFontStrokeWidth() = settingFloatView(
        main.settings.format::textStrokeWidthDip,
        TEXT_STROKE_WIDTH,
        R.drawable.settings_font_stroke_dec,
        R.drawable.settings_font_stroke_inc
)