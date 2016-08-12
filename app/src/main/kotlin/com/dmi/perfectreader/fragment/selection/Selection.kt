package com.dmi.perfectreader.fragment.selection

import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.selection.positionOf
import com.dmi.perfectreader.fragment.book.selection.selectionCaretAt
import com.dmi.perfectreader.fragment.book.selection.selectionCaretNearestTo
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.PositionF
import com.dmi.util.mainScheduler
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.BehaviorSubject

class Selection(private val book: Book) : BaseViewModel() {
    val leftHandleObservable = BehaviorSubject<Handle>()
    val rightHandleObservable = BehaviorSubject<Handle>()

    var leftHandle: Handle by rxObservable(Handle.Invisible, leftHandleObservable)
        private set
    var rightHandle: Handle by rxObservable(Handle.Invisible, rightHandleObservable)
        private set

    init {
        updateHandles()

        subscribe(book.onIsAnimatingChanged.observeOn(mainScheduler)) {
            updateHandles()
        }
        subscribe(book.onPagesChanged.observeOn(mainScheduler)) {
            updateHandles()
        }
    }

    private fun updateHandles() {
        val currentPage = book.pageAt(0)
        val selectionRange = book.selectionRange
        val isAnimating = book.isAnimating

        if (currentPage != null && selectionRange != null && !isAnimating) {
            val pageRange = currentPage.range

            fun createHandle(location: Location, isLeft: Boolean, isNotOnPage: Boolean) : Handle {
                val caret = selectionCaretAt(currentPage, location, isLeft)
                val position = if (caret != null) positionOf(currentPage, caret) else null
                return if (position != null) {
                    if (isNotOnPage) Handle.NotOnPage(position) else Handle.Visible(position)
                } else {
                    Handle.Invisible
                }
            }

            leftHandle = when {
                selectionRange.begin < pageRange.begin && selectionRange.end <= pageRange.begin -> {
                    Handle.Invisible
                }
                selectionRange.begin < pageRange.begin && selectionRange.end > pageRange.begin -> {
                    createHandle(pageRange.begin, isLeft = true, isNotOnPage = true)
                }
                selectionRange.begin >= pageRange.end && selectionRange.end > pageRange.end -> {
                    createHandle(pageRange.end, isLeft = false, isNotOnPage = true)
                }
                else -> {
                    createHandle(selectionRange.begin, isLeft = true, isNotOnPage = false)
                }
            }

            rightHandle = when {
                selectionRange.end > pageRange.end && selectionRange.begin >= pageRange.end -> {
                    Handle.Invisible
                }
                selectionRange.end > pageRange.end && selectionRange.begin < pageRange.end -> {
                    createHandle(pageRange.end, isLeft = false, isNotOnPage = true)
                }
                selectionRange.end <= pageRange.begin  -> {
                    createHandle(pageRange.begin, isLeft = true, isNotOnPage = true)
                }
                else -> {
                    createHandle( selectionRange.end, isLeft = false, isNotOnPage = false)
                }
            }
        } else {
            leftHandle = Handle.Invisible
            rightHandle = Handle.Invisible
        }
    }

    /**
     * @return newIsLeft
     */
    fun moveHandleTo(touchPosition: PositionF, isLeft: Boolean): Boolean {
        val currentPage = book.pageAt(0)
        val selectionRange = book.selectionRange
        if (currentPage != null && selectionRange != null) {
            val newSelectionRange = newSelectionRange(selectionRange, currentPage, touchPosition, isLeft)
            book.selectionRange = newSelectionRange
            updateHandles()
            return newIsLeft(isLeft, newSelectionRange, selectionRange)
        } else {
            return isLeft
        }
    }

    private fun newIsLeft(isLeft: Boolean, newSelectionRange: LocationRange, oldSelectionRange: LocationRange): Boolean {
        return when {
            newSelectionRange.end == oldSelectionRange.end || newSelectionRange.begin == oldSelectionRange.begin -> isLeft
            newSelectionRange.end == oldSelectionRange.begin -> true
            newSelectionRange.begin == oldSelectionRange.end -> false
            else -> throw IllegalStateException()
        }
    }

    private fun newSelectionRange(oldRange: LocationRange, page: Page, touchPosition: PositionF, isLeftHandle: Boolean): LocationRange {
        val oppositeLocation = if (isLeftHandle) oldRange.end else oldRange.begin
        val selectionChar = selectionCaretNearestTo(page, touchPosition.x, touchPosition.y, oppositeLocation)
        val selectionLocation = selectionChar?.obj?.charLocation(selectionChar.charIndex)
        return if (selectionLocation != null) {
            if (isLeftHandle) {
                if (selectionLocation <= oldRange.end) {
                    LocationRange(selectionLocation, oldRange.end)
                } else {
                    LocationRange(oldRange.end, selectionLocation)
                }
            } else {
                if (selectionLocation >= oldRange.begin) {
                    LocationRange(oldRange.begin, selectionLocation)
                } else {
                    LocationRange(selectionLocation, oldRange.begin)
                }
            }
        } else {
            oldRange
        }
    }

    sealed class Handle {
        object Invisible : Handle()
        class Visible(val position: PositionF) : Handle()
        class NotOnPage(val position: PositionF) : Handle()
    }
}