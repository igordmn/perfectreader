package com.dmi.perfectreader.settingschange

import com.dmi.perfectreader.reader.Reader
import com.dmi.util.scope.Scope
import com.dmi.util.screen.Screen
import com.dmi.util.screen.Screens
import com.dmi.util.screen.ScreensState
import com.dmi.util.screen.StateScreen
import kotlinx.serialization.Serializable

class SettingsChange(
        val back: () -> Unit,
        val reader: Reader,
        val state: SettingsChangeState,
        private val scope: Scope = Scope()
) : Screen by Screen(scope) {
    val screens by scope.observableDisposable(Screens(state.screens, back, this::Screen))

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun Screen(state: Any): Screen = StateScreen(state)
}

@Serializable
class SettingsChangeState(val screens: ScreensState = ScreensState.Home(SettingsChangeMainState()))

@Serializable
class SettingsChangeMainState

@Serializable
class SettingsChangeDetailsState(val id: Int)