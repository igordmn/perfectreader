package com.dmi.perfectreader.book.gl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
        val pageBitmap = pageBitmap(size)

        val pixelBuffer = ByteBuffer.allocateDirect(size.width * size.height * 4).order(ByteOrder.nativeOrder())

        glContext.perform {
            resourceScope {
                val animation = glPageAnimation(uriHandler, path, size).use()
                val pageTexture = GLTexture(size).apply { refreshBy(pageBitmap) }.use()
                val previewTexture = GLTexture(size).use()
                val frameBuffer = GLFrameBuffer().apply { bindTo(previewTexture) }.use()

                glViewport(0, 0, size.width, size.height)

                frameBuffer.bind {
                    glClearColor(0F, 0F, 0F, 0F)
                    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                    animation.draw(pageTexture, 0.3F)
                    animation.draw(pageTexture, -0.7F)
                    glReadPixels(0, 0, size.width, size.height, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer)
                    pixelBuffer.rewind()
                }
            }
        }

        val previewBitmap = pageBitmap
        previewBitmap.copyPixelsFromBuffer(pixelBuffer)
        return pageBitmap
    }

    private fun pageBitmap(size: Size): Bitmap {
        val canvas = Canvas()
        val paint = Paint()
        paint.isAntiAlias = true
        val pageBitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(pageBitmap)

        canvas.drawColor(Color.TRANSPARENT)

        val width = size.width.toFloat()
        val height = size.height.toFloat()
        val spacing = width * 0.04F

        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawRect(spacing, spacing, width - spacing, height - spacing, paint)

        paint.color = Color.LTGRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1F
        canvas.drawRect(spacing, spacing, width - spacing, height - spacing, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.DKGRAY
        canvas.drawRect(width * 0.20F, height * 0.15F, width * 0.80F, height * 0.25F, paint)
        canvas.drawRect(width * 0.20F, height * 0.35F, width * 0.80F, height * 0.45F, paint)
        canvas.drawRect(width * 0.20F, height * 0.55F, width * 0.80F, height * 0.65F, paint)
        canvas.drawRect(width * 0.20F, height * 0.75F, width * 0.80F, height * 0.85F, paint)

        return pageBitmap
    }
}