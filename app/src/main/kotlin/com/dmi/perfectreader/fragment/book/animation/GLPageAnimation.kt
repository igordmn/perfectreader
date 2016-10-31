package com.dmi.perfectreader.fragment.book.animation

import android.opengl.GLES20.*
import android.opengl.Matrix.*
import com.dmi.util.android.opengl.GLArrayBuffer
import com.dmi.util.android.opengl.GLElementArrayBuffer
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.android.opengl.GLUtils.createProgram
import com.dmi.util.android.opengl.use
import com.dmi.util.graphic.SizeF

private val ROWS = 1
private val COLS = 100

// todo при пересоздании создается большой буфер. нужно использовать тот же
// todo нет метода destroy, уничтожающего программу и буферы
class GLPageAnimation(vertexShader: String, fragmentShader: String, size: SizeF) {
    private val normalizedSize = SizeF(1F, size.width / size.height)

    private val POSITION_SIZE = 2
    private val TEX_COORD_SIZE = 2

    private val programId = createProgram(vertexShader, fragmentShader)
    private val positionHandle = glGetAttribLocation(programId, "pr_position")
    private val texCoordHandle = glGetAttribLocation(programId, "pr_texCoord")
    private val projectionMatrixHandle = glGetUniformLocation(programId, "pr_projectionMatrix")
    private val progressHandle = glGetUniformLocation(programId, "pr_progress")
    private val textureHandle = glGetUniformLocation(programId, "pr_texture")

    private val positions = GLArrayBuffer(GL_STATIC_DRAW,
            FloatArray(POSITION_SIZE * (ROWS + 1) * (COLS + 1)).apply {
                val halfWidth = normalizedSize.width / 2F
                val halfHeight = normalizedSize.height / 2F

                var i = 0
                for (y in 0..ROWS) {
                    for (x in 0..COLS) {
                        this[i++] = halfWidth * (x.toFloat() / COLS * 2 - 1)
                        this[i++] = -halfHeight * (y.toFloat() / ROWS * 2 - 1)
                    }
                }
            }
    )

    private val indices = GLElementArrayBuffer(GL_STATIC_DRAW,
            ShortArray(2 * ROWS * (COLS + 1) + 2 * (ROWS - 1)).apply {
                fun indexAt(x: Int, y: Int) = (y * (COLS + 1) + x).toShort()

                var i = 0
                for (y in 0..ROWS - 1) {
                    // для рисования "degenerate triangles" (невидимые треугольники, связывающие предыдущую и следующую строки)
                    if (y > 0)
                        this[i++] = indexAt(0, y)

                    for (x in 0..COLS) {
                        this[i++] = indexAt(x, y)
                        this[i++] = indexAt(x, y + 1)
                    }

                    // для рисования "degenerate triangles"
                    if (y < ROWS - 1)
                        this[i++] = indexAt(COLS, y + 1)
                }
            }
    )

    private val texCoords = GLArrayBuffer(GL_STATIC_DRAW,
            FloatArray(POSITION_SIZE * (ROWS + 1) * (COLS + 1)).apply {
                var i = 0
                for (y in 0..ROWS) {
                    for (x in 0..COLS) {
                        this[i++] = x.toFloat() / COLS
                        this[i++] = y.toFloat() / ROWS
                    }
                }
            }
    )

    private val projectionMatrix = FloatArray(16).apply {
        val screenZ = normalizedSize.width
        val userZ = screenZ * 2F
        val horizonZ = -userZ * 100F

        val near = userZ - screenZ
        val far = userZ - horizonZ
        val right = near / userZ * normalizedSize.width / 2
        val left = -right
        val top = near / userZ * normalizedSize.height / 2
        val bottom = -top

        val projectionMatrix = FloatArray(16)
        frustumM(projectionMatrix, 0, left, right, bottom, top, near, far)

        val viewMatrix = FloatArray(16)
        setIdentityM(viewMatrix, 0)
        translateM(viewMatrix, 0, 0F, 0F, -userZ)

        multiplyMM(this, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    fun draw(texture: GLTexture, progress: Float) {
        glUseProgram(programId)
        glEnableVertexAttribArray(positionHandle)
        positions.use {
            glVertexAttribPointer(positionHandle, POSITION_SIZE, GL_FLOAT, false, 0, 0)
        }
        glEnableVertexAttribArray(texCoordHandle)
        texCoords.use {
            glVertexAttribPointer(texCoordHandle, TEX_COORD_SIZE, GL_FLOAT, false, 0, 0)
        }
        glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0)
        glUniform1f(progressHandle, progress)
        glUniform1i(textureHandle, 0)
        use(texture, indices) {
            glDrawElements(GL_TRIANGLE_STRIP, indices.size, GL_UNSIGNED_SHORT, 0)
        }
        glUseProgram(0)
    }
}