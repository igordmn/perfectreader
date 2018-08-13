package com.dmi.perfectreader.book.gl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmi.util.android.graphics.resizeSeamCarvingTo
import com.dmi.util.android.opengl.GLColor
import com.dmi.util.android.opengl.GLObject
import com.dmi.util.android.opengl.GLQuad
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.scope.Scope
import kotlinx.coroutines.CommonPool
import kotlinx.coroutines.withContext

suspend fun glBackground(
        size: Size,
        quad: GLQuad,
        model: GLBookModel,
        uriHandler: ProtocolURIHandler
): GLObject {
    return if (model.pageBackgroundIsColor) {
        GLColor(model.pageBackgroundColor)
    } else {
        val path = model.pageBackgroundPath
        val contentAwareResize = model.pageBackgroundContentAwareResize
        val bitmap = withContext(CommonPool) {
            val original = BitmapFactory.decodeStream(
                    uriHandler.open(path),
                    null,
                    BitmapFactory.Options()
            )!!
            if (contentAwareResize) {
                val aspectRatio: Double = original.width.toDouble() / original.height
                val fitted = Bitmap.createScaledBitmap(original, size.width, (size.width / aspectRatio).toInt(), true)
                Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888).apply {
                    fitted.resizeSeamCarvingTo(this)
                }
            } else {
                Bitmap.createScaledBitmap(original, size.width, size.height, true)
            }
        }
        GLBackground(size, quad, bitmap)
    }
}

class GLBackground(
        size: Size,
        private val quad: GLQuad,
        bitmap: Bitmap,
        private val scope: Scope = Scope()
) : GLObject {
    private val texture by scope.disposable(GLTexture(size).apply {
        refreshBy(bitmap)
    })

    override fun dispose() = scope.dispose()
    override fun draw() = quad.draw(texture)
}