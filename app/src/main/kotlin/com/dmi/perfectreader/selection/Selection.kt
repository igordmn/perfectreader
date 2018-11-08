package com.dmi.perfectreader.selection

import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.selection.BookSelections
import com.dmi.perfectreader.book.selection.BookSelections.Handle
import com.dmi.perfectreader.book.selection.BookSelections.Handles
import com.dmi.perfectreader.reader.ReaderContext
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.graphic.Position
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.Size
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.scope.observableProperty
import kotlinx.serialization.Serializable
import java.lang.Math.max
import java.lang.Math.min

class Selection(
        private val context: ReaderContext,
        private val book: Book,
        val deselect: () -> Unit,
        val state: SelectionState,
        dip2px: (Float) -> Float = context.main.dip2px,
        private val settings: Settings = context.main.settings,
        private val textActions: TextActions = context.textActions,
        scope: Scope = Scope()
) : Disposable by scope {
    private val bottomActionsOffset = dip2px(24F)

    var isSelecting: Boolean by observable(false)
    var range: LocationRange by observableProperty(state::range)
        private set

    val handles: Handles by scope.cached {
        val selections = book.selections
        val isMoving = book.isMoving

        if (selections != null && !isMoving) {
            selections.handles(this.range)
        } else {
            Handles.INVISIBLE
        }
    }

    val actionsIsVisible: Boolean get() = !isSelecting && handles.isPositioned

    private val selectedText get() = book.text.plain(range)

    fun actionsPosition(actionsContainerSize: Size, actionsSize: Size): Position {
        val handles = handles
        return when {
            handles.left is BookSelections.Handle.Positioned && handles.right is Handle.Positioned -> {
                actionsPosition(actionsContainerSize, actionsSize, handles.left, handles.right)
            }
            handles.left is Handle.Positioned -> actionsPosition(actionsContainerSize, actionsSize, handles.left, handles.left)
            handles.right is Handle.Positioned -> actionsPosition(actionsContainerSize, actionsSize, handles.right, handles.right)
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
        return Position(
                x = min(
                        actionsContainerSize.width - actionsSize.width.toFloat(),
                        max(0F, unalignedX)
                ).toInt(),
                y = when {
                    topY - actionsSize.height >= 0 -> topY - actionsSize.height
                    bottomY + bottomActionsOffset + actionsSize.height <= actionsContainerSize.height -> bottomY + bottomActionsOffset
                    else -> bottomY - actionsSize.height
                }.toInt()
        )
    }

    /**
     * @return newIsLeft
     */
    fun moveHandleTo(touchPosition: PositionF, isLeft: Boolean): Boolean {
        val selections = book.selections
        return if (selections != null) {
            val newSelection = selections.newSelection(range, isLeft, touchPosition, settings.selection.selectWords)
            range = newSelection.range
            newSelection.isLeftHandle
        } else {
            isLeft
        }
    }

    fun copySelectedText() {
        textActions.copy(selectedText)
        deselect()
    }

    fun translateSelectedText() {
        textActions.translate(selectedText)
        deselect()
    }
}

@Serializable
class SelectionState(
        var range: LocationRange
)