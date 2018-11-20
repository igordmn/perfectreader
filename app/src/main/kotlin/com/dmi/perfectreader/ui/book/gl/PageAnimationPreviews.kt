package com.dmi.perfectreader.ui.book.gl

import android.graphics.*
import android.opengl.GLES20.*
import com.dmi.perfectreader.MainContext
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.android.opengl.GLFrameBuffer
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.android.opengl.bind
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.scope.resourceScope
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PageAnimationPreviews(
        private val context: MainContext,
        private val glContext: GLContext,
        private val uriHandler: ProtocolURIHandler = context.uriHandler
) {
    suspend fun of(path: URI, size: Size): Bitmap {
        val pageBitmapLeft = pageBitmap(size, "B")
        val pageBitmapRight = pageBitmap(size, "A")

        val pixelBuffer = ByteBuffer.allocateDirect(size.width * size.height * 4).order(ByteOrder.nativeOrder())

        glContext.perform {
            resourceScope {
                val animation = glPageAnimation(uriHandler, path, size).use()
                val pageTextureLeft = GLTexture(size).apply { refreshBy(pageBitmapLeft) }.use()
                val pageTextureRight = GLTexture(size).apply { refreshBy(pageBitmapRight) }.use()
                val previewTexture = GLTexture(size).use()
                val frameBuffer = GLFrameBuffer().apply { bindTo(previewTexture) }.use()

                glViewport(0, 0, size.width, size.height)

                frameBuffer.bind {
                    glClearColor(0F, 0F, 0F, 0F)
                    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
                    animation.draw(pageTextureRight, 0.3F)
                    animation.draw(pageTextureLeft, -0.7F)
                    glReadPixels(0, 0, size.width, size.height, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer)
                    pixelBuffer.rewind()
                }
            }
        }

        val previewBitmap = pageBitmapLeft  // reuse bitmap
        previewBitmap.copyPixelsFromBuffer(pixelBuffer)
        return pageBitmapLeft
    }

    private fun pageBitmap(size: Size, text: String): Bitmap {
        val paint = Paint()
        paint.isAntiAlias = true
        val canvas = Canvas()
        val pageBitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(pageBitmap)

        canvas.drawColor(Color.TRANSPARENT)

        val width = size.width.toFloat()
        val height = size.height.toFloat()
        val spacing = height * 0.04F

        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawRect(spacing, spacing, width - spacing, height - spacing, paint)

        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = height * 0.01F
        canvas.drawRect(spacing, spacing, width - spacing, height - spacing, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK

        val textBounds = Rect()
        paint.textSize = height * 0.8F
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(text, width / 2 - textBounds.exactCenterX(), height / 2 - textBounds.exactCenterY(), paint)

        return pageBitmap
    }
}