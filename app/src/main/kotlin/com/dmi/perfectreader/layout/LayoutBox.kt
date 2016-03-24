package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.common.LayoutSize
import com.dmi.perfectreader.layout.common.LayoutLength
import com.dmi.perfectreader.style.Align

class LayoutBox(
        val size: LayoutSize,
        val contentAlign: Align,
        val children: List<Child>
) : LayoutObject() {
    class Margins(val left: LayoutLength, val right: LayoutLength, val top: LayoutLength, val bottom: LayoutLength)
    class Child(val margins: Margins, val obj: LayoutObject)
}
