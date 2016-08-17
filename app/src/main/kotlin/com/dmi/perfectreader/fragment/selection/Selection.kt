package com.dmi.perfectreader.fragment.selection

import com.dmi.perfectreader.data.UserSettingKeys
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.fragment.book.Book
import com.dmi.perfectreader.fragment.book.content.plainText
import com.dmi.perfectreader.fragment.book.selection.*
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.Position
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.Size
import com.dmi.util.rx.rxObservable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import java.lang.Math.max
import java.lang.Math.min

class Selection(
        private val book: Book,
        private val settings: UserSettings,
        private val copyPlainText: (String) -> Unit,
        private val close: () -> Unit,
        dip2px: (Float) -> Float
) : BaseViewModel() {
    private val BOTTOM_ACTIONS_OFFSET = dip2px(24F)

    val leftHandleObservable = BehaviorSubject<Handle>()
    val rightHandleObservable = BehaviorSubject<Handle>()
    val actionsIsVisibleObservable = BehaviorSubject<Boolean>()
    val onSelectionCopiedToClipboard = PublishSubject<Unit>()

    var leftHandle: Handle by rxObservable(Handle.Invisible, leftHandleObservable)
        private set
    var rightHandle: Handle by rxObservable(Handle.Invisible, rightHandleObservable)
        private set

    var actionsIsVisible: Boolean by rxObservable(false, actionsIsVisibleObservable)
        private set

    var isSelecting: Boolean = false
        set(value) {
            field = value
            updateActions()
        }

    init {
        updateControls()

        subscribe(book.onIsAnimatingChanged) {
            updateControls()
        }
        subscribe(book.onPagesChanged) {
            updateControls()
        }
    }

    private fun updateControls() {
        updateHandles()
        updateActions()
    }

    private fun updateHandles() {
        val currentPage = book.pageAt(0)
        val selectionRange = book.selectionRange
        val isAnimating = book.isAnimating

        if (currentPage != null && selectionRange != null && !isAnimating) {
            val pageRange = currentPage.range

            fun notOnPageOrInvisible(caret: LayoutCaret?) =
                    if (caret != null) {
                        Handle.NotOnPage(topOf(currentPage, caret), bottomOf(currentPage, caret))
                    } else {
                        Handle.Invisible
                    }

            fun visibleOrInvisible(caret: LayoutCaret?) =
                    if (caret != null) {
                        Handle.Visible(topOf(currentPage, caret), bottomOf(currentPage, caret))
                    } else {
                        Handle.Invisible
                    }

            leftHandle = when {
                selectionRange.begin < pageRange.begin && selectionRange.end <= pageRange.begin -> {
                    Handle.Invisible
                }
                selectionRange.begin < pageRange.begin && selectionRange.end > pageRange.begin -> {
                    notOnPageOrInvisible(selectionCaretAtBegin(currentPage))
                }
                selectionRange.begin >= pageRange.end && selectionRange.end > pageRange.end -> {
                    notOnPageOrInvisible(selectionCaretAtEnd(currentPage))
                }
                else -> {
                    visibleOrInvisible(selectionCaretAtLeft(currentPage, selectionRange.begin))
                }
            }

            rightHandle = when {
                selectionRange.end > pageRange.end && selectionRange.begin >= pageRange.end -> {
                    Handle.Invisible
                }
                selectionRange.end > pageRange.end && selectionRange.begin < pageRange.end -> {
                    notOnPageOrInvisible(selectionCaretAtEnd(currentPage))
                }
                selectionRange.end <= pageRange.begin -> {
                    notOnPageOrInvisible(selectionCaretAtBegin(currentPage))
                }
                else -> {
                    visibleOrInvisible(selectionCaretAtRight(currentPage, selectionRange.end))
                }
            }
        } else {
            leftHandle = Handle.Invisible
            rightHandle = Handle.Invisible
        }
    }

    private fun updateActions() {
        actionsIsVisible = !isSelecting && (leftHandle is Handle.Positioned || rightHandle is Handle.Positioned)
    }

    fun actionsPosition(actionsContainerSize: Size, actionsSize: Size): Position {
        val leftHandle = leftHandle
        val rightHandle = rightHandle

        return when {
            leftHandle is Handle.Positioned && rightHandle is Handle.Positioned -> {
                actionsPosition(actionsContainerSize, actionsSize, leftHandle, rightHandle)
            }
            leftHandle is Handle.Positioned -> actionsPosition(actionsContainerSize, actionsSize, leftHandle, leftHandle)
            rightHandle is Handle.Positioned -> actionsPosition(actionsContainerSize, actionsSize, rightHandle, rightHandle)
            else -> Position(0, 0)
        }
    }

    private fun actionsPosition(
            actionsContainerSize: Size, actionsSize: Size,
            firstHandle: Handle.Positioned, secondHandle: Handle.Positioned
    ): Position {
        val topY = min(firstHandle.top.y, secondHandle.top.y)
        val bottomY = max(firstHandle.bottom.y, secondHandle.bottom.y)
        val leftX = min(firstHandle.bottom.x, secondHandle.bottom.x)
        val rightX = max(firstHandle.bottom.x, secondHandle.bottom.x)

        val unalignedX = (leftX + rightX - actionsSize.width) / 2
        val x = min(actionsContainerSize.width - actionsSize.width.toFloat(), max(0F, unalignedX))

        val y: Float
        if (topY - actionsSize.height >= 0) {
            y = topY - actionsSize.height
        } else if (bottomY + BOTTOM_ACTIONS_OFFSET + actionsSize.height <= actionsContainerSize.height) {
            y = bottomY + BOTTOM_ACTIONS_OFFSET
        } else {
            y = bottomY - actionsSize.height
        }

        return Position(x.toInt(), y.toInt())
    }

    /**
     * @return newIsLeft
     */
    fun moveHandleTo(touchPosition: PositionF, isLeft: Boolean): Boolean {
        val currentPage = book.pageAt(0)
        val selectionRange = book.selectionRange
        if (currentPage != null && selectionRange != null) {
            val newSelection = newSelection(book.content, currentPage, selectionRange, isLeft, touchPosition, isSelectWords())
            book.selectionRange = newSelection.range
            updateControls()
            return newSelection.isLeftHandle
        } else {
            return isLeft
        }
    }

    fun copySelectedText() {
        val selectionRange = book.selectionRange
        if (selectionRange != null) {
            val plainText = book.content.plainText(selectionRange)
            copyPlainText(plainText)
            onSelectionCopiedToClipboard.onNext(Unit)
            book.selectionRange = null
            close()
        }
    }

    private fun isSelectWords() = settings[UserSettingKeys.UI.selectionSelectWords]

    sealed class Handle {
        object Invisible : Handle()
        abstract class Positioned(val top: PositionF, val bottom: PositionF) : Handle()
        class Visible(top: PositionF, bottom: PositionF) : Positioned(top, bottom)
        class NotOnPage(top: PositionF, bottom: PositionF) : Positioned(top, bottom)
    }
}