package com.dmi.perfectreader.book

import android.os.Bundle
import com.dmi.perfectreader.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.book.location.Location
import com.dmi.perfectreader.book.location.LocationRange
import com.dmi.perfectreader.book.pagination.page.PageContext
import com.dmi.perfectreader.book.selection.selectionInitialRange
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.afterSet
import com.dmi.util.lang.returnUnit
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import java.net.URI

// todo устранить дублирование с StaticBook, AnimatedBook. мб data перенести в StaticBook
class Book(
        private val createAnimated: (SizeF) -> AnimatedBook,
        private val data: BookData,
        val bitmapDecoder: BitmapDecoder
) : BaseViewModel() {
    val locationObservable = BehaviorSubject(data.location)
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

    var pageContext: PageContext = PageContext(null)
        private set

    var isSelected: Boolean by rxObservable(false, isSelectedObservable)
        private set
    val isAnimating: Boolean get() = animated?.isAnimating ?: false

    val animationUri: URI get() = animated!!.animationUri
    val animatedPages: AnimatedBook.AnimatedPages get() = animated!!.pages
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
        animated.locationObservable.subscribe {
            if (locationObservable.value != it) {
                locationObservable.onNext(it)
                data.location = it
            }
        }
        this.animated = animated
    }

    fun reformat() = animated?.reformat().returnUnit()

    fun scroll() = animated!!.scroll()
    fun goLocation(location: Location) = animated?.goLocation(location)
    fun goPage(relativeIndex: Int) = animated?.goPage(relativeIndex)
    fun movePage(relativeIndex: Int) = animated?.movePage(relativeIndex)

    fun pageAt(relativeIndex: Int) = animated?.pageAt(relativeIndex)

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