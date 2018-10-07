package com.dmi.util.screen

import com.dmi.util.scope.Disposable
import com.dmi.util.scope.EmittableEvent
import kotlinx.serialization.Serializable
import java.util.*

class Screens(
        val state: ScreensState,
        private val back: () -> Unit,
        val screen: (childState: Any) -> Screen
) : Disposable {
    init {
        require(state.childStates.size >= 1)
    }

    private val childStates = state.childStates
    var current: Screen? = if (childStates.size > 0) screen(childStates.last()) else null
        private set

    val afterGoForward = EmittableEvent()
    val afterGoBackward = EmittableEvent()

    val size: Int get() = childStates.size

    fun goForward(childState: Any) {
        current?.dispose()
        childStates.add(childState)
        this.current = screen(childState)
        afterGoForward.emit()
    }

    fun goBackward() {
        if (size > 1) {
            current?.dispose()
            childStates.removeAt(childStates.size - 1)
            this.current = childStates.lastOrNull()?.let(screen)
            afterGoBackward.emit()
        } else {
            back()
        }
    }

    override fun dispose() {
        current?.dispose()
    }
}

@Serializable
class ScreensState(val childStates: ArrayList<Any>) {
    companion object {
        fun Home(state: Any) = ScreensState(arrayListOf(state))
    }
}