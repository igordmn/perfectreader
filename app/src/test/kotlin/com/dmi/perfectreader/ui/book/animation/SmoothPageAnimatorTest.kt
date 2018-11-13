package com.dmi.perfectreader.ui.book.animation

import com.dmi.perfectreader.ui.book.page.PageAnimation
import com.dmi.perfectreader.ui.book.page.SmoothPageAnimator
import com.dmi.test.shouldNearThreePrecision
import com.dmi.util.system.seconds
import org.junit.Test

@Suppress("IllegalIdentifier")
class SmoothPageAnimatorTest {
    private var animator = SmoothPageAnimator(seconds(10.0))

    @Test
    fun `no move`() = test(seconds = 100.0, page = 2.0) {
        update(seconds = 103.0).currentPage shouldNearThreePrecision 2.0
        update(seconds = 1044.0).currentPage shouldNearThreePrecision 2.0
    }

    @Test
    fun `go next page`() = test(seconds = 100.0, page = 2.0) {
        goNextPage().currentPage shouldNearThreePrecision 2.0
        update(seconds = 100.0).currentPage shouldNearThreePrecision 2.0
        update(seconds = 101.0).currentPage shouldNearThreePrecision 2.028
        update(seconds = 102.0).currentPage shouldNearThreePrecision 2.104
        update(seconds = 103.0).currentPage shouldNearThreePrecision 2.216
        update(seconds = 104.0).currentPage shouldNearThreePrecision 2.352
        update(seconds = 105.0).currentPage shouldNearThreePrecision 2.5
        update(seconds = 106.0).currentPage shouldNearThreePrecision 2.648
        update(seconds = 107.0).currentPage shouldNearThreePrecision 2.784
        update(seconds = 108.0).currentPage shouldNearThreePrecision 2.896
        update(seconds = 109.0).currentPage shouldNearThreePrecision 2.972
        update(seconds = 110.0).currentPage shouldNearThreePrecision 3.0
        update(seconds = 111.0).currentPage shouldNearThreePrecision 3.0
    }

    @Test
    fun `go previous page`() = test(seconds = 100.0, page = 2.0) {
        goPreviousPage().currentPage shouldNearThreePrecision 2.0
        update(seconds = 100.0).currentPage shouldNearThreePrecision 2.0
        update(seconds = 101.0).currentPage shouldNearThreePrecision 1.972
        update(seconds = 102.0).currentPage shouldNearThreePrecision 1.896
        update(seconds = 103.0).currentPage shouldNearThreePrecision 1.784
        update(seconds = 104.0).currentPage shouldNearThreePrecision 1.648
        update(seconds = 105.0).currentPage shouldNearThreePrecision 1.5
        update(seconds = 106.0).currentPage shouldNearThreePrecision 1.352
        update(seconds = 107.0).currentPage shouldNearThreePrecision 1.216
        update(seconds = 108.0).currentPage shouldNearThreePrecision 1.104
        update(seconds = 109.0).currentPage shouldNearThreePrecision 1.028
        update(seconds = 110.0).currentPage shouldNearThreePrecision 1.0
        update(seconds = 111.0).currentPage shouldNearThreePrecision 1.0
    }

    @Test
    fun `go next pages twice`() = test(seconds = 100.0, page = 2.0) {
        goNextPage().currentPage shouldNearThreePrecision 2.0
        goNextPage().currentPage shouldNearThreePrecision 2.0
        update(seconds = 100.0).currentPage shouldNearThreePrecision 2.0
        update(seconds = 101.0).currentPage shouldNearThreePrecision 2.036
        update(seconds = 102.0).currentPage shouldNearThreePrecision 2.135
        update(seconds = 103.0).currentPage shouldNearThreePrecision 2.286
        update(seconds = 104.0).currentPage shouldNearThreePrecision 2.477
        update(seconds = 105.0).currentPage shouldNearThreePrecision 2.695
        update(seconds = 106.0).currentPage shouldNearThreePrecision 2.929
        update(seconds = 107.0).currentPage shouldNearThreePrecision 3.166
        update(seconds = 108.0).currentPage shouldNearThreePrecision 3.395
        update(seconds = 109.0).currentPage shouldNearThreePrecision 3.604
        update(seconds = 110.0).currentPage shouldNearThreePrecision 3.78
        update(seconds = 111.0).currentPage shouldNearThreePrecision 3.912
        update(seconds = 112.0).currentPage shouldNearThreePrecision 3.987
        update(seconds = 113.0).currentPage shouldNearThreePrecision 4.0
        update(seconds = 114.0).currentPage shouldNearThreePrecision 4.0
    }

