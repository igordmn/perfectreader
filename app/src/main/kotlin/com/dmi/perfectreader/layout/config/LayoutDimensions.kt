package com.dmi.perfectreader.layout.config

import com.dmi.util.lang.clamp
import hashCode
import safeEquals

data class LayoutDimensions(val width: Dimension, val height: Dimension) {
    sealed class Dimension {
        class Auto(
                val min: FixedValue = FixedValue.Absolute(0F),
                val max: FixedValue = FixedValue.Absolute(Float.MAX_VALUE)
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
                val size: FixedValue,
                val min: FixedValue = FixedValue.Absolute(0F),
                val max: FixedValue = FixedValue.Absolute(Float.MAX_VALUE)
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

    sealed class FixedValue {
        abstract fun compute(metric: LayoutSpace.Metric): Float

        class Absolute(val value: Float) : FixedValue() {
            override fun compute(metric: LayoutSpace.Metric) = value

            override fun equals(other: Any?) = safeEquals(other) { value == it.value }
            override fun hashCode() = hashCode(value)
        }

        class Percent(val percent: Float) : FixedValue() {
            override fun compute(metric: LayoutSpace.Metric) = percent * metric.percentBase
            override fun equals(other: Any?) = safeEquals(other) { percent == it.percent }
            override fun hashCode() = hashCode(percent)
        }
    }
}
