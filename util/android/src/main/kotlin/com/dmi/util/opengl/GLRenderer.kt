package com.dmi.util.opengl

interface GLRenderer {
    fun onSurfaceCreated()

    fun onSurfaceChanged(width: Int, height: Int)

    /**
     * Free resources, created in onSurfaceCreated. May be invoke multiple times after onSurfaceCreated.
     */
    fun onFreeResources()

    fun onDrawFrame()
}
