package com.dmi.util.opengl

import android.content.res.Resources
import android.graphics.Color
import android.opengl.GLES20

import com.dmi.util.ResourceUtils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

import android.opengl.GLES20.GL_FRAMEBUFFER_BINDING
import android.opengl.GLES20.glGenBuffers
import android.opengl.GLES20.glGenFramebuffers
import android.opengl.GLES20.glGenTextures
import android.opengl.GLES20.glGetIntegerv
import java.lang.String.format

object Graphics {
    val EGL_CONTEXT_CLIENT_VERSION = 0x3098

    val BYTES_PER_FLOAT = 4

    private val intBuffer = IntArray(1)

    fun floatBuffer(items: FloatArray): FloatBuffer {
        val floatBuffer = ByteBuffer.allocateDirect(items.size * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer()
        floatBuffer.put(items).position(0)
        return floatBuffer
    }

    fun vertexBuffer(buffer: FloatBuffer): Int {
        val buffers = IntArray(1)
        GLES20.glGenBuffers(1, buffers, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.capacity() * BYTES_PER_FLOAT,
                buffer, GLES20.GL_STATIC_DRAW)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        return buffers[0]
    }

    fun floatColor(color: Int): FloatBuffer {
        return floatBuffer(floatArrayOf(Color.red(color) / 255f, Color.green(color) / 255f, Color.blue(color) / 255f, Color.alpha(color) / 255f))
    }

    fun compileShader(strSource: String, type: Int): Int {
        val ids = IntArray(1)
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, strSource)
        GLES20.glCompileShader(shader)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, ids, 0)
        if (ids[0] == 0) {
            throw RuntimeException(format("Compile shader failed: %s", GLES20.glGetShaderInfoLog(shader)))
        }
        return shader
    }

    fun createProgram(resources: Resources, vertexShaderResId: Int, fragmentShaderResId: Int): Int {
        val vertexShader = ResourceUtils.readTextRawResource(resources, vertexShaderResId)
        val fragmentShader = ResourceUtils.readTextRawResource(resources, fragmentShaderResId)
        return createProgram(vertexShader, fragmentShader)
    }

    fun createProgram(vertexShader: String, fragmentShader: String): Int {
        val ids = IntArray(1)

        val vShader = compileShader(vertexShader, GLES20.GL_VERTEX_SHADER)
        if (vShader == 0) {
            throw RuntimeException("Vertex shader failed")
        }
        val fShader = compileShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER)
        if (fShader == 0) {
            throw RuntimeException("Fragment shader failed")
        }

        val progId = GLES20.glCreateProgram()

        GLES20.glAttachShader(progId, vShader)
        GLES20.glAttachShader(progId, fShader)

        GLES20.glLinkProgram(progId)
        GLES20.glGetProgramiv(progId, GLES20.GL_LINK_STATUS, ids, 0)
        if (ids[0] <= 0) {
            throw RuntimeException("Linking shader failed")
        }

        GLES20.glDeleteShader(vShader)
        GLES20.glDeleteShader(fShader)

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

    val frameBufferBinding: Int
        get() = synchronized (intBuffer) {
            glGetIntegerv(GL_FRAMEBUFFER_BINDING, intBuffer, 0)
            return intBuffer[0]
        }

    fun eglMakeCurrent(egl: EGL10, eglDisplay: EGLDisplay, eglDrawSurface: EGLSurface, eglReadSurface: EGLSurface, eglContext: EGLContext) {
        if (!egl.eglMakeCurrent(eglDisplay, eglDrawSurface, eglReadSurface, eglContext)) {
            throwEGLException(egl, "eglMakeCurrent error")
        }
    }

    fun throwEGLException(egl: EGL10, message: String) {
        throw RuntimeException(format("%s. 0x%08X", message, egl.eglGetError()))
    }
}
