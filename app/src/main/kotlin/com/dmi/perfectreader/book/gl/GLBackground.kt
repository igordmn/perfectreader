package com.dmi.perfectreader.book.gl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmi.util.android.graphics.resizeSeamCarvingTo
import com.dmi.util.android.opengl.GLColor
import com.dmi.util.android.opengl.GLObject
import com.dmi.util.android.opengl.GLQuad
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.graphic.Color
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.scope.Scope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URI

private val backgroundLoadContext = newSingleThreadContext("backgroundLoad")

fun glBookBackground(
        size: Size,
        quad: GLQuad,
        uriHandler: ProtocolURIHandler,
        model: GLBookModel
) = GLBackground(
        size, quad, uriHandler,
        object : GLBackground.Model {
            override val isImage get() = model.bookBackgroundIsImage
            override val color get() = model.bookBackgroundColor
            override val path get() = model.bookBackgroundPath
            override val contentAwareResize get() = model.bookBackgroundContentAwareResize
        }
)

fun glPageBackground(
        size: Size,
        quad: GLQuad,
        uriHandler: ProtocolURIHandler,
        model: GLBookModel
) = GLBackground(
        size, quad, uriHandler,
        object : GLBackground.Model {
            override val isImage get() = model.pageBackgroundIsImage
            override val color get() = model.pageBackgroundColor
            override val path get() = model.pageBackgroundPath
            override val contentAwareResize get() = model.pageBackgroundContentAwareResize
        }
)

class GLBackground(
        size: Size,
        private val quad: GLQuad,
        uriHandler: ProtocolURIHandler,
        model: Model,
        private val scope: Scope = Scope()
) : GLObject {
    private val color: GLColor by scope.cachedDisposable { GLColor(model.color) }
    private val texture: GLTexture? by scope.asyncDisposable {
        if (model.isImage) {
            val path = model.path
            val contentAwareResize = model.contentAwareResize
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

    override fun dispose() = scope.dispose()

    override fun draw() {
        val texture = texture
        if (texture != null) {
            quad.draw(texture)
        } else {
            color.draw()
        }
    }

    interface Model {
        val isImage: Boolean
        val color: Color
        val path: URI
        val contentAwareResize: Boolean
    }
}