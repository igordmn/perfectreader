package com.dmi.perfectreader.layout.common

import com.dmi.util.lang.clamp
import com.dmi.util.lang.safeEquals
import com.dmi.util.lang.hashCode

data class LayoutSize(val width: Dimension, val height: Dimension) {
    sealed class Dimension {
        class Auto(val limits: Limits = Limits.NONE) : Dimension() {
            fun compute(autoValue: Float, percentBase: Float) = limits.clampCompute(autoValue, percentBase)

            override fun equals(other: Any?) = safeEquals(other) { limits == it.limits}
            override fun hashCode() = limits.hashCode()
        }

        class Fixed(val value: LayoutLength, val limits: Limits = Limits.NONE) : Dimension() {
            fun compute(percentBase: Float) = limits.clampCompute(value.compute(percentBase), percentBase)

            override fun equals(other: Any?) = safeEquals(other) { value == it.value && limits == it.limits }
            override fun hashCode() = hashCode(value.hashCode(), limits.hashCode())
        }
    }

    data class Limits(val min: LayoutLength, val max: LayoutLength) {
        companion object {
            val NONE: Limits = Limits(LayoutLength.Absolute(0F), LayoutLength.Absolute(Float.MAX_VALUE))
        }

        fun clampCompute(value: Float, percentBase: Float) = clamp(
                value,
                min.compute(percentBase),
                max.compute(percentBase)
        )
    }
}
