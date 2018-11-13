package com.dmi.perfectreader.ui.book.gl

import android.opengl.GLES20.*
import android.opengl.Matrix.*
import com.dmi.util.android.opengl.*
import com.dmi.util.graphic.Size
import com.dmi.util.graphic.SizeF
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.xml.getChild
import com.dmi.util.xml.parseXML
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI

suspend fun glPageAnimation(uriHandler: ProtocolURIHandler, uri: URI, size: Size): GLPageAnimation {
    val source = loadAnimationSource(uriHandler, uri)
    return GLPageAnimation(source.vertexShader, source.fragmentShader, size)
}

private suspend fun loadAnimationSource(uriHandler: ProtocolURIHandler, uri: URI): AnimationSource = withContext(Dispatchers.IO) {
    val animation = uriHandler.open(uri).use(::parseXML).documentElement
    require(animation.nodeName == "animation") { "Root tag name should be \"animation\"" }

    val version = animation.getAttribute("version")
    require(version == "1.0") { "Unsupported version" }

    val vertexShader: String = animation.getChild("vertex_shader").textContent
    val fragmentShader: String = animation.getChild("fragment_shader").textContent

    AnimationSource(vertexShader, fragmentShader)
}

private class AnimationSource(val vertexShader: String, val fragmentShader: String)

class GLPageAnimation(
        vertexShader: String,
        fragmentShader: String,
        size: Size,
        scope: Scope = Scope()
) : Disposable by scope {
    private val rows = 1
    private val cols = 100
    private val normalizedSize = SizeF(1F, size.width.toFloat() / size.height)

    private val positionSize = 2
    private val texCoordSize = 2

    private val program by scope.observableDisposable(GLProgram(vertexShader, fragmentShader))
    private val positionHandle = glGetAttribLocation(program.id, "pr_position")
    private val texCoordHandle = glGetAttribLocation(program.id, "pr_texCoord")
    private val projectionMatrixHandle = glGetUniformLocation(program.id, "pr_projectionMatrix")
    private val progressHandle = glGetUniformLocation(program.id, "pr_progress")
    private val textureHandle = glGetUniformLocation(program.id, "pr_texture")

    private val positions by scope.observableDisposable(GLArrayBuffer(GL_STATIC_DRAW,
            FloatArray(positionSize * (rows + 1) * (cols + 1)).apply {
                val halfWidth = normalizedSize.width / 2F
                val halfHeight = normalizedSize.height / 2F

                var i = 0
                for (y in 0..rows) {
                    for (x in 0..cols) {
                        this[i++] = halfWidth * (x.toFloat() / cols * 2 - 1)
                        this[i++] = -halfHeight * (y.toFloat() / rows * 2 - 1)
                    }
                }
            }
    ))

    private val indices by scope.observableDisposable(GLElementArrayBuffer(GL_STATIC_DRAW,
            ShortArray(2 * rows * (cols + 1) + 2 * (rows - 1)).apply {
                fun indexAt(x: Int, y: Int) = (y * (cols + 1) + x).toShort()

                var i = 0
                for (y in 0 until rows) {
                    // для рисования "degenerate triangles" (невидимые треугольники, связывающие предыдущую и следующую строки)
                    if (y > 0)
                        this[i++] = indexAt(0, y)

                    for (x in 0..cols) {
                        this[i++] = indexAt(x, y)
                        this[i++] = indexAt(x, y + 1)
                    }

                    // для рисования "degenerate triangles"
                    if (y < rows - 1)
                        this[i++] = indexAt(cols, y + 1)
                }
            }
    ))

    private val texCoords by scope.observableDisposable(GLArrayBuffer(GL_STATIC_DRAW,
            FloatArray(positionSize * (rows + 1) * (cols + 1)).apply {
                var i = 0
                for (y in 0..rows) {
                    for (x in 0..cols) {
                        this[i++] = x.toFloat() / cols
                        this[i++] = y.toFloat() / rows
                    }
                }
            }
    ))

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

    /**
     * @progress from -1.0 to 1.0
     */
    fun draw(texture: GLTexture, progress: Float) {
        program.bind {
            glEnableVertexAttribArray(positionHandle)
            positions.bind {
                glVertexAttribPointer(positionHandle, positionSize, GL_FLOAT, false, 0, 0)
            }
            glEnableVertexAttribArray(texCoordHandle)
            texCoords.bind {
                glVertexAttribPointer(texCoordHandle, texCoordSize, GL_FLOAT, false, 0, 0)
            }
            glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0)
            glUniform1f(progressHandle, progress)
            glUniform1i(textureHandle, 0)
            bind(texture, indices) {
                glDrawElements(GL_TRIANGLE_STRIP, indices.size, GL_UNSIGNED_SHORT, 0)
            }
        }
    }
}