package com.dmi.perfectreader.settingschange.setting

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingschange.SettingItems
import com.dmi.util.android.font.AndroidFont
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.color
import com.dmi.util.android.view.withTransparency
import com.dmi.util.font.Fonts
import com.dmi.util.lang.splitIntoTwoLines
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor

fun Context.fontFamilyItems() = SettingItems(main.fonts.familyNames, ::FontFamilyItemView, ::FontFamilyPreviewView)

class FontFamilyItemView(context: Context) : TextView(context), Bindable<String> {
    private val fonts: Fonts = context.main.fonts

    init {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        padding = dip(16)
        gravity = Gravity.CENTER
        textColor = if (isActivated) color(R.color.onSecondary) else color(R.color.onBackground)
    }

    override fun bind(model: String) {
        val familyName = model
        val font = fonts.loadFont(familyName, isBold = false, isItalic = false).font as AndroidFont
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

class FontFamilyPreviewView(context: Context) : TextView(context), Bindable<String> {
    private val fonts: Fonts = context.main.fonts

    init {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body2)
        singleLine = true
        ellipsize = TextUtils.TruncateAt.END
        textColor = color(R.color.onBackground).withTransparency(0.60)
    }

    override fun bind(model: String) {
        val familyName = model
        val font = fonts.loadFont(familyName, isBold = false, isItalic = false).font as AndroidFont
        this.text = if (familyName == "") "Default" else familyName
        typeface = font.typeface
    }
}