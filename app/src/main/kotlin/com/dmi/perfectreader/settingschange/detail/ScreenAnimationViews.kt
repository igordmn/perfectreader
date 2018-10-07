package com.dmi.perfectreader.settingschange.detail

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.dmi.perfectreader.R
import com.dmi.perfectreader.common.Nano
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingschange.SettingsChangeDetails
import com.dmi.perfectreader.settingschange.SettingsChangeDetailsContent
import com.dmi.perfectreader.settingschange.common.SettingListView
import com.dmi.util.android.font.AndroidFont
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.ViewLoad
import com.dmi.util.android.view.autorun
import com.dmi.util.android.view.color
import com.dmi.util.graphic.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor
import java.net.URI
import kotlin.reflect.KProperty0

val ScreenAnimationViews = SettingsDetailViews(
        R.string.settingsChangeScreenAnimation,
        SettingsChangeDetailsContent.SCREEN_ANIMATION,
        { ScreenAnimationPreviewView(it) },
        ::screenAnimationListView
)

fun screenAnimationListView(context: Context, model: SettingsChangeDetails) = SettingListView(
        context,
        model,
        context.main.settings.format::pageAnimationPath,
        context.main.resources.pageAnimations.map { it.path },
        ::FontFamilyItemView
)

class ScreenAnimationItemView(context: Context) : TextView(context), Bindable<String> {
    private val fonts = context.main.resources.fonts
    private val load = ViewLoad(this)

    init {
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Body1)
        padding = dip(16)
        minimumHeight = dip(56)
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

class ScreenAnimationPreviewView(
        context: Context,
        model: KProperty0<String> = context.main.settings.format::pageAnimationPath
) : ImageView(context) {
    private val previews = context.main.resources.pageAnimationPreviews
    private val load = ViewLoad(this)
    private val previewSize = Size(dip(24), dip(24 * 4 / 3F))

    init {
        minimumWidth = previewSize.width
        minimumHeight = previewSize.height
        autorun {
            bind(model.get())
        }
    }

    private fun bind(model: String) {
        val path = URI(model)
        setImageBitmap(null)
        load.start {
            val preview = previews.of(path, previewSize)
            setImageBitmap(preview)
        }
    }
}