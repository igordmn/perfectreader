package com.dmi.perfectreader.book.pagebook

import com.dmi.util.opengl.GLRenderer

interface PageBookRenderer : GLRenderer {
    val isLoading: Boolean
}
