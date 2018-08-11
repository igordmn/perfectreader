package com.dmi.perfectreader.book

import android.widget.FrameLayout
import com.dmi.perfectreader.book.gl.GLBook
import com.dmi.perfectreader.book.gl.GLBookModel
import com.dmi.perfectreader.book.render.factory.FramePainter
import com.dmi.perfectreader.book.render.factory.ImagePainter
import com.dmi.perfectreader.book.render.factory.PageRenderer
import com.dmi.perfectreader.book.render.factory.TextPainter
import com.dmi.perfectreader.common.ViewContext
import com.dmi.perfectreader.reader.Reader
import com.dmi.util.android.base.BaseView
import com.dmi.util.android.opengl.GLSurfaceScopedView
import com.dmi.util.android.widget.onSizeChange
import com.dmi.util.graphic.Size
import com.dmi.util.log.Log
import com.dmi.util.system.ApplicationWindow

class BookView(
        viewContext: ViewContext,
        private val reader: Reader,
        log: Log = viewContext.main.log,
        window: ApplicationWindow = viewContext.window
) : BaseView(FrameLayout(viewContext.android)) {
    private val glSurface = GLSurfaceScopedView(viewContext.android, log) {
        val model = GLBookModel(it, viewContext.main.settings, reader, reader.book)
        val bitmapDecoder = reader.book.bitmapDecoder
        val pageRenderer = PageRenderer(FramePainter(), ImagePainter(bitmapDecoder), TextPainter())
        val context = viewContext.android
        val uriHandler = viewContext.main.uriHandler

        val createRenderer = { size: Size ->
            val glBook = GLBook(model, context, pageRenderer, size, uriHandler)

            object : GLSurfaceScopedView.Renderer {
                override fun dispose() = glBook.dispose()
                override fun draw() = glBook.draw()
            }
        }
        createRenderer
    }

    init {
        widget.addView(glSurface)
        widget.keepScreenOn = true

        glSurface.onSizeChange { size, _ ->
            reader.book.size = size.toFloat()
        }

        autorun {
            if (window.isActive) {
                glSurface.onResume()
            } else {
                glSurface.onPause()
            }
        }
    }

    override fun dispose() {
        glSurface.dispose()
        super.dispose()
    }
}