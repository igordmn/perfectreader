package com.dmi.perfectreader.fragment.book.page

import com.dmi.util.android.opengl.GLColorPlane
import com.dmi.util.graphic.Color
import rx.lang.kotlin.PublishSubject

class GLPageBackground(
        private val colorPlane: GLColorPlane
) {
    val onChanged = PublishSubject<Unit>()

    fun draw(matrix: FloatArray) {
        colorPlane.draw(matrix, Color.WHITE)
    }
}