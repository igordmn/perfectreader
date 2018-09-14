package com.dmi.perfectreader.menu

import kotlinx.serialization.Serializable

class Menu(
        val showSettings: () -> Unit,
        val back: () -> Unit,
        val state: MenuState
)

@Serializable
class MenuState