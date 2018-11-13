package com.dmi.perfectreader.ui.book.gl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmi.util.android.graphics.resizeSeamCarvingTo
import com.dmi.util.android.opengl.GLColor
import com.dmi.util.android.opengl.GLQuad
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.io.IOException

private val backgroundLoadContext = newSingleThreadContext("backgroundLoad")

class GLBackground(
        size: Size,
        private val quad: GLQuad,
        uriHandler: ProtocolURIHandler,
        private val model: GLBookModel,
        private val scope: Scope = Scope()
) : Disposable by scope {
    private val color: GLColor by scope.cached { GLColor(model.pageBackgroundColor) }
    private val texture: GLTexture? by scope.asyncDisposable {
        if (model.pageBackgroundIsImage) {
            val path = model.pageBackgroundPath
            val contentAwareResize = model.pageBackgroundContentAwareResize
            val bitmap: Bitmap? = withContext(backgroundLoadContext) {
                val original: Bitmap? = try {
                    BitmapFactory.decodeStream(
                            uriHandler.open(path),
                            null,
                            BitmapFactory.Options()
                    )!!
                } catch (e: IOException) {
                    println(e) // todo show toast instead. maybe write into log
                    null
                }
                original?.resize(size, contentAwareResize)
            }
            if (bitmap != null) {
                GLTexture(size).apply { refreshBy(bitmap) }
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun Bitmap.resize(size: Size, contentAware: Boolean): Bitmap {
        return if (contentAware) {
            val aspectRatio: Double = width.toDouble() / height
            val fitted = Bitmap.createScaledBitmap(this, size.width, (size.width / aspectRatio).toInt(), true)
            Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888).apply {
                fitted.resizeSeamCarvingTo(this)
            }
        } else {
            Bitmap.createScaledBitmap(this, size.width, size.height, true)
        }
    }

    fun draw() {
        val texture = texture
        if (texture != null) {
            quad.draw(texture)
        } else {
            color.draw()
        }
    }
}