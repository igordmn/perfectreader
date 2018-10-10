package com.dmi.perfectreader.book.gl

import com.dmi.util.android.opengl.GLSurfaceScopedView
import org.jetbrains.anko.onAttachStateChangeListener
import kotlin.coroutines.CoroutineContext

@Suppress("ObjectPropertyName")
private var _glContext: CoroutineContext? = null

fun GLSurfaceScopedView.provideGLContext() {
    onAttachStateChangeListener {
        onViewAttachedToWindow {
            _glContext = glContext
        }

        onViewDetachedFromWindow {
            _glContext = null
        }
    }
}

val currentGLContext: CoroutineContext get() = _glContext!!