package com.dmi.util.android.opengl

import android.opengl.GLES20.*
import com.dmi.util.graphic.Color

class GLColor(private val color: Color) {
    fun draw() {
        glClearColor(color.red / 255F, color.green / 255F, color.blue / 255F, color.alpha / 255F)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }
}