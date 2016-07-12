package com.dmi.perfectreader.fragment.book.page

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import com.dmi.perfectreader.app.pagePaintScheduler
import com.dmi.util.collection.SinglePool
import com.dmi.util.ext.LambdaObservable
import com.dmi.util.graphic.Size
import rx.Subscription
import rx.lang.kotlin.PublishSubject
import java.util.*

class GLRefreshScheduler(private val bitmapBuffer: BitmapBuffer) {
    val onNeedRefresh = PublishSubject<Unit>()

    private val bitmapResource = BitmapResource(bitmapBuffer)
    private val queue: Queue<Refreshable> = LinkedList()
    private var currentRefresh: Refresh? = null

    fun destroy() {
        queue.clear()
        currentRefresh?.cancel()
        currentRefresh = null
    }

    fun schedule(refreshable: Refreshable): Subscription {
        queue.offer(refreshable)

        if (currentRefresh == null)
            startNext()

        return object : Subscription {
            override fun isUnsubscribed() = throw UnsupportedOperationException()

            override fun unsubscribe() {
                val current = currentRefresh
                if (current != null && refreshable == current.refreshable) {
                    current.cancel()
                } else {
                    queue.remove(refreshable)
                }
            }
        }
    }

    private fun startNext() {
        val refreshable = queue.poll()
        if (refreshable != null) {
            currentRefresh = Refresh(refreshable) {
                currentRefresh = null
                startNext()
            }
        }
    }

    fun refresh() {
        currentRefresh?.checkComplete()
    }

    private inner class Refresh(
            val refreshable: Refreshable,
            private val onComplete: () -> Unit
    ) {
        private @Volatile var bitmap: Bitmap? = null
        private var cancelled = false

        init {
            LambdaObservable {
                bitmapResource.acquire { refreshable.paint(it) }
            }.subscribeOn(pagePaintScheduler).subscribe {
                bitmap = it
                onNeedRefresh.onNext(Unit)
            }
        }

        fun checkComplete() {
            val bitmap = this.bitmap
            if (bitmap != null) {
                if (!cancelled)
                    refreshable.refreshBy(bitmap)
                bitmapResource.release()
                onComplete()
            }
        }

        fun cancel() {
            cancelled = true
        }
    }

    private class BitmapResource(private val buffer: BitmapBuffer) {
        private val pool = SinglePool { buffer }

        fun acquire(paint: (Canvas) -> Unit): Bitmap = pool.acquire().let {
            it.acquire(paint)
        }

        fun release() = pool.release(buffer)
    }

    class BitmapBuffer(size: Size, private val density: Float) {
        private val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        private val canvas = Canvas(bitmap)

        fun acquire(paint: (Canvas) -> Unit) = bitmap.apply {
            val density = this@BitmapBuffer.density
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            canvas.save()
            canvas.scale(density, density)
            paint(canvas)
            canvas.restore()
        }
    }

    interface Refreshable {
        fun paint(canvas: Canvas)
        fun refreshBy(bitmap: Bitmap)
    }
}