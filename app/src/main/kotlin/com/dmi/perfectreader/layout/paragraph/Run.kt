package com.dmi.perfectreader.layout.paragraph

import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.style.FontStyle

sealed class Run {
    class Object(val obj: LayoutObject) : Run()
    class Text(val text: String, val style: FontStyle) : Run()
}
