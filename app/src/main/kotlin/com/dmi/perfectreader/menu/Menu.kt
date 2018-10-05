package com.dmi.perfectreader.menu

import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

class Menu(
        val showSettings: () -> Unit,
        val back: () -> Unit,
        val state: MenuState
) : Screen by Screen()

@Serializable
class MenuState