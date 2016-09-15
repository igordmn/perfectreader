package com.dmi.perfectreader.fragment.book

import android.os.Bundle
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.selection.selectionInitialRange
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.afterSet
import com.dmi.util.lang.returnUnit
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import java.lang.Math.max
import java.lang.Math.min

class Book(
        private val createAnimated: (SizeF) -> AnimatedBook,
        private val data: BookData,
        val bitmapDecoder: BitmapDecoder
) : BaseViewModel() {
    val locationObservable = data.locationObservable
    val isSelectedObservable = BehaviorSubject<Boolean>()
    val onIsAnimatingChanged = PublishSubject<Boolean>()
    val onNewFrame = PublishSubject<Unit>()
    val onPagesChanged = PublishSubject<Unit>()

    val content = data.content
    var selectionRange: LocationRange? by saveState<LocationRange?>(null) afterSet { value ->
        pageContext = PageContext(value)
        isSelected = value != null
        onNewFrame.onNext(Unit)
    }

    val location: Location get() = animated!!.location
    val pageNumber: Int get() = locationConverter.locationToPageNumber(animated!!.location)
    val numberOfPages: Int get() = locationConverter.numberOfPages

    var pageContext: PageContext = PageContext(null)
        private set

    var isSelected: Boolean by rxObservable(false, isSelectedObservable)
        private set
    val isAnimating: Boolean get() = animated?.isAnimating ?: false

    private var animated: AnimatedBook? = null

    val locationConverter: LocationConverter get() = animated!!.locationConverter

    override fun restore(state: Bundle) {
        super.restore(state)
        pageContext = PageContext(selectionRange)
        isSelected = selectionRange != null
    }

    override fun destroy() {
        animated?.destroy()
        super.destroy()
    }

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

    fun reformat() = animated?.reformat().returnUnit()

    fun goLocation(location: Location) {
        data.location = location
        animated?.goLocation(location)
    }

    fun scroll(startPosition: PositionF) = animated!!.scroll(startPosition)

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

    fun goNextPages(increment: Int) = goPageNumber(min(pageNumber + increment, numberOfPages))
    fun goPreviousPages(decrement: Int) = goPageNumber(max(pageNumber - decrement, 1))

    fun goPageNumber(pageNumber: Int) {
        if (this.pageNumber != pageNumber) {
            val newLocation = locationConverter.pageNumberToLocation(pageNumber)
            data.location = newLocation
            animated!!.goLocation(newLocation)
        }
    }

    fun pageAt(relativeIndex: Int) = animated?.pageAt(relativeIndex)

    fun takeFrame(frame: BookFrame) {
        animated!!.takeFrame(frame)
        frame.pageContext = pageContext
    }

    fun startSelectionAtCenter() {
        animated?.let {
            startSelection(it.size.width / 2, it.size.height / 2)
        }
    }

    fun startSelection(x: Float, y: Float) {
        val currentPage = pageAt(0)
        if (currentPage != null)
            selectionRange = selectionInitialRange(content, currentPage, x, y)
    }

    fun cancelSelection() {
        selectionRange = null
    }
}