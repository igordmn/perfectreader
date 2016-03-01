package com.dmi.util.opengl

import android.content.res.Resources
import android.graphics.Color
import android.opengl.GLES20.*
import com.google.common.io.CharStreams.toString
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object Graphics {
    val BYTES_PER_FLOAT = 4

    private val intBuffer = IntArray(1)

    fun floatBuffer(items: FloatArray): FloatBuffer {
        return ByteBuffer
                .allocateDirect(items.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply { put(items).position(0) }
    }

    fun vertexBuffer(buffer: FloatBuffer): Int {
        val buffers = IntArray(1)
        glGenBuffers(1, buffers, 0)
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0])
        glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * BYTES_PER_FLOAT, buffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        return buffers[0]
    }

    fun floatColor(color: Int): FloatBuffer {
        return floatBuffer(floatArrayOf(
                Color.red(color) / 255F,
                Color.green(color) / 255F,
                Color.blue(color) / 255F,
                Color.alpha(color) / 255F)
        )
    }

    fun compileShader(strSource: String, type: Int): Int {
        val ids = IntArray(1)
        val shader = glCreateShader(type)
        glShaderSource(shader, strSource)
        glCompileShader(shader)
        glGetShaderiv(shader, GL_COMPILE_STATUS, ids, 0)
        if (ids[0] == 0)
            throw RuntimeException("Compile shader failed: ${glGetShaderInfoLog(shader)}")
        return shader
    }

    fun createProgram(resources: Resources, vertexShaderResId: Int, fragmentShaderResId: Int): Int {
        val vertexShader = resources.readShader(vertexShaderResId)
        val fragmentShader = resources.readShader(fragmentShaderResId)
        return createProgram(vertexShader, fragmentShader)
    }

    private fun Resources.readShader(resId: Int) = openRawResource(resId).use { toString(it.reader()) }

    fun createProgram(vertexShader: String, fragmentShader: String): Int {
        val ids = IntArray(1)

        val vShader = compileShader(vertexShader, GL_VERTEX_SHADER)
        if (vShader == 0)
            throw RuntimeException("Vertex shader failed")
        val fShader = compileShader(fragmentShader, GL_FRAGMENT_SHADER)
        if (fShader == 0)
            throw RuntimeException("Fragment shader failed")

        val progId = glCreateProgram()

        glAttachShader(progId, vShader)
        glAttachShader(progId, fShader)

        glLinkProgram(progId)
        glGetProgramiv(progId, GL_LINK_STATUS, ids, 0)
        if (ids[0] <= 0)
            throw RuntimeException("Linking shader failed")

        glDeleteShader(vShader)
        glDeleteShader(fShader)

        return progId
    }

    fun glGenTexture(): Int {
        synchronized (intBuffer) {
            glGenTextures(1, intBuffer, 0)
            return intBuffer[0]
        }
    }

    fun glGenBuffer(): Int {
        synchronized (intBuffer) {
            glGenBuffers(1, intBuffer, 0)
            return intBuffer[0]
        }
    }

    fun glGenFramebuffer(): Int {
        synchronized (intBuffer) {
            glGenFramebuffers(1, intBuffer, 0)
            return intBuffer[0]
        }
    }
}
