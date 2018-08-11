package com.dmi.util.android.opengl

import android.opengl.GLES20.*

class GLFrameBuffer: GLResource {
    val id = glGenFrameBuffer()

    fun bindTo(texture: GLTexture) = bind {
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.id, 0)
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            val errorCode = glGetError()
            error("Framebuffer is not completed. Error code: $errorCode")
        }
    }

    override fun dispose() {
        glDelFrameBuffer(id)
    }

    override fun bind() = glBindFramebuffer(GL_FRAMEBUFFER, id)
    override fun unbind() = glBindFramebuffer(GL_FRAMEBUFFER, 0)
}