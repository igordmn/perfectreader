package com.dmi.perfectreader.book.gl

import android.content.Context
import android.opengl.GLES20.*
import com.dmi.perfectreader.book.render.factory.PageRenderer
import com.dmi.util.android.opengl.GLFrameBuffer
import com.dmi.util.android.opengl.GLQuad
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.android.opengl.bind
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.scope.Scoped

class GLBook(
        model: GLBookModel,
        context: Context,
        pageRenderer: PageRenderer,
        size: Size,
        uriHandler: ProtocolURIHandler
) : Scoped by Scoped.Impl() {
    init {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
        glDepthFunc(GL_LEQUAL)
        glViewport(0, 0, size.width, size.height)
    }

    private val pageTexture by scope.disposable(GLTexture(size))
    private val pageFrameBuffer by scope.disposable(GLFrameBuffer().apply { bindTo(pageTexture) })
    private val quad by scope.disposable(GLQuad(context))
    private val pageAnimation: GLPageAnimation? by scope.asyncDisposable(resetOnRecompute = false) {
        glPageAnimation(uriHandler, model.pageAnimationPath, size)
    }
    private val pageBackground: GLBackground by scope.disposable(GLBackground(size, quad, model, uriHandler))
    private val pages by scope.disposable(GLPages(size, quad, model, pageRenderer))

    fun draw() {
        glClearColor(1F, 1F, 1F, 1F)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        drawAnimatedPage(pages.right, pages.rightProgress)
        drawAnimatedPage(pages.left, pages.leftProgress)
    }

    private fun drawAnimatedPage(page: GLPage?, progress: Float) {
        if (progress <= -1.0 || progress >= 1.0) return

        // При рисовании во фреймбуфер, привязанный к текстуре, нужно рисовать вверх ногами, чтобы текстура была правильно повернута
        // (page/pageBackground рисуются вверх ногами из-за использования GLTextureBackground)
        pageFrameBuffer.bind {
            glClearColor(0F, 0F, 0F, 0F)
            glClear(GL_COLOR_BUFFER_BIT)
            pageBackground.draw()
            page?.draw()
        }

        pageAnimation?.draw(pageTexture, progress)
    }
}