package com.dmi.util.opengl

import android.graphics.Bitmap
import android.opengl.GLES20.*
import android.opengl.GLUtils.texSubImage2D
import com.dmi.util.graphic.Size

class Texture(private val size: Size) {
    internal val textureId = Graphics.glGenTexture()

    init {
        glBindTexture(GL_TEXTURE_2D, textureId)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size.width, size.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun refreshBy(bitmap: Bitmap) {
        glBindTexture(GL_TEXTURE_2D, textureId)
        texSubImage2D(GL_TEXTURE_2D, 0, 0, 0, bitmap)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    internal inline fun use(action: () -> Unit) {
        glBindTexture(GL_TEXTURE_2D, textureId)
        action()
        glBindTexture(GL_TEXTURE_2D, 0)
    }
}