package com.dmi.util.android.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.dmi.util.android.R
import com.dmi.util.scope.Disposable

/**
 * Рисует текстуру вверх ногами (отраженную по Y)
 */
class GLQuad(context: Context) : Disposable {
    private val vertexCount = 4
    private val coordinateSize = 4

    private val id = glCreateProgram(context.resources, R.raw.shader_texture_background_vertex, R.raw.shader_texture_background_fragment)
    private val coordinateHandle = glGetAttribLocation(id, "coordinate")
    private val textureHandle = glGetUniformLocation(id, "texture")

    private val coordinates = GLArrayBuffer(GL_STATIC_DRAW, floatArrayOf(
            -1F, -1F, 0F, 1F,
            1F, -1F, 1F, 1F,
            -1F, 1F, 0F, 0F,
            1F, 1F, 1F, 0F
    ))

    override fun dispose() {
        coordinates.dispose()
        glDeleteProgram(id)
    }

    fun draw(texture: GLTexture) {
        glUseProgram(id)
        glEnableVertexAttribArray(coordinateHandle)
        coordinates.bind {
            glVertexAttribPointer(coordinateHandle, coordinateSize, GL_FLOAT, false, 0, 0)
        }
        glUniform1i(textureHandle, 0)
        texture.bind {
            glDrawArrays(GL_TRIANGLE_STRIP, 0, vertexCount)
        }
        glUseProgram(0)
    }
}