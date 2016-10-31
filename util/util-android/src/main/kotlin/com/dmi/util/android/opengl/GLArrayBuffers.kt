package com.dmi.util.android.opengl

import android.opengl.GLES20.*
import com.dmi.util.android.opengl.GLUtils.floatBufferOf
import com.dmi.util.android.opengl.GLUtils.glGenBuffer
import com.dmi.util.android.opengl.GLUtils.shortBufferOf

class GLArrayBuffer(usage: Int, items: FloatArray) : GLResource {
    val id = glGenBuffer()
    val size = items.size

    init {
        use {
            glBufferData(GL_ARRAY_BUFFER, items.size * GLUtils.BYTES_PER_FLOAT, floatBufferOf(items), usage)
        }
    }

    override fun bind() = glBindBuffer(GL_ARRAY_BUFFER, id)
    override fun unbind() = glBindBuffer(GL_ARRAY_BUFFER, 0)
}

class GLElementArrayBuffer(usage: Int, items: ShortArray) : GLResource {
    val id = glGenBuffer()
    val size = items.size

    init {
        use {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, items.size * GLUtils.BYTES_PER_SHORT, shortBufferOf(items), usage)
        }
    }

    override fun bind() = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
    override fun unbind() = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
}