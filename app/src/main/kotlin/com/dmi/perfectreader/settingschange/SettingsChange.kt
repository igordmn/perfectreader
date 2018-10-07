package com.dmi.perfectreader.settingschange

import com.dmi.perfectreader.book.Book
import com.dmi.perfectreader.reader.Reader
import com.dmi.util.lang.unsupported
import com.dmi.util.screen.Screen
import com.dmi.util.screen.Screens
import com.dmi.util.screen.ScreensState
import kotlinx.serialization.Serializable

class SettingsChange(
        val back: () -> Unit,
        val reader: Reader,
        val state: SettingsChangeState
) : Screen by Screen() {
    var screens = Screens(state.screens, back, this::Screen)

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun Screen(state: Any): Screen = when (state) {
        is SettingsChangeMainState -> SettingsChangeChild(this, state)
        is SettingsChangeFontFamilyState -> SettingsChangeChild(this, state)
        is SettingsChangeScreenAnimationState -> SettingsChangeScreenAnimation(this, state)
        else -> unsupported(state)
    }

    fun goForward(state: Any) = screens.goForward(state)
    fun goBackward() = screens.goBackward()
}

open class SettingsChangeChild(
        private val parent: SettingsChange,
        val state: Any
) : Screen by Screen() {
    fun goForward(state: Any) = parent.goForward(state)
    fun goBackward() = parent.goBackward()
}

open class SettingsChangeScreenAnimation(
        private val parent: SettingsChange,
        state: Any,
        private val book: Book = parent.reader.book
) : SettingsChangeChild(parent, state) {
    fun showDemo() = book.showDemoAnimation()
}

@Serializable
class SettingsChangeState(var screens: ScreensState = ScreensState.Home(SettingsChangeMainState()))

@Serializable
class SettingsChangeMainState

@Serializable
class SettingsChangeFontFamilyState

@Serializable
class SettingsChangeScreenAnimationState