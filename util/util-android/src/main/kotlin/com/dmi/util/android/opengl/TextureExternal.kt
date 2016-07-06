package com.dmi.util.android.opengl

import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20.*

class TextureExternal {
    val id = Graphics.glGenTexture()

    init {
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, id)
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0)
    }

    inline fun use(action: () -> Unit) {
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, id)
        action()
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0)
    }
}