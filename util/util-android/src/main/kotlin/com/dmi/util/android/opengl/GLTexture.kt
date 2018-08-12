package com.dmi.util.android.opengl

import android.graphics.Bitmap
import android.opengl.GLES20.*
import android.opengl.GLUtils.texSubImage2D
import com.dmi.util.graphic.Rect
import com.dmi.util.graphic.Size
import com.dmi.util.lang.clamp

class GLTexture(size: Size) : GLResource {
    val id = glGenTexture()

    init {
        glBindTexture(GL_TEXTURE_2D, id)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size.width, size.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    override fun dispose() {
        glDelTexture(id)
    }

    fun refreshBy(bitmap: Bitmap) {
        glBindTexture(GL_TEXTURE_2D, id)
        texSubImage2D(GL_TEXTURE_2D, 0, 0, 0, bitmap)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun refreshBy(bitmap: Bitmap, rect: Rect) {
        glBindTexture(GL_TEXTURE_2D, id)

        val left = clamp(rect.left, 0, bitmap.width)
        val top = clamp(rect.top, 0, bitmap.height)
        val right = clamp(rect.right, 0, bitmap.width)
        val bottom = clamp(rect.bottom, 0, bitmap.height)

        if (left == 0 && top == 0 && right == bitmap.width && bottom == bitmap.height) {
            // это быстрее в 2-5 раз для полной текстуры
            texSubImage2D(GL_TEXTURE_2D, 0, 0, 0, bitmap)
        } else {
            texSubImage2D(GL_TEXTURE_2D, 0, left, top, bitmap, left, top, right - left, bottom - top)
        }

        glBindTexture(GL_TEXTURE_2D, 0)
    }

    override fun bind() = glBindTexture(GL_TEXTURE_2D, id)
    override fun unbind() = glBindTexture(GL_TEXTURE_2D, 0)
}