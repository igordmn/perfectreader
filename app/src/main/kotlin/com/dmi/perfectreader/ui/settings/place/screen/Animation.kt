package com.dmi.perfectreader.ui.settings.place.screen

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.dmi.perfectreader.R
import com.dmi.perfectreader.main
import com.dmi.perfectreader.ui.book.Book
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.perfectreader.ui.settings.common.PreviewView
import com.dmi.perfectreader.ui.settings.common.details
import com.dmi.perfectreader.ui.settings.common.list.SettingSingleChoiceListView
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.android.view.*
import com.dmi.util.graphic.Size
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import java.net.URI

fun ViewBuild.screenAnimationDetails(
        model: SettingsUI,
        book: Book,
        glContext: GLContext
) = details(
        model, R.string.settingsUIScreenAnimation,
        SettingSingleChoiceListView(
                context,
                context.main.settings.screen::animationPath,
                context.main.resources.pageAnimations.map { it.toString() },
                {
                    ScreenAnimationPreviewView(context, glContext, size = context.dip(64)).apply {
                        padding = dip(12)
                    }
                },
                GridAutoFitLayoutManager(context, columnWidth = context.dip(64 + 12 * 2)),
                onItemClick = book::showDemoAnimation
        ).apply {
            setPaddingRelative(dip(12), 0, dip(12), 0)
        }
)

fun ViewBuild.screenAnimationPreview(
        glContext: GLContext
) = PreviewView(ScreenAnimationPreviewView(context, glContext, size = context.dip(24)).apply {
    autorun {
        bind(context.main.settings.screen.animationPath)
    }
})

private class ScreenAnimationPreviewViewInner(
        context: Context,
        glContext: GLContext,
        size: Int
) : AppCompatImageView(context), Bindable<String> {
    private val previews = context.main.resources.pageAnimationPreviews(glContext)
    private val load = ViewLoad(this)
    private val previewSize = Size(size, size * 4 / 3)

    override fun bind(model: String) {
        val path = URI(model)
        setImageBitmap(null)
        load.start {
            val preview = previews.of(path, previewSize)
            setImageBitmap(preview)
        }
    }
}

class ScreenAnimationPreviewView(context: Context, glContext: GLContext, size: Int) : FrameLayout(context), Bindable<String> {
    private val inner = ScreenAnimationPreviewViewInner(context, glContext, size) into container(size, size, gravity = Gravity.CENTER)

    override fun bind(model: String) {
        inner.bind(model)
    }
}