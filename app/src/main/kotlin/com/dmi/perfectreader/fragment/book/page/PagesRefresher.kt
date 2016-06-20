package com.dmi.perfectreader.fragment.book.page

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.dmi.perfectreader.fragment.book.layout.pagination.Page
import com.dmi.perfectreader.fragment.book.layout.pagination.PagePainter
import com.dmi.perfectreader.fragment.book.page.PagesRenderModel.LoadingPage
import com.dmi.util.collection.ImmediatelyCreatePool
import com.dmi.util.graphic.Size
import com.dmi.util.opengl.Texture
import com.dmi.util.refWatcher
import rx.Subscription
import java.util.*

class PagesRefresher(
        private val pagePainter: PagePainter,
        private val pagesRenderer: PagesRenderer,
        private val refreshScheduler: RefreshScheduler,
        size: Size
) {
    val onNeedRefresh = refreshScheduler.onNeedRefresh

    private val loadingPageTexturePool = TexturePool(size, 1)
    private val loadedPagesTexturePool = TexturePool(size, PagesRenderModel.MAX_LOADED_PAGES)

    private var loadingPage: LoadingPage? = null
    private val loadedPages = LinkedHashSet<Page>()

    private val loadedPageToSubscription = HashMap<Page, Subscription>()

    fun destroy() {
        refreshScheduler.destroy()
        refWatcher.watch(this)
    }

    fun refreshBy(pagesModel: PagesRenderModel) {
        refreshLoadingPageBy(pagesModel)
        refreshLoadedPagesBy(pagesModel)

        refreshScheduler.refresh()
    }

    private fun refreshLoadingPageBy(pagesModel: PagesRenderModel) {
        val loadingPage = pagesModel.loadingPage
        if (this.loadingPage != loadingPage) {
            this.loadingPage = loadingPage
            scheduleRefreshLoadingPage()
        }
    }

    private fun scheduleRefreshLoadingPage() {
        refreshScheduler.schedule(object : RefreshScheduler.Refreshable {
            override fun paint(canvas: Canvas) = canvas.drawColor(Color.TRANSPARENT)

            override fun refreshBy(bitmap: Bitmap) {
                pagesRenderer.loadingPageTexture = loadingPageTexturePool.acquire(bitmap)
            }
        })
    }

    private fun refreshLoadedPagesBy(pagesModel: PagesRenderModel) {
        val loadedPages = pagesModel.loadedPages

        val toUnload = this.loadedPages - loadedPages
        val toRefresh = loadedPages - this.loadedPages
        this.loadedPages.removeAll(toUnload)
        this.loadedPages.addAll(toRefresh)

        toUnload.forEach { unload(it) }
        toRefresh.forEach { scheduleRefreshLoadedPage(it) }
    }

    private fun scheduleRefreshLoadedPage(page: Page) {
        loadedPageToSubscription[page] = refreshScheduler.schedule(object : RefreshScheduler.Refreshable {
            override fun paint(canvas: Canvas){
                pagePainter.paint(page, canvas)
            }

            override fun refreshBy(bitmap: Bitmap) {
                pagesRenderer.loadedPageToTexture[page] = loadedPagesTexturePool.acquire(bitmap)
            }
        })
    }

    private fun unload(page: Page) {
        val texture = pagesRenderer.loadedPageToTexture.remove(page)
        texture?.let { loadedPagesTexturePool.release(it) }
        loadedPageToSubscription.remove(page)!!.unsubscribe()
    }

    private class TexturePool(size: Size, count: Int) {
        private val pool = ImmediatelyCreatePool(count) { Texture(size) }

        fun acquire(bitmap: Bitmap): Texture = pool.acquire().apply {
            refreshBy(bitmap)
        }

        fun release(texture: Texture) = pool.release(texture)
    }
}