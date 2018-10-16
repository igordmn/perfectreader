package com.dmi.perfectreader.settingsui

import com.dmi.perfectreader.reader.Reader
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observableProperty
import com.dmi.util.screen.Screen
import com.dmi.util.screen.Screens
import com.dmi.util.screen.ScreensState
import com.dmi.util.screen.StateScreen
import kotlinx.serialization.Serializable

class SettingsUI(
        val back: () -> Unit,
        val reader: Reader,
        val state: SettingsUIState,
        private val scope: Scope = Scope()
) : Screen by Screen(scope) {
    val screens by scope.observableDisposable(Screens(state.screens, back, this::Screen))
    var popup: Any? by observableProperty(state::popup)

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun Screen(state: Any): Screen = StateScreen(state)
}

@Serializable
class SettingsUIState(
        val screens: ScreensState = ScreensState.Home(SettingsUIMainState()),
        var popup: Any? = null
)

@Serializable
class SettingsUIMainState

typealias Id = Int