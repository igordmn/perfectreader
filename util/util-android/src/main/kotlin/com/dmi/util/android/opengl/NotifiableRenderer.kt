package com.dmi.util.android.opengl

import com.dmi.util.graphic.Size
import rx.Observable

interface NotifiableRenderer : FixedRenderer {
    val onNeedDraw: Observable<Unit>
}

class NotifiableRendererWrapper(
        surface: GLSurfaceViewExt,
        private val renderer: NotifiableRenderer
) : FixedRenderer {
    private val subscription = renderer.onNeedDraw.subscribe {
        surface.requestRender()
    }

    override fun destroy() {
        subscription.unsubscribe()
        renderer.destroy()
    }

    override fun draw() = renderer.draw()
}

fun GLSurfaceViewExt.setRenderer(create: (Size) -> NotifiableRenderer) = setRenderer(
        FixedRendererWrapper { size ->
            NotifiableRendererWrapper(this@setRenderer, create(size))
        }
)