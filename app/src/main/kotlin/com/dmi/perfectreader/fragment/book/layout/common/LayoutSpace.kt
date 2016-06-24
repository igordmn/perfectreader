package com.dmi.perfectreader.fragment.book.layout.common

import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.hashCode
import com.dmi.util.lang.safeEquals

data class LayoutSpace(val width: Dimension, val height: Dimension) {
    companion object {
        fun root(size: SizeF) = LayoutSpace(
                Dimension(size.width, Area.Fixed(size.width)),
                Dimension(size.height, Area.Fixed(size.height))
        )
    }

    /**
     * @param percentBase величина, равная 100%. по ней будут считаться все дочерние объекты, определенные в процентах
     * @param area свободное пространство, в которое помещаются дочерние объекты
     */
    data class Dimension(val percentBase: Float, val area: Area)

    sealed class Area {
        class WrapContent(val max: Float) : Area() {
            override fun equals(other: Any?) = safeEquals(other) { max == it.max }
            override fun hashCode() = hashCode(max)
        }

        class Fixed(val value: Float) : Area() {
            override fun equals(other: Any?) = safeEquals(other) { value == it.value }
            override fun hashCode() = hashCode(value)
        }
    }
}