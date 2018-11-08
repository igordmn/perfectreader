package com.dmi.perfectreader.book

import com.dmi.perfectreader.book.gl.GLBook
import com.dmi.perfectreader.book.gl.GLBookModel
import com.dmi.perfectreader.book.render.factory.FramePainter
import com.dmi.perfectreader.book.render.factory.ImagePainter
import com.dmi.perfectreader.book.render.factory.PageRenderer
import com.dmi.perfectreader.book.render.factory.TextPainter
import com.dmi.perfectreader.main
import com.dmi.perfectreader.reader.Reader
import com.dmi.util.android.opengl.GLSurfaceScopedView
import com.dmi.util.android.view.ViewBuild
import com.dmi.util.android.view.onSizeChange
import com.dmi.util.graphic.Size

typealias BookView = GLSurfaceScopedView

fun ViewBuild.bookView(reader: Reader): BookView {
    val main = context.main
    val glSurface = GLSurfaceScopedView(context, main.log) {
        val model = GLBookModel(it, main.settings, reader, reader.book)
        val bitmapDecoder = reader.book.bitmapDecoder
        val pageRenderer = PageRenderer(FramePainter(), ImagePainter(bitmapDecoder), TextPainter())
        val uriHandler = main.uriHandler

        val createRenderer = { size: Size ->
            val glBook = GLBook(model, context, pageRenderer, size, uriHandler)

            object : GLSurfaceScopedView.Renderer {
                override fun dispose() = glBook.dispose()
                override fun draw() = glBook.draw()
            }
        }
        createRenderer
    }
    glSurface.keepScreenOn = true
    glSurface.onSizeChange { size, _ ->
        reader.book.size = size.toFloat()
    }
    return glSurface
}