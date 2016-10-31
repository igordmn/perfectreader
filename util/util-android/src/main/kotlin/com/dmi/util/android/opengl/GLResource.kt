package com.dmi.util.android.opengl

interface GLResource {
    fun bind()
    fun unbind()
}

inline fun GLResource.use(action: () -> Unit) {
    bind()
    action()
    unbind()
}

inline fun use(res1: GLResource, res2: GLResource, action: () -> Unit) {
    res1.bind()
    res2.bind()
    action()
    res2.unbind()
    res1.unbind()
}