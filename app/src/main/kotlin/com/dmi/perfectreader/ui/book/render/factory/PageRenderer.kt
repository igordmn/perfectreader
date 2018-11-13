package com.dmi.perfectreader.ui.book.render.factory

import android.graphics.Picture
import com.dmi.perfectreader.book.layout.obj.LayoutFrame
import com.dmi.perfectreader.book.layout.obj.LayoutImage
import com.dmi.perfectreader.book.layout.obj.LayoutText
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.ui.book.render.obj.RenderPage
import com.dmi.perfectreader.ui.book.render.obj.RenderPicture
import com.dmi.perfectreader.ui.book.render.obj.RenderSelection
import com.dmi.util.graphic.Rect
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.intCeil
import java.util.*

class PageRenderer(
        private val framePainter: FramePainter,
        private val imagePainter: ImagePainter,
        private val textPainter: TextPainter
) {
    fun render(page: Page): RenderPage {
        val framePicture = Picture()
        val imagePicture = Picture()
        val textInfoList = ArrayList<RenderSelection.TextInfo>(50)
        val textShadowPicture = Picture()
        val textPicture = Picture()

        val frameCanvas = framePicture.beginRecording(page.size)
        val imageCanvas = imagePicture.beginRecording(page.size)
        val textShadowCanvas = textShadowPicture.beginRecording(page.size)
        val textCanvas = textPicture.beginRecording(page.size)

        page.forEachChildRecursive(0F, 0F) { objX, objY, obj ->
            when (obj) {
                is LayoutText -> {
                    textInfoList.add(RenderSelection.TextInfo(objX, objY, obj))
                    textPainter.paintText(objX, objY, obj, textCanvas)
                    textPainter.paintTextShadow(objX, objY, obj, textShadowCanvas)
                }
                is LayoutFrame -> framePainter.paint(objX, objY, obj, frameCanvas)
                is LayoutImage -> imagePainter.paint(objX, objY, obj, imageCanvas)
            }
        }

        textPicture.endRecording()
        textShadowPicture.endRecording()
        imagePicture.endRecording()
        framePicture.endRecording()

        return RenderPage(listOf(
                RenderPicture(framePicture),
                RenderPicture(imagePicture),
                RenderSelection(textInfoList),
                RenderPicture(textShadowPicture),
                RenderPicture(textPicture, page.textGammaCorrection)
        ), Rect(0, 0, intCeil(page.size.width), intCeil(page.size.height)))
    }

    private fun Picture.beginRecording(size: SizeF) = beginRecording(intCeil(size.width), intCeil(size.height))
}