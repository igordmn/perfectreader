package com.dmi.perfectreader.book.gl

import com.dmi.util.android.opengl.GLSurfaceScopedView
import org.jetbrains.anko.onAttachStateChangeListener
import kotlin.coroutines.CoroutineContext

private var glContext: CoroutineContext? = null

fun GLSurfaceScopedView.provideGLContext() {
    onAttachStateChangeListener {
        onViewAttachedToWindow {
            glContext = coroutineContext
        }

        onViewDetachedFromWindow {
            glContext = null
        }
    }
}

val currentGLContext: CoroutineContext get() = glContext!!