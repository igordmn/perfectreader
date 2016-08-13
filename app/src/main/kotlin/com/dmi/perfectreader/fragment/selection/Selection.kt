package com.dmi.perfectreader.fragment.selection

import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.perfectreader.fragment.book.selection.*
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.PositionF
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

        subscribe(book.onIsAnimatingChanged) {
            updateHandles()
        }
        subscribe(book.onPagesChanged) {
            updateHandles()
        }
    }

    private fun updateHandles() {
        val currentPage = book.pageAt(0)
        val selectionRange = book.selectionRange
        val isAnimating = book.isAnimating

        if (currentPage != null && selectionRange != null && !isAnimating) {
            val pageRange = currentPage.range

            fun Caret?.position() = if (this != null) positionOf(currentPage, this) else null
            fun notOnPageOrInvisible(position: PositionF?) = if (position != null) Handle.NotOnPage(position) else Handle.Invisible
            fun visibleOrInvisible(position: PositionF?) = if (position != null) Handle.Visible(position) else Handle.Invisible

            leftHandle = when {
                selectionRange.begin < pageRange.begin && selectionRange.end <= pageRange.begin -> {
                    Handle.Invisible
                }
                selectionRange.begin < pageRange.begin && selectionRange.end > pageRange.begin -> {
                    notOnPageOrInvisible(selectionCaretAtBegin(currentPage).position())
                }
                selectionRange.begin >= pageRange.end && selectionRange.end > pageRange.end -> {
                    notOnPageOrInvisible(selectionCaretAtEnd(currentPage).position())
                }
                else -> {
                    visibleOrInvisible(selectionCaretAtLeft(currentPage, selectionRange.begin).position())
                }
            }

            rightHandle = when {
                selectionRange.end > pageRange.end && selectionRange.begin >= pageRange.end -> {
                    Handle.Invisible
                }
                selectionRange.end > pageRange.end && selectionRange.begin < pageRange.end -> {
                    notOnPageOrInvisible(selectionCaretAtEnd(currentPage).position())
                }
                selectionRange.end <= pageRange.begin  -> {
                    notOnPageOrInvisible(selectionCaretAtBegin(currentPage).position())
                }
                else -> {
                    visibleOrInvisible(selectionCaretAtLeft(currentPage, selectionRange.end).position())
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