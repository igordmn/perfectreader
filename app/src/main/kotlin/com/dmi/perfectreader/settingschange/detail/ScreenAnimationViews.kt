package com.dmi.perfectreader.settingschange.detail

import android.content.Context
import android.widget.ImageView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingschange.SettingsChangeDetails
import com.dmi.perfectreader.settingschange.SettingsChangeDetailsContent
import com.dmi.perfectreader.settingschange.common.SettingListView
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.GridAutoFitLayoutManager
import com.dmi.util.android.view.ViewLoad
import com.dmi.util.android.view.autorun
import com.dmi.util.graphic.Size
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import java.net.URI
import kotlin.reflect.KProperty0

val ScreenAnimationViews = SettingsDetailViews(
        R.string.settingsChangeScreenAnimation,
        SettingsChangeDetailsContent.SCREEN_ANIMATION,
        { ScreenAnimationPreviewView(it) },
        ::screenAnimationListView
)

@Suppress("UNUSED_PARAMETER")
fun screenAnimationListView(context: Context, model: SettingsChangeDetails) = SettingListView(
        context,
        context.main.settings.format::pageAnimationPath,
        context.main.resources.pageAnimations.map { it.toString() },
        ::ScreenAnimationItemView
).apply {
    layoutManager = GridAutoFitLayoutManager(context, columnWidth = dip(64 + 12 * 2))
    setPaddingRelative(dip(12), 0, dip(12), 0)
}

class ScreenAnimationItemView(context: Context) : ImageView(context), Bindable<String> {
    private val previews = context.main.resources.pageAnimationPreviews
    private val load = ViewLoad(this)
    private val previewSize = Size(dip(64), dip(64 * 4 / 3F))

    init {
        minimumWidth = previewSize.width
        minimumHeight = previewSize.height
        padding = dip(12)
    }

    override fun bind(model: String) {
        val path = URI(model)
        setImageBitmap(null)
        load.start {
            val preview = previews.of(path, previewSize)
            setImageBitmap(preview)
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