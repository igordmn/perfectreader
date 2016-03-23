package com.dmi.perfectreader.layout.common

import com.dmi.perfectreader.layout.common.LayoutSpace.Metric
import com.dmi.util.lang.clamp
import hashCode
import safeEquals

data class LayoutDimensions(val width: Dimension, val height: Dimension) {
    sealed class Dimension {
        class Auto(val limits: Limits) : Dimension() {
            fun compute(autoValue: Float, metric: Metric) = limits.clampCompute(autoValue, metric)

            override fun equals(other: Any?) = safeEquals(other) { limits == it.limits}
            override fun hashCode() = limits.hashCode()
        }

        class Fixed(val size: LayoutLength, val limits: Limits) : Dimension() {
            fun compute(metric: Metric) = limits.clampCompute(size, metric)

            override fun equals(other: Any?) = safeEquals(other) { size == it.size && limits == it.limits }
            override fun hashCode() = hashCode(size.hashCode(), limits.hashCode())
        }
    }

    data class Limits(val min: LayoutLength, val max: LayoutLength) {
        fun clampCompute(value: Float, metric: Metric) = clamp(
                value,
                min.compute(metric),
                max.compute(metric)
        )

        fun clampCompute(value: LayoutLength, metric: Metric) =
                clampCompute(value.compute(metric), metric)
    }
}
