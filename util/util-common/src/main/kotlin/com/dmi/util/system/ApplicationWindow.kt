package com.dmi.util.system

import com.dmi.util.scope.Scoped

/**
 * Activity in android, Window in Windows
 */
interface ApplicationWindow {
    val isActive: Boolean
    fun close()
}

class ChangingApplicationWindow : Scoped by Scoped.Impl(), ApplicationWindow {
    override var isActive: Boolean by scope.value(false)
    var exit: (() -> Unit)? = null

    override fun close() = exit!!()
}