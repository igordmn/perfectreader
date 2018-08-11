package com.dmi.util.android.opengl

import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Event

interface GLObject : Disposable {
    val onChange: Event
    fun draw() = Unit
    override fun dispose()
}