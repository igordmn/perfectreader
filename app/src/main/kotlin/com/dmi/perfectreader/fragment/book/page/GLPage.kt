package com.dmi.perfectreader.fragment.book.page

import android.graphics.Bitmap
import android.graphics.Canvas
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.paint.PagePainter
import com.dmi.util.android.opengl.GLPlane
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.collection.Pool
import rx.Subscription
import rx.lang.kotlin.PublishSubject

class GLPage(
        val page: Page,
        private val texturePool: Pool<GLTexture>,
        private val plane: GLPlane,
        private val background: GLPageBackground,
        private val pagePainter: PagePainter,
        refreshScheduler: GLRefreshScheduler
) {
    private var texture = texturePool.acquire()
    private var refreshed = false
    private var refreshSubscription: Subscription

    val onChanged = PublishSubject<Unit>()

    init {
        refreshSubscription = refreshScheduler.schedule(object : GLRefreshScheduler.Refreshable {
            override fun paint(canvas: Canvas) {
                pagePainter.paint(page, canvas)
            }

            override fun refreshBy(bitmap: Bitmap) {
                texture.refreshBy(bitmap)
                refreshed = true
                onChanged.onNext(Unit)
            }
        })
    }

    fun destroy() {
        refreshSubscription.unsubscribe()
        texturePool.release(texture)
    }

    fun draw(matrix: FloatArray) {
        background.draw(matrix)
        if (refreshed)
            plane.draw(matrix, texture)
    }
}