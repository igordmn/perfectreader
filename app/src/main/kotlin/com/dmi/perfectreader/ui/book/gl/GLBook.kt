package com.dmi.perfectreader.ui.book.gl

import android.content.Context
import android.opengl.GLES20.*
import com.dmi.perfectreader.ui.book.render.factory.PageRenderer
import com.dmi.util.android.opengl.*
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope

class GLBook(
        model: GLBookModel,
        context: Context,
        pageRenderer: PageRenderer,
        private val size: Size,
        uriHandler: ProtocolURIHandler,
        scope: Scope = Scope()
) : Disposable by scope {
    init {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glDepthFunc(GL_LEQUAL)
    }

    private val underColor: GLColor by scope.cached { GLColor(model.themeUnderColor) }
    private val pageTexture by scope.observableDisposable(GLTexture(size))
    private val pageFrameBuffer by scope.observableDisposable(GLFrameBuffer().apply { bindTo(pageTexture) })
    private val quad by scope.observableDisposable(GLQuad(context))
    private val pageAnimation: GLPageAnimation? by scope.asyncDisposable(resetOnRecompute = false) {
        glPageAnimation(uriHandler, model.animationPath, size)
    }
    private val pageBackground: GLBackground by scope.observableDisposable(GLBackground(size, quad, uriHandler, model))
    private val pages by scope.observableDisposable(GLPages(size, quad, model, pageRenderer))

    fun draw() {
        glViewport(0, 0, size.width, size.height)
        underColor.draw()
        drawAnimatedPage(pages.right, pages.rightProgress)
        drawAnimatedPage(pages.left, pages.leftProgress)
    }

    private fun drawAnimatedPage(page: GLPage?, progress: Float) {
        if (progress <= -1.0 || progress >= 1.0) return

        pageFrameBuffer.bind {
            glClearColor(0F, 0F, 0F, 0F)
            glClear(GL_COLOR_BUFFER_BIT)
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
            pageBackground.draw()
            page?.draw()
        }

        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE)
        pageAnimation?.draw(pageTexture, progress)
    }
}