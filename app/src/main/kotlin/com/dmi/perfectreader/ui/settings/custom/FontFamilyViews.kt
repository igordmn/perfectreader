package com.dmi.perfectreader.ui.settings.custom

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.common.Nano
import com.dmi.perfectreader.main
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.PreviewView
import com.dmi.perfectreader.ui.settings.common.SettingChoiceListView
import com.dmi.perfectreader.ui.settings.common.details
import com.dmi.util.android.font.AndroidFont
import com.dmi.util.android.view.*
import com.dmi.util.font.Fonts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor
import kotlin.reflect.KProperty0

fun fontFamilyDetails(context: Context, model: SettingsUI) = details(
        context, model, R.string.settingsUIFontFamily,
        SettingChoiceListView(
            context,
            context.main.settings.font::fontFamily,
            context.main.resources.fonts.familyNames,
            ::FontFamilyItemView,
            onItemClick = {},
            onItemSecondClick = model.screens::goBackward
        )
)

class FontFamilyItemView(context: Context) : TextView(context), Bindable<String> {
    private val fonts = context.main.resources.fonts
    private val load = ViewLoad(this)

    init {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        padding = dip(12)
        minimumHeight = dip(48)
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        textColor = color(R.color.onBackground)
    }

    override fun bind(model: String) {
        val familyName = model
        text = if (familyName == "") "Default" else familyName
        typeface = Typeface.DEFAULT

        load.start {
            visibility = View.INVISIBLE
            val font = withContext(Dispatchers.Nano) {
                fonts.loadFont(familyName, isBold = false, isItalic = false).font as AndroidFont
            }

            typeface = font.typeface
            visibility = View.VISIBLE
        }
    }
}

fun fontFamilyPreview(context: Context) = PreviewView(FontFamilyPreviewView(context))

class FontFamilyPreviewView(
        context: Context,
        model: KProperty0<String> = context.main.settings.font::fontFamily
) : TextView(context) {
    private val fonts: Fonts = context.main.resources.fonts

    init {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        singleLine = true
        ellipsize = TextUtils.TruncateAt.END
        textColor = color(R.color.onBackground).withOpacity(0.60)

        autorun {
            bind(model.get())
        }
    }

    private fun bind(model: String) {
        val familyName = model
        val font = fonts.loadFont(familyName, isBold = false, isItalic = false).font as AndroidFont
        this.text = if (familyName == "") "Default" else familyName
        typeface = font.typeface
    }
}