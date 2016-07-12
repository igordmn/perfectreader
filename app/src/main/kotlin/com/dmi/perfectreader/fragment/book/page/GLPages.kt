package com.dmi.perfectreader.fragment.book.page

import android.content.Context
import android.opengl.Matrix.*
import com.dmi.perfectreader.fragment.book.page.PagesRenderModel.Companion.MAX_LOADED_PAGES
import com.dmi.perfectreader.fragment.book.page.PagesRenderModel.Slide
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.paint.PagePainter
import com.dmi.util.android.opengl.GLPlane
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.collection.ImmediatelyCreatePool
import com.dmi.util.ext.merge
import com.dmi.util.graphic.Size
import com.dmi.util.refWatcher
import rx.lang.kotlin.PublishSubject
import java.util.*

class GLPages(
        context: Context,
        size: Size,
        density: Float,
        private val refreshScheduler: GLRefreshScheduler,
        private val pagePainter: PagePainter
) {
    private val sizeF = size.toFloat()

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private val plane = GLPlane(context, sizeF / density)
    private val pageBackgroundTexture = GLTexture(size)
    private val pagesTexturePool = ImmediatelyCreatePool(MAX_LOADED_PAGES) { GLTexture(size) }

    private val pageBackground = GLPageBackground(pageBackgroundTexture, plane, refreshScheduler)
    private val pages = GLPageSet {
        GLPage(it, pagesTexturePool, plane, pageBackground, pagePainter, refreshScheduler)
    }

    val onNeedDraw = refreshScheduler.onNeedRefresh merge pageBackground.onChanged merge pages.onChanged

    init {
        orthoM(projectionMatrix, 0, 0F, sizeF.width / density, sizeF.height / density, 0F, -1F, 1F)
        setLookAtM(viewMatrix, 0, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F, 0F)
    }

    fun destroy() {
        pages.destroy()
        pageBackground.destroy()
        refreshScheduler.destroy()
        refWatcher.watch(this)
    }

    fun draw(model: PagesRenderModel) {
        pages.setModel(model.loadedPages)

        model.visibleSlides.forEach { drawSlide(it) }
    }

    private fun drawSlide(slide: Slide) {
        translateM(viewMatrix, 0, slide.offsetX, 0F, 0F)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        if (slide.page != null) {
            pages[slide.page].draw(viewProjectionMatrix)
        } else {
            pageBackground.draw(viewProjectionMatrix)
        }
        translateM(viewMatrix, 0, -slide.offsetX, 0F, 0F)
    }

    private class GLPageSet(
            private val createPage: (Page) -> GLPage
    ) {
        private val pages = LinkedHashSet<Page>()
        private val glPages = HashSet<GLPage>()
        private val pageToGLPage = HashMap<Page, GLPage>()

        val onChanged = PublishSubject<Unit>()

        fun destroy() {
            glPages.forEach { it.destroy() }
        }

        fun setModel(model: LinkedHashSet<Page>) {
            val toRemove = this.pages - model
            val toAdd = model - this.pages

            toRemove.forEach { page->
                pages.remove(page)
                val glPage = pageToGLPage.remove(page)!!
                glPage.onChanged.subscribe(onChanged)
                glPages.remove(glPage)
                glPage.destroy()
            }

            toAdd.forEach { page->
                val glPage = createPage(page)
                pages.add(page)
                pageToGLPage[page] = glPage
                glPages.add(glPage)
            }
        }

        operator fun get(page: Page) = pageToGLPage[page]!!
    }
}