package com.dmi.util.android.opengl

import android.graphics.Bitmap
import com.dmi.util.collection.ImmediatelyCreatePool
import com.dmi.util.graphic.Size

class GLTexturePool(size: Size, count: Int) {
    private val pool = ImmediatelyCreatePool(count) { GLTexture(size) }

    fun acquire(bitmap: Bitmap): GLTexture = pool.acquire().apply {
        refreshBy(bitmap)
    }

    fun release(texture: GLTexture) = pool.release(texture)
}