package com.dmi.util.android.opengl

import android.graphics.Bitmap
import android.opengl.GLES20.*
import android.opengl.GLUtils.texSubImage2D
import com.dmi.util.android.opengl.OpenGL.texSubImage2D
import com.dmi.util.graphic.Rect
import com.dmi.util.graphic.Size

class GLTexture(size: Size) {
    val id = Graphics.glGenTexture()

    init {
        glBindTexture(GL_TEXTURE_2D, id)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size.width, size.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun refreshBy(bitmap: Bitmap, rect: Rect) {
        glBindTexture(GL_TEXTURE_2D, id)
        if (rect.left == 0 && rect.top == 0 && rect.width == bitmap.width && rect.height == bitmap.height) {
            // это быстрее в два раза для полной текстуры
            texSubImage2D(GL_TEXTURE_2D, 0, 0, 0, bitmap)
        } else {
            texSubImage2D(GL_TEXTURE_2D, 0, rect.left, rect.top, bitmap, rect.left, rect.top, rect.width, rect.height)
        }
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    inline fun use(action: () -> Unit) {
        glBindTexture(GL_TEXTURE_2D, id)
        action()
        glBindTexture(GL_TEXTURE_2D, 0)
    }
}