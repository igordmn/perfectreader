package com.dmi.perfectreader.book.gl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmi.util.android.opengl.GLColor
import com.dmi.util.android.opengl.GLObject
import com.dmi.util.android.opengl.GLQuad
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.scope.Scope
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

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
        val bitmap = withContext(CommonPool) {
            Bitmap.createScaledBitmap(BitmapFactory.decodeStream(
                    uriHandler.open(path),
                    null,
                    BitmapFactory.Options()
            ), size.width, size.height, true)
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