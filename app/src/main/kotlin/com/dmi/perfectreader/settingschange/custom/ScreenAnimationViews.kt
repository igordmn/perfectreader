package com.dmi.perfectreader.settingschange.custom

import android.content.Context
import android.widget.ImageView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingschange.SettingsChange
import com.dmi.perfectreader.settingschange.common.PreviewView
import com.dmi.perfectreader.settingschange.common.SettingListView
import com.dmi.perfectreader.settingschange.common.details
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.GridAutoFitLayoutManager
import com.dmi.util.android.view.ViewLoad
import com.dmi.util.android.view.autorun
import com.dmi.util.graphic.Size
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import java.net.URI

fun screenAnimationDetails(
        context: Context,
        model: SettingsChange,
        book: Book,
        glContext: GLContext
) = details(
        context, model, R.string.settingsChangeScreenAnimation,
        SettingListView(
                context,
                context.main.settings.format::pageAnimationPath,
                context.main.resources.pageAnimations.map { it.toString() },
                {
                    ScreenAnimationPreviewView(context, glContext, size = 64).apply {
                        padding = dip(12)
                    }
                },
                onItemClick = book::showDemoAnimation
        ).apply {
            layoutManager = GridAutoFitLayoutManager(context, columnWidth = dip(64 + 12 * 2))
            setPaddingRelative(dip(12), 0, dip(12), 0)
        }
)

fun screenAnimationPreview(
        context: Context, glContext: GLContext
) = PreviewView(ScreenAnimationPreviewView(context, glContext, size = 24).apply {
    autorun {
        bind(context.main.settings.format.pageAnimationPath)
    }
})

class ScreenAnimationPreviewView(
        context: Context,
        glContext: GLContext,
        size: Int
) : ImageView(context), Bindable<String> {
    private val previews = context.main.resources.pageAnimationPreviews(glContext)
    private val load = ViewLoad(this)
    private val previewSize = Size(dip(size), dip(size * 4 / 3F))

    init {
        minimumWidth = previewSize.width
        minimumHeight = previewSize.height
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