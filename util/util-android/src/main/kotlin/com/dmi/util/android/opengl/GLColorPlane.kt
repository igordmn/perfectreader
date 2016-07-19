package com.dmi.util.android.opengl

import android.content.Context
import android.opengl.GLES20.*
import com.dmi.util.android.R
import com.dmi.util.graphic.Color
import com.dmi.util.graphic.SizeF

class GLColorPlane(context: Context, size: SizeF) {
    private val VERTEX_COUNT = 4

    private val programId = Graphics.createProgram(context.resources, R.raw.shader_color_plane_vertex, R.raw.shader_color_plane_fragment)
    private val coordinateHandle = glGetAttribLocation(programId, "coordinate")
    private val mvpMatrixHandle = glGetUniformLocation(programId, "mvpMatrix")
    private val colorHandle = glGetUniformLocation(programId, "color")

    private val vertices = Graphics.floatBufferOf(
            0F, 0F,
            size.width, 0F,
            0F, size.height,
            size.width, size.height
    )

    fun draw(matrix: FloatArray, color: Color) {
        glUseProgram(programId)
        glEnableVertexAttribArray(coordinateHandle)
        glVertexAttribPointer(coordinateHandle, 2, GL_FLOAT, false, 0, vertices)
        glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0)
        glUniform4f(colorHandle, color.red / 255F, color.green / 255F, color.blue / 255F, color.alpha / 255F)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, VERTEX_COUNT)
        glDisableVertexAttribArray(coordinateHandle)
        glUseProgram(0)
    }
}