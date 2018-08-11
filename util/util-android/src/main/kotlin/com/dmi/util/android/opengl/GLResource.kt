package com.dmi.util.android.opengl

import com.dmi.util.scope.Disposable

interface GLResource: Disposable {
    fun bind()
    fun unbind()
}

inline fun GLResource.bind(action: () -> Unit) {
    bind()
    action()
    unbind()
}

inline fun bind(res1: GLResource, res2: GLResource, action: () -> Unit) {
    res1.bind()
    res2.bind()
    action()
    res2.unbind()
    res1.unbind()
}