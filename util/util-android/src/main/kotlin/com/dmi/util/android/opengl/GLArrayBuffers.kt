package com.dmi.util.android.opengl

import android.opengl.GLES20.*

class GLArrayBuffer(usage: Int, items: FloatArray) : GLResource {
    val id = glGenBuffer()
    val size = items.size

    init {
        bind {
            glBufferData(GL_ARRAY_BUFFER, items.size * BYTES_PER_FLOAT, floatBufferOf(items), usage)
        }
    }

    override fun dispose() {
        glDelBuffer(id)
    }

    override fun bind() = glBindBuffer(GL_ARRAY_BUFFER, id)
    override fun unbind() = glBindBuffer(GL_ARRAY_BUFFER, 0)
}

class GLElementArrayBuffer(usage: Int, items: ShortArray) : GLResource {
    val id = glGenBuffer()
    val size = items.size

    init {
        bind {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, items.size * BYTES_PER_SHORT, shortBufferOf(items), usage)
        }
    }

    override fun dispose() {
        glDelBuffer(id)
    }

    override fun bind() = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
    override fun unbind() = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
}