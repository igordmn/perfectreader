package com.dmi.perfectreader.book

import android.content.Context
import android.opengl.GLES20.*
import com.dmi.perfectreader.book.AnimatedBook.AnimatedPages
import com.dmi.perfectreader.book.animation.GLLoadablePageAnimation
import com.dmi.perfectreader.book.page.BitmapBuffer
import com.dmi.perfectreader.book.page.GLPage
import com.dmi.perfectreader.book.page.GLPageBackground
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.book.pagination.page.PageContext
import com.dmi.perfectreader.book.render.factory.PageRenderer
import com.dmi.util.android.opengl.*
import com.dmi.util.android.system.ThreadPriority
import com.dmi.util.android.system.setPriority
import com.dmi.util.collection.ImmediatelyCreatePool
import com.dmi.util.collection.SingleBlockingPool
import com.dmi.util.ext.merge
import com.dmi.util.graphic.Size
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.refWatcher
import com.google.common.collect.HashBiMap
import rx.lang.kotlin.PublishSubject
import java.lang.Thread.currentThread

class GLBook(
        context: Context,
        size: Size,
        private val model: Book,
        private val pageRenderer: PageRenderer,
        uriHandler: ProtocolURIHandler
) : NotifiableRenderer {
    private val sizeF = size.toFloat()
    private val pageTexture = GLTexture(size)
    private val pageFrameBuffer = GLFrameBuffer().apply { bindTo(pageTexture) }
    private val textureBackground = GLTextureBackground(context)
    private val pagesTexturePool = ImmediatelyCreatePool(AnimatedPages.MAX_PAGES) { GLTexture(size) }
    private val bitmapBufferPool = SingleBlockingPool { BitmapBuffer(size.width, size.height) }

    private val pageAnimation = GLLoadablePageAnimation(sizeF, model.animationUri, uriHandler)
    private val pageBackground = GLPageBackground()
    private val pages = GLPages()

    override val onNeedDraw = pageBackground.onChanged merge pages.onChanged merge model.onNewFrame merge pageAnimation.onChanged

    init {
        currentThread().setPriority(ThreadPriority.DISPLAY)

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
        glDepthFunc(GL_LEQUAL)
        glViewport(0, 0, size.width, size.height)
    }

    override fun destroy() {
        pages.destroy()
        pageAnimation.destroy()
        refWatcher.watch(this)
    }

    override fun draw() {
        glClearColor(1F, 1F, 1F, 1F)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val pagesModel = model.animatedPages
        pages.refresh(pagesModel, model.pageContext)
        pageAnimation.refresh(model.animationUri)

        drawPageIfVisible(pages.right, pagesModel.rightProgress)
        drawPageIfVisible(pages.left, pagesModel.leftProgress)
    }

    private fun drawPageIfVisible(page: GLPage?, progress: Float) {
        if (progress > -1.0 && progress < 1.0) {
            drawPage(page, progress)
        }
    }

    private fun drawPage(page: GLPage?, progress: Float) {
        // При рисовании во фреймбуфер, привязанный к текстуре, нужно рисовать вверх ногами, чтобы текстура была правильно повернута
        // (page/pageBackground рисуются вверх ногами из-за использования GLTextureBackground)
        pageFrameBuffer.use {
            glClearColor(1F, 1F, 1F, 1F)
            glClear(GL_COLOR_BUFFER_BIT)
            if (page != null) {
                page.draw()
            } else {
                pageBackground.draw()
            }
        }

        pageAnimation.draw(pageTexture, progress)
    }

    private inner class GLPages {
        var left: GLPage? = null
            private set
        var right: GLPage? = null
            private set

        private val pageToGLPage = HashBiMap.create<Page, GLPage>()
        private val pages = pageToGLPage.keys
        private val glPages = pageToGLPage.values

        val onChanged = PublishSubject<Unit>()

        fun destroy() {
            pageToGLPage.values.forEach(GLPage::destroy)
        }

        fun refresh(model: AnimatedPages, pageContext: PageContext) {
            val it = pages.iterator()
            while (it.hasNext()) {
                val page = it.next()
                if (page != model.left && page != model.right && page != model.future) {
                    val glPage = pageToGLPage[page]
                    glPage!!.destroy()
                    it.remove()
                }
            }

            putPage(model.left, pageContext)
            putPage(model.right, pageContext)
            putPage(model.future, pageContext)

            left = glPageFor(model.left)
            right = glPageFor(model.right)

            glPages.forEach {
                it.pageContext = pageContext
                it.refresh()
            }
        }

        private fun putPage(page: Page?, pageContext: PageContext) {
            if (page != null && !pageToGLPage.containsKey(page)) {
                pageToGLPage[page] = createGLPage(page, pageContext)
            }
        }

        private fun glPageFor(page: Page?) = if (page == null) null else pageToGLPage[page]

        private fun createGLPage(page: Page, pageContext: PageContext): GLPage {
            val glPage = GLPage(
                    page, pageContext,
                    pagesTexturePool, textureBackground,
                    pageBackground, bitmapBufferPool,
                    pageRenderer
            )
            glPage.onChanged.subscribe(onChanged)
            return glPage
        }
    }
}