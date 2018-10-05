package com.dmi.perfectreader.settingschange

import com.dmi.util.lang.unsupported
import com.dmi.util.screen.Screen
import com.dmi.util.screen.Screens
import com.dmi.util.screen.ScreensState
import kotlinx.serialization.Serializable

class SettingsChange(
        val back: () -> Unit,
        val state: SettingsChangeState
) : Screen by Screen() {
    var screens = Screens(state.screens, this::Screen)

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun Screen(state: Any): Screen = when (state) {
        is SettingsChangeMainState -> SettingsChangeMain(this::backDetails, this::goDetails, state)
        is SettingsChangeDetailsState -> SettingsChangeDetails(this::backDetails, state)
        else -> unsupported()
    }

    private fun goDetails() {
        screens.goForward(SettingsChangeDetailsState())
    }

    private fun backDetails() {
        if (screens.size > 0) {
            screens.goBackward()
        } else {
            back()
        }
    }
}

class SettingsChangeMain(
        val back: () -> Unit,
        val goDetails: () -> Unit,
        val state: SettingsChangeMainState
) : Screen by Screen()

class SettingsChangeDetails(val back: () -> Unit, val state: SettingsChangeDetailsState) : Screen by Screen()


@Serializable
class SettingsChangeState(var screens: ScreensState = ScreensState.Home(SettingsChangeMainState()))

@Serializable
class SettingsChangeMainState

@Serializable
class SettingsChangeDetailsState