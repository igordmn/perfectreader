package com.dmi.perfectreader.layout.common

import com.dmi.util.lang.clamp
import hashCode
import safeEquals

data class LayoutDimensions(val width: Dimension, val height: Dimension) {
    sealed class Dimension {
        class Auto(
                val min: LayoutLength = LayoutLength.Absolute(0F),
                val max: LayoutLength = LayoutLength.Absolute(Float.MAX_VALUE)
        ) : Dimension() {
            fun compute(autoValue: Float, metric: LayoutSpace.Metric) = clamp(
                    autoValue,
                    min.compute(metric),
                    max.compute(metric)
            )

            override fun equals(other: Any?) = safeEquals(other) { min == it.min && max == it.max }
            override fun hashCode() = hashCode(min.hashCode(), max.hashCode())
        }

        class Fixed(
                val size: LayoutLength,
                val min: LayoutLength = LayoutLength.Absolute(0F),
                val max: LayoutLength = LayoutLength.Absolute(Float.MAX_VALUE)
        ) : Dimension() {
            fun compute(metric: LayoutSpace.Metric) = clamp(
                    size.compute(metric),
                    min.compute(metric),
                    max.compute(metric)
            )

            override fun equals(other: Any?) = safeEquals(other) { size == it.size && min == it.min && max == it.max }
            override fun hashCode() = hashCode(size.hashCode(), min.hashCode(), max.hashCode())
        }
    }

}
