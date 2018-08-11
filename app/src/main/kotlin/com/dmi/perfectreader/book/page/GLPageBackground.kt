package com.dmi.perfectreader.book.page

import android.opengl.GLES20.*
import rx.lang.kotlin.PublishSubject

class GLPageBackground {
    val onChanged = PublishSubject<Unit>()

    fun draw() {
        glClearColor(1F, 1F, 1F, 1F)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }
}