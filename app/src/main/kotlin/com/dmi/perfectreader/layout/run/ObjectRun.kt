package com.dmi.perfectreader.layout.run

import com.dmi.perfectreader.layout.LayoutObject

class ObjectRun(private val obj: LayoutObject) : Run() {

    fun obj(): LayoutObject {
        return obj
    }
}