    @Test
    fun `go next page with velocity`() = test(seconds = 100.0, page = 2.0) {
        goNextPage().currentPage shouldNearThreePrecision 2.0
        addVelocity(0.2).currentPage shouldNearThreePrecision 2.0
        update(seconds = 100.0).currentPage shouldNearThreePrecision 2.0
        update(seconds = 101.0).currentPage shouldNearThreePrecision 2.204
        update(seconds = 102.0).currentPage shouldNearThreePrecision 2.407
        update(seconds = 103.0).currentPage shouldNearThreePrecision 2.598
        update(seconds = 104.0).currentPage shouldNearThreePrecision 2.765
        update(seconds = 105.0).currentPage shouldNearThreePrecision 2.895
        update(seconds = 106.0).currentPage shouldNearThreePrecision 2.977
        update(seconds = 107.0).currentPage shouldNearThreePrecision 3.0
        update(seconds = 108.0).currentPage shouldNearThreePrecision 3.0
    }

    @Test
    fun `go next and previous pages with delays`() = test(seconds = 100.0, page = 2.0) {
        goNextPage().currentPage shouldNearThreePrecision 2.0
        update(seconds = 100.0).currentPage shouldNearThreePrecision 2.0
        update(seconds = 101.0).currentPage shouldNearThreePrecision 2.028
        update(seconds = 102.0).currentPage shouldNearThreePrecision 2.104
        update(seconds = 103.0).currentPage shouldNearThreePrecision 2.216
        update(seconds = 104.0).currentPage shouldNearThreePrecision 2.352
        goNextPage().currentPage shouldNearThreePrecision 2.352
        update(seconds = 105.0).currentPage shouldNearThreePrecision 2.516
        update(seconds = 106.0).currentPage shouldNearThreePrecision 2.712
        update(seconds = 107.0).currentPage shouldNearThreePrecision 2.929
        update(seconds = 108.0).currentPage shouldNearThreePrecision 3.153
        goPreviousPage().currentPage shouldNearThreePrecision 3.153
        update(seconds = 109.0).currentPage shouldNearThreePrecision 3.139
        update(seconds = 109.2).currentPage shouldNearThreePrecision 3.133
        update(seconds = 109.4).currentPage shouldNearThreePrecision 3.127
        update(seconds = 109.6).currentPage shouldNearThreePrecision 3.12
        update(seconds = 109.8).currentPage shouldNearThreePrecision 3.113
        goPreviousPage().currentPage shouldNearThreePrecision 3.113
        update(seconds = 110.0).currentPage shouldNearThreePrecision 3.104
        update(seconds = 110.2).currentPage shouldNearThreePrecision 3.093
        update(seconds = 110.4).currentPage shouldNearThreePrecision 3.08
        update(seconds = 110.6).currentPage shouldNearThreePrecision 3.066
        update(seconds = 110.8).currentPage shouldNearThreePrecision 3.049
        goNextPage().currentPage shouldNearThreePrecision 3.049
        update(seconds = 111.0).currentPage shouldNearThreePrecision 3.033
        update(seconds = 111.2).currentPage shouldNearThreePrecision 3.02
        update(seconds = 111.4).currentPage shouldNearThreePrecision 3.01
        update(seconds = 111.6).currentPage shouldNearThreePrecision 3.004
        update(seconds = 111.8).currentPage shouldNearThreePrecision 3.0
        update(seconds = 112.0).currentPage shouldNearThreePrecision 3.0
    }

    fun test(seconds: Double, page: Double, actions: Tester.() -> Unit) {
        Tester(seconds, page).actions()
    }

    inner class Tester(seconds: Double, page: Double) {
        private var animation = PageAnimation(time = seconds(seconds), currentPage = page, targetPage = page)

        fun update(seconds: Double) = doAction { animator.update(animation, seconds(seconds)) }
        fun addVelocity(velocity: Double) = doAction { animation.copy(velocity = animation.velocity + velocity) }
        fun goNextPage() = doAction { animation.copy(targetPage = animation.targetPage + 1) }
        fun goPreviousPage() = doAction { animation.copy(targetPage = animation.targetPage - 1) }

        fun doAction(action: PageAnimation.() -> PageAnimation): PageAnimation {
            animation = animation.action()
            return animation
        }
    }
}