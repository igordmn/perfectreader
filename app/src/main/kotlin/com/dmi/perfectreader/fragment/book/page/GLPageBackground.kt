package com.dmi.perfectreader.fragment.book.page

import android.graphics.Canvas
import android.graphics.Color
import com.dmi.util.android.opengl.GLPlane
import com.dmi.util.android.opengl.GLTexture
import rx.Subscription
import rx.lang.kotlin.PublishSubject

class GLPageBackground(
        private val texture: GLTexture,
        private val plane: GLPlane,
        textureRefresher: GLTextureRefresher
) {
    private var refreshSubscription: Subscription
    private var refreshed = false

    val onChanged = PublishSubject<Unit>()

    init {
        val paint = { canvas: Canvas -> canvas.drawColor(Color.WHITE) }
        refreshSubscription = textureRefresher.scheduleRefresh(texture, paint) {
            refreshed = true
            onChanged.onNext(Unit)
        }
    }

    fun destroy() {
        refreshSubscription.unsubscribe()
    }

    fun draw(matrix: FloatArray) {
        if (refreshed)
            plane.draw(matrix, texture)
    }
}