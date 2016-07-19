package com.dmi.util.android.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.dmi.util.android.R
import com.dmi.util.graphic.SizeF

class GLTexturePlane(context: Context, size: SizeF) {
    private val VERTEX_COUNT = 4

    private val programId = Graphics.createProgram(context.resources, R.raw.shader_texture_plane_vertex, R.raw.shader_texture_plane_fragment)
    private val coordinateHandle = glGetAttribLocation(programId, "coordinate")
    private val mvpMatrixHandle = glGetUniformLocation(programId, "mvpMatrix")
    private val textureHandle = glGetUniformLocation(programId, "texture")

    private val vertices = Graphics.floatBufferOf(
            0F, 0F, 0F, 0F,
            size.width, 0F, 1F, 0F,
            0F, size.height, 0F, 1F,
            size.width, size.height, 1F, 1F
    )

    fun draw(matrix: FloatArray, texture: GLTexture) {
        texture.use {
            glUseProgram(programId)
            glEnableVertexAttribArray(coordinateHandle)
            glVertexAttribPointer(coordinateHandle, 4, GL_FLOAT, false, 0, vertices)
            glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0)
            glUniform1i(textureHandle, 0)
            glDrawArrays(GL_TRIANGLE_STRIP, 0, VERTEX_COUNT)
            glDisableVertexAttribArray(coordinateHandle)
            glUseProgram(0)
        }
    }
}