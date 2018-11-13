package com.dmi.perfectreader.ui.book.selection

import com.dmi.perfectreader.book.content.ContentText
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.util.graphic.PositionF

class BookSelections(val page: Page, val text: ContentText) {
    fun center(): LocationRange? = selectionInitialRange(page.size.width / 2, page.size.height / 2)
    fun at(position: PositionF): LocationRange? = selectionInitialRange(position.x, position.y)

    fun newSelection(oldRange: LocationRange, isLeftHandle: Boolean, touchPosition: PositionF, selectWords: Boolean): NewSelectionResult {
        val result = newSelectionSelectChars(oldRange, isLeftHandle, touchPosition)
        return if (selectWords) NewSelectionResult(selectionAlignToWords(result.range), result.isLeftHandle) else result
    }

    private fun newSelectionSelectChars(oldRange: LocationRange, isLeftHandle: Boolean, touchPosition: PositionF): NewSelectionResult {
        val oppositeLocation = if (isLeftHandle) oldRange.endInclusive else oldRange.start
        val selectionChar = page.selectionCaretNearestTo(touchPosition.x, touchPosition.y, oppositeLocation)
        return if (selectionChar != null) {
            val selectionLocation = selectionChar.obj.charLocation(selectionChar.charIndex)
            if (isLeftHandle) {
                if (selectionLocation <= oldRange.endInclusive) {
                    NewSelectionResult(LocationRange(selectionLocation, oldRange.endInclusive), true)
                } else {
                    NewSelectionResult(LocationRange(oldRange.endInclusive, selectionLocation), false)
                }
            } else {
                if (selectionLocation >= oldRange.start) {
                    NewSelectionResult(LocationRange(oldRange.start, selectionLocation), false)
                } else {
                    NewSelectionResult(LocationRange(selectionLocation, oldRange.start), true)
                }
            }
        } else {
            NewSelectionResult(oldRange, isLeftHandle)
        }
    }

    data class NewSelectionResult(val range: LocationRange, val isLeftHandle: Boolean)

    fun selectionInitialRange(x: Float, y: Float): LocationRange? {
        val caret = page.selectionCaretOfCharNearestTo(x, y)
        return if (caret != null) {
            val range = LocationRange(caret.obj.charLocation(caret.charIndex), caret.obj.charLocation(caret.charIndex + 1))
            selectionAlignToWords(range)
        } else {
            null
        }
    }

    /**
     * если в range нету слов, то возвращает range
     * если есть, то смещаем левую границу к началу первого слова, правую границу к концу последнего слова
     * (границы могут сместить как влево, так и вправо, если range содержит слово не целиком)
     */
    fun selectionAlignToWords(range: LocationRange): LocationRange {
        val firstWordEnd = text.wordEndAfter(range.start)
        val lastWordBegin = text.wordBeginBefore(range.endInclusive)
        val firstWordBegin = if (firstWordEnd != null) text.wordBeginBefore(firstWordEnd) else null
        val lastWordEnd = if (lastWordBegin != null) text.wordEndAfter(lastWordBegin) else null
        return if (firstWordBegin != null && lastWordEnd != null && lastWordEnd > firstWordBegin) {
            LocationRange(firstWordBegin, lastWordEnd)
        } else {
            range
        }
    }

    fun handles(selection: LocationRange): Handles {
        val pageRange = page.range

        fun notOnPageOrInvisible(caret: LayoutCaret?): Handle {
            return if (caret != null) {
                Handle.NotOnPage(page.topOf(caret), page.bottomOf(caret))
            } else {
                Handle.Invisible
            }
        }

        fun visibleOrInvisible(caret: LayoutCaret?): Handle {
            return if (caret != null) {
                Handle.Visible(page.topOf(caret), page.bottomOf(caret))
            } else {
                Handle.Invisible
            }
        }

        return Handles(
                left = when {
                    selection.start < pageRange.start && selection.endInclusive <= pageRange.start -> {
                        Handle.Invisible
                    }
                    selection.start < pageRange.start && selection.endInclusive > pageRange.start -> {
                        notOnPageOrInvisible(page.selectionCaretAtBegin())
                    }
                    selection.start >= pageRange.endInclusive && selection.endInclusive > pageRange.endInclusive -> {
                        notOnPageOrInvisible(page.selectionCaretAtEnd())
                    }
                    else -> {
                        visibleOrInvisible(page.selectionCaretAtLeft(selection.start))
                    }
                },

                right = when {
                    selection.endInclusive > pageRange.endInclusive && selection.start >= pageRange.endInclusive -> {
                        Handle.Invisible
                    }
                    selection.endInclusive > pageRange.endInclusive && selection.start < pageRange.endInclusive -> {
                        notOnPageOrInvisible(page.selectionCaretAtEnd())
                    }
                    selection.endInclusive <= pageRange.start -> {
                        notOnPageOrInvisible(page.selectionCaretAtBegin())
                    }
                    else -> {
                        visibleOrInvisible(page.selectionCaretAtRight(selection.endInclusive))
                    }
                }
        )
    }

    sealed class Handle {
        object Invisible : Handle()
        abstract class Positioned : Handle() {
            abstract val top: PositionF
            abstract val bottom: PositionF
        }

        data class Visible(override val top: PositionF, override val bottom: PositionF) : Positioned()
        data class NotOnPage(override val top: PositionF, override val bottom: PositionF) : Positioned()
    }

    data class Handles(val left: Handle, val right: Handle) {
        companion object {
            val INVISIBLE = Handles(Handle.Invisible, Handle.Invisible)
        }

        val isPositioned get() = left is Handle.Positioned && right is Handle.Positioned
    }
}