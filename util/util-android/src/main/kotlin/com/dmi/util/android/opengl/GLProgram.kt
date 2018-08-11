package com.dmi.util.android.opengl

import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glUseProgram

class GLProgram(vertexShader: String, fragmentShader: String) : GLResource {
    val id = glCreateProgram(vertexShader, fragmentShader)
    override fun bind() = glUseProgram(id)
    override fun unbind() = glUseProgram(0)
    override fun dispose() = glDeleteProgram(id)
}