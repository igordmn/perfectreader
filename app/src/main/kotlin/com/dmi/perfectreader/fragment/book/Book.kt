package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationConverter
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.returnUnit
import rx.lang.kotlin.PublishSubject
import java.util.*

class Book(
        private val createAnimated: (SizeF) -> AnimatedBook,
        private val data: BookData,
        val bitmapDecoder: BitmapDecoder,
        val locationConverter: LocationConverter
) : BaseViewModel() {
    val locationObservable = data.locationObservable
    val onIsAnimatingChanged = PublishSubject<Boolean>()
    val onChanged = PublishSubject<Unit>()
    val onPagesChanged = PublishSubject<Unit>()

    val content = data.content
    var selectionRange: LocationRange?
        get() = animated!!.selectionRange
        set(value) = run { animated!!.selectionRange = value }

    val pageContext: PageContext get() = animated!!.pageContext
    val isAnimating: Boolean get() = animated!!.isAnimating
    val loadedPages:  LinkedHashSet<Page> get() = animated!!.loadedPages
    val visibleSlides : ArrayList<AnimatedBook.Slide> get() = animated!!.visibleSlides

    private var animated: AnimatedBook? = null

    fun resize(size: SizeF) {
        animated?.destroy()
        val animated = createAnimated(size)
        animated.onIsAnimatingChanged.subscribe {
            onIsAnimatingChanged.onNext(it)
        }
        animated.onChanged.subscribe {
            onChanged.onNext(Unit)
        }
        animated.onPagesChanged.subscribe {
            onPagesChanged.onNext(Unit)
        }
        this.animated = animated
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

    fun update() {
        animated?.update()
    }
}