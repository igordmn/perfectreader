package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.modPositive
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.EmittableEvent
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.system.Display

/**
 * Show animation for demonstration (for example, when choose animation in settings)
 */
class DemoAnimatedPages(
        val size: SizeF,
        private val pages: Pages,
        private val display: Display,
        private val animator: PageAnimator,
        scope: Scope = Scope()
) : Disposable by scope {
    companion object {
        fun pages(pages: LoadingPages) = object : Pages {
            override val current: Page? = pages[0]
        }
    }

    private var animation by observable(PageAnimation(display.currentTime))

    val visible: VisiblePages by scope.cached {
        val animationX = (animation.currentPage modPositive 1.0).toFloat()
        VisiblePages(
                left = pages.current,
                right = pages.current,
                future = pages.current,
                leftProgress = -animationX,
                rightProgress = 1 - animationX
        )
    }

    val isMoving: Boolean get() = !animation.isStill

    private val afterAnimate = EmittableEvent()

    init {
        scope.launch {
            while (true) {
                if (!animation.isStill) {
                    val time = display.waitVSyncTime()
                    animation = animator.update(animation, time)
                    if (animation.targetPage == 1.0)
                        animation = animation.copy(targetPage = 0.0)
                } else {
                    afterAnimate.wait()
                }
            }
        }
    }

    fun reset() {
        animation = animation.reset()
    }

    fun animate() {
        animation = PageAnimation(display.currentTime, targetPage = 1.0)
        afterAnimate.emit()
    }

    interface Pages {
        val current: Page?
    }
}