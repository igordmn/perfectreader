package com.dmi.util.screen

import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope

interface Screen: Disposable

fun Screen(scope: Scope) = object : Screen {
    override fun dispose() = scope.dispose()
}

fun Screen() = object : Screen {
    override fun dispose() = Unit
}

class StateScreen(val state: Any): Screen by Screen()