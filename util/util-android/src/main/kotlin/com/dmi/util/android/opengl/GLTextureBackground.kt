package com.dmi.util.android.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.dmi.util.android.R
import com.dmi.util.android.opengl.GLUtils.createProgram

/**
 * Рисует текстуру вверх ногами (отраженную по Y)
 */
class GLTextureBackground(context: Context) {
    private val VERTEX_COUNT = 4
    private val COORDINATE_SIZE = 4

    private val programId = createProgram(context.resources, R.raw.shader_texture_background_vertex, R.raw.shader_texture_background_fragment)
    private val coordinateHandle = glGetAttribLocation(programId, "coordinate")
    private val textureHandle = glGetUniformLocation(programId, "texture")

    private val coordinates = GLArrayBuffer(GL_STATIC_DRAW, floatArrayOf(
            -1F, -1F, 0F, 0F,
            1F, -1F, 1F, 0F,
            -1F, 1F, 0F, 1F,
            1F, 1F, 1F, 1F
    ))

    fun draw(texture: GLTexture) {
        glUseProgram(programId)
        glEnableVertexAttribArray(coordinateHandle)
        coordinates.use {
            glVertexAttribPointer(coordinateHandle, COORDINATE_SIZE, GL_FLOAT, false, 0, 0)
        }
        glUniform1i(textureHandle, 0)
        texture.use {
            glDrawArrays(GL_TRIANGLE_STRIP, 0, VERTEX_COUNT)
        }
        glUseProgram(0)
    }
}