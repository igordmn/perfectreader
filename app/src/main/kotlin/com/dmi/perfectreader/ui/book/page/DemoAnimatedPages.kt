package com.dmi.perfectreader.ui.book.page

import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.util.graphic.SizeF
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.EmittableEvent
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.system.Display
import com.dmi.util.system.Nanos
import com.dmi.util.system.seconds
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * Show animation for demonstration (for example, when choose animation in settings)
 */
class DemoAnimatedPages(
        val size: SizeF,
        private val pages: Pages,
        private val display: Display,
        private val animator: PageAnimator,
        private val delayBeforeReverse: Nanos = seconds(0.2),
        private val scope: Scope = Scope()
) : Disposable by scope {
    companion object {
        fun pages(pages: LoadingPages) = object : Pages {
            override val current: Page? get() = pages[0]
            override val next: Page? get() = pages[1]
        }
    }

    private var animation by observable(PageAnimation(display.currentTime))

    val visible: VisiblePages by scope.cached {
        val animationX = animation.currentPage.toFloat()
        VisiblePages(
                left = pages.current,
                right = pages.next ?: pages.current,
                future = pages.current,
                leftProgress = -animationX,
                rightProgress = 1 - animationX
        )
    }

    val isMoving: Boolean get() = animation.currentPage != 0.0

    private val afterAnimate = EmittableEvent()

    private var job = job()

    private fun job() = scope.launch {
        while (true) {
            if (!animation.isStill) {
                val time = display.waitVSyncTime()
                animation = animator.update(animation, time)
                if (animation.currentPage == 1.0)
                    reverseAnimation()
            } else {
                afterAnimate.wait()
            }
        }
    }

    private suspend fun reverseAnimation() {
        delay(TimeUnit.NANOSECONDS.toMillis(delayBeforeReverse))
        val time = display.waitVSyncTime()
        animation = animation.copy(targetPage = 0.0, time = time)
    }

    fun reset() {
        job.cancel()
        job = job()
        animation = animation.reset()
    }

    fun animate() {
        job.cancel()
        job = job()
        animation = PageAnimation(display.currentTime, targetPage = 1.0)
        afterAnimate.emit()
    }

    interface Pages {
        val current: Page?
        val next: Page?
    }
}