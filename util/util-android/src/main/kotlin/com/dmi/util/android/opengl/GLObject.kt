package com.dmi.util.android.opengl

import com.dmi.util.scope.Disposable

interface GLObject : Disposable {
    fun draw()
    override fun dispose()
}