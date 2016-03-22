package com.dmi.perfectreader.layout.common

import hashCode
import safeEquals

data class LayoutSpace(val width: Metric, val height: Metric) {
    companion object {
        fun root(width: Float, height: Float) = LayoutSpace(
                Metric(width, Area.Fixed(width)),
                Metric(height, Area.Fixed(height))
        )
    }

    /**
     * @param percentBase величиная, равная 100%. по ней будут считаться все дочерние объекты, определенные в процентах
     * @param area свободное пространство, в которое помещаются дочерние объекты
     */
    data class Metric(val percentBase: Float, val area: Area)

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
