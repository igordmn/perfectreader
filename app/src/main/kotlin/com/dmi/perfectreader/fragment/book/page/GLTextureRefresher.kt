package com.dmi.perfectreader.fragment.book.page

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import com.dmi.perfectreader.app.pagePaintScheduler
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.concurrent.ResourceQueueProcessor
import com.dmi.util.graphic.Size
import rx.Subscription

class GLTextureRefresher(size: Size, private val density: Float) {
    private val buffer = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(buffer)
    private val resourceProcessor = ResourceQueueProcessor(buffer, pagePaintScheduler)

    val onNeedRefresh = resourceProcessor.onNeedCheck

    fun destroy() = resourceProcessor.destroy()

    fun scheduleRefresh(texture: GLTexture, paint: (Canvas) -> Unit, afterRefresh: () -> Unit): Subscription {
        val process = { buffer: Bitmap ->
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            canvas.save()
            canvas.scale(density, density)
            paint(canvas)
            canvas.restore()
        }
        return resourceProcessor.scheduleProcess(process) {
            texture.refreshBy(buffer)
            afterRefresh()
        }
    }

    fun refresh() = resourceProcessor.checkComplete()
}