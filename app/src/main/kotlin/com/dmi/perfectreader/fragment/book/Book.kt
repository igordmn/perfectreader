package com.dmi.perfectreader.fragment.book

import android.os.Bundle
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationConverter
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.afterSet
import com.dmi.util.lang.returnUnit
import rx.lang.kotlin.PublishSubject

class Book(
        private val createAnimated: (SizeF) -> AnimatedBook,
        private val data: BookData,
        val bitmapDecoder: BitmapDecoder,
        val locationConverter: LocationConverter
) : BaseViewModel() {
    val locationObservable = data.locationObservable
    val onIsAnimatingChanged = PublishSubject<Boolean>()
    val onNewFrame = PublishSubject<Unit>()
    val onPagesChanged = PublishSubject<Unit>()

    val content = data.content
    var selectionRange: LocationRange? by saveState<LocationRange?>(null) afterSet { value ->
        pageContext = PageContext(value)
        onNewFrame.onNext(Unit)
    }

    var pageContext: PageContext = PageContext(null)
        private set
    val isAnimating: Boolean get() = animated?.isAnimating ?: false

    private var animated: AnimatedBook? = null

    fun resize(size: SizeF) {
        animated?.destroy()
        val animated = createAnimated(size)
        animated.onIsAnimatingChanged.subscribe {
            onIsAnimatingChanged.onNext(it)
        }
        animated.onNewFrame.subscribe {
            onNewFrame.onNext(Unit)
        }
        animated.onPagesChanged.subscribe {
            onPagesChanged.onNext(Unit)
        }
        this.animated = animated
    }

    override fun restore(state: Bundle) {
        super.restore(state)
        pageContext = PageContext(selectionRange)
    }

    override fun destroy() {
        super.destroy()
        animated?.destroy()
    }

    fun reformat() = animated?.reformat().returnUnit()

    fun goLocation(location: Location) {
        data.location = location
        animated?.goLocation(location)
    }

    fun goNextPage() {
        animated?.let {
            it.goNextPage()
            data.location = it.location
        }
    }

    fun goPreviousPage() {
        animated?.let {
            it.goPreviousPage()
            data.location = it.location
        }
    }

    fun pageAt(relativeIndex: Int) = animated?.pageAt(relativeIndex)

    fun takeFrame(frame: BookFrame) {
        animated!!.takeFrame(frame)
        frame.pageContext = pageContext
    }
}