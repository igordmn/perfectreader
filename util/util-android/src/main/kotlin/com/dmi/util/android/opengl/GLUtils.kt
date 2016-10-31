package com.dmi.util.android.opengl

import android.content.res.Resources
import android.opengl.GLES20.*
import com.google.common.io.CharStreams.toString
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGL11

object GLUtils {
    val BYTES_PER_FLOAT = 4
    val BYTES_PER_SHORT = 2

    fun floatBufferOf(items: FloatArray): FloatBuffer = ByteBuffer
            .allocateDirect(items.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(items).position(0) }

    fun shortBufferOf(items: ShortArray): ShortBuffer = ByteBuffer
            .allocateDirect(items.size * BYTES_PER_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply { put(items).position(0) }

    fun compileShader(strSource: String, type: Int): Int {
        val ids = IntArray(1)
        val shader = glCreateShader(type)
        glShaderSource(shader, strSource)
        glCompileShader(shader)
        glGetShaderiv(shader, GL_COMPILE_STATUS, ids, 0)
        if (ids[0] == 0) {
            val typeStr = if (type == GL_VERTEX_SHADER) "vertex" else "fragment"
            val msg = glGetShaderInfoLog(shader)
            throw RuntimeException("Compile $typeStr shader failed: $msg")
        }
        return shader
    }

    fun createProgram(resources: Resources, vertexShaderResId: Int, fragmentShaderResId: Int): Int {
        val vertexShader = resources.readShader(vertexShaderResId)
        val fragmentShader = resources.readShader(fragmentShaderResId)
        return createProgram(vertexShader, fragmentShader)
    }

    private fun Resources.readShader(resId: Int) = openRawResource(resId).use { toString(it.reader()) }

    fun createProgram(vertexShader: String, fragmentShader: String): Int {
        val vShader = compileShader(vertexShader, GL_VERTEX_SHADER)
        val fShader = compileShader(fragmentShader, GL_FRAGMENT_SHADER)

        val progId = glCreateProgram()

        glAttachShader(progId, vShader)
        glAttachShader(progId, fShader)

        linkProgram(progId)

        glDeleteShader(vShader)
        glDeleteShader(fShader)

        return progId
    }

    private fun linkProgram(progId: Int) {
        val ids = IntArray(1)
        glLinkProgram(progId)
        glGetProgramiv(progId, GL_LINK_STATUS, ids, 0)
        if (ids[0] <= 0) {
            glGetProgramiv(progId, GL_INFO_LOG_LENGTH, ids, 0)
            val msg = glGetProgramInfoLog(progId)
            throw RuntimeException("Linking shader failed: " + msg)
        }
    }

    fun glGenTexture(): Int {
        val ids = IntArray(1)
        glGenTextures(1, ids, 0)
        return ids[0]
    }

    fun glGenFrameBuffer(): Int {
        val ids = IntArray(1)
        glGenFramebuffers(1, ids, 0)
        return ids[0]
    }

    fun glGenBuffer(): Int {
        val ids = IntArray(1)
        glGenBuffers(1, ids, 0)
        return ids[0]
    }

    fun getEGLErrorString(error: Int) = when (error) {
        EGL10.EGL_SUCCESS -> "EGL_SUCCESS"
        EGL10.EGL_NOT_INITIALIZED -> "EGL_NOT_INITIALIZED"
        EGL10.EGL_BAD_ACCESS -> "EGL_BAD_ACCESS"
        EGL10.EGL_BAD_ALLOC -> "EGL_BAD_ALLOC"
        EGL10.EGL_BAD_ATTRIBUTE -> "EGL_BAD_ATTRIBUTE"
        EGL10.EGL_BAD_CONFIG -> "EGL_BAD_CONFIG"
        EGL10.EGL_BAD_CONTEXT -> "EGL_BAD_CONTEXT"
        EGL10.EGL_BAD_CURRENT_SURFACE -> "EGL_BAD_CURRENT_SURFACE"
        EGL10.EGL_BAD_DISPLAY -> "EGL_BAD_DISPLAY"
        EGL10.EGL_BAD_MATCH -> "EGL_BAD_MATCH"
        EGL10.EGL_BAD_NATIVE_PIXMAP -> "EGL_BAD_NATIVE_PIXMAP"
        EGL10.EGL_BAD_NATIVE_WINDOW -> "EGL_BAD_NATIVE_WINDOW"
        EGL10.EGL_BAD_PARAMETER -> "EGL_BAD_PARAMETER"
        EGL10.EGL_BAD_SURFACE -> "EGL_BAD_SURFACE"
        EGL11.EGL_CONTEXT_LOST -> "EGL_CONTEXT_LOST"
        else -> "0x" + Integer.toHexString(error)
    }
}