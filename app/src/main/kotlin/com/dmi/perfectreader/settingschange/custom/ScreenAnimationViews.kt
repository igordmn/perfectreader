package com.dmi.perfectreader.settingschange.custom

import android.content.Context
import android.widget.ImageView
import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.main
import com.dmi.perfectreader.settingschange.common.PreviewView
import com.dmi.perfectreader.settingschange.common.SettingListView
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.GridAutoFitLayoutManager
import com.dmi.util.android.view.ViewLoad
import com.dmi.util.android.view.autorun
import com.dmi.util.graphic.Size
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import java.net.URI
import kotlin.reflect.KProperty0

fun screenAnimationDetails(context: Context, book: Book, glContext: GLContext): SettingListView<String, ScreenAnimationItemView> {
    fun itemView(context: Context) = ScreenAnimationItemView(context, glContext)

    return SettingListView(
            context,
            context.main.settings.format::pageAnimationPath,
            context.main.resources.pageAnimations.map { it.toString() },
            ::itemView,
            onItemClick = book::showDemoAnimation
    ).apply {
        layoutManager = GridAutoFitLayoutManager(context, columnWidth = dip(64 + 12 * 2))
        setPaddingRelative(dip(12), 0, dip(12), 0)
    }
}

class ScreenAnimationItemView(context: Context, glContext: GLContext) : ImageView(context), Bindable<String> {
    private val previews = context.main.resources.pageAnimationPreviews(glContext)
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

fun screenAnimationPreview(context: Context, glContext: GLContext) = PreviewView(ScreenAnimationPreviewView(context, glContext))

class ScreenAnimationPreviewView(
        context: Context,
        glContext: GLContext,
        model: KProperty0<String> = context.main.settings.format::pageAnimationPath
) : ImageView(context) {
    private val previews = context.main.resources.pageAnimationPreviews(glContext)
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