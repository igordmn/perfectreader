package com.dmi.util.opengl

interface GLRenderer {
    fun onSurfaceCreated()

    fun onSurfaceChanged(width: Int, height: Int)

    fun onDrawFrame()
}