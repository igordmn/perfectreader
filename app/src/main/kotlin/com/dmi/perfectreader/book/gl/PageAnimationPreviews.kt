package com.dmi.perfectreader.book.gl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.opengl.GLES20.*
import com.dmi.perfectreader.Main
import com.dmi.util.android.opengl.GLFrameBuffer
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.android.opengl.bind
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.scope.resourceScope
import kotlinx.coroutines.withContext
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PageAnimationPreviews(
        private val main: Main,
        private val dip2px: (Float) -> Float = main.dip2px,
        private val uriHandler: ProtocolURIHandler = main.uriHandler
) {
    suspend fun of(path: URI, size: Size): Bitmap {
        val pageBitmap = pageBitmap(size)

        val pixelBuffer = ByteBuffer.allocateDirect(size.width * size.height * 4).order(ByteOrder.nativeOrder())

        withContext(currentGLContext) {
            resourceScope {
                val pageTexture = GLTexture(size).apply { refreshBy(pageBitmap) }.use()
                val previewTexture = GLTexture(size).use()
                val frameBuffer = GLFrameBuffer().apply { bindTo(previewTexture) }.use()
                val animation = glPageAnimation(uriHandler, path, size).use()

                glViewport(0, 0, size.width, size.height)

                frameBuffer.bind {
                    glClearColor(1F, 1F, 1F, 1F)
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

        paint.color = Color.BLACK
        paint.strokeWidth = dip2px(1F)
        canvas.drawColor(Color.WHITE)
        val width = size.width.toFloat()
        val height = size.height.toFloat()
        canvas.drawRect(width * 0.15F, height * 0.15F, width * 0.85F, height * 0.25F, paint)
        canvas.drawRect(width * 0.15F, height * 0.35F, width * 0.85F, height * 0.45F, paint)
        canvas.drawRect(width * 0.15F, height * 0.55F, width * 0.85F, height * 0.65F, paint)
        canvas.drawRect(width * 0.15F, height * 0.75F, width * 0.85F, height * 0.85F, paint)
        return pageBitmap
    }
}