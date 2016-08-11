package com.dmi.perfectreader.fragment.book

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.Matrix
import android.opengl.Matrix.orthoM
import android.opengl.Matrix.setLookAtM
import com.dmi.perfectreader.fragment.book.page.BitmapBuffer
import com.dmi.perfectreader.fragment.book.page.GLPage
import com.dmi.perfectreader.fragment.book.page.GLPageBackground
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.render.paint.PagePainter
import com.dmi.perfectreader.fragment.book.render.render.PageRenderer
import com.dmi.util.android.opengl.GLColorPlane
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.android.opengl.GLTexturePlane
import com.dmi.util.android.opengl.NotifiableRenderer
import com.dmi.util.android.system.ThreadPriority
import com.dmi.util.android.system.setPriority
import com.dmi.util.collection.ImmediatelyCreatePool
import com.dmi.util.collection.SingleBlockingPool
import com.dmi.util.ext.merge
import com.dmi.util.graphic.Size
import com.dmi.util.refWatcher
import rx.lang.kotlin.PublishSubject
import java.lang.Thread.currentThread
import java.util.*

class GLBook(
        context: Context,
        size: Size,
        private val model: Book,
        private val pageRenderer: PageRenderer,
        private val pagePainter: PagePainter
) : NotifiableRenderer {
    private val sizeF = size.toFloat()

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private val pageColorPlane = GLColorPlane(context, sizeF)
    private val pageTexturePlane = GLTexturePlane(context, sizeF)
    private val pagesTexturePool = ImmediatelyCreatePool(AnimatedBook.MAX_LOADED_PAGES) { GLTexture(size) }
    private val bitmapBufferPool = SingleBlockingPool { BitmapBuffer(size.width, size.height) }

    private val pageBackground = GLPageBackground(pageColorPlane)
    private val pages = GLPages()

    override val onNeedDraw = pageBackground.onChanged merge pages.onChanged merge model.onChanged

    init {
        currentThread().setPriority(ThreadPriority.DISPLAY)

        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
        glViewport(0, 0, size.width, size.height)

        orthoM(projectionMatrix, 0, 0F, sizeF.width, sizeF.height, 0F, -1F, 1F)
        setLookAtM(viewMatrix, 0, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F, 0F)
    }

    override fun destroy() {
        pages.destroy()
        refWatcher.watch(this)
    }

    override fun draw() {
        glClearColor(1F, 1F, 1F, 1F)
        glClear(GL_COLOR_BUFFER_BIT)

        model.update()
        pages.setModel(model.loadedPages, model.pageContext)
        pages.refresh()
        model.visibleSlides.forEach { drawSlide(it) }
    }

    private fun drawSlide(slide: AnimatedBook.Slide) {
        Matrix.translateM(viewMatrix, 0, slide.offsetX, 0F, 0F)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        if (slide.page != null) {
            pages.forPage(slide.page).draw(viewProjectionMatrix)
        } else {
            pageBackground.draw(viewProjectionMatrix)
        }
        Matrix.translateM(viewMatrix, 0, -slide.offsetX, 0F, 0F)
    }

    private inner class GLPages {
        private val pages = LinkedHashSet<Page>()
        private val glPages = ArrayList<GLPage>()
        private val pageToGLPage = HashMap<Page, GLPage>()

        val onChanged = PublishSubject<Unit>()

        fun destroy() {
            glPages.forEach { it.destroy() }
        }

        fun setModel(model: LinkedHashSet<Page>, pageContext: PageContext) {
            val toRemove = this.pages - model
            val toAdd = model - this.pages

            pages.clear()
            glPages.clear()

            toRemove.forEach { page->
                val glPage = pageToGLPage.remove(page)!!
                glPage.destroy()
            }

            toAdd.forEach { page->
                val glPage = GLPage(
                        page, pageContext,
                        pagesTexturePool, pageTexturePlane,
                        pageBackground, bitmapBufferPool,
                        pageRenderer, pagePainter
                )
                glPage.onChanged.subscribe(onChanged)
                pageToGLPage[page] = glPage
            }

            pages.addAll(model)
            pages.forEach {
                glPages.add(forPage(it))
            }

            glPages.forEach {
                it.pageContext = pageContext
            }
        }

        fun refresh() {
            glPages.forEach {
                it.refresh()
            }
        }

        fun forPage(page: Page) = pageToGLPage[page]!!
    }
}